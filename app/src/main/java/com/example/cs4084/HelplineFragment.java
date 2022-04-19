package com.example.cs4084;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HelplineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_helpline,container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView womensAid = view.findViewById(R.id.womensAidLink);
        womensAid.setMovementMethod(LinkMovementMethod.getInstance());

        TextView dvna = view.findViewById(R.id.dvnaLink);
        dvna.setMovementMethod(LinkMovementMethod.getInstance());

        TextView aoibhneas = view.findViewById(R.id.aoibhneasLink);
        aoibhneas.setMovementMethod(LinkMovementMethod.getInstance());

        TextView wng = view.findViewById(R.id.wngLink);
        wng.setMovementMethod(LinkMovementMethod.getInstance());

        TextView safeIE = view.findViewById(R.id.safeIELink);
        safeIE.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}