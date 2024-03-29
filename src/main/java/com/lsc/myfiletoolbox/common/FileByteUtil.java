package com.lsc.myfiletoolbox.common;

import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class FileByteUtil {
    /**
     * 修改文件头的前两个字节
     *
     * @param header
     * @param filePath
     * @throws Exception
     */
    public static boolean modifyFileHeader(byte[] header, String filePath) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.seek(0);
            src.write(header);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("修改文件header失败!" + filePath);
            return false;
        }
    }

    /**
     * 根据文件路径获取文件头前两个字节
     *
     * @param filePath 文件路径
     * @return 文件头前两个字节信息
     */
    public static byte[] getFileHeader(String filePath, int len) {
        byte[] value = null;
        try (FileInputStream is = new FileInputStream(filePath)) {
            value = new byte[len];
            is.read(value, 0, len);
            return value;
        } catch (Exception e) {
            System.out.println("获取文件header失败!" + filePath);
            return null;
        }
    }

    public static byte[] getFileTail(String filePath, int len) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.seek(src.length() - len);
            byte[] res = new byte[len];
            src.read(res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取tail失败!" + filePath);
            return null;
        }
    }

    public static boolean appendFileTail(String filePath, byte[] tail) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.seek(src.length());
            src.write(tail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("添加tail失败!" + filePath);
            return false;
        }
    }

    public static boolean writeFromHead(String filePath, byte[] b) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.setLength(0);
            src.seek(0);
            src.write(b);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("写入失败!" + filePath);
            return false;
        }
    }

    public static boolean removeTail(String filePath, int len) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.setLength(src.length() - len);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("清除尾部失败!" + filePath);
            return false;
        }
    }

    public static boolean clearContent(String filePath) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            src.setLength(0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("清空文件失败!" + filePath);
            return false;
        }
    }

}
