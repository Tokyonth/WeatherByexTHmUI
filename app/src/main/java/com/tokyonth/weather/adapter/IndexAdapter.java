package com.tokyonth.weather.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.entity.Index;

import java.util.List;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {

    private List<Index> indexList;
    private Context context;

    public IndexAdapter(Context context, List<Index> indexList) {
        this.context = context;
        this.indexList = indexList;
    }

    @NonNull
    @Override
    public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index_weather, parent, false);
        return new IndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndexViewHolder holder, int position) {
        Index index = indexList.get(position);
        if (index != null) {
            if (index.getName().equals(getString(R.string.index_air_conditioning))) {
                holder.indexImageIv.setImageResource(R.drawable.index_air_conditioning);
                holder.titleTv.setText(getString(R.string.index_air_conditioning));
            } else if (index.getName().equals(getString(R.string.index_sport))) {
                holder.indexImageIv.setImageResource(R.drawable.index_sport);
                holder.titleTv.setText(getString(R.string.index_sport));
            } else if (index.getName().equals(getString(R.string.index_uv))) {
                holder.indexImageIv.setImageResource(R.drawable.index_uv);
                holder.titleTv.setText(getString(R.string.index_uv));
            } else if (index.getName().equals(getString(R.string.index_virus))) {
                holder.indexImageIv.setImageResource(R.drawable.index_virus);
                holder.titleTv.setText(getString(R.string.index_virus));
            } else if (index.getName().equals(getString(R.string.index_washing))) {
                holder.indexImageIv.setImageResource(R.drawable.index_washing);
                holder.titleTv.setText(getString(R.string.index_washing));
            } else if (index.getName().equals(getString(R.string.index_air_pollution))) {
                holder.indexImageIv.setImageResource(R.drawable.index_air_pollution);
                holder.titleTv.setText(getString(R.string.index_air_pollution));
            } else if (index.getName().equals(getString(R.string.index_clothes))) {
                holder.indexImageIv.setImageResource(R.drawable.index_clothes);
                holder.titleTv.setText(getString(R.string.index_clothes));
            }
            if (index.getValue() != null) {
                holder.valueTv.setText(index.getValue());
            } else {
                holder.valueTv.setText(getString(R.string.text_null));
            }
            if (index.getDetail() != null) {
                holder.detailTv.setText(index.getDetail());
            } else {
                holder.detailTv.setText(getString(R.string.text_not_info));
            }
        }

    }

    private String getString(int resID) {
        return context.getString(resID);
    }

    @Override
    public int getItemCount() {
        return indexList.size();
    }

    static class IndexViewHolder extends ViewHolder {

        private TextView valueTv, detailTv, titleTv;
        private ImageView indexImageIv;

        IndexViewHolder(View itemView) {
            super(itemView);
            indexImageIv = itemView.findViewById(R.id.item_index_image_iv);
            valueTv = itemView.findViewById(R.id.item_index_value_tv);
            detailTv = itemView.findViewById(R.id.item_index_detail_tv);
            titleTv = itemView.findViewById(R.id.item_index_title_tv);
        }
    }

}
