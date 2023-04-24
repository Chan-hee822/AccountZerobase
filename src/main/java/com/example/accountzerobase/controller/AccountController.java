package com.example.accountzerobase.controller;

import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.dto.AccountDto;
import com.example.accountzerobase.dto.CreateAccount;
import com.example.accountzerobase.service.AccountService;
import com.example.accountzerobase.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


// 외부에서는 컨트롤러로만 접속을 하고 컨트롤러는 서비스로 접속, 서비스는 레파지토리로 접속을하는 계층 구조
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {

        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    @GetMapping("/get-lock")
    public String getLock() {
        return redisTestService.getLock();
    }


    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable  Long id) {
        return accountService.getAccount(id);
    }
}
