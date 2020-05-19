package com.areaShop.service;

import com.areaShop.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {

    User findByUsername(String username);

    User findByEmail(String email);

    User saveUser(User user);

    User save(User userForSave);

    PasswordEncoder getPasswordEncoder();

    void delete(User user);
}
