package com.revolut.easyrest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.revolut.easyrest.exception.BankException;




public class Server {
	
	private Thread currentThread;
	private int numThreads = 5;
	
	private Bank bank = new Bank();
	
	public Server() {
    	bank.addAccount("frank",10000);
		bank.addAccount("jack",5000);
		bank.addAccount("emma",70000);
		bank.addAccount("mark",15000);
		bank.addAccount("ed",60000);
		bank.addAccount("alice",9000);		
	}

    @SuppressWarnings("unchecked")
	private void start() throws IOException {
			
        final AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(numThreads, Executors.defaultThreadFactory());

        final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group);
			
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8080);
        listener.bind(hostAddress);
        
        System.out.println("Easy rest service started...");
        
        currentThread = Thread.currentThread();
		
        final String att1 = "connection";

        listener.accept(att1, new CompletionHandler() {
        	@Override
            public void completed(Object ch, Object att) {

                AsynchronousSocketChannel asc = (AsynchronousSocketChannel)ch;
                String msg = "";
				try {
					msg = handleConnection(asc);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
					
                if (msg.equals("quit")) {

                    if (! group.isTerminated()) {
                    	
                        try{
                            group.shutdownNow();
                            group.awaitTermination(10, TimeUnit.SECONDS);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
						
                        currentThread.interrupt();
                    }
                } 
				
                listener.accept("next conn", this);
            }
			
            @Override
            public void failed(Throwable e, Object att) {
                e.printStackTrace();
                currentThread.interrupt();
            }
        });
			
        try {
            currentThread.join();
        }
        	catch (InterruptedException e) {
        }
		
        System.out.println("quit");	
    }
    
    private String handleConnection(AsynchronousSocketChannel ch) throws Exception {
			
        ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
        Future result = ch.read(buffer);
        while (! result.isDone()) { }

        buffer.flip();
        String msg = new String(buffer.array()).trim();
        
        System.out.println(msg);
        
        String url = msg.split("\r\n")[0].split(" ")[1];
        
        String[] aux = url.split("/");
        
        if(aux[1].compareToIgnoreCase("transfer")==0) {
	        String sender = aux[2];
	        String receiver = aux[4];
	        Integer amount = Integer.parseInt(aux[6]);
	        
	        try {
				bank.transfer(sender, receiver, amount);
			} catch (BankException e) {
				ch.write(ByteBuffer.wrap("HTTP/1.1 404 Not Found\r\n\r\n".getBytes("ISO-8859-1")));
			}
	
			ch.write(ByteBuffer.wrap("HTTP/1.1 200 OK\r\n\r\n".getBytes("ISO-8859-1")));
	        
	        buffer.clear();
        } else if(aux[1].compareToIgnoreCase("balance")==0) {
        	String account = aux[2];
        	String balance = String.valueOf(bank.getBalance(account));
        	StringBuilder response = new StringBuilder();
        	response.append("HTTP/1.1 200 OK");
        	response.append("\r\n");
        	response.append("Content-Length: ");
        	response.append(balance.length());
        	response.append("\r\n");
        	response.append("\r\n");
        	response.append(balance);
        	ch.write(ByteBuffer.wrap(response.toString().getBytes("ISO-8859-1")));
        }
		
        return msg; 
    }
    
    public static void main (String [] args)throws IOException {
        new Server().start();
    }
    
}