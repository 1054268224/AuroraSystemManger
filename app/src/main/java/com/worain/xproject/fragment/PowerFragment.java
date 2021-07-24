package com.worain.xproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.worain.xproject.PowerAnimView;
import com.worain.xproject.R;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/23
 *     desc   : ...
 * </pre>
 */
public class PowerFragment extends Fragment {
    private Context context;
    private View view;
    PowerAnimView powerOffView;
    PowerAnimView powerRestartView;
    TextView tvBtn;
    TextView tvLogo;
    private TextView mTvOff;
    private TextView mTvRestart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        view = LayoutInflater.from(context).inflate(R.layout.power_fragment, container, false);
        showPowerAnimView();
        return view;
    }

    private void showPowerAnimView() {
        powerOffView = view.findViewById(R.id.power_off);
        powerRestartView = view.findViewById(R.id.power_restart);
        tvBtn = view.findViewById(R.id.tv);
        tvLogo = view.findViewById(R.id.tv_logo);
        mTvRestart = view.findViewById(R.id.tv_restart);
        mTvOff = view.findViewById(R.id.tv_off);
        powerOffView.setBg(getResources().getColor(R.color.power_red, null));
        powerOffView.setSvg(getResources().getDrawable(R.drawable.ic_power_off, null));
        powerRestartView.setBg(getResources().getColor(R.color.power_green, null));
        powerRestartView.setSvg(getResources().getDrawable(R.drawable.ic_power_restart, null));

        powerOffView.setDoActionListener(new PowerAnimView.DoActionListener() {
            @Override
            public void doChangeView(boolean isclick) {
                mask(powerOffView, isclick);

            }

            @Override
            public void doAction(boolean isclick) {
                if (isclick) {
                    Toast.makeText(context, "关机。。。。。", Toast.LENGTH_SHORT).show();
                    cancelMask();
                } else {
                    Toast.makeText(context, "安全模式。。。。。", Toast.LENGTH_SHORT).show();
                    cancelMask();
                }

            }

            @Override
            public void doCancel(boolean isclick) {
                cancelMask();
            }
        });

        powerRestartView.setDoActionListener(new PowerAnimView.DoActionListener() {
            @Override
            public void doChangeView(boolean isclick) {
                mask(powerRestartView, isclick);

            }

            @Override
            public void doAction(boolean isclick) {
                if (isclick) {
                    Toast.makeText(context, "重启。。。。", Toast.LENGTH_SHORT).show();
                    cancelMask();
                } else {
                    Toast.makeText(context, "安全模式。。。。。", Toast.LENGTH_SHORT).show();
                    cancelMask();
                }
            }

            @Override
            public void doCancel(boolean isclick) {
                cancelMask();
            }
        });

        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerOffView.reset();
                powerRestartView.reset();
                cancelMask();
            }
        });
    }

    public synchronized void mask(PowerAnimView power, boolean isClick) {
        if (power.isEnabled()) {
            tvLogo.setAlpha(0.3f);
            tvLogo.setEnabled(false);
            mTvOff.setAlpha(0.3f);
            mTvOff.setEnabled(false);
            mTvRestart.setAlpha(0.3f);
            mTvRestart.setEnabled(false);
            if (!isClick) {
                tvBtn.setAlpha(0.3f);
                tvBtn.setEnabled(false);
            }
            if (power == powerOffView) {
                powerRestartView.setAlpha(0.3f);
                powerRestartView.setEnabled(false);
            }
            if (power == powerRestartView) {
                powerOffView.setAlpha(0.3f);
                powerOffView.setEnabled(false);
            }
        }
    }

    public void cancelMask() {
        tvBtn.setAlpha(1f);
        tvBtn.setEnabled(true);
        tvLogo.setAlpha(1f);
        tvLogo.setEnabled(true);
        mTvOff.setAlpha(1f);
        mTvOff.setEnabled(true);
        mTvRestart.setAlpha(1f);
        mTvRestart.setEnabled(true);
        powerOffView.setAlpha(1f);
        powerOffView.setEnabled(true);
        powerRestartView.setAlpha(1f);
        powerRestartView.setEnabled(true);
    }
}
