package com.web.paygate237.repositories;

import com.web.paygate237.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
