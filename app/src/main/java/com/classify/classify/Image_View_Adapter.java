package com.classify.classify;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.classify.classify.Global_Share.CurrentCategory;

/**
 * Created by Rutviz Vyas on 19-12-2017.
 */

public class Image_View_Adapter extends PagerAdapter {


    DatabaseHandler db;
    private Activity _activity;
    private List<String> _imagePaths = new ArrayList<>();
    private LayoutInflater inflater;

    public Image_View_Adapter(Activity activity, List<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageViewTouch imgDisplay;
        Button btnDelete,btnShare;
        ImageView back;
        TextView category_title;

        db = new DatabaseHandler(_activity);

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_view_screen, container,
                false);

        imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.imgDisplay);
        category_title = (TextView) viewLayout.findViewById(R.id.category_title);
        btnDelete = (Button) viewLayout.findViewById(R.id.btnDelete);
        btnShare = (Button) viewLayout.findViewById(R.id.btnShare);
        back = (ImageView) viewLayout.findViewById(R.id.btnBack);


        Log.d(TAG,"showing "+ _imagePaths.get(position));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        Matrix matrix = imgDisplay.getImageMatrix();
        //imgDisplay.setImageBitmap(bitmap, matrix );
        imgDisplay.setImageBitmap(bitmap);
        imgDisplay.setDisplayType(DisplayType.FIT_IF_BIGGER);

        // close button click event
        if(CurrentCategory.equals("All"))
        {
            category_title.setText(db.getSingleCategory(_imagePaths.get(position)));
        }
        else
        category_title.setText(CurrentCategory+"");

        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String deletePath = _imagePaths.get(position);
                ContentResolver contentResolver = _activity.getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ deletePath });
                db.deleteimagepath(deletePath);
                Global_Share.paths_of_image.remove(deletePath);
                Photo_Viewer.viewPager.setAdapter(new Image_View_Adapter(_activity,Global_Share.paths_of_image));
                if(Global_Share.paths_of_image.size() == 0)
                    _activity.finish();
                if(position==Global_Share.paths_of_image.size())
                    Photo_Viewer.viewPager.setCurrentItem(position-1);
                else
                    Photo_Viewer.viewPager.setCurrentItem(position);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });



        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mediaUri = Uri.parse(_imagePaths.get(position));
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,mediaUri);
                _activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
