package com.example.SecurityPractice;

import com.example.SecurityPractice.Common.AuthRole;
import com.example.SecurityPractice.Model.RefreshToken;
import com.example.SecurityPractice.Model.User;
import com.example.SecurityPractice.Repository.RedisRepository;
import com.example.SecurityPractice.Repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

@SpringBootTest
class SecurityPracticeApplicationTests {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	@Autowired
	RedisRepository redisRepository;

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

	@Test
	@DisplayName("Redis 삽입 테스트")
	public void redisInsert() {
		final String key = "a";
		final String value = "1";

		final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(key, value);

		final String temp = valueOperations.get(key);
		System.out.println("redis 테스트 : " + temp);
		Assertions.assertThat(temp).isEqualTo(value);
	}

}
