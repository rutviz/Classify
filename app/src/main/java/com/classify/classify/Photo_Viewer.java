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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static com.classify.classify.Global_Share.CurrentCategory;

public class Photo_Viewer extends AppCompatActivity{

    int width,height;
     public ViewPager viewPager;
    RelativeLayout UpperLayer,BottomLayer;

      Button btnDelete,btnShare;
     ImageView back;
    static TextView category_title;
    DatabaseHandler db;
    static int index;
    private ImageView hide,unhide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer);
//        setDragEdge(DragEdge.BOTTOM);
        db = new DatabaseHandler(Photo_Viewer.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();


        UpperLayer = (RelativeLayout) findViewById(R.id.toplayer);
        BottomLayer= (RelativeLayout) findViewById(R.id.bottomlayer);
        category_title = (TextView)findViewById(R.id.category_title);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnShare = (Button) findViewById(R.id.btnShare);
        back = (ImageView) findViewById(R.id.btnBack);
        hide = (ImageView) findViewById(R.id.hide);
        unhide = (ImageView) findViewById(R.id.unhide);

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

        hide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UpperLayer.setVisibility(View.INVISIBLE);
                BottomLayer.setVisibility(View.INVISIBLE);
                unhide.setVisibility(View.VISIBLE);
                Log.d(Global_Share.TAG,"touched");
            }
        });
        unhide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                unhide.setVisibility(View.GONE);
                UpperLayer.setVisibility(View.VISIBLE);
                BottomLayer.setVisibility(View.VISIBLE);
                Log.d(Global_Share.TAG,"touched");
            }
        });

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
                recyclerbin(deletePath);
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

    public void recyclerbin(String path){
        InputStream in = null;
        OutputStream out = null;

        Uri mediaUri = Uri.parse("file://"+ path);
        File imagepath =new File(mediaUri.getPath());
        Date date = new Date(imagepath.lastModified());
        String time = String.valueOf(date.getTime());
        String imagename = imagepath.getName().toString();
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();
        Log.d("hey1",timestamp);
        try {

            //create output directory if it doesn't exist
            String outputPath = "/storage/emulated/0/Classifyrecycle/"+imagename+".classify";
            File dir = new File ("/storage/emulated/0/Classifyrecycle");
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(path);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            Log.d("oldpath",path + "main");
            db.recyclebinaddData(path,timestamp,time,outputPath);


            // delete the original file
            //   new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }



    }
}
