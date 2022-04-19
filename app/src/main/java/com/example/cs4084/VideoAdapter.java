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
    ArrayList<Video> youtubeVideoList; //List holding all the videos we want to show up on Self Def Fragment

    public VideoAdapter(ArrayList<Video> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selfdef_video, parent, false);
        return new VideoViewHolder(view);
    }

//    Called by RecyclerView to display the data at a specific position
    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video current = youtubeVideoList.get(position);

        // Load url link into web view
        holder.videoWeb.loadUrl(current.getUrl());
        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());
    }

    @Override
    public int getItemCount() {
        return youtubeVideoList.size();

    }

//    Represents an item in the RecyclerView, stores all the data and binds it to the view contents
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
        }
    }

}