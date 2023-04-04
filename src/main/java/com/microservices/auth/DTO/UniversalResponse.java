package com.microservices.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UniversalResponse implements Serializable {
    private int status;
    private String message;
    //TODO: implement how to display data to the client
    private String data;
}