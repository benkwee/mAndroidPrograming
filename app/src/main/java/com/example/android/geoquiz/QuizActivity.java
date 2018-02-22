package com.example.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
  
  private static final String TAG = "QuizActivity";
  private static final String KEY_INDEX = "Index";
  private static final String QUESTIONS_ANSWERED_KEY = "Question_Answered_Key";
  private static final String ANSWER_KEY = "Answer_key";
  private static final String SCORE_KEY = "Score_key";
  private static final String CHEATER_KEY = "Cheater_key";
  private static final int REQUEST_CHEAT_CODE = 0;

  private Button mTrueButton;
  private Button mFalseButton;
  private Button mCheatButton;
  private ImageButton mBackButton;
  private ImageButton mNextButton;
  private TextView mQuestionTextView;

  private Question[] mQuestionBank = new Question[] {
      new Question(R.string.question_austrailia, true),
      new Question(R.string.question_oceans, true),
      new Question(R.string.question_mideast, false),
      new Question(R.string.question_africa, false),
      new Question(R.string.question_americas, true),
      new Question(R.string.question_asia, true),
  };

  private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];
  private boolean[] mQuestionsCheated = new boolean[mQuestionBank.length];

  private int mCurrentIndex = 0;
  private int mMaxCheatAllowed = 3;
  private double mScore = 0;
  private double mAnswer = 0;
  private boolean mIsCheater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate: ");
    setContentView(R.layout.activity_quiz);

    if (savedInstanceState != null) {
      mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
      mQuestionsAnswered = savedInstanceState.getBooleanArray(QUESTIONS_ANSWERED_KEY);
      mQuestionsCheated = savedInstanceState.getBooleanArray(CHEATER_KEY);
      mScore = savedInstanceState.getDouble(SCORE_KEY);
      mAnswer = savedInstanceState.getDouble(ANSWER_KEY);
    }

    mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
    mQuestionTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
      }
    });

    mTrueButton = (Button) findViewById(R.id.true_button);
    mTrueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        checkAnswer(true);
      }
    });

    mFalseButton = (Button) findViewById(R.id.false_button);
    mFalseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        checkAnswer(false);
      }
    });

    mBackButton = (ImageButton) findViewById(R.id.back_button);
    mBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCurrentIndex <= 0) {
          mCurrentIndex = mQuestionBank.length - 1;
        } else {
          mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        }

        updateQuestion();
      }
    });

    mNextButton = (ImageButton) findViewById(R.id.next_button);
    mNextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
      }
    });

    mCheatButton = (Button) findViewById(R.id.cheat_button);
    mCheatButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
        startActivityForResult(intent, REQUEST_CHEAT_CODE);
      }
    });

    updateQuestion();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }

    if (requestCode == REQUEST_CHEAT_CODE) {
      if (data == null) {
        return;
      }
      mIsCheater = CheatActivity.wasAnswerShown(data);
      mQuestionsCheated[mCurrentIndex] = mIsCheater;
    }
  }

  private void updateQuestion() {
    int question = mQuestionBank[mCurrentIndex].getTextResId();
    mQuestionTextView.setText(question);
    mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
    mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
  }

  private void checkAnswer(boolean userPressedTrue) {
    mQuestionsAnswered[mCurrentIndex] = true;
    mTrueButton.setEnabled(false);
    mFalseButton.setEnabled(false);

    boolean isAnswerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

    int messageResId = 0;

    mIsCheater = mQuestionsCheated[mCurrentIndex];

    if (mIsCheater) {
      messageResId = R.string.judgement_toast;
    } else {
      if (userPressedTrue == isAnswerTrue) {
        messageResId = R.string.correct_toast;
        mScore++;
      } else {
        messageResId = R.string.incorrect_toast;
      }
    }

    mAnswer++;
    if (mAnswer == mQuestionBank.length) {
      double totalScore = Math.round((mScore/mQuestionBank.length) * 100);
      Toast.makeText(this, "Total Score is: " + totalScore + "%", Toast.LENGTH_LONG)
        .show();
      mScore = 0;
      mAnswer = 0;
    } else {
      Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
          .show();
    }
  }

  private boolean maxCheated() {
    int count = 0;
    for (int i = 0; i < mQuestionsCheated.length; i++) {
      if (mQuestionsCheated[i]){
        count++;
      }
    }
    return count == mMaxCheatAllowed;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.i(TAG, "onSaveInstanceState: ");
    outState.putInt(KEY_INDEX, mCurrentIndex);
    outState.putBooleanArray(QUESTIONS_ANSWERED_KEY, mQuestionsAnswered);
    outState.putBooleanArray(CHEATER_KEY, mQuestionsCheated);
    outState.putDouble(ANSWER_KEY, mAnswer);
    outState.putDouble(SCORE_KEY, mScore);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart: ");
    if (maxCheated()) {
      mCheatButton.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume: ");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause: ");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop: ");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy: ");
  }
}
