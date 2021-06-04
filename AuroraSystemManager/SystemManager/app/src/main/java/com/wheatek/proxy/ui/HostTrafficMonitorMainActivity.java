package com.wheatek.proxy.ui;

import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicyManager;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.SparseArray;


import com.cydroid.softmanager.common.MainProcessSettingsProviderHelper;
import com.cydroid.softmanager.trafficassistant.SIMInfoWrapper;
import com.cydroid.softmanager.trafficassistant.SIMParame;
import com.cydroid.softmanager.trafficassistant.controler.TrafficCalibrateControler;
import com.cydroid.softmanager.trafficassistant.net.AppItem;
import com.cydroid.softmanager.trafficassistant.net.SummaryForAllUidLoader;
import com.cydroid.softmanager.trafficassistant.net.UidDetail;
import com.cydroid.softmanager.trafficassistant.net.UidDetailProvider;
import com.cydroid.softmanager.trafficassistant.utils.Constant;
import com.cydroid.softmanager.trafficassistant.utils.MobileTemplate;
import com.cydroid.softmanager.trafficassistant.utils.TimeFormat;
import com.cydroid.softmanager.trafficassistant.utils.TrafficassistantUtil;
import com.example.systemmanageruidemo.actionpresent.TrafficMonitorPresent2;
import com.example.systemmanageruidemo.actionview.TrafficMonitorView2;
import com.example.systemmanageruidemo.trafficmonitor.TrafficMonitorMainActivity2;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraPagerBean;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraRecyBean;
import com.example.systemmanageruidemo.view.ChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.net.NetworkPolicyManager.POLICY_REJECT_METERED_BACKGROUND;
import static com.cydroid.softmanager.trafficassistant.AppDetailActivity.isInvalidNetworkControlApp;
import static com.cydroid.softmanager.trafficassistant.TrafficRankActivity.getAppsUsingMobileData;

public class HostTrafficMonitorMainActivity extends HostProxyActivity<TrafficMonitorView2> implements TrafficMonitorPresent2 {
    {
        attach(new TrafficMonitorMainActivity2());
    }

    public static void TrafficPackageSettingNoti(Context context) {
        if (TrafficassistantUtil.getSimCount(context) == 0) {
            return;
        }
        int simIndex = TrafficassistantUtil.getSimCardNo(context);
        MainProcessSettingsProviderHelper providerHelper = new MainProcessSettingsProviderHelper(context);
        if (providerHelper.getBoolean(TrafficassistantUtil.getSimSetting(simIndex), false)) { // setting
            return;
        }
        // Gionee: mengdw <2016-06-29> delete for CR01724694 begin
        // popNoti(context, simIndex);
        // Gionee: mengdw <2016-06-29> delete for CR01724694 end
        providerHelper.putBoolean(TrafficassistantUtil.getSimNotification(simIndex), true);
    }

    private static final String TAG = HostTrafficMonitorMainActivity.class.getSimpleName();
    private TrafficMonitorView2 viewAvtion;
    private Context mContext;
    private TrafficCalibrateControler mTrafficCalibrateControler;
    private INetworkStatsService mStatsService;
    private INetworkStatsSession mStatsSession;
    private NetworkPolicyManager mPolicyManager;
    private NetworkTemplate[] mTemplate;
    private UidDetailProvider mUidDetailProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle(R.string.traffic_control_summary);
        setSimpleSupportABar(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUidDetailProvider.clearCache();
        mUidDetailProvider = null;
        TrafficStats.closeQuietly(mStatsSession);
    }

    private void init() {
        mContext = this;
//        TrafficMonitorControlService.processMonitorControlServiceIntent(mContext, true);
        mTrafficCalibrateControler = TrafficCalibrateControler.getInstance(mContext);
        mUidDetailProvider = new UidDetailProvider(mContext);
        mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager
                .getService(Context.NETWORK_STATS_SERVICE));
        mPolicyManager = NetworkPolicyManager.from(mContext);
        try {
            mStatsSession = mStatsService.openSession();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
//        initNetworkInfo(mContext, 0);
    }

