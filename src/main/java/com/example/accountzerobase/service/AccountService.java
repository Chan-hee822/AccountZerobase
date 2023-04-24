package com.example.accountzerobase.service;

import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountUser;
import com.example.accountzerobase.dto.AccountDto;
import com.example.accountzerobase.exception.AccountException;
import com.example.accountzerobase.repository.AccountRepository;
import com.example.accountzerobase.repository.AccountUserRepository;
import com.example.accountzerobase.type.AccountStatus;
import com.example.accountzerobase.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service                //리파지토리 사용하기 위한 서비스를 만듬
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository; // final 타입으로 잡아야함
    private final AccountUserRepository accountUserRepository;

    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성
     * 계좌를 저장하고, 그 정보를 넘김.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {       // Account라는 테이블에 데이터를 저장할 때 사용하는 것

        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        Account account = accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );
        return AccountDto.fromEntity(account);
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if(id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

}
