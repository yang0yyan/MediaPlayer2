package com.yy.mediaplayer.room;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DBManager {
    private static volatile AppDatabase db;
    private static Context context = null;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("alter table `music_info` add `imageUrl` TEXT");
        }
    };

    public static void init(Context context) {
        DBManager.context = context;
        RoomDatabase.Builder<AppDatabase> builder = Room.databaseBuilder(context, AppDatabase.class, "sample.db");
        builder.addMigrations(MIGRATION_1_2);
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
