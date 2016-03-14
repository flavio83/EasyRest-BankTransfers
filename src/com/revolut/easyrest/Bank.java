package com.revolut.easyrest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.revolut.easyrest.exception.BankException;



public class Bank {
	
	private boolean negativeBalanceAllowed = true;
	
	//in memory datastore
	private Map<AccsEnum,AtomicReference<BigInteger>> accounts = null;
	//private Map<AccsEnum,Integer> accounts = null;

	//accounts can have negative balance (overdrafts allowed)
	public Bank() {
		accounts = new HashMap<>();
	}
	
	public Bank(boolean negativeBalanceAllowed) {
		accounts = new HashMap<>();
		this.negativeBalanceAllowed = negativeBalanceAllowed;
	}
	
	public int getBalance(AccsEnum account) {
		return accounts.get(account).get().intValueExact();
	}
	
	public void addAccount(AccsEnum account) {
		addAccount(account, 0);
	}
	
	public void addAccount(AccsEnum account, Integer initTransfer) {
		accounts.put(account, new AtomicReference(new BigInteger(String.valueOf(initTransfer))));
	}
	
	public void transfer(AccsEnum sender, AccsEnum receiver, Integer amount) throws BankException {
		
		checkCornerCases(sender,receiver,amount);
		
		AtomicReference<BigInteger> balanceSender = accounts.get(sender);
		AtomicReference<BigInteger> balanceReceiver = accounts.get(receiver);
		
		//synchronized(this) { using the synchronized keyword we do not need to use atomicreference
			balanceSender.updateAndGet(x -> x.subtract(BigInteger.valueOf(amount)));
			balanceReceiver.updateAndGet(x -> x.add(BigInteger.valueOf(amount)));
		//}
		
		/*
		Integer balanceSender = accounts.get(sender);
		accounts.put(sender, balanceSender.intValue()-amount.intValue());
		
		Integer balanceReceiver = accounts.get(receiver);
		accounts.put(receiver, balanceReceiver.intValue()-amount.intValue());
		*/
	}
	
	private void checkCornerCases(AccsEnum sender, AccsEnum receiver, Integer amount) throws BankException {
		if(!accounts.containsKey(sender) && !accounts.containsKey(receiver)) {
			throw new BankException("sender and receiver accounts not present");
		} else if(!accounts.containsKey(sender)) {
			throw new BankException("sender account not present");
		} else if(!accounts.containsKey(receiver)) {
			throw new BankException("receiver account not present");
		} else if(!negativeBalanceAllowed && Integer.signum(amount)<=0) {
			throw new BankException("you can't transfer a negative/equals to zero amount");
		} else if(sender.equals(receiver)) {
			throw new BankException("you can't transfer money to the same account");
		}
	}

}
