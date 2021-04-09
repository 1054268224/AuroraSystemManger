package com.cydroid.softmanager.rubbishcleaner.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore.Files;
import android.text.format.Formatter;

import com.cleanmaster.sdk.CMCleanConst;
import com.cleanmaster.sdk.IAdDirCallback;
import com.cleanmaster.sdk.ICacheCallback;
import com.cleanmaster.sdk.IKSCleaner;
import com.cleanmaster.sdk.IResidualCallback;
import com.cydroid.softmanager.R;
import com.cydroid.softmanager.rubbishcleaner.activities.RubbishCleanerMainActivity;
import com.cydroid.softmanager.rubbishcleaner.common.CleanTypeConst;
import com.cydroid.softmanager.rubbishcleaner.common.MsgConst;
import com.cydroid.softmanager.utils.Log;
import com.cydroid.softmanager.utils.PreferenceHelper;
import com.keniu.security.CleanMasterSDK;

import java.util.Locale;

public class RubbishScanService extends Service {
    private static final String TAG = "CyeeRubbishCleaner/RubbishCleanerService";

    private IKSCleaner mKSCleaner;
    private ServiceConnection mServiceConn;
    private String mLanguage;
    private String mCountry;
    private long mTotalSize;
    private final boolean[] mFlagArray = {false, false, false, false};
    private BackgroundQueryHandler mQueryHandler;
    private final boolean DEBUG = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(DEBUG, TAG, "RubbishScanService onCreate()");
        super.onCreate();
        CleanMasterSDK.getInstance().Initialize(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mQueryHandler = new BackgroundQueryHandler(getContentResolver());
        Locale locale = getResources().getConfiguration().locale;
        mLanguage = locale.getLanguage();
        mCountry = locale.getCountry();
        bindKSService(intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG, TAG, "RubbishScanService is destroy...");
        super.onDestroy();
        unbindService(mServiceConn);
        mTotalSize = 0;
        mQueryHandler = null;
    }

