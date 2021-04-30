package com.wheatek.proxy.ui;

import android.os.Bundle;

import com.example.systemmanageruidemo.OptimiseActivity;
import com.example.systemmanageruidemo.actionpresent.OptimisePresent;
import com.example.systemmanageruidemo.actionview.OptimiseView;

public class HostOptimiseActivity extends HostProxyActivity<OptimiseView> implements OptimisePresent {
    {
        attach(new OptimiseActivity());
    }

    OptimiseView viewAvtion;
    @Override
    public void setViewAction(OptimiseView viewAvtion) {
        this.viewAvtion=viewAvtion;
    }

    @Override
    public OptimiseView getViewAction() {
        return viewAvtion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
