package com.example.lyricsplayer;

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}
