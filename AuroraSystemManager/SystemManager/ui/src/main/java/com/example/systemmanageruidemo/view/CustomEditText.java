package com.example.systemmanageruidemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.trafficmonitor.TraSettingDialog;

public class CustomEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    private Drawable mDraClear;
    private Context context;
    private final static int len = 7;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        this.context = context;
        initView();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        isDraClearVisible(s.length() > 0);

    }

    @Override
    public void afterTextChanged(Editable s) {

        String inputStr = s.toString().trim();
        byte[] bytes = inputStr.getBytes();
        if (bytes.length > len) {
            Toast.makeText(context, "只能输入这么多了哟~~", Toast.LENGTH_SHORT).show();
            byte[] newBytes = new byte[len];
            for (int i = 0; i < len; i++) {
                newBytes[i] = bytes[i];
            }
            String newStr = new String(newBytes);
            setText(newStr);
            //将光标定位到最后
            Selection.setSelection(getEditableText(), newStr.length());
        }
        if (onClickListener != null) {
            onClickListener.onClick(s.toString());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            isDraClearVisible(getText().length() > 0);
        } else {
            isDraClearVisible(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean xTouchable = event.getX() > (getWidth() - getPaddingRight() - mDraClear.getIntrinsicWidth())
                        && (event.getX() < (getWidth() - getPaddingRight()));

                boolean yTouchable = event.getY() > (getHeight() - mDraClear.getIntrinsicHeight()) / 2
                        && event.getY() < (getHeight() + mDraClear.getIntrinsicHeight()) / 2;

                //清除文本
                if (xTouchable && yTouchable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void startShake(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(500);
        startAnimation(translateAnimation);
    }

    private void initView() {
        mDraClear = getResources().getDrawable(R.drawable.ic_tra_edit, null);
        mDraClear.setBounds(0, 0, mDraClear.getIntrinsicWidth(), mDraClear.getIntrinsicHeight());
        isDraClearVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    private void isDraClearVisible(Boolean isShow) {
        Drawable rightDrawable = isShow ? mDraClear : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                rightDrawable, getCompoundDrawables()[3]);
    }

    public interface OnEditListener {
        void onClick(String strEdit);
    }

    public OnEditListener onClickListener;

    public CustomEditText setOnEditListener(OnEditListener listener) {
        onClickListener = listener;
        return this;
    }
}
