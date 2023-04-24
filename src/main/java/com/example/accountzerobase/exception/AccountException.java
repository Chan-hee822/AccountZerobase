package com.example.accountzerobase.exception;

import com.example.accountzerobase.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountException extends RuntimeException {
	private ErrorCode errorCode;
	private String errorMessage;

	public AccountException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}
}
