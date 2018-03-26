package com.classify.classify;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.classify.classify.Global_Share.CurrentCategory;
import static com.classify.classify.Global_Share.classifier;
import static com.classify.classify.Global_Share.mmediaStorecursor;

public class MainActivity extends AppCompatActivity  {

    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;
    private static final int THUMBNAIL_SIZE = 224;
    private RecyclerView mThumbnailRecyclerView;
    private MediaStoreAdapter mMediaStoreAdapter;
    ImageButton share;
    DatabaseHandler myDB;
    ProgressDialog progressDialog;
    private ImageView search;
    RecyclerView recyclerView_types;
    ArrayList<String> types = new ArrayList<>();
    int width,height;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/optimized_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/retrained_labels.txt";
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    final int[] Delete_mode = {0};
    TextView title;
    MaterialSearchView searchView;

    private BottomSheetBehavior mBottomSheetBehavior;

    //    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
//    private static final String LABEL_FILE =
//            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    // private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0 ;
    DatabaseHandler databaseHandler,db2,dbs;
    DatabaseHandler myDBForRecycle;
    Activity mActivity;
    ArrayAdapter<String> searchadapter;
    ArrayList<Classify_path> Specific_data = new ArrayList<>();
    int Mydbcount,flag_of_activate=0 ;
    int Mediacount ;
    final Handler handler = new Handler();
    List<String> delete_from_appp = new ArrayList<String>();
    List<String> paths_of_image = new ArrayList<String>();
    List<Integer> visible = new ArrayList<Integer>();
    type_adapter typeadapter ;
    AutoCompleteTextView Search_bar;

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
    int total_image;
    ImageButton menu;
    DrawerLayout mDrawerLayout;
    int delete_flag=0;
    NavigationView navigationView;
    AsyncTaskRunner runner;
    ImageView home,trash,auto_del,notification,settings,bck_srch;
    AsyncTaskRunnerForDelete run;
    private TextView bottom_bar_activate;
    private RelativeLayout bottom_sheet;
    private int Bottom_flag = 0;
    private TextView cat_name;
    int count_of_image;
    private RelativeLayout bottom_bar,top_bar;
    private Toolbar mToolbar;


    @Override
    protected void onResume() {
//        navigationView.setCheckedItem(R.id.nav_Images);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkReadExternalStoragePermission();

//        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar_with_search));

        myDB = new DatabaseHandler(MainActivity.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        myDBForRecycle = new DatabaseHandler(MainActivity.this);
        dbs = new DatabaseHandler(MainActivity.this);
        db2 = new DatabaseHandler(MainActivity.this);
        delete_btn = (ImageButton) findViewById(R.id.delete);
        close = (ImageButton) findViewById(R.id.close);
        select_all = (ImageButton) findViewById(R.id.check);
        share = (ImageButton) findViewById(R.id.share);
        menu = (ImageButton) findViewById(R.id.menu_delete);
        bck_srch = (ImageView) findViewById(R.id.back_srch);
        count_selected = (TextView) findViewById(R.id.count_selelcted);
        final RelativeLayout bottom_trans = (RelativeLayout)findViewById(R.id.bottom_sheet);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Moving to trash...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        search = (ImageView)findViewById(R.id.action_search);
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navigationView.setCheckedItem(R.id.nav_Images);
//        title = (TextView) findViewById(R.id.title);
        int resId = R.anim.animate_slide_from_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getBaseContext(), resId);
        int resId1 = R.anim.animate_bottom;
        LayoutAnimationController animation1 = AnimationUtils.loadLayoutAnimation(getBaseContext(), resId1);
        final View bottomsheet = findViewById(R.id.bottom_sheet);
        bottom_sheet = (RelativeLayout)findViewById(R.id.bottom_sheet);
        bottom_bar = (RelativeLayout)findViewById(R.id.relative_bottom);
//        top_bar = (RelativeLayout)findViewById(R.id.search_box);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottom_bar_activate = (TextView) findViewById(R.id.category_name);
        cat_name = (TextView) findViewById(R.id.category_name);

        count_of_image = dbs.getDataCount();
        String catname = "All (" + count_of_image +")";
        cat_name.setText(catname);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        Search_bar = (AutoCompleteTextView) findViewById(R.id.search_bar);
        home = (ImageView) findViewById(R.id.Home);
        trash = (ImageView) findViewById(R.id.Trash);
        auto_del = (ImageView) findViewById(R.id.AutoDelete);
        notification = (ImageView) findViewById(R.id.Notification);
        settings = (ImageView) findViewById(R.id.Settings);

        LayoutParams lp = home.getLayoutParams();
        int pdd = (int) Math.round(width/14.4);
        home.setPadding(pdd-20,pdd-20,pdd-20,pdd-20);
        Log.d("widthh",width+"");
        lp.width = width/5;
        lp.height = width/5;
        LayoutParams lp2 = trash.getLayoutParams();
        trash.setPadding(pdd,pdd,pdd,pdd);
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
                Intent i = new Intent(MainActivity.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        trash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    runner.cancel(true);
                }catch (Exception e)
                {

                }
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent i = new Intent(MainActivity.this,RecycleBin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bottom_bar, "bottom_tranisition");
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
                try {
                    runner.cancel(true);
                }catch (Exception e)
                {

                }
                Intent i = new Intent(MainActivity.this,AutoDelete.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    runner.cancel(true);
                }catch (Exception e)
                {

                }
                Intent i = new Intent(MainActivity.this,AppSettings.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        bck_srch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_bar.setVisibility(View.GONE);
                bck_srch.setVisibility(View.GONE);
            }
        });


