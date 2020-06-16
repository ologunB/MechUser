package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private Context mContext;

    public SharedPreferencesUtil(Context context){
        this.mContext = context;
    }

    private SharedPreferences getTermsSharedPrefs(){
        return mContext.getSharedPreferences("terms", mContext.MODE_PRIVATE);
    }

    public Boolean clickYes(){
        SharedPreferences.Editor editor = getTermsSharedPrefs().edit();
        editor.putString("clickedTerms", "yes");
        return editor.commit();
    }

    public Boolean hasUserClickedYes(){
        String hasClicked = getTermsSharedPrefs().getString("clickedTerms", "");
        return hasClicked.equalsIgnoreCase("yes") ;
    }
}
