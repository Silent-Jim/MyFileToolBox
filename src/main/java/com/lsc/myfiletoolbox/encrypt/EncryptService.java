package com.lsc.myfiletoolbox.encrypt;

import com.alibaba.fastjson.JSON;
import com.lsc.myfiletoolbox.App;
import com.lsc.myfiletoolbox.common.CommonUtil;
import com.lsc.myfiletoolbox.common.FileByteUtil;
import com.lsc.myfiletoolbox.common.FileUtil;
import com.lsc.myfiletoolbox.encrypt.entity.FileStatus;
import com.lsc.myfiletoolbox.encrypt.entity.FileTail;

import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;

import static com.lsc.myfiletoolbox.App.ENCRYPTEDLENGTH;

public class EncryptService {

    public static int ENCRYPTMODE = 0;
    public static int DECRYPTMODE = 1;

    private Encryptor encryptor;
    private Base64.Encoder encoder;
    private Base64.Decoder decoder;
    private MessageDigest md5;
    private MessageDigest sha1;
    private File encryptionLog;
    //private BufferedWriter writer;
    private BufferedWriter appendWriter;
    private BufferedReader reader;

    public EncryptService() throws Exception {
        this.encryptor = new AESEncryptor();
        this.encoder = Base64.getUrlEncoder();
        this.decoder = Base64.getUrlDecoder();
        md5 = MessageDigest.getInstance("MD5");
        sha1 = MessageDigest.getInstance("SHA");
    }

    public EncryptService(Encryptor encryptor) throws Exception {
        this.encryptor = encryptor;
        this.encoder = Base64.getUrlEncoder();
        this.decoder = Base64.getUrlDecoder();
        md5 = MessageDigest.getInstance("MD5");
        sha1 = MessageDigest.getInstance("SHA");
    }


