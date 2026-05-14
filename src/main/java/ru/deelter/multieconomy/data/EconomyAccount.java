package ru.deelter.multieconomy.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EconomyAccount {
	private UUID holderId;
	private String currencyId;
	private double balance;
}