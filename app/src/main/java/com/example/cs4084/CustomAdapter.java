package com.example.cs4084;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter{

    ArrayList defensemoves;
    Context context;
    View v;

    public CustomAdapter(ArrayList defensemoves, Context context){
        this.defensemoves = defensemoves;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_selfdef, parent, false);
        ViewHolder viewHolder= new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ViewHolder myviewHolder=(ViewHolder)holder;
        myviewHolder.defensemoves.setText((String)defensemoves.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), (Integer) defensemoves.get(position), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return defensemoves.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView defensemoves;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
