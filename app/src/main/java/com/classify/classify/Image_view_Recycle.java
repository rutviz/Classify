package com.classify.classify;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.classify.classify.Global_Share.Flag_hide_layout;

/**
 * Created by Rutviz Vyas on 19-12-2017.
 */

public class Image_view_Recycle extends PagerAdapter {

    DatabaseHandler db;
    private Activity _activity;
    private List<String> _imagePaths = new ArrayList<>();
    private LayoutInflater inflater;

    public Image_view_Recycle(Activity activity, List<String> imagePaths) {
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
        Button btnDelete;
        ImageView back;
        int width,height;
        final RelativeLayout top,bottom;

        db = new DatabaseHandler(_activity);

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.activity_image_view__recycle, container,
                false);

        width = _activity.getWindowManager().getDefaultDisplay().getWidth();
        height = _activity.getWindowManager().getDefaultDisplay().getHeight();

        RelativeLayout rlv = (RelativeLayout) viewLayout.findViewById(R.id.all_id_recycle);
        imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.imgDisplay_recycle);

        btnDelete = (Button) viewLayout.findViewById(R.id.btnDelete_recycle);
        back = (ImageView) viewLayout.findViewById(R.id.btnBack_recycle);
        top = (RelativeLayout)viewLayout.findViewById(R.id.toplayer_recycle);
        bottom = (RelativeLayout)viewLayout.findViewById(R.id.bottomlayer_recycle);

        Log.d(TAG,"showing "+ _imagePaths.get(position));

        imgDisplay.setDisplayType(DisplayType.FIT_TO_SCREEN);
        Glide.with(_activity).load(_imagePaths.get(position)).skipMemoryCache(true).override(width,height-200).fitCenter().into(imgDisplay);

        // close button click event
        Log.d(Global_Share.TAG,Flag_hide_layout+" outside");

        imgDisplay.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(Global_Share.TAG,Flag_hide_layout+" clicked");
                if(Flag_hide_layout == 1)
                {
                    top.setVisibility(View.GONE);
                    bottom.setVisibility(View.GONE);
//                    fadeOutAndHideImage(top);
//                    fadeOutAndHideImage(bottom);
                    Flag_hide_layout = 0;
                }
                else
                {
                    top.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.VISIBLE);
//                    fadeInAndHideImage(top);
//                    fadeInAndHideImage(bottom);
                    Flag_hide_layout = 1;
                }
                return false;
            }

        });


        rlv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global_Share.TAG,Flag_hide_layout+" clicked");
                if(Flag_hide_layout == 1)
                {
                    top.setVisibility(View.GONE);
                    bottom.setVisibility(View.GONE);
//                    fadeOutAndHideImage(top);
//                    fadeOutAndHideImage(bottom);
                    Flag_hide_layout = 0;
                }
                else
                {
                    top.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.VISIBLE);
//                    fadeInAndHideImage(top);
//                    fadeInAndHideImage(bottom);
                    Flag_hide_layout = 1;
                }
            }
        });


        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String deletePath = _imagePaths.get(position);
                Uri uri= Uri.parse("file://"+deletePath);
                File delete = new File(uri.getPath());
                delete.delete();
                db.deleteimagepathfromrecycle(deletePath);
                Global_Share.paths_of_image.remove(deletePath);
                Photo_Viewer_recycle.viewPager.setAdapter(new Image_view_Recycle(_activity,Global_Share.paths_of_image));
                if(Global_Share.paths_of_image.size() == 0)
                    _activity.finish();
                if(position==Global_Share.paths_of_image.size())
                    Photo_Viewer_recycle.viewPager.setCurrentItem(position-1);
                else
                    Photo_Viewer_recycle.viewPager.setCurrentItem(position);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });



        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
    private void fadeOutAndHideImage(final RelativeLayout img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
    private void fadeInAndHideImage(final RelativeLayout img)
    {
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
}