    public void encryptFile(String filepath, String password) throws Exception {
        String name = CommonUtil.getNameFromUrl(filepath);
        String folder = CommonUtil.getParentFolder(filepath);
        //忽略日志和备份文件
        if (App.ENCRYPTIONLOGNAME.equals(name) || App.HEADRECOVERYNAME.equals(name)
                || App.CURRENTFILESTATUSNAME.equals(name)) return;

        File file = new File(filepath);
        int encryptLength = (int) Math.min(file.length(), ENCRYPTEDLENGTH);

        File fileStatus = new File(CommonUtil.concatUrl(folder, App.CURRENTFILESTATUSNAME));
        if (!fileStatus.exists()) {
            fileStatus.createNewFile();
        }
        FileStatus fileStatusEntity = new FileStatus(filepath, file.length(), encryptLength, 0);
        FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);
        ///s0
        File headRec = new File(CommonUtil.concatUrl(folder, App.HEADRECOVERYNAME));
        if (!headRec.exists()) {
            headRec.createNewFile();
        }
        byte[] header = FileByteUtil.getFileHeader(filepath, encryptLength);
        byte[] headerMd5 = md5.digest(header);
        byte[] headerSha1 = sha1.digest(header);
        if (backupHead(headRec, header)) {
            fileStatusEntity.setOriginMd5(new String(headerMd5));
            fileStatusEntity.setOriginSha1(new String(headerSha1));
            fileStatusEntity.setStatus(1);
            FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);
        }
        ////s1
        byte[] result = encryptor.encrypt(header, password);
        FileByteUtil.modifyFileHeader(result, filepath);

        FileTail fileTailEntity = new FileTail();
        fileTailEntity.setEncryptedLength(CommonUtil.toByteArray(encryptLength));
        fileTailEntity.setOriginMd5(headerMd5);
        fileTailEntity.setOriginSha1(headerSha1);
        byte[] encryptedMd5 = md5.digest(result);
        byte[] encryptedSha1 = sha1.digest(result);
        fileTailEntity.setEncryptedMd5(encryptedMd5);
        fileTailEntity.setEncryptedSha1(encryptedSha1);
        byte[] encryptedTail = encryptor.encrypt(fileTailEntity.toByteArray(), password);
        FileByteUtil.appendFileTail(filepath, encryptedTail);
        ////
        byte[] encrypted = FileByteUtil.getFileHeader(filepath, encryptLength);
        byte[] origin = encryptor.decrypt(encrypted, password);
        byte[] readedTail = FileByteUtil.getFileTail(filepath, FileTail.TAILLENGTH);
        FileTail readedFileTail = new FileTail(encryptor.decrypt(readedTail, password));
        if (checkMessageDigest(header, origin) &&
                CommonUtil.checkSame(FileTail.TOUNCHSTONE, readedFileTail.getTouchStone())) {
            fileStatusEntity.setEncryptedMd5(new String(encryptedMd5));
            fileStatusEntity.setEncryptedSha1(new String(encryptedSha1));
            fileStatusEntity.setStatus(2);
            FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);
        }
        ///s2
        byte[] namebytes = encoder.encode(name.getBytes());
        file.renameTo(new File(folder + new String(namebytes)));
        clearup(filepath, fileStatus, headRec);
    }

    public void decryptFile(String filepath, String password) throws Exception {
        String name = CommonUtil.getNameFromUrl(filepath);
        String folder = CommonUtil.getParentFolder(filepath);
        if (App.ENCRYPTIONLOGNAME.equals(name) || App.HEADRECOVERYNAME.equals(name)
                || App.CURRENTFILESTATUSNAME.equals(name)) return;
        /////
        File file = new File(filepath);
        byte[] fileTailB = FileByteUtil.getFileTail(filepath, FileTail.TAILLENGTH);
        FileTail fileTail = new FileTail(encryptor.decrypt(fileTailB, password));
        if (!CommonUtil.checkSame(FileTail.TOUNCHSTONE, fileTail.getTouchStone())) {
            throw new Exception("密码错误或不是支持的加密后文件");
        }
        int encryptLength = CommonUtil.byteArrayToInt(fileTail.getEncryptedLength());
        //////
        File fileStatus = new File(CommonUtil.concatUrl(folder, App.CURRENTFILESTATUSNAME));
        if (!fileStatus.exists()) {
            fileStatus.createNewFile();
        }
        FileStatus fileStatusEntity = new FileStatus(filepath, file.length(), encryptLength, 0);
        FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);
        ///s0
        File headRec = new File(CommonUtil.concatUrl(folder, App.HEADRECOVERYNAME));
        if (!headRec.exists()) {
            headRec.createNewFile();
        }
        byte[] header = FileByteUtil.getFileHeader(filepath, encryptLength);
        byte[] headerMd5 = md5.digest(header);
        byte[] headerSha1 = sha1.digest(header);
        if (backupHead(headRec, header)) {
            fileStatusEntity.setOriginMd5(new String(headerMd5));
            fileStatusEntity.setOriginSha1(new String(headerSha1));
            fileStatusEntity.setStatus(1);
            FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);
        }
        ////s1
        byte[] result = encryptor.decrypt(header, password);
        if (CommonUtil.checkSame(fileTail.getOriginMd5(), md5.digest(result))
                && CommonUtil.checkSame(fileTail.getOriginSha1(), sha1.digest(result))) {
        } else {
            throw new Exception("解密错误");
        }
        FileByteUtil.modifyFileHeader(result, filepath);
        byte[] readed=FileByteUtil.getFileHeader(filepath,encryptLength);
        if(!checkMessageDigest(result,readed)){
            throw new Exception("写入head错误");
        }
        FileByteUtil.removeTail(filepath, FileTail.TAILLENGTH);
        fileStatusEntity.setStatus(2);
        FileUtil.writeToFile(fileStatus, JSON.toJSONBytes(fileStatusEntity), false);

        byte[] namebytes = decoder.decode(name.getBytes());
        file.renameTo(new File(folder + new String(namebytes)));
        clearup(filepath, fileStatus, headRec);
    }

    //加密解密文件夹内文件
    public void encryptFolderFile(String folderPath, String password, int mode, boolean encryptFolder) throws Exception {
        File directory = new File(folderPath);
        if (directory.isDirectory()) {
            createLog(CommonUtil.concatUrl(folderPath, App.ENCRYPTIONLOGNAME));
            File[] filelist = directory.listFiles();
            for (int i = 0; i < filelist.length; i++) {
                /**如果当前是文件夹，进入递归扫描文件夹**/
                if (filelist[i].isDirectory()) {
                    /**递归扫描下面的文件夹**/
                    System.out.println("正在处理:" + filelist[i].getAbsolutePath());
                    encryptFolderFile(filelist[i].getAbsolutePath(), password, mode, encryptFolder);
                    //加密文件夹
                    if (encryptFolder) {
                        String parentFolder = CommonUtil.getParentFolder(filelist[i].getAbsolutePath());
                        String name = CommonUtil.getNameFromUrl(filelist[i].getAbsolutePath());
                        File file = new File(filelist[i].getAbsolutePath());
                        if (mode == ENCRYPTMODE) {
                            byte[] namebytes = encoder.encode(name.getBytes());
                            file.renameTo(new File(parentFolder + new String(namebytes)));
                        } else if (mode == DECRYPTMODE) {
                            byte[] namebytes = decoder.decode(name.getBytes());
                            file.renameTo(new File(parentFolder + new String(namebytes)));
                        }
                    }
                    System.out.println("处理完毕:" + filelist[i].getAbsolutePath());
                }
                /**非文件夹**/
                else {
                    String filePath = filelist[i].getAbsolutePath();
                    if (mode == ENCRYPTMODE) {
                        encryptFile(filePath, password);
                    } else if (mode == DECRYPTMODE) {
                        decryptFile(filePath, password);
                    }
                }
            }
        } else {
            String father = CommonUtil.getParentFolder(folderPath);
            createLog(CommonUtil.concatUrl(father, App.ENCRYPTIONLOGNAME));
            if (mode == ENCRYPTMODE) {
                encryptFile(folderPath, password);
            } else if (mode == DECRYPTMODE) {
                decryptFile(folderPath, password);
            }
        }
        beforeFinish();
    }

    public boolean checkMessageDigest(byte[] a, byte[] b) {
        byte[] aMd5 = md5.digest(a);
        byte[] aSha1 = sha1.digest(a);
        byte[] bMd5 = md5.digest(b);
        byte[] bSha1 = sha1.digest(b);
        if (CommonUtil.checkSame(aMd5, bMd5) && CommonUtil.checkSame(aSha1, bSha1)) {
            return true;
        } else return false;
    }

    private boolean backupHead(File headRec, byte[] header) {
        FileByteUtil.writeFromHead(headRec.getAbsolutePath(), header);
        byte[] readed = FileByteUtil.getFileHeader(headRec.getAbsolutePath(), header.length);
        if (checkMessageDigest(header, readed)) {
            return true;
        } else return false;
    }

    private void createLog(String url) throws Exception {
        encryptionLog = new File(url);
        if (encryptionLog.exists()) {
            if (!encryptionLog.delete()) {
                new Exception("旧日志删除失败");
            }
        }
        if (!encryptionLog.exists()) {
            encryptionLog.createNewFile();
        }
        appendWriter = new BufferedWriter(new FileWriter(encryptionLog, true));
        reader = new BufferedReader(new FileReader(encryptionLog));
        appendWriter.write(String.format("%s\n", "encryptionlength=" + App.ENCRYPTEDLENGTH));
        appendWriter.flush();
    }

    private void clearup(String filepath, File fileStatus, File headRec) throws Exception {
        appendWriter.write(String.format("%s\n", filepath));
        appendWriter.write(String.format("%s\n", "finished"));
        appendWriter.flush();
        fileStatus.delete();
        headRec.delete();
    }

    private void beforeFinish() {
        try {
            appendWriter.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }
}
