package com.yy.mediaplayer.room;

import android.util.Log;

import com.yy.mediaplayer.net.BaseView;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public abstract class RoomBaseConsumer<T> implements Consumer<T> {
    private BaseView baseView;
    String TAG = "BaseObserver";

    public RoomBaseConsumer() {
    }

    public RoomBaseConsumer(BaseView view) {
        baseView = view;
    }

    @Override
    public void accept(T t) throws Exception {
        onSuccess(t);
    }

    public void onError(@NonNull Throwable e) {
        Log.d(TAG, "onError: ");
//        baseView.onError(e.getMessage());
        onError(e.getMessage());
    }

    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}
