package com.classify.classify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AppSettings extends AppCompatActivity {

    Switch aSwitch;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        db = new DatabaseHandler(this);
        aSwitch = (Switch)findViewById(R.id.recycleSwitch);
        String recycle_flags = db.globalgetvalue("Flag_for_recycle");
        if(recycle_flags.equals("0"))
        {
            aSwitch.setChecked(false);
        }
        else {
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String recycle_flag = db.globalgetvalue("Flag_for_recycle");
                if(recycle_flag.equals("0"))
                {
                    db.globalEditaddData("Flag_for_recycle","1");
                }
                else {
                    db.globalEditaddData("Flag_for_recycle","0");
                }
            }
        });


    }
}
