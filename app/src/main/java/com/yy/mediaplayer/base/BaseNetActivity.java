package com.yy.mediaplayer.base;

import com.yy.mediaplayer.net.bean.BaseModel;
import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.utils.ToastUtil;

public abstract class BaseNetActivity<P extends BasePresenter> extends BaseActivity implements BaseView {
    public P mRoomPresenter;

    @Override
    protected void createPresenter_() {
        mRoomPresenter = createRoomPresenter();
    }

    protected abstract P createRoomPresenter();

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
    protected void onDestroy() {
        if (mRoomPresenter != null) {
            mRoomPresenter.detachView();
        }
        super.onDestroy();
    }
}
