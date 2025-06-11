package com.iyuba.voa.ui.widget.strategy;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.headlinelibrary.ui.common.ss.MixNative;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.sdk.data.iyu.IyuNative;
import com.iyuba.sdk.data.youdao.YDNative;
import com.iyuba.sdk.nativeads.NativeAdPositioning;
import com.iyuba.sdk.nativeads.NativeAdRenderer;
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
import com.iyuba.sdk.nativeads.NativeViewBinder;
import com.iyuba.voa.R;
import com.iyuba.voa.utils.Constants;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.EnumSet;


public final class ContentMixStrategy extends ContentNonVipStrategy {

    private int mHolderType;
    private int mStart;
    private int mInterval;
    private int[] mStreamTypes;

    public ContentMixStrategy(int start, int interval, int[] streamTypes, int holderType) {
        mStart = start;
        mInterval = interval;
        mStreamTypes = streamTypes;
        mHolderType = holderType;
    }

    @Override
    public RecyclerView.Adapter buildWorkAdapter(Context context, RecyclerView.Adapter originalAdapter) {
        NativeAdPositioning.ClientPositioning cp = new NativeAdPositioning.ClientPositioning();
        cp.addFixedPosition(mStart);
        cp.enableRepeatingPositions(mInterval);
        NativeRecyclerAdapter nativeAdapter = new NativeRecyclerAdapter(context, originalAdapter, cp);

        EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .location(null)
                .keywords(null)
                .desiredAssets(desiredAssets)
                .build();
        try {
            YDNative ydNative = new YDNative(context, "edbd2c39ce470cd72472c402cccfb586", requestParameters);

//        IyuNative iyuNative = new IyuNative(context, IHeadlineManager.appId);

            IyuNative iyuNative = new IyuNative(context, String.valueOf(Constants.CONFIG.appId));

            MixNative mixNative = new MixNative(ydNative, iyuNative);
            mixNative.setStreamSource(mStreamTypes);

            nativeAdapter.setAdSource(mixNative);
            nativeAdapter.setAdViewTypeMax(56);
        } catch (Exception var2) {
        }

        return nativeAdapter;
    }

    @Override
    public void init(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        NativeRecyclerAdapter nativeAdapter = (NativeRecyclerAdapter) adapter;
        NativeAdRenderer nativeAdRenderer = makeAdRenderer();
        nativeAdapter.registerAdRenderer(nativeAdRenderer);

        recyclerView.setAdapter(adapter);
    }

    private NativeAdRenderer makeAdRenderer() {
        switch (mHolderType) {
//            case HolderType.MIDDLE: {
//                NativeViewBinder viewBinder = new NativeViewBinder.Builder(com.iyuba.headlinelibrary.R.layout.headline_youdao_ad_row_middle)
//                        .titleId(com.iyuba.headlinelibrary.R.id.headline_native_title)
//                        .iconImageId(com.iyuba.headlinelibrary.R.id.headline_native_icon_image)
//                        .mainImageId(com.iyuba.headlinelibrary.R.id.headline_native_ad_image)
//                        .build();
//                return new NativeAdRenderer(viewBinder);
//            }
            case HolderType.SMALL:
            default: {
                NativeViewBinder viewBinder = new NativeViewBinder.Builder(R.layout.item_rv_home_ad)
                        .titleId(R.id.tv_main_title)
                        .mainImageId(R.id.iv_main_native)
                        .build();
                return new NativeAdRenderer(viewBinder);
            }
        }
    }

    @Override
    public int getOriginalAdapterPosition(RecyclerView.Adapter adapter, int position) {
        return ((NativeRecyclerAdapter) adapter).getOriginalPosition(position);
    }

    @Override
    public void loadAd(RecyclerView.Adapter adapter) {
        ((NativeRecyclerAdapter) adapter).loadAds();
    }

    @Override
    public void refreshAd(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        ((NativeRecyclerAdapter) adapter).refreshAds();
    }

}
