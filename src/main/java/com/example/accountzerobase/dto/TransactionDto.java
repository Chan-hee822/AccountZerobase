package com.example.accountzerobase.dto;

import com.example.accountzerobase.type.TransactionResultType;
import com.example.accountzerobase.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
	private String accountNumber;
	private TransactionType transactionType;
	private TransactionResultType transactionResultType;
	private Long amount;
	private Long balanceSnapshot;
	private String transactionId;
	private LocalDateTime transactedAt;
}
