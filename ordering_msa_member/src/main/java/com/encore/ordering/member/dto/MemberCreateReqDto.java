package com.encore.ordering.member.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class MemberCreateReqDto {
    @NotEmpty(message = "name is essential")
    private String name;
    @NotEmpty(message = "email is essential")
    @Email(message = "email is not vaild")
    private String email;
    @NotEmpty(message = "password is essential")
    @Size(min = 4, message = "minimumLength is 4")
    private String password;
    private String city;
    private String street;
    private String zipcode;
}
