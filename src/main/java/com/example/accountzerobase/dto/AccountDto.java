package com.example.accountzerobase.dto;

import com.example.accountzerobase.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {			// controller와 service간 응답을 주고 받을 때
	private Long userId;
	private String accountNumber;
	private Long balance;

	private LocalDateTime registeredAt;
	private LocalDateTime unRegisteredAt;

	public static AccountDto fromEntity(Account account) {
		return AccountDto.builder()
				.userId(account.getAccountUser().getId())
				.accountNumber(account.getAccountNumber())
				.balance(account.getBalance())
				.registeredAt(account.getRegisteredAt())
				.unRegisteredAt(account.getUnRegisteredAt())
				.build();
	}

}
