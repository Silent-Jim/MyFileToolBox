package com.lsc.myfiletoolbox.encrypt.entity;

import java.lang.reflect.Array;

public class FileTail {
    private static byte[] TOUNCHSTONE = {54, 117, 1, 81, 5, 86, 98, 16, 18, 96, 47, 66, 82, 122, 44, 67};
    private byte[] touchStone;
    private byte[] encryptedLength;
    private byte[] originMd5;
    private byte[] originSha1;
    private byte[] encryptedMd5;
    private byte[] encryptedSha1;

    public byte[] toByteArray(){
        byte[] b= new byte[92];
        int cnt=0;
        for (int i = 0; i <16 ; i++) {
            b[cnt]=touchStone[i];
            cnt++;
        }
        for (int i = 0; i <4 ; i++) {
            b[cnt]=encryptedLength[i];
            cnt++;
        }
        for (int i = 0; i <16 ; i++) {
            b[cnt]=originMd5[i];
            cnt++;
        }
        for (int i = 0; i <20; i++) {
            b[cnt]=originSha1[i];
            cnt++;
        }
        for (int i = 0; i <16 ; i++) {
            b[cnt]=encryptedMd5[i];
            cnt++;
        }
        for (int i = 0; i <20; i++) {
            b[cnt]=encryptedSha1[i];
            cnt++;
        }
        return b;
    }

    public FileTail(byte[] b)throws Exception {
        if(b.length!=92){
            throw new Exception("byte length must be 92");
        }
        int cnt=0;
        for (int i = 0; i <16 ; i++) {
            touchStone[i]=b[cnt];
            cnt++;
        }
        for (int i = 0; i <4 ; i++) {
            encryptedLength[i]=b[cnt];
            cnt++;
        }
        for (int i = 0; i <16 ; i++) {
            originMd5[i]=b[cnt];
            cnt++;
        }
        for (int i = 0; i <20; i++) {
            originSha1[i]=b[cnt];
            cnt++;
        }
        for (int i = 0; i <16 ; i++) {
            encryptedMd5[i]=b[cnt];
            cnt++;
        }
        for (int i = 0; i <20; i++) {
            encryptedSha1[i]=b[cnt];
            cnt++;
        }
    }

    public FileTail() {
        touchStone=FileTail.TOUNCHSTONE;
    }

    public static byte[] getTOUNCHSTONE() {
        return TOUNCHSTONE;
    }

    public static void setTOUNCHSTONE(byte[] TOUNCHSTONE) {
        FileTail.TOUNCHSTONE = TOUNCHSTONE;
    }

    public byte[] getTouchStone() {
        return touchStone;
    }

    public void setTouchStone(byte[] touchStone) {
        this.touchStone = touchStone;
    }

    public byte[] getEncryptedLength() {
        return encryptedLength;
    }

    public void setEncryptedLength(byte[] encryptedLength) {
        this.encryptedLength = encryptedLength;
    }

    public byte[] getOriginMd5() {
        return originMd5;
    }

    public void setOriginMd5(byte[] originMd5) {
        this.originMd5 = originMd5;
    }

    public byte[] getOriginSha1() {
        return originSha1;
    }

    public void setOriginSha1(byte[] originSha1) {
        this.originSha1 = originSha1;
    }

    public byte[] getEncryptedMd5() {
        return encryptedMd5;
    }

    public void setEncryptedMd5(byte[] encryptedMd5) {
        this.encryptedMd5 = encryptedMd5;
    }

    public byte[] getEncryptedSha1() {
        return encryptedSha1;
    }

    public void setEncryptedSha1(byte[] encryptedSha1) {
        this.encryptedSha1 = encryptedSha1;
    }
}
