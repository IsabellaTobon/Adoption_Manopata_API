package com.example.adoption_Manopata.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String nickname;
    private String oldPassword;
    private String newPassword;
}
