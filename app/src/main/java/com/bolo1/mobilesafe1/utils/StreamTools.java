package com.bolo1.mobilesafe1.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 菠萝 on 2017/7/14.
 */

public class StreamTools {
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len=in.read(buffer))!=-1){
            baos.write(buffer,0,len);
        }
        in.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
