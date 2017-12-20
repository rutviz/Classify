package com.classify.classify;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MediaStoreAdapter extends RecyclerView.Adapter<MediaStoreAdapter.ViewHolder> {

    private Cursor mMediaStoreCursor;
    private final Activity mActivity;
    private OnClickThumbListener mOnClickThumbListener;
    int flag=0;
    String no;
    int width,height;
    int flags = 1;
    List<String> paths_of_image = new ArrayList<String>();
    List<String> date_list = new ArrayList<String>();
    DatabaseHandler myDB;
    ArrayList<String> category = new ArrayList<>();
    private Classifier classifier;

    public interface OnClickThumbListener {
        void OnClickImage(Uri imageUri);
    }
    public MediaStoreAdapter(Activity activity, Classifier classifier) {
        Log.d("lests","7");
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener)activity;
        this.classifier = classifier;
    }
    public MediaStoreAdapter(Activity activity) {
        Log.d("lests","8");
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener)activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("lests","9");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_grid, parent, false);

    myDB = new DatabaseHandler(view.getContext());
        if(flag==0){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("lests","10");
                    int Count_new = mMediaStoreCursor.getCount();
                    int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    for (int i = 0;i<mMediaStoreCursor.getCount();i++)
                    {
                        mMediaStoreCursor.moveToPosition(i);
                        String dataString = mMediaStoreCursor.getString(dataIndex);
                        Uri mediaUri = Uri.parse("file://" + dataString);
                        File imagePath = new File(mediaUri.getPath());
                        File file = new File(imagePath.getAbsolutePath());
                        long length = file.length();
                        length = length/1024;
                        String size = String.valueOf(length );
                        Date lastModDate = new Date(file.lastModified());
                        String date = lastModDate.getTime()+"";
                        //boolean isInserted = myDb.insertData(imagePath.getName(),imagePath.getAbsolutePath(),size,date);
                        paths_of_image.add(imagePath.getAbsolutePath());
                        date_list.add(date);
                        //myDB.addData(new Classify_path(imagePath.getAbsolutePath(),"none"));
//                        Log.d("DATE",date);


                    }
//                    Log.d("Database",myDB.getDataCount()+"");
//                    Log.d("count12345", String.valueOf(paths_of_image.size()) + " "+ mMediaStoreCursor.getCount());

                    if(myDB.getDataCount()<1){
                        Log.d("lests1","11");
                        for(int z =0; z < paths_of_image.size();z++)
                        {
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(z)),224,224);
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
//                            int r = (new Random()).nextInt(4);
                            if(results.size()!=0){
                                myDB.addData(new Classify_path(paths_of_image.get(z),results.get(0).toString(),date_list.get(z)));
                            }
                            else
                            {
                                myDB.addData(new Classify_path(paths_of_image.get(z),"none",date_list.get(z)));
                            }
                           Log.d("CLassifying",results.toString()+" "+paths_of_image.get(z));
                        }
                    }
                    else
                    {
                        Log.d("lests1","12");
                        int new_images = Count_new - myDB.getDataCount();
                        int init = 0;
                        while(new_images>0)
                        {
                            Log.d("hey","13");
                            if(myDB.getpathCount(paths_of_image.get(init))==0)
                            {
                                Log.d("lests1","14");
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(init)),224,224);
                                try{
                                    final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                                    if(results.size()!=0){
                                        Log.d("lests1","15 : lest");
                                        myDB.addData(new Classify_path(paths_of_image.get(init),results.get(0).toString(),date_list.get(init)));
                                    }
                                    else
                                    {
                                        Log.d("lests","15 : lest");
                                        myDB.addData(new Classify_path(paths_of_image.get(init),"none",date_list.get(init)));
                                    }
                                    Log.d("CLassifying",results.toString()+" "+paths_of_image.get(init));
                                }

                                catch (Exception e)
                                {
                                    myDB.addData(new Classify_path(paths_of_image.get(init),"none",date_list.get(init)));
                                }

//                            int r = (new Random()).nextInt(4);

                            }
                            Log.d("lests","16");
                            new_images = Count_new - myDB.getDataCount();
                            init++;
                        }

//                            Log.d("DB_PATH","new path updated "+cp.toString());

//                        Log.d("DB_PATH","new path updated "+new_images+"");
                    }


                }
            });
            t.start();
            flag=1;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("lests","17");
//        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(position)),width/3,width/3);
//                holder.image.setImageBitmap(bitmap);
       while(paths_of_image.size()==position){}
            Glide.with(mActivity).load(paths_of_image.get(position)).centerCrop().into(holder.image);
//        Log.d("count","running "+position);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mActivity, Photo_Viewer.class);
                    i.putExtra("path_of_image", paths_of_image.get(position));
                    i.putExtra("category",myDB.getSingleCategory(paths_of_image.get(position)));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, (View) holder.image, "image");
                    mActivity.startActivity(i, options.toBundle());
                }
            });

    }
    int size_data()
    {
        int size;
        size = mMediaStoreCursor.getCount();
        return size;
    }

    @Override
    public int getItemCount() {
        Log.d("lests","18");
        //return (mMediaStoreCursor == null) ? 0 : mMediaStoreCursor.getCount();
        return (mMediaStoreCursor == null) ? 0:mMediaStoreCursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("lests","19");
            image = (ImageView) itemView.findViewById(R.id.image_all);
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        Log.d("lests","6");
        if (mMediaStoreCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mMediaStoreCursor;
        this.mMediaStoreCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Log.d("lests","3");
        Cursor oldCursor = swapCursor(cursor);
        Log.d("lests","4");
        if (oldCursor != null) {
            Log.d("lests","5");
            oldCursor.close();
        }
    }
}

