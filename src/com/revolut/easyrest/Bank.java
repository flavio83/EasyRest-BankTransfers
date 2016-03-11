package com.revolut.easyrest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

import com.revolut.easyrest.exception.BankException;




public class Bank {
	
	private static final IntBinaryOperator ioSum = (x,y)->x+y;
	private static final IntBinaryOperator ioSub = (x,y)->x-y;
	
	//accounts can have negative balance (overdrafts allowed)
	private Map<String,AtomicInteger> accounts = null;

	public Bank() {
		accounts = new ConcurrentHashMap<>();
	}
	
	public int getBalance(String account) {
		return accounts.get(account).get();
	}
	
	public void addAccount(String account) {
		addAccount(account,0);
	}
	
	public void addAccount(String account, Integer initTransfer) {
		accounts.put(account,new AtomicInteger(initTransfer));
	}
	
	public void transfer(String sender, String receiver, Integer amount) throws BankException {
		AtomicInteger balanceSender = accounts.get(sender);
		if(balanceSender==null) {
			throw new BankException("sender account not present");
		}
		balanceSender.accumulateAndGet(amount, ioSub);
		AtomicInteger balanceReceiver = accounts.get(receiver);
		if(balanceReceiver==null) {
			throw new BankException("receiver account not present");
		}
		balanceReceiver.accumulateAndGet(amount, ioSum);
	}

}
