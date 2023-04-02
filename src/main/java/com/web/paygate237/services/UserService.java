package com.web.paygate237.services;

import com.web.paygate237.models.Role;
import com.web.paygate237.models.User;
import com.web.paygate237.models.VerifyUser;
import com.web.paygate237.repositories.UserRepository;
import com.web.paygate237.repositories.VerifyRepository;
import com.web.paygate237.requests.UserRequest;
import com.web.paygate237.requests.VerifyRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VerifyRepository verifyRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    //    @Autowired
    public List<User> listAll() {
        return userRepo.findAll();
    }

    @Autowired
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.applicationContext = appContext;
    }

    public SpringResourceTemplateResolver htmlTemplateResolver() {
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setApplicationContext(applicationContext);
        emailTemplateResolver.setPrefix("classpath:/templates/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return emailTemplateResolver;
    }

    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(htmlTemplateResolver());
        return templateEngine;
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
        user.setRole(Role.USER);

        VerifyUser verifyUser = new VerifyUser();
        verifyUser.setUser(user);
        verifyUser.setCreatedDate(LocalDateTime.now());
        verifyUser.setExpiredCode(5);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String generatedCode = generateRandomCode();

        verifyUser.setCode(generatedCode);
        user.setVerificationCode(generatedCode);
        user.setEnabled(false);

        sendVerificationEmail(user);

        verifyRepo.save(verifyUser);

        return userRepo.save(user);
    }

    private void sendVerificationEmail(User user) throws UnsupportedEncodingException, MessagingException {
        String to = user.getEmail();
        String from = "contact@paygate237.com";
        String senderName = "PayGate237";
        String subject = "Confirm your account";
        String content = "Hello, " + user.getUsername() + "<br><br>"
                + "Thank you for signing up. Enter this code to confirm your email:"
                + "<br><br>"
                + "<h2 style='font-size: 80px'>" + user.getVerificationCode() + "</h2>";

        Context context = new Context();
        context.setVariable("user", user);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String html = templateEngine().process("verify-code", context);

        helper.setFrom(from, senderName);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.setText(html, true);

        javaMailSender.send(message);

        System.out.println("Email has been sent successfully!");
    }

    public HttpStatus verifyUser(VerifyRequest verifyRequest) {
        User user = userRepo.findByVerificationCode(verifyRequest.getCode());

        System.out.println("User: " + user);

        VerifyUser verifyUser = verifyRepo.findByCode(verifyRequest.getCode());

        if (user != null && verifyUser != null) {
            if (verifyUser.isExpired()) {
                return HttpStatus.UNAUTHORIZED;
            } else {
                if (!user.isEnabled()) {
                    
                    user.setEnabled(true);
                    userRepo.save(user);

                    return HttpStatus.OK;

                } else {
                    return HttpStatus.CONFLICT;
                }
            }
        } else {
            return HttpStatus.NOT_FOUND;
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
