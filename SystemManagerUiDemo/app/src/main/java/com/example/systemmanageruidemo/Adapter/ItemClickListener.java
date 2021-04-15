package com.example.systemmanageruidemo.Adapter;

import com.example.systemmanageruidemo.modle.DataBean;

public interface ItemClickListener {

    void onExpandChildren(DataBean dataBean);

    void onHideChildren(DataBean dataBean);
}
