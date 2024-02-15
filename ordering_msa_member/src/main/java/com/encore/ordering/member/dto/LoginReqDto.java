package com.encore.ordering.member.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginReqDto {
    @Email(message = "email is not vaild")
    private String email;
    @NotEmpty(message = "password is essential")
    @Size(min = 4, message = "minimumLength is 4")
    private String password;
}
