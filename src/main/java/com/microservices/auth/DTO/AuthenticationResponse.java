package com.microservices.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;

    public static ResponseEntity.BodyBuilder status(HttpStatus unauthorized) {
        return status(HttpStatus.UNAUTHORIZED);
    }
}
