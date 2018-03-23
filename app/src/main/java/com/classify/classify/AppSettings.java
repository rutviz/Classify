package com.classify.classify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class AppSettings extends AppCompatActivity {

    Switch aSwitch;
    DatabaseHandler db;
    ImageView home,trash,auto_del,notification,settings;
    int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        home = (ImageView) findViewById(R.id.Home);
        trash = (ImageView) findViewById(R.id.Trash);
        auto_del = (ImageView) findViewById(R.id.AutoDelete);
        notification = (ImageView) findViewById(R.id.Notification);
        settings = (ImageView) findViewById(R.id.Settings);
        final RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.bottom_bar);
        ViewGroup.LayoutParams lp = home.getLayoutParams();
        int pdd = (int) Math.round(width/14.4);
        settings.setPadding(pdd-20,pdd-20,pdd-20,pdd-20);
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
                Intent i = new Intent(AppSettings.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AppSettings.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AppSettings.this,RecycleBin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AppSettings.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        auto_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AppSettings.this,AutoDelete.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AppSettings.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppSettings.this,AppSettings.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AppSettings.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });



    }
}
