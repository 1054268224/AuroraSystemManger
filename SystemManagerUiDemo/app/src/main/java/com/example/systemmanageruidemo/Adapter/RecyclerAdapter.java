package com.example.systemmanageruidemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.modle.DataBean;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<DataBean> mDataBeanList;
    private LayoutInflater mInflater;
    private OnScrollListener mOnScrollListener;

    public RecyclerAdapter(Context context, List<DataBean> dataBeanList){

        mContext = context;
        mDataBeanList = dataBeanList;
        mInflater  = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case DataBean.PARENT_ITEM:
                view = mInflater.inflate(R.layout.recycleview_item_parent,parent,false);
                return new ParentViewHolder(mContext,view);
            case DataBean.CHILD_ITEM:
                view = mInflater.inflate(R.layout.recycleview_item_child,parent,false);
                return new ChildViewHolder(mContext,view);
            default:
                view = mInflater.inflate(R.layout.recycleview_item_parent,parent,false);
                return new ParentViewHolder(mContext,view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case DataBean.PARENT_ITEM:
                ParentViewHolder parentViewHolder = (ParentViewHolder) holder;
                parentViewHolder.bindView(mDataBeanList.get(position), position,itemClickListener);
                break;
            case DataBean.CHILD_ITEM:
                ChildViewHolder childViewHolder = (ChildViewHolder) holder;
                childViewHolder.bindView(mDataBeanList.get(position), position);
        }

    }

    @Override
    public int getItemCount() { return mDataBeanList.size(); }

    @Override
    public int getItemViewType(int position) { return mDataBeanList.get(position).getType();  }

    private  ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onExpandChildren(DataBean bean) {
            int position = getCurrentPoint(bean.getID());
            DataBean children = getChildDataBean(bean);
            if(children == null){
                return;
            }
            add(children,position +1);
            if(position == mDataBeanList.size() - 2 && mOnScrollListener != null) {
                mOnScrollListener.scrollTo(position + 1);
            }

        }

        @Override
        public void onHideChildren(DataBean bean) {

            int position = getCurrentPoint(bean.getID());
            DataBean children = bean.getChildBean();
            if (children == null){
                return;
            }
            remove(position + 1);//删除
            if (mOnScrollListener != null) {
                mOnScrollListener.scrollTo(position);
            }

        }
    };

    public void add(DataBean bean, int position){
        mDataBeanList.add(position, bean);
        notifyItemInserted(position);
    }

    protected void remove(int position){
        mDataBeanList.remove(position);
        notifyItemRemoved(position);
    }

    protected int getCurrentPoint(String uuid){
        for (int i = 0;i < mDataBeanList.size(); i++){
            if(uuid.equalsIgnoreCase(mDataBeanList.get(i).getID())){
                return i;
            }
        }
        return -1;
    }

    private  DataBean getChildDataBean(DataBean bean){
        DataBean child = new DataBean();
        child.setType(1);
        child.setParentLeftTxt(bean.getParentLeftTxt());
        child.setParentRightTxt(bean.getParentRightTxt());
        child.setChildLeftTxt(bean.getChildLeftTxt());
        child.setChildRightTxt(bean.getChildRightTxt());
        return child;
    }

    public interface OnScrollListener{
        void scrollTo(int pos);
    }

    public void  setOnScrollListener(OnScrollListener onScrollListener){
        mOnScrollListener = onScrollListener;
    }
}
