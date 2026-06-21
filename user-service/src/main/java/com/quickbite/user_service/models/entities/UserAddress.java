package com.quickbite.user_service.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String label; // Nhà riêng, Công ty
    
    @Column(name = "detail_address")
    private String detailAddress;
    
    @Column(name = "is_default")
    private boolean isDefault;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
