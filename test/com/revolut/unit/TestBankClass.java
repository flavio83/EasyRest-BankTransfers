package com.revolut.unit;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.revolut.easyrest.AccsEnum;
import com.revolut.easyrest.Bank;
import com.revolut.easyrest.exception.BankException;
import com.revolut.integration.*;



@Test
public class TestBankClass {
	
	Bank bank = null;
	TransactionsGenerator tg = new TransactionsGenerator(10000);

	@BeforeTest
	public void populateBank() {
		bank = new Bank();
		for(AccsEnum acc : AccsEnum.values()) {
			if(!acc.equals(AccsEnum.TOM)) {
				bank.addAccount(acc, acc.getInitialBalance());
			}
		}	
	}
	
	@Test(threadPoolSize = 10, invocationCount = 1,  timeOut = 10000)
	public void interation() throws Exception {
		for (Transaction t : tg) {
			bank.transfer(t.getSender(), t.getReceiver(), t.getAmount());
		}
	}
	
	@Test(expectedExceptions = BankException.class)
	public void checkException() throws Exception {
		bank.transfer(AccsEnum.TOM, AccsEnum.ALICE, 5000);
	}
	
	@AfterTest
	public void checkFinalBalances() {
		for(AccsEnum acc : AccsEnum.values()) {
			if(!acc.equals(AccsEnum.TOM)) {
				Assert.assertEquals(bank.getBalance(acc), tg.getExpectedBalance(acc));
			}
		}
	}

}
