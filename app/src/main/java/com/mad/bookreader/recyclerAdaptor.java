package com.mad.bookreader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class recyclerAdaptor extends RecyclerView.Adapter<recyclerViewHolder> {

    private List<importedBooks> data;

    public recyclerAdaptor(List<importedBooks> input){
        data=input;
    }
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlayout,parent,false);
        return new recyclerViewHolder(item);
    }
    public void onBindViewHolder(recyclerViewHolder holder,int position){
        String s=data.get(position).getTitle();
        holder.txt.setText(s);
        holder.img.setImageResource(data.get(position).getImage());
    }

    public int getItemCount(){
        return data.size();
    }
}
