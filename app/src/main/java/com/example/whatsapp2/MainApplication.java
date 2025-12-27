package com.example.whatsapp2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.example.whatsapp2.fragments.PopupFragment;
import java.util.Random;

public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private Runnable showPopupRunnable;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        schedulePopup();
    }

    private void schedulePopup() {
        showPopupRunnable = () -> {
            if (currentActivity != null && !currentActivity.isFinishing()) {
                showPopup(currentActivity);
            }
            // Reschedule for a random time between 10 and 20 seconds
            handler.postDelayed(showPopupRunnable, (10 + random.nextInt(11)) * 1000);
        };
        handler.postDelayed(showPopupRunnable, (10 + random.nextInt(11)) * 1000);
    }

    private void showPopup(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
            if (fm.findFragmentByTag(PopupFragment.TAG) == null) {
                new PopupFragment().show(fm, PopupFragment.TAG);
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // Not used
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // Not used
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // Not used
    }
}
