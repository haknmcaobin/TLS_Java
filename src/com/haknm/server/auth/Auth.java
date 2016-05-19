package com.haknm.server.auth;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.haknm.server.config.Configuration;
//Factory packages define factory class,实例化实现类对象并返回该对象调用方法实现业务逻辑操作。
//Factory Class make sure system maintain即使修改某个类中的方法也不会影响到其他的类。
public class Auth {
	private static SSLContext sslContext;
	
	public static SSLContext getSSLContext() throws Exception{
		Properties p = Configuration.getConfig();
		String protocol = p.getProperty("protocol");
		String serverCer = p.getProperty("serverCer");
		String serverCerPwd = p.getProperty("serverCerPwd");
		String serverKeyPwd = p.getProperty("serverKeyPwd");
		
		//key store
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(serverCer), serverCerPwd.toCharArray());
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, serverKeyPwd.toCharArray());
		KeyManager[] kms = keyManagerFactory.getKeyManagers();
		
		TrustManager[] tms  =null;
		if(Configuration.getConfig().getProperty("authority").equals("2")){
			String serverTrustCer = p.getProperty("serverTrustCer");
			String serverTrustCerPwd = p.getProperty("serverTrustCerPwd");
			//trust key store
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(serverTrustCer), serverTrustCerPwd.toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(keyStore);
			tms = trustManagerFactory.getTrustManagers();
		}
		sslContext = SSLContext.getInstance(protocol);
		sslContext.init(kms, tms, null);
		
		return sslContext;
	}
}
