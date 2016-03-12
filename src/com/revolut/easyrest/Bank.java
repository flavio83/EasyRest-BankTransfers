package com.revolut.easyrest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

import com.revolut.easyrest.exception.BankException;



//accounts can have negative balance (overdrafts allowed)
public class Bank {
	
	private static final IntBinaryOperator ioSum = (x,y)->x+y;
	private static final IntBinaryOperator ioSub = (x,y)->x-y;
	
	//in memory datastore
	private Map<AccsEnum,AtomicInteger> accounts = null;

	public Bank() {
		accounts = new ConcurrentHashMap<>();
	}
	
	public int getBalance(AccsEnum account) {
		return accounts.get(account).get();
	}
	
	public void addAccount(AccsEnum account) {
		addAccount(account, 0);
	}
	
	public void addAccount(AccsEnum account, Integer initTransfer) {
		accounts.put(account,new AtomicInteger(initTransfer));
	}
	
	public void transfer(AccsEnum sender, AccsEnum receiver, Integer amount) throws BankException {
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
