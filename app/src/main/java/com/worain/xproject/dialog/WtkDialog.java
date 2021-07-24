package com.worain.xproject.dialog;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.worain.xproject.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/05
 *     desc   : 全局dialog 实现2s后自动消失
 * </pre>
 */
public class WtkDialog {
    public final static int VIEW_SUCESS = 0;
    public final static int VIEW_WARNNING = 1;
    public final static int VIEW_SMILE = 2;
    private static WtkDialog INSTANCE;
    private static Activity curActivity;
    private static MDialog mDialog;
    private static final Object lock = new Object();
    private Timer timer;

    DialogInterface.OnDismissListener mDismissListener;

    public void setmDismissListener(DialogInterface.OnDismissListener mDismissListener) {
        this.mDismissListener = mDismissListener;
    }

    private WtkDialog() {
    }

    public WtkDialog showToast(int viewType, String str) {
        if (mDialog == null) {
            synchronized (lock) {
                if (mDialog == null) {
                    mDialog = new MDialog(curActivity);
                    mDialog.setOnDismissListener(v -> {
                        mDialog = null;
                    });
                    mDialog.setOnCancelListener(v -> {
                        mDialog = null;
                    });
                } else {
                    return this;
                }
            }
        }
        if (mDialog.isShowing()) {
            mDialog.showit(viewType, str);
        } else {
            mDialog.showit(viewType, str);
            mDialog.show();
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelWtkDialog();
            }
        }, 2000);
        return this;
    }

    protected void cancelWtkDialog() {
        if (mDialog != null) {
            if (curActivity != null) {
                curActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        mDialog = null;
                        if (mDismissListener != null) {
                            mDismissListener.onDismiss(null);
                        }
                    }
                });
            }
        }
    }

    public static WtkDialog getInstance() {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = new WtkDialog();
                }
            }
        }
        return INSTANCE;
    }

    private static class MDialog extends Dialog {
        private ImageView mIv;
        private TextView mTvMessage;

        public MDialog(Context context) {
            super(context);
            initView();
        }

        private void initView() {
            setContentView(R.layout.wtk_dialog);
            mIv = findViewById(R.id.iv);
            mTvMessage = findViewById(R.id.tv_message);
        }

        public void showit(int viewType, String str) {
            mTvMessage.setText(str);
            if (viewType == VIEW_SUCESS) {
                mIv.setImageResource(R.drawable.ic_success);
            } else if (viewType == VIEW_WARNNING) {
                mIv.setImageResource(R.drawable.ic_warning);
            } else if (viewType == VIEW_SMILE) {
                mIv.setImageResource(R.drawable.ic_smile);
            }
        }
    }


    public Application.ActivityLifecycleCallbacks createLifeCallback() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (curActivity != null && curActivity != activity) {
                    cancelWtkDialog();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                curActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (curActivity != null && curActivity == activity) {
                    cancelWtkDialog();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }
}
