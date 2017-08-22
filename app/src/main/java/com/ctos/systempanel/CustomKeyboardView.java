package com.ctos.systempanel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomKeyboardView extends KeyboardView {
    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void showCustomKeyboard() {
        setVisibility(View.VISIBLE);
        setEnabled(true);
        setPreviewEnabled(false);
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    public void hideCustomKeyboard() {
        setVisibility(View.INVISIBLE);
        setEnabled(false);
    }

    public void randomizeKeys() {
        Random randomGenerator = new Random();
        ArrayList<Integer> pool = new ArrayList<Integer>();

        for (int i = KeyEvent.KEYCODE_0; i <= KeyEvent.KEYCODE_9; i++)
            pool.add(i);
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] <= KeyEvent.KEYCODE_9) {
                int index = randomGenerator.nextInt(pool.size());
                key.codes[0] = pool.get(index);
                key.label = (key.codes[0] - KeyEvent.KEYCODE_0) + "";
                pool.remove(index);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(40);

        int gap = 1;
        List<Keyboard.Key> keys = getKeyboard().getKeys();

        for (Keyboard.Key key : keys) {

            Drawable dr = (Drawable) getContext().getResources().getDrawable(R.drawable.gray_color);
            dr.setBounds(key.x + gap, key.y + gap, key.x + key.width - gap, key.y + key.height - gap);
            dr.draw(canvas);

            if (key.codes[0] == KeyEvent.KEYCODE_DEL) {
                Drawable dr1 = (Drawable) getContext().getResources().getDrawable(R.drawable.sym_keyboard_delete);
                dr1.setBounds(key.x + (key.width - 72) / 2, key.y + (key.height - 68) / 2, key.x + (key.width - 72) / 2 + 72, key.y + (key.height - 68) / 2 + 68);
                dr1.draw(canvas);
            } else if (key.codes[0] == KeyEvent.KEYCODE_TAB) {
                Drawable dr1 = (Drawable) getContext().getResources().getDrawable(R.drawable.sym_keyboard_return);
                dr1.setBounds(key.x + (key.width - 72) / 2, key.y + (key.height - 68) / 2, key.x + (key.width - 72) / 2 + 72, key.y + (key.height - 68) / 2 + 68);
                dr1.draw(canvas);
            } else {
                if (key.label != null)
                    canvas.drawText(key.label.toString(), key.x + (key.width - 40) / 2, key.y + (key.height - 40) / 2 + 40, paint);
            }
        }
    }

}
