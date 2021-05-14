package com.yy.mediaplayer.room;

import android.util.Log;

import com.yy.mediaplayer.net.BaseView;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public abstract class RoomBaseObserver<T> extends DisposableObserver<T> {
    private final BaseView baseView;
    String TAG = "BaseObserver";

    public RoomBaseObserver(BaseView view) {
        baseView = view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onNext(@NonNull T o) {
        Log.d(TAG, "onNext: ");
        onSuccess(o);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.d(TAG, "onError: ");
//        baseView.onError(e.getMessage());
        onError(e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }

    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}
