package com.twx.utils;

import com.twx.exception.FfmpegTransferException;

import java.io.IOException;

/**
 * ffmpeg转换工具
 */
public class FfmpegUtil {

    public static void ts2mp4(String tsPath,String mp4Path){
        String cmdTemplate = "ffmpeg -i %s -threads 2 -vcodec copy -f mp4 %s";

        String cmd = String.format(cmdTemplate,tsPath,mp4Path);

        System.out.println(cmd);

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            int i = process.waitFor();

            if (i==0){
                System.out.println("转换完毕。。。");
            }else {
                throw new FfmpegTransferException("ffmpeg转换出错...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
