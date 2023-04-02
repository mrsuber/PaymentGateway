package com.web.paygate237.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verified_users")
public class VerifyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_code", columnDefinition = "text", unique = true)
    private String code;
    @Column
    private LocalDateTime expiredCode;
    @Column
    private LocalDateTime createdDate;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    public void setExpiredCode(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiredCode = now.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredCode);
    }

}
