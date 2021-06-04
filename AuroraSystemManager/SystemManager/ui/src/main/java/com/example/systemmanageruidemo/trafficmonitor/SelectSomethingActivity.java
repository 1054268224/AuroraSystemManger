package com.example.systemmanageruidemo.trafficmonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.systemmanageruidemo.R;

import java.util.ArrayList;
import java.util.List;

public class SelectSomethingActivity extends AppCompatActivity {

    private String selectaction;
    private String title;
    private String hint;
    private List<String> mlist;
    private int currentIndex;
    private int supperindex;
    private boolean immediatelynotifaction;
    private TextView mHint;
    private RecyclerView mRecycleview;
    private MAdapter mAdapter = new MAdapter();
    private View.OnClickListener mlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int re = ((int) v.getTag());
            if (re != currentIndex) {
                ischanged = true;
                currentIndex = re;
                mAdapter.notifyDataSetChanged();
            }

        }
    };
    private boolean ischanged;

    public static Intent getShowIntent(Context context, int index) {
        Intent intent = new Intent(context, SelectSomethingActivity.class);
        intent.putExtra("currentIndex", index);
        return intent;
    }

    public static int getIndexfromIntent(Intent data, int def) {
        return data.getIntExtra("currentIndex", def);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initdata();
        setContentView(R.layout.selectsomethingactivity);
        initView();
        setSimpleSupportABar(this);

    }

    public static void setSimpleSupportABar(AppCompatActivity appCompatActivity) {
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(appCompatActivity.getColor(R.color.white_wyh_traf)));
        appCompatActivity.getSupportActionBar().setElevation(0.0f);
        appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.svg_icon_back_left);
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(appCompatActivity.getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        appCompatActivity.getWindow().setStatusBarColor(appCompatActivity.getColor(R.color.transparent));
        appCompatActivity.getWindow().setBackgroundDrawable(new ColorDrawable(appCompatActivity.getColor(R.color.white_wyh_traf)));
    }

    private void initdata() {
        ischanged = false;
        Intent intent = getIntent();
        if (intent != null) {
            selectaction = intent.getStringExtra("selectaction");
            title = intent.getStringExtra("title");
            if (title == null) title = getString(R.string.select_traffic_notif_title);
            hint = intent.getStringExtra("hint");
            if (hint == null) hint = getString(R.string.select_traffic_notif_title);
            mlist = intent.getStringArrayListExtra("mlist");
            if (mlist == null) mlist = getDefaultList(this);
            supperindex = intent.getIntExtra("supperindex", -1);
            if (supperindex == -1) supperindex = mlist.size() - 1;
            currentIndex = intent.getIntExtra("currentIndex", supperindex);
            immediatelynotifaction = intent.getBooleanExtra("immediatelynotifaction", false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onresultfinish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onresultfinish() {
        Intent result = new Intent();
        result.putExtra("currentIndex", currentIndex);
        setResult(ischanged ? RESULT_OK : RESULT_CANCELED, result);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onresultfinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String indextoString(Context context, int index) {
        return getDefaultList(context).get(index);
    }

    public static List<String> getDefaultList(Context context) {
        List<String> re = new ArrayList<>();
        String str = "10MB 50MB 100MB 200MB 500MB 1GB 5GB 10GB 100GB";
        String[] astr = str.split("\\s+");
        for (String s : astr) {
            re.add(s.trim());
        }
        re.add(context.getResources().getString(R.string.notnotifaction));
        return re;
    }

    private void initView() {
        mHint = findViewById(R.id.hint);
        mRecycleview = findViewById(R.id.recycleview);
        mHint.setText(hint);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mRecycleview.setAdapter(mAdapter);
    }

    private class MAdapter extends RecyclerView.Adapter<VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SelectSomethingActivity.this).inflate(viewType, parent, false);
            if (viewType == R.layout.super_selectsomethingactivity_item_container)
                return new VH_Supper(view);
            else return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.mName.setText(mlist.get(position));
            holder.mCheckbox.setChecked(position == currentIndex);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mlistener);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == supperindex) {
                return R.layout.super_selectsomethingactivity_item_container;
            } else return R.layout.selectsomethingactivity_item_container;
        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }
    }

    private class VH extends RecyclerView.ViewHolder {
        LinearLayout mLay;
        TextView mName;
        CheckBox mCheckbox;

        public VH(@NonNull View itemView) {
            super(itemView);
            mLay = itemView.findViewById(R.id.lay);
            mName = itemView.findViewById(R.id.name);
            mCheckbox = itemView.findViewById(R.id.checkbox);
        }
    }

    private class VH_Supper extends VH {

        public VH_Supper(@NonNull View itemView) {
            super(itemView);
        }
    }
}
