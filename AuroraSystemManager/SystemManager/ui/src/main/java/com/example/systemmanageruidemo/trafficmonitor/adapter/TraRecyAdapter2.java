package com.example.systemmanageruidemo.trafficmonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.UnitUtil;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraRecyBean;
import com.example.systemmanageruidemo.view.CustomProgressView;

import java.util.List;

public class TraRecyAdapter2 extends RecyclerView.Adapter<TraRecyAdapter2.VH> {
    private Context context;
    private List<TraRecyBean> datas;
    private View.OnClickListener listener;
    long maxl = 0;
    boolean iszk = false;
    boolean isneedzk = true;

    public TraRecyAdapter2(Context context, View.OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public List<TraRecyBean> getDatas() {
        return datas;
    }

    public void setDatas(List<TraRecyBean> datas) {
        this.datas = datas;
        iszk = false;
        maxl = 0;
    }


    @Override
    public int getItemViewType(int position) {
        if (!iszk && isneedzk && position == (getItemCount() - 1)) {
            return R.layout.more_appusetraffic_container;
        }
        return R.layout.appuser_item_wyh;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        if (viewType == R.layout.more_appusetraffic_container) {
            return new MVH(view);
        } else {
            return new VH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (holder instanceof MVH) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iszk = true;
                    notifyDataSetChanged();
                }
            });
        } else {
            if (datas.size() > 0 && maxl == 0) {
                maxl = datas.get(0).getUsedTraSize();
            }
            TraRecyBean data = datas.get(position);
            holder.mIcon.setImageDrawable(data.getImageId());
            holder.mAppName.setText(data.getName());
            holder.mUseTrafficResult.setText(UnitUtil.convertStorage(data.getUsedTraSize()));
            if (maxl != 0) {
                holder.mProgress.setProgressCurrent(((int) (data.getUsedTraSize() * 100 / maxl)));
            }
            holder.mlistenerRight.setVisibility(listener == null ? View.GONE : View.VISIBLE);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        if (iszk) {
            return datas == null ? 0 : datas.size();
        } else {
            if (datas != null) isneedzk = datas.size() > 6;
            return datas == null ? 0 : Math.min(datas.size(), 6);
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        //        private LinearLayout mAppuseTrafficLay;
        private ImageView mIcon;
        //        private LinearLayout mSavetrafficLay;
        private TextView mAppName;
        private CustomProgressView mProgress;
        private TextView mUseTrafficResult;
        private ImageView mlistenerRight;

        public VH(@NonNull View itemView) {
            super(itemView);
//            mAppuseTrafficLay = itemView.findViewById(R.id.appuse_traffic_lay);
            mIcon = itemView.findViewById(R.id.icon);
//            mSavetrafficLay = itemView.findViewById(R.id.savetraffic_lay);
            mAppName = itemView.findViewById(R.id.app_name);
            mProgress = itemView.findViewById(R.id.progress);
            mUseTrafficResult = itemView.findViewById(R.id.use_traffic_result);
            mlistenerRight = itemView.findViewById(R.id.listener_right);
        }
    }

    static class MVH extends VH {

        public MVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
