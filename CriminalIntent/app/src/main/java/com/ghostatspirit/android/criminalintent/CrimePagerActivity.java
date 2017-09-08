package com.ghostatspirit.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by GhostatSpirit on 07/09/2017.
 */

public class CrimePagerActivity extends AppCompatActivity{

    public static final String TAG = "CrimePagerActivity";

    public static final String EXTRA_CRIME_ID =
            "com.ghostatspirit.android.criminalintent.crime_id";

    public static final String EXTRA_MODIFIED_POSITIONS =
            "com.ghostatspirit.android.criminalintent.modified_positions";

    public static final String EXTRA_IS_NEW_CRIME =
            "com.ghostatspirit.android.criminalintent.is_new_crime";

    public static final String EXTRA_UPDATE_DATA_SET =
            "com.ghostatspirit.android.criminalintent.update_data_set";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Set<Integer> mModifiedPositions;
    private boolean mIsNewCrime;

    public static Intent newIntent(Context packageContext, UUID crimeId, boolean isNewCrime){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(EXTRA_IS_NEW_CRIME, isNewCrime);
        return intent;
    }

    public static Set<Integer> getModifiedPositions(Intent result) {
        @SuppressWarnings("unchecked")
        HashSet<Integer> modifiedPositions =
                (HashSet<Integer>) result.getSerializableExtra(EXTRA_MODIFIED_POSITIONS);
        return modifiedPositions;
    }

    public static boolean needUpdateDataSet(Intent result){
        return result.getBooleanExtra(EXTRA_UPDATE_DATA_SET, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mIsNewCrime = getIntent().getBooleanExtra(EXTRA_IS_NEW_CRIME, false);

        mModifiedPositions = new HashSet<>();

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();


        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                // Log.d(TAG, "Getting item " + position);
                boolean isFirst = position == 0;
                boolean isLast = position == (mCrimes.size() - 1);
                return CrimeFragment.newInstance(crime.getId(), isFirst, isLast);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                mModifiedPositions.add(position);
                super.setPrimaryItem(container, position, object);
            }
        });

        for(int i = 0; i < mCrimes.size(); ++i){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                mModifiedPositions.add(i);
                break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(EXTRA_MODIFIED_POSITIONS, (Serializable) mModifiedPositions);
        data.putExtra(EXTRA_UPDATE_DATA_SET, mIsNewCrime);
        setResult(RESULT_OK, data);

        super.onBackPressed();
    }

    public void jumpToFirstPage() {
        mViewPager.setCurrentItem(0);
    }

    public void jumpToLastPage(){
        mViewPager.setCurrentItem(mCrimes.size() - 1);
    }
}