    private void bindKSService(final Intent intent) {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(DEBUG, TAG,
                        "RubbishScanService, service connected, can scan rubbish");
                mKSCleaner = IKSCleaner.Stub.asInterface(service);
                scanPhoneRubbish();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(DEBUG, TAG,
                        "RubbishScanService, service disconnected, maybe have some problems");
            }

        };
        // Intent remoteIntent = new Intent("com.cleanmaster.CleanService");
        Intent remoteIntent = new Intent(this, com.cleanmaster.CleanService.class);
        bindService(remoteIntent, mServiceConn, BIND_AUTO_CREATE);
    }

    private void scanPhoneRubbish() {
        scanCaches();
        scanAds();
        scanResiduals();
        scanApks();
    }

    private long calculateItemSize(String path) {
        long size = 0;
        if (mKSCleaner == null) {
            return size;
        }
        try {
            size = mKSCleaner.pathCalcSize(path);
        } catch (RemoteException e) {
            e.printStackTrace();
            return size;
        }

        return size;
    }

    private void scanCaches() {
        final ICacheCallback.Stub cacheCallbackStub = new ICacheCallback.Stub() {

            @Override
            public void onStartScan(int nTotalScanItem) throws RemoteException {
            }

            @Override
            public boolean onScanItem(String desc, int nProgressIndex)
                    throws RemoteException {
                return false;
            }

            @Override
            public void onFindCacheItem(String cacheType, String dirPath,
                                        String pkgName, boolean bAdviseDel, String alertInfo,
                                        String descx) throws RemoteException {
                sendScanMsg(MsgConst.FIND_ITEM, -1, calculateItemSize(dirPath));
            }

            @Override
            public void onCacheScanFinish() throws RemoteException {
                sendScanMsg(MsgConst.END, CleanTypeConst.CACHE, 0);

            }
        };
        new Thread() {
            public void run() {
                if (mKSCleaner == null) {
                    return;
                }
                Log.d(DEBUG, TAG,
                        "RubbishScanService, begin scanCache() in new thread");
                try {
                    mKSCleaner.init(mLanguage, mCountry);
                    mKSCleaner.scanCache(CMCleanConst.MASK_SCAN_COMMON,
                            cacheCallbackStub);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }

    private void scanAds() {
        final IAdDirCallback.Stub adCallbackStub = new IAdDirCallback.Stub() {

            @Override
            public void onStartScan(int nTotalScanItem) throws RemoteException {
            }

            @Override
            public boolean onScanItem(String desc, int nProgressIndex)
                    throws RemoteException {
                return false;
            }

            @Override
            public void onFindAdDir(String name, String dirPath)
                    throws RemoteException {
                sendScanMsg(MsgConst.FIND_ITEM, -1, calculateItemSize(dirPath));
            }

            @Override
            public void onAdDirScanFinish() throws RemoteException {
                sendScanMsg(MsgConst.END, CleanTypeConst.AD, 0);
            }
        };
        new Thread() {
            public void run() {
                if (mKSCleaner == null) {
                    return;
                }
                Log.d(DEBUG, TAG,
                        "RubbishScanService, begin scanAdDir() in new thread");
                try {
                    mKSCleaner.init(mLanguage, mCountry);
                    mKSCleaner.scanAdDir(adCallbackStub);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }

    private void scanResiduals() {
        final IResidualCallback.Stub residualCallStub = new IResidualCallback.Stub() {

            @Override
            public void onStartScan(int nTotalScanItem) throws RemoteException {
            }

            @Override
            public boolean onScanItem(String desc, int nProgressIndex)
                    throws RemoteException {
                return false;
            }

            @Override
            public void onFindResidualItem(String dirPath, String descName,
                                           boolean bAdviseDel, String alertInfo)
                    throws RemoteException {
                sendScanMsg(MsgConst.FIND_ITEM, -1, calculateItemSize(dirPath));
            }

            @Override
            public void onResidualScanFinish() throws RemoteException {
                sendScanMsg(MsgConst.END, CleanTypeConst.RESIDUAL, 0);
            }
        };
        new Thread() {
            public void run() {
                if (mKSCleaner == null) {
                    return;
                }
                Log.d(DEBUG, TAG,
                        "RubbishScanService, begin scanResidual() in new thread");
                try {
                    mKSCleaner.init(mLanguage, mCountry);
                    mKSCleaner.scanResidual(CMCleanConst.MASK_SCAN_COMMON,
                            residualCallStub);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }

    private void scanApks() {
        String volumeName = "external";
        final Uri uri = Files.getContentUri(volumeName);
        final String[] columns = new String[]{"_data", "_size"};
        final String selection = "_data like '%.apk'";
        mQueryHandler.post(new Runnable() {
            @Override
            public void run() {
                mQueryHandler.startQuery(0, null, uri, columns, selection,
                        null, null);
            }

        });
    }

    private class BackgroundQueryHandler extends AsyncQueryHandler {

        public BackgroundQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            try {
                if (null == cursor || !cursor.moveToFirst()) {
                    sendScanMsg(MsgConst.END, CleanTypeConst.APK, 0);
                    return;
                }
                Log.d(TAG, "RubbishScanService, onQueryComplete, get apks size");
                do {
                    sendScanMsg(MsgConst.FIND_ITEM, -1, cursor.getLong(1)); // _size
                } while (cursor.moveToNext());
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            sendScanMsg(MsgConst.END, CleanTypeConst.APK, 0);
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MsgConst.FIND_ITEM:
                    mTotalSize += Long.valueOf((String) msg.obj);
                    break;
                case MsgConst.END:
                    setScanFinishFlag(msg.arg1, true);
                    if (isAllScanFinished()) {
                        judgeWhetherPopNotification();
                        resetScanFlag();
                    }
                    break;
            }
        }

    };

    private boolean isAllScanFinished() {
        for (int i = 0; i < mFlagArray.length; i++) {
            if (!mFlagArray[i]) {
                return false;
            }
        }
        return true;
    }

    private void setScanFinishFlag(int category, boolean isFinished) {
        if (category < 0 && category > CleanTypeConst.APK) {
            return;
        }
        mFlagArray[category] = isFinished;
    }

    private void resetScanFlag() {
        for (int i = 0; i < mFlagArray.length; i++) {
            setScanFinishFlag(i, false);
        }
    }

    private void judgeWhetherPopNotification() {
        String defaultSize = PreferenceHelper.getString(this,
                "rubbish_size_alert_key", "100");
        // Chenyee xionghg 20171216 modify for storage conversion begin
        // long threshold = Long.valueOf(defaultSize) * 1048576; // 1024 * 1024
        long threshold = Long.valueOf(defaultSize) * 1000000; // 1000 * 1000
        // Chenyee xionghg 20171216 modify for storage conversion end
        Log.d(DEBUG, TAG, "RubbishScanService, detected " + mTotalSize
                + " rubbish, threshold is " + threshold);
        if (mTotalSize >= threshold) {
            showNotification(Formatter.formatShortFileSize(this, threshold));
        }
    }

    private void showNotification(String thresholdStr) {
        Intent intent = new Intent(this,
                RubbishCleanerMainActivity.class);
        // Gionee <changph> <2016-09-07> modify for CR01758203 begin
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        // Gionee <changph> <2015-09-07> modify for CR01758203 end
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager notifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.notifi_title) + thresholdStr)
                .setContentText(getResources().getString(R.string.notifi_content))
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(0x4fc6bb)
                .setShowWhen(false)
                .setAutoCancel(true);
        notifiManager.notify(0, builder.build());
    }

    private void sendScanMsg(int what, int group, long size) {
        String sizeObj = Long.toString(size);
        Message msg = mHandler.obtainMessage(what, group, 0, sizeObj);
        mHandler.sendMessage(msg);
    }

}
