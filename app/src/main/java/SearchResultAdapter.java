package com.example.lyricsplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context context;
    private List<VideoItem> videoItems;
    private BilibiliApiService apiService;

    public SearchResultAdapter(Context context) {
        this.context = context;
        this.videoItems = new ArrayList<>();
        this.apiService = new BilibiliApiService();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItem item = videoItems.get(position);
        
        holder.titleTextView.setText(item.getTitle());
        holder.authorTextView.setText(item.getAuthor());
        
        // 加载缩略图
        Glide.with(context)
             .load(item.getCoverUrl())
             .placeholder(R.drawable.placeholder)
             .into(holder.thumbnailImageView);
        
        // 设置点击事件
        holder.cardView.setOnClickListener(v -> {
            // 显示加载进度
            showProcessingDialog();
            
            // 提取音频
            apiService.extractAudioFromVideo(item.getBvid(), new ApiCallback<String>() {
                @Override
                public void onSuccess(String audioPath) {
                    // 获取歌词
                    apiService.getLyrics(item.getTitle(), new ApiCallback<String>() {
                        @Override
                        public void onSuccess(String lyrics) {
                            dismissProcessingDialog();
                            
                            // 启动播放器活动
                            Intent intent = new Intent(context, LyricPlayerActivity.class);
                            intent.putExtra("AUDIO_PATH", audioPath);
                            intent.putExtra("LYRICS", lyrics);
                            intent.putExtra("TITLE", item.getTitle());
                            context.startActivity(intent);
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            dismissProcessingDialog();
                            showError("获取歌词失败: " + errorMessage);
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    dismissProcessingDialog();
                    showError("提取音频失败: " + errorMessage);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }
    
    public void updateData(List<VideoItem> items) {
        this.videoItems.clear();
        this.videoItems.addAll(items);
        notifyDataSetChanged();
    }
    
    // ViewHolder类
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView authorTextView;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail_image);
            titleTextView = itemView.findViewById(R.id.title_text);
            authorTextView = itemView.findViewById(R.id.author_text);
        }
    }
}
