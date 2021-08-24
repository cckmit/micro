package micro.commons.ftp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpDemo {

	public static void main(String[] args) throws IOException {
		// 上传、读取文件
		try (FtpClient ftp = FtpClient.newBuilder().ip("127.0.0.1").port(21).setAcc("gewx").setPwd("198710").build()) {
			ftp.storeFile("Hello.java", new FileInputStream(new File("c:\\Account.java")));
			
			byte[] fileByte = ftp.readFileToByte("Hello.java");
			ftp.storeFile("Hello.java", new ByteArrayInputStream(fileByte), "bak");
			
			ftp.changeToParentDirectory();
			ftp.deleteFile("Hello.java"); 
		}		
	}
}
