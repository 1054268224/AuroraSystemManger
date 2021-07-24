package com.worain.xproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static int VIEW = R.layout.item_view;
    private Context context;
    private List<String> strings = new ArrayList<>();
    private RecyclerView rv;
    private SelectAdapter selectAdapter;
    private String[] viewStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.rv);
        viewStr = getResources().getStringArray(R.array.list_view_type);
        String str = null;
        for (int i = 0; i < 15; i++) {
            if (i < viewStr.length) {
                str = viewStr[i];
            } else {
                str = String.valueOf(i);
            }
            strings.add(str);
        }
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        selectAdapter = new SelectAdapter();
        rv.setAdapter(selectAdapter);
    }

    private class SelectAdapter extends RecyclerView.Adapter {
        Bundle bundle = new Bundle();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(viewType, parent, false);
            if (viewType == VIEW) {
                return new VH(view);
            } else {
                return new VH(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof VH) {
                ((VH) holder).bindData(strings.get(position));
                ((VH) holder).itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ViewActivity.class);
                    if (position == 0) {
                        intent.putExtra("viewtype", ViewActivity.VIEW_HEALTH_CHART_VIEW);
                    } else if (position == 1) {
                        intent.putExtra("viewtype", ViewActivity.VIEW_POWER_AnimView);
                    }
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        @Override
        public int getItemViewType(int position) {
            return VIEW;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView mTvName;

            public VH(@NonNull View itemView) {
                super(itemView);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            }

            public void bindData(String str) {
                mTvName.setText(str);
            }
        }
    }

}