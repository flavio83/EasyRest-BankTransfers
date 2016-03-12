package com.revolut.unit;

import java.util.concurrent.Callable;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.revolut.easyrest.AccsEnum;
import com.revolut.easyrest.Bank;
import com.revolut.easyrest.exception.BankException;
import com.revolut.integration.Transaction;



@Test
public class LightTestBankClass {
	
	Bank bank = null;

	@BeforeTest
	public void populateBank() {
		bank = new Bank();
		for(AccsEnum acc : AccsEnum.values()) {
			if(!acc.equals(AccsEnum.TOM)) {
				bank.addAccount(acc, acc.getInitialBalance());
			}
		}	
	}
	
	@Test(threadPoolSize = 3, invocationCount = 10,  timeOut = 10000)
	public void interation() throws Exception {
		bank.transfer(AccsEnum.FRANK, AccsEnum.JACK, 5000);
		bank.transfer(AccsEnum.JACK, AccsEnum.MARK, 2000);
		bank.transfer(AccsEnum.ED, AccsEnum.ALICE, 5000);
		bank.transfer(AccsEnum.EMMA, AccsEnum.FRANK, 4200);
		bank.transfer(AccsEnum.ED, AccsEnum.EMMA, 150);
		bank.transfer(AccsEnum.MARK, AccsEnum.JACK, 350);
	}
	
	@Test(expectedExceptions = BankException.class)
	public void checkException() throws Exception {
		bank.transfer(AccsEnum.TOM, AccsEnum.ALICE, 5000);
	}
	
	@AfterTest
	public void checkFinalBalances() {
		Assert.assertEquals(bank.getBalance(AccsEnum.FRANK), 2000);
		Assert.assertEquals(bank.getBalance(AccsEnum.JACK), 38500);
		Assert.assertEquals(bank.getBalance(AccsEnum.ED), 8500);
		Assert.assertEquals(bank.getBalance(AccsEnum.ALICE), 59000);
		Assert.assertEquals(bank.getBalance(AccsEnum.EMMA), 29500);
		Assert.assertEquals(bank.getBalance(AccsEnum.MARK), 31500);
	}

}
