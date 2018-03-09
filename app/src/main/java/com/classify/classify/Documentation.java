package com.classify.classify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Documentation extends Activity {

    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;
/*
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentation);

        LinearLayout view = (LinearLayout) findViewById(R.id.view);

        /*open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///storage/emulated/0/Download/2170709_30122016_074820AM.pdf
                String path = "/storage/emulated/0/Download/2170709_30122016_074820AM.pdf";
                displayFromAsset(path);
            }
        });*/
        //getting SDcard root path/*
        root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        getfile(root);


        for (int i = 0; i < fileList.size(); i++) {
            final TextView textView = new TextView(this);
            textView.setText(fileList.get(i).getName());
            textView.setPadding(5, 5, 5, 5);

            System.out.println(fileList.get(i).getName());

            if (fileList.get(i).isDirectory()) {
            }
            else
            {
                view.addView(textView);
            }


            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Documentation.this, Document_view.class);
                    intent.putExtra("path",fileList.get(finalI).getAbsolutePath());
                    startActivity(intent);
                }
            });
        }


    }
    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf"))
                    {
                        Log.d("heyy",listFile[i].getAbsolutePath());
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }

   /* private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        File f = new File(pdfFileName);
        pdfView.fromFile(f)
                .defaultPage(1)
                //.showMinimap(false)
                .enableSwipe(true)
                .load();
        *//*pdfView.fromFile(f)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();*//*
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }

    }*/
}