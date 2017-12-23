package com.classify.classify;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutviz Vyas on 21-12-2017.
 */

public class Global_Share {

    static List<String> paths_of_image = new ArrayList<String>();
    static String CurrentCategory = "All";
    static final String TAG = "Image_Classify";
    static Classifier classifier;
    static Cursor mmediaStorecursor;
}
