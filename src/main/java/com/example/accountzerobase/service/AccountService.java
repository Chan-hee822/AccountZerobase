package com.example.accountzerobase.service;

import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountStatus;
import com.example.accountzerobase.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service                //리파지토리 사용하기 위한 서비스를 만듬
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository; // final 타입으로 잡아야함

    @Transactional
    public void createAccount() {       // Account라는 테이블에 데이터를 저장할 때 사용하는 것
        Account account = Account.builder()
                .accountNumber("40000")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);
    }

    @Transactional
    public Account getAccount(Long id) {
        if(id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

}
