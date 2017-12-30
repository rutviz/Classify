package com.classify.classify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.classify.classify.Global_Share.CurrentCategory;

public class RecycleBin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DatabaseHandler myDb;private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
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

    public static ImageButton delete_btn,select_all,close;
    TextView count_selected;
    final String TAG = "Image_Classify";
    ArrayList<String> date_list = new ArrayList<>();
    List<String> paths_of_images = new ArrayList<String>();
    image_adapter imageadapter;
    int all_selected=0,flag=0;
    int Count_new;
    Thread t;
    String notification_title = "Classify in progress...";
    int notification_rate = 0;
    DrawerLayout mDrawerLayout;
    int total_image;
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);
        myDb = new DatabaseHandler(this);
        paths_of_image = myDb.recyclebingetdata();
        mActivity = this;
        mThumbnailRecyclerView = (RecyclerView) findViewById(R.id.recycler__recycle_view_photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbnailRecyclerView.setHasFixedSize(true);
        imageadapter = new image_adapter();
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
        mThumbnailRecyclerView.setAdapter(imageadapter);
        delete_btn = (ImageButton) findViewById(R.id.delete);
        close = (ImageButton) findViewById(R.id.close);
        select_all = (ImageButton) findViewById(R.id.check);
        count_selected = (TextView) findViewById(R.id.count_selelcted);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menu = (ImageButton) findViewById(R.id.menu_delete);
        navigationView.setCheckedItem(R.id.nav_Trash);

        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_change();
                imageadapter.notifyDataSetChanged();
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
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_Document)
        {
            // Handle the camera action
        }
        else if(id == R.id.nav_Images)
        {
            Intent i = new Intent(RecycleBin.this,MainActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_Trash)
        {
            Intent i = new Intent(RecycleBin.this,RecycleBin.class);
            startActivity(i);
        }
        else if (id == R.id.nav_Auto_delete)
        {

        }
        else if (id == R.id.nav_Notification)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        Intent i = new Intent(mActivity, Photo_Viewer.class);
                        Global_Share.paths_of_image = paths_of_image;
                        i.putExtra("id", position+"");
                        i.putExtra("category","");
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
                        delete_from_appp.add(paths_of_image.get(position));
                        count_selected.setText(delete_from_appp.size()+"");
                    }
                    return true;
                }
            });
        }
        @Override
        public int getItemCount() {
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
        imageadapter.notifyDataSetChanged();
    }
}
