package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String TAG="Main Activity";

    List<importedBooks> listBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*To add my own toolbar instead of the default*/
        Toolbar toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        //Create list and populate( populate To be removed)
        listBooks=new ArrayList<>();
        for (int i=0;i<50;i++){
            importedBooks b1=new importedBooks("Manga",R.drawable.isla,"testpdf.pdf");
            importedBooks b2=new importedBooks("Kendo",R.drawable.isla2,"kendo.pdf");
            listBooks.add(b1);
            listBooks.add(b2);
        }


        //Call the recyclerView function
        recyclerFunction(listBooks);


    }

    private void recyclerFunction(List<importedBooks>bList){
        RecyclerView rview=(RecyclerView)findViewById(R.id.bookCycler);
        recyclerAdaptor rAdaptor=new recyclerAdaptor(bList);
        GridLayoutManager gLayoutManager=new GridLayoutManager(this,3);
        rview.setLayoutManager(gLayoutManager);
        rview.setAdapter(rAdaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData();
        }
    }
}
