package com.example.SecurityPractice.Service;

import com.example.SecurityPractice.Model.User;
import com.example.SecurityPractice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public User saveOrUpdateUser(User user) {
        // 비밀번호 암호화
        user.encodePassword(this.passwordEncoder);

        return this.userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByName(name);

        /**
         * Username 값이 DATA DB 에 존재하지 않는 경우
         * UsernameNotFoundException 에러 메소드를 사용합니다.
         * */
        if (user.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getId())
                    .password(user.get().getPassword())
                    //.roles(user.get().getRole())
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException(name + "정보를 찾을 수 없습니다.");
        }
    }
}
