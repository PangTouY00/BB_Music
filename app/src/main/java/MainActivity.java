package com.example.lyricsplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private EditText searchInput;
    private Button searchButton;
    private RecyclerView resultsView;
    private SearchResultAdapter adapter;
    private BilibiliApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化界面组件
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        resultsView = findViewById(R.id.results_recycler_view);
        
        // 设置RecyclerView
        resultsView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter(this);
        resultsView.setAdapter(adapter);
        
        // 初始化API服务
        apiService = new BilibiliApiService();
        
        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString();
                if (!query.isEmpty()) {
                    searchVideos(query);
                }
            }
        });
    }
    
    // 搜索B站视频
    private void searchVideos(String query) {
        // 显示加载指示器
        showLoading(true);
        
        // 调用API服务搜索
        apiService.searchVideosByKeyword(query, new ApiCallback<List<VideoItem>>() {
            @Override
            public void onSuccess(List<VideoItem> results) {
                runOnUiThread(() -> {
                    adapter.updateData(results);
                    showLoading(false);
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showError(errorMessage);
                    showLoading(false);
                });
            }
        });
    }
}
