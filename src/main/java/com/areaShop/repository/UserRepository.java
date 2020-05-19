package com.areaShop.repository;

import com.areaShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@Param("email") String email);

    User findByUsername(@Param("username") String username);
}
