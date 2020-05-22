package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class bookreadActivity extends AppCompatActivity {

    private TextView testTitle;
    private ImageView testImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookreadlayout);

        testTitle=findViewById(R.id.textTest);
        testImage=findViewById(R.id.testImage);

        Intent intent=getIntent();
        String titleBook=intent.getExtras().getString("Title");
        int imageBook=intent.getExtras().getInt("Image");

        testTitle.setText(titleBook);
        testImage.setImageResource(imageBook);
    }
}
