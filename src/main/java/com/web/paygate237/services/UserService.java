package com.web.paygate237.services;

import com.web.paygate237.models.User;
import com.web.paygate237.repositories.UserRepository;
import com.web.paygate237.requests.UserRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    public List<User> listAll() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new User(user);
    }

    public User signupUser(UserRequest userRequest) throws UnsupportedEncodingException, MessagingException {
        User user = new User();
        user.setPassword(userRequest.getPassword());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String generatedCode = generateRandomCode();

        user.setVerificationCode(generatedCode);
        user.setEnabled(false);

        sendVerificationEmail(user);

        return userRepo.save(user);
    }

    private void sendVerificationEmail(User user) throws UnsupportedEncodingException, MessagingException {
        String to = user.getEmail();
        String from = "contact@paygate237.com";
        String senderName = "PayGate237";
        String subject ="Confirm your account";
        String content = "Hello, " + user.getUsername() + "<br><br>"
                + "Thank you for signing up. Enter this code to confirm your email:"
                + "<br><br>"
                + "<h2 style='font-size: 80px'>" + user.getVerificationCode() +"</h2>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from, senderName);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.setText(content);

        javaMailSender.send(message);

        System.out.println("Email has been sent successfully!");
    }

    public boolean verifyUser(String verificationCode) {
        User user =userRepo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepo.save(user);

            return true;
        }
    }

    private String generateRandomCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
