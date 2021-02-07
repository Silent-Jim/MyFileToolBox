package com.lsc.myfiletoolbox.encrypt.entity;

public class FileStatus {
    private String fileName;
    private String encryptedName;
    private long fileLength;
    private int encryptedLength;
    private int status;
    private String originMd5;
    private String originSha1;
    private String encryptedMd5;
    private String encryptedSha1;
    private String content;
    private String extension;

    public FileStatus() { }

    public FileStatus(String fileName, long fileLength, int encryptedLength, int status) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.encryptedLength = encryptedLength;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getEncryptedName() {
        return encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getEncryptedLength() {
        return encryptedLength;
    }

    public void setEncryptedLength(int encryptedLength) {
        this.encryptedLength = encryptedLength;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOriginMd5() {
        return originMd5;
    }

    public void setOriginMd5(String originMd5) {
        this.originMd5 = originMd5;
    }

    public String getOriginSha1() {
        return originSha1;
    }

    public void setOriginSha1(String originSha1) {
        this.originSha1 = originSha1;
    }

    public String getEncryptedMd5() {
        return encryptedMd5;
    }

    public void setEncryptedMd5(String encryptedMd5) {
        this.encryptedMd5 = encryptedMd5;
    }

    public String getEncryptedSha1() {
        return encryptedSha1;
    }

    public void setEncryptedSha1(String encryptedSha1) {
        this.encryptedSha1 = encryptedSha1;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
