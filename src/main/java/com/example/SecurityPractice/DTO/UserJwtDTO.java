package com.example.SecurityPractice.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
public class UserJwtDTO {
    private String id;

    private String role;

    public UserJwtDTO(String id, String role) {
        super();
        this.id = id;
        this.role = role;
    }
}
