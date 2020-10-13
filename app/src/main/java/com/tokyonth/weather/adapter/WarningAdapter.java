package com.tokyonth.weather.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tokyonth.weather.base.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.HttpUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WarningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BODY = 0;
    private static final int TYPE_FOOTER = 1;

    private Activity activity;
    private List<String> warningList;

    public WarningAdapter(Activity activity) {
        this.activity = activity;
        warningList = new ArrayList<>();
        warningList.add("高温预警");
        warningList.add("暴雨预警");
        warningList.add("沙尘暴预警");
        warningList.add("强对流预警");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BODY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning, parent, false);
            return new ContentViewHolder(view);
        } else {
            View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning_footer, parent, false);
            return new FooterViewHolder(footerView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContentViewHolder) {
            String warningTitle = warningList.get(position);
            ((ContentViewHolder) holder).titleTv.setText(warningTitle);
            ((ContentViewHolder) holder).itemView.setOnClickListener(v -> {
                if (((ContentViewHolder) holder).contentLl.getVisibility() == View.GONE) {
                    ((ContentViewHolder) holder).iv_arrow.setVisibility(View.GONE);
                    ((ContentViewHolder) holder).progressBar_load.setVisibility(View.VISIBLE);
                    if (position == 0) {
                        String tempUrl = "http://www.nmc.cn/publish/country/warning/megatemperature.html";
                        getWarningContent(tempUrl, ((ContentViewHolder) holder));
                    } else if (position == 1) {
                        String rainUrl = "http://www.nmc.cn/publish/country/warning/downpour.html";
                        getWarningContent(rainUrl, ((ContentViewHolder) holder));
                    } else if (position == 2) {
                        String sandUrl = "http://www.nmc.cn/publish/country/warning/dust.html";
                        getWarningContent(sandUrl, ((ContentViewHolder) holder));
                    } else if (position == 3) {
                        String convectionUrl = "http://www.nmc.cn/publish/country/warning/strong_convection.html";
                        getWarningContent(convectionUrl, ((ContentViewHolder) holder));
                    }
                } else if (((ContentViewHolder) holder).contentLl.getVisibility() == View.VISIBLE) {
                    ((ContentViewHolder) holder).animateClose(((ContentViewHolder) holder).contentLl);
                    ((ContentViewHolder) holder).iv_arrow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_arrow_right));
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).footerTv.setText("数据来源于中央气象台");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == warningList.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_BODY;
        }
    }

    @Override
    public int getItemCount() {
        return warningList.size() + 1;
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTv, contentTv;
        private LinearLayout contentLl, titleLl;
        private ProgressBar progressBar_load;
        private ImageView iv_arrow;

        private int hiddenLayoutHeight;

        ContentViewHolder(View itemView) {
            super(itemView);
            progressBar_load = itemView.findViewById(R.id.progress_load);
            iv_arrow = itemView.findViewById(R.id.iv_arrow);
            titleLl = itemView.findViewById(R.id.item_warning_title_ll);
            contentLl = itemView.findViewById(R.id.item_warning_content_ll);
            titleTv = itemView.findViewById(R.id.item_warning_title);
            contentTv = itemView.findViewById(R.id.item_warning_content_tv);
            hiddenLayoutHeight = contentLl.getLayoutParams().height;
        }

        private void animateOpen(View view) {
            view.setVisibility(View.VISIBLE);
            ValueAnimator animator = createDropAnimator(view, 0, hiddenLayoutHeight);
            animator.start();
        }

        private void animateClose(final View view) {
            int origHeight = view.getHeight();
            ValueAnimator animator = createDropAnimator(view, origHeight, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }

        private ValueAnimator createDropAnimator(final View view, int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(animation -> {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            });
            return animator;
        }

    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        private TextView footerTv;

        FooterViewHolder(View itemView) {
            super(itemView);
            footerTv = itemView.findViewById(R.id.item_warning_footer_tv);
        }

    }

    @SuppressLint("SetTextI18n")
    private void getWarningContent(String url, final ContentViewHolder holder) {
        HttpUtil.doGetAsynchronous(url, result -> {
            if (result == null) {
                return;
            }
            Document document = Jsoup.parse(result);
            Element textElement = document.getElementById("text");
            Element writingElement = textElement.getElementsByClass("writing").first();
            Elements ps = writingElement.getElementsByTag("p");
            final String title = ps.get(0).text();
            final String content = ps.get(1).text();
            activity.runOnUiThread(() -> {
                int index = title.lastIndexOf("色");
                String strColor = title.substring(index - 1, index);
                int textColor = Color.BLACK;
                switch (strColor) {
                    case "蓝":
                        textColor = Color.parseColor("#2962FF");
                        break;
                    case "黄":
                        textColor = Color.parseColor("#FFD600");
                        break;
                    case "橙":
                        textColor = Color.parseColor("#FF6D00");
                        break;
                    case "红":
                        textColor = Color.parseColor("#D50000");
                        break;
                }
                holder.contentTv.setText("\t\t\t" + title + content);
                holder.contentTv.setTextColor(textColor);
                holder.progressBar_load.setVisibility(View.GONE);
                holder.iv_arrow.setVisibility(View.VISIBLE);
                holder.iv_arrow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_arrow_open));
                holder.animateOpen((holder).contentLl);
            });
        });
    }

}
