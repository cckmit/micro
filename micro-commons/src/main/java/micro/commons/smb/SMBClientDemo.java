package micro.commons.smb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SMBClientDemo {

	public static void main(String[] args) throws IOException {
		SMBClient client = SMBClient.newBuilder().path("smb://192.168.63.194/MSMQ").setAcc("administrator").setPwd("zjg3.1415").build();
		client.storeFile("/ppmcloud/HelloJava.java", new FileInputStream(new File("c://1.java")));
		String val = client.readFile("/ppmcloud/HelloJava.java");
		System.out.println(val);
	}
}
