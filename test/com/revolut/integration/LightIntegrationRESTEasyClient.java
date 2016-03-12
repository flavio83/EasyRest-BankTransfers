package com.revolut.integration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.revolut.easyrest.AccsEnum;



/*
 * To run this class please activate the assertion check using the -ea option on the java command.
 * 
 * Running this class the first time will provide a correct feedback for the reason that the initial balances 
 * in the bank are as per Server class constructor; running the second time the assert will failed as the 
 * user's balances have changed.
 * 
 */
public class LightIntegrationRESTEasyClient {
	
	private static String host = "localhost";
	private static String port = "8080";
	
	private static String baseUrl = null;

	public static void main(String[] args) throws Exception {
		
		if(args!=null && args.length==2) {
			host = args[0];
			port = args[1];
		}
		
		StringBuilder aux = new StringBuilder();
		aux.append("http://");
		aux.append(host);
		aux.append(":");
		aux.append(port);
		
		baseUrl = aux.toString();
	    
	    ForkJoinPool pool = ForkJoinPool.commonPool(); //by default it uses Runtime.availableProcessors()

	    for (int i=0;i<10;i++) {
			Callable callable = new Callable() {
	            public Object call() throws Exception {
	            	makeTransfer(AccsEnum.FRANK, AccsEnum.JACK, 5000);
	            	makeTransfer(AccsEnum.JACK, AccsEnum.MARK, 2000);
	            	makeTransfer(AccsEnum.ED,AccsEnum.ALICE, 5000);
	            	makeTransfer(AccsEnum.EMMA,AccsEnum.FRANK, 4200);
	            	makeTransfer(AccsEnum.ED,AccsEnum.EMMA, 150);
	            	makeTransfer(AccsEnum.MARK,AccsEnum.JACK, 350);
	                return null;
	            }
	        };
	        pool.submit(callable);
	    }
	    
	    //waiting...
	    pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
		
	    //check the results
	    checkAccountBalance(AccsEnum.FRANK, 2000);
	    checkAccountBalance(AccsEnum.JACK, 38500);
	    checkAccountBalance(AccsEnum.ED, 8500);
	    checkAccountBalance(AccsEnum.ALICE, 59000);
	    checkAccountBalance(AccsEnum.EMMA, 29500);
	    checkAccountBalance(AccsEnum.MARK, 31500);
	    
		System.out.println("service consumed correctly!");
	}
	
	public static void makeTransfer(AccsEnum sender, AccsEnum receiver, int amount) throws Exception {
		ClientRequest request = null;
    	request = new ClientRequest((baseUrl + "/transfer/" + sender + "/to/" + receiver + "/amount/" + amount).toLowerCase());
		manageResponse(request.get(String.class));
	}
	
	public static void checkAccountBalance(AccsEnum user, int expected) throws Exception {
		ClientRequest request = new ClientRequest((baseUrl + "/balance/" + user).toLowerCase());
		String r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase(String.valueOf(expected))==0;
	}
	
	public static String manageResponse(ClientResponse<String> response) throws Exception {
		if (response.getStatus()!=200) {
			response.releaseConnection();
			throw new RuntimeException("Failed with HTTP error code : " + response.getStatus());	
		} else {
			
			if(response.getHeaders().size()!=0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));
		
				String output;
				while ((output = br.readLine()) != null) {
					return output;
				}
			}
			response.releaseConnection();
			return null;
		}
	}

}