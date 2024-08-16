package com.example.adoption_Manopata.dto;

import lombok.Data;

@Data
public class DeleteAccountRequest {
    private String nickname;
    private String password;
}
