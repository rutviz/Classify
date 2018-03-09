package com.classify.classify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class AutoDelete extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerViewAutoDelete;
    DatabaseHandler db,db2,db3;
    adapter adap;
    String line;
    //AsyncTaskAutoDelete runner;
    ImageButton menu;
    DrawerLayout mDrawerLayout;
    Button show_recommend;
    private NavigationView navigationView;
    ArrayList<String> recomndation_category = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete);
        show_recommend = (Button) findViewById(R.id.show_recommend_btn);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_Auto_delete);

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
                    startActivity(new Intent(AutoDelete.this,AutoDelete_Recommendation.class));
            }
        });

/*
        runner = new AsyncTaskAutoDelete();
        runner.execute();*/
        recyclerViewAutoDelete = (RecyclerView)findViewById(R.id.Auto_delete_recycle);
        menu = (ImageButton) findViewById(R.id.menu_auto_delete1);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        adap = new adapter(getApplicationContext(),Global_Share.aList);
        recyclerViewAutoDelete.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerViewAutoDelete.setAdapter(adap);
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_Document)
        {
            // Handle the camera action
        }
        else if(id == R.id.nav_Images)
        {
            Intent i = new Intent(AutoDelete.this,MainActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_Trash)
        {
//            runner.cancel(true);
            Intent i = new Intent(AutoDelete.this,RecycleBin.class);
            startActivity(i);
        }
        else if (id == R.id.nav_Auto_delete)
        {
//            runner.cancel(true);
            Intent i = new Intent(AutoDelete.this,AutoDelete.class);
            startActivity(i);

        }
        else if (id == R.id.nav_Notification)
        {

        }
        else if (id == R.id.nav_Settings)
        {
//            runner.cancel(true);
            Intent i = new Intent(AutoDelete.this,AppSettings.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