//        bottom_bar_activate.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mBottomSheetBehavior.getState() == 3)
//                {
//                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    mBottomSheetBehavior.setPeekHeight(50);
//                }
//                else if(mBottomSheetBehavior.getPeekHeight() == 550){
//                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                }
//                else
//                {
//                    mBottomSheetBehavior.setPeekHeight(550);
//                }
//            }
//        });
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("bottomsheet",slideOffset+" "+Bottom_flag);
//                if(slideOffset == 1.0 && Bottom_flag == 0)
//                {
//                    LayoutParams lp= bottom_sheet.getLayoutParams();
//                    Log.d("bottomsheet",lp.height+" -------------");
//                    lp.height = getWindowManager().getDefaultDisplay().getHeight();
//                    bottom_sheet.setLayoutParams(lp);
//                    Bottom_flag = 1;
//                }
//                else if(slideOffset == 0.0 && Bottom_flag == 1)
//                {
//                    LayoutParams lp= bottom_sheet.getLayoutParams();
//                    Log.d("bottomsheet",lp.height+" ----------------");
//                    lp.height = recyclerView_types.getHeight()+100;
//                    Log.d("bottomsheet",lp.height+"");
//                    bottom_sheet.setLayoutParams(lp);
//                    Bottom_flag = 0;
//                }
//                else
//                {
//                    LayoutParams lp= bottom_sheet.getLayoutParams();
//                    Log.d("bottomsheet",lp.height+" ----------------------------");
//                }
            }
        });
        
        // startActivity(new Intent(MainActivity.this,AutoDelete_Recommendation.class));

        checkPermission();

        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_bar.setVisibility(View.VISIBLE);
                bck_srch.setVisibility(View.VISIBLE);
            }
        });

        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ArrayList<Uri> uris = new ArrayList<>();
                for(int i=0;i<delete_from_appp.size();i++)
                {
                    uris.add(Uri.parse("file://"+delete_from_appp.get(i)));
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared via Classify");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Shared via Classify ");
                //sharingIntent.putExtra(Intent.EXTRA_STREAM,mediaUri);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
                MainActivity.this.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                Category_change();
                imageadapter.notifyDataSetChanged();
            }
        });

        delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_flag=1;
                try {
                    runner.cancel(true);
                }catch (Exception e)
                {

                }
                String recycle_flag = db2.globalgetvalue("Flag_for_recycle");
                if(recycle_flag.equals("1"))
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Move to Trash");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    run = new AsyncTaskRunnerForDelete();
                                    try {
                                        runner.cancel(true);
                                    }catch (Exception e)
                                    {

                                    }
                                    progressDialog.show();
                                    run.execute();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Category_change();
                            imageadapter.notifyDataSetChanged();
                            delete_flag=0;
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
                else{
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Permanently delete");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    run = new AsyncTaskRunnerForDelete();
                                    try {
                                        runner.cancel(true);
                                    }catch (Exception e)
                                    {

                                    }
                                    progressDialog.show();
                                    run.execute();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Category_change();
                            imageadapter.notifyDataSetChanged();
                            delete_flag=0;
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
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
//        search = (AutoCompleteTextView) findViewById(R.id.edit_search_box);
        recyclerView_types = (RecyclerView) findViewById(R.id.recycler_view_type);
        mThumbnailRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbnailRecyclerView.setHasFixedSize(true);
        //to hide focus of search
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        search.clearFocus();
        types.addAll(databaseHandler.getCategory());
        searchadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,types);
