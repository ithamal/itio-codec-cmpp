package io.github.ithmal.itio.codec.cmpp.base;

import io.github.ithmal.itio.codec.cmpp.util.ByteUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author: ken.lin
 * @since: 2023-10-01 08:54
 */
@Getter
public class AuthenticatorSource {

    /**
     * 用户名
     */
    @Setter
    private String sourceAddr;

    /**
     * 密码
     */
    @Setter
    private String password;

    /**
     * MMDDHHMMSS，即月日时分秒，10 位
     */
    private final int timestamp;

    /**
     * 加密后数据
     */
    private byte[] digestBytes;

    public AuthenticatorSource(String sourceAddr, String password, int timestamp) {
        this.sourceAddr = sourceAddr;
        this.password = password;
        this.timestamp = timestamp;
    }

    public AuthenticatorSource(int timestamp, byte[] digestBytes) {
        this.timestamp = timestamp;
        this.digestBytes = digestBytes;
    }

    @SneakyThrows
    private byte[] generateDigestBytes() {
        Charset charset = StandardCharsets.US_ASCII;
        byte[] sourceAddrBytes = sourceAddr.getBytes(charset);
        byte[] passwordBytes = password.getBytes(charset);
        byte[] timestampBytes = String.valueOf(timestamp).getBytes(charset);
        byte[] bytes = ByteUtils.concat(sourceAddrBytes, new byte[9], passwordBytes, timestampBytes);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(bytes);
    }

    public byte[] getDigestBytes() {
        if (digestBytes != null) {
            return digestBytes;
        }
        return generateDigestBytes();
    }

    public boolean validate() {
        if (digestBytes == null) {
            return false;
        }
        return Arrays.toString(generateDigestBytes()).equals(Arrays.toString(digestBytes));
    }

    @Override
    public String toString() {
        return Arrays.toString(getDigestBytes());
    }
}
