package com.example.SecurityPractice.DTO;

import com.example.SecurityPractice.Common.AuthRole;
import com.example.SecurityPractice.Model.User;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class UserDTO {
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "영어 혹은 숫자만 입력 가능합니다.")
    @Length(min = 1, max = 10)
    @NotBlank(message = "아이디는 필수로 작성해야 합니다.")
    private String id;

    private String name;
    private String password;
    private final AuthRole role = AuthRole.ROLE_USER;

    public User toEntity() {
        // DTO -> entity 변경
        return User.builder()
                .id(this.id)
                .name(this.name)
                .password(this.password)
                .role(this.role)
                .build();
    }
}
