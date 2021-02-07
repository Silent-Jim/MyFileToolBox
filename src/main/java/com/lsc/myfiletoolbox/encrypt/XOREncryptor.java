package com.lsc.myfiletoolbox.encrypt;

public class XOREncryptor implements Encryptor{
    @Override
    public byte[] encrypt(byte[] content, String password) {
        byte[] passbyte=password.getBytes();
        byte[] encrypted=new byte[content.length];
        for (int i = 0; i <content.length ; i++) {
            encrypted[i]=(byte) (content[i]^passbyte[i%passbyte.length]);
        }
        return encrypted;
    }

    @Override
    public byte[] decrypt(byte[] content, String password) {
        return encrypt(content,password);
    }
}
