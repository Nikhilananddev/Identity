package com.bitespeed.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyRequest {

    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;
}
