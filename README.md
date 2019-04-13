# 使用

1、 安装ffmpeg

```$xslt
MacOS:  brew install ffmpeg

#其他操作系统请参考网络安装
```

2、 构造下载任务

下载多集：
```$xslt
[
     {
         "url": "https://cdn-3.haku99.com/hls/2019/01/31/3jyHnnTX/playlist.m3u8",
         "dir": "/Users/twx/m3du",
         "name": "今日子的备忘录1.mp4"
     },
     {
       "url": "https://cdn-3.haku99.com/hls/2019/01/31/XKl1IWb1/playlist.m3u8",
       "dir": "/Users/twx/m3du",
       "name": "今日子的备忘录10.mp4"
     }
 ]
 
 url:  m3u8下载链接
 dir:  存放的目录
 name: 指定输出文件名
 
 然后调用方法： M3U8Downloader.multiTask(json);
```


下载单集：
```$xslt
M3U8Downloader downloader = new M3U8Downloader(
                "https://cdn-3.haku99.com/hls/2019/01/31/3jyHnnTX/playlist.m3u8",
                "/Users/twx/m3du",
                "今日子的备忘录1.mp4");
downloader.start();
```