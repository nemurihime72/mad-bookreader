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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
        Log.v(TAG, "Setting up holder");
        final String s = data.get(position).getTitle();
        holder.txt.setText(s);
        holder.img.setImageBitmap(data.get(position).getImage());
        final String p = data.get(position).getTitle();
        Log.v(TAG, "string p = " + p);
        final String uri = data.get(position).getPdfUri();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = Uri.parse(uri);
                File file = new File(fileUri.getPath());
                String[] split = file.getPath().split(":");
                String filePath = split[1];
                Log.v(TAG, filePath);
                File pdfFile = new File(filePath);
                if (pdfFile.exists()) {
                    Intent intent=new Intent(v.getContext(),bookreadActivity.class);
                    intent.putExtra("PdfUri", uri);
                    intent.putExtra("PdfName", p);
                    Log.v(TAG,"PDF put inside intent, going to the book read activity now");
                    v.getContext().startActivity(intent);
                }
                else {
                    BookDBHandler db = new BookDBHandler(v.getContext(), null, null, 1);
                    Toast.makeText(v.getContext(), "File not found, deleting book from database", Toast.LENGTH_SHORT).show();
                    db.deleteBook(p);
                    data.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(),data.size());
                }
            }
        });
        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final BookDBHandler db = new BookDBHandler(v.getContext(),null, null, 1);
                PopupMenu popup = new PopupMenu(v.getContext(), holder.imgButton);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteBook:
                                Log.v(TAG,"Delete book");
                                AlertDialog.Builder delAlert=new AlertDialog.Builder(v.getContext());
                                delAlert.setMessage("Would you like to delete this book?");
                                delAlert.setCancelable(true);
                                delAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"Deleting book");
                                        db.deleteBook(s);
                                        data.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(),data.size());
                                    }
                                });
                                delAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"User choose not to delete book");
                                    }
                                });
                                AlertDialog alert = delAlert.create();
                                alert.setTitle("Delete book");
                                alert.show();
                                return true;


                            case R.id.editBook:
                                Log.v(TAG,"Editing book");
                                AlertDialog.Builder editAlert = new AlertDialog.Builder(v.getContext());
                                View editView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialogue,null);
                                final EditText titleEdit=editView.findViewById(R.id.fileTitle);
                                editAlert.setView(editView).setTitle("Edit title of book").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String oldTitle=data.get(position).getTitle();
                                        String newTitle=titleEdit.getText().toString();
                                        data.get(position).setTitle(newTitle);
                                        holder.txt.setText(newTitle);
                                        db.editTitleBook(oldTitle,newTitle);
                                        Log.v(TAG,"New title set");
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.v(TAG,"Edit cancelled");
                                    }
                                });
                                AlertDialog alert2=editAlert.create();
                                alert2.show();
                                //return true;
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
