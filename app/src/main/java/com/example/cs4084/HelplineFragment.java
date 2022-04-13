package com.example.cs4084;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HelplineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_helpline,container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = (TextView) view.findViewById(R.id.textView);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text2 = (TextView) view.findViewById(R.id.textView2);
        text2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text3 = (TextView) view.findViewById(R.id.textView3);
        text3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text4 = (TextView) view.findViewById(R.id.textView4);
        text4.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}