package com.classify.classify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoDelete_category extends AppCompatActivity {

    String cate,change;
    ArrayList<String> al = new ArrayList<>();
    DatabaseHandler db,myDb,databaseHandler,db2,db3,myDB;
    RecyclerView autoDelete;
    image_adapter adapter;
    List<Integer> visible = new ArrayList<Integer>();
    Activity mActivity;
    final Handler handler = new Handler();
    List<String> delete_from_appp = new ArrayList<String>();
    List<String> paths_of_image = new ArrayList<String>();
    int width,height;
    final int[] Delete_mode = {1};
    ProgressDialog progressDialog;
    public static ImageButton select_all,close;
    public static TextView count_selected,title;
    int all_selected=0,flag=0,delete_flag=0;
    ImageButton delete;
    DrawerLayout mDrawerLayout;
    AsyncTaskRunnerForDelete run;
    private NavigationView navigationView;
    ImageView home,trash,auto_del,notification,settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete_category);
        mActivity = this;
        close = (ImageButton) findViewById(R.id.close1);
        select_all = (ImageButton) findViewById(R.id.check1);
        delete = (ImageButton) findViewById(R.id.delete_auto_category);
        count_selected = (TextView) findViewById(R.id.count_selelcted1);
        title = (TextView) findViewById(R.id.trash_logo1);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        home = (ImageView) findViewById(R.id.Home);
        trash = (ImageView) findViewById(R.id.Trash);
        auto_del = (ImageView) findViewById(R.id.AutoDelete);
        notification = (ImageView) findViewById(R.id.Notification);
        settings = (ImageView) findViewById(R.id.Settings);
        final RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.toolbar2);
        ViewGroup.LayoutParams lp = home.getLayoutParams();
        int pdd = (int) Math.round(width/14.4);
        auto_del.setPadding(pdd-20,pdd-20,pdd-20,pdd-20);
        Log.d("widthh",width+"");
        lp.width = width/5;
        lp.height = width/5;
        ViewGroup.LayoutParams lp2 = trash.getLayoutParams();
        trash.setPadding(pdd,pdd,pdd,pdd);
        home.setPadding(pdd,pdd,pdd,pdd);
        notification.setPadding(pdd,pdd,pdd,pdd);
        settings.setPadding(pdd,pdd,pdd,pdd);
        lp2.width = width/5;
        lp2.height = width/5;
        ViewGroup.LayoutParams lp3 = auto_del.getLayoutParams();
        lp3.width = width/5;
        lp3.height = width/5;
        ViewGroup.LayoutParams lp4 = notification.getLayoutParams();
        lp4.width = width/5;
        lp4.height = width/5;
        ViewGroup.LayoutParams lp5 = settings.getLayoutParams();
        lp5.width = width/5;
        lp5.height = width/5;

        home.setLayoutParams(lp);
        trash.setLayoutParams(lp2);
        auto_del.setLayoutParams(lp3);
        notification.setLayoutParams(lp4);
        settings.setLayoutParams(lp5);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete_category.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete_category.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete_category.this,RecycleBin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete_category.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        auto_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete_category.this,AutoDelete.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete_category.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AutoDelete_category.this,AppSettings.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete_category.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });
        autoDelete = (RecyclerView)findViewById(R.id.recycler__auto_delete1);
        db = new DatabaseHandler(this);
        myDb = new DatabaseHandler(this);
        databaseHandler = new DatabaseHandler(this);
        db2 = new DatabaseHandler(this);
        db3 = new DatabaseHandler(this);
        myDB = new DatabaseHandler(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Moving to trash...");
        cate = getIntent().getExtras().getString("category");
        change = getIntent().getExtras().getString("change");

        if(change.equals("0"))
        {
            title.setText(cate);
            al = db.getUniqueDataPathOfAutoDelete(cate);
        }
        else if(change.equals("1"))
        {
            title.setText(cate);
            al = db.getUniqueDataPath(cate);
        }




        close.setVisibility(View.VISIBLE);
        count_selected.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_change();
                adapter.notifyDataSetChanged();
            }
        });
        for(int i=0;i<al.size();i++)
        {
            visible.add(1);
            delete_from_appp.add(al.get(i));
        }

        count_selected.setText(delete_from_appp.size()+"");
        adapter = new image_adapter();
        autoDelete.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        autoDelete.setHasFixedSize(true);
        autoDelete.setLayoutManager(gridLayoutManager);
        select_all.setVisibility(View.VISIBLE);

        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(all_selected==0)
                {
                    select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_white_24dp));
                    visible.clear();
                    delete_from_appp.clear();
                    for(int i = 0; i<al.size();i++)
                    {
                        visible.add(1);
                        delete_from_appp.add(al.get(i));
                    }
                    count_selected.setText(delete_from_appp.size()+"");
                    adapter.notifyDataSetChanged();
                    all_selected = 1;
                }
                else
                {
                    UpdateUI();
                    all_selected = 0;
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_flag=1;
                String recycle_flag = db2.globalgetvalue("Flag_for_recycle");
                if(recycle_flag.equals("1"))
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoDelete_category.this);
                    alertDialogBuilder.setMessage("Move to Trash");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    run = new AsyncTaskRunnerForDelete();
                                    progressDialog.show();
                                    run.execute();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Category_change();
                            adapter.notifyDataSetChanged();
                            delete_flag=0;
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
                else{
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoDelete_category.this);
                    alertDialogBuilder.setMessage("Permanently delete");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    run = new AsyncTaskRunnerForDelete();
                                    progressDialog.show();
                                    run.execute();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Category_change();
                            adapter.notifyDataSetChanged();
                            delete_flag=0;
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }

    private class image_adapter extends RecyclerView.Adapter<image_adapter.ViewHolder>
    {
        @Override
        public image_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card,parent,false);
            return new image_adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final image_adapter.ViewHolder holder, final int position)
        {
            if(visible.get(position)==0)
                holder.chk.setVisibility(View.GONE);
            else
                holder.chk.setVisibility(View.VISIBLE);

//            Log.d(TAG,""+visible.get(position));
            Log.d("atd",position+"");
            ViewGroup.LayoutParams params = holder.image.getLayoutParams();
            params.width = (width-6)/3;
            params.height = (width-6)/3;
            holder.image.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = holder.chk.getLayoutParams();
            params1.width = (width-6)/3;
            params1.height = (width-6)/3;
            holder.image.setLayoutParams(params1);

            Glide.with(mActivity).load(al.get(position)).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Delete_mode[0] == 1) {
                        if (visible.get(position)==1) {
                            holder.chk.setVisibility(View.GONE);
                            visible.set(position,0);
                            delete_from_appp.remove(delete_from_appp.indexOf(new String(al.get(position))));
                            select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
                            all_selected=0;
                            if (delete_from_appp.size() == 0) {
                                Delete_mode[0] = 0;
                                Category_change();
                            }
                        } else {
                            holder.chk.setVisibility(View.VISIBLE);
                            delete_from_appp.add(al.get(position));
                            visible.set(position,1);
                            if(delete_from_appp.size()==al.size())
                            {
                                all_selected = 1;
                                select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_white_24dp));
                            }
                        }
                        count_selected.setText(delete_from_appp.size()+"");
                    } else {
                        /*Intent i = new Intent(mActivity, Photo_Viewer.class);
                        Global_Share.paths_of_image = al;
                        i.putExtra("id", position+"");
                        i.putExtra("category", databaseHandler.getSingleCategory(al.get(position)));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, (View) holder.image, "image");
                        mActivity.startActivity(i, options.toBundle());*/
                    }
