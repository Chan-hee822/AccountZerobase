package com.example.accountzerobase.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Request {        //명시적으로 알아보기 좋게 이너 클래스 생성.

		// 어떻게 valid 해야할지 정해줌.
		@NotNull
		@Min(1)
		private Long userId;

		@NotNull
		@Min(100)
		private Long initialBalance;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private Long userId;
		private String accountNumber;
		private LocalDateTime registeredAt;

		public static Response from(AccountDto accountDto) {
			return Response.builder()
					.userId(accountDto.getUserId())
					.accountNumber(accountDto.getAccountNumber())
					.registeredAt(accountDto.getRegisteredAt())
					.build();
		}
	}
}
