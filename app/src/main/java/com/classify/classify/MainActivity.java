package com.classify.classify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.classify.classify.Global_Share.CurrentCategory;
import static com.classify.classify.Global_Share.classifier;
import static com.classify.classify.Global_Share.mmediaStorecursor;

public class MainActivity extends AppCompatActivity  {

    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;
    private RecyclerView mThumbnailRecyclerView;
    private MediaStoreAdapter mMediaStoreAdapter;
    DatabaseHandler myDB;
    private AutoCompleteTextView search;
    RecyclerView recyclerView_types;
    ArrayList<String> types = new ArrayList<>();
    int width,height;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    final int[] Delete_mode = {0};

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0 ;
    DatabaseHandler databaseHandler;
    Activity mActivity;
    ArrayAdapter<String> searchadapter;
    ArrayList<Classify_path> Specific_data = new ArrayList<>();
    int Mydbcount ;
    int Mediacount ;
    final Handler handler = new Handler();
    List<String> delete_from_appp = new ArrayList<String>();
    List<String> paths_of_image = new ArrayList<String>();
    List<Integer> visible = new ArrayList<Integer>();
    type_adapter type ;

    public static ImageButton delete_btn,select_all,close;
    TextView count_selected;
    final String TAG = "Image_Classify";
    ArrayList<String> date_list = new ArrayList<>();
    List<String> paths_of_images = new ArrayList<String>();
    image_adapter imageadapter;
    int all_selected=0,flag=0;
    int Count_new;
    Thread t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DatabaseHandler(MainActivity.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        final String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
        delete_btn = (ImageButton) findViewById(R.id.delete);
        close = (ImageButton) findViewById(R.id.close);
        select_all = (ImageButton) findViewById(R.id.check);
        count_selected = (TextView) findViewById(R.id.count_selelcted);
        //myDB.createtable();


        t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(flag==0)
                {
                    initTensorFlowAndLoadModel();
                    flag=1;
                }
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
            }
        });
        t.start();

        delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Are you sure you want to delete all photos");
                            alertDialogBuilder.setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            if(delete_from_appp.size()!=0){

                                                for (int i = 0; i < delete_from_appp.size(); i++) {

                                                    String myPath = delete_from_appp.get(i);
                                                    ContentResolver contentResolver = getContentResolver();
                                                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                            MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                                                    myDB.deleteimagepath(delete_from_appp.get(i));
                                                }
                                                UpdateUI();
                                            }
                                        }
                                    });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Category_change();
                            imageadapter.notifyDataSetChanged();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


            }
        });

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_change();
                imageadapter.notifyDataSetChanged();
            }
        });

        select_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(all_selected==0)
                {
                    select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_white_24dp));
                    visible.clear();
                    delete_from_appp.clear();
                    for(int i = 0; i<Specific_data.size();i++)
                    {
                        visible.add(1);
                        delete_from_appp.add(Specific_data.get(i).getPath());
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
                Log.d(TAG,delete_from_appp.size()+" "+Delete_mode[0]+" "+Specific_data.size()+" "+visible.size()+" "+all_selected);

                for(int i :visible)
                Log.d(TAG,""+i);
            }
        });

        types.add("All");
        mActivity = this;
        databaseHandler = new DatabaseHandler(this);
        Mydbcount = databaseHandler.getDataCount();
        try{
            classifier.close();
        }
        catch (Exception e){

        }
        Specific_data = databaseHandler.getUniqueData("All");
        paths_of_image = databaseHandler.getImagepathlist();

//        getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
        search = (AutoCompleteTextView) findViewById(R.id.edit_search_box);
        recyclerView_types = (RecyclerView) findViewById(R.id.recycler_view_type);
        mThumbnailRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbnailRecyclerView.setHasFixedSize(true);
        //to hide focus of search
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        search.clearFocus();
        types.addAll(databaseHandler.getCategory());
        searchadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,types);
        search.setAdapter(searchadapter);
        imageadapter = new image_adapter();
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String SEARCH =  search.getText().toString();
                    CurrentCategory = SEARCH;
                    databaseHandler = new DatabaseHandler(mActivity);
                    Specific_data = databaseHandler.getUniqueData(SEARCH);
                    mThumbnailRecyclerView.setAdapter(imageadapter);
                    close.callOnClick();
            }
        });

        recyclerView_types.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
//        mMediaStoreAdapter = new MediaStoreAdapter(this,classifier);
        type = new type_adapter(types);
        recyclerView_types.setAdapter(type);
        mThumbnailRecyclerView.setAdapter(imageadapter);
