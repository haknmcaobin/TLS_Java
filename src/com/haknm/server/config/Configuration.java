package com.haknm.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger; //log4j up to date -->v2.5
import org.apache.logging.log4j.LogManager;

public class Configuration {
	private static Properties config;
	
	private static Logger logger = LogManager.getLogger(Configuration.class);
	
	public static Properties getConfig(){
		try{
			if(null==config){
				File configFile = new File("conf/conf.properties");
				if(configFile.exists()&&configFile.isFile()&&configFile.canRead()){
					InputStream input = new FileInputStream(configFile);
					config = new Properties();
					config.load(input);
				}
			}
		}catch(Exception e){
			//default set
			config = new Properties();
			config.setProperty("protocol", "TLSV1");  
            config.setProperty("serverCer", "certificate/server.jks");  
            config.setProperty("serverCerPwd", "11019926haknm");  //hide when published
            config.setProperty("serverKeyPwd", "11019926haknm");  
            config.setProperty("serverTrustCer", "certificate/serverTrust.jks");  
            config.setProperty("serverTrustCerPwd", "11019926haknm");  
            config.setProperty("clientCer", "certificate/client.jks");  
            config.setProperty("clientCerPwd", "11019926haknm");  
            config.setProperty("clientKeyPwd", "11019926haknm");  
            config.setProperty("clientTrustCer", "certificate/clientTrust.jks");  
            config.setProperty("clientTrustCerPwd", "11019926haknm");  
            config.setProperty("serverListenPort", "10000");  
            config.setProperty("serverThreadPoolSize", "5");  
            config.setProperty("serverRequestQueueSize", "10");  
            config.setProperty("socketStreamEncoding", "UTF-8");
		}
		return config;
	}
}
