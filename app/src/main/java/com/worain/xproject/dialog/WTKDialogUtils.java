package com.worain.xproject.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.worain.xproject.R;

/**
 * 本类仅提供对话框的简单提供逻辑与简单布局，不涉及业务逻辑，
 */
public class WTKDialogUtils {

    public static Dialog createAlertDialog(Activity activity, String hint, Drawable iconD, DialogInterface.OnDismissListener dismissListener) {
        return createCommonDialog(activity, ((ViewGroup) activity.getWindow().getDecorView()), R.layout.wtk_tips, null, hint, iconD,
                null, null, dismissListener, false, false, -1,
                true, false, null);
    }

    public static Dialog createCommonDialog(Context context, ViewGroup decorView, int layoutRes, String title, String hint,
                                            Drawable iconD,
                                            View.OnClickListener sureAction,
                                            View.OnClickListener action2,
                                            DialogInterface.OnDismissListener dismissListener,
                                            boolean outsideCancel, boolean autoCancel,
                                            int autoCancelTime, boolean isOnlyfirstShow, boolean isShowNotMoreShowit, String keyword) {
        boolean notNeedShow = false;
        if (keyword != null && keyword.length() > 0)
            notNeedShow = context.getSharedPreferences("mtkdialog", Context.MODE_PRIVATE).getBoolean(keyword, false);
        if (notNeedShow) {
            nextAction(sureAction, dismissListener);
            return null;
        }
        if (isOnlyfirstShow && keyword != null && keyword.length() > 0) {
            SharedPreferences sharep = context.getSharedPreferences("mtkdialog", Context.MODE_PRIVATE);
            notNeedShow = sharep.getBoolean(keyword, false);
            if (notNeedShow) {
                nextAction(sureAction, dismissListener);
                return null;
            } else {
                sharep.edit().putBoolean(keyword, true).commit();
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes, decorView, false);
        MViewHolder viewHolder = new MViewHolder(view);
        if (iconD != null && viewHolder.icon != null) {
            viewHolder.icon.setImageDrawable(iconD);
        }
        if (viewHolder.title != null && title != null) {
            viewHolder.title.setText(title);
        }
        if (viewHolder.hint != null && hint != null) {
            viewHolder.hint.setText(hint);
        }
        if (viewHolder.checkbox != null) {
            viewHolder.checkbox.setVisibility(isShowNotMoreShowit ? View.VISIBLE : View.GONE);
        }
        final Dialog dialog = new Dialog(context);
        if (viewHolder.cancel != null) {
            viewHolder.cancel.setOnClickListener((v) -> {
                if (dialog != null && dialog.getContext() != null) {
                    dialog.dismiss();
                }
            });
        }
        if (viewHolder.sureAction != null) {
            viewHolder.sureAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null && dialog.getContext() != null) {
                        dialog.dismiss();
                    }
                    if (viewHolder.checkbox != null) {
                        if (viewHolder.checkbox.isChecked() && keyword != null && keyword.length() > 0) {
                            SharedPreferences sharep = context.getSharedPreferences("mtkdialog", Context.MODE_PRIVATE);
                            sharep.edit().putBoolean(keyword, true).commit();
                        }
                    }
                    if (sureAction != null) {
                        sureAction.onClick(null);
                    }
                }
            });
        }
        if (viewHolder.action2 != null) {
            viewHolder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null && dialog.getContext() != null) {
                        dialog.dismiss();
                    }
                    if (action2 != null) {
                        action2.onClick(null);
                    }
                }
            });
        }
        dialog.setContentView(view);
        dialog.setOnDismissListener(dismissListener);
        dialog.setCanceledOnTouchOutside(outsideCancel);
        dialog.show();
        dealDialogAfterShow(dialog, context);
        if (autoCancel) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (dialog != null && dialog.getContext() != null) {
                    dialog.dismiss();
                }
            }, autoCancelTime);
        }
        return dialog;
    }

    public static void nextAction(View.OnClickListener sureAction, DialogInterface.OnDismissListener dismissListener) {
        if (sureAction != null) {
            sureAction.onClick(null);
        }
        if (dismissListener != null) {
            dismissListener.onDismiss(null);
        }
    }

    private static void dealDialogAfterShow(Dialog dialog, Context context) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        dialog.getWindow().getDecorView().setBackgroundColor(context.getResources().getColor(android.R.color.transparent, null));
    }

    public static class MViewHolder {
        public TextView title;
        public TextView hint;
        public ImageView icon;
        public View sureAction;
        public View action2;
        //        public View action3;
        public View cancel;
        public CheckBox checkbox;

        public MViewHolder(View convertView) {
            title = convertView.findViewById(R.id.title);
            hint = convertView.findViewById(R.id.hint);
            sureAction = convertView.findViewById(R.id.sure_action);
            icon = convertView.findViewById(R.id.icon);
            action2 = convertView.findViewById(R.id.action2);
            cancel = convertView.findViewById(R.id.cancel);
            checkbox = convertView.findViewById(R.id.checkbox);
        }
    }
}