//        mMediaStoreAdapter.notifyDataSetChanged();


    }

    private void initTensorFlowAndLoadModel() {
        try {
            classifier = TensorFlowImageClassifier.create(
                    getAssets(),
                    MODEL_FILE,
                    LABEL_FILE,
                    INPUT_SIZE,
                    IMAGE_MEAN,
                    IMAGE_STD,
                    INPUT_NAME,
                    OUTPUT_NAME);
        }
        catch (final Exception e) {
            throw new RuntimeException("Error initializing TensorFlow!", e);
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {

            while (true) {
                final String[] projection = {MediaStore.Images.Media.DATA};
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
                Count_new = mmediaStorecursor.getCount();
                int dataIndex = mmediaStorecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                paths_of_images = new ArrayList<String>();
                Log.d("c1124","beforesize"+paths_of_images.size());
                date_list = new ArrayList<String>();
                for (int i = 0; i < mmediaStorecursor.getCount(); i++) {
                    mmediaStorecursor.moveToPosition(i);
                    String dataString = mmediaStorecursor.getString(dataIndex);
                    Uri mediaUri = Uri.parse("file://" + dataString);
                    File imagePath = new File(mediaUri.getPath());
                                        paths_of_images.add(imagePath.getAbsolutePath());
                }
//               Log.d("currentmedia: ",Count_new+"");
                updatedbimagepath();
                int new_images = Count_new - myDB.getDataCount();
  //              Log.d("new_media: ",new_images+"");
                int init = 0;
             List<String> paths_of_image_db = new ArrayList<String>();
             paths_of_image_db = myDB.getImagepathlist();
//                Log.d("c1124","db_  Size: "+paths_of_image_db.size());
//                Log.d("c1124","im_  Size: "+paths_of_images.size());
             paths_of_images.removeAll(paths_of_image_db);
             Log.d("c1124","Size: "+paths_of_images.size());
                for (int j =0;j<paths_of_images.size();j++) {
                    Uri mediaUri = Uri.parse(paths_of_images.get(j));
                    File imagePath = new File(mediaUri.getPath());
                    File file = new File(imagePath.getAbsolutePath());
                    Date lastModDate = new Date(file.lastModified());
                    String date = lastModDate.getTime() + "";
                    date_list.add(date);
                }

                while (new_images > 0) {
                  //  Log.d("heyy","1");
//                    Log.d("c1124",init+"");
                    if (myDB.getpathCount(paths_of_images.get(init)) == 0) {
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_images.get(init)), 224, 224);
                        try {
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                            if (results.size() != 0) {
                                myDB.addData(new Classify_path(paths_of_images.get(init), results.get(0).toString(), date_list.get(init)));
//                                Log.d("class","category:"+results.get(0).toString());
                            } else {
                                myDB.addData(new Classify_path(paths_of_images.get(init), "none", date_list.get(init)));
                            }
                      //      Log.d("CLassifying", results.toString() + " " + paths_of_images.get(init));
                        } catch (Exception e) {
                            myDB.addData(new Classify_path(paths_of_images.get(init), "none", date_list.get(init)));
                        }
                    }

                    new_images = Count_new - myDB.getDataCount();
                    init++;
                    if(new_images==0)
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UpdateLists();
                                imageadapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }


        @Override
        protected void onPostExecute(Void result) {

        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
    
    @Override
    protected void onPostResume() {
        super.onPostResume();
        handler.post(new Runnable() {
            @Override
            public void run() {
                UpdateLists();
                imageadapter.notifyDataSetChanged();
            }
        });
    }
    private class type_adapter extends RecyclerView.Adapter<type_adapter.ViewHolder>
    {
        ArrayList<String> type = new ArrayList<>();
        type_adapter(ArrayList<String> item)
        {
            this.type = item;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if(type.size()!=0)
           {
             view= LayoutInflater.from(parent.getContext()).inflate(R.layout.type_card, parent, false);}
            else
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            }
            return  new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(type.size()!=0)
            {
                holder.type_name.setText(type.get(position));
                holder.type_name.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        search.setText("");
                        String SEARCH = (String) holder.type_name.getText();
                        CurrentCategory = SEARCH;
                        //databaseHandler = new DatabaseHandler(mActivity);
                        Specific_data = databaseHandler.getUniqueData(SEARCH);
                        imageadapter.notifyDataSetChanged();
                        Category_change();
                    }
                });
            }
            else {
                Glide.with(mActivity).load(R.drawable.classify_logo).asGif().into(holder.imv);
                holder.imv.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {

            if(type.size()!=0)
                return type.size();
            else
                return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView type_name;
            ImageView imv;

            public ViewHolder(View itemView) {
                super(itemView);
                type_name = (TextView)itemView.findViewById(R.id.type_title);
                imv = (ImageView) itemView.findViewById(R.id.loading);
            }
        }
    }

    private class image_adapter extends RecyclerView.Adapter<image_adapter.ViewHolder>
    {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            if(visible.get(position)==0)
                holder.chk.setVisibility(View.GONE);
            else
                holder.chk.setVisibility(View.VISIBLE);

            Log.d(TAG,""+visible.get(position));

            Glide.with(mActivity).load(Specific_data.get(position).getPath()).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Delete_mode[0] == 1) {
                        if (visible.get(position)==1) {
                            holder.chk.setVisibility(View.GONE);
                            visible.set(position,0);
                            delete_from_appp.remove(delete_from_appp.indexOf(new String(Specific_data.get(position).getPath())));
                            select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
                            all_selected=0;
                            if (delete_from_appp.size() == 0) {
                                Delete_mode[0] = 0;
                                Category_change();
                            }
                        } else {
                            holder.chk.setVisibility(View.VISIBLE);
                            delete_from_appp.add(Specific_data.get(position).getPath());
                            visible.set(position,1);
//                            Global_Share.delete_from_app = delete_from_appp;
                        }
                        count_selected.setText(delete_from_appp.size()+"");
                    } else {
                        Intent i = new Intent(mActivity, Photo_Viewer.class);
                        Global_Share.paths_of_image = convert(Specific_data);
                        i.putExtra("id", position+"");
                        i.putExtra("category", databaseHandler.getSingleCategory(Specific_data.get(position).getPath()));
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
                        select_all.setVisibility(View.VISIBLE);
                        close.setVisibility(View.VISIBLE);
                        count_selected.setVisibility(View.VISIBLE);
                        Delete_mode[0] = 1;
                        Log.d(TAG,position+"");
                        holder.chk.setVisibility(View.VISIBLE);
                        delete_from_appp.add(Specific_data.get(position).getPath());
                        count_selected.setText(delete_from_appp.size()+"");
                    }
                    return true;
                }
            });
        }
        @Override
        public int getItemCount() {
            return Specific_data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView image,chk;
             CheckBox checkBox;
            public ViewHolder(View itemView) {
                super(itemView);
                for(int i = 0; i<Specific_data.size();i++)
                    visible.add(0);
                image = (ImageView)itemView.findViewById(R.id.image_all);
                chk = (ImageView) itemView.findViewById(R.id.isSelected);
            }
        }
    }

    void Category_change()
    {
        visible.clear();
        delete_from_appp.clear();
        for(int i = 0; i<Specific_data.size();i++)
            visible.add(0);
       hide_button();
    }
    void hide_button()
    {
        select_all.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_check_box_outline_blank_white_24dp));
        delete_btn.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
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
        types.clear();
        visible.clear();
        Specific_data = databaseHandler.getUniqueData(CurrentCategory);
        if(Specific_data.size()==0)
        {
            Specific_data = databaseHandler.getUniqueData("All");
            CurrentCategory = "All";
            recyclerView_types.setAdapter(type);
        }
        for(int i = 0; i<Specific_data.size();i++)
            visible.add(0);
        types.add("All");
        types.addAll(databaseHandler.getCategory());
        type.notifyDataSetChanged();
        imageadapter.notifyDataSetChanged();

    }
    public void updatedbimagepath() {
        List<String> paths_of_image_db = new ArrayList<String>();
        paths_of_image_db = myDB.getImagepathlist();
//        Log.d("c1124","update_db_Size: "+paths_of_image_db.size());
//        Log.d("c1124","update_im_Size: "+paths_of_images.size());
        paths_of_image_db.removeAll(paths_of_images);
//        Log.d("c1124","update_db_Size: "+paths_of_image_db.size());
        if(paths_of_image_db.size()!=0){
            for (int i = 0; i < paths_of_image_db.size(); i++) {
                myDB.deleteimagepath(paths_of_image_db.get(i));
            }
        }
    }

    List<String> convert(ArrayList<Classify_path> Specific)
    {
        List<String> temp = new ArrayList<>();
        for(int i=0;i<Specific.size();i++)
        {
            temp.add(Specific.get(i).getPath());
        }
        Log.d(TAG,temp.size()+" Main");
        return temp;
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finishAffinity();
    }
    static {
        System.loadLibrary("native-lib");
    }
}