    private void initNetworkInfo(Context context, int simIndex) {
        mTemplate = new NetworkTemplate[data.getList().size()];
        for (int i = 0; i < data.getList().size(); i++) {
            mTemplate[i] = MobileTemplate.getTemplate(context, (data.getList().get(i).getSlot()));
        }
    }

    @Override
    public void setViewAction(TrafficMonitorView2 viewAvtion) {
        this.viewAvtion = viewAvtion;
    }

    @Override
    public TrafficMonitorView2 getViewAction() {
        return viewAvtion;
    }

    @Override
    public void onRequestSIM(TraPagerBean object) {
        this.data = object;
        startRequesSIM();
    }

    private void startRequesSIM() {
        SIMInfoWrapper wrapper = SIMInfoWrapper.getDefault(mContext);
        int count = wrapper.getInsertedSimCount();
        data.setList(new ArrayList<>());
        for (int i = 0; i < count; i++) {
            SIMParame simInfo = wrapper.getInsertedSimInfo().get(i);
            String name = simInfo.mDisplayName;
            long id = simInfo.mSimId;
            TraPagerBean.SIMBean bean = new TraPagerBean.SIMBean(id, name);
            bean.setNumber(simInfo.mNumber);
            bean.setIssetted(mTrafficCalibrateControler.isTafficPackageSetted(mContext, simInfo.mSlot));
            bean.setSlot(simInfo.mSlot);
            bean.setmICCId(simInfo.mICCId);
            data.getList().add(bean);
//            int[] todayDate = TimeFormat.getNowTimeArray();
//            long startTime = TimeFormat.getStartTime(todayDate[0], todayDate[1] + 1, todayDate[2], 0, 0, 0);
//            bean.setUsedFlow(TrafficassistantUtil.getTrafficData(mContext, i, startTime, 0, 0));
            bean.setUsedFlow(mTrafficCalibrateControler.getCommonUsedTaffic(mContext, simInfo.mSlot));
            bean.setTraPack(mTrafficCalibrateControler.getCommonTotalTaffic(mContext, simInfo.mSlot));
            bean.setSurplusFlow(mTrafficCalibrateControler.getCommonLeftTraffic(mContext, simInfo.mSlot));
        }
        data.setSimCardScount(data.getList().size());
        initNetworkInfo(mContext, 0);
        responseSIM(data);
    }

    private final LoaderManager.LoaderCallbacks<NetworkStats> mSummaryCallbacks = new LoaderManager.LoaderCallbacks<NetworkStats>() {

        @Override
        public Loader<NetworkStats> onCreateLoader(int id, Bundle args) {
            return new SummaryForAllUidLoader(mContext, mStatsSession, args);
        }

        @Override
        public void onLoadFinished(Loader<NetworkStats> arg0, NetworkStats arg1) {
            final int[] restrictedUids = mPolicyManager.getUidsWithPolicy(POLICY_REJECT_METERED_BACKGROUND);
            refreshListView(arg1, restrictedUids);
        }

        @Override
        public void onLoaderReset(Loader<NetworkStats> arg0) {
            refreshListView(null, new int[0]);
        }
    };


    // 查询图标状态的耗时操作，注意不要占用主线程
    private void refreshListView(final NetworkStats stats, final int[] restrictedUids) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            fun(stats, restrictedUids);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fun(stats, restrictedUids);
                }
            }).start();
        }
    }
