package com.thegrasshoppers.geoquiz;

/**
 * Created by lykav on 7/24/2017.
 */

public class Question {
    private int mTestResId;
    private boolean mAnswerTrue;

    public Question(int textResId, boolean answerTrue){
        mTestResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getTestResId() {
        return mTestResId;
    }

    public void setTestResId(int testResId) {
        mTestResId = testResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
