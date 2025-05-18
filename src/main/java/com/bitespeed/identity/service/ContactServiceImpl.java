package com.bitespeed.identity.service;

import com.bitespeed.identity.dto.IdentifyRequest;
import com.bitespeed.identity.dto.IdentifyResponse;
import com.bitespeed.identity.model.Contact;
import com.bitespeed.identity.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public IdentifyResponse identify(IdentifyRequest request) {
        String email = request.getEmail();
        String phone = request.getPhoneNumber();

        List<Contact> matchedContacts = contactRepository.findByEmailOrPhoneNumber(email, phone);
        Set<Contact> allLinked = new HashSet<>(matchedContacts);

        for (Contact c : matchedContacts) {
            if (c.getLinkPrecedence() == Contact.LinkPrecedence.secondary) {
                contactRepository.findById(c.getLinkedId()).ifPresent(allLinked::add);
            }
        }

        Contact primary = allLinked.stream()
                .filter(c -> c.getLinkPrecedence() == Contact.LinkPrecedence.primary)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElse(null);

        if (primary == null) {
            // No contact found — create new primary
            Contact newContact = contactRepository.save(Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkPrecedence(Contact.LinkPrecedence.primary)
                    .build());

            return IdentifyResponse.builder()
                    .contact(IdentifyResponse.ContactDetails.builder()
                            .primaryContatctId(newContact.getId())
                            .emails(email == null ? List.of() : List.of(email))
                            .phoneNumbers(phone == null ? List.of() : List.of(phone))
                            .secondaryContactIds(List.of())
                            .build())
                    .build();
        }

        boolean exists = allLinked.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), email) && Objects.equals(c.getPhoneNumber(), phone));

        if (!exists) {
            // Create secondary for new info
            Contact secondary = Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkedId(primary.getId())
                    .linkPrecedence(Contact.LinkPrecedence.secondary)
                    .build();
            contactRepository.save(secondary);
            allLinked.add(secondary);
        }

        Set<String> emails = allLinked.stream()
                .map(Contact::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> phones = allLinked.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Long> secondaryIds = allLinked.stream()
                .filter(c -> !Objects.equals(c.getId(), primary.getId()))
                .map(Contact::getId)
                .toList();

        return IdentifyResponse.builder()
                .contact(IdentifyResponse.ContactDetails.builder()
                        .primaryContatctId(primary.getId())
                        .emails(new ArrayList<>(emails))
                        .phoneNumbers(new ArrayList<>(phones))
                        .secondaryContactIds(secondaryIds)
                        .build())
                .build();
    }
}
