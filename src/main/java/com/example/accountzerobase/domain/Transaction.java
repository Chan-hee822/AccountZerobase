package com.example.accountzerobase.domain;

import com.example.accountzerobase.type.TransactionResultType;
import com.example.accountzerobase.type.TransactionType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Transaction extends BaseEntity{
	//-- 실제 비지니스에 사용될 부분들
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	@Enumerated(EnumType.STRING)
	private TransactionResultType transactionResultType;

	@ManyToOne
	private Account account;
	private Long amount;
	private Long balanceSnapshot;

	private String transactionId;
	private LocalDateTime transactedAt;
	// ---------------------------


}
