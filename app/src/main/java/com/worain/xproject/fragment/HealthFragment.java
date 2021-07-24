package com.worain.xproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.worain.xproject.R;
import com.worain.xproject.healthchartView.HealthChartView;
import com.worain.xproject.healthchartView.ParmBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/23
 *     desc   : ...
 * </pre>
 */
public class HealthFragment extends Fragment {
    private Context context;
    private View view;
    HealthChartView healthChartView;
    List<ParmBean> parmBeans = new ArrayList<>();
    List<ParmBean> aparmBeans = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        view = LayoutInflater.from(context).inflate(R.layout.health_fragmenet, container, false);
        showHealthView();
        return view;
    }


    private void showHealthView() {
        healthChartView = (HealthChartView) view.findViewById(R.id.health_chart_view);
        for (int s = 0; s < 100; s++) {
            Random random = new Random();
            ParmBean.TimerBean timerBean = new ParmBean.TimerBean(String.valueOf(s), String.valueOf(s), String.valueOf(s), String.valueOf(s));
            ParmBean parmBean = new ParmBean(random.nextInt(150), 2, timerBean);
            parmBeans.add(parmBean);
        }
        for (int a = 0; a < 100; a++) {
            Random randoms = new Random();
            ParmBean.TimerBean timerBean = new ParmBean.TimerBean(String.valueOf(a), String.valueOf(a), String.valueOf(a), String.valueOf(a));
            ParmBean aparmBean = new ParmBean(randoms.nextInt(150), 2, timerBean);
            aparmBeans.add(aparmBean);
        }
        healthChartView.setDatas(parmBeans,aparmBeans, ParmBean.VIEW_BLOODPRESSURE);
    }

}
