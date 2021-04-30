package com.example.systemmanageruidemo.softmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.systemmanageruidemo.R;

import java.util.List;

public class SoftAdapter extends RecyclerView.Adapter<SoftAdapter.ViewHolder> {
    private List<SoftItem> mSoftItemList;

    public List<SoftItem> getmSoftItemList() {
        return mSoftItemList;
    }

    public void setmSoftItemList(List<SoftItem> mSoftItemList) {
        this.mSoftItemList = mSoftItemList;
    }

    private Context mContext;

    public SoftAdapter(Context context) {
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView softImage;
        TextView softName;
        TextView softMemory;
        CheckBox softCheckBox;

        public ViewHolder(View view) {
            super(view);
            softImage = (ImageView) view.findViewById(R.id.soft_image);
            softName = (TextView) view.findViewById(R.id.soft_name);
            softMemory = (TextView) view.findViewById(R.id.soft_memory);
            softCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
        }

    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.softmanager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoftItem softItem = mSoftItemList.get(position);
        holder.softImage.setImageDrawable(softItem.getImageId());
        holder.softName.setText(softItem.getName());
        holder.softMemory.setText(softItem.getMomory());
        holder.softCheckBox.setChecked(softItem.getTrue());
        holder.softCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                softItem.setTrue(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSoftItemList.size();
    }

}
