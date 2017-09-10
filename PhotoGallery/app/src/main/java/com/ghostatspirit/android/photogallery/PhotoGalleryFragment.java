package com.ghostatspirit.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by GhostatSpirit on 08/09/2017.
 */

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private GridLayoutManager mLayoutManager;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final int PAGE_START = 1;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        updateItems();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>(){
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);

        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(mLayoutManager);

        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // variables for endless scrolling
            private int previousTotal = 0;
            private boolean loading = true;
            private int visibleThreshold = 5;
            int firstVisibleItem, visibleItemCount, totalItemCount;


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mPhotoRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem  = mLayoutManager.findFirstVisibleItemPosition();

                if(loading){
                    if(totalItemCount > previousTotal){
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if(!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // end has been reached
                    Log.i(TAG, "end called");

                    updateItems();

                    loading = true;
                }

            }
        });

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        mThumbnailDownloader.clearQueue();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(getActivity(), s);
                resetItems();
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                resetItems();
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemTask(query).execute(currentPage);
        currentPage++;
    }

    private void resetItems(){
        mItems = new ArrayList<>();
        currentPage = PAGE_START;
        mThumbnailDownloader.clearQueue();
        setupAdapter();
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView){
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoholder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);

            Drawable placeholder =
                    ResourcesCompat.getDrawable(getResources(), R.drawable.bill_up_close, null);
            photoholder.bindDrawable(placeholder);

            mThumbnailDownloader.queueThumbnail(photoholder, galleryItem.getUrl());

        }


        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemTask(String query){
            mQuery = query;
        }

        public FetchItemTask(){
            mQuery = null;
        }

        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {

            if(params == null){
                return new ArrayList<>();
            } else if(mQuery == null){
                return new FlickrFetchr().fetchRecentPhotos(params[0]);
            } else {
                return new FlickrFetchr().searchPhotos(mQuery, params[0]);
            }

//
//            if(params == null){
//                return new ArrayList<>();
//            } else {
//                return new FlickrFetchr().fetchItems(params[0]);
//            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            int initialSize = mItems.size();
            int insertedSize = items.size();

            mItems.addAll(items);
            if(mPhotoRecyclerView.getAdapter() == null) {
                setupAdapter();
            } else {
                mPhotoRecyclerView.getAdapter().
                        notifyItemRangeInserted(initialSize, initialSize + insertedSize);
            }
        }
    }
}
