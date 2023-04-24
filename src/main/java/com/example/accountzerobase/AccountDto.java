package com.example.accountzerobase;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
//@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
//@Data //  주로 중요하지 않는 데이터들이 있는 곳에서 사용.
@Slf4j
//@UtilityClass
public class AccountDto {
    private String accountNumber;
    private String nickname;
    private LocalDateTime registeredAt;

    public void log(){
        log.error("error is occurred");
    }


}
