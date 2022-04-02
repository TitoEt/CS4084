package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Scroller;
import android.widget.TextView;

public class HelplineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text1 = (TextView) findViewById(R.id.textView2);
        text1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text2 = (TextView) findViewById(R.id.textView3);
        text2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text3 = (TextView) findViewById(R.id.textView4);
        text3.setMovementMethod(LinkMovementMethod.getInstance());


    }
}