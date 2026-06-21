package com.quickbite.user_service.models.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWalletDto {
    private Long id;
    private BigDecimal balance;
}
