package com.example.systemmanageruidemo.trafficmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
            currentIndex = ((int) v.getTag());
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initdata();
        setContentView(R.layout.selectsomethingactivity);
        initView();
    }

    private void initdata() {
        Intent intent = getIntent();
        if (intent != null) {
            selectaction = intent.getStringExtra("selectaction");
            title = intent.getStringExtra("title");
            if (title == null) title = getString(R.string.select_traffic_notif_title);
            hint = intent.getStringExtra("hint");
            if (hint == null) hint = getString(R.string.select_traffic_notif_hint);
            mlist = intent.getStringArrayListExtra("mlist");
            if (mlist == null) mlist = getDefaultList();
            supperindex = intent.getIntExtra("supperindex", -1);
            if (supperindex == -1) supperindex = mlist.size() - 1;
            currentIndex = intent.getIntExtra("currentIndex", supperindex);
            immediatelynotifaction = intent.getBooleanExtra("immediatelynotifaction", false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent();
            result.putExtra("currentIndex", currentIndex);
            setResult(RESULT_OK, result);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent result = new Intent();
            result.putExtra("currentIndex", currentIndex);
            setResult(RESULT_OK, result);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<String> getDefaultList() {
        List<String> re = new ArrayList<>();
        String str = "10MB 50MB 100MB 200MB 500MB 1GB 5GB 10GB 100GB";
        String[] astr = str.split("\\s+");
        for (String s : astr) {
            re.add(s);
        }
        re.add(getResources().getString(R.string.notnotifaction));
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
            mLay = findViewById(R.id.lay);
            mName = findViewById(R.id.name);
            mCheckbox = findViewById(R.id.checkbox);
        }
    }

    private class VH_Supper extends VH {

        public VH_Supper(@NonNull View itemView) {
            super(itemView);
        }
    }
}
