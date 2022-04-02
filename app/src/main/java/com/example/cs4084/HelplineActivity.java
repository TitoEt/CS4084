package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelplineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text2 = (TextView) findViewById(R.id.textView2);
        text2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text3 = (TextView) findViewById(R.id.textView3);
        text3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text4 = (TextView) findViewById(R.id.textView4);
        text4.setMovementMethod(LinkMovementMethod.getInstance());
    }
}