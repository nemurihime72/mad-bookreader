package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<importedBooks> listBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*To add my own toolbar instead of the default*/
        //Toolbar toolbar=findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);

        importedBooks b1=new importedBooks("Isla",R.drawable.isla);
        listBooks=new ArrayList<>();
        listBooks.add(b1);
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
        return super.onOptionsItemSelected(item);
    }
}
