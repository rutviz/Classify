package com.classify.classify;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.classify.classify.Global_Share.CurrentCategory;

public class Photo_Viewer extends AppCompatActivity{

    int width,height;
     public ViewPager viewPager;

      Button btnDelete,btnShare;
     ImageView back;
    static TextView category_title;
    DatabaseHandler db;
    static int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer);
//        setDragEdge(DragEdge.BOTTOM);
        db = new DatabaseHandler(Photo_Viewer.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        category_title = (TextView)findViewById(R.id.category_title);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnShare = (Button) findViewById(R.id.btnShare);
        back = (ImageView) findViewById(R.id.btnBack);

//        category_title = (TextView) findViewById(R.id.category_title);

//        imageView = (ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        int id = Integer.parseInt(bundle.getString("id"));
//        String category = bundle.getString("category");
//        category_title.setText(category);
//        Glide.with(this).load(path).into(imageView);

        LayoutParams params = btnShare.getLayoutParams();
        params.width = width/2;
        btnShare.setLayoutParams(params);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new Image_View_Adapter(this,Global_Share.paths_of_image));
        viewPager.setCurrentItem(id);


        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo_Viewer.this.finish();
            }
        });
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mediaUri = Uri.parse(Global_Share.paths_of_image.get(viewPager.getCurrentItem()));
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared via Classify");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Shared via Classify ");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,mediaUri);
                Photo_Viewer.this.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global_Share.TAG,"pager "+viewPager.getCurrentItem());
                int position = viewPager.getCurrentItem();

                String deletePath = Global_Share.paths_of_image.get(position);
                ContentResolver contentResolver = Photo_Viewer.this.getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ deletePath });
                db.deleteimagepath(deletePath);
                Global_Share.paths_of_image.remove(deletePath);
                viewPager.setAdapter(new Image_View_Adapter(Photo_Viewer.this,Global_Share.paths_of_image));
                if(Global_Share.paths_of_image.size() == 0)
                    Photo_Viewer.this.finish();
                if(position==Global_Share.paths_of_image.size())
                    viewPager.setCurrentItem(position-1);
                else
                    viewPager.setCurrentItem(position);
            }
        });

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(CurrentCategory.equals("All"))
                {
                    Photo_Viewer.category_title.setText(db.getSingleCategory(Global_Share.paths_of_image.get(position)));
                }
                else
                    Photo_Viewer.category_title.setText(CurrentCategory+"");
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
