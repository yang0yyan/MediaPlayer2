package com.yy.mediaplayer.net;

import com.yy.mediaplayer.net.bean.BaseModel;

public interface BaseView {
    //显示dialog
    void showLoading();

    //隐藏 dialog
    void hideLoading();

    //显示错误信息
    void showError(String msg);

    //错误码
    void onErrorCode(BaseModel model);

    //进度条显示
    void showProgress();

    //进度条隐藏
    void hideProgress();

    //文件下载进度监听
    void onProgress(int progress);
}
