package com.iyuba.voa.ui.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.iyuba.voa.R;
import com.iyuba.widget.popmenu.ActionItem;

import java.util.ArrayList;

/**
 * 20221104
 */
public class TitlePopupDialog extends Dialog {
    private Context mContext;


    private Rect mRect = new Rect();

    private final int[] mLocation = new int[2];



    private int popupGravity = Gravity.NO_GRAVITY;

    private OnItemOnClickListener mItemOnClickListener;

    private RecyclerView mListView;

    private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();
    private boolean mIsDirty;

    public TitlePopupDialog(Context context) {
        super(context, R.style.Theme_BottomDialog_Base);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_popup);
        initUI();
    }


    private void initUI() {
        mListView = findViewById(R.id.title_list);
        mIsDirty = false;

        mListView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_rv_pop, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(inflate);
                return myViewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
                ActionItem actionItem = mActionItems.get(position);
                holder.textView.setText(actionItem.mTitle);
                holder.imageView.setBackground(actionItem.mDrawable);
                holder.layout.setOnClickListener(view -> {
                    dismiss();
                    if (mItemOnClickListener != null)
                        mItemOnClickListener.onItemClick(mActionItems.get(position), position);
                });
            }

            @Override
            public int getItemCount() {
                return mActionItems.size();
            }
        });
        //设置布局
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
//        manager.setOrientation(RecyclerView.VERTICAL);
        mListView.setLayoutManager(manager);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }



    public void addAction(ActionItem action) {
        if (action != null) {
            mActionItems.add(action);
            mIsDirty = true;
        }
    }


    public void cleanAction() {
        if (mActionItems.isEmpty()) {
            mActionItems.clear();
            mIsDirty = true;
        }
    }


    public ActionItem getAction(int position) {
        if (position < 0 || position > mActionItems.size())
            return null;
        return mActionItems.get(position);
    }


    public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }


    public interface OnItemOnClickListener {
        void onItemClick(ActionItem item, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView = null;
        ImageView imageView = null;
        LinearLayout layout = null;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview);
            imageView = itemView.findViewById(R.id.image);
            layout = itemView.findViewById(R.id.backlayout);

//            textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
            textView.setTextSize(16);
            textView.setPadding(0, 20, 0, 20);
            textView.setSingleLine(true);

        }
    }
}
