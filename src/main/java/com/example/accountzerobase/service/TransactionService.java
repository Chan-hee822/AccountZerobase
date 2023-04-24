package com.example.accountzerobase.service;


import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountUser;
import com.example.accountzerobase.dto.TransactionDto;
import com.example.accountzerobase.exception.AccountException;
import com.example.accountzerobase.repository.AccountRepository;
import com.example.accountzerobase.repository.AccountUserRepository;
import com.example.accountzerobase.repository.TransactionRepository;
import com.example.accountzerobase.type.AccountStatus;
import com.example.accountzerobase.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Objects;

import static com.example.accountzerobase.type.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountUserRepository accountUserRepository;
	private final AccountRepository accountRepository;

	/**
	 * 사용자 또는 계좌가 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
	 * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
	 * 거래금액이 너무 작거나 큰 경우 실패 응답
	 */
	@Transactional
	public TransactionDto useBalance(Long userId, String accountNumber, Long amount){
		AccountUser user = accountUserRepository.findById(userId)
				.orElseThrow(() -> new AccountException(USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
						.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		validateUseBalance(user, account, amount);

	}

	private void validateUseBalance(AccountUser user, Account account, Long amount) {
		if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
			throw new AccountException(USER_ACCOUNT_NOT_MATCH);
		}
		if(account.getAccountStatus() != AccountStatus.IN_USE){
			throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
		}
		if(account.getBalance() < amount){
			throw new AccountException(AMOUNT_EXCEED_BALANCE);
		}
	}
}
