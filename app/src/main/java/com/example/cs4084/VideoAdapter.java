package com.example.cs4084;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{
    ArrayList<Video> youtubeVideoList;

    public VideoAdapter(ArrayList<Video> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selfdef_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video current = youtubeVideoList.get(position);

        // Load frame of video in web view.
//        holder.videoWeb.loadData(current.getFrame(), "text/html", "utf-8");
        holder.videoWeb.loadUrl(current.getUrl());
        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());
    }

    @Override
    public int getItemCount() {
        return youtubeVideoList.size();

    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView videoWeb;
        TextView title;
        TextView description;
        public VideoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            description = itemView.findViewById(R.id.videoDescription);
            videoWeb = itemView.findViewById(R.id.videoWebView);
            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient());
            videoWeb.clearCache(true);
        }
    }

}