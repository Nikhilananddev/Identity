package com.bitespeed.identity.service;

import com.bitespeed.identity.dto.IdentifyRequest;
import com.bitespeed.identity.dto.IdentifyResponse;

// service/ContactService.java
public interface ContactService {
    IdentifyResponse identify(IdentifyRequest request);
}
