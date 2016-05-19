package com.haknm.client;

import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.net.SocketAddress;  
import java.util.Properties;  
  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLSocket;  
import javax.net.ssl.SSLSocketFactory;  
  
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
  
import com.haknm.client.auth.Auth;  
import com.haknm.server.config.Configuration;  
import com.haknm.tools.HSCompletedListener;  
import com.haknm.tools.SocketIO;

public class Client {
	static Logger logger = LogManager.getLogger(Client.class);
	private SSLContext sslContext;
	private int port =10000;
	private String host = "127.0.0.1";
	private SSLSocket socket;
	private Properties p;
	
	public Client(){
		try{
			p = Configuration.getConfig();
			
			sslContext = Auth.getSSLContext();
			SSLSocketFactory factory = (SSLSocketFactory)sslContext.getSocketFactory();
			socket = (SSLSocket)factory.createSocket();
			String[] pwdsuits = socket.getSupportedCipherSuites();//available crypto suit
			socket.setEnabledCipherSuites(pwdsuits);
			socket.setUseClientMode(true);//default value
			
			SocketAddress addr = new InetSocketAddress(host,port);
			socket.connect(addr, 0);
			HSCompletedListener listener = new HSCompletedListener();
			socket.addHandshakeCompletedListener(listener);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("socket establish failed!");
		}
	}
	
	public void request(){
		try{
			String encoding =p.getProperty("socketStreamEncoding");
			
			DataOutputStream output = SocketIO.getDataOutput(socket);
			String user="name";
			byte[] bytes=user.getBytes(encoding);
			int length = bytes.length;
			int pwd = 123;
			
			output.write(length);
			output.write(bytes);
			output.write(pwd);
			
			DataInputStream input = SocketIO.getDataInput(socket);
			length=input.readShort();
			bytes=new byte[length];
			input.read(bytes);
			
			logger.info("request result:"+new String(bytes,encoding));
		}catch(Exception e){
			e.printStackTrace();
			logger.error("request error");
		}finally{
			try{
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args){
		Client client = new Client();
		client.request();
	}
}
