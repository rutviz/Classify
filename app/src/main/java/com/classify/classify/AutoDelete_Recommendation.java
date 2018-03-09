package com.classify.classify;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class AutoDelete_Recommendation extends AppCompatActivity {

    int width,height,delete_flag=0;
    RecyclerView rc1,rc2;
    Context context;
    DatabaseHandler myDb,db2,myDB,db3;
    Button delete,cancel;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> recomndation_category = new ArrayList<>();
    ArrayList<String> delete_cat = new ArrayList<>();
    AsyncTaskRunnerForDelete1 run;
    ProgressDialog progressDialog;
    Adapter adap= new Adapter();
    final Handler handler = new Handler();
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete__recommendation);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Moving to trash...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        delete = (Button)findViewById(R.id.delete);
        cancel = (Button)findViewById(R.id.cancel);
        btnBack = (ImageView)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AutoDelete_Recommendation.this,MainActivity.class));
            }
        });
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();



        myDb = new DatabaseHandler(this);
        myDB = new DatabaseHandler(this);
        db2 = new DatabaseHandler(this);
        db3 = new DatabaseHandler(this);
        recomndation_category = myDb.getCategoryFromAutoDeleteSelected();

        if(recomndation_category.size()==0)
        {
            startActivity(new Intent(AutoDelete_Recommendation.this,MainActivity.class));
        }

        context = getApplicationContext();

        rc1 = (RecyclerView) findViewById(R.id.recycler_recomnd);
        rc1.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rc1.setAdapter(adap);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomandation_card,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ViewGroup.LayoutParams params = holder.delete_btn.getLayoutParams();
            params.width = width/2;
            holder.delete_btn.setLayoutParams(params);
            images = myDb.getUniqueDataPathOfAutoDelete(recomndation_category.get(position));
            holder.rcv.setLayoutManager(new LinearLayoutManager(context, LinearLayout.HORIZONTAL,false));
            holder.rcv.setAdapter(new Adapter_images(images,recomndation_category.get(position)));
            holder.cat_name.setText(recomndation_category.get(position));
            holder.cat_num.setText(String.valueOf(images.size()));

            holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete_cat = myDb.getUniqueDataPathOfAutoDelete(recomndation_category.get(position));
                    for(String path : delete_cat)
                    {
                        myDB.deleteimagepathFromAutoDelete(path);
                    }
                    UpdateUI();
                }
            });

            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete_cat = myDb.getUniqueDataPathOfAutoDelete(recomndation_category.get(position));
                    delete_flag=1;
                    String recycle_flag = db2.globalgetvalue("Flag_for_recycle");
                    if(recycle_flag.equals("1"))
                    {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoDelete_Recommendation.this);
                        alertDialogBuilder.setMessage("Move to Trash");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        run = new AsyncTaskRunnerForDelete1();
                                        progressDialog.show();
                                        run.execute();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                delete_flag=0;
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                    else{
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoDelete_Recommendation.this);
                        alertDialogBuilder.setMessage("Permanently delete");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        run = new AsyncTaskRunnerForDelete1();
                                        progressDialog.show();
                                        run.execute();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete_flag=0;
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return recomndation_category.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            RecyclerView rcv;
            TextView cat_name,cat_num;
            Button delete_btn,cancel_btn;
            public ViewHolder(View itemView) {
                super(itemView);
                rcv = itemView.findViewById(R.id.image_recomnd);
                cat_name= itemView.findViewById(R.id.category_title_name);
                cat_num= itemView.findViewById(R.id.category_title_number);
                delete_btn = itemView.findViewById(R.id.delete);
                cancel_btn = itemView.findViewById(R.id.cancel);
            }
        }
    }

    class Adapter_images extends RecyclerView.Adapter<Adapter_images.ViewHolder>
    {
        ArrayList<String> al = new ArrayList<>();
        String category;
        Adapter_images(ArrayList<String> a,String catee)
        {
            al = a;
            category = catee;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_rcmd,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Glide.with(context).load(al.get(position)).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(AutoDelete_Recommendation.this,AutoDelete_category.class);
                    i.putExtra("category",category);
                    i.putExtra("change","0");
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return al.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_all);
            }
        }
    }

    public class AsyncTaskRunnerForDelete1 extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.d("hey1","1");
            if(delete_cat.size()!=0){
                Log.d("hey1","2");
                progressDialog.setMax(delete_cat.size());
                for (int i = 0; i < delete_cat.size(); i++) {
                    Log.d("hey1","3");
                    progressDialog.setProgress(i);
                    // Log.d("yess","2");
                    String myPath = delete_cat.get(i);
                    try{

                        String recycle_flag = db2.globalgetvalue("Flag_for_recycle");
                        if(recycle_flag.equals("1"))
                        {
                            Log.d("hey1","4");
                            recyclerbin(myPath);
                            ContentResolver contentResolver = getContentResolver();
                            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                            myDB.deleteimagepath(delete_cat.get(i));
                            myDB.deleteimagepathFromAutoDelete(delete_cat.get(i));
                        }
                        else {
                            ContentResolver contentResolver = getContentResolver();
                            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                            myDB.deleteimagepath(delete_cat.get(i));
                            myDB.deleteimagepathFromAutoDelete(delete_cat.get(i));
                        }


                    }
                    catch(Exception e){
                        // StorageProblem(delete_from_appp.get(i));
                        Log.d("yes",e.getMessage());
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("hey1","5");
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

    public void UpdateUI()
    {
        recomndation_category = myDb.getCategoryFromAutoDeleteSelected();
        if(recomndation_category.size()==0)
        {
            startActivity(new Intent(AutoDelete_Recommendation.this,MainActivity.class));
        }
        adap.notifyDataSetChanged();
    }

    public void recyclerbin(String path){
        Log.d("hey","6");
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
