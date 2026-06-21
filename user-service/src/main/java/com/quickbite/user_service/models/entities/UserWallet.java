package com.quickbite.user_service.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal balance; // Số dư tài khoản mô phỏng
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
