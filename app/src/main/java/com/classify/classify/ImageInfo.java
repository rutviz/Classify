package com.classify.classify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ImageInfo extends AppCompatActivity {

    String name,path,date,size,category;
    TextView names,paths,dates,sizes,categorys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);

        name = getIntent().getExtras().getString("name");
        size = getIntent().getExtras().getString("size");
        date = getIntent().getExtras().getString("date");
        path = getIntent().getExtras().getString("path");
        category = getIntent().getExtras().getString("category");
        names = (TextView)findViewById(R.id.name);
        dates = (TextView)findViewById(R.id.date);
        paths = (TextView)findViewById(R.id.path);
        sizes = (TextView)findViewById(R.id.size);
        categorys = (TextView)findViewById(R.id.category);
        names.setText(name);
        paths.setText(path);
        dates.setText(date);
        sizes.setText(size + " KB");
        categorys.setText(category);
    }
}