//        search.setAdapter(searchadapter);
        Search_bar.setAdapter(searchadapter);
        imageadapter = new image_adapter();

        Search_bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String SEARCH =  Search_bar.getText().toString();
                CurrentCategory = SEARCH;
                databaseHandler = new DatabaseHandler(mActivity);
                Specific_data = databaseHandler.getUniqueData(SEARCH);
                mThumbnailRecyclerView.setAdapter(imageadapter);
                close.callOnClick();

                count_of_image = dbs.getpathCountFromCat(Search_bar.getText().toString());
                String catname = Search_bar.getText().toString() + " (" + count_of_image +")";
                cat_name.setText(catname);
                Search_bar.setText("");
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_change();
                imageadapter.notifyDataSetChanged();
            }
        });

        recyclerView_types.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false));
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
//        mMediaStoreAdapter = new MediaStoreAdapter(this,classifier);
        typeadapter = new type_adapter(types);
        recyclerView_types.setAdapter(typeadapter);
        //recyclerView_types.setLayoutAnimation(animation);
        mThumbnailRecyclerView.setLayoutAnimation(animation1);
        mThumbnailRecyclerView.setAdapter(imageadapter);



//        mMediaStoreAdapter.notifyDataSetChanged();
        mThumbnailRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0  && Delete_mode[0]==0) {
//                    slideUp();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//                    top_bar.setVisibility(View.VISIBLE);
                } else if (dy < 0 ) {
//                    slideDown();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    top_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void hideViews() {
    }

    public void slideUp(){
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }
    public void slideDown(){
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public void AsynctaskAfterPermission()
    {
        final String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(flag==0)
                {
                    initTensorFlowAndLoadModel();
                    flag=1;
                }
                runner = new AsyncTaskRunner();
                runner.execute();
            }
        });
        t.start();


    }

    public  void StorageProblem(final String s)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Image will not been stored in Trash. Are you sure you want to delete?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ s });
                        myDB.deleteimagepath(s);
                        UpdateUI();
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

    private void initTensorFlowAndLoadModel() {
        try {
            Log.d("tensors","init");
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

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_Document)
//        {
//            runner.cancel(true);
//            Intent i = new Intent(MainActivity.this,Documentation.class);
//            startActivity(i);
//        }
//        else if(id == R.id.nav_Images)
//        {
//            Intent i = new Intent(MainActivity.this,MainActivity.class);
//            startActivity(i);
//        }
//        else if (id == R.id.nav_Trash)
//        {
//            runner.cancel(true);
//            Intent i = new Intent(MainActivity.this,RecycleBin.class);
//            startActivity(i);
//        }
//        else if (id == R.id.nav_Auto_delete)
//        {
//            runner.cancel(true);
//            Intent i = new Intent(MainActivity.this,AutoDelete.class);
//            startActivity(i);
//
//        }
//        else if (id == R.id.nav_Notification)
//        {
//
//        }
//        else if (id == R.id.nav_Settings)
//        {
//            runner.cancel(true);
//            Intent i = new Intent(MainActivity.this,AppSettings.class);
//            startActivity(i);
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private void addNotification(String title,int rate) {
        String rates = "Total "+rate+ " images Classify out of " +total_image ;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n  = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(rates)
                .setSmallIcon(R.drawable.logo_white)
                .setContentIntent(contentIntent)
                .setProgress(total_image,rate,true)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText("")).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);

        if(rate == total_image)
        {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            n  = new Notification.Builder(this)
                    .setContentTitle("Classify")
                    .setContentText("All new images are successfully classified.")
                    .setSmallIcon(R.drawable.logo_white)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setStyle(new Notification.BigTextStyle().bigText("")).build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager2.notify(0, n);
        }
    }

    public void AutoDelete()
    {
        ArrayList<String> autoDelete = new ArrayList<>();
        autoDelete = databaseHandler.getCategoryFromAutoDelete();
        for(String ad : autoDelete)
        {
            int countOfCate = databaseHandler.getCategoryCountFromAutoDelete(ad);
            if(countOfCate>5)
            {
                Log.d("countOf",countOfCate+" " +ad);
                Intent notificationIntent = new Intent(this, AutoDelete_Recommendation.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification n  = new Notification.Builder(this)
                        .setContentTitle("Classify")
                        .setContentText("Auto delete image Recommendation")
                        .setSmallIcon(R.drawable.logo_white)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setStyle(new Notification.BigTextStyle().bigText("")).build();
                n.flags |= Notification.FLAG_AUTO_CANCEL;
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, n);
            }
        }
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
                        }
                        else {
                            ContentResolver contentResolver = getContentResolver();
                            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ myPath });
                            myDB.deleteimagepath(delete_from_appp.get(i));
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
            runner = new AsyncTaskRunner();
            runner.execute();
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
    private class AsyncTaskRunner extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {

            while (true) {
                if(isCancelled())
                    break;
                if(delete_flag==0)
                {
                    final String[] projection = {MediaStore.Images.Media.DATA};
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
                    Count_new = mmediaStorecursor.getCount();
                    int dataIndex = mmediaStorecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    paths_of_images = new ArrayList<String>();
                    date_list = new ArrayList<String>();
                    for (int i = 0; i < mmediaStorecursor.getCount(); i++) {
                        mmediaStorecursor.moveToPosition(i);
                        String dataString = mmediaStorecursor.getString(dataIndex);
                        Uri mediaUri = Uri.parse("file://" + dataString);
                        File imagePath = new File(mediaUri.getPath());
                        paths_of_images.add(imagePath.getAbsolutePath());
                    }
                    updatedbimagepath();
                    int new_images = Count_new - myDB.getDataCount();
                    total_image = new_images;
                    int init = 0;
                    List<String> paths_of_image_db = new ArrayList<String>();
                    paths_of_image_db = myDB.getImagepathlist();
                    paths_of_images.removeAll(paths_of_image_db);
                    for (int j =0;j<paths_of_images.size();j++) {
                        Uri mediaUri = Uri.parse(paths_of_images.get(j));
                        File imagePath = new File(mediaUri.getPath());
                        File file = new File(imagePath.getAbsolutePath());
                        Date lastModDate = new Date(file.lastModified());
                        String date = lastModDate.getTime() + "";
//                        Log.d("date",date);
                        date_list.add(date);
                    }

                    while (new_images > 0) {
                        if(delete_flag==1)
                        {
                            break;
                        }
                        if (myDB.getpathCount(paths_of_images.get(init)) == 0) {
                            //Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths_of_images.get(init)), 224, 224);

                            Bitmap bitmap = null;
                            try {
//                                    Log.d("class_e",paths_of_images.get(init).toString());
                                Uri uri1 = Uri.parse("file://"+paths_of_images.get(init));
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri1);
                                bitmap = Bitmap.createScaledBitmap(
                                        bitmap, 224, 224, false);
                                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                                if (results.size() != 0) {
                                    myDB.addData(new Classify_path(paths_of_images.get(init), results.get(0).toString(), date_list.get(init)));
                                    Log.d("counts","13");
                                    String value = myDB.AutoDeleteGetData(results.get(0).toString());
                                    Log.d("counts",value + " 133");
                                    if(value.equals("2"))
                                    {
                                        Log.d("counts","12");
                                        myDB.addDataForAutoDelete(new Classify_path(paths_of_images.get(init), results.get(0).toString(), date_list.get(init)));
                                        flag_of_activate=1;
                                    }
                                    notification_title = "Classify in progress...";
                                    notification_rate = init+1;
                                    addNotification(notification_title,notification_rate);
                                } else {
                                    myDB.addData(new Classify_path(paths_of_images.get(init), "none", date_list.get(init)));
                                    notification_title = "Classify in progress...";
                                    notification_rate = init+1;
                                    addNotification(notification_title,notification_rate);
                                }
                            }
                            catch (Exception e) {
//                                      Log.d("class_e",e.getMessage());
                                myDB.addData(new Classify_path(paths_of_images.get(init), "none", date_list.get(init)));
                                notification_title = "Classify in progress...";
                                notification_rate = init+1;
                                addNotification(notification_title,notification_rate);
                                e.printStackTrace();
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
                                    if(flag_of_activate==1)
                                    {
                                        AutoDelete();
                                        flag_of_activate=0;
                                    }
                                }
                            });

                        }
                    }
                }

            }
            return null;
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
    protected void onPause() {
        super.onPause();
        try {
            runner.cancel(true);
        }catch (Exception e)
        {

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

//                if(type.get(position).equals(CurrentCategory))
//                {
//                    holder.type_name.setBackgroundResource(R.drawable.corner_accent);
//                    holder.type_name.setTextColor(Color.parseColor("#FFFFFF"));
//                    holder.type_name.setTypeface(null, Typeface.BOLD);
//                }
//                else
//                {
//                    holder.type_name.setTextColor(Color.parseColor("#000000"));
//                    holder.type_name.setTypeface(null, Typeface.NORMAL);
//                    holder.type_name.setBackgroundResource(R.drawable.corner_tag);
//                }
                holder.type_name.setText(type.get(position));
                int icon = seticon(type.get(position));
                Glide.with(mActivity).load(icon).fitCenter().into(holder.cat_image_icon);
                holder.count_image.setText(dbs.getpathCountFromCat(type.get(position))+" Images");
                holder.type_name.setTypeface(Typeface.createFromAsset(getAssets(),"montserrat-bold.ttf"));
                holder.card_click.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        search.setText("");
                        if(holder.type_name.getText().equals("All"))
                        {
                            count_of_image = dbs.getDataCount();
                            String catname = "All (" + count_of_image +")";
                            cat_name.setText(catname);
                        }else
                        {
                            count_of_image = dbs.getpathCountFromCat(holder.type_name.getText().toString());
                            String catname = holder.type_name.getText().toString() + " (" + count_of_image +")";
                            cat_name.setText(catname);
                        }
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        String SEARCH = (String) holder.type_name.getText();
                        CurrentCategory = SEARCH;
                        //databaseHandler = new DatabaseHandler(mActivity);
                        Specific_data = databaseHandler.getUniqueData(SEARCH);
                        typeadapter.notifyDataSetChanged();
//                        imageadapter.notifyDataSetChanged();
                        mThumbnailRecyclerView.setAdapter(imageadapter);
                        Category_change();
                    }
                });
            }
            else {
                Glide.with(mActivity).load(R.drawable.classify_logo).asGif().into(holder.imv);
                holder.imv.setVisibility(View.VISIBLE);
            }

        }


        public int seticon(String s)
        {
            int id;
            switch (s)
            {
                case "Animals":
                    id = R.drawable.animal;
                    break;
                case "Appliances":
                    id = R.drawable.appliances;
                    break;
                case "Building":
                    id = R.drawable.building;
                    break;
                case "Documents":
                    id = R.drawable.download;
                    break;
                case "Food":
                    id = R.drawable.food;
                    break;
                case "God":
                    id = R.drawable.god;
                    break;
                case "People":
                    id = R.drawable.people;
                    break;
                case "Meme":
                    id = R.drawable.meme;
                    break;
                case "Nature":
                    id = R.drawable.nature;
                    break;
                case "Person":
                    id = R.drawable.person;
                    break;
                case "Screenshots":
                    id = R.drawable.screenshot;
                    break;
                case "Streets":
                    id = R.drawable.streets;
                    break;
                case "Thoughts":
                    id = R.drawable.thoughts;
                    break;
                case "Vehicles":
                    id = R.drawable.vehicle;
                    break;
                case "Wishes":
                    id = R.drawable.wishes;
                    break;

                case "All":
                    id = R.drawable.all;
                    break;
                case "none":
                    id = R.drawable.none;
                    break;
                default : id = R.drawable.logo;
            }
            return id;
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
            TextView type_name,count_image;
            ImageView imv,cat_image_icon;
            RelativeLayout card_click;

            public ViewHolder(View itemView) {
                super(itemView);
                type_name = (TextView)itemView.findViewById(R.id.type_title);
                count_image = (TextView)itemView.findViewById(R.id.count);
                imv = (ImageView) itemView.findViewById(R.id.loading);
                cat_image_icon = (ImageView) itemView.findViewById(R.id.cat_imagee);
                card_click = (RelativeLayout)itemView.findViewById(R.id.card_types);
            }
        }
    }

    private class image_adapter extends RecyclerView.Adapter<image_adapter.ViewHolder>
    {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            if(visible.get(position)==0)
                holder.chk.setVisibility(View.GONE);
            else
                holder.chk.setVisibility(View.VISIBLE);

//            Log.d(TAG,""+visible.get(position));

            LayoutParams params = holder.image.getLayoutParams();
            params.width = (width-38)/3;
            params.height = (width-38)/3;
            holder.image.setLayoutParams(params);

            LayoutParams params1 = holder.chk.getLayoutParams();
            params1.width = (width-38)/3;
            params1.height = (width-38)/3;
            holder.image.setLayoutParams(params1);

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
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,bottom_bar, "bottom_tranisition");
                        startActivity(i,options.toBundle());
                    }
