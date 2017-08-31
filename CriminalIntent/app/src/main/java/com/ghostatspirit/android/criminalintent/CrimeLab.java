package com.ghostatspirit.android.criminalintent;

import android.content.Context;

/**
 * Created by GhostatSpirit on 31/08/2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);

        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){

    }
}
