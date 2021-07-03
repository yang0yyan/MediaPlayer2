package com.yy.mediaplayer.base;

import android.app.Dialog;
import android.view.KeyEvent;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.net.bean.BaseModel;
import com.yy.mediaplayer.utils.DialogHolder;
import com.yy.mediaplayer.utils.ToastUtil;

public abstract class BaseNetActivity<P extends BasePresenter> extends BaseActivity implements BaseView {
    public P mRoomPresenter;
    private Dialog dialog;
    private boolean isShowDialog;

    @Override
    protected void initView() {
        dialog = DialogHolder.customDialog(this, R.layout.dialog_loading);
    }

    @Override
    protected void createPresenter_() {
        mRoomPresenter = createRoomPresenter();
    }

    protected abstract P createRoomPresenter();

    @Override
    public void showLoading() {
        isShowDialog = true;
        dialog.show();
    }

    @Override
    public void hideLoading() {
        isShowDialog = false;
        dialog.hide();
    }

    @Override
    public void showError(String msg) {
        ToastUtil.showToast(msg);
    }

    @Override
    public void onErrorCode(BaseModel model) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowDialog) {
                mRoomPresenter.removeDisposable();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mRoomPresenter != null) {
            mRoomPresenter.detachView();
        }
        super.onDestroy();
    }
}
