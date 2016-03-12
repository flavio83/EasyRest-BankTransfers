package com.revolut.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.revolut.easyrest.AccsEnum;




public class TransactionsGenerator extends ArrayList<Transaction> {
	
	private Map<AccsEnum,Integer> resultExpected = new HashMap<>();
	
	public TransactionsGenerator(long numTrans) {
		for(AccsEnum acc : AccsEnum.values()) {
			if(!acc.equals(AccsEnum.TOM)) {
				resultExpected.put(acc, acc.getInitialBalance());
			}
		}
		for(long i=0;i<numTrans;i++) {
			Transaction t = new Transaction();
			resultExpected.put(t.getSender(), resultExpected.get(t.getSender())-t.getAmount());
			resultExpected.put(t.getReceiver(), resultExpected.get(t.getReceiver())+t.getAmount());
			add(t);
		}
	}
	
	public int getExpectedBalance(AccsEnum acc) {
		return resultExpected.get(acc);
	}

}