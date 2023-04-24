package com.example.accountzerobase.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {		// Account의 필요한 몇 가지 정보만 가져올 때, client와 application간 응답 주고 받을 때
	private String accountNumber;
	private Long balance;


}
