package com.example.accountzerobase.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND("사용자가 없습니다."),
	ACCOUNT_NOT_FOUND("계좌가 없습니다."),
	AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 금액을 초과."),
	USER_ACCOUNT_NOT_MATCH("사용자와 계좌 소유주가 다릅니다."),
	ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
	BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
	MAX_ACCOUNT_PER_USER_10("사용자 당 최대 계좌 개수는 10개입니다.")
	;

	private final String description;

}
