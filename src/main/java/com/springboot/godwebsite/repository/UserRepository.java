package com.springboot.godwebsite.repository;


import com.springboot.godwebsite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);

}
