package com.jinxin.cloudmusic.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinxin.cloudmusic.util.LoadingUtil;

import butterknife.ButterKnife;

/**
 * Created by XTER on 2016/9/20.
 * 基类
 */
public abstract class BaseFragment extends Fragment {
    Dialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflate(inflater, container);
        ButterKnife.bind(this, rootView);
        initData();
        return rootView;
    }

    public abstract View inflate(LayoutInflater inflater, ViewGroup container);

    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected void showLoading(String msg) {
        loadingDialog = LoadingUtil.createLoadingDialog(getActivity(), msg);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    protected void hideLoading() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
        loadingDialog = null;
    }

}
