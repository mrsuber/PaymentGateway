package com.web.paygate237;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.paygate237.models.User;
import com.web.paygate237.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRespositoryTests {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepo;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("daniegraham");
        user.setEmail("daniegraham97@gmail.com");
        user.setPassword("daniegraham");
        user.setPhoneNumber(651523013);
        user.setVerificationCode("ab23dc");
        user.setEnabled(true);

        User savedUser = userRepo.save(user);

        User existingUser = entityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existingUser.getEmail());
    }
}
