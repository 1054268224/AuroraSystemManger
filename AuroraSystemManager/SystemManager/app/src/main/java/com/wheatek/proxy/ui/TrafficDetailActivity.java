package com.wheatek.proxy.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cydroid.softmanager.R;
import com.cydroid.softmanager.trafficassistant.net.UidDetail;
import com.cydroid.softmanager.trafficassistant.net.UidDetailProvider;
import com.cydroid.systemmanager.BaseWheatekActivity;
import com.example.systemmanageruidemo.UnitUtil;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraRecyBean;
import com.mediatek.security.datamanager.CheckedPermRecord;
import com.mediatek.security.service.INetworkDataControllerService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TrafficDetailActivity extends BaseWheatekActivity {

    private ImageView mIcon;
    private TextView mTime;
    private TextView mAlltraff;
    private TextView mQiantai;
    private TextView mHoutai;
    private LinearLayout mLay;
    private Switch mSwitchBtn;
    private LinearLayout mLay2;
    private Switch mSwitchBtn2;
    private LinearLayout mLay3;
    private Switch mSwitchBtn3;
    TraRecyBean bean;
    boolean iswifi;
    boolean isbacklimitwhite;
    boolean isnetwork;
    boolean iscanshowpermission;
    private INetworkDataControllerService mNetworkDataControllerBinder;
    private boolean mShouldUnbind;
    private int uid;
    private com.mediatek.security.datamanager.CheckedPermRecord checkedPermRecord;
    private CompoundButton.OnCheckedChangeListener listener1 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isnetwork = isChecked;
            if (isnetwork) {
                iswifi = true;
                mSwitchBtn2.setOnCheckedChangeListener(null);
                mSwitchBtn2.setChecked(true);
                mSwitchBtn2.setOnCheckedChangeListener(listener2);
            }
            changepermission();
        }
    };
    private NetworkPolicyManager manager;
    private UidDetailProvider mUidDetailProvider;

    private void changepermission() {
        checkedPermRecord.setStatus(getPermmissionStatue());
        try {
            mNetworkDataControllerBinder.modifyNetworkDateRecord(checkedPermRecord);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private CompoundButton.OnCheckedChangeListener listener2 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            iswifi = isChecked;
            if (!iswifi) {
                isnetwork = false;
                mSwitchBtn.setOnCheckedChangeListener(null);
                mSwitchBtn.setChecked(false);
                mSwitchBtn.setOnCheckedChangeListener(listener1);
            }
            changepermission();
        }
    };
    private CompoundButton.OnCheckedChangeListener listener3 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            manager.setUidPolicy(uid, isChecked ? NetworkPolicyManager.POLICY_ALLOW_METERED_BACKGROUND : NetworkPolicyManager.POLICY_REJECT_METERED_BACKGROUND);
        }
    };

    public static Intent showIntent(Context context, TraRecyBean bean) {
        Intent intent = new Intent(context, TrafficDetailActivity.class);
        Bundle arguments = new Bundle();
        arguments.putParcelable("detailbean", bean);
        intent.putExtras(arguments);
        return intent;
    }

    public static TraRecyBean showBean(Intent intent) {
        return intent.getExtras().getParcelable("detailbean");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            bean = showBean(getIntent());
        }
        setContentView(R.layout.traffic_detail_activity_wyh);
        mUidDetailProvider = new UidDetailProvider(this);
        initView();
        if (bean != null) {
            binddata();
        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void binddata() {
//        mIcon.setImageDrawable(bean.getImageId());
        ((TextView) findViewById(R.id.textView)).setText(bean.getName());
        mTime.setText(getcurrentmonthday());
        mAlltraff.setText(UnitUtil.convertStorage(bean.getUsedTraSize()));
        mQiantai.setText(UnitUtil.convertStorage(bean.getQiantai()));
        mHoutai.setText(UnitUtil.convertStorage(bean.getHoutai()));
        UidDetail detail = mUidDetailProvider.getUidDetail(bean.getKey(), true);
        mIcon.setImageDrawable(detail.icon);
        getPermission();
    }

    private void bindPermission() {
        if (!iscanshowpermission) {
            mLay.setVisibility(View.GONE);
            mLay2.setVisibility(View.GONE);
            mLay3.setVisibility(View.GONE);
        } else {
            mLay.setVisibility(View.VISIBLE);
            mLay2.setVisibility(View.VISIBLE);
            mLay3.setVisibility(View.VISIBLE);
            mSwitchBtn.setChecked(isnetwork);
            mSwitchBtn.setOnCheckedChangeListener(listener1);
            mSwitchBtn2.setChecked(iswifi);
            mSwitchBtn2.setOnCheckedChangeListener(listener2);
            mSwitchBtn3.setChecked(isbacklimitwhite);
            mSwitchBtn3.setOnCheckedChangeListener(listener3);
        }
    }


    private String getcurrentmonthday() {
        String language = Locale.getDefault().getDisplayLanguage();
        String[] strings;
        if (language.contains("en") || language.equals("English")) {
            strings = getTime(new SimpleDateFormat("MMM d", Locale.ENGLISH), new SimpleDateFormat("d", Locale.ENGLISH));
            return strings[0] + "-" + strings[1];
        } else {
            strings = getTime(new SimpleDateFormat("MM月dd日"), new SimpleDateFormat("dd日"));
            return strings[0] + "至" + strings[1];
        }
    }

    private String[] getTime(SimpleDateFormat start, SimpleDateFormat end) {
        String[] strings = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(calendar.SECOND, 0);
        strings[0] = start.format(new Date(calendar.getTimeInMillis()));
        strings[1] = end.format(new Date(System.currentTimeMillis()));
        return strings;
    }

    private void getPermission() {
        manager = NetworkPolicyManager.from(this);
        PackageManager packageManager = getPackageManager();
        uid = bean.getKey();
        if (uid != -1) {
            for (int cuid : manager.getUidsWithPolicy(NetworkPolicyManager.POLICY_ALLOW_METERED_BACKGROUND)) {
                if (uid == cuid) {
                    isbacklimitwhite = true;
                    break;
                }
            }
            bindService();
        }
    }

    private void getPermissionFromStatue(int status) {
        switch (status) {
            case CheckedPermRecord.STATUS_DENIED:
                isnetwork = false;
                iswifi = false;
                break;
            case CheckedPermRecord.STATUS_WIFI_ONLY:
                isnetwork = false;
                iswifi = true;
                break;
            case CheckedPermRecord.STATUS_GRANTED:
                isnetwork = true;
                iswifi = true;
                break;
            default:
                isnetwork = false;
                iswifi = false;
                break;
        }
    }

    private int getPermmissionStatue() {
        int re = CheckedPermRecord.STATUS_DENIED;
        if (isnetwork) re = CheckedPermRecord.STATUS_GRANTED;
        else if (iswifi) re = CheckedPermRecord.STATUS_WIFI_ONLY;
        return re;
    }

    private void bindService() {
        Intent intent = new Intent("com.mediatek.security.START_SERVICE");
        intent.setClassName("com.mediatek.security.service",
                "com.mediatek.security.service.NetworkDataControllerService");
        mShouldUnbind = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (mShouldUnbind) {
            unbindService(mServiceConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        mUidDetailProvider.clearCache();
        mUidDetailProvider = null;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mNetworkDataControllerBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mNetworkDataControllerBinder = INetworkDataControllerService.Stub.asInterface(service);
            try {
                checkedPermRecord = mNetworkDataControllerBinder.getNetworkDataRecord(uid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iscanshowpermission = (bean.getKey() != android.os.Process.SYSTEM_UID);
            getPermissionFromStatue(checkedPermRecord.getStatus());
            runOnUiThread(() -> {
                bindPermission();
            });
        }
    };

    private void initView() {
        mIcon = findViewById(R.id.icon);
        mTime = findViewById(R.id.time);
        mAlltraff = findViewById(R.id.alltraff);
        mQiantai = findViewById(R.id.qiantai);
        mHoutai = findViewById(R.id.houtai);
        mLay = findViewById(R.id.lay);
        mSwitchBtn = findViewById(R.id.switch_btn);
        mLay2 = findViewById(R.id.lay2);
        mSwitchBtn2 = findViewById(R.id.switch_btn2);
        mLay3 = findViewById(R.id.lay3);
        mSwitchBtn3 = findViewById(R.id.switch_btn3);
    }
}
