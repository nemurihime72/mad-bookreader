package com.mad.bookreader;

import android.content.Context;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.epub.Main;

public class recyclerAdaptor extends RecyclerView.Adapter<recyclerViewHolder> implements Filterable {

    final static String TAG="Adapter";

    private List<importedBooks> data;
    private List <importedBooks> datalist2;

    private Context context;

    public recyclerAdaptor(List<importedBooks> input){
        data=input;
        datalist2=new ArrayList<>(data);
    }
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context=parent.getContext();
        View item= LayoutInflater.from(context).inflate(R.layout.recyclerlayout,parent,false);
        Log.v(TAG,"View inflated with recyclerlayout set as the layout");
        return new recyclerViewHolder(item);
    }
    public void onBindViewHolder(final recyclerViewHolder holder, final int position){
        Log.v(TAG, "Setting up holder");
        final String title = data.get(position).getTitle();
        final int id=data.get(position).getId();
        holder.txt.setText(title);
        //holder.txt.setBackgroundColor(Color.parseColor("#000000"));
        BookDBHandler dbHandler = new BookDBHandler(context, null, null, 1);
        final int lastread=dbHandler.lastPage(id);
        final int swipedirection=dbHandler.pageSwipe(id);
        final int chapter = dbHandler.lastChapter(id);
        final float progress = dbHandler.lastProgress(id);
        if (lastread != 0 || chapter != 0 || progress != 0){
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            holder.imgButton.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        }
        else{
            holder.cardView.setBackgroundResource(R.color.titlebg);
            holder.imgButton.setBackgroundResource(R.color.titlebg);
        }
        holder.img.setImageBitmap(data.get(position).getImage());
        final String uri = data.get(position).getBookUri();
        final String fileType = data.get(position).getFileType();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "filetype is " + fileType);
                if (fileType.equals("pdf")) {
                    Intent intent=new Intent(v.getContext(),bookreadActivity.class);
                    intent.putExtra("id",String.valueOf(id));
                    intent.putExtra("PdfUri", uri);
                    intent.putExtra("PdfName", title);
                    Log.v(TAG,"PDF put inside intent, going to the book read activity now");
                    v.getContext().startActivity(intent);
                }
                else if (fileType.equals("epub")) {
                    Intent intent = new Intent(v.getContext(), epubReaderActivity.class);
                    intent.putExtra("id",String.valueOf(id));
                    intent.putExtra("Bookpath", uri);
                    intent.putExtra("BookName", title);
                    Log.v(TAG, "Epub put inside intent, going to epub read activity now");
                    v.getContext().startActivity(intent);
                }
                else if(fileType.equals("online")){
                    Intent intent=new Intent(v.getContext(),onlinereadActivity.class);
                    intent.putExtra("id",String.valueOf(id));
                    intent.putExtra("urllink",uri);
                    intent.putExtra("lastread",String.valueOf(lastread));
                    intent.putExtra("swipe",String.valueOf(swipedirection));
                    v.getContext().startActivity(intent);
                }

                else {
                    Toast.makeText(context, "Failed to load file!", Toast.LENGTH_SHORT).show();
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
                                //delete book from db and from recyclerview
                                Log.v(TAG,"Delete book");
                                AlertDialog.Builder delAlert=new AlertDialog.Builder(v.getContext());
                                delAlert.setMessage("Would you like to delete this book?");
                                delAlert.setCancelable(true);
                                delAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "Deleting book: " + title + "...", Toast.LENGTH_SHORT).show();
                                        Log.v(TAG,"Deleting book");
                                        db.deleteBook(id);
                                        data.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(),data.size());
                                        datalist2=new ArrayList<>(data);
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
                                //edit book title
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
                                        db.editTitleBook(id,newTitle);
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


    public int getItemCount() {
        return data.size();
    }
    public Filter getFilter(){
        return exampleFilter;
    }
    private Filter exampleFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<importedBooks> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(datalist2);
            } else {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for (importedBooks books : datalist2) {
                    if (books.getTitle().toLowerCase().contains(filterpattern)) {
                        filteredList.add(books);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
