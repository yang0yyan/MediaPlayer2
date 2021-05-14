package com.yy.mediaplayer.room;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public class DBManager {
    private static volatile AppDatabase db;
    private static Context context = null;

    public static void init(Context context) {
        DBManager.context = context;
        RoomDatabase.Builder<AppDatabase> builder = Room.databaseBuilder(context, AppDatabase.class, "sample.db");
        db = builder.build();
    }

    public static AppDatabase getDb() {
        synchronized (DBManager.class) {
            if (db == null && context != null) {
                init(context);
            }
        }
        return db;
    }

    public static void release() {
        if (null != db) {
            db.close();
            db = null;
        }
    }
}
