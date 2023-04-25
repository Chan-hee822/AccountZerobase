package com.example.accountzerobase.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class AccountUser extends BaseEntity {
	private String name;
}
