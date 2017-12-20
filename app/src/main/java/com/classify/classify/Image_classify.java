package com.classify.classify;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class Image_classify extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;
    private RecyclerView mThumbnailRecyclerView;
    private image_adapter mMediaStoreAdapter;
    private AutoCompleteTextView search;
    private TextView dbcount;
    private TextView mediacount;
    private RelativeLayout splash_view;
    private ProgressBar loader_bar;


    RecyclerView recyclerView_types;
    ArrayList<String> types = new ArrayList<>();
    int width,height;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0 ;

    DatabaseHandler databaseHandler;
    private Classifier classifier;
    Runnable runnable;

    Activity mActivity;
    ArrayAdapter<String> searchadapter;

    ArrayList<Classify_path> Specific_data = new ArrayList<>();

    int Mydbcount ;
    int Mediacount ;
    final Handler handler = new Handler();
    File img;
    Cursor cursorcount;
    Cursor resss;
    int num=0;
    int dataIndex;
    String projection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classify);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        dbcount = (TextView) findViewById(R.id.dbcount0);
        mediacount = (TextView) findViewById(R.id.mediacount0);
        splash_view = (RelativeLayout) findViewById(R.id.splash_screen0);
        loader_bar = (ProgressBar) findViewById(R.id.progressBar0);
        types.add("All");
        mActivity = this;
        databaseHandler = new DatabaseHandler(this);

        Mydbcount = databaseHandler.getDataCount();
        try{
            classifier.close();
        }
        catch (Exception e){

        }
        checkReadExternalStoragePermission();

        //to hide focus of search
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        types = databaseHandler.getCategory();

        mThumbnailRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_photos0);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mThumbnailRecyclerView.setHasFixedSize(true);
        initTensorFlowAndLoadModel();
        mThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
        mMediaStoreAdapter = new image_adapter(this,classifier);
        mThumbnailRecyclerView.setAdapter(mMediaStoreAdapter);
        mMediaStoreAdapter.notifyDataSetChanged();
    }

    private void startTimerThread() {
        runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                Mediacount = findcount();

               /* final String[] projection = {MediaStore.Images.Media.DATA};
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                cursorcount = getContentResolver().query(uri, projection, null, null, null);

                if (Mydbcount > Mediacount) {
                    int cntt = Mydbcount;
                    Log.d("plaa", "1");
                    for (int i = 1; i < cntt; i++) {
                        resss = databaseHandler.getPathOfnum(i);

                        if (resss.getString(0).equals("")) {
                            Log.d("Emptyy", "emptyy");
                        } else {
                            for (int j = 0; j < cursorcount.getCount(); j++) {
                                Log.d("plaa", "2");
                                cursorcount.moveToPosition(j);
                                String dataString = cursorcount.getString(dataIndex);
                                Uri mediaUri = Uri.parse("file://" + dataString);
                                File imagePath = new File(mediaUri.getPath());
                                if (resss.getString(0).equals(imagePath.getAbsolutePath())) {
                                    num = 1;
                                }

                                //int num = getALL(resss.getString(0));
                                if (num == 0) {
                                    databaseHandler.getDeleteRow(Mydbcount);
                                }
                                Mydbcount = databaseHandler.getDataCount();
                            }

                        }
                    }
                }
                    if (Mydbcount < Mediacount) {
               */
               while (Mydbcount < Mediacount) {
                   Mydbcount = databaseHandler.getDataCount();
                   handler.post(new Runnable() {
                       public void run() {
                           dbcount.setText(Mydbcount + "");
                           mediacount.setText(Mediacount + "");
                           loader_bar.setMax(Mediacount);
                           loader_bar.setProgress(Mydbcount);
                       }
                   });
               }

               if(Mydbcount>Mediacount)
               {
                   Log.d("tests1","occur");
                   String[] projection = {MediaStore.Images.Media.DATA};
                   Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                   cursorcount = getContentResolver().query(uri, projection, null, null, null);
                   int cntt = Mydbcount;
                   for(int i=0;i<=cntt;i++)
                   {
                       resss = databaseHandler.getPathOfnum(i);
                       dataIndex = cursorcount.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                       if(resss.moveToFirst())
                       {
                           for (int j = 0; j < cursorcount.getCount(); j++) {
                               Log.d("plaa", resss.getString(0));
                               cursorcount.moveToPosition(j);
                               String dataString = cursorcount.getString(dataIndex);
                               Uri mediaUri = Uri.parse("file://" + dataString);
                               File imagePath = new File(mediaUri.getPath());
                               if (resss.getString(0).equals(imagePath.getAbsolutePath())) {
                                   num = 1;
                               }
                               //int num = getALL(resss.getString(0));
                               if (num == 0) {
                                   databaseHandler.getDeleteRow(i);
                               }
                               Mydbcount = databaseHandler.getDataCount();
                               Log.d("tests_updated",Mydbcount+"");
                           }
                       }
                       if(Mediacount==Mydbcount)
                       {
                           break;
                       }
                   }
               }

               handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("tests", "MydbCount "+Mydbcount);
                        Log.d("tests", "Mediacount "+Mediacount);
                        Intent i = new Intent(Image_classify.this, MainActivity.class);
                        startActivity(i);
                    }
                });
            }
          //  }
        };
        new Thread(runnable).start();

    }

    public int getALL(final String DbPath) {
        final int[] flag = {0};
        ///Log.d("plzz",img.getAbsolutePath()+"  hj");

                final int dataIndex = cursorcount.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<cursorcount.getCount();i++)
                        {
                            Log.d("plaa","2");
                            cursorcount.moveToPosition(i);
                            String dataString = cursorcount.getString(dataIndex);
                            Uri mediaUri = Uri.parse("file://" + dataString);
                            File imagePath = new File(mediaUri.getPath());
                            //File file = new File(imagePath.getAbsolutePath());
                            if(DbPath.equals(imagePath.getAbsolutePath()))
                            {
                                flag[0] =1;
                            }
                        }
                        Log.d("plaaa","232");

                    }
                };
                new Thread(r).start();
              Log.d("plaaa","23");
        return flag[0];
    }

    int findcount(){
        //checkReadExternalStoragePermission();
        int count = 0;

        final String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursorcount = getContentResolver().query(uri,projection,null,null,null);

        if(cursorcount!=null)
        {
            count = cursorcount.getCount();
            Log.d("curser"," curser "+count);
        }
        else
            Log.d("curser","no curser");
        cursorcount.close();
        return count;
    }
    private void initTensorFlowAndLoadModel() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
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
//            }
//        });
//        t.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startTimerThread();
                    checkReadExternalStoragePermission();
                    getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Start cursor loader
                startTimerThread();

                getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            // Start cursor loader
            startTimerThread();
           getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        //+ " OR "
        //+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        //+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
//        Log.d("qas","4");
        return new CursorLoader(
                this,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMediaStoreAdapter.changeCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMediaStoreAdapter.changeCursor(null);
    }

}
