package com.revolut.integration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;



/*
 * To run this class please activate the assertion check using the -ea option on the java command.
 * 
 * Running this class the first time will provide a correct feedback for the reason that the initial balances 
 * in the bank are as per Server class constructor; running the second time the assert will failed as the 
 * user's balances have changed.
 * 
 */
public class RESTEasyClientGet {

	public static void main(String[] args) throws Exception {
	    
	    ForkJoinPool pool = ForkJoinPool.commonPool(); //by default it uses Runtime.availableProcessors().

	    for (int i=0;i<10;i++) {
			Callable callable = new Callable() {
	            public Object call() throws Exception {
	            	ClientRequest request = null;
	            	request = new ClientRequest("http://localhost:8080/transfer/frank/to/jack/amount/5000");
	        		manageResponse(request.get(String.class));
	        		request = new ClientRequest("http://localhost:8080/transfer/jack/to/mark/amount/2000");
	        		manageResponse(request.get(String.class));
	        		request = new ClientRequest("http://localhost:8080/transfer/ed/to/alice/amount/5000");
	        		manageResponse(request.get(String.class));
	        		request = new ClientRequest("http://localhost:8080/transfer/emma/to/frank/amount/4200");
	        		manageResponse(request.get(String.class));
	        		request = new ClientRequest("http://localhost:8080/transfer/ed/to/emma/amount/150");
	        		manageResponse(request.get(String.class));
	        		request = new ClientRequest("http://localhost:8080/transfer/mark/to/jack/amount/350");
	        		manageResponse(request.get(String.class));
	                return null;
	            }
	        };
	        pool.submit(callable);
	    }
	    
	    //waiting...
	    pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
		
	    //check the result
		String r = null;
		ClientRequest request = null;
		request = new ClientRequest("http://localhost:8080/balance/frank");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("2000")==0;
		request = new ClientRequest("http://localhost:8080/balance/jack");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("38500")==0;
		request = new ClientRequest("http://localhost:8080/balance/ed");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("8500")==0;
		request = new ClientRequest("http://localhost:8080/balance/alice");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("59000")==0;
		request = new ClientRequest("http://localhost:8080/balance/emma");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("29500")==0;
		request = new ClientRequest("http://localhost:8080/balance/mark");
		r = manageResponse(request.get(String.class));
		assert r.compareToIgnoreCase("31500")==0;
		System.out.println("service consumed correctly!");
	}
	
	public static String manageResponse(ClientResponse<String> response) throws Exception {
		if (response.getStatus()!=200) {
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
			
			return null;
		}
	}

}