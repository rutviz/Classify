package com.classify.classify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class AutoDelete extends AppCompatActivity {

    RecyclerView recyclerViewAutoDelete;
    DatabaseHandler db,db2,db3;
    adapter adap;
    String line;
    //AsyncTaskAutoDelete runner;
    ImageButton menu;
    int width,height;
    Button show_recommend;
    ArrayList<String> recomndation_category = new ArrayList<>();
    ImageView home,trash,auto_del,notification,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete);
        show_recommend = (Button) findViewById(R.id.show_recommend_btn);

        int resId1 = R.anim.animate_bottom;
        LayoutAnimationController animation1 = AnimationUtils.loadLayoutAnimation(getBaseContext(), resId1);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        home = (ImageView) findViewById(R.id.Home);
        trash = (ImageView) findViewById(R.id.Trash);
        auto_del = (ImageView) findViewById(R.id.AutoDelete);
        notification = (ImageView) findViewById(R.id.Notification);
        settings = (ImageView) findViewById(R.id.Settings);
        final RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.toolbar2);
        LayoutParams lp = home.getLayoutParams();
        int pdd = (int) Math.round(width/14.4);
        auto_del.setPadding(pdd-20,pdd-20,pdd-20,pdd-20);
        Log.d("widthh",width+"");
        lp.width = width/5;
        lp.height = width/5;
        LayoutParams lp2 = trash.getLayoutParams();
        trash.setPadding(pdd,pdd,pdd,pdd);
        home.setPadding(pdd,pdd,pdd,pdd);
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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete.this,RecycleBin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete.this,bottom_bar, "bottom_tranisition");
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
                Intent i = new Intent(AutoDelete.this,AutoDelete.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AutoDelete.this,AppSettings.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });



        db = new DatabaseHandler(this);
        db2 = new DatabaseHandler(this);
        db3 = new DatabaseHandler(this);
        recomndation_category = db.getCategoryFromAutoDeleteSelected();
        if(recomndation_category.size()==0)
        {
            show_recommend.setVisibility(View.GONE);
        }
        else
        {
            show_recommend.setVisibility(View.VISIBLE);
        }

        show_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AutoDelete.this,AutoDelete_Recommendation.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AutoDelete.this,bottom_bar, "bottom_tranisition");
                startActivity(i,options.toBundle());
            }
        });

/*
        runner = new AsyncTaskAutoDelete();
        runner.execute();*/
        recyclerViewAutoDelete = (RecyclerView)findViewById(R.id.Auto_delete_recycle);

        adap = new adapter(getApplicationContext(),Global_Share.aList);
        recyclerViewAutoDelete.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerViewAutoDelete.setAdapter(adap);
        recyclerViewAutoDelete.setLayoutAnimation(animation1);
    }

/*
    public class AsyncTaskAutoDelete extends AsyncTask<String,String,Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<String> list = new ArrayList<>();
            while (true)
            {
                list = db3.getUniqueDataPath("god");
//                for(String path : list)
//                {
//                    Log.d("cate",path);
//                }
            }
        }
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        runner.cancel(true);
    }

    private class adapter extends RecyclerView.Adapter<adapter.ViewHolder>
    {
        ArrayList<String> al1 = new ArrayList<>();
        Context context;
        adapter(Context c,ArrayList<String> al)
        {
            this.context = c;
            this.al1 = al;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView category;
            Switch onOff;

            public ViewHolder(View itemView) {
                super(itemView);
                category = (TextView) itemView.findViewById(R.id.cateAutoDelete);
                onOff = (Switch) itemView.findViewById(R.id.OnOff);
            }
        }

        @Override
        public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_delete_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final adapter.ViewHolder holder, final int position) {
            holder.category.setText(al1.get(position));
            final String[] val = {db2.AutoDeleteGetData(al1.get(position))};
            if (val[0].equals("2")) {
                holder.onOff.setChecked(true);
            } else {
                holder.onOff.setChecked(false);
            }
            holder.onOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    val[0] = db2.AutoDeleteGetData(al1.get(position));
                    if (val[0].equals("2")) {
                        db2.globalsetvalue(al1.get(position), "0");
                    } else {
                        db2.globalsetvalue(al1.get(position), "2");
                        Intent i = new Intent(AutoDelete.this, AutoDelete_category.class);
                        i.putExtra("category", al1.get(position));
                        i.putExtra("change","1");
                        startActivity(i);
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return al1.size();
        }
    }

}
