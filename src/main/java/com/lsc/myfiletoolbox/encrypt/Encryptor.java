package com.lsc.myfiletoolbox.encrypt;

public interface Encryptor {
    public byte[] encrypt(byte[] content, String password);
    public byte[] decrypt(byte[] content, String password);
}