//    /**
//     * Bind the given {@link NetworkStats}, or {@code null} to clear list.
//     */
//    private void bindStats(Context context,NetworkStats stats, int[] restrictedUids) {
//        mApps.removeAll();
//        if (stats == null) {
//            if (LOGD) {
//                Log.d(TAG, "No network stats data. App list cleared.");
//            }
//            return;
//        }
//
//        final ArrayList<AppItem> items = new ArrayList<>();
//        long largest = 0;
//
//        final int currentUserId = ActivityManager.getCurrentUser();
//        final UserManager userManager = UserManager.get(context);
//        final List<UserHandle> profiles = userManager.getUserProfiles();
//        final SparseArray<AppItem> knownItems = new SparseArray<AppItem>();
//
//        final android.app.usage.NetworkStats.Bucket bucket = new android.app.usage.NetworkStats.Bucket();
//        while (stats.hasNextBucket() && stats.getNextBucket(bucket)) {
//            // Decide how to collapse items together
//            final int uid = bucket.getUid();
//            final int collapseKey;
//            final int category;
//            final int userId = UserHandle.getUserId(uid);
//            if (UserHandle.isApp(uid)) {
//                if (profiles.contains(new UserHandle(userId))) {
//                    if (userId != currentUserId) {
//                        // Add to a managed user item.
//                        final int managedKey = UidDetailProvider.buildKeyForUser(userId);
//                        largest = accumulate(managedKey, knownItems, bucket,
//                                AppItem.CATEGORY_USER, items, largest);
//                    }
//                    // Add to app item.
//                    collapseKey = uid;
//                    category = AppItem.CATEGORY_APP;
//                } else {
//                    // If it is a removed user add it to the removed users' key
//                    final UserInfo info = userManager.getUserInfo(userId);
//                    if (info == null) {
//                        collapseKey = UID_REMOVED;
//                        category = AppItem.CATEGORY_APP;
//                    } else {
//                        // Add to other user item.
//                        collapseKey = UidDetailProvider.buildKeyForUser(userId);
//                        category = AppItem.CATEGORY_USER;
//                    }
//                }
//            } else if (uid == UID_REMOVED || uid == UID_TETHERING
//                    || uid == Process.OTA_UPDATE_UID) {
//                collapseKey = uid;
//                category = AppItem.CATEGORY_APP;
//            } else {
//                collapseKey = android.os.Process.SYSTEM_UID;
//                category = AppItem.CATEGORY_APP;
//            }
//            largest = accumulate(collapseKey, knownItems, bucket, category, items, largest);
//        }
//        stats.close();
//
//        final int restrictedUidsMax = restrictedUids.length;
//        for (int i = 0; i < restrictedUidsMax; ++i) {
//            final int uid = restrictedUids[i];
//            // Only splice in restricted state for current user or managed users
//            if (!profiles.contains(new UserHandle(UserHandle.getUserId(uid)))) {
//                continue;
//            }
//
//            AppItem item = knownItems.get(uid);
//            if (item == null) {
//                item = new AppItem(uid);
//                item.total = -1;
//                items.add(item);
//                knownItems.put(item.key, item);
//            }
//            item.restricted = true;
//        }
//
//        Collections.sort(items);
//        for (int i = 0; i < items.size(); i++) {
//            final int percentTotal = largest != 0 ? (int) (items.get(i).total * 100 / largest) : 0;
//            final AppDataUsagePreference preference = new AppDataUsagePreference(getContext(),
//                    items.get(i), percentTotal, mUidDetailProvider);
//            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    AppDataUsagePreference pref = (AppDataUsagePreference) preference;
//                    AppItem item = pref.getItem();
//                    startAppDataUsage(item);
//                    return true;
//                }
//            });
//            mApps.addPreference(preference);
//        }
//    }



    private void fun(NetworkStats stats, int[] restrictedUids) {
        mlist.clear();
        ArrayList<AppItem> appItems = getAppsUsingMobileData(HostTrafficMonitorMainActivity.this,stats);
        for (AppItem appItem : appItems) {
            UidDetail detail = mUidDetailProvider.getUidDetail(appItem.key, true);
            String name = detail.label.toString();
            long size = appItem.total;
            String pkgName = mUidDetailProvider.getPackageName();
//            boolean isDisabledApp = isDisabledApp(pkgName, mContext);
            boolean isInvalidControlApp = isInvalidNetworkControlApp(pkgName, mContext);
            TraRecyBean bean = new TraRecyBean(pkgName, name);
//            bean.setIslimit(!isDisabledApp);
            bean.setKey(appItem.key);
            bean.setImageId(detail.icon);
            bean.setUsedTraSize(size);
            bean.setInvalidControlApp(isInvalidControlApp);
            bean.setQiantai(appItem.qiantai);
            bean.setHoutai(appItem.houtai);
            int k=appItem.uids.size();
            if(appItem.uids!=null){
                for (int i = 0; i < k; i++) {
                    bean.setUid(bean.getUid()+appItem.uids.keyAt(i));
                }
            }
            mlist.add(bean);
        }
        refresh(mlist);
    }


    @Override
    public void responseSIM(TraPagerBean object) {
        viewAvtion.onResponseSIM(object);
    }

    private TraPagerBean data;
    private List<TraRecyBean> mlist;

    @Override
    public void onInitData(List<TraRecyBean> list, int simindex) {
        this.mlist = list;
        long[] timeZone = new long[3];
        int[] timeArray = TimeFormat.getNowTimeArray();
//        timeZone[0] = TimeFormat.getStartTime(timeArray[0], timeArray[1]+1, 0, 0, 0, 0);
        Calendar cl = Calendar.getInstance();
        cl.set(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), 1, 0, 0, 0);
        timeZone[0] = cl.getTimeInMillis();
        timeZone[1] = System.currentTimeMillis();
        timeZone[2] = System.currentTimeMillis();
        getLoaderManager().restartLoader(Constant.LOADER_SUMMARY,
                SummaryForAllUidLoader.buildArgs(mTemplate[simindex], timeZone[0], timeZone[1]),
                mSummaryCallbacks);
    }

    @Override
    public void refresh(List<TraRecyBean> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewAvtion.onRefresh(mlist);
            }
        });
    }

    @Override
    public void onAppChangeState(TraRecyBean object, boolean ischecked) {
//        nothing
    }

    List<ChartView.Info>[] mlist2 = new List[2];
    int currentSim;

    @Override
    public void onRequestChartData(List<ChartView.Info> list, int currentSim) {
        this.currentSim = currentSim;
        if (mlist2[currentSim] == null) {
            List list2 = new ArrayList<>();
            mlist2[currentSim] = list2;
            new MAsyncTask().execute(list2);
        } else {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    responseChartData(mlist2[currentSim], currentSim);
                }
            }, 250);

        }
    }

    private class MAsyncTask extends AsyncTask<List<ChartView.Info>, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(List<ChartView.Info>... lists) {
            for (List<ChartView.Info> list : lists) {
                list.clear();
                List<Long> starts = queryTrafficSplitDay(getStarofDay());
                if (starts.size() >= 2) {
                    for (int i = 0; i < starts.size() - 1; i++) {
                        long xstart = starts.get(i);
                        long end = starts.get(i + 1);
                        long y = TrafficassistantUtil.getTrafficData(mContext, (data.getList().get(currentSim).getSlot()), xstart, end, 0);
                        list.add(new ChartView.Info(xstart, y));
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            responseChartData(mlist2[currentSim], currentSim);
        }
    }

    private int getStarofDay() {
        //// TODO: 2021/5/31
        return 1;
    }

    public static List<Long> queryTrafficSplitDay(int startofday) {
        List<Long> starts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
//        System.out.println(calendar.getTime().toString());
        int currentday = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(calendar.SECOND, 0);
        if (startofday <= currentday) {
            //本月
        } else {  //上月
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, startofday);


        for (; calendar.getTimeInMillis() < System.currentTimeMillis(); ) {
            starts.add(new Long(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        starts.add(new Long(System.currentTimeMillis()));
//        for (Long start : starts) {
//            calendar.setTimeInMillis(start);
////            System.out.println(calendar.getTime().toString());
//        }
        return starts;
    }

    @Override
    public void responseChartData(List<ChartView.Info> list, int currentSim) {
        viewAvtion.onResponseChartData(list, currentSim);
    }

    @Override
    public void statactivityDetail(int position) {
        startActivity(TrafficDetailActivity.showIntent(this,mlist.get(position)));
    }
}