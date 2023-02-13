package com.example.SecurityPractice.Model;

import com.example.SecurityPractice.Common.AuthRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "t_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private String id;

    private String password;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private AuthRole role;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    @Builder
    public User(String id, String password, String name, AuthRole role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    //    @CreatedDate
//    @Column(updatable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy-MM-dd", timezone = "Asia/Seoul")
//    private LocalDateTime createdDate;
//
//    @LastModifiedDate
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy-MM-dd", timezone = "Asia/Seoul")
//    private LocalDateTime modifiedDate;

    /**
     * JPA에서는 프록시를 생성을 위해서 기본 생성자를 반드시 하나를 생성해야함
     * */
}
