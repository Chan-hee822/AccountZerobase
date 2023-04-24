package com.example.accountzerobase.repository;

import com.example.accountzerobase.domain.Account;
import com.example.accountzerobase.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// spring에서 jpa를 좀 더 쓰기 쉽게 하는 인터페이스
// Account라는 테이블에 접속하기 위한 인터페이스를 만든 것
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> { // pk의 타입 Long
	Optional<Account> findFirstByOrderByIdDesc();

	Integer countByAccountUser(AccountUser accountUser);

	Optional<Account> findByAccountNumber(String AccountNumber);
}
