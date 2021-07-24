package com.worain.xproject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.worain.xproject.fragment.HealthFragment;
import com.worain.xproject.healthchartView.HealthChartView;
import com.worain.xproject.healthchartView.ParmBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final int VIEW_HEALTH_CHART_VIEW = 0;
    public static final int VIEW_POWER_AnimView = 1;
    Context context;
    private FragmentManager fragmentManager;
    private HealthFragment healthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        showView(VIEW_HEALTH_CHART_VIEW);
    }

    private void showView(int viewType) {
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        if (viewType == VIEW_HEALTH_CHART_VIEW) {
            if (healthFragment == null) {
                healthFragment = new HealthFragment();
                fTransaction.add(R.id.rl_content, healthFragment);
            } else {
                fTransaction.show(healthFragment);
            }
        } else if (viewType == VIEW_POWER_AnimView) {
        }
        fTransaction.commit();
    }
}