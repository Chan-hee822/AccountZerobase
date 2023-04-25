package com.example.accountzerobase.domain;

import com.example.accountzerobase.exception.AccountException;
import com.example.accountzerobase.type.AccountStatus;
import com.example.accountzerobase.type.ErrorCode;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
// 일종의 설정 클래스, 설정 파일
// 클래스 처럼 보이지만 Account 라는 테이블을 하나 만드는 것이다.
public class Account extends BaseEntity {
	@ManyToOne
	private AccountUser accountUser;
	private String accountNumber;

	@Enumerated(EnumType.STRING) // 이넘값의 실제 문자열을 저장할 수 있음 0,1,2,3 이런 숫자 말고
	private AccountStatus accountStatus;
	private Long balance;

	private LocalDateTime registeredAt;
	private LocalDateTime unRegisteredAt;

	public void useBalance(Long amount) {
		if (amount > balance) {
			throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
		}
		balance -= amount;
	}

	public void cancelBalance(Long amount) {
		if (amount < 0) {
			throw new AccountException(ErrorCode.INVALID_REQUEST);
		}
		balance += amount;
	}

}
