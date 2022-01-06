package com.twx.client;

import com.alibaba.fastjson.JSONObject;
import com.twx.utils.FfmpegUtil;
import com.twx.utils.MyFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.twx.utils.MyFileUtil.*;

/**
 * 实现思路：<br>
 * 1. 下载m3u8文件，提取出类似于out000.ts内容<br>
 * 2. 对步骤1生成对内容分片操作，即分配多少个线程去下载整个ts文件<br>
 * 3. 每个线程都会生成一个thread+number.tmp文件<br>
 * 4. 当所有线程都下载完成时，合并这些文件到一个（merge.ts)文件中<br>
 * 5. 使用ffmpeg将ts文件转换成mp4文件<br>
 * 6. 清理多线程生成的tmp文件和merge.ts文件<br>
 * <br>
 * 该工具主要的作用是使用多线程分片下载ts文件，最终融合成一个完整的ts文件<br>
 * 比使用命令 ffmpeg -i http://example/index.m3u8 output.mp4 直接下载要快速得多<br>
 * <br>
 * 本工具使用的转换命令见 ffmpeg -i %s -threads 2 -vcodec copy -f mp4 %s
 */
public class M3U8Downloader {
    
    private static final Integer THREAD_TASK = 50;

    private String M3U8_FORMAT="ts";

    private Boolean M4S=false;

    private String initExtUrl;

    private String m3u8_origin_url;

    private String download_dir;

    private String output_mp4_name;

    /**
     *
     * @param m3u8_origin_url m3u8地址
     * @param download_dir  下载目录
     * @param output_mp4_name 保存的mp4文件名
     */
    public M3U8Downloader(String m3u8_origin_url, String download_dir, String output_mp4_name) {
        this.m3u8_origin_url = m3u8_origin_url;
        this.download_dir = download_dir;
        this.output_mp4_name = output_mp4_name;
    }

    public void enableM4s(String initExtUrl) {
        this.initExtUrl=initExtUrl;
        this.M4S=true;
        this.M3U8_FORMAT = "m4s";
    }

    /**
     * 多任务下载,json格式如下：
     * <pre>
     * [
     *      {
     *          "url": "https://cdn-3.haku99.com/hls/2019/01/31/3jyHnnTX/playlist.m3u8",
     *          "dir": "/Users/twx/m3du",
     *          "name": "今日子的备忘录1.mp4"
     *      },
     *      {
     *        "url": "https://cdn-3.haku99.com/hls/2019/01/31/XKl1IWb1/playlist.m3u8",
     *        "dir": "/Users/twx/m3du",
     *        "name": "今日子的备忘录10.mp4"
     *      }
     * ]
     * </pre>
     * @param json 任务列表--json格式
     */
    @Deprecated
    public static void multiTask(String json) {
        List<DownloaderParam> downloaderParamList = JSONObject.parseArray(json, DownloaderParam.class);

        System.out.println(downloaderParamList.size());
        for (DownloaderParam param : downloaderParamList) {
            M3U8Downloader downloader = new M3U8Downloader(param.getUrl(),param.getDir(),param.getName());
            downloader.start();
        }
    }


