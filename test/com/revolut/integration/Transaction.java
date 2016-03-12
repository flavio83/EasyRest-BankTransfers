package com.revolut.integration;

import java.security.SecureRandom;

import com.revolut.easyrest.AccsEnum;




public class Transaction {
	
	private AccsEnum sender;
	private AccsEnum receiver;
	private int amount;
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	public Transaction() {
		sender = AccsEnum.getRandomAcc(AccsEnum.TOM);
		receiver = AccsEnum.getRandomAcc(AccsEnum.TOM, sender);
		amount = RANDOM.nextInt(1000);
	}

	public AccsEnum getSender() {
		return sender;
	}

	public AccsEnum getReceiver() {
		return receiver;
	}

	public int getAmount() {
		return amount;
	}
	
}
