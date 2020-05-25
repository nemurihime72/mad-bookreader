package com.mad.bookreader;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class recyclerAdaptor extends RecyclerView.Adapter<recyclerViewHolder> {

    final static String TAG="Adapter";

    private List<importedBooks> data;

    public recyclerAdaptor(List<importedBooks> input){
        data=input;
    }
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlayout,parent,false);
        Log.v(TAG,"View inflated with recyclerlayout set as the layout");
        return new recyclerViewHolder(item);
    }
    public void onBindViewHolder(final recyclerViewHolder holder, final int position){
        String s=data.get(position).getTitle();
        holder.txt.setText(s);
        holder.img.setImageResource(data.get(position).getImage());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),bookreadActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    public int getItemCount(){
        return data.size();
    }
}
