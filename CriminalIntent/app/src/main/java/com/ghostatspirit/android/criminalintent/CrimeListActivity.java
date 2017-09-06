package com.ghostatspirit.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by lykav on 9/6/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
