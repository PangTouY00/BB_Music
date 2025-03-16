package com.example.lyricsplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LyricPlayerActivity extends AppCompatActivity {
    private TextView lyricTextView;
    private Button playButton, pauseButton;
    private String audioPath;
    private String lyrics;
    private AudioPlayerService playerService;
    private boolean bound = false;
    private LyricSynchronizer lyricSync;
    
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.PlayerBinder binder = (AudioPlayerService.PlayerBinder) service;
            playerService = binder.getService();
            bound = true;
            
            // 初始化完成后自动播放
            playerService.play(audioPath);
            lyricSync.start();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_player);
        
        // 获取传递过来的数据
        audioPath = getIntent().getStringExtra("AUDIO_PATH");
        lyrics = getIntent().getStringExtra("LYRICS");
        
        // 初始化UI组件
        lyricTextView = findViewById(R.id.lyric_text_view);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        
        // 创建歌词同步器
        lyricSync = new LyricSynchronizer(lyrics, lyricTextView);
        
        // 绑定服务
        Intent intent = new Intent(this, AudioPlayerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        
        // 设置按钮点击事件
        playButton.setOnClickListener(v -> {
            if (bound && playerService != null) {
                playerService.resume();
                lyricSync.resume();
            }
        });
        
        pauseButton.setOnClickListener(v -> {
            if (bound && playerService != null) {
                playerService.pause();
                lyricSync.pause();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            playerService.stop();
            unbindService(connection);
            bound = false;
        }
        if (lyricSync != null) {
            lyricSync.stop();
        }
    }
}
