package com.hs.dsch.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AddressConvertor {	
	public List<String> getLocalIPList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface = null;
			Enumeration<InetAddress> inetAddresses = null;
			InetAddress inetAddress = null;
			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && inetAddress instanceof Inet4Address) {
						if (!inetAddress.isLoopbackAddress() 
			                     && inetAddress.getHostAddress().indexOf(":") == -1) {
							ipList.add(inetAddress.getHostAddress());
						}
					}
				}
			}
		} catch (SocketException e) {
			return Collections.emptyList();
		}
		
		return ipList;
	}
}
