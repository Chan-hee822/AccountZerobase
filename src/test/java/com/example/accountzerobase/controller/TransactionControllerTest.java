package com.example.accountzerobase.controller;

import com.example.accountzerobase.dto.AccountDto;
import com.example.accountzerobase.dto.CancelBalance;
import com.example.accountzerobase.dto.TransactionDto;
import com.example.accountzerobase.dto.UseBalance;
import com.example.accountzerobase.service.TransactionService;
import com.example.accountzerobase.type.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.example.accountzerobase.type.TransactionResultType.S;
import static com.example.accountzerobase.type.TransactionType.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
	@MockBean
	private TransactionService transactionService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void successUseBalance() throws Exception {
		//given
		given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
				.willReturn(TransactionDto.builder()
						.accountNumber("1000000000")
						.transactedAt(LocalDateTime.now())
						.amount(12345L)
						.transactionId("transactionId")
						.transactionResultType(S)
						.build());
		//when
		//then
		mockMvc.perform(post("/transaction/use")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new UseBalance.Request(1L, "2000000000", 3000L)
						))
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value("1000000000"))
				.andExpect(jsonPath("$.transactionResult").value("S"))
				.andExpect(jsonPath("$.transactionId").value("transactionId"))
				.andExpect(jsonPath("$.amount").value(12345));
	}

	@Test
	void successCancelBalance() throws Exception {
		//given
		given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
				.willReturn(TransactionDto.builder()
						.accountNumber("1000000000")
						.transactedAt(LocalDateTime.now())
						.amount(54321L)
						.transactionId("transactionIdForCancel")
						.transactionResultType(S)
						.build());
		//when
		//then
		mockMvc.perform(post("/transaction/cancel")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new CancelBalance.Request("transactionId",
										"2000000000", 3000L)
						))
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value("1000000000"))
				.andExpect(jsonPath("$.transactionResult").value("S"))
				.andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
				.andExpect(jsonPath("$.amount").value(54321));
	}

	@Test
	void successQueryTransaction () throws Exception {
		//given
		given(transactionService.queryTransaction(anyString()))
				.willReturn(TransactionDto.builder()
						.accountNumber("1000000000")
						.transactionType(USE)
						.transactedAt(LocalDateTime.now())
						.amount(54321L)
						.transactionId("transactionIdForCancel")
						.transactionResultType(S)
						.build());
		//when
		//then
		mockMvc.perform(get("/transaction/111225454"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value("1000000000"))
				.andExpect(jsonPath("$.transactionType").value("USE"))
				.andExpect(jsonPath("$.transactionResult").value("S"))
				.andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
				.andExpect(jsonPath("$.amount").value(54321));
	}
}