package com.ctos.systempanel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText firstPasswordText;
    EditText secondPasswordText;
    FloatingActionButton changeButton;
    private Keyboard mKeyboard;
    private CustomKeyboardView mKeyboardView;
    private Activity main;
    private final int MIN_LENGTH_PASSWD = 8;
    private final int MAX_LENGTH_PASSWD = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        main = this;

        firstPasswordText = (EditText) findViewById(R.id.first_password);
        secondPasswordText = (EditText) findViewById(R.id.second_password);

        changeButton = (FloatingActionButton) findViewById(R.id.button_verifyPassword);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                mKeyboardView.showCustomKeyboard();
                return true;
            }
        };

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (mKeyboardView.getVisibility() == View.GONE)
                    mKeyboardView.showCustomKeyboard();
            }
        };

        mKeyboard = new Keyboard(this, R.xml.keyboard);
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(this));
        mKeyboardView.randomizeKeys();
        firstPasswordText.setOnFocusChangeListener(listener);
        secondPasswordText.setOnFocusChangeListener(listener);
        firstPasswordText.setOnTouchListener(touchListener);
        secondPasswordText.setOnTouchListener(touchListener);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_TAB)
            mKeyboardView.hideCustomKeyboard();
        return super.onKeyDown(keyCode, event);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        changeButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verify Password...");
        progressDialog.show();

        String firstPassword = firstPasswordText.getText().toString();
        String secondPassword = secondPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
//        MainActivity.ctosInfo.setPassword(password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);

    }


    public void onSignupSuccess() {
        changeButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        changeButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String firstPassword = firstPasswordText.getText().toString();
        String secondPassword = secondPasswordText.getText().toString();

        if (firstPassword.isEmpty() || firstPassword.length() < MIN_LENGTH_PASSWD || firstPassword.length() > MAX_LENGTH_PASSWD) {
            firstPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;

        } else if (!MainActivity.ctosInfo.getPasswordHash(firstPassword).equals(MainActivity.ctosInfo.getPasswordHash())) {
            firstPasswordText.setError("incorrect password");
            valid = false;

        } else {
            firstPasswordText.setError(null);
        }

        if (secondPassword.isEmpty() || secondPassword.length() < MIN_LENGTH_PASSWD || secondPassword.length() > MAX_LENGTH_PASSWD) {
            secondPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;

        } else if (!MainActivity.ctosInfo.getPasswordHash(secondPassword).equals(MainActivity.ctosInfo.getPasswordHash())) {
            secondPasswordText.setError("incorrect password");
            valid = false;
        } else {
            secondPasswordText.setError(null);
        }


        return valid;
    }
}