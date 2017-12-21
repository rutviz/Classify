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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    List<String> delete_from_appp = new ArrayList<String>();
    DatabaseHandler myDB;
    ArrayList<String> category = new ArrayList<>();
    private Classifier classifier;

    public interface OnClickThumbListener {
        void OnClickImage(Uri imageUri);
    }
    public MediaStoreAdapter(Activity activity, Classifier classifier) {
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener)activity;
        this.classifier = classifier;
    }
    public MediaStoreAdapter(Activity activity) {
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener)activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_grid, parent, false);

    myDB = new DatabaseHandler(view.getContext());
        if(flag==0){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
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
                        paths_of_image.add(imagePath.getAbsolutePath());
                        date_list.add(date);
                    }
                    if(myDB.getDataCount()<1)
                    {
                        for(int z =0; z < paths_of_image.size();z++)
                        {
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(z)),224,224);
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
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
                        int new_images = Count_new - myDB.getDataCount();
                        int init = 0;
                        while(new_images>0)
                        {
                            if(myDB.getpathCount(paths_of_image.get(init))==0)
                            {
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(init)),224,224);
                                try{
                                    final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                                    if(results.size()!=0){
                                        myDB.addData(new Classify_path(paths_of_image.get(init),results.get(0).toString(),date_list.get(init)));
                                    }
                                    else
                                    {
                                        myDB.addData(new Classify_path(paths_of_image.get(init),"none",date_list.get(init)));
                                    }
                                    Log.d("CLassifying",results.toString()+" "+paths_of_image.get(init));
                                }
                                catch (Exception e)
                                {
                                    myDB.addData(new Classify_path(paths_of_image.get(init),"none",date_list.get(init)));
                                }
                            }
                            new_images = Count_new - myDB.getDataCount();
                            init++;
                        }

                    }

                    updatedbimagepath();
                }
            });
            t.start();
            flag=1;

        }

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

//        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_image.get(position)),width/3,width/3);
//                holder.image.setImageBitmap(bitmap);
         final int[] Delete_mode = {0};
       while(paths_of_image.size()==position){}
            Glide.with(mActivity).load(paths_of_image.get(position)).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Delete_mode[0]==1)
                    {
//                        if(holder.checkBox.isChecked())
//                        {
//                            holder.checkBox.setVisibility(View.GONE);
//                            holder.checkBox.setChecked(false);
//                            delete_from_appp.remove(delete_from_appp.indexOf(new String(paths_of_image.get(position))));
//                            Global_Share.delete_from_app = delete_from_appp;
//                            if(delete_from_appp.size()==0)
//                            {
//                                Delete_mode[0]=0;
//                                MainActivity.delete_btn.setVisibility(View.GONE);
//                            }
//                        }
//                        else
                        {
//                            holder.checkBox.setVisibility(View.VISIBLE);
//                            holder.checkBox.setChecked(true);
                            delete_from_appp.add(paths_of_image.get(position));
                            Global_Share.delete_from_app = delete_from_appp;
                        }
                    }
                    else
                    {
                        Intent i = new Intent(mActivity, Photo_Viewer.class);
                        i.putExtra("path_of_image", paths_of_image.get(position));
                        i.putExtra("category",myDB.getSingleCategory(paths_of_image.get(position)));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, (View) holder.image, "image");
                        mActivity.startActivity(i, options.toBundle());
                    }
                }
            });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(Delete_mode[0] == 0){
                    MainActivity.delete_btn.setVisibility(View.VISIBLE);
                    Delete_mode[0] = 1;
//                    holder.checkBox.setVisibility(View.VISIBLE);
//                    holder.checkBox.setChecked(true);
                    delete_from_appp.add(paths_of_image.get(position));
                    Global_Share.delete_from_app = delete_from_appp;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mMediaStoreCursor == null) ? 0:mMediaStoreCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
       // private final CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_all);
          // checkBox = (CheckBox) itemView.findViewById(R.id.itemCheckBox);
        }
    }

    private Cursor swapCursor(Cursor cursor) {
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
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    public void updatedbimagepath() {
        List<String> paths_of_image_db = new ArrayList<String>();
        paths_of_image_db = myDB.getImagepathlist();
        paths_of_image_db.removeAll(paths_of_image);
        if(paths_of_image_db.size()!=0){

            for (int i = 0; i < paths_of_image_db.size(); i++) {
                myDB.deleteimagepath(paths_of_image_db.get(i));
            }
        }
    }
}


