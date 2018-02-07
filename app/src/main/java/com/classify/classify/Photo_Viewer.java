package com.classify.classify;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.StackTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.classify.classify.Global_Share.CurrentCategory;
import static com.classify.classify.Global_Share.paths_of_image;

public class Photo_Viewer extends AppCompatActivity{

    private static int FLAG_POPUP = 0;
    int width,height;
    public ViewPager viewPager;
    RelativeLayout UpperLayer,BottomLayer;
    RecyclerView EditRecycle;
    int flag=0;
    Button btnDelete,btnShare,btnEdit;
    Spinner dropdown;
    ImageView btnInfo;
    ImageView back;
    static TextView category_title;
    DatabaseHandler db;
    static int index;
    int pos;
    String path;
    private ImageView hide,unhide;
    ArrayList<String> category_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__viewer);
//        setDragEdge(DragEdge.BOTTOM);
        db = new DatabaseHandler(Photo_Viewer.this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        dropdown = (Spinner) findViewById(R.id.spinner1);
        category_list = db.getCategory();
        EditRecycle = (RecyclerView) findViewById(R.id.editRecycle);
        EditRecycle.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        Edit_adapter adapters = new Edit_adapter(category_list);
        EditRecycle.setAdapter(adapters);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_list, category_list);
        dropdown.setAdapter(adapter);*/

        UpperLayer = (RelativeLayout) findViewById(R.id.toplayer);
        BottomLayer= (RelativeLayout) findViewById(R.id.bottomlayer);
        category_title = (TextView)findViewById(R.id.category_title);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnInfo = (ImageView) findViewById(R.id.btninfo);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnEdit = (Button) findViewById(R.id.btnEditCategory);
        back = (ImageView) findViewById(R.id.btnBack);
        hide = (ImageView) findViewById(R.id.hide);
        unhide = (ImageView) findViewById(R.id.unhide);

//        category_title = (TextView) findViewById(R.id.category_title);

//        imageView = (ImageView)findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        int id = Integer.parseInt(bundle.getString("id"));
//        String category = bundle.getString("category");
//        category_title.setText(category);
//        Glide.with(this).load(path).into(imageView);

        LayoutParams params = btnShare.getLayoutParams();
        params.width = width/3;
        LayoutParams params2 = btnDelete.getLayoutParams();
        params2.width = width/3;
        btnShare.setLayoutParams(params);
        btnDelete.setLayoutParams(params2);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new Image_View_Adapter(this,Global_Share.paths_of_image));
        viewPager.setCurrentItem(id);
        viewPager.setPageTransformer(true, new StackTransformer());

        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==0)
                {
                    EditRecycle.setVisibility(View.VISIBLE);
                    flag=1;
                }
                else if(flag==1)
                {
                    EditRecycle.setVisibility(View.GONE);
                    flag=0;
                }
            }
        });

        btnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager.getCurrentItem();
                String path = paths_of_image.get(pos);
                File file = new File(path);
                long length = file.length();
                length = length/1024;
                String imagename = file.getName();
                String imagepath = file.getAbsolutePath();
                String dates = getDateCurrentTimeZone(file.lastModified());
                String size = length+"";
                int poss  = viewPager.getCurrentItem();
                Intent i = new Intent(Photo_Viewer.this,ImageInfo.class);
                i.putExtra("name",imagename);
                i.putExtra("path",imagepath);
                i.putExtra("date",dates);
                i.putExtra("size",size);
                i.putExtra("category",db.getSingleCategory(Global_Share.paths_of_image.get(poss)));
                startActivity(i);
            }
        });

        hide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UpperLayer.setVisibility(View.INVISIBLE);
                BottomLayer.setVisibility(View.INVISIBLE);
                unhide.setVisibility(View.VISIBLE);
                Log.d(Global_Share.TAG,"touched");


            }
        });
        unhide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                unhide.setVisibility(View.GONE);
                UpperLayer.setVisibility(View.VISIBLE);
                BottomLayer.setVisibility(View.VISIBLE);
                Log.d(Global_Share.TAG,"touched");
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo_Viewer.this.finish();
            }
        });
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "file://"+Global_Share.paths_of_image.get(viewPager.getCurrentItem());
                Uri mediaUri = Uri.parse("file://"+Global_Share.paths_of_image.get(viewPager.getCurrentItem()));
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared via Classify");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Shared via Classify ");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,mediaUri);
                Photo_Viewer.this.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(Global_Share.TAG,"pager "+viewPager.getCurrentItem());
                int position = viewPager.getCurrentItem();

                String deletePath = Global_Share.paths_of_image.get(position);
                recyclerbin(deletePath);
                ContentResolver contentResolver = Photo_Viewer.this.getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ deletePath });
                db.deleteimagepath(deletePath);
                Global_Share.paths_of_image.remove(deletePath);
                viewPager.setAdapter(new Image_View_Adapter(Photo_Viewer.this,Global_Share.paths_of_image));
                if(Global_Share.paths_of_image.size() == 0)
                    Photo_Viewer.this.finish();
                if(position==Global_Share.paths_of_image.size())
                    viewPager.setCurrentItem(position-1);
                else
                    viewPager.setCurrentItem(position);
            }
        });

        viewPager.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(Global_Share.TAG,"clicked");
                return true;
            }
        });

        viewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global_Share.TAG,"clicked");
            }
        });

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(CurrentCategory.equals("All"))
                {
                    Photo_Viewer.category_title.setText(db.getSingleCategory(Global_Share.paths_of_image.get(position)));
                }
                else
                    Photo_Viewer.category_title.setText(CurrentCategory+"");
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public  String getDateCurrentTimeZone(long timestamp) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        /*try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";*/
        return date;
    }

    public void change_category(String category,String path)
    {
        int position = viewPager.getCurrentItem();
        db.update_category(category,path);
        Global_Share.paths_of_image=db.getUniqueDataPath(Global_Share.CurrentCategory);
        viewPager.setAdapter(new Image_View_Adapter(Photo_Viewer.this,Global_Share.paths_of_image));
        if(Global_Share.paths_of_image.size() == 0)
            Photo_Viewer.this.finish();
        if(position==Global_Share.paths_of_image.size())
            viewPager.setCurrentItem(position-1);
        else
            viewPager.setCurrentItem(position);
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
        Log.d("hey1",timestamp);
        try {
            Log.d("path123",path);
            //create output directory if it doesn't exist
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
            // write the output file
            out.flush();
            out.close();
            out = null;
            Log.d("oldpath",path + "main");
            db.recyclebinaddData(path,timestamp,time,outputPath);
            // delete the original file
            //   new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private class Edit_adapter extends RecyclerView.Adapter<Edit_adapter.ViewHolder>
    {
        ArrayList<String> category_list = new ArrayList<>();

        public Edit_adapter(ArrayList<String> category_list) {
            this.category_list = category_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            holder.editCategory.setText(category_list.get(position));
            holder.editCategory.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = viewPager.getCurrentItem();
                    String paths = paths_of_image.get(pos);
                    String category = category_list.get(position);
                    change_category(category,paths);
                    EditRecycle.setVisibility(View.GONE);
                }
            });
        }
        @Override
        public int getItemCount() {
            return category_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView editCategory;
            public ViewHolder(View itemView) {
                super(itemView);
                editCategory = (TextView)itemView.findViewById(R.id.editText1);

            }
        }
    }




}
