package com.johnnyyin.sortgridview;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnyyin.sortgridview.library.AbsoluteExchangeChildAnimationController;
import com.johnnyyin.sortgridview.library.AlphaChildAnimationController;
import com.johnnyyin.sortgridview.library.AnimationInfo;
import com.johnnyyin.sortgridview.library.ExchangeChildAnimationController;
import com.johnnyyin.sortgridview.library.SortGridView;
import com.johnnyyin.sortgridview.library.WaterFallChildAnimationController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private SortGridView mSortGridView;
    private SortGridViewAdapter mAdapter;
    private ExchangeChildAnimationController mExchangeChildAnimationController;
    private WaterFallChildAnimationController mWaterFallChildAnimationController;
    private AbsoluteExchangeChildAnimationController mAbsoluteExchangeChildAnimationController;
    private AlphaChildAnimationController mAlphaChildAnimationController;
    private int mNumColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortGridView = (SortGridView) findViewById(R.id.sort_grid_view);
        initData();
    }

    private void initData() {
        mNumColumns = 3;
        int baseDuration = 600;
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                log("animation start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                log("animation end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Interpolator interpolator = new AnticipateOvershootInterpolator(1.0f);
        mExchangeChildAnimationController = new ExchangeChildAnimationController.Builder()
                .numColumns(mNumColumns)
                .animationDuration(baseDuration)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mAbsoluteExchangeChildAnimationController = new AbsoluteExchangeChildAnimationController.Builder()
                .sortGridView(mSortGridView)
                .numColumns(mNumColumns)
                .animationDuration(baseDuration)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mWaterFallChildAnimationController = new WaterFallChildAnimationController.Builder()
                .sortGridView(mSortGridView)
                .numColumns(mNumColumns)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mAlphaChildAnimationController = new AlphaChildAnimationController.Builder()
                .numColumns(mNumColumns)
                .animationListener(animationListener).build();

        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 99; i++) {
            data.add("item:" + i);
        }
        mAdapter = new SortGridViewAdapter(data);
        mSortGridView.setClipToPadding(false);
        mSortGridView.setAdapter(mAdapter);
        mSortGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSortGridView.isChildAnimating()) {
                    mSortGridView.clearAnimation();
                }
                int max = mSortGridView.getLastVisiblePosition() + 1;
                Random random = new Random();
                switch (position % 4) {
                    case 0:
                        mSortGridView.setChildAnimationController(mAbsoluteExchangeChildAnimationController);
                        int count = 0;
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        while (count < max / 2) {
                            int oldPos;
                            do {
                                oldPos = random.nextInt(max);
                            } while (list.contains(oldPos));
                            list.add(oldPos);
                            int newPos;
                            do {
                                newPos = random.nextInt(max);
                            } while (list.contains(newPos));
                            list.add(newPos);
                            testExchangeAnimation(mAbsoluteExchangeChildAnimationController, oldPos, newPos, 0);
                            count++;
                        }
                        break;
                    case 1:
                        mSortGridView.setChildAnimationController(mWaterFallChildAnimationController);
                        for (int i = 0; i < max; i++) {
                            testWaterFallAnimation(mWaterFallChildAnimationController, i, ((max - 1 - i) / 3) * 50);
                        }
                        break;
                    case 2:
                        ExchangeChildAnimationController controller = mAbsoluteExchangeChildAnimationController;
//                        ExchangeChildAnimationController controller = mExchangeChildAnimationController;
                        mSortGridView.setChildAnimationController(controller);
                        testExchangeAnimation2(controller, 0, 1, 0);
                        testExchangeAnimation2(controller, 1, 2, 0);
                        testExchangeAnimation2(controller, 2, 3, 0);
                        testExchangeAnimation2(controller, 3, 4, 0);
                        testExchangeAnimation2(controller, 4, 5, 0);
                        testExchangeAnimation2(controller, 5, 0, 0);
                        break;
                    case 3:
                        mSortGridView.setChildAnimationController(mAlphaChildAnimationController);
                        for (int i = 0; i < max; i++) {
                            testAlphaAnimation(mAlphaChildAnimationController, i, i * 60);
                        }
                        break;
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mSortGridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSortGridView.performItemClick(null, 1, 0);
            }
        }, 10);
    }

    public boolean testExchangeAnimation(ExchangeChildAnimationController controller, int pos1, int pos2, long startOffset) {
        mAdapter.changeChildPos(pos1, pos2);
        int firstPosition = mSortGridView.getFirstVisiblePosition();
        int lastPosition = mSortGridView.getLastVisiblePosition();
        if (pos2 < firstPosition || pos2 > lastPosition || pos1 < firstPosition || pos1 > lastPosition) {
            return false;
        }
        controller.changePos(pos1, pos2, startOffset);
        return true;
    }

    public boolean testExchangeAnimation2(ExchangeChildAnimationController controller, int pos1, int pos2, long startOffset) {
        mAdapter.changeChildPos(pos1, pos2);
        int firstPosition = mSortGridView.getFirstVisiblePosition();
        int lastPosition = mSortGridView.getLastVisiblePosition();
        if (pos2 < firstPosition || pos2 > lastPosition || pos1 < firstPosition || pos1 > lastPosition) {
            return false;
        }
        controller.setChildAnimation(pos2, new ExchangeChildAnimationController.ExchangeAnimationInfo(pos1, null, startOffset));
        return true;
    }

    public boolean testWaterFallAnimation(WaterFallChildAnimationController controller, int position, int startOffset) {
        controller.setChildAnimation(position, new AnimationInfo(startOffset));
        return true;
    }

    public boolean testAlphaAnimation(AlphaChildAnimationController controller, int position, int startOffset) {
        controller.setChildAnimation(position, new AnimationInfo(startOffset));
        return true;
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
