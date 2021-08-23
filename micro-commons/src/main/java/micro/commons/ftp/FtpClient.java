package micro.commons.ftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import micro.commons.annotation.ThreadSafe;

/**
 * Ftp客户端工具类
 * 
 * @author gewx
 **/

@ThreadSafe
public final class FtpClient {

	private final FTPClient ftp;

	private FtpClient(FTPClient ftp) {
		this.ftp = ftp;
	}

	/**
	 * 上传ftp文件
	 * 
	 * @author gewx
	 * @param fileName  上传文件名
	 * @param stream    输入字节流
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return void
	 **/
	public void storeFile(String fileName, InputStream stream, String directory) throws IOException {
		try {
			boolean isDirectory = ftp.changeWorkingDirectory(directory);
			if (!isDirectory) {
				throw new RuntimeException("文件上传失败,不存在的目录!");
			}
			boolean isUpload = ftp.storeFile(fileName, stream);
			if (!isUpload) {
				throw new RuntimeException("文件上传失败!");
			}
		} finally {
			if (ftp.isConnected()) {
				ftp.logout();
				ftp.disconnect();
			}
		}
	}

	/**
	 * 读取ftp文件
	 * 
	 * @author gewx
	 * @param fileName  读取文件名
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return void
	 **/
	public String readFile(String fileName, String directory) throws IOException {
		try {
			boolean isDirectory = ftp.changeWorkingDirectory(directory);
			if (!isDirectory) {
				throw new RuntimeException("文件上传失败,不存在的目录!");
			}
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			boolean isDownload = ftp.retrieveFile(fileName, byteOut);
			if (!isDownload) {
				throw new RuntimeException("文件读取失败!");
			}
			return byteOut.toString("utf-8");
		} finally {
			if (ftp.isConnected()) {
				ftp.logout();
				ftp.disconnect();
			}
		}
	}

	/**
	 * 获取构建器
	 * 
	 * @author gewx
	 * @return 构建器实例
	 **/
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * 构建器
	 **/
	public static class Builder {

		/**
		 * ftp服务器地址
		 **/
		private String ip;

		/**
		 * ftp服务器端口
		 **/
		private int port;

		/**
		 * ftp服务器账号
		 **/
		private String userName;

		/**
		 * ftp服务器账号密码
		 **/
		private String password;

		private Builder() {
			this.port = 21;
		}

		/**
		 * 设置ftp服务器地址
		 * 
		 * @author gewx
		 * @param ip ftp服务器地址
		 * @return 构建者对象
		 **/
		public Builder ip(String ip) {
			this.ip = ip;
			return this;
		}

		/**
		 * 设置ftp服务器端口
		 * 
		 * @author gewx
		 * @param port ftp服务器端口
		 * @return 构建者对象
		 **/
		public Builder port(int port) {
			this.port = port;
			return this;
		}

		/**
		 * 设置ftp服务器账号
		 * 
		 * @author gewx
		 * @param userName ftp账号
		 * @return 构建者对象
		 **/
		public Builder setAcc(String userName) {
			this.userName = userName;
			return this;
		}

		/**
		 * 设置ftp服务器账号密码
		 * 
		 * @author gewx
		 * @param password ftp账号密码
		 * @return 构建者对象
		 **/
		public Builder setPwd(String password) {
			this.password = password;
			return this;
		}

		/**
		 * 构建ftp客户端对象
		 * 
		 * @author gewx
		 * @return ftp客户端
		 **/
		public FtpClient build() {
			try {
				FTPClient ftp = new FTPClient();
				ftp.setControlKeepAliveTimeout(-1);
				ftp.setControlKeepAliveReplyTimeout(-1);
				ftp.setConnectTimeout(1000 * 15);
				ftp.setControlEncoding("utf-8");
				ftp.setBufferSize(1024 * 1024);
				ftp.enterLocalPassiveMode();
				ftp.connect(this.ip, this.port);
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					ftp.disconnect();
					throw new RuntimeException("Ftp Connection Fail!");
				}
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				boolean isLogin = ftp.login(this.userName, this.password);
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode()) || !isLogin) {
					ftp.disconnect();
					throw new RuntimeException("Ftp Login Fail!");
				}
				return new FtpClient(ftp);
			} catch (Exception ex) {
				throw new RuntimeException("Ftp客户端构建失败, ex: " + ex.getMessage());
			}
		}
	}
}
