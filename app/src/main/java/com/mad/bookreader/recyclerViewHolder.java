package com.mad.bookreader;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class recyclerViewHolder extends RecyclerView.ViewHolder {

    TextView txt;
    ImageView img;

    public recyclerViewHolder(View itemView){
        super(itemView);
        txt=itemView.findViewById(R.id.nameOfBook);
        img=itemView.findViewById(R.id.bookPic);
    }
}
