package com.classify.classify;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    DatabaseHandler myDB,myDB2;
    Context context;
    ArrayList<Classify_path> Specific_data = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    RecyclerView recyclerView_photos;
    Adapter adapter;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
        recyclerView_photos = (RecyclerView) findViewById(R.id.recycler_view_photos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_photos.setLayoutManager(linearLayoutManager);
        recyclerView_photos.setHasFixedSize(true);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"font/montreal.ttf"));

        myDB = new DatabaseHandler(this);
        myDB2 = new DatabaseHandler(this);
        types.add("All");
        types.addAll(myDB.getCategory());

        adapter = new Adapter();
        recyclerView_photos.setAdapter(adapter);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.cat_title.setText(types.get(position));
            holder.cat_rec.setLayoutManager(new LinearLayoutManager(context, LinearLayout.HORIZONTAL,false));
            holder.cat_rec.setAdapter(new Adapter_images(types.get(position)));

        }

        @Override
        public int getItemCount() {
            return types.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView cat_title;
            RecyclerView cat_rec;
            public ViewHolder(View itemView) {
                super(itemView);
                cat_title = (TextView)itemView.findViewById(R.id.cat_title);
                cat_rec = (RecyclerView)itemView.findViewById(R.id.recycle_list);
            }
        }
    }

    class Adapter_images extends RecyclerView.Adapter<Adapter_images.ViewHolder>
    {

        String category;
        Adapter_images(String catee)
        {
            category = catee;
            Specific_data = myDB2.getUniqueData(category);

        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Glide.with(context).load(Specific_data.get(position).getPath()).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return Specific_data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_all);
            }
        }
    }



}
