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
        M3U8Downloader.multiTask(json);
    }



    @Test
    public void singleTask() {
        M3U8Downloader downloader = new M3U8Downloader(
                "https://cdn-3.haku99.com/hls/2019/01/31/3jyHnnTX/playlist.m3u8",
                "/Users/twx/m3du",
                "今日子的备忘录1.mp4");
        downloader.start();
    }
}
