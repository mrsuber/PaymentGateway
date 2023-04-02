package com.web.paygate237.repositories;

import com.web.paygate237.models.User;
import com.web.paygate237.models.VerifyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerifyRepository extends JpaRepository<VerifyUser, Long> {
    @Query("SELECT v FROM VerifyUser v WHERE v.code = ?1")
    VerifyUser findByCode(String code);

    @Query("SELECT v from VerifyUser v WHERE v.user = ?1")
    VerifyUser findByUser(User user);
}
