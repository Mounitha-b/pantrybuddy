package com.pantrybuddy.Schedules;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pantrybuddy.activity.IWebService;
import com.pantrybuddy.activity.MainActivity;
import com.pantrybuddy.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class AppNotifier extends Worker implements IWebService {
    public AppNotifier(@NonNull @org.jetbrains.annotations.NotNull Context context, @NonNull @org.jetbrains.annotations.NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Result doWork() {
        Server server = new Server(getApplicationContext());
        server.fetchExpiredProducts();
        return Result.success();
    }

    @Override
    public void processResponse(JSONObject responseObj) throws JSONException {
        if(responseObj !=null){
            if(responseObj.getString("code") != null && responseObj.getString("code").equalsIgnoreCase("200")){
                if(responseObj.getBoolean("expired_in_pantry")){
                    MainActivity.globalVariables.createNotificationChannel();
                }
            }
        }
    }

}
