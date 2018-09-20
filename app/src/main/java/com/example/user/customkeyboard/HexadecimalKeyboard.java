package com.example.user.customkeyboard;

import android.app.Activity;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class HexadecimalKeyboard {

    private KeyboardView mKeyboardView;
    private AppCompatActivity mHostActivity;

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {

        public final static int CodeDelete = -5,
                CodeCancel = -3,
                CodePoint = 44;
        //public final static int CodePrev     = 55000;
        //public final static int CodeAllLeft  = 55001;
        //public final static int CodeLeft     = 55002;
        //public final static int CodeRight    = 55003;
        //public final static int CodeAllRight = 55004;
        //public final static int CodeNext     = 55005;
        //public final static int CodeClear    = 55006;

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();

            Log.d("LOG", "onKey() "+primaryCode);
            if (focusCurrent == null || focusCurrent.getClass() != AppCompatEditText.class) return;

            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();

            int start = edittext.getSelectionStart();

            switch (primaryCode) {

                case CodeCancel:
                    hideKeyboard();
                    break;

                case CodeDelete:
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start);
                    }
                    break;


                //case CodePoint:
                //    if (editable.toString().indexOf(",") != -1) {
                //        return;
                //    }
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
                    break;
            }

        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };

    public HexadecimalKeyboard(AppCompatActivity host) {
        Log.d("Keyboard", "FloatKeyboard");
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(R.id.keyboard_view);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        //params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.gravity = Gravity.BOTTOM;
//
//
//        mHostActivity.addContentView(mKeyboardView, params);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.hexadecimal_keyboard));
        mKeyboardView.setPreviewEnabled(true);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Returns whether the FloatKeyboard is visible.
     */
    public boolean isKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the FloatKeyboard visible, and hide the system keyboard for view v.
     */
    public void showFloatKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Make the FloatKeyboard invisible.
     */
    public void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    private void moveCursor(EditText edittext, MotionEvent event) {
        Paint p = new Paint();
        p.setTextSize(edittext.getTextSize());
        edittext.setSelection(Math.max(0, Math.min(edittext.getEditableText().length(),
                Math.round(edittext.getEditableText().length() *
                        (event.getX() - edittext.getPaddingLeft()) / p.measureText(edittext.getEditableText().toString())))));
        edittext.requestFocus();
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param edittext The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(EditText edittext) {
        // Find the EditText 'resid'
        //EditText edittext = (EditText) mHostActivity.findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Log.d("Keyboard", "onFocusChange");
                if (hasFocus) showFloatKeyboard(v);
                else hideKeyboard();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                //Log.d("Keyboard", "onClick");
                showFloatKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                //Log.d("Keyboard", "setOnTouchListener: " + event.getX());
                moveCursor(edittext, event);
                edittext.performClick();
                //edittext.setSelection(index);
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
}
