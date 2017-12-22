package com.classify.classify;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
    List<Integer> visible = new ArrayList<Integer>();
    type_adapter type ;

    public static ImageButton delete_btn,select_all,close;
    TextView count_selected;
    final String TAG = "Image_Classify";
    String CurrentCategory = "All";
    image_adapter imageadapter;
    int all_selected=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DatabaseHandler(MainActivity.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        delete_btn = (ImageButton) findViewById(R.id.delete);
        close = (ImageButton) findViewById(R.id.close);
        select_all = (ImageButton) findViewById(R.id.check);
        count_selected = (TextView) findViewById(R.id.count_selelcted);

        delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    databaseHandler = new DatabaseHandler(mActivity);
                    Specific_data = databaseHandler.getUniqueData(SEARCH);
                    mThumbnailRecyclerView.setAdapter(imageadapter);
                    close.callOnClick();
            }
        });
        //initTensorFlowAndLoadModel();
        recyclerView_types.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
//        mMediaStoreAdapter = new MediaStoreAdapter(this,classifier);
        type = new type_adapter(types);
        recyclerView_types.setAdapter(type);
        mThumbnailRecyclerView.setAdapter(imageadapter);
//        mMediaStoreAdapter.notifyDataSetChanged();

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
                        i.putExtra("path_of_image", Specific_data.get(position).getPath());
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
        }
        for(int i = 0; i<Specific_data.size();i++)
            visible.add(0);
        types.add("All");
        types.addAll(databaseHandler.getCategory());
        type.notifyDataSetChanged();
        imageadapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    static {
        System.loadLibrary("native-lib");
    }
}
