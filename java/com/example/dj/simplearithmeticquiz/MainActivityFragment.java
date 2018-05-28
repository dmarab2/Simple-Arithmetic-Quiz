package com.example.dj.simplearithmeticquiz;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.animation.Animator;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final int PROBLEMS_IN_QUIZ = 10;
    private List<String> quizProblemsList;
    private List<String> problemsList;
    private Set<String> problemsSet;
    private int correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;

    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextview;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;
    private TextView problemTextView;
    private TextView resultTextView;

    private Button resetButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        quizProblemsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        quizLinearLayout = (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextview = (TextView) view.findViewById(R.id.questionNumberTextView);
        problemTextView = (TextView) view.findViewById(R.id.problemTextView);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);
        resultTextView = (TextView) view.findViewById(R.id.resultsTextView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = (LinearLayout) view.findViewById(R.id.row4LinearLayout);
        resetButton = (Button) view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(resetListener);

        for(LinearLayout row : guessLinearLayouts){
            for(int column = 0; column < row.getChildCount(); column++){
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextview.setText(getString(R.string.question, 1, PROBLEMS_IN_QUIZ));
        return view;



    }

    public void updateGuessRows(SharedPreferences sharedPreferences){
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        for(LinearLayout layout : guessLinearLayouts) layout.setVisibility(View.GONE);
        for(int row = 0; row < guessRows; row++) guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    public void updateProblems(SharedPreferences sharedPreferences){
        problemsSet = sharedPreferences.getStringSet(MainActivity.PROBLEMS, null);
    }

    public void resetQuiz(){
        AssetManager assets = getActivity().getAssets();

        correctAnswers = 0;
        totalGuesses = 0;
        quizProblemsList.clear();
        resultTextView.setText("");

        int counter = 1;
        int number = 1;

        loadNextProblem();

    }

    private void loadNextProblem(){
        answerTextView.setText("");
        String[] problemsArray = problemsSet.toArray(new String[0]);
        int randomNum = random.nextInt(problemsArray.length);
        int first = 0;
        int second = 0;
        int answer = 0;
        String operand;
        ArrayList<Integer> numsForProblem = generateProblem(problemsArray[randomNum]);
        first = numsForProblem.get(0);
        second = numsForProblem.get(1);
        answer = numsForProblem.get(2);
        operand = getOperand(problemsArray[randomNum]);
        correctAnswer = answer;


        questionNumberTextview.setText(getString(R.string.question, (correctAnswers + 1), PROBLEMS_IN_QUIZ));
        problemTextView.setText(first + " " + operand + " " + second + " =");

        for(int row = 0; row < guessRows; row++){
            for(int column = 0; column < guessLinearLayouts[row].getChildCount(); column++){
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                newGuessButton.setText(Integer.toString(random.nextInt(101)));
            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        ((Button) randomRow.getChildAt(column)).setText(Integer.toString(answer));
    }

    private ArrayList<Integer> generateProblem(String type){
        int first = 0;
        int second = 0;
        int answer = 0;
        ArrayList<Integer> set = new ArrayList<Integer>();
        if(type.equals("Addition")){
            first = random.nextInt(101);
            second = random.nextInt(101);
            answer = first + second;
            set.add(first);
            set.add(second);
            set.add(answer);
            return set;
        }
        else if(type.equals("Subtraction")){
            first = random.nextInt(101);
            second = random.nextInt(first);
            answer = first - second;
            set.add(first);
            set.add(second);
            set.add(answer);
            return set;
        }
        else if(type.equals("Multiplication")){
            first = random.nextInt(26);
            second = random.nextInt(26);
            answer = first * second;
            set.add(first);
            set.add(second);
            set.add(answer);
            return set;
        }

        else if(type.equals("Division")){
            answer = random.nextInt(26);
            second = random.nextInt(26);
            first = answer * second;
            set.add(first);
            set.add(second);
            set.add(answer);
            return set;
        }
        else{
            set.add(first);
            set.add(second);
            set.add(answer);
            return set;
        }
    }

    String getOperand(String type){
        if(type.equals("Addition")){
            return "+";
        }
        else if(type.equals("Subtraction")){
            return "-";
        }
        else if(type.equals("Multiplication")){
            return "x";
        }
        else if(type.equals("Division")){
            return "/";
        }
        else{
            return "ERROR";
        }
    }

    private void animate(boolean animateOut){
        if(correctAnswers == 0){
            return;
        }

        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        int radius = Math.max(quizLinearLayout.getWidth(),quizLinearLayout.getHeight());

        Animator animator;

        if(animateOut){
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout,centerX,centerY,radius,0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextProblem();
                }
            });
        }
        else{
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout,centerX,centerY,0,radius);
        }
        animator.setDuration(500);
        animator.start();
    }
    private OnClickListener guessButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            int guess = Integer.parseInt(guessButton.getText().toString());
            ++totalGuesses;
            if (guess == correctAnswer) {
                ++correctAnswers;
                answerTextView.setText(correctAnswer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));
                disableButtons();

                if (correctAnswers == PROBLEMS_IN_QUIZ) {
                    resultTextView.setText("You made a total of " + totalGuesses + " guess. Please reset the quiz.");
                } else {
                    handler.postDelayed(
                            new Runnable() {

                                @Override
                                public void run() {
                                    animate(true);
                                }
                            }, 2000
                    );
                }
            } else {
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false);
            }
        }

    };

    private OnClickListener resetListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            resetQuiz();
        }
    };

    private void disableButtons(){
        for(int row = 0; row < guessRows; row++){
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int i = 0; i < guessRow.getChildCount(); i++) guessRow.getChildAt(i).setEnabled(false);
        }
    }


}
