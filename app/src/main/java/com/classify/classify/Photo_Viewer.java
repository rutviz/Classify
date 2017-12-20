package com.classify.classify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout.DragEdge;

public class Photo_Viewer extends SwipeBackActivity {

    int width,height;
    ImageView imageView;
    TextView category_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer);
        setDragEdge(DragEdge.BOTTOM);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        category_title = (TextView) findViewById(R.id.category_title);

        imageView = (ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path_of_image");
        String category = bundle.getString("category");
        category_title.setText(category);
        Glide.with(this).load(path).into(imageView);
    }
}
