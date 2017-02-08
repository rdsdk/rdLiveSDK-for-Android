package com.rd.mix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rdlivedemo.R;
import com.rd.demo.utils.AppUtil;
import com.rd.demo.utils.DateTimeUtils;
import com.rd.demo.utils.PathUtils;
import com.rd.imenu.IMenuListener;
import com.rd.live.RDLiveSDK;
import com.rd.recorder.AudioPlayer;
import com.rd.recorder.AudioPlayer.OnCompletionListener;
import com.rd.recorder.AudioPlayer.OnInfoListener;
import com.rd.recorder.AudioPlayer.OnPreparedListener;

/**
 * 直播中混音 (只支持播放本地音乐,支持的音频格式mp3、mp2、 aac、 wma、 wmv、 ac3、 ogg)
 *
 * @author JIAN
 */
public class MixHandler {

    private AudioPlayer player;//混音播放器

    private View mRootView;
    private Context mContext;
    private ListView mlistMusic;
    private MixAdapter mixAdapter;
    private TextView mixTv;
    private IMenuListener listener;
    private int tvColor_n, tvColor_ed;//选中与非选中的状态颜色

    public MixHandler(View mixLayout, IMenuListener mlistener) {
        mRootView = mixLayout;
        listener = mlistener;
        mContext = mRootView.getContext();
        Resources res = mContext.getResources();
        tvColor_n = res.getColor(R.color.white);
        tvColor_ed = res.getColor(R.color.main_color);
        //混音列表
        mlistMusic = (ListView) mRootView.findViewById(R.id.mix_listview);
        mixAdapter = new MixAdapter(mContext);
        mlistMusic.setOnItemClickListener(listviewListener);
        mlistMusic.setAdapter(mixAdapter);
        // 关闭mix控制面板
        mRootView.findViewById(R.id.shadow_music).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onMixVisibleGone();
                    }
                });

    }

    private OnItemClickListener listviewListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            String path = mixAdapter.getItem(position).getPath();
            if (!TextUtils.isEmpty(path)) {
                RDLiveSDK.enableMixAudio(true);// 开启混音 ，   要混淆的音乐只能通过AudioPlayer播放
                initPlayer(mixAdapter.getItem(position));
                mixTv.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.mix_music_ed, 0, 0);
                mixTv.setText(R.string.mix_music_ed);
                mixTv.setTextColor(tvColor_ed);

            } else {
                RDLiveSDK.enableMixAudio(false);// 关闭混音
                release();
                mixTv.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.mix_none, 0, 0);
                mixTv.setText(R.string.mix_music_n);
                mixTv.setTextColor(tvColor_n);
            }
            mixAdapter.checked(position);
            onMixVisibleGone();

        }

    };

    /**
     * 关闭混音控制面板
     */
    private void onMixVisibleGone() {
        mRootView.setVisibility(View.GONE);
        if (null != listener) {
            listener.onMenuGone();
        }

    }

    /**
     * 初始化音乐
     *
     * @param info  mix对象
     */
    private void initPlayer(final MixInfo info) {
        if (null != player) {
            player.stop();
            player.release();
        }
        player = new AudioPlayer();
        try {
            player.setDataSource(info.getPath());
            player.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(AudioPlayer mp) {
                    info.setDuration(DateTimeUtils.updateTime(mp.getDuration()));
                    mixAdapter.notifyDataSetChanged();
                    start();//初始化完成就播放
                }
            });
            player.setOnInfoListener(new OnInfoListener() {

                @Override
                public boolean onInfo(AudioPlayer mp, int what, int extra) {
                    return false;
                }
            });
            player.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(AudioPlayer mp) {
                    player.seekTo(0);
                    start();// 无限循环播放
                }
            });
            player.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始播放音乐
     */
    private void start() {
        if (null != player && !player.isPlaying()) {
            player.start();
        }
    }

    /**
     * 销毁混音播放器
     */
    public void release() {
        if (null != player) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        System.gc();
    }

    /**
     * 导出内置音乐到sd
     */
    public void exportMusic() {
        new AsyncTask<Integer, Integer, Integer>() {
            ArrayList<MixInfo> list = new ArrayList<MixInfo>();
            String mixRoot;

            @Override
            protected void onPreExecute() {
                mixRoot = PathUtils.createDir("mixs");
            }

            ;

            @Override
            protected Integer doInBackground(Integer... params) {

                File f = new File(mixRoot, "01.mp3");
                if (!f.exists()) {
                    // // 导出内置音乐
                    AppUtil.assetRes2File(mContext.getAssets(), "mixs/01.mp3",
                            f.getAbsolutePath());
                }
                list.add(new MixInfo("00:50", f.getAbsolutePath(), "街头"));

                f = new File(mixRoot, "02.mp3");
                if (!f.exists()) {
                    // // 导出内置音乐
                    AppUtil.assetRes2File(mContext.getAssets(), "mixs/02.mp3",
                            f.getAbsolutePath());
                }
                list.add(new MixInfo("00:48", f.getAbsolutePath(), "旅行"));

                f = new File(mixRoot, "03.mp3");
                if (!f.exists()) {
                    // // 导出内置音乐
                    AppUtil.assetRes2File(mContext.getAssets(), "mixs/03.mp3",
                            f.getAbsolutePath());
                }
                list.add(new MixInfo("00:49", f.getAbsolutePath(), "清新"));

                return 0;
            }

            @Override
            protected void onPostExecute(Integer result) {
                list.add(new MixInfo("", "", "关闭混音"));
                mixAdapter.update(list);

            }

            ;
        }.execute();
    }

    /***
     * 选择要混淆的音乐
     */
    public void onCheckMixMusics(TextView parent) {
        mRootView.setVisibility(View.VISIBLE);
        mixTv = parent;
    }

    /**
     * 返回
     *
     * @return true 可以返回到上一级,false不能返回到上一级
     */
    public boolean onBackPressed() {
        if (mRootView.getVisibility() == View.VISIBLE) {
            mRootView.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

}
