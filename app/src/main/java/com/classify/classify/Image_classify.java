package com.classify.classify;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.classify.classify.Classifier.Recognition;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.classify.classify.Global_Share.classifier;
import static com.classify.classify.Global_Share.mmediaStorecursor;

public class Image_classify extends AppCompatActivity  {

    private final static int READ_EXTERNAL_STORAGE_PERMMISSION_RESULT = 0;
    private TextView dbcount;
    private TextView mediacount;
    private Button skip;
    private ProgressBar loader_bar;
    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> Image_path = new ArrayList<>();
    ArrayList<String> Date_list = new ArrayList<>();
    int width,height,notification_rate,total_image;
    String notification_title;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/optimized_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/retrained_labels.txt";
//    private static final int INPUT_SIZE = 224;
//    private static final int IMAGE_MEAN = 117;
//    private static final float IMAGE_STD = 1;
//    private static final String INPUT_NAME = "input";
//    private static final String OUTPUT_NAME = "output";
//    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
//    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0 ;
    DatabaseHandler databaseHandler;
    DatabaseHandler databaseHandlerCount;
    AsyncTaskRunner runner;
    Runnable runnable;
    Activity mActivity;
    int Mydbcount ;
    int Mediacount ;
    final Handler handler = new Handler();
    RelativeLayout relativeLayout;
    final String TAG = "Image_Classify";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classify);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        dbcount = (TextView) findViewById(R.id.dbcount0);
        mediacount = (TextView) findViewById(R.id.mediacount0);
        skip = (Button) findViewById(R.id.skip);
        loader_bar = (ProgressBar) findViewById(R.id.progressBar0);
        mActivity = this;
        databaseHandler = new DatabaseHandler(this);
        databaseHandlerCount = new DatabaseHandler(this);
        relativeLayout = (RelativeLayout)findViewById(R.id.relt);
        Mydbcount = databaseHandler.getDataCount();
        try{
            classifier.close();
        }
        catch (Exception e){
        }
        initTensorFlowAndLoadModel();

        databaseHandler.globaladdData("firstrun","0");
        databaseHandler.globaladdData("Flag_for_recycle","1");
        databaseHandler.AutoDeleteGlobalAddData();
        for(String cate : Global_Share.aList)
        {
            String val = databaseHandler.AutoDeleteGetData(cate);
            Log.d("hey",val + " gh");
        }
        Log.d("globalvalue",databaseHandler.globalgetvalue("firstrun"));
        if(databaseHandler.globalgetvalue("firstrun").equals("1")){
            Log.d("Skip","1");
            Intent i = new Intent(Image_classify.this, PermissionActivity.class);
            startActivity(i);
        }
        else{
            Log.d("Skip","0");
            checkReadExternalStoragePermission();
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runner.cancel(true);
                databaseHandler.globalsetvalue("firstrun","1");
                Intent i = new Intent(Image_classify.this, PermissionActivity.class);
                startActivity(i);
            }
        });

    }

    private void startTimerThread() {
        runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                Mediacount = findcount();
               while (Mydbcount < Mediacount) {
                   Mydbcount = databaseHandlerCount.getDataCount();

                   handler.post(new Runnable() {
                       public void run() {
                           dbcount.setText(Mydbcount + "");
                           mediacount.setText(Mediacount + "");
                           loader_bar.setMax(Mediacount);
                           loader_bar.setProgress(Mydbcount);
                       }
                   });
               }
               handler.post(new Runnable() {
                    @Override
                    public void run() {
                        databaseHandler.globalsetvalue("firstrun","1");
                        Intent i = new Intent(Image_classify.this, PermissionActivity.class);
                        startActivity(i);

        }
    });
}
        };
        new Thread(runnable).start();
    }


    private void addNotification(String title,int rate) {
        String rates = "Total "+rate+ " images Classify out of " +total_image ;
        Intent notificationIntent = new Intent(this, Image_classify.class);
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


    private class AsyncTaskRunner extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {

            int Count_new = findcount();
            total_image = Count_new;
            if(databaseHandler.getDataCount()<1)
            {
                for(int z =0; z < Image_path.size();z++)
                {
                    try {
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Image_path.get(z)), 224, 224);
                        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                        if (results.size() != 0) {
                            databaseHandler.addData(new Classify_path(Image_path.get(z), results.get(0).toString(), Date_list.get(z)));
                            notification_title = "Classify in progress...";
                            notification_rate = z+1;
                            addNotification(notification_title,notification_rate);
                        } else {
                            databaseHandler.addData(new Classify_path(Image_path.get(z), "none", Date_list.get(z)));
                            notification_title = "Classify in progress...";
                            notification_rate = z+1;
                            addNotification(notification_title,notification_rate);
                        }
                    }
                    catch(Exception e){
                        databaseHandler.addData(new Classify_path(Image_path.get(z), "none", Date_list.get(z)));
                        notification_title = "Classify in progress...";
                        notification_rate = z+1;
                        addNotification(notification_title,notification_rate);
                    }
                }
            }
            else
            {
                int new_images = Count_new - databaseHandler.getDataCount();
                total_image = new_images;
                int init = 0;
                while(new_images>0)
                {
                    if(databaseHandler.getpathCount(Image_path.get(init))==0)
                    {
                        try{
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Image_path.get(init)),224,224);
                            final List<Recognition> results = classifier.recognizeImage(bitmap);
                            if(results.size()!=0){
                                databaseHandler.addData(new Classify_path(Image_path.get(init),results.get(0).toString(),Date_list.get(init)));
                                notification_title = "Classify in progress...";
                                notification_rate = init+1;
                                addNotification(notification_title,notification_rate);
                            }
                            else
                            {
                                databaseHandler.addData(new Classify_path(Image_path.get(init),"none",Date_list.get(init)));
                                notification_title = "Classify in progress...";
                                notification_rate = init+1;
                                addNotification(notification_title,notification_rate);
                            }

                        }
                        catch (Exception e)
                        {
                            databaseHandler.addData(new Classify_path(Image_path.get(init),"none",Date_list.get(init)));
                            notification_title = "Classify in progress...";
                            notification_rate = init+1;
                            addNotification(notification_title,notification_rate);
                        }
                    }
                    new_images = Count_new - databaseHandler.getDataCount();
                    init++;
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

    /*void ClassifyImage()
    {
        Runnable classify = new Runnable() {
//            private long startTime = System.currentTimeMillis();
            public void run() {

                int Count_new = findcount();

                if(databaseHandler.getDataCount()<1)
                {
                    for(int z =0; z < Image_path.size();z++)
                    {
                        try {
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Image_path.get(z)), 224, 224);
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                            if (results.size() != 0) {
                                databaseHandler.addData(new Classify_path(Image_path.get(z), results.get(0).toString(), Date_list.get(z)));
                            } else {
                                databaseHandler.addData(new Classify_path(Image_path.get(z), "none", Date_list.get(z)));
                            }
                        }
                        catch(Exception e){
                            databaseHandler.addData(new Classify_path(Image_path.get(z), "none", Date_list.get(z)));
                        }
                    }
                }
                else
                {
                    int new_images = Count_new - databaseHandler.getDataCount();
                    int init = 0;
                    while(new_images>0)
                    {
                        if(databaseHandler.getpathCount(Image_path.get(init))==0)
                        {
                            try{
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Image_path.get(init)),224,224);
                                final List<Recognition> results = classifier.recognizeImage(bitmap);
                                if(results.size()!=0){
                                    databaseHandler.addData(new Classify_path(Image_path.get(init),results.get(0).toString(),Date_list.get(init)));
                                }
                                else
                                {
                                    databaseHandler.addData(new Classify_path(Image_path.get(init),"none",Date_list.get(init)));
                                }

                            }
                            catch (Exception e)
                            {
                                databaseHandler.addData(new Classify_path(Image_path.get(init),"none",Date_list.get(init)));
                            }
                        }
                        new_images = Count_new - databaseHandler.getDataCount();
                        init++;
                    }

                }
            }
        };
        new Thread(classify).start();
    }
*/
    int findcount(){
        int count = 0;
        final String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
        if(mmediaStorecursor!=null)
        {
            count = mmediaStorecursor.getCount();
        }
        else
        mmediaStorecursor.close();
        return count;
    }
    void getPath()
    {
        final String[] projection = {MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mmediaStorecursor = getContentResolver().query(uri,projection,null,null,null);
        if(mmediaStorecursor!=null)
        {
            int dataIndex = mmediaStorecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            for (int i = 0;i<mmediaStorecursor.getCount();i++)
            {
                mmediaStorecursor.moveToPosition(i);
                String dataString = mmediaStorecursor.getString(dataIndex);
                Uri mediaUri = Uri.parse("file://" + dataString);
                File imagePath = new File(mediaUri.getPath());
                Image_path.add(imagePath.getAbsolutePath());
                File file = new File(imagePath.getAbsolutePath());
                Date lastModDate = new Date(file.lastModified());
                String date = lastModDate.getTime() + "";
                Date_list.add(date);

            }
        }
        //ClassifyImage();
        runner = new AsyncTaskRunner();
        runner.execute();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkReadExternalStoragePermission();
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
                startTimerThread();
                getPath();
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMMISSION_RESULT);
            }
        } else {
            startTimerThread();
            getPath();
        }
    }
}
