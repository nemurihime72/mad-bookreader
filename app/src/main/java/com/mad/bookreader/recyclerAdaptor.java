package com.mad.bookreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
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
        String s = data.get(position).getTitle();
        holder.txt.setText(s);
        holder.img.setImageBitmap(data.get(position).getImage());
        final String p = data.get(position).getPdfName();
        final String uri = data.get(position).getPdfUri();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),bookreadActivity.class);
                intent.putExtra("PdfUri", uri);
                Log.v(TAG,"PDF put inside intent, going to the book read activity now");
                v.getContext().startActivity(intent);
            }
        });
        holder.imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(v.getContext(),holder.imgbutton);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteBook:
                                Log.v(TAG,"Delete book");
                                AlertDialog.Builder delalert=new AlertDialog.Builder(v.getContext());
                                delalert.setMessage("Would you like to delete this book?");
                                delalert.setCancelable(true);
                                delalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"Deleting book");
                                        data.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(),data.size());
                                    }
                                });
                                delalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"User choose not to delete book");
                                    }
                                });
                                AlertDialog alert= delalert.create();
                                alert.setTitle("Delete book");
                                alert.show();


                            case R.id.editBook:
                                Log.v(TAG,"Editting book");
                                AlertDialog.Builder editalert=new AlertDialog.Builder(v.getContext());
                                View editView=LayoutInflater.from(v.getContext()).inflate(R.layout.dialogue,null);
                                final EditText titleEdit=editView.findViewById(R.id.fileTitle);
                                editalert.setView(editView).setTitle("Edit title of book").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newtitle=titleEdit.getText().toString();
                                        data.get(position).setTitle(newtitle);
                                        holder.txt.setText(newtitle);
                                        Log.v(TAG,"New title set");
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"Edit cancelled");
                                    }
                                });
                                AlertDialog alert2=editalert.create();
                                alert2.show();
                                return true;

                        }


                        return false;
                    }
                });
            }
        });
    }


    public int getItemCount(){
        return data.size();
    }
}
