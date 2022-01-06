package com.twx;

import com.twx.client.M3U8Downloader;
import org.junit.Test;

public class M3U8DownloaderTest {

    @Test
    public void multiTask() {
        //language=JSON
        String json = "[\n" +
                "    {\n" +
                "        \"url\": \"https://cdn-3.haku99.com/hls/2019/01/31/3jyHnnTX/playlist.m3u8\",\n" +
                "        \"dir\": \"/Users/twx/m3du\",\n" +
                "        \"name\": \"今日子的备忘录1.mp4\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"url\": \"https://cdn-3.haku99.com/hls/2019/01/31/XKl1IWb1/playlist.m3u8\",\n" +
                "      \"dir\": \"/Users/twx/m3du\",\n" +
                "      \"name\": \"今日子的备忘录10.mp4\"\n" +
                "    }\n" +
                "]";
        //language=JSON
       /* String json1 = "[{\n" +
                "  \"url\": \"https://cn3.7639616.com/hls/20191029/7a4c88366a8ac381e5064e85973f10ae/1572307071/index.m3u8\",\n" +
                "  \"dir\": \"/Users/twx/m3du\",\n" +
                "  \"name\": \"今日子的备忘录01.mp4\"\n" +
                "}]";*/
        M3U8Downloader.multiTask(json);
    }



    @Test
    public void singleTask() {
        M3U8Downloader downloader = new M3U8Downloader(
                "https://europe.olemovienews.com/hlstimeofffmp4/20210825/HdourruI/mp4/HdourruI.mp4/index-v1-a1.m3u8",
                "/Users/twx/m3du",
                "测谎人.mp4");
        downloader.enableM4s("https://europe.olemovienews.com/hlstimeofffmp4/20210825/HdourruI/mp4/HdourruI.mp4/init-v1-a1.mp4");
        downloader.start();
//        downloader.unionTmp2Ts("/Users/twx/m3du/test.m4s");
    }
}
