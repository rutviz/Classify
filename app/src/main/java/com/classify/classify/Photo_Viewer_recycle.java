package com.classify.classify;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Photo_Viewer_recycle extends AppCompatActivity{

    int width,height;
    static public ViewPager viewPager;
    private List<String> _imagePaths = new ArrayList<>();
    Button btnDelete;
    ImageView back;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer_recycle);
//        setDragEdge(DragEdge.BOTTOM);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        btnDelete = (Button)findViewById(R.id.btnDelete_recycle);
        back = (ImageView)findViewById(R.id.btnBack_recycle);
        db = new DatabaseHandler(Photo_Viewer_recycle.this);
//        category_title = (TextView) findViewById(R.id.category_title);

//        imageView = (ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        int id = Integer.parseInt(bundle.getString("id"));
//        String category = bundle.getString("category");
//        category_title.setText(category);
//        Glide.with(this).load(path).into(imageView);
        _imagePaths = Global_Share.paths_of_image;
        viewPager = (ViewPager) findViewById(R.id.pager_recycle);
        viewPager.setAdapter(new Image_view_Recycle(this,Global_Share.paths_of_image));
        viewPager.setCurrentItem(id);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo_Viewer_recycle.this.finish();
            }
        });

        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewPager.getCurrentItem();
                String deletePath = _imagePaths.get(position);
                Uri uri= Uri.parse("file://"+deletePath);
                File delete = new File(uri.getPath());
                delete.delete();
                db.deleteimagepathfromrecycle(deletePath);
                Global_Share.paths_of_image.remove(deletePath);
                Photo_Viewer_recycle.viewPager.setAdapter(new Image_view_Recycle(Photo_Viewer_recycle.this,Global_Share.paths_of_image));
                if(Global_Share.paths_of_image.size() == 0)
                    Photo_Viewer_recycle.this.finish();
                if(position==Global_Share.paths_of_image.size())
                    Photo_Viewer_recycle.viewPager.setCurrentItem(position-1);
                else
                    Photo_Viewer_recycle.viewPager.setCurrentItem(position);
            }
        });
    }
}
