package com.revolut.easyrest;

import java.security.SecureRandom;
import java.util.Arrays;


public enum AccsEnum {

	FRANK(10000),
	JACK(5000),
	ED(60000),
	ALICE(9000),
	EMMA(70000),
	MARK(15000), 
	TOM(0); //account used for exception test
	
	private static final SecureRandom RANDOM = new SecureRandom();

	private int initialBalance = 0;

	private AccsEnum(int initialBalance) {
		this.initialBalance = initialBalance;
	}

	public int getInitialBalance() {
		return initialBalance;
	}

	public static AccsEnum getRandomAcc(AccsEnum... blacklist) {
		AccsEnum randomAcc = AccsEnum.values()[RANDOM.nextInt(AccsEnum.values().length-1)];
	    while (Arrays.asList(blacklist).contains(randomAcc)) {
	    	randomAcc = AccsEnum.values()[RANDOM.nextInt(AccsEnum.values().length)];
	    }
	    return randomAcc;
	}

}