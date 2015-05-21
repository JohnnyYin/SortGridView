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

import com.johnnyyin.sortgridview.library.AbsoluteExchangeChildAnimationController;
import com.johnnyyin.sortgridview.library.AlphaChildAnimationController;
import com.johnnyyin.sortgridview.library.AnimationInfo;
import com.johnnyyin.sortgridview.library.ChildAnimationControllerBase;
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
    private long mBaseDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortGridView = (SortGridView) findViewById(R.id.sort_grid_view);
        initData();
    }

    private void initData() {
        mNumColumns = 3;
        mBaseDuration = 600;
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
                .duration(mBaseDuration)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mAbsoluteExchangeChildAnimationController = new AbsoluteExchangeChildAnimationController.Builder()
                .sortGridView(mSortGridView)
                .numColumns(mNumColumns)
                .duration(mBaseDuration)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mWaterFallChildAnimationController = new WaterFallChildAnimationController.Builder()
                .sortGridView(mSortGridView)
                .numColumns(mNumColumns)
                .interpolator(interpolator)
                .animationListener(animationListener).build();
        mAlphaChildAnimationController = new AlphaChildAnimationController.Builder()
                .numColumns(mNumColumns)
                .duration(mBaseDuration)
                .animationListener(animationListener).build();

        List<Data> data = new ArrayList<Data>();
        for (int i = 0; i < 99; i++) {
            data.add(new Data(i, "item:" + i));
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
                int firstVisible = mSortGridView.getFirstVisiblePosition();
                int max = mSortGridView.getLastVisiblePosition() + 1 - firstVisible;
                Random random = new Random();
                ChildAnimationControllerBase controller;
                switch (position % 5) {
                    case 0: {
                        controller = mAbsoluteExchangeChildAnimationController;
                        int count = 0;
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        while (count < max / 2) {
                            int oldPos;
                            do {
                                oldPos = firstVisible + random.nextInt(max);
                            } while (list.contains(oldPos));
                            list.add(oldPos);
                            int newPos;
                            do {
                                newPos = firstVisible + random.nextInt(max);
                            } while (list.contains(newPos));
                            list.add(newPos);
                            testExchangeAnimation((ExchangeChildAnimationController) controller, oldPos, newPos, 0);
                            count++;
                        }
                        break;
                    }
                    case 1: {
                        controller = mWaterFallChildAnimationController;
                        for (int i = 0; i < max; i++) {
                            testWaterFallAnimation((WaterFallChildAnimationController) controller, i + firstVisible, ((max - 1 - i) / 3) * 50);
                        }
                        break;
                    }
                    case 2: {
                        controller = mAbsoluteExchangeChildAnimationController;
                        //controller = mExchangeChildAnimationController;
                        List<Data> data = new ArrayList<Data>(mAdapter.getData());
                        data.add(firstVisible, data.remove(firstVisible + max - 1));
                        testExchangeAnimation2((ExchangeChildAnimationController) controller, data, firstVisible, max);
                        break;
                    }
                    case 3: {
                        controller = mAlphaChildAnimationController;
                        for (int i = 0; i < max; i++) {
                            testAlphaAnimation((AlphaChildAnimationController) controller, firstVisible + i, i * 20);
                        }
                        break;
                    }
                    case 4: {
                        controller = mAbsoluteExchangeChildAnimationController;
                        testExchangeAnimation3((ExchangeChildAnimationController) controller, firstVisible, max);
                        break;
                    }
                    default:
                        return;
                }
                mSortGridView.setChildAnimationController(controller);
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

    public boolean testExchangeAnimation2(ExchangeChildAnimationController controller, List<Data> data, int offset, int size) {
        int changeCount = 0;
        List<Data> oldData = mAdapter.getData();
        if (controller != null && oldData != null && !oldData.isEmpty() && data != null && !data.isEmpty()) {
            for (int pos = 0; pos < size; pos++) {
                int realPos = pos + offset;
                int oldPos = oldData.indexOf(data.get(realPos));
                if (oldPos != pos) {
                    controller.setChildAnimation(realPos, new ExchangeChildAnimationController.ExchangeAnimationInfo(oldPos, null, changeCount++ * 20));
                }
            }
        }
        mAdapter.setData(data);
        return true;
    }

    public boolean testExchangeAnimation3(ExchangeChildAnimationController controller, int offset, int size) {
        if (controller != null) {
            for (int pos = 0; pos < size; pos++) {
                int realPos = pos + offset;
                controller.setChildAnimation(realPos, new ExchangeChildAnimationController.ExchangeAnimationInfo(offset, null, pos * 20));
            }
        }
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

    private class Data {
        int id;
        String text;

        public Data(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Data)
                return ((Data) o).id == id;
            return false;
        }
    }

    private class SortGridViewAdapter extends BaseAdapter {

        private List<Data> mData;

        public SortGridViewAdapter(List<Data> data) {
            setData(data);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Data getItem(int position) {
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
            ((TextView) convertView.findViewById(R.id.content)).setText(getItem(position).text);
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

        public List<Data> getData() {
            return mData;
        }

        public void setData(List<Data> data) {
            this.mData = data;
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
