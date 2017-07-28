package com.example.armen.accesstoaddress.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.armen.accesstoaddress.R;
import com.example.armen.accesstoaddress.pojo.UrlModel;

import java.util.ArrayList;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<UrlModel> mUrlList;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UrlAdapter(ArrayList<UrlModel> contactArrayList, OnItemClickListener onItemClickListener) {
        mUrlList = contactArrayList;
        mOnItemClickListener = onItemClickListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item_url, viewGroup, false);
        return new ViewHolder(view, mUrlList, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView tvUrlAddress;
        LinearLayout llItemContainer;
        ImageView ivUrl;
        OnItemClickListener onItemClickListener;
        ArrayList<UrlModel> contactArrayList;

        ViewHolder(View itemView, ArrayList<UrlModel> urlModelArrayList, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.context = itemView.getContext();
            this.contactArrayList = urlModelArrayList;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
        }

        void findViews(View view) {
            llItemContainer = (LinearLayout) view.findViewById(R.id.ll_url_item_container);
            tvUrlAddress = (TextView) view.findViewById(R.id.tv_item_url_address);
            ivUrl = (ImageView) view.findViewById(R.id.iv_item_access_checker);
        }

        void bindData() {

            Glide.with(itemView.getContext())
                    .load(contactArrayList.get(getAdapterPosition()).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivUrl);

            tvUrlAddress.setText(contactArrayList.get(getAdapterPosition()).getUrlAddress());

            llItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(contactArrayList.get(getAdapterPosition()),
                            getAdapterPosition());
                }
            });

            llItemContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(contactArrayList.get(getAdapterPosition()),
                            getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(UrlModel url, int position);

        void onItemLongClick(UrlModel url, int position);

    }
}