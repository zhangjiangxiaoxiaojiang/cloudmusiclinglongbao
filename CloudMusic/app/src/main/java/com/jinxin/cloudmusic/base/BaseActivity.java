package com.jinxin.cloudmusic.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jinxin.cloudmusic.util.LoadingUtil;

import butterknife.ButterKnife;

/**
 * Created by XTER on 2016/9/19.
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity {
	Dialog loadingDialog;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		ButterKnife.bind(this);
		initView();
	}

	protected abstract void setContentView();

	protected abstract void initView();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}

	public void showLoading(String msg){
		loadingDialog = LoadingUtil.createLoadingDialog(this, msg);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	public void hideLoading(){
		if(loadingDialog!=null)
			loadingDialog.dismiss();
		loadingDialog = null;
	}
}
