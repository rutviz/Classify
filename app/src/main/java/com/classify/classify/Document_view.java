package com.classify.classify;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class Document_view extends Activity implements OnPageChangeListener,OnLoadCompleteListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);
        pdfView = (PDFView)findViewById(R.id.pdfView);
        String paths = getIntent().getExtras().getString("path");
        Log.d("pathss",paths);
        displayFromAsset(paths);
    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        File f = new File(pdfFileName);
        pdfView.fromFile(f)
                .defaultPage(1)
                //.showMinimap(false)
                .enableSwipe(true)
                .load();
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

    }
}
