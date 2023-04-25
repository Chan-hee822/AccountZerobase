package com.example.accountzerobase.dto;

import com.example.accountzerobase.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
	private ErrorCode errorCode;
	private String errorMessage;

}
