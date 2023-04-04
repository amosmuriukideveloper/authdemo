package com.microservices.auth.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.transform.Source;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest implements Source {

    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String password;
    @NotBlank(message = "Repeat password is required")
    private String repeatPassword;

    @Override
    public void setSystemId(String systemId) {

    }

    @Override
    public String getSystemId() {
        return null;
    }
}
