package com.lsc.myfiletoolbox;

import com.lsc.myfiletoolbox.encrypt.EncryptService;

import static com.lsc.myfiletoolbox.App.DEFAULTPASSWORD;

public class Entrance {

    public static void main(String[] args)throws Exception {
        boolean paramcheck = true;
        if (args.length >= 2) {
            if ("-e".equals(args[0]) || "-d".equals(args[0])) paramcheck = true;
            else paramcheck = false;
        } else paramcheck = false;
        if (!paramcheck) {
            System.out.println("encrypt usage: -e [filepath] [password] ");
            System.out.println("decrypt usage: -d [filepath] [password] ");
            return;
        }
        EncryptService encryptService = new EncryptService();
        //encryptService.setEncryptor(new XOREncryptor());
        String filepath = args[1];
        String password = DEFAULTPASSWORD;
        if (args.length >= 3) password = args[2];
        int mode = 0;
        if ("-e".equals(args[0])) {
            mode = EncryptService.ENCRYPTMODE;
        } else if ("-d".equals(args[0])) {
            mode = EncryptService.DECRYPTMODE;
        }
        long st = System.currentTimeMillis();
        encryptService.processFolderFile(filepath, password, mode, true);

        long dis = System.currentTimeMillis() - st;
        System.out.println(dis);
    }
}
