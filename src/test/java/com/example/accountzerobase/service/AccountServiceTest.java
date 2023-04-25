package com.example.accountzerobase.service;

import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountUser;
import com.example.accountzerobase.dto.AccountDto;
import com.example.accountzerobase.exception.AccountException;
import com.example.accountzerobase.repository.AccountRepository;
import com.example.accountzerobase.repository.AccountUserRepository;
import com.example.accountzerobase.type.AccountStatus;
import com.example.accountzerobase.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountUserRepository accountUserRepository;

	@InjectMocks
	private AccountService accountService;

	@Test
	void createAccountSuccess() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.of(Account.builder()
						.accountNumber("1000000012").build()));
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
						.accountUser(user)
						.accountNumber("1000000015").build());
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		//when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);

		//then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(12L, accountDto.getUserId());
		assertEquals("1000000013", captor.getValue().getAccountNumber());
	}

	@Test
	void createFirstAccount() {
		//given
		AccountUser user = AccountUser.builder()
				.id(15L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.empty());
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
						.accountUser(user)
						.accountNumber("1000000015").build());
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		//when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);

		//then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(15L, accountDto.getUserId());
		assertEquals("1000000000", captor.getValue().getAccountNumber());
	}

	@Test
	@DisplayName("해당 유저 없음 - 계좌 생성 실패")
	void createAccount_userNotFound() {
		//given

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));

		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("유저 당 최대 계좌 수는 10개")
	void createAccount_maxAccountIs10() {
		//given
		AccountUser user = AccountUser.builder()
				.id(15L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.countByAccountUser(any()))
				.willReturn(10);

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));

		//then
		assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
	}

	@Test
	void deleteAccountSuccess() {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
						.accountUser(user)
						.balance(0L)
						.accountNumber("1000000012").build()));
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		//when
		AccountDto accountDto = accountService.deleteAccount(1L, "1000000019");

		//then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(12L, accountDto.getUserId());
		assertEquals("1000000012", captor.getValue().getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
	}

	@Test
	@DisplayName("해당 유저 없음 - 계좌 해지 실패")
	void deleteAccount_userNotFound() {
		//given

		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(1L, "1000000019"));

		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("해당 계좌 없음 - 계좌 해지 실패")
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
				() -> accountService.deleteAccount(1L, "1000000019"));

		//then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("계좌 소유자 불일치")
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
				() -> accountService.deleteAccount(1L, "1000000019"));

		//then
		assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH, exception.getErrorCode());

	}

	@Test
	@DisplayName("해지할 계좌에는 잔액이 없어야한다.")
	void deleteAccountFailed_balanceNotEmpty() {
		//given
		AccountUser cobi = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(cobi));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
						.accountUser(cobi)
						.balance(1L)
						.accountNumber("1000000012").build()));

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(1L, "1000000019"));

		//then
		assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());

	}

	@Test
	@DisplayName("해지한 계좌는 해지 불가")
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
				() -> accountService.deleteAccount(1L, "1000000019"));

		//then
		assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
	}

	@Test
	void successGetAccountsByUserId() {
	    //given
		AccountUser cobi = AccountUser.builder()
				.id(12L)
				.name("Cobi").build();
		List<Account> accounts = Arrays.asList(
				Account.builder()
						.accountUser(cobi)
						.accountNumber("1111111111")
						.balance(1000L)
						.build(),
				Account.builder()
						.accountUser(cobi)
						.accountNumber("2222222222")
						.balance(2000L)
						.build(),
				Account.builder()
						.accountUser(cobi)
						.accountNumber("3333333333")
						.balance(3000L)
						.build()
		);
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(cobi));
		given(accountRepository.findByAccountUser(any()))
				.willReturn(accounts);

	    //when
		List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

		//then
		assertEquals(3, accountDtos.size());
		assertEquals("1111111111",accountDtos.get(0).getAccountNumber());
		assertEquals(1000,accountDtos.get(0).getBalance());
		assertEquals("2222222222",accountDtos.get(1).getAccountNumber());
		assertEquals(2000,accountDtos.get(1).getBalance());
		assertEquals("3333333333",accountDtos.get(2).getAccountNumber());
		assertEquals(3000,accountDtos.get(2).getBalance());
	}

	@Test
	void failedToGetAccounts() {
	    //given
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.getAccountsByUserId(1L));

		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
}