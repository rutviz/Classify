package com.classify.classify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

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
    public Object instantiateItem(ViewGroup container, int position) {
        ImageViewTouch imgDisplay;
        Button btnClose;
        TextView category_title;

        db = new DatabaseHandler(_activity);

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_view_screen, container,
                false);

        imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.imgDisplay);
        category_title = (TextView) viewLayout.findViewById(R.id.category_title);
//        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);


        Log.d(TAG,"showing "+ _imagePaths.get(position));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        Matrix matrix = imgDisplay.getImageMatrix();
        //imgDisplay.setImageBitmap(bitmap, matrix );
        imgDisplay.setImageBitmap(bitmap);
//        imgDisplay.setDisplayType(DisplayType.FIT_WIDTH);

        // close button click event
        if(CurrentCategory.equals("All"))
        {
            category_title.setText(db.getSingleCategory(_imagePaths.get(position)));
        }
        else
        category_title.setText(CurrentCategory+"");
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _activity.finish();
//            }
//        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
