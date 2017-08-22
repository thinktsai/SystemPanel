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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText oldPasswordText;
    EditText passwordText;
    EditText reEnterPasswordText;
    FloatingActionButton changeButton;
    FloatingActionButton cancelButton;
    private Keyboard mKeyboard;
    private CustomKeyboardView mKeyboardView;
    private Activity main;
    private final int MIN_LENGTH_PASSWD = 8;
    private final int MAX_LENGTH_PASSWD = 12;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        main = this;

        oldPasswordText = (EditText) findViewById(R.id.input_oldPassword);
        passwordText = (EditText) findViewById(R.id.input_password);
        reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);

        changeButton = (FloatingActionButton) findViewById(R.id.button_change);
        cancelButton = (FloatingActionButton) findViewById(R.id.button_cancel);


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
        oldPasswordText.setOnFocusChangeListener(listener);
        passwordText.setOnFocusChangeListener(listener);
        reEnterPasswordText.setOnFocusChangeListener(listener);
        oldPasswordText.setOnTouchListener(touchListener);
        passwordText.setOnTouchListener(touchListener);
        reEnterPasswordText.setOnTouchListener(touchListener);


        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
//                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                //               startActivity(intent);
                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String oldPassword = oldPasswordText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        MainActivity.ctosInfo.setPassword(password);

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
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        changeButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String oldPassword = oldPasswordText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (oldPassword.isEmpty() || password.length() < MIN_LENGTH_PASSWD || password.length() > MAX_LENGTH_PASSWD) {
            oldPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!MainActivity.ctosInfo.getPasswordHash(oldPassword).equals(MainActivity.ctosInfo.getPasswordHash())) {
            oldPasswordText.setError("incorrect password");
            valid = false;
        } else {
            oldPasswordText.setError(null);
        }


        if (password.isEmpty() || password.length() < MIN_LENGTH_PASSWD || password.length() > MAX_LENGTH_PASSWD) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < MIN_LENGTH_PASSWD
                || reEnterPassword.length() > MAX_LENGTH_PASSWD || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }
}