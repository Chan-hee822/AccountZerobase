package com.example.accountzerobase.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// 일종의 설정 클래스, 설정 파일
// 클래스 처럼 보이지만 Account 라는 테이블을 하나 만드는 것이다.
public class Account {
    @Id             //--pk설정할 때.
    @GeneratedValue
    private Long id;

    private String accountNumber;

    @Enumerated(EnumType.STRING) // 이넘값의 실제 문자열을 저장할 수 있음 0,1,2,3 이런 숫자 말고
    private AccountStatus accountStatus;

}
