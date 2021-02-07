package com.lsc.myfiletoolbox.encrypt;

import com.lsc.myfiletoolbox.App;
import com.lsc.myfiletoolbox.common.CommonUtil;
import com.lsc.myfiletoolbox.common.FileByteUtil;

import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;

import static com.lsc.myfiletoolbox.App.ENCRYPTEDLENGTH;

public class EncryptService {

    public static int ENCRYPTMODE = 0;
    public static int DECRYPTMODE = 1;

    private static byte[] touchStone = {54, 117, 1, 81, 5, 86, 98, 16, 18, 96, 47, 66, 82, 122, 44, 67};

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
        //long st=System.currentTimeMillis();
        String name = CommonUtil.getNameFromUrl(filepath);
        String folder = CommonUtil.getParentFolder(filepath);
        if (App.ENCRYPTIONLOGNAME.equals(name) || App.HEADRECOVERY.equals(name)
                || App.CURRENTFILESTATUSNAME.equals(name)) return;
        File file = new File(filepath);
        File fileStatus=new File(CommonUtil.concatUrl(folder,App.CURRENTFILESTATUSNAME));
        if(!fileStatus.exists()){
            fileStatus.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(encryptionLog, false));
        writer.write(String.format("%-255s\n", filepath));
        writer.write(String.format("%-255s\n", file.length()));
        writer.flush();


        int encryptLength = (int) Math.min(file.length(), ENCRYPTEDLENGTH);
        byte[] header = FileByteUtil.getFileHeader(filepath, encryptLength);
        byte[] result = null;
        result = encryptor.encrypt(header, password);
        byte[] headerMd5B = md5.digest(header);
        byte[] headerSha1B = sha1.digest(header);
        if (result == null) {
            throw new Exception("加密错误");
        }
        FileByteUtil.modifyFileHeader(result, filepath);
        byte[] namebytes = encoder.encode(name.getBytes());
        file.renameTo(new File(folder + new String(namebytes)));
        appendWriter.write(String.format("%s\n", filepath));
        appendWriter.write(String.format("%s\n", "finished"));
        appendWriter.flush();
        //long dis=System.currentTimeMillis()-st;
        //System.out.println(dis+"  "+filepath);
        writer.close();
    }

    public void decryptFile(String filepath, String password) throws Exception {
        //long st=System.currentTimeMillis();
        String name = CommonUtil.getNameFromUrl(filepath);
        String folder = CommonUtil.getParentFolder(filepath);
        if (App.ENCRYPTIONLOGNAME.equals(name) || App.HEADRECOVERY.equals(name)
                || App.CURRENTFILESTATUSNAME.equals(name)) return;
        File file = new File(filepath);
        int encryptLength = (int) Math.min(file.length(), ENCRYPTEDLENGTH);
        byte[] header = FileByteUtil.getFileHeader(filepath, encryptLength);
        byte[] result = null;
        result = encryptor.decrypt(header, password);
        byte[] headerMd5B = md5.digest(header);
        byte[] headerSha1B = sha1.digest(header);
        if (result == null) {
            throw new Exception("解密错误");
        }
        FileByteUtil.modifyFileHeader(result, filepath);
        byte[] namebytes = decoder.decode(name.getBytes());
        file.renameTo(new File(folder + new String(namebytes)));
        //long dis=System.currentTimeMillis()-st;
        //System.out.println(dis+"  "+filepath);
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
