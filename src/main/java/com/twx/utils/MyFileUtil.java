package com.twx.utils;

import java.io.*;
import java.util.*;

public class MyFileUtil {


    /**
     * 将输入流追加到文件中
     * @param ins
     * @param file
     */
    public static void appendInputstreamToFile(InputStream ins, File file) {

        try (OutputStream os = new FileOutputStream(file, true)){
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = ins.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 合并多个输入流
     * @param vector
     * @param desFilePath
     * @throws IOException
     */
    public static void unioFile(Vector<InputStream> vector, String desFilePath) throws IOException{
        SequenceInputStream sis = new SequenceInputStream(vector.elements());
//
        OutputStream os = new FileOutputStream(desFilePath,true);

        byte[] buf = new byte[1024];
        int len;
        while((len=sis.read(buf))!=-1)
        {
            os.write(buf,0,len);
        }
        os.close();
        //sis会将inputstream依次关闭
        sis.close();
    }

    /**
     * 删除后缀suffix的文件
     * @param f
     * @param suffix
     */
    public static void deleteSuffix(File f,String suffix) {
        File[] fi=f.listFiles();
        for (File file : fi) {
            if(file.isDirectory()){
                deleteSuffix(file,suffix);
            }else if(file.getName().substring(file.getName().lastIndexOf(".")+1).equals(suffix)){
                System.out.println("成功删除"+file.getName());
                file.delete();
            }
        }
    }

    /**
     *  对文件进行排序
     * @param files
     * @param orderStr 排序:asc,des,不区分大小写
     * @return
     */
    public static List<File> sortFileByName(List<File> files, final String orderStr) {
        if (!orderStr.equalsIgnoreCase("asc") && orderStr.equalsIgnoreCase("desc")) {
            return files;
        }
        File[] files1 = files.toArray(new File[0]);
        Arrays.sort(files1, new Comparator<File>() {
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                if(orderStr == null || orderStr.length() < 1 || orderStr.equalsIgnoreCase("asc")) {
                    return n1 - n2;
                } else {
                    //降序
                    return n2 - n1;
                }
            }
        });
        return new ArrayList<File>(Arrays.asList(files1));
    }

    private static int extractNumber(String name) {
        int i;
        try {
            String number = name.replaceAll("[^\\d]", "");
            i = Integer.parseInt(number);
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }
}
