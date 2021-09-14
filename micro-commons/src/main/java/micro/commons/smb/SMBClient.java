package micro.commons.smb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import lombok.Getter;
import lombok.ToString;
import micro.commons.annotation.ThreadSafe;
import micro.commons.exception.BusinessRuntimeException;
import micro.commons.util.ValidatorUtils;

/**
 * SMB协议客户端
 * 
 * @author gewx
 **/
@ThreadSafe
public final class SMBClient {

	/**
	 * 数据传输编码
	 **/
	private static final String CHARSET = "utf-8";

	/**
	 * 秘钥
	 **/
	private final NtlmPasswordAuthentication ntlmPasswordAuthentication;

	/**
	 * 根路径
	 **/
	private final String path;

	private SMBClient(String path, NtlmPasswordAuthentication ntlmPasswordAuthentication) {
		this.ntlmPasswordAuthentication = ntlmPasswordAuthentication;
		this.path = path;
	}

	/**
	 * 创建SMBClient构建器
	 * 
	 * @author gewx
	 * @return 构建器对象
	 **/
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * 新建目录
	 * 
	 * @author gewx
	 * @param subPath 目录名
	 * @throws IOException
	 * @return void
	 **/
	public void mkdir(String subPath) throws IOException {
		SmbFile remoteFile = new SmbFile(path + subPath, ntlmPasswordAuthentication);
		if (!remoteFile.exists()) {
			remoteFile.mkdir();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @author gewx
	 * @param fullPathFileName 上传文件名
	 * @param stream           输入字节流
	 * @throws IOException
	 * @return void
	 **/
	public void storeFile(String fullPathFileName, InputStream stream) throws IOException {
		SmbFile remoteFile = new SmbFile(path + fullPathFileName, ntlmPasswordAuthentication);
		remoteFile.getOutputStream().write(IOUtils.toByteArray(stream));
	}

	/**
	 * 读取文件
	 * 
	 * @author gewx
	 * @param fullPathFileName 读取文件名
	 * @throws IOException
	 * @return 文件字符串
	 **/
	public String readFile(String fullPathFileName) throws IOException {
		SmbFile remoteFile = new SmbFile(path + fullPathFileName, ntlmPasswordAuthentication);
		try (InputStream input = remoteFile.getInputStream()) {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			byteOut.write(IOUtils.toByteArray(input));
			return byteOut.toString(CHARSET);
		}
	}

	/**
	 * 读取文件
	 * 
	 * @author gewx
	 * @param fullPathFileName 读取文件名
	 * @throws IOException
	 * @return 文件字节流
	 **/
	public byte[] readFileToByte(String fullPathFileName) throws IOException {
		SmbFile remoteFile = new SmbFile(path + fullPathFileName, ntlmPasswordAuthentication);
		try (InputStream input = remoteFile.getInputStream()) {
			return IOUtils.toByteArray(input);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @author gewx
	 * @param fullPathFileName 删除文件名
	 * @throws IOException
	 * @return void
	 **/
	public void deleteFile(String fullPathFileName) throws IOException {
		SmbFile remoteFile = new SmbFile(path + fullPathFileName, ntlmPasswordAuthentication);
		remoteFile.delete();
	}

	/**
	 * @author gewx
	 * @param subPath 子目录
	 * @return 文件/目录集合
	 **/
	public SmbFile[] listFile(String subPath) {
		String fullPath = new StringBuilder(path + subPath).toString();
		try {
			SmbFile remoteFile = new SmbFile(fullPath, ntlmPasswordAuthentication);
			return remoteFile.listFiles();
		} catch (Exception ex) {
			throw new RuntimeException("SMB文件读取失败！ex: " + ex.getMessage());
		}
	}

	/**
	 * 构建器
	 **/
	@Getter
	@ToString
	public static class Builder implements Serializable {

		private static final long serialVersionUID = 6450177493605118256L;

		/**
		 * SMB根路径
		 **/
		@NotBlank(message = "SMB根路径必填!")
		private String path;

		/**
		 * 域
		 **/
		private String domain;

		/**
		 * 账号
		 **/
		@NotBlank(message = "账号必填!")
		private String userName;

		/**
		 * 密码
		 **/
		@NotBlank(message = "密码必填!")
		private String password;

		public Builder() {
			this.domain = StringUtils.EMPTY;
		}

		/**
		 * 新增SMB根服务器地址
		 * 
		 * @author gewx
		 * @param path 根服务器地址
		 * @return 构建者对象
		 **/
		public Builder path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * 设置SMB服务器账号
		 * 
		 * @author gewx
		 * @param userName 账号
		 * @return 构建者对象
		 **/
		public Builder setAcc(String userName) {
			this.userName = userName;
			return this;
		}

		/**
		 * 设置SMB服务器账号密码
		 * 
		 * @author gewx
		 * @param password 账号密码
		 * @return 构建者对象
		 **/
		public Builder setPwd(String password) {
			this.password = password;
			return this;
		}

		/**
		 * 构建SMB客户端对象
		 * 
		 * @author gewx
		 * @return SMBClient客户端
		 **/
		public SMBClient build() {
			ValidatorUtils.FieldBean bean = ValidatorUtils.validator(this);
			if (bean.isSuccess()) {
				throw new BusinessRuntimeException("SMB客户端构建失败, ex: " + bean.getMsg());
			}

			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(getDomain(), getUserName(), getPassword());
			return new SMBClient(getPath(), auth);
		}
	}
}
