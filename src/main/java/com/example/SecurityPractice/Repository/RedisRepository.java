package com.example.SecurityPractice.Repository;

import com.example.SecurityPractice.Model.RefreshToken;
import org.springframework.data.repository.CrudRepository;


public interface RedisRepository extends CrudRepository<RefreshToken, String> {
}
