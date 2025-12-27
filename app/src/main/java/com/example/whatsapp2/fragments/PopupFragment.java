package com.example.whatsapp2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.whatsapp2.R;
import com.example.whatsapp2.activities.MainActivity;
import com.example.whatsapp2.api.OperacionesSaldo;
import com.example.whatsapp2.database.AppBaseDeDatos;
import java.util.Random;
import java.util.concurrent.Executors;

public class PopupFragment extends DialogFragment {

    public interface OnCoinUpdateListener {
        void onCoinUpdated();
    }

    private OnCoinUpdateListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCoinUpdateListener) {
            listener = (OnCoinUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCoinUpdateListener");
        }
    }

    public static final String TAG = "PopupFragment";
    private final Random random = new Random();
    private final int currentUserId = 1; // Hardcoded user ID
    private OperacionesSaldo operacionesSaldo;

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

        Bundle args = getArguments();
        boolean isLanguagePopup = args != null && args.getBoolean("isLanguagePopup", false);

        if (isLanguagePopup) {
            handleLanguagePopup(view);
        } else {
            handleQuizPopup(view);
        }
    }

    private void handleLanguagePopup(View view) {
        setCancelable(true);

        final TextView popupTitle = view.findViewById(R.id.popup_title);
        popupTitle.setText(R.string.language);

        final RadioButton radioButton1 = view.findViewById(R.id.radio_button_1);
        radioButton1.setText("Español");

        final RadioButton radioButton2 = view.findViewById(R.id.radio_button_2);
        radioButton2.setText("English");

        final RadioButton radioButton3 = view.findViewById(R.id.radio_button_3);
        radioButton3.setVisibility(View.GONE);

        view.findViewById(R.id.send_button).setVisibility(View.GONE);

        final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                if (checkedId == R.id.radio_button_1) {
                    activity.setLocale("es");
                } else if (checkedId == R.id.radio_button_2) {
                    activity.setLocale("en");
                }
            }
            dismiss();
        });
    }

    private void handleQuizPopup(View view) {
        setCancelable(false);

        operacionesSaldo = new OperacionesSaldo(AppBaseDeDatos.getDatabase(getContext()).chatDao());

        final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        final TextView popupTitle = view.findViewById(R.id.popup_title);
        final RadioButton radioButton1 = view.findViewById(R.id.radio_button_1);
        final RadioButton radioButton2 = view.findViewById(R.id.radio_button_2);
        final RadioButton radioButton3 = view.findViewById(R.id.radio_button_3);

        int questionNumber = random.nextInt(3) + 1;

        int questionResId;
        int[] answerResIds = new int[3];

        switch (questionNumber) {
            case 1:
                questionResId = R.string.question_1;
                answerResIds[0] = R.string.q1_answer_1;
                answerResIds[1] = R.string.q1_answer_2;
                answerResIds[2] = R.string.q1_answer_3;
                break;
            case 2:
                questionResId = R.string.question_2;
                answerResIds[0] = R.string.q2_answer_1;
                answerResIds[1] = R.string.q2_answer_2;
                answerResIds[2] = R.string.q2_answer_3;
                break;
            case 3:
            default:
                questionResId = R.string.question_3;
                answerResIds[0] = R.string.q3_answer_1;
                answerResIds[1] = R.string.q3_answer_2;
                answerResIds[2] = R.string.q3_answer_3;
                break;
        }

        popupTitle.setText(questionResId);
        radioButton1.setText(answerResIds[0]);
        radioButton2.setText(answerResIds[1]);
        radioButton3.setText(answerResIds[2]);


        view.findViewById(R.id.send_button).setOnClickListener(v -> {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getContext(), "Se debe seleccionar una respuesta", Toast.LENGTH_SHORT).show();
                return;
            }

            v.setEnabled(false);
            double amount = 0.01 + (2.0 * random.nextDouble());

            Executors.newSingleThreadExecutor().execute(() -> {
                operacionesSaldo.addCoins(currentUserId, amount);
                if (listener != null) {
                    listener.onCoinUpdated();
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

        ObjectAnimator moveUp = ObjectAnimator.ofFloat(textView, "y", initialY, initialY - 200f);
        moveUp.setDuration(1500);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        fadeOut.setDuration(1500);

        moveUp.addListener(new AnimatorListenerAdapter() {
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