//                    Log.d(TAG,""+Delete_mode[0]+" "+visible.get(position));
                }
            });
            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(Delete_mode[0] == 0){
                        visible.set(position,1);
                        delete_btn.setVisibility(View.VISIBLE);
                        select_all.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);
                        menu.setVisibility(View.GONE);
                        close.setVisibility(View.VISIBLE);
                        search.setVisibility(View.GONE);
                        Search_bar.setVisibility(View.GONE);
                        bck_srch.setVisibility(View.GONE);
                        count_selected.setVisibility(View.VISIBLE);
                        Delete_mode[0] = 1;
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        Log.d(TAG,position+"");
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
        share.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
//        menu.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        select_all.setVisibility(View.GONE);
        count_selected.setVisibility(View.GONE);
        Search_bar.setVisibility(View.GONE);
        bck_srch.setVisibility(View.GONE);
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
            recyclerView_types.setAdapter(typeadapter);
        }
        for(int i = 0; i<Specific_data.size();i++)
            visible.add(0);
        types.add("All");
        types.addAll(databaseHandler.getCategory());
        typeadapter.notifyDataSetChanged();
        imageadapter.notifyDataSetChanged();

    }
    public void updatedbimagepath() {
        List<String> paths_of_image_db = new ArrayList<String>();
        paths_of_image_db = myDB.getImagepathlist();
        paths_of_image_db.removeAll(paths_of_images);
        if(paths_of_image_db.size()!=0){
            for (int i = 0; i < paths_of_image_db.size(); i++) {
                myDB.deleteimagepath(paths_of_image_db.get(i));
            }
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
            myDBForRecycle.recyclebinaddData(path,timestamp,time,outputPath);
        }
        catch (FileNotFoundException fnfe1) {
        }
        catch (Exception e) {
        }
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

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            MainActivity.this.finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    static {
        System.loadLibrary("native-lib");
    }

    private void checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                AsynctaskAfterPermission();
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            AsynctaskAfterPermission();
        }
    }
    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkReadExternalStoragePermission();
                }
                else
                {checkReadExternalStoragePermission();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
                else
                {
                    Intent i = new Intent(MainActivity.this, PermissionActivity.class);
                    startActivity(i);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;

            default:
                break;
        }
    }*/
}