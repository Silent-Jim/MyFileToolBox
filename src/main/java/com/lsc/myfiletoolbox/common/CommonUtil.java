package com.lsc.myfiletoolbox.common;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description 公共工具，比较杂的小功能有不必写成单独工具类的
 * @Author Liu Shaochen
 * @Date 2019/7/09 0027 16:19
 **/
public class CommonUtil {

    public static String getNameFromUrl(String url) {
        url = url.trim();
        String name = url;
        String separator = "";
        if (url.contains("/")) {
            separator = "/";
        } else if (url.contains("\\")) {
            separator = "\\";
        }
        name = url.substring(url.lastIndexOf(separator) + 1);
        return name;
    }

    public static String getParentFolder(String url) {
        url = url.trim();
        String folder = url;
        String separator = "";
        if (url.contains("/")) {
            separator = "/";
        } else if (url.contains("\\")) {
            separator = "\\";
        }
        folder = url.substring(0, url.lastIndexOf(separator) + 1);
        return folder;
    }

    public static String getNameWithoutSuffix(String url) {
        url = getNameFromUrl(url);
        return url.substring(0, url.lastIndexOf("."));
    }

    public static String getFileSuffix(String url) {
        return url.substring(url.lastIndexOf(".") + 1, url.length());
    }

    public static String getRemainUrl(String key, String fileUrl) {
        int index = fileUrl.indexOf(key);
        return fileUrl.substring(index);
    }

    public static String concatUrl(String url, String name) {
        url = url.trim();
        String folder = url;
        String separator = "";
        if (url.contains("/")) {
            separator = "/";
        } else if (url.contains("\\")) {
            separator = "\\";
        }
        if (url.endsWith(separator)) {
            return url + name;
        } else {
            return url + separator + name;
        }
    }


    public static Set interSection(Set a, Set b) {
        Set tmp = new HashSet();
        tmp.addAll(a);
        tmp.retainAll(b);
        return tmp;
    }

    public static Set union(Set a, Set b) {
        Set tmp = new HashSet();
        tmp.addAll(a);
        tmp.addAll(b);
        return tmp;
    }

    public static Set differenceSet(Set a, Set b) {
        Set tmp = new HashSet();
        tmp.addAll(a);
        tmp.removeAll(b);
        return tmp;
    }


    //反序列化实现深克隆，避免对被合并的对象造成影响
    public static Object clone(Object obj) {

        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream objOut = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream objIn = null;

        try {
            byteOut = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(obj);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            objIn = new ObjectInputStream(byteIn);
            return objIn.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Clone Object failed in IO.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found.", e);
        } finally {
            try {
                byteIn = null;
                byteOut = null;
                if (objOut != null) objOut.close();
                if (objIn != null) objIn.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean checkSame(byte[] a, byte[] b) {
        if (a == null || b == null) {
            if (a == null && b == null) return true;
            else return false;
        }
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
}


