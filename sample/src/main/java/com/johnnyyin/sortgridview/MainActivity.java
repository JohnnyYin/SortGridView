package com.johnnyyin.sortgridview;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnyyin.sortgridview.library.ChildAnimationController;
import com.johnnyyin.sortgridview.library.SortGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private SortGridView mSortGridView;
    private SortGridViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortGridView = (SortGridView) findViewById(R.id.sort_grid_view);
        initData();
    }

    private void initData() {
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            data.add("item:" + i);
        }
        mAdapter = new SortGridViewAdapter(data);
        mSortGridView.setChildAnimationController(new ChildAnimationController.Builder().numColumns(3)
                .animationDuration(1000).animationType(ChildAnimationController.ANIMATION_TYPE_TRANSLATE)
                .interpolator(new AnticipateOvershootInterpolator(1.0f)).build());
        mSortGridView.setAdapter(mAdapter);
        mSortGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Random random = new Random();
//                int count = 0;
//                while (count++ < 8) {
//                    changeChildPos(random.nextInt(12), random.nextInt(12));
//                }
                if (!mSortGridView.canChangeChildPos()) {
                    Toast.makeText(MainActivity.this, "SortGridView is animating.", Toast.LENGTH_SHORT).show();
                    return;
                }
                changeChildPos(0, 5);
                changeChildPos(2, 3);
                changeChildPos(6, 10);
                changeChildPos(7, 8);
                changeChildPos(9, 1);
                changeChildPos(4, 11);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void changeChildPos(int oldPos, int newPos) {
        if (mSortGridView == null || mAdapter == null) {
            return;
        }
        if (mSortGridView.changeChildPos(oldPos, newPos)) {
            mAdapter.changeChildPos(oldPos, newPos);
        } else {
            Toast.makeText(this, "oldPos = " + oldPos + " newPos = " + newPos + " out visible bounds.", Toast.LENGTH_SHORT).show();
        }
    }

    private class SortGridViewAdapter extends BaseAdapter {
        private List<String> mData;

        public SortGridViewAdapter(List<String> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            if (position < 0 || position >= mData.size()) {
                return null;
            }
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getBaseContext(), R.layout.grid_item, null);
            }
            ((TextView) convertView.findViewById(R.id.content)).setText(getItem(position));
            convertView.setTag(Integer.valueOf(position));
            return convertView;
        }

        public void changeChildPos(int oldPos, int newPos) {
            if (oldPos == newPos) {
                return;
            }
            int count = getCount();
            if (oldPos < 0 || oldPos >= count || newPos < 0 || newPos >= count) {
                return;
            }
            mData.set(newPos, mData.set(oldPos, mData.get(newPos)));
        }
    }

    private void log(String log) {
        if (TextUtils.isEmpty(log))
            return;
        if (BuildConfig.DEBUG) {
            Log.i(SortGridView.TAG, log);
        }
    }

}
