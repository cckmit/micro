package micro.commons.ftp;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static micro.commons.util.StringUtil.getErrorText;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import micro.commons.annotation.ThreadSafe;
import micro.commons.log.MicroLogger;

/**
 * Ftp客户端工具类
 * 
 * @author gewx
 **/

@ThreadSafe
public final class FtpClient implements Closeable {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(FtpClient.class);

	/**
	 * ftp连接超时
	 **/
	private static final int FTP_CONNECT_TIMEOUT = 1000 * 15;

	/**
	 * ftp打开连接超时
	 **/
	private static final int FTP_SO_TIMEOUT = 1000 * 15;

	/**
	 * ftp数据读取超时
	 **/
	private static final int FTP_DATA_TIMEOUT = 1000 * 15;

	/**
	 * ftp连接报活超时
	 **/
	private static final int FTP_KEEPALIVE_TIMEOUT = 1000;

	/**
	 * ftp读写缓冲区
	 **/
	private static final int FTP_BUFFER_SIZE = 1024 * 1024;

	/**
	 * ftp数据传输编码
	 **/
	private static final String CHARSET = "utf-8";

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
		final String methodName = "storeFile";
		LOGGER.enter(methodName, "fileName: " + fileName);

		if (isNotBlank(directory)) {
			if (!ftp.changeWorkingDirectory(directory)) {
				LOGGER.error(methodName, "文件上传失败,不存在的目录!");
				throw new RuntimeException("文件上传失败,不存在的目录!");
			}
		}
		boolean isUpload = ftp.storeFile(fileName, stream);
		if (!isUpload) {
			LOGGER.error(methodName, "文件上传失败!");
			throw new RuntimeException("文件上传失败!");
		}
	}

	/**
	 * 上传ftp文件
	 * 
	 * @author gewx
	 * @param fileName 上传文件名
	 * @param stream   输入字节流
	 * @throws IOException
	 * @return void
	 **/
	public void storeFile(String fileName, InputStream stream) throws IOException {
		storeFile(fileName, stream, null);
	}

	/**
	 * 读取ftp文件
	 * 
	 * @author gewx
	 * @param fileName  读取文件名
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return 文件字符串
	 **/
	public String readFile(String fileName, String directory) throws IOException {
		final String methodName = "readFile";
		LOGGER.enter(methodName, "fileName: " + fileName);

		if (isNotBlank(directory)) {
			if (!ftp.changeWorkingDirectory(directory)) {
				LOGGER.error(methodName, "文件上传失败,不存在的目录!");
				throw new RuntimeException("文件上传失败,不存在的目录!");
			}
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		boolean isDownload = ftp.retrieveFile(fileName, byteOut);
		if (!isDownload) {
			LOGGER.error(methodName, "文件读取失败!");
			throw new RuntimeException("文件读取失败!");
		}
		return byteOut.toString(CHARSET);
	}

	/**
	 * 读取ftp文件
	 * 
	 * @author gewx
	 * @param fileName 读取文件名
	 * @throws IOException
	 * @return 文件字符串
	 **/
	public String readFile(String fileName) throws IOException {
		return readFile(fileName, null);
	}

	/**
	 * 读取ftp文件
	 * 
	 * @author gewx
	 * @param fileName  读取文件名
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return 文件字节流
	 **/
	public byte[] readFileToByte(String fileName, String directory) throws IOException {
		final String methodName = "readFileToByte";
		LOGGER.enter(methodName, "fileName: " + fileName);

		if (isNotBlank(directory)) {
			if (!ftp.changeWorkingDirectory(directory)) {
				LOGGER.error(methodName, "文件上传失败,不存在的目录!");
				throw new RuntimeException("文件上传失败,不存在的目录!");
			}
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		boolean isDownload = ftp.retrieveFile(fileName, byteOut);
		if (!isDownload) {
			LOGGER.error(methodName, "文件读取失败!");
			throw new RuntimeException("文件读取失败!");
		}
		return byteOut.toByteArray();
	}

	/**
	 * 读取ftp文件
	 * 
	 * @author gewx
	 * @param fileName 读取文件名
	 * @throws IOException
	 * @return 文件字节流
	 **/
	public byte[] readFileToByte(String fileName) throws IOException {
		return readFileToByte(fileName, null);
	}

	/**
	 * 删除ftp文件
	 * 
	 * @author gewx
	 * @param fileName 删除文件名
	 * @throws IOException
	 * @return void
	 **/
	public void deleteFile(String fileName) throws IOException {
		final String methodName = "deleteFile";
		LOGGER.enter(methodName, "fileName: " + fileName);

		boolean isDel = ftp.deleteFile(fileName);
		if (!isDel) {
			LOGGER.error(methodName, "文件删除失败!");
			throw new RuntimeException("文件删除失败!");
		}
	}

	/**
	 * 读取指定目录下ftp文件
	 * 
	 * @author gewx
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return 文件集合
	 **/
	public List<String> listFile(String directory) throws IOException {
		final String methodName = "listFile";
		LOGGER.enter(methodName, "directory: " + directory);

		if (isNotBlank(directory)) {
			if (!ftp.changeWorkingDirectory(directory)) {
				LOGGER.error(methodName, "文件读取失败,不存在的目录!");
				throw new RuntimeException("文件读取失败,不存在的目录!");
			}
		}

		List<String> fileList = Stream.of(ftp.listFiles()).map(val -> val.getName()).collect(Collectors.toList());
		return fileList;
	}

	/**
	 * 变更ftp目录
	 * 
	 * @author gewx
	 * @param directory 文件存储目录
	 * @throws IOException
	 * @return void
	 **/
	public void changeWorkingDirectory(String directory) throws IOException {
		final String methodName = "changeWorkingDirectory";
		LOGGER.enter(methodName, "directory: " + directory);

		boolean isChange = ftp.changeWorkingDirectory(directory);
		if (!isChange) {
			LOGGER.error(methodName, "文件目录切换失败!");
			throw new RuntimeException("文件目录切换失败!");
		}
	}

	/**
	 * 返回文件夹上一级目录
	 * 
	 * @author gewx
	 * @throws IOException
	 * @return void
	 **/
	public void changeToParentDirectory() throws IOException {
		final String methodName = "changeToParentDirectory";
		LOGGER.enter(methodName);

		boolean isChange = ftp.changeToParentDirectory();
		if (!isChange) {
			LOGGER.error(methodName, "文件目录切换失败!");
			throw new RuntimeException("文件目录切换失败!");
		}
	}

	/**
	 * 获取当前ftp工作目录
	 * 
	 * @author gewx
	 * @throws IOException
	 * @return 当前目录
	 **/
	public String getDirectory() throws IOException {
		return ftp.printWorkingDirectory();
	}

	/**
	 * 新增目录
	 * 
	 * @author gewx
	 * @param directory 目录
	 * @throws IOException
	 * @return 当前目录
	 **/
	public void mkdir(String directory) throws IOException {
		final String methodName = "mkdir";
		LOGGER.enter(methodName, "directory: " + directory);

		boolean isMkdir = ftp.makeDirectory(directory);
		if (!isMkdir) {
			LOGGER.error(methodName, "新增目录失败!");
			throw new RuntimeException("新增目录失败!");
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
			final String methodName = "build";

			try {
				FTPClient ftp = new FTPClient();
				ftp.setControlKeepAliveTimeout(FTP_KEEPALIVE_TIMEOUT);
				ftp.setConnectTimeout(FTP_CONNECT_TIMEOUT);
				ftp.setDataTimeout(FTP_DATA_TIMEOUT);
				ftp.setControlEncoding(CHARSET);
				ftp.setBufferSize(FTP_BUFFER_SIZE);
				ftp.enterLocalPassiveMode();
				ftp.connect(this.ip, this.port);
				ftp.setSoTimeout(FTP_SO_TIMEOUT);
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					ftp.disconnect();
					LOGGER.error(methodName, "ftp客户端连接失败!");
					throw new RuntimeException("ftp客户端连接失败!");
				}
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				boolean isLogin = ftp.login(this.userName, this.password);
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode()) || !isLogin) {
					ftp.disconnect();
					LOGGER.error(methodName, "ftp客户端登录失败!");
					throw new RuntimeException("ftp客户端登录失败!");
				}
				return new FtpClient(ftp);
			} catch (Exception ex) {
				LOGGER.error(methodName, "ftp客户端构建失败, ex: " + getErrorText(ex));
				throw new RuntimeException("ftp客户端构建失败, ex: " + getErrorText(ex));
			}
		}
	}

	@Override
	public void close() throws IOException {
		final String methodName = "close";
		LOGGER.enter(methodName);
		try {
			LOGGER.info(methodName, "当前ftp服务器连接状态: " + ftp.isConnected());
			if (ftp.isConnected()) {
				ftp.logout();
				ftp.disconnect();
			}
			LOGGER.exit(methodName, "当前ftp服务器连接状态: " + ftp.isConnected());
		} catch (Exception ex) {
			LOGGER.error(methodName, "ftp客户端链接关闭失败, ex: " + getErrorText(ex));
		}
	}
}
