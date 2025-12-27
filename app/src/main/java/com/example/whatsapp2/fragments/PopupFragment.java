package com.example.whatsapp2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.AppBaseDeDatos;
import java.util.Random;
import java.util.concurrent.Executors;

public class PopupFragment extends DialogFragment {

    public static final String TAG = "PopupFragment";
    private final Random random = new Random();
    private final int currentUserId = 1; // Hardcoded user ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.send_button).setOnClickListener(v -> {
            v.setEnabled(false);
            double amount = 0.01 + (2.0 * random.nextDouble());

            Executors.newSingleThreadExecutor().execute(() -> {
                if (getContext() != null) {
                    AppBaseDeDatos.getDatabase(getContext()).chatDao().addBalance(currentUserId, amount);
                }
            });

            final int[] anchorPos = new int[2];
            v.getLocationOnScreen(anchorPos);
            final int anchorWidth = v.getWidth();
            final String text = String.format("+%.2f$", amount);

            if (getView() != null) {
                getView().setVisibility(View.INVISIBLE);
            }

            showFadingText(anchorPos, anchorWidth, text);
        });
    }

    private void showFadingText(int[] anchorPos, int anchorWidth, String text) {
        final Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
             if (isAdded()) {
                dismiss();
            }
            return;
        }

        final ViewGroup container = (ViewGroup) activity.getWindow().getDecorView();
        final TextView textView = new TextView(activity);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.green, null));
        textView.setTextSize(24f);
        textView.setShadowLayer(5, 0, 0, getResources().getColor(android.R.color.black, null));

        container.addView(textView, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        textView.measure(0, 0);

        float initialX = anchorPos[0] + (anchorWidth - textView.getMeasuredWidth()) / 2f;
        float initialY = anchorPos[1];
        textView.setX(initialX);
        textView.setY(initialY);

// --- FIX STARTS HERE ---

// 1. Change "translationY" to "y"
// 2. Animate from current initialY to (initialY - 200)
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(textView, "y", initialY, initialY - 200f);
        moveUp.setDuration(1500);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        fadeOut.setDuration(1500);

// --- FIX ENDS HERE ---

        moveUp.addListener(new AnimatorListenerAdapter() {
            // ... rest of code
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(textView);
                if (isAdded()) {
                    dismiss();
                }
            }
        });

        moveUp.start();
        fadeOut.start();
    }
}
