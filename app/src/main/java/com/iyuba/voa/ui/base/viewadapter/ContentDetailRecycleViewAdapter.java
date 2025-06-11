package com.iyuba.voa.ui.base.viewadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.elvishew.xlog.XLog;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.databinding.ItemRvContentBinding;
import com.iyuba.voa.ui.main.home.detail.content.ContentViewModel;
import com.iyuba.voa.ui.widget.SelectWordTextView;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/10
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class ContentDetailRecycleViewAdapter<T> extends BindingRecyclerViewAdapter<T> {

    private final ContentViewModel viewModel;
    private SelectWordTextView selectWordTextView;  //上次点击视图
    private int clickPosition;    //记录上次点击位置

    public ContentDetailRecycleViewAdapter(ContentViewModel viewModel) {
        this.viewModel = viewModel;

    }

    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        ViewDataBinding binding = super.onCreateBinding(inflater, layoutId, viewGroup);
        XLog.d("created binding: " + binding);
        viewModel.UC.dismissWordDialog.observe((LifecycleOwner) viewModel.getLifecycleProvider(), new Observer<XmlWord>() {
            @Override
            public void onChanged(XmlWord xmlWord) {
                selectWordTextView.setmTempPosition(new int[]{-1, -1});  //重置单词点击位置
                selectWordTextView.setText(selectWordTextView.getText().toString());  //取消之后返回原先颜色
            }
        });
        return binding;
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutId, int position, T item) {
        super.onBindBinding(binding, bindingVariable, layoutId, position, item);
        ItemRvContentBinding binding1 = (ItemRvContentBinding) binding;
//        ObservableList<DetailItemViewModel> observableList = viewModel.observableList;
        binding1.tvDetailEng.setOnClickWordListener(word -> {
//            String sentence = observableList.get(position).entity.get().getSentence();
            if (selectWordTextView != null && clickPosition != position) {
                selectWordTextView.setText(selectWordTextView.getText().toString());  //之前的变回颜色
            }
            viewModel.searchWord(word);

          /*  Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, word);
            viewModel.startContainerActivity(SearchFragment.class.getCanonicalName(), bundle);*/
            clickPosition = position;
            selectWordTextView = binding1.tvDetailEng;
        });
        binding.executePendingBindings();//最后加这句，立即更新View，不延迟
        XLog.d("bound binding: " + binding + " at position: " + position);
    }

}
