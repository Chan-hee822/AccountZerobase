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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.accountzerobase.type.AccountStatus.IN_USE;
import static com.example.accountzerobase.type.TransactionResultType.F;
import static com.example.accountzerobase.type.TransactionResultType.S;
import static com.example.accountzerobase.type.TransactionType.CANCEL;
import static com.example.accountzerobase.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountUserRepository accountUserRepository;

	@InjectMocks
	private TransactionService transactionService;

	@Test
	void successUseBalance() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));
		given(transactionRepository.save(any())).
				willReturn(Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionResultType(S)
						.transactionId("transcationId")
						.transactedAt(LocalDateTime.now())
						.amount(1000L)
						.balanceSnapshot(9000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		//when
		TransactionDto transactionDto = transactionService.useBalance(
				1L, "1000000000", 100L);

		//then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(100L, captor.getValue().getAmount());
		assertEquals(9900L, captor.getValue().getBalanceSnapshot());
		assertEquals(S, transactionDto.getTransactionResultType());
		assertEquals(USE, transactionDto.getTransactionType());
		assertEquals(9000L, transactionDto.getBalanceSnapshot());
		assertEquals(1000L, transactionDto.getAmount());
	}

	@Test
	@DisplayName("해당 유저 없음 - 잔액 사용 실패")
	void useBalance_userNotFound() {
		//given

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("해당 계좌 없음 - 잔액 사용 실패")
	void deleteAccount_AccountNotFound() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("계좌 소유자 불일치 - 잔액 사용 실패")
	void deleteAccountFailed_userUnMatch() {
		//given
		AccountUser cobi = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		AccountUser bryant = AccountUser.builder()
				.id(13L)
				.name("Bryant").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(cobi));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
						.accountUser(bryant)
						.balance(0L)
						.accountNumber("1000000012").build()));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH, exception.getErrorCode());

	}

	@Test
	@DisplayName("해지한 계좌는 사용 불가.")
	void deleteAccountFailed_alreadyUnregistered() {
		//given
		AccountUser cobi = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(cobi));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
						.accountUser(cobi)
						.accountStatus(AccountStatus.UNREGISTERED)
						.balance(0L)
						.accountNumber("1000000012").build()));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
	}

	@Test
	@DisplayName("거래 금액이 잔액보다 큽니다.")
	void exceedAmount_UseBalance() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(100L)
				.accountNumber("1000000012").build();
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		//when

		//then
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.useBalance(1L, "1000000000", 1000L));

		assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
		verify(transactionRepository, times(0)).save(any());
	}

	@Test
	@DisplayName("실패 트랜잭션 저장 성공")
	void saveFailedUseTransaction() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));
		given(transactionRepository.save(any())).
				willReturn(Transaction.builder()
						.account(account)
						.transactionType(USE)
						.transactionResultType(S)
						.transactionId("transactionId")
						.transactedAt(LocalDateTime.now())
						.amount(1000L)
						.balanceSnapshot(9000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		//when
		transactionService.saveFailedUseTransaction("1000000000", 100L);

		//then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(100L, captor.getValue().getAmount());
		assertEquals(10000L, captor.getValue().getBalanceSnapshot());
		assertEquals(F, captor.getValue().getTransactionResultType());
	}

	@Test
	void successCancelBalance() {    // cancel amount 100L, balance = 10000L
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		Transaction transaction = Transaction.builder()
				.account(account)
				.transactionType(USE)
				.transactionResultType(S)
				.transactionId("transactionId")
				.transactedAt(LocalDateTime.now())
				.amount(100L)
				.balanceSnapshot(9900L)
				.build();
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));
		given(transactionRepository.save(any())).
				willReturn(Transaction.builder()
						.account(account)
						.transactionType(CANCEL)
						.transactionResultType(S)
						.transactionId("transactionIdForCancel")
						.transactedAt(LocalDateTime.now())
						.amount(100L)
						.balanceSnapshot(10000L)
						.build());
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

		//when
		TransactionDto transactionDto = transactionService.cancelBalance(
				"transactionIdForCancel", "1000000000", 100L);

		//then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(100L, captor.getValue().getAmount());
		assertEquals(10000L + 100L, captor.getValue().getBalanceSnapshot());
		assertEquals(S, transactionDto.getTransactionResultType());
		assertEquals(CANCEL, transactionDto.getTransactionType());
		assertEquals(10000L, transactionDto.getBalanceSnapshot());
		assertEquals(100L, transactionDto.getAmount());
	}

	@Test
	@DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
	void cancelTransaction_AccountNotFound() {
		//given

		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(Transaction.builder().build()));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance("transactionIdForCancel", "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("원 거래 없음 - 잔액 사용 취소 실패")
	void cancelTransaction_TransactionNotFound() {
		//given

		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionIdForCancel", "1000000000", 1000L));

		//then
		assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("거래와 계좌 매칭 실패- 잔액 사용 취소 실패")
	void cancelTransaction_TransactionAccountUnMatch() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		Account accountNotUse = Account.builder()
				.id(2L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000013").build();
		Transaction transaction = Transaction.builder()
				.account(account)
				.transactionType(USE)
				.transactionResultType(S)
				.transactionId("transactionId")
				.transactedAt(LocalDateTime.now())
				.amount(100L)
				.balanceSnapshot(9900L)
				.build();
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(accountNotUse));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionIdForCancel",
						"1000000000", 100L));

		//then
		assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH, exception.getErrorCode());
	}

	@Test
	@DisplayName("거래 금액과 취소 금액 불일치- 잔액 사용 취소 실패")
	void cancelTransaction_CANCELMUSTFULLY() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		Transaction transaction = Transaction.builder()
				.account(account)
				.transactionType(USE)
				.transactionResultType(S)
				.transactionId("transactionId")
				.transactedAt(LocalDateTime.now())
				.amount(100L + 1000L)
				.balanceSnapshot(8900L)
				.build();
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionIdForCancel",
						"1000000000", 100L));

		//then
		assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
	}

	@Test
	@DisplayName("취소는 1년까지만 가능- 잔액 사용 취소 실패")
	void cancelTransaction_TOOOLDORDERTOCANCEL() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		Transaction transaction = Transaction.builder()
				.account(account)
				.transactionType(USE)
				.transactionResultType(S)
				.transactionId("transactionId")
				.transactedAt(LocalDateTime.now().minusYears(1).minusDays(2))
				.amount(100L)
				.balanceSnapshot(9900L)
				.build();
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(account));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.cancelBalance(
						"transactionIdForCancel",
						"1000000000", 100L));

		//then
		assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
	}

	@Test
	void successQueryTransaction() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		Account account = Account.builder()
				.id(1L)
				.accountUser(user)
				.accountStatus(IN_USE)
				.balance(10000L)
				.accountNumber("1000000012").build();
		Transaction transaction = Transaction.builder()
				.account(account)
				.transactionType(USE)
				.transactionResultType(S)
				.transactionId("transactionId")
				.transactedAt(LocalDateTime.now().minusYears(1).minusDays(2))
				.amount(100L)
				.balanceSnapshot(9900L)
				.build();
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));

		//when
		TransactionDto transactionDto = transactionService.queryTransaction("trxID");

		//then
		assertEquals(USE, transactionDto.getTransactionType());
		assertEquals(S, transactionDto.getTransactionResultType());
		assertEquals(100L, transactionDto.getAmount());
		assertEquals("transactionId", transactionDto.getTransactionId());
	}
	@Test
	@DisplayName("원 거래 없음 - 거래 조회 실패")
	void queryTransaction_TransactionNotFound() {
		//given

		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> transactionService.queryTransaction(
						"transactionIdForCancel"));

		//then
		assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
	}

}