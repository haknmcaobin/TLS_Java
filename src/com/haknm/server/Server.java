package com.haknm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.haknm.server.auth.Auth;
import com.haknm.server.business.Job;
import com.haknm.server.config.Configuration;
//originally javax was intended to be for extensions, and sometimes things would be promoted out of javax into java
public class Server {
	static Logger logger = LogManager.getLogger(Server.class);
	private SSLContext sslContext;
	private SSLServerSocketFactory sslServerSocketFactory;
	private SSLServerSocket sslServerSocket;
	private final Executor executor;
	
	public Server() throws Exception{
		Properties p = Configuration.getConfig();
		Integer serverListenPort=Integer.valueOf(p.getProperty("serverListenPort"));
		Integer serverThreadPoolSize = Integer.valueOf(p.getProperty("serverThreadPoolSize"));
		Integer serverRequestQueueSize = Integer.valueOf(p.getProperty("serverRequestQueueSize"));
		Integer authority = Integer.valueOf(p.getProperty("authority"));
		
		executor=Executors.newFixedThreadPool(serverThreadPoolSize);
		
		sslContext=Auth.getSSLContext();
		sslServerSocketFactory=sslContext.getServerSocketFactory();
		//establish tcp conn
	    //ssl hs begin when c/s try to fetch ssl i/o stream
		sslServerSocket=(SSLServerSocket) sslServerSocketFactory.createServerSocket();
		String[] pwdsuits=sslServerSocket.getSupportedCipherSuites();
		sslServerSocket.setEnabledCipherSuites(pwdsuits);
		sslServerSocket.setUseClientMode(false);
		if(authority.intValue()==2){
			//only when server mode -->exe
			//conn dis without certi
			sslServerSocket.setNeedClientAuth(true);
		}else{
			//only when server mode -->exe
			//conn keep without certi
			sslServerSocket.setWantClientAuth(true);;
		}
		sslServerSocket.setReuseAddress(true);
		sslServerSocket.setReceiveBufferSize(128*1024);
		sslServerSocket.setPerformancePreferences(3, 2, 1);
		sslServerSocket.bind(new InetSocketAddress(serverListenPort),serverRequestQueueSize);
		
		logger.info("server start up");
		logger.info("server port is"+serverListenPort);
	}
	
	
	private void service(){
		while(true){
			SSLSocket socket=null;
			try{
				logger.debug("Wait for client request...");
				socket = (SSLSocket)sslServerSocket.accept();
				logger.debug("Got client request!");
				
				Runnable job = new Job(socket);
				executor.execute(job);
			}catch(Exception e){
				logger.error("server run exception!");
				try{
					socket.close();
				}catch(IOException e1){
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		Server server;
		try{
			server= new Server();
			server.service();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("server socket established error!");
		}
	}
}
