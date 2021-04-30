package com.example.systemmanageruidemo;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cydroid.softmanager.view.AnimBallView;
import com.example.systemmanageruidemo.actionpresent.OptimisePresent;
import com.example.systemmanageruidemo.actionview.OptimiseView;
import com.example.systemmanageruidemo.adapter.RecyclerAdapter;
import com.example.systemmanageruidemo.bean.DataBean;


import java.util.ArrayList;
import java.util.List;

public class OptimiseActivity extends BaseSupportProxyActivity<OptimisePresent> implements OptimiseView {
    private Context mContext;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private DataBean dataBean;
    private DataBean.DataBeanChild child;

    private List<DataBean> dataBeans=new ArrayList<>();
    private List<DataBean.DataBeanChild> children;

    private TextView scoreCount;
    private TextView mBtnOptimise;

    private AnimBallView animBallView;
    private boolean isStart = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getRealContext();
        setContentView(R.layout.activity_optimise);
        recyclerView = findViewById(R.id.optimising_item_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        initView();
        initData();

    }

    private void initView() {

        scoreCount = (TextView) findViewById(R.id.score_count_view);
        mBtnOptimise = (TextView) findViewById(R.id.optimise_btn);
        animBallView = (AnimBallView)findViewById(R.id.anim_ball_view);
        animBallView.onStartAnim();
    }

    /*数据测试*/
    private void initData() {
        /*设置分数*/
        scoreCount.setText("92");
        mBtnOptimise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "测试", Toast.LENGTH_SHORT).show();
                isStart = !isStart;
                if (isStart) {
                    animBallView.onStartAnim();
                } else {
                    animBallView.onStopAnim();
                }
            }

        });

        /*垃圾种类*/
        String[] type = {UnitUtil.getStr(mContext, R.string.cache_type), UnitUtil.getStr(mContext, R.string.ad_type),
                UnitUtil.getStr(mContext, R.string.unload_type), UnitUtil.getStr(mContext, R.string.package_type)};

        dataBeans = new ArrayList<>();
//        for (int i = 1; i < 5; i++) {
//            dataBean = new DataBean(type[i - 1], i + "00KB", false);
//            children = new ArrayList<>();
//            for (int i1 = 1; i1 < 6; i1++) {
//                child = new DataBean.DataBeanChild(R.drawable.app_manager_new, "软件" + i1, i1 + "00KB", true);
//                children.add(child);
//            }
//            dataBean.setChildren(children);
//            dataBeans.add(dataBean);
//        }
        adapter = new RecyclerAdapter(mContext, dataBeans);
        recyclerView.setAdapter(adapter);
    }

    OptimisePresent presenter;

    @Override
    public void setPresenter(OptimisePresent presenter) {
        this.presenter = presenter;
    }

    @Override
    public OptimisePresent getPresenter(OptimisePresent presenter) {
        return presenter;
    }
}