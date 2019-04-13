package com.twx.client;

public class DownloaderParam {

    private String url;
    private String dir;
    private String name;

    public DownloaderParam(String url, String dir, String name) {
        this.url = url;
        this.dir = dir;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