//                    Log.d(TAG,""+Delete_mode[0]+" "+visible.get(position));
                }
            });
            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(Delete_mode[0] == 0){
                        visible.set(position,1);
                        select_all.setVisibility(View.VISIBLE);
                        close.setVisibility(View.VISIBLE);
                        count_selected.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        Delete_mode[0] = 1;
//                        Log.d(TAG,position+"");
                        holder.chk.setVisibility(View.VISIBLE);
                        delete_from_appp.add(al.get(position));
                        count_selected.setText(delete_from_appp.size()+"");
                    }
                    return true;
                }
            });
        }
        @Override
        public int getItemCount() {

            Log.d("atd", String.valueOf(al.size()+"hi"));
            return al.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView image,chk;
            CheckBox checkBox;
            public ViewHolder(View itemView) {
                super(itemView);
                for(int i = 0; i<al.size();i++)
                    visible.add(0);
                image = (ImageView)itemView.findViewById(R.id.image_all);
                chk = (ImageView) itemView.findViewById(R.id.isSelected);
            }
        }
    }
    void Category_change()
    {
        al = db.getUniqueDataPath(cate);
        visible.clear();
        delete_from_appp.clear();
        for(int i = 0; i<al.size();i++)
            visible.add(0);
        hide_button();
    }
    void hide_button()
    {
        select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
        close.setVisibility(View.GONE);
        select_all.setVisibility(View.GONE);
        count_selected.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        count_selected.setText("0");
        all_selected = 0;
        Delete_mode[0] = 0;
    }
    private void UpdateUI() {
        delete_from_appp.clear();
        hide_button();
        UpdateLists();
    }
    List<String> convert(ArrayList<Classify_path> Specific)
    {
        List<String> temp = new ArrayList<>();
        for(int i=0;i<Specific.size();i++)
        {
            temp.add(Specific.get(i).getPath());
        }
//        Log.d(TAG,temp.size()+" Main");
        return temp;
    }


    private void UpdateLists() {
        visible.clear();
        paths_of_image = convert(myDb.getUniqueData(cate));
        for(int i = 0; i<paths_of_image.size();i++)
            visible.add(0);
        adapter.notifyDataSetChanged();
    }

    public class AsyncTaskRunnerForDelete extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if(delete_from_appp.size()!=0){
                progressDialog.setMax(delete_from_appp.size());
                for (int i = 0; i < delete_from_appp.size(); i++) {
                    progressDialog.setProgress(i);
                    // Log.d("yess","2");
                    String myPath = delete_from_appp.get(i);
                    try{

                        String recycle_flag = db2.globalgetvalue("Flag_for_recycle");
                        if(recycle_flag.equals("1"))
                        {
                            recyclerbin(myPath);
                            ContentResolver contentResolver = getContentResolver();
                            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                            myDB.deleteimagepath(delete_from_appp.get(i));
                            myDB.deleteimagepathFromAutoDelete(delete_from_appp.get(i));
                        }
                        else {
                            ContentResolver contentResolver = getContentResolver();
                            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                            myDB.deleteimagepath(delete_from_appp.get(i));
                            myDB.deleteimagepathFromAutoDelete(delete_from_appp.get(i));
                        }
                        al.remove(al.indexOf(delete_from_appp.get(i)));

                    }
                    catch(Exception e){
                        // StorageProblem(delete_from_appp.get(i));
                        Log.d("yes",e.getMessage());
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UpdateUI();
                    }
                });
                progressDialog.dismiss();
                delete_flag=0;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            run.cancel(true);
        }
        @Override
        protected void onPreExecute() {
            Log.d("yess","pre");
        }
        @Override
        protected void onProgressUpdate(String... text) {
            Log.d("yess","update");
        }
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
        try {
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

            out.flush();
            out.close();
            out = null;
            db3.recyclebinaddData(path,timestamp,time,outputPath);
        }
        catch (FileNotFoundException fnfe1) {
        }
        catch (Exception e) {
        }
    }

}
