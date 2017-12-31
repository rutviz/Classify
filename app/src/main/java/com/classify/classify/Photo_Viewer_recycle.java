package com.classify.classify;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class Photo_Viewer_recycle extends AppCompatActivity{

    int width,height;
    ImageView back;
    TextView category_title;
    static public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer_recycle);
//        setDragEdge(DragEdge.BOTTOM);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

//        category_title = (TextView) findViewById(R.id.category_title);

//        imageView = (ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        int id = Integer.parseInt(bundle.getString("id"));
//        String category = bundle.getString("category");
//        category_title.setText(category);
//        Glide.with(this).load(path).into(imageView);

        viewPager = (ViewPager) findViewById(R.id.pager_recycle);
        viewPager.setAdapter(new Image_view_Recycle(this,Global_Share.paths_of_image));
        viewPager.setCurrentItem(id);
    }
}
