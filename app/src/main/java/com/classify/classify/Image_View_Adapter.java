package com.classify.classify;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

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

        int width,height;
        final RelativeLayout top,bottom;

        db = new DatabaseHandler(_activity);

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_view_screen, container,
                false);

        width = _activity.getWindowManager().getDefaultDisplay().getWidth();
        height = _activity.getWindowManager().getDefaultDisplay().getHeight();


        RelativeLayout rlv = (RelativeLayout) viewLayout.findViewById(R.id.all_id);
        imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.imgDisplay);

        top = (RelativeLayout)viewLayout.findViewById(R.id.toplayer);
        bottom = (RelativeLayout)viewLayout.findViewById(R.id.bottomlayer);

        Log.d(TAG,"showing "+ _imagePaths.get(position));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
//        Matrix matrix = imgDisplay.getImageMatrix();
//        //imgDisplay.setImageBitmap(bitmap, matrix );
//        imgDisplay.setImageBitmap(bitmap);
        imgDisplay.setDisplayType(DisplayType.FIT_TO_SCREEN);
        Glide.with(_activity).load(_imagePaths.get(position)).skipMemoryCache(true).override(width,height-200).fitCenter().into(imgDisplay);
        imgDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global_Share.TAG,"clicked");
            }
        });

        // close button click event

//        Log.d(Global_Share.TAG,Flag_hide_layout+" outside");

//        imgDisplay.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Log.d(Global_Share.TAG,Flag_hide_layout+" clicked");
//                if(Flag_hide_layout == 1)
//                {
//                    top.setVisibility(View.GONE);
//                    bottom.setVisibility(View.GONE);
////                    fadeOutAndHideImage(top);
////                    fadeOutAndHideImage(bottom);
//                    Flag_hide_layout = 0;
//                }
//                else
//                {
//                    top.setVisibility(View.VISIBLE);
//                    bottom.setVisibility(View.VISIBLE);
////                    fadeInAndHideImage(top);
////                    fadeInAndHideImage(bottom);
//                    Flag_hide_layout = 1;
//                }
//                return false;
//            }
//
//        });


//        rlv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(Global_Share.TAG,Flag_hide_layout+" clicked");
//                if(Flag_hide_layout == 1)
//                {
//                    top.setVisibility(View.GONE);
//                    bottom.setVisibility(View.GONE);
////                    fadeOutAndHideImage(top);
////                    fadeOutAndHideImage(bottom);
//                    Flag_hide_layout = 0;
//                }
//                else
//                {
//                    top.setVisibility(View.VISIBLE);
//                    bottom.setVisibility(View.VISIBLE);
////                    fadeInAndHideImage(top);
////                    fadeInAndHideImage(bottom);
//                    Flag_hide_layout = 1;
//                }
//            }
//        });

//        rlv.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.d(Global_Share.TAG,Flag_hide_layout+" clicked");
//                if(Flag_hide_layout == 1)
//                {
//                    top.setVisibility(View.GONE);
//                    bottom.setVisibility(View.GONE);
////                    fadeOutAndHideImage(top);
////                    fadeOutAndHideImage(bottom);
//                    Flag_hide_layout = 0;
//                }
//                else
//                {
//                    top.setVisibility(View.VISIBLE);
//                    bottom.setVisibility(View.VISIBLE);
////                    fadeInAndHideImage(top);
////                    fadeInAndHideImage(bottom);
//                    Flag_hide_layout = 1;
//                }
//                return false;
//            }
//        });

//        btnDelete.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String deletePath = _imagePaths.get(position);
//                ContentResolver contentResolver = _activity.getContentResolver();
//                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ deletePath });
//                db.deleteimagepath(deletePath);
//                Global_Share.paths_of_image.remove(deletePath);
//                Photo_Viewer.viewPager.setAdapter(new Image_View_Adapter(_activity,Global_Share.paths_of_image));
//                if(Global_Share.paths_of_image.size() == 0)
//                    _activity.finish();
//                if(position==Global_Share.paths_of_image.size())
//                    Photo_Viewer.viewPager.setCurrentItem(position-1);
//                else
//                    Photo_Viewer.viewPager.setCurrentItem(position);
//            }
//        });
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _activity.finish();
//            }
//        });
//
//
//
//        Photo_Viewer.btnShare.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri mediaUri = Uri.parse(_imagePaths.get(position));
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("image/jpeg");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared via Classify");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Shared via Classify ");
//                sharingIntent.putExtra(Intent.EXTRA_STREAM,mediaUri);
//                _activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
//            }
//        });
//

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
