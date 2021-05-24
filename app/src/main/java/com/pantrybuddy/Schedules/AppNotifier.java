package com.pantrybuddy.Schedules;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;

public class AppNotifier extends Worker {
    public AppNotifier(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
        uploadImages();

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
