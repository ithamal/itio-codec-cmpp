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
public class AuthenticatorISMG {

    private int status;

    @Setter
    private AuthenticatorSource authenticatorSource;

    @Setter
    private String password;

    /**
     * 加密后数据
     */
    private byte[] digestBytes;

    public AuthenticatorISMG(short status, AuthenticatorSource authenticatorSource, String password) {
        this.status = status;
        this.authenticatorSource = authenticatorSource;
        this.password = password;
    }

    public AuthenticatorISMG(int status, byte[] digestBytes) {
        this.status = status;
        this.digestBytes = digestBytes;
    }

    @SneakyThrows
    private byte[] generateDigestBytes() {
        Charset charset = StandardCharsets.US_ASCII;
        byte[] statusBytes = String.valueOf(status).getBytes();
        byte[] authenticatorSourceBytes =  authenticatorSource.getDigestBytes();
        byte[] passwordBytes = password.getBytes(charset);
        byte[] bytes = ByteUtils.concat(statusBytes, authenticatorSourceBytes, passwordBytes);
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
