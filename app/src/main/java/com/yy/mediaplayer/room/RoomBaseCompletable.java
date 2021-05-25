package com.yy.mediaplayer.room;

import android.util.Log;

import com.yy.mediaplayer.net.BaseView;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public abstract class RoomBaseCompletable implements Action {
    private BaseView baseView;
    String TAG = "BaseObserver";

    public RoomBaseCompletable() {
    }

    public RoomBaseCompletable(BaseView view) {
        baseView = view;
    }

    @Override
    public void run() throws Exception {
        onSuccess();
    }

    public void onError(@NonNull Throwable e) {
        Log.d(TAG, "onError: ");
//        baseView.onError(e.getMessage());
        onError(e.getMessage());
    }

    public abstract void onSuccess();

    public abstract void onError(String msg);
}
