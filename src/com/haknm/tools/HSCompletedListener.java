package com.haknm.tools;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;

public class HSCompletedListener implements HandshakeCompletedListener{

	public void handshakeCompleted(HandshakeCompletedEvent arg0) {
		System.out.println("Handshake finsihed sucessfully!");
	}
}
