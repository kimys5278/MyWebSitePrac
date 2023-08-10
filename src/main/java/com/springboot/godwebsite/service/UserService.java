package com.springboot.godwebsite.service;

import com.springboot.godwebsite.entity.Role;
import com.springboot.godwebsite.entity.User;
import com.springboot.godwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User save(User user) {
        //password 암호화
        String encodedpassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedpassword);

        //회원가입을 하면 비밀번호 활성화
        user.setEnabled(true);

        Role role = new Role();
        role.setId(1L);

        //role에 어떤 권한을 줄건지 저장
        user.getRoles().add(role);

        return userRepository.save(user);
    }

}
