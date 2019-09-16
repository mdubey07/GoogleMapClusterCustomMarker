package il.co.wwo.mapapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ramkumar on 4/8/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "wnt-map-app";
    private static final String SEARCH_RADIUS ="wnt-search-radius";
    private static final String MAX_RESULT ="wnt-max-result";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setSearchRadius(int radius){
        editor.putInt(SEARCH_RADIUS, radius);
        editor.commit();
    }

    public int getSearchRadius(){
        return pref.getInt(SEARCH_RADIUS, 15);
    }

    public void setMaxResult(int maxResult){

        editor.putInt(MAX_RESULT, maxResult);
        editor.commit();
    }

    public int getMaxResult(){
        return pref.getInt(MAX_RESULT, 20);
    }
}


