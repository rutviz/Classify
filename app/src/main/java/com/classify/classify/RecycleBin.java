package com.classify.classify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecycleBin extends AppCompatActivity{

    DatabaseHandler myDb;
    private RecyclerView mThumbnailRecyclerView;
    final int[] Delete_mode = {0};
    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0 ;
    DatabaseHandler databaseHandler;
    Activity mActivity;
    ArrayAdapter<String> searchadapter;
    Context context;
    int Mydbcount ;
    ProgressDialog progressDialog,pr;
    int Mediacount ;
    final Handler handler = new Handler();
    List<String> delete_from_appp = new ArrayList<String>();
    List<String> paths_of_image = new ArrayList<String>();
    List<Integer> visible = new ArrayList<Integer>();

    Switch aSwitch;
    DatabaseHandler db;


    public static Button delete_btn;
    public static ImageButton select_all;
    public static ImageButton close;
    public static ImageButton delete_per;
    TextView count_selected,trash_logo;
    final String TAG = "Image_Classify";
    ArrayList<String> date_list = new ArrayList<>();
    List<String> paths_of_images = new ArrayList<String>();
    image_adapter imageadapter;
    int all_selected=0,flag=0;
    int Count_new;
    Thread t;
    String notification_title = "Classify in progress...";
    int notification_rate = 0;
    int total_image;
    ImageView noImage;
    int width,height;
    private TextView trash_logo_text;
    private RelativeLayout bottom_sheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    ImageView home,trash,auto_del,notification,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);
        myDb = new DatabaseHandler(this);
        paths_of_image = myDb.recyclebingetdata();
        mActivity = this;
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        mThumbnailRecyclerView = (RecyclerView) findViewById(R.id.recycler__recycle_view_photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbnailRecyclerView.setHasFixedSize(true);
        imageadapter = new image_adapter();
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
        mThumbnailRecyclerView.setAdapter(imageadapter);
        noImage = (ImageView) findViewById(R.id.no_photos);
        delete_btn = (Button) findViewById(R.id.restore);
        delete_per = (ImageButton) findViewById(R.id.delete);
        close = (ImageButton) findViewById(R.id.close);
        select_all = (ImageButton) findViewById(R.id.check);
        trash_logo = (TextView) findViewById(R.id.trash_logo);
        trash_logo_text = (TextView) findViewById(R.id.no_photos_text);
        count_selected = (TextView) findViewById(R.id.count_selelcted);
        final View bottomsheet = findViewById(R.id.bottom_sheet);
        bottom_sheet = (RelativeLayout)findViewById(R.id.bottom_sheet);
        final RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.toolbar);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting permanently...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        pr = new ProgressDialog(this);
        pr.setTitle("Restoring...");
        pr.setCancelable(false);
        pr.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        delete_per.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setMax(delete_from_appp.size());
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int init=0;init<delete_from_appp.size();init++) {
                            progressDialog.setProgress(init);
                            String path = delete_from_appp.get(init);
                            Uri uri= Uri.parse("file://"+path);
                            File delete = new File(uri.getPath());
                            delete.delete();
                            myDb.deleteimagepathfromrecycle(path);
                        }
                        progressDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UpdateUI();
                            }
                        });

                    }
                });
                t.start();

            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pr.show();
                restore();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_change();
                imageadapter.notifyDataSetChanged();
            }
        });

        db = new DatabaseHandler(this);
        aSwitch = (Switch)findViewById(R.id.recycleSwitch);
        String recycle_flags = db.globalgetvalue("Flag_for_recycle");
        if(recycle_flags.equals("0"))
        {
            aSwitch.setChecked(false);
        }
        else {
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String recycle_flag = db.globalgetvalue("Flag_for_recycle");
                if(recycle_flag.equals("0"))
                {
                    db.globalEditaddData("Flag_for_recycle","1");
                }
                else {
                    db.globalEditaddData("Flag_for_recycle","0");
                }
            }
        });


        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(all_selected==0)
                {
                    select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_white_24dp));
                    visible.clear();
                    delete_from_appp.clear();
                    for(int i = 0; i<paths_of_image.size();i++)
                    {
                        visible.add(1);
                        delete_from_appp.add(paths_of_image.get(i));
                    }
                    count_selected.setText(delete_from_appp.size()+"");
                    imageadapter.notifyDataSetChanged();
                    all_selected = 1;
                }
                else
                {
                    UpdateUI();
                    all_selected = 0;
                }
                for(int i :visible)
                    Log.d(TAG,""+i);
            }
        });



    /*BottomBar*/

        home = (ImageView) findViewById(R.id.Home);
        trash = (ImageView) findViewById(R.id.Trash);
        auto_del = (ImageView) findViewById(R.id.AutoDelete);
        notification = (ImageView) findViewById(R.id.Notification);
        settings = (ImageView) findViewById(R.id.Settings);


        LayoutParams lp = home.getLayoutParams();
        int pdd = (int) Math.round(width/14.4);
        trash.setPadding(pdd-20,pdd-20,pdd-20,pdd-20);
        Log.d("widthh",width+"");
        lp.width = width/5;
        lp.height = width/5;
        LayoutParams lp2 = trash.getLayoutParams();
        home.setPadding(pdd,pdd,pdd,pdd);
        auto_del.setPadding(pdd,pdd,pdd,pdd);
        notification.setPadding(pdd,pdd,pdd,pdd);
        settings.setPadding(pdd,pdd,pdd,pdd);
        lp2.width = width/5;
        lp2.height = width/5;
        LayoutParams lp3 = auto_del.getLayoutParams();
        lp3.width = width/5;
        lp3.height = width/5;
        LayoutParams lp4 = notification.getLayoutParams();
        lp4.width = width/5;
        lp4.height = width/5;
        LayoutParams lp5 = settings.getLayoutParams();
        lp5.width = width/5;
        lp5.height = width/5;

        home.setLayoutParams(lp);
        trash.setLayoutParams(lp2);
        auto_del.setLayoutParams(lp3);
        notification.setLayoutParams(lp4);
        settings.setLayoutParams(lp5);

        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecycleBin.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(RecycleBin.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        trash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecycleBin.this,RecycleBin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(RecycleBin.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        notification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        auto_del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecycleBin.this,AutoDelete.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(RecycleBin.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecycleBin.this,AppSettings.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(RecycleBin.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });


    /*End*/
    }


    public void restore()
    {
        pr.setMax(delete_from_appp.size());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int init=0;init<delete_from_appp.size();init++){
                    pr.setProgress(init);
                    String path = delete_from_appp.get(init);
                    Log.d("oldpath",path);
                    String oldpath = myDb.recyclegetvalue(path);
                    String modtime = myDb.recyclegetdatevalue(path);
                    InputStream in = null;
                    OutputStream out = null;
                    Uri mediaUri = Uri.parse("file://"+ path);
                    File imagepath =new File(mediaUri.getPath());
                    String imagename = imagepath.getName().toString();
                    int length = imagename.length();
                    imagename = imagename.substring(0,length-9);
                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();
                    Log.d("hey1",timestamp);
                    try {

                        String outputPath = oldpath;
                        try {
                            in = new FileInputStream(delete_from_appp.get(init));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        out = new FileOutputStream(outputPath);

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                        Uri uria = Uri.parse(oldpath);
                        File f = new File(uria.getPath());
                        Log.d("date",modtime);
                        long l = Long.parseLong(modtime);
                        boolean b = f.setLastModified(l);
                        if(b)
                        {
                            Log.d("booll","true");
                        }
                    }
                    catch (FileNotFoundException fnfe1) {
                        Log.e("tag", fnfe1.getMessage());
                    }
                    catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }
                    Uri uri1 = Uri.parse("file://"+delete_from_appp.get(init));
                    File delete = new File(uri1.getPath());
                    delete.delete();
                    myDb.deleteimagepathfromrecycle(delete_from_appp.get(init));
                    Uri urii = Uri.parse("file://"+oldpath);
                    final File file = new File(urii.getPath());

                    MediaScannerConnection.scanFile(
                            getApplicationContext(),
                            new String[]{file.getAbsolutePath()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.d("complete","file "+ s +" scanned "+ uri );
                                }
                            }
                    );
                }
                pr.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UpdateUI();
                    }
                });

            }
        });
        thread.start();


    }


    private class image_adapter extends RecyclerView.Adapter<image_adapter.ViewHolder>
    {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card    ,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            if(visible.get(position)==0)
                holder.chk.setVisibility(View.GONE);
            else
                holder.chk.setVisibility(View.VISIBLE);

            LayoutParams params = holder.image.getLayoutParams();
            params.width = (width-6)/3;
            params.height = (width-6)/3;
            holder.image.setLayoutParams(params);

            LayoutParams params1 = holder.chk.getLayoutParams();
            params1.width = (width-6)/3;
            params1.height = (width-6)/3;
            holder.image.setLayoutParams(params1);

            Glide.with(mActivity).load(paths_of_image.get(position)).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Delete_mode[0] == 1) {
                        if (visible.get(position)==1) {
                            holder.chk.setVisibility(View.GONE);
                            visible.set(position,0);
                            delete_from_appp.remove(delete_from_appp.indexOf(new String(paths_of_image.get(position))));
                            select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
                            all_selected=0;
                            if (delete_from_appp.size() == 0) {
                                Delete_mode[0] = 0;
                                Category_change();
                            }
                        } else {
                            holder.chk.setVisibility(View.VISIBLE);
                            delete_from_appp.add(paths_of_image.get(position));
                            visible.set(position,1);
//                            Global_Share.delete_from_app = delete_from_appp;
                        }
                        count_selected.setText(delete_from_appp.size()+"");
                    } else {
                        Intent i = new Intent(mActivity, Photo_Viewer_recycle.class);
                        Global_Share.paths_of_image = paths_of_image;
                        i.putExtra("id", position+"");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, (View) holder.image, "image");
                        mActivity.startActivity(i, options.toBundle());
                    }
                    Log.d(TAG,""+Delete_mode[0]+" "+visible.get(position));
                }
            });
            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(Delete_mode[0] == 0){
                        visible.set(position,1);
                        delete_btn.setVisibility(View.VISIBLE);
                        delete_per.setVisibility(View.VISIBLE);
                        select_all.setVisibility(View.VISIBLE);
                        close.setVisibility(View.VISIBLE);
                        trash_logo.setVisibility(View.GONE);
                        count_selected.setVisibility(View.VISIBLE);
                        Delete_mode[0] = 1;
                        Log.d(TAG,position+"");
                        holder.chk.setVisibility(View.VISIBLE);
                        delete_from_appp.add(paths_of_image.get(position));
                        count_selected.setText(delete_from_appp.size()+"");
                    }
                    return true;
                }
            });
        }
        @Override
        public int getItemCount()
        {
            if(paths_of_image.size() == 0){
                noImage.setVisibility(View.VISIBLE);
                trash_logo_text.setVisibility(View.VISIBLE);
            }
            else {
                noImage.setVisibility(View.GONE);
                trash_logo_text.setVisibility(View.GONE);
            }
            return paths_of_image.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView image,chk;
            CheckBox checkBox;
            public ViewHolder(View itemView) {
                super(itemView);
                for(int i = 0; i<paths_of_image.size();i++)
                    visible.add(0);
                image = (ImageView)itemView.findViewById(R.id.image_all);
                chk = (ImageView) itemView.findViewById(R.id.isSelected);
            }
        }
    }

//    List<String> convert(ArrayList<String> Specific)
//    {
//        List<String> temp = new ArrayList<>();
//        for(int i=0;i<Specific.size();i++)
//        {
//            temp.add(Specific.get(i).getPath());
//        }
//        Log.d(TAG,temp.size()+" Main");
//        return temp;
//    }

    void Category_change()
    {
        visible.clear();
        delete_from_appp.clear();
        for(int i = 0; i<paths_of_image.size();i++)
            visible.add(0);
        hide_button();
    }
    void hide_button()
    {
        select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
        delete_btn.setVisibility(View.GONE);
        delete_per.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
        trash_logo.setVisibility(View.VISIBLE);
        select_all.setVisibility(View.GONE);
        count_selected.setVisibility(View.GONE);
        count_selected.setText("0");
        all_selected = 0;
        Delete_mode[0] = 0;
    }
    private void UpdateUI() {
        delete_from_appp.clear();
        hide_button();
        UpdateLists();
    }

    private void UpdateLists() {
        visible.clear();
        paths_of_image = myDb.recyclebingetdata();

        for(int i = 0; i<paths_of_image.size();i++)
            visible.add(0);
        imageadapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        UpdateUI();
    }
}
