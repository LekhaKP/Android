package com.qburst.lekha.databindingexample2;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by carl on 12/1/15.
 */

public class MyAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private List<MyModel> mMyModels;

    public MyAdapter(List<MyModel> myModels) {
        mMyModels = myModels;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.my_text_layout, viewGroup, false);
        
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        ViewDataBinding viewDataBinding = customViewHolder.getViewDataBinding();

        viewDataBinding.setVariable(com.qburst.lekha.databindingexample2.BR.mymodel, mMyModels.get(i));
    }

    @Override
    public int getItemCount() {
        return (null != mMyModels ? mMyModels.size() : 0);
    }

    public void removeItem(int position) {
        mMyModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMyModels.size());
    }
}