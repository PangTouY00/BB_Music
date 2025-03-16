package com.example.lyricsplayer;

import android.os.Handler;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricSynchronizer {
    private List<LyricLine> lyricLines;
    private TextView lyricView;
    private Handler handler;
    private Runnable updateRunnable;
    private boolean isRunning = false;
    private int currentLineIndex = 0;
    
    // 歌词行类
    private static class LyricLine {
        long timeMs;
        String text;
        
        LyricLine(long timeMs, String text) {
            this.timeMs = timeMs;
            this.text = text;
        }
    }
    
    public LyricSynchronizer(String lyricsText, TextView lyricView) {
        this.lyricView = lyricView;
        this.lyricLines = parseLyrics(lyricsText);
        this.handler = new Handler();
        
        this.updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning && currentLineIndex < lyricLines.size() - 1) {
                    // 显示当前行
                    lyricView.setText(lyricLines.get(currentLineIndex).text);
                    
                    // 计算下一行的延迟
                    long currentTime = lyricLines.get(currentLineIndex).timeMs;
                    long nextTime = lyricLines.get(currentLineIndex + 1).timeMs;
                    long delay = nextTime - currentTime;
                    
                    // 增加行索引并安排下一次更新
                    currentLineIndex++;
                    handler.postDelayed(this, delay);
                }
            }
        };
    }
    
    // 解析LRC格式歌词
    private List<LyricLine> parseLyrics(String lyricsText) {
        List<LyricLine> lines = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+):(\\d+)\\.(\\d+)\\](.*)");
        
        String[] lrcLines = lyricsText.split("\n");
        for (String line : lrcLines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int milliseconds = Integer.parseInt(matcher.group(3));
                String text = matcher.group(4).trim();
                
                long timeMs = minutes * 60000 + seconds * 1000 + milliseconds * 10;
                lines.add(new LyricLine(timeMs, text));
            }
        }
        
        return lines;
    }
    
    // 开始同步
    public void start() {
        if (!isRunning && !lyricLines.isEmpty()) {
            isRunning = true;
            currentLineIndex = 0;
            handler.post(updateRunnable);
        }
    }
    
    // 暂停同步
    public void pause() {
        isRunning = false;
        handler.removeCallbacks(updateRunnable);
    }
    
    // 恢复同步
    public void resume() {
        if (!isRunning && currentLineIndex < lyricLines.size()) {
            isRunning = true;
            handler.post(updateRunnable);
        }
    }
    
    // 停止同步
    public void stop() {
        isRunning = false;
        currentLineIndex = 0;
        handler.removeCallbacks(updateRunnable);
    }
}
