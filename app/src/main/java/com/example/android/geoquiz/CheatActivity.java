package com.example.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity {

  private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
  private static final String EXTRA_ANSWER_SHOW = "com.bignerdranch.android.geoquiz.answer_shown";
  private static final String ANSWER_SHOWN_KEY = "ANSWER_SHOWN";
  private static final String API_KEY = "API";

  private boolean mAnswerIsTrue;

  private TextView mAnswerTextView;
  private TextView mAPIVersionTextView;
  private Button mShowAnswerButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cheat);

    mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

    mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
    mAPIVersionTextView = (TextView) findViewById(R.id.api_version_text_view);

    String api_version = String.valueOf(Build.VERSION.SDK_INT);
    mAPIVersionTextView.setText("API Level " + api_version);

    if (savedInstanceState != null) {
      mAnswerTextView.setText(savedInstanceState.getString(ANSWER_SHOWN_KEY));
      mAPIVersionTextView.setText(savedInstanceState.getString(API_KEY));
      setAnwserShownResult(true);
    }

    mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
    mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mAnswerIsTrue) {
          mAnswerTextView.setText(R.string.true_button);
        } else {
          mAnswerTextView.setText(R.string.false_button);
        }
        setAnwserShownResult(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          int cx = mShowAnswerButton.getWidth();
          int cy = mShowAnswerButton.getHeight();
          float radius = mShowAnswerButton.getWidth();
          Animator anim = ViewAnimationUtils
              .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
          anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
          });
          anim.start();
        } else {
          mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
      }
    });
  }

  public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
    Intent intent = new Intent(packageContext, CheatActivity.class);
    intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
    return intent;
  }

  public static boolean wasAnswerShown(Intent result) {
    return result.getBooleanExtra(EXTRA_ANSWER_SHOW, false);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(ANSWER_SHOWN_KEY, mAnswerTextView.getText().toString());
    outState.putString(API_KEY, mAPIVersionTextView.getText().toString());

  }

  private void setAnwserShownResult(boolean isAnswerShown) {
    Intent data = new Intent();
    data.putExtra(EXTRA_ANSWER_SHOW, isAnswerShown);
    setResult(RESULT_OK, data);
  }
}
