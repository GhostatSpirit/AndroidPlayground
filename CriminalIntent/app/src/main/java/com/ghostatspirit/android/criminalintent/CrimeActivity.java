package com.ghostatspirit.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID =
            "com.ghostatspirit.android.criminalintent.crime_id";
    public static final String EXTRA_IS_FIRST =
            "com.ghostatspirit.android.criminalintent.is_first";
    public static final String EXTRA_IS_LAST =
            "com.ghostatspirit.android.criminalintent.is_last";

    public static Intent newIntent
            (Context packageContext, UUID crimeId, boolean isFirst, boolean isLast){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(EXTRA_IS_FIRST, isFirst);
        intent.putExtra(EXTRA_IS_LAST, isLast);
        return intent;
    }


    @Override
    protected Fragment createFragment(){
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        boolean isFirst = getIntent().getBooleanExtra(EXTRA_IS_FIRST, false);
        boolean isLast = getIntent().getBooleanExtra(EXTRA_IS_LAST, false);
        return CrimeFragment.newInstance(crimeId, isFirst, isLast);
    }
}
