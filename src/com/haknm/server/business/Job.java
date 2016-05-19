package com.haknm.server.business;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.haknm.server.config.Configuration;
import com.haknm.tools.SocketIO;

public class Job implements Runnable {
	static Logger logger = LogManager.getLogger(Job.class);
	private Socket socket;
	
	public Job(Socket socket){
		this.socket=socket;
	}
	
	public void run(){
		Properties p=Configuration.getConfig();
		String encoding = p.getProperty("socketStreamEncoding");
		
		DataInputStream input =null;
		DataOutputStream output=null;
		try{
			input=SocketIO.getDataInput(socket);
			int length = input.read();
			byte[] bytes = new byte[length];
			input.read(bytes);
			String user=new String(bytes,encoding).trim();
			int pwd=input.read();
			
			String result=null;
			if(null!=user&&!user.equals("")&&user.equals("name")&&pwd==123){//cy
				result="login succ!";
			}else{
				result="login fail!";
			}
			logger.info("request user:"+user);
			logger.info("request pwd:"+pwd);
			
			output=SocketIO.getDataOutput(socket);
			bytes = result.getBytes(encoding);
			length=(short)bytes.length;
			output.writeShort(length);
			output.write(bytes);
			logger.info("response info:"+result);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("bussiness thread run excption!");
		}finally{
			try{
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
				logger.error("server socket close error!");
			}
		}
	}
}
