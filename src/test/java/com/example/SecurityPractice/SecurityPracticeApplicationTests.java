package com.example.SecurityPractice;

import com.example.SecurityPractice.Common.AuthRole;
import com.example.SecurityPractice.Model.User;
import com.example.SecurityPractice.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SecurityPracticeApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("User테이블 삽입 테스트")
	public void userInsert() {
		//given
		userRepository.save(
				User.builder()
						.id("test")
						.name("test")
						.password("1234")
						.role(AuthRole.ROLE_USER)
						.build()
		);

		// when
		List<User> userList = userRepository.findAll();

		// then
		User user = userList.get(0);
		System.out.println(user.getName());
	}

}
