package com.example.SecurityPractice.Common;

import lombok.Data;


@Data
public class Jwt {

    private String grantType;
    private String accessToken;
    private String refreshToken;

}
