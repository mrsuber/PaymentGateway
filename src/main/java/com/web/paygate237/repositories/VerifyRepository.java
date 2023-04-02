package com.web.paygate237.repositories;

import com.web.paygate237.models.VerifyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerifyRepository extends JpaRepository<VerifyUser, Long> {
//    VerifyUser create(VerifyUser verifyUser);

    @Query("SELECT v FROM VerifyUser v WHERE v.code = ?1")
    VerifyUser findByCode(String code);

//    Optional<VerifyUser> findById(Long id);
}
