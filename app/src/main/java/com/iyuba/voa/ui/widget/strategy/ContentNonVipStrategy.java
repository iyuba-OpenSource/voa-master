package com.iyuba.voa.ui.widget.strategy;

import androidx.recyclerview.widget.RecyclerView;

public abstract class ContentNonVipStrategy extends ContentStrategy {

    public abstract void loadAd(RecyclerView.Adapter adapter);

    public abstract void refreshAd(RecyclerView recyclerView, RecyclerView.Adapter adapter);

}
