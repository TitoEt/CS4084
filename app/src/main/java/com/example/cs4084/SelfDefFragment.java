package com.example.cs4084;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SelfDefFragment extends Fragment {
    RecyclerView recyclerView;
    TextView selfDefTitle;
    TextView selfDefWarning;
    ArrayList<Video> youtubeVideos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfdef,container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        youtubeVideos.add(new Video("https://s3.amazonaws.com/gd-images.gooddata.com/customtext/magic.html?imgurl=https%3A%2F%2Fcdn.discordapp.com%2Fattachments%2F347483038957043743%2F965314004861984768%2Fsecurus_logo-TRANS-Colour_outline.png&ratio=true",
                "Self Defense Precaution",
                "Here are some readily available self defense tutorials found on YouTube that you might find helpful." +
                        "\n\nThese videos are categorised, outlined and timestamped for your own convenience." +
                        "\n\nPlease exercise precaution and stay safe while practicing these procedures. Any injuries caused will be at your own discretion."));

        youtubeVideos.add(new Video("https://www.youtube.com/embed/wo0CHBZB0DM?start=122", "Hair Grabs",
                "This will briefly go over simple moves that you can make in various positions." +
                        "\n\n2:02 Front Hair Grab\n2:45 Side Hair Grab\n3:35 Rear Hair Grab"));

        youtubeVideos.add(new Video("https://www.youtube.com/embed/jAh0cU1J5zk",
                "Wrists Both Pinned Under an Attacker",
                "This video thoroughly explains the procedures to take when both of your wrists are pinned down by an attacker who is mounted on top of you."));

        youtubeVideos.add(new Video("https://www.youtube.com/embed/GZ9nC5ZTQ90?start=111",
                "Front Chokes",
                "This video covers ways to recover very from threatening positions in standing choke holds." +
                        "\n\n1:51 Single Hand Front Choke\n2:27 Double Hand Front Choke\n3:10 Double Hand Front Choke Against a Wall"));

        VideoAdapter videoAdapter = new VideoAdapter(youtubeVideos);
        recyclerView.setAdapter(videoAdapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}