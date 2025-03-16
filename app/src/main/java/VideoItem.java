package com.example.lyricsplayer;

public class VideoItem {
    private String bvid;
    private String title;
    private String author;
    private String coverUrl;
    
    public VideoItem(String bvid, String title, String author, String coverUrl) {
        this.bvid = bvid;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }
    
    // Getters
    public String getBvid() {
        return bvid;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getCoverUrl() {
        return coverUrl;
    }
}
