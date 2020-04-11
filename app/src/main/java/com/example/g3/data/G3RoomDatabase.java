package com.example.g3.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.g3.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version=1, exportSchema=true)
public abstract class G3RoomDatabase extends RoomDatabase
{
    public abstract UserDao userDao();

    private static final int NUM_THREADS = 4;
    private static final RoomDatabase.Callback sRoomDatabaseCallback;

    private static volatile G3RoomDatabase INSTANCE;

    static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUM_THREADS);

    static {
        sRoomDatabaseCallback = new Callback()
        {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db)
            {
                super.onOpen(db);

                // Create the default user if it doesn't already exist
                writeExecutor.execute(() -> {
                    UserDao dao = INSTANCE.userDao();
                    LiveData<User> userData = dao.getDefaultUser();

                    if (userData == null) {
                        System.out.println("Creating default user");
                        User user = new User();
                        user.setId(1); //default user must have ID=1
                        dao.insert(user);
                    }
                });
            }
        };
    }

    static G3RoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (G3RoomDatabase.class) {
                if (INSTANCE == null) {
                    RoomDatabase.Builder<G3RoomDatabase> databaseBuilder = Room.databaseBuilder(context.getApplicationContext(), G3RoomDatabase.class, "g3_database");
                    databaseBuilder.addCallback(sRoomDatabaseCallback);

                    INSTANCE = databaseBuilder.build();
                }
            }
        }
        return INSTANCE;
    }

}