    /**
     * 单任务下载--构造任务的下载流程
     */
    public void start() {
        List<String> m3u8 = getTsListByUrl(m3u8_origin_url);

        //计算需要多少个线程执行
        int total = m3u8.size();
        int threadNum=0;
        if (total % THREAD_TASK == 0) {
            threadNum = total/THREAD_TASK;
        }else{
            threadNum = total/THREAD_TASK+1;
        }
        CountDownLatch latch = new CountDownLatch(threadNum);

        if (M4S) {
            download(initExtUrl,new File(download_dir+"/"+"init.mp4"),false);
        }

        multiDownload(m3u8,latch);

        //等待下载完成
        try {
            latch.await();
            System.out.println("主线程正在等待下载完成。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //下载完成后合并文件
        String tsName = download_dir+"/merge.mm";
        unionTmp2Ts(tsName);

        //ffmpeg转码
        FfmpegUtil.ts2mp4(tsName,download_dir+"/"+output_mp4_name);

        //清理资源
//        MyFileUtil.deleteSuffix(new File(download_dir),"tmp");
//        MyFileUtil.deleteSuffix(new File(download_dir),"ts");

        MyFileUtil.deleteSuffix(new File(download_dir),M3U8_FORMAT);
        MyFileUtil.deleteSuffix(new File(download_dir),"mm");
        if (M4S) {
            FileUtils.deleteQuietly(new File(download_dir+"/"+"init.mp4"));
        }

        System.out.println("下载完成！！！");

    }


    /**
     * 根据url下载m3u8文件，过滤得到out000.ts等内容
     * @param url
     * @return
     */
    private List<String> getTsListByUrl(String url) {
        List<String> res=null;
        try {
            File tmpFile = File.createTempFile("play",".m3u8");
            System.out.println("临时文件路径： "+tmpFile.getAbsolutePath());

            tmpFile.deleteOnExit();

            //下载m3u8文件到临时文件夹
            download(url,tmpFile,false);
            //过滤得到以ts结尾到行
//            res = FileUtils.readLines(tmpFile, "utf-8")
//                    .stream()
//                    .filter(line -> !line.startsWith("#")&&line.endsWith(".ts"))
//                    .collect(Collectors.toList());
            res = FileUtils.readLines(tmpFile, "utf-8")
                    .stream()
                    .filter(line -> !line.startsWith("#")&&line.endsWith(M3U8_FORMAT))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 多线程任务分配
     * @param m3u8  完整的m3u8文件列表
     * @param latch 计算器
     */
    private void multiDownload(List<String> m3u8,CountDownLatch latch) {

        int total = m3u8.size();

        int threadNum=total/THREAD_TASK;

        int yushu = total%THREAD_TASK;

        for (int i = 0; i < threadNum; i++) {

            final int temp = i;

            new Thread(() ->
            {
                //当前线程下载m3u8文件哪个片段
                List<String> segTs = m3u8.subList(THREAD_TASK*temp,THREAD_TASK*(temp+1)-1);
                download(segTs);

                System.out.println(Thread.currentThread().getName()+" 完成下载。。。");
                latch.countDown();
            }, "thread"+temp
            ).start();
        }

        if (yushu!=0){
            new Thread(
                    () -> {
                        //当前线程下载m3u8文件哪个片段
                        List<String> segTs = m3u8.subList(THREAD_TASK*threadNum,total);
                        download(segTs);

                        System.out.println(Thread.currentThread().getName()+" 完成下载。。。");
                        latch.countDown();
                    },"thread"+(threadNum)
            ).start();
        }
    }


    /**
     * 下载文件列表
     * @param m3u8Ts
     * @throws IOException
     */
    private void download(List<String> m3u8Ts){

        for (String ts:m3u8Ts) {

            //如果当前行是url模式，则取最后到名字
            if (ts.lastIndexOf("/") != -1) {
                ts=ts.substring(ts.lastIndexOf("/")+1);
            }

            String M3U8_BASE_URL = m3u8_origin_url.substring(0,m3u8_origin_url.lastIndexOf("/")+1);
            String targetUrl = M3U8_BASE_URL+ts;

            File saveFile = new File(download_dir + "/" + ts);
            if (!saveFile.exists()){
                System.out.println(Thread.currentThread().getName()+"->>正在下载："+ts);
                download(targetUrl,saveFile,false);
            }
        }
    }

    /**
     * 通过HttpClient将资源下载到文件中
     * @param url
     * @param target
     * @param append
     */
    private void download(String url,File target,boolean append) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response=null;
        try {
            response = httpclient.execute(httpGet);
            InputStream content = response.getEntity().getContent();

            if (append){
                appendInputstreamToFile(content,target);
            }else {
                FileUtils.copyInputStreamToFile(content,target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 将多线程下载下来的零碎文件合并成一个完成的文件
     * @param targetTs
     * @throws IOException
     */
    public void unionTmp2Ts(String targetTs){

        File dir = new File(download_dir);
        File[] files = dir.listFiles();

        //通过线程名称排序
        List<File> sortedFiles = MyFileUtil.sortFileByName(Arrays.asList(files), "asc");
        System.out.println("排好序的文件=>"+sortedFiles);
        System.out.println("排好序的文件个数=>"+sortedFiles.size());
        //过滤得到.tmp结尾的文件列表
        Vector<InputStream> vector = new Vector<>();

        try {
            for (File file : sortedFiles) {
                String extension = FilenameUtils.getExtension(file.getName());
                if (M3U8_FORMAT.equalsIgnoreCase(extension)) {
                    vector.add(new FileInputStream(file));
                }
                if (M4S) {
                    if ("mp4".equals(extension)) {
                        vector.add(new FileInputStream(file));
                    }
                }
            }
            System.out.println("vector 大小=>"+vector.size());
            unioFile(vector,targetTs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
