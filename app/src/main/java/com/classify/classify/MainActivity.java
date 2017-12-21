package com.classify.classify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

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
    private Classifier classifier;
    Activity mActivity;
    ArrayAdapter<String> searchadapter;
    ArrayList<Classify_path> Specific_data = new ArrayList<>();
    int Mydbcount ;
    int Mediacount ;
    final Handler handler = new Handler();
    List<String> delete_from_appp = new ArrayList<String>();
    List<String> paths_of_image = new ArrayList<String>();

    public static ImageButton delete_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DatabaseHandler(MainActivity.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        delete_btn = (ImageButton) findViewById(R.id.delete);

        delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global_Share.delete_from_app.size()!=0){

                    for (int i = 0; i < Global_Share.delete_from_app.size(); i++) {
                        myDB.deleteimagepath(Global_Share.delete_from_app.get(i));
                    }
                }
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

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String SEARCH =  search.getText().toString();
                    databaseHandler = new DatabaseHandler(mActivity);
                    Specific_data = databaseHandler.getUniqueData(SEARCH);
                    mThumbnailRecyclerView.setAdapter(new image_adapter());

            }
        });
        //initTensorFlowAndLoadModel();
        recyclerView_types.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
//        mMediaStoreAdapter = new MediaStoreAdapter(this,classifier);
        recyclerView_types.setAdapter(new type_adapter(types));
        mThumbnailRecyclerView.setAdapter(new image_adapter());
//        mMediaStoreAdapter.notifyDataSetChanged();

    }

//
//    private void startTimerThread() {
//        Runnable runnable = new Runnable() {
//            private long startTime = System.currentTimeMillis();
//            public void run() {
//                Mediacount = findcount();
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        types = databaseHandler.getCategory();
//                        types.add(0,"All");
//                        recyclerView_types.setAdapter(new type_adapter(types));
//                        searchadapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,types);
//                        search.setAdapter(searchadapter);
//                    }
//                });
//            }
//        };
//        new Thread(runnable).start();
//    }
//
//    int findcount(){
//        int count = 0;
//
//        final String[] projection = {MediaStore.Images.Media.DATA};
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Cursor cursorcount = getContentResolver().query(uri,projection,null,null,null);
//
//        if(cursorcount!=null)
//        {
//            count = cursorcount.getCount();
//        }
//        else
//        cursorcount.close();
//        return count;
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] projection = {
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns.DATE_ADDED,
//                MediaStore.Files.FileColumns.DATA,
//                MediaStore.Files.FileColumns.MEDIA_TYPE
//        };
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
//        return new CursorLoader(
//                this,
//                MediaStore.Files.getContentUri("external"),
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//        );
//
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mMediaStoreAdapter.changeCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mMediaStoreAdapter.changeCursor(null);
//    }
//
//    @Override
//    public void OnClickImage(Uri imageUri) {
//
//    }

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
                        databaseHandler = new DatabaseHandler(mActivity);
                        Specific_data = databaseHandler.getUniqueData(SEARCH);
                        mThumbnailRecyclerView.setAdapter(new image_adapter());
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

            Glide.with(mActivity).load(Specific_data.get(position).getPath()).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Delete_mode[0] == 1) {
                        if (holder.checkBox.isChecked()) {
                            holder.checkBox.setVisibility(View.GONE);
                            holder.checkBox.setChecked(false);
                            delete_from_appp.remove(delete_from_appp.indexOf(new String(paths_of_image.get(position))));
                            Global_Share.delete_from_app = delete_from_appp;
                            if (delete_from_appp.size() == 0) {
                                Delete_mode[0] = 0;
                                MainActivity.delete_btn.setVisibility(View.GONE);
                            }
                        } else {
                            holder.checkBox.setVisibility(View.VISIBLE);
                            holder.checkBox.setChecked(true);
                            delete_from_appp.add(paths_of_image.get(position));
                            Global_Share.delete_from_app = delete_from_appp;
                        }
                    } else {
                        Intent i = new Intent(mActivity, Photo_Viewer.class);
                        i.putExtra("path_of_image", Specific_data.get(position).getPath());
                        i.putExtra("category", databaseHandler.getSingleCategory(Specific_data.get(position).getPath()));
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
                        holder.checkBox.setVisibility(View.VISIBLE);
                        holder.checkBox.setChecked(true);
                        delete_from_appp.add(paths_of_image.get(position));
                        Global_Share.delete_from_app = delete_from_appp;
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
            ImageView image;
            private final CheckBox checkBox;
            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView)itemView.findViewById(R.id.image_all);
                checkBox = (CheckBox) itemView.findViewById(R.id.itemCheckBox);
            }
        }
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    static {
        System.loadLibrary("native-lib");
    }
}
