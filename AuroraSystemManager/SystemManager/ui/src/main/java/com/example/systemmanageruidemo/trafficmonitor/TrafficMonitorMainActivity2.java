package com.example.systemmanageruidemo.trafficmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.systemmanageruidemo.BaseSupportProxyActivity;
import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.UnitUtil;
import com.example.systemmanageruidemo.actionpresent.TrafficMonitorPresent2;
import com.example.systemmanageruidemo.actionview.TrafficMonitorView2;
import com.example.systemmanageruidemo.trafficmonitor.adapter.TraRecyAdapter2;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraPagerBean;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraRecyBean;
import com.example.systemmanageruidemo.view.ChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrafficMonitorMainActivity2 extends BaseSupportProxyActivity<TrafficMonitorPresent2> implements TrafficMonitorView2 {
    TraPagerBean traPagerBean = new TraPagerBean();
    int simcount;
    int currentSim;
    TrafficMonitorPresent2 presenter;
    private Context mContext;
    private FrameLayout mNosim;
    private LinearLayout mHassim;
    private FrameLayout mDoublesim;
    private ViewPager2 mTrafficViewpager;
    private FrameLayout mSinglesim;
    private LinearLayout mNosettrafficLay;
    private Button mSettrafficBtn;
    private LinearLayout mSettrafficLay;
    private TextView mSettextTrafficText;
    private LinearLayout mSavetrafficLay;
    private TextView mSavetextTrafficText;
    private TextView mSaveresult;
    private LinearLayout mProtectedtrafficLay;
    private TextView mProtectedtextTrafficText;
    private TextView mUsetrafficTextHint;
    private ChartView mCharview;
    private TextView mAppuseTrafficTextHint;
    private RecyclerView mRecycleview;
    private final MViewPager msimViewPagerAdapter = new MViewPager();
    private final ViewPager.OnPageChangeListener mObPageListenter = new MPageListenter();
    private final List<TraRecyBean> mlist = new ArrayList<>();
    private TraRecyAdapter2 recyAdapter;
    private final ViewPager2.OnPageChangeCallback mPageChangerCallBack = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (currentSim != position) {
                changeCurrentSim(position);
                currentSim = position;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getRealContext();
        setContentView(R.layout.activity_traffic_monitor_main2);
        initView();
        loaddata();
    }

    private void loaddata() {
        requestSIM(traPagerBean);
    }

    private void setdata(TraPagerBean object) {
        if (object != null) {
            simcount = object.getSimCardScount();
            if (simcount == 0) {
                showNosim();
            } else if (simcount == 1) {
                showSingleSim();
            } else if (simcount == 2) {
                showMultSim();
            }
            if (simcount != 0) {
                initBlowSimView();
                changeCurrentSim(currentSim);
            }
        }
    }

    private void initBlowSimView() {
        NetworkPolicyManager mPolicyManager = NetworkPolicyManager.from(mContext);
        boolean isrestricted = mPolicyManager.getRestrictBackground();
        mSaveresult.setText(isrestricted ? getString(R.string.open) : getString(R.string.closed));
    }

    /**
     * sim 卡对应的下方数据
     *
     * @param currentSim
     */
    private void changeCurrentSim(int currentSim) {
        TraPagerBean.SIMBean object = traPagerBean.getList().get(currentSim);
        if (object.isIssetted()) {
            mNosettrafficLay.setVisibility(View.GONE);
            mSettrafficLay.setVisibility(View.VISIBLE);
        } else {
            mNosettrafficLay.setVisibility(View.VISIBLE);
            mSettrafficLay.setVisibility(View.GONE);
        }
        String f = getResources().getString(R.string.usetraffic_text_hint);
        mUsetrafficTextHint.setText(String.format(f, UnitUtil.convertStorage3(traPagerBean.getList().get(currentSim).getUsedFlow())));
        requestChartData(null, currentSim);
        initData(mlist, currentSim);
    }

    public void requestChartData(List<ChartView.Info> list, int currentSim) {
        presenter.onRequestChartData(list, currentSim);
    }

    public void onResponseChartData(List<ChartView.Info> list, int currentSim) {
        mCharview.setmInfos(list);
    }

    public void showMultSim() {
        mHassim.setVisibility(View.VISIBLE);
        mSinglesim.setVisibility(View.GONE);
        mDoublesim.setVisibility(View.VISIBLE);
        mTrafficViewpager.setAdapter(msimViewPagerAdapter);
        me.relex.circleindicator.CircleIndicator3 indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mTrafficViewpager);
//        mTrafficViewpager.addOnPageChangeListener(mObPageListenter);
        mTrafficViewpager.registerOnPageChangeCallback(mPageChangerCallBack);
    }

    public void showSingleSim() {
        mHassim.setVisibility(View.VISIBLE);
        mSinglesim.setVisibility(View.VISIBLE);
        mDoublesim.setVisibility(View.GONE);
        currentSim = 0;
        bindSimCard(currentSim, mSinglesim);
    }

    public void showNosim() {
        mNosim.setVisibility(View.VISIBLE);
        mHassim.setVisibility(View.GONE);
        startActivity(new Intent(mContext,TrafficMonitorSettingActivity.class));
    }

    private void initView() {
        mNosim = findViewById(R.id.nosim);
        mHassim = findViewById(R.id.hassim);
        mDoublesim = findViewById(R.id.doublesim);
        mTrafficViewpager = findViewById(R.id.traffic_viewpager);
        mSinglesim = findViewById(R.id.singlesim);
        mNosettrafficLay = findViewById(R.id.nosettraffic_lay);
        mSettrafficBtn = findViewById(R.id.settraffic_btn);
        mSettrafficLay = findViewById(R.id.settraffic_lay);
        mSettextTrafficText = findViewById(R.id.settext_traffic_text);
        mSavetrafficLay = findViewById(R.id.savetraffic_lay);
        mSavetextTrafficText = findViewById(R.id.savetext_traffic_text);
        mSaveresult = findViewById(R.id.saveresult);
        mProtectedtrafficLay = findViewById(R.id.protectedtraffic_lay);
        mProtectedtextTrafficText = findViewById(R.id.protectedtext_traffic_text);
        mUsetrafficTextHint = findViewById(R.id.usetraffic_text_hint);
        mCharview = findViewById(R.id.charview);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        mCharview.getMchartConfig().textFormatter = new ChartView.TextFormatter() {
            @Override
            public String textXFormatter(long data) {
                return simpleDateFormat.format(new Date(data));
            }

            @Override
            public String textYFormatter(long data) {
                return UnitUtil.convertStorage(data);
            }

            @Override
            public String textXAndYFormatter(long x, long y) {
                return null;
            }

            @Override
            public long getDefaultX() {
                return System.currentTimeMillis();
            }
        };
        mAppuseTrafficTextHint = findViewById(R.id.appuse_traffic_text_hint);
        mRecycleview = findViewById(R.id.recycleview);
        mSavetrafficLay.setOnClickListener(view -> {
            Intent intent = new Intent("android.settings.DATA_SAVER_SETTINGS");
            startActivity(intent);
        });
        mProtectedtrafficLay.setOnClickListener(view -> {
            Intent intent = new Intent("com.wheatek.security.NETWORK_DATA_CONTROLLER");
//            Intent intent = new Intent("com.mediatek.security.NETWORK_DATA_CONTROLLER");
            startActivity(intent);
        });
        mRecycleview.setLayoutManager(new LinearLayoutManager(mContext));
        recyAdapter = new TraRecyAdapter2(mContext, null);
        mRecycleview.setAdapter(recyAdapter);
    }

    @Override
    public void setPresenter(TrafficMonitorPresent2 presenter) {
        this.presenter = presenter;
    }

    @Override
    public TrafficMonitorPresent2 getPresenter(TrafficMonitorPresent2 presenter) {
        return presenter;
    }


    @Override
    public void requestSIM(TraPagerBean object) {
        presenter.onRequestSIM(object);
    }

    @Override
    public void onResponseSIM(TraPagerBean object) {
        setdata(object);
    }

    @Override
    public void initData(List<TraRecyBean> list, int simindex) {
        presenter.onInitData(list, simindex);
    }

    @Override
    public void onRefresh(List<TraRecyBean> list) {
        recyAdapter.setDatas(list);
        recyAdapter.notifyDataSetChanged();
    }

    @Override
    public void appChangeState(TraRecyBean object, boolean ischecked) {
        presenter.onAppChangeState(object, ischecked);
    }

    private void bindSimCard(int position, View viewGroup) {
        SimViewHolder simViewHolder = new SimViewHolder(viewGroup);
        bindSimCard(position, simViewHolder);
    }

    private void bindSimCard(int position, SimViewHolder holder) {
        TraPagerBean.SIMBean sim = traPagerBean.getList().get(position);
        if (traPagerBean.getList().size() == 1) {
            holder.mNameSim.setVisibility(View.GONE);
        }
        holder.mNameSim.setText("SIM卡" + (position + 1));
        holder.mNameCmc.setText(sim.getName());
        holder.mPhoneNumber.setText(sim.getNumber());
        holder.mAlltraff.setText("总套餐：" + UnitUtil.convertStorage3(sim.getTraPack()));
        holder.mUsetraff.setText("套餐已用：" + UnitUtil.convertStorage3(sim.getUsedFlow()));
        String[] us = UnitUtil.convertStorage4(sim.getSurplusFlow());
        holder.mSurplusFlow.setText(us[0]);
        holder.mUnit.setText(us[1]);
    }

    public static class SimViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameSim;
        private final TextView mNameCmc;
        private final TextView mPhoneNumber;
        private final TextView mSurplusFlow;
        private final TextView mUnit;
        private final TextView mAlltraff;
        private final TextView mUsetraff;

        private SimViewHolder(View view) {
            super(view);
            mNameSim = view.findViewById(R.id.name_sim);
            mNameCmc = view.findViewById(R.id.name_cmc);
            mPhoneNumber = view.findViewById(R.id.phone_number);
            mSurplusFlow = view.findViewById(R.id.surplus_flow);
            mUnit = view.findViewById(R.id.unit);
            mAlltraff = view.findViewById(R.id.alltraff);
            mUsetraff = view.findViewById(R.id.usetraff);
        }
    }

    private class MViewPager extends RecyclerView.Adapter<SimViewHolder> {

//        @Override
//        public int getCount() {
//            return simcount;
//        }
//
//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            return view == object;
//        }
//
//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
////            return super.instantiateItem(container, position);
//            View view = createSimCard(container);
//            container.addView(view);
//            bindSimCard(position, view);
//            return view;
//        }
//
//        private View createSimCard(ViewGroup container) {
//            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.traffic_simcard_container, container, false);
//            return view;
//        }

        @NonNull
        @Override
        public SimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.traffic_simcard_container, parent, false);
            return new SimViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SimViewHolder holder, int position) {
            bindSimCard(position, holder);
        }

        @Override
        public int getItemCount() {
            return simcount;
        }
    }

    private class MPageListenter implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentSim = position;
            changeCurrentSim(currentSim);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


}