package com.example.accountzerobase.service;


import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountUser;
import com.example.accountzerobase.domain.Transaction;
import com.example.accountzerobase.dto.TransactionDto;
import com.example.accountzerobase.exception.AccountException;
import com.example.accountzerobase.repository.AccountRepository;
import com.example.accountzerobase.repository.AccountUserRepository;
import com.example.accountzerobase.repository.TransactionRepository;
import com.example.accountzerobase.type.AccountStatus;
import com.example.accountzerobase.type.ErrorCode;
import com.example.accountzerobase.type.TransactionResultType;
import com.example.accountzerobase.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.accountzerobase.type.ErrorCode.*;
import static com.example.accountzerobase.type.TransactionResultType.F;
import static com.example.accountzerobase.type.TransactionResultType.S;
import static com.example.accountzerobase.type.TransactionType.CANCEL;
import static com.example.accountzerobase.type.TransactionType.USE;

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
	public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
		AccountUser user = accountUserRepository.findById(userId)
				.orElseThrow(() -> new AccountException(USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		validateUseBalance(user, account, amount);

		account.useBalance(amount);

		return TransactionDto.fromEntity(saveAndGetTransaction(USE, S, account, amount));
	}

	private void validateUseBalance(AccountUser user, Account account, Long amount) {
		if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
			throw new AccountException(USER_ACCOUNT_NOT_MATCH);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
		}
		if (account.getBalance() < amount) {
			throw new AccountException(AMOUNT_EXCEED_BALANCE);
		}
	}

	@Transactional
	public void saveFailedUseTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(USE, F, account, amount);
	}

	private Transaction saveAndGetTransaction(
			TransactionType transactionType,
			TransactionResultType transactionResultType,
			Account account, Long amount) {
		return transactionRepository.save(
				Transaction.builder()
						.transactionType(transactionType)
						.transactionResultType(transactionResultType)
						.account(account)
						.amount(amount)
						.balanceSnapshot(account.getBalance())
						.transactionId(
								UUID.randomUUID().toString().replace("-", ""))
						.transactedAt(LocalDateTime.now())
						.build()
		);
	}

	@Transactional
	public TransactionDto cancelBalance(
			String transactionId,
			String accountNumber,
			Long amount)
	{
		Transaction transaction = transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
		validateCancelBalance(transaction, account, amount);

		account.cancelBalance(amount);

		return TransactionDto.fromEntity(
				saveAndGetTransaction(CANCEL, S, account, amount));

	}

	private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
		if(!Objects.equals(transaction.getAccount().getId(), account.getId())){
			throw new AccountException(TRANSACTION_ACCOUNT_UN_MATCH);
		}
		if(!Objects.equals(transaction.getAmount(), amount)){
			throw new AccountException(CANCEL_MUST_FULLY);
		}
		if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
			throw new AccountException(TOO_OLD_ORDER_TO_CANCEL);
		}
	}

	@Transactional
	public void saveFailedCancelTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

		saveAndGetTransaction(CANCEL, F, account, amount);
	}
}
