package org.foenix.shufflerecycler;

import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import org.foenix.shufflerecycler.model.Item;
import org.foenix.shufflerecycler.model.ItemCollection;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int LOADER_ID = 1;
    private View mProgressView;
    private RecyclerView mRecycler;
    private RecyclerAdapter mRecyclerAdapter;
    Intent mSaveDataIntent;
    private LoaderManager.LoaderCallbacks<List<Item>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Item>>() {
        @Override
        public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ID) {
                return new ItemLoader(getApplicationContext());
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
            hideProgress();
            ItemCollection items = new ItemCollection(data);
            mRecyclerAdapter.swapData(items);
        }

        @Override
        public void onLoaderReset(Loader<List<Item>> loader) {
            mRecyclerAdapter.emptyData();

        }
    };
    private RecyclerAdapter.IDataChangeListener mDataChangeListener = new RecyclerAdapter.IDataChangeListener() {
        @Override
        public void onMoveItem(Item item) {
            startService(mSaveDataIntent.putExtra("item", item));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.progress_view);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        showProgress();

        mRecyclerAdapter = new RecyclerAdapter(null, mDataChangeListener);
        mRecyclerAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_on_longpress
        // drag & drop manager
        RecyclerViewDragDropManager recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        recyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
        // Start dragging after long press
        recyclerViewDragDropManager.setInitiateOnLongPress(true);
        recyclerViewDragDropManager.setInitiateOnMove(false);


        RecyclerView.Adapter wrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(mRecyclerAdapter);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setAdapter(wrappedAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.drawable.list_divider_h), true));
        recyclerViewDragDropManager.attachRecyclerView(mRecycler);

        getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);

        mSaveDataIntent = new Intent(this, SaveDataService.class);
    }

    public void showProgress() {
        mRecycler.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mRecycler.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
