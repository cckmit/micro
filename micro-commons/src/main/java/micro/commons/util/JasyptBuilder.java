package micro.commons.util;

import lombok.Getter;
import micro.commons.annotation.ThreadSafe;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

/**
 * jasypt PBE基于口令加密
 * 
 * @author gewx
 **/
@ThreadSafe
public final class JasyptBuilder {

	private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private final PooledPBEStringEncryptor encryptor;

	private JasyptBuilder(Builder builder) {
		encryptor = new PooledPBEStringEncryptor();
		encryptor.setPoolSize(CORE_SIZE);
		encryptor.setPassword(builder.getSalt());
		encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Getter
	public static class Builder {

		private String salt;

		public Builder setSalt(String salt) {
			this.salt = salt;
			return this;
		}

		public JasyptBuilder build() {
			Assert.isNotBlank(salt, "秘钥必填!");
			return new JasyptBuilder(this);
		}
	}

	/**
	 * 加密
	 * 
	 * @author gewx
	 * @param val 加密字符串
	 * @return 返回加密后的字符串
	 **/
	public String encrypt(String val) {
		return encryptor.encrypt(val);
	}

	/**
	 * 解密
	 * 
	 * @author gewx
	 * @param val 解密字符串
	 * @return 返回解密后的字符串
	 **/
	public String decrypt(String val) {
		return encryptor.decrypt(val);
	}
}
