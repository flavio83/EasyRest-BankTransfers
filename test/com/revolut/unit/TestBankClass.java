package com.revolut.unit;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.revolut.easyrest.Bank;
import com.revolut.easyrest.exception.BankException;



@Test
public class TestBankClass {
	
	Bank bank = null;

	@BeforeTest
	public void populateBank() {
		bank = new Bank();
		bank.addAccount("frank",10000);
		bank.addAccount("jack",5000);
		bank.addAccount("emma",70000);
		bank.addAccount("mark",15000);
		bank.addAccount("ed",60000);
		bank.addAccount("alice",9000);
	}
	
	@Test(threadPoolSize = 3, invocationCount = 10,  timeOut = 10000)
	public void interation() throws Exception {
		bank.transfer("frank", "jack", 5000);
		bank.transfer("jack", "mark", 2000);
		bank.transfer("ed", "alice", 5000);
		bank.transfer("emma", "frank", 4200);
		bank.transfer("ed", "emma", 150);
		bank.transfer("mark", "jack", 350);
	}
	
	@Test(expectedExceptions = BankException.class)
	public void checkException() throws Exception {
		bank.transfer("tom", "jack", 5000);
	}
	
	@AfterTest
	public void checkFinalBalances() {
		Assert.assertEquals(bank.getBalance("frank"), 2000);
		Assert.assertEquals(bank.getBalance("jack"), 38500);
		Assert.assertEquals(bank.getBalance("ed"), 8500);
		Assert.assertEquals(bank.getBalance("alice"), 59000);
		Assert.assertEquals(bank.getBalance("emma"), 29500);
		Assert.assertEquals(bank.getBalance("mark"), 31500);
	}

}
