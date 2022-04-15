package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class SelfDefActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> defensemoves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfdef);
        recyclerView = findViewById(R.id.recVi);
        defensemoves = new ArrayList<>();
        defensemoves.add("Disarm");
        defensemoves.add("Go for vulnerable spots");
        defensemoves.add("How to punch safely");
        defensemoves.add("Escape bindings");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        CustomAdapter customAdapter=new CustomAdapter((ArrayList<String>) defensemoves, SelfDefActivity.this);
        recyclerView.setAdapter(customAdapter);

        VideoView videoView = findViewById(R.id.video_view);
        String videoPath = "android.resource://com.example.cs4084/" + getPackageName() + "/" + R.raw.selfdef;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

    }
}