package com.jinxin.cloudmusic.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by ZJ on 2017/3/10 0010.
 * 音乐搜索界面
 */
public class MusicSearchFragment extends BaseFragment {


    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fra_music_search, container, false);
    }

    @Override
    public void initData() {

    }


}
