package com.mad.bookreader;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

public class recyclerViewHolder extends RecyclerView.ViewHolder {

    TextView txt;
    ImageView img;
    CardView cardView;
    String pdfName;
    ImageButton imgButton;

    public recyclerViewHolder(View itemView){
        super(itemView);
        txt=itemView.findViewById(R.id.nameOfBook);
        img=itemView.findViewById(R.id.bookPic);
        cardView=itemView.findViewById(R.id.cardid);
        imgButton=itemView.findViewById(R.id.editdeletebutton);
    }
}
