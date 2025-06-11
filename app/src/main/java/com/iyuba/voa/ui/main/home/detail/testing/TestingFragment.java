package com.iyuba.voa.ui.main.home.detail.testing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.databinding.DialogCorrectBinding;
import com.iyuba.voa.databinding.DialogWordBinding;
import com.iyuba.voa.databinding.FragmentDetailTestingBinding;
import com.iyuba.voa.databinding.ViewTestingPlayerBinding;
import com.iyuba.voa.ui.base.BaseTitleFragment;
import com.iyuba.voa.ui.widget.SelectWordTextView;
import com.iyuba.voa.utils.AudioRecorderUtils;
import com.iyuba.voa.utils.Constants;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;

import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@RuntimePermissions
public class TestingFragment extends BaseTitleFragment<FragmentDetailTestingBinding, TestingViewModel> {

    private String voaId;
    private ExoPlayer playerWord;
    private MenuItem menuItem;
    private AudioRecorderUtils recorderManager;
    private String recFilePath;
    private Drawable recDrawable;
    private AudioRecorderUtils.OnAudioStatusUpdateListener listener;
    private ViewTestingPlayerBinding playerBinding;
    private VoaText voaText;
    private TitleTed titleTed;

    public TestingFragment() {
    }

    @Override
    public void initData() {
        super.initData();

        Bundle extras = getArguments();
        titleTed = extras.getParcelable(Constants.BUNDLE.KEY);
        voaId = titleTed.getVoaId();
        String sound = titleTed.getSound();

        viewModel.mPlayer = new ExoPlayer.Builder(getContext()).build();
        binding.player.setPlayer(viewModel.mPlayer);

        playerBinding = DataBindingUtil.bind(binding.player.getChildAt(0));
        playerBinding.setVariable(BR.viewModel, viewModel);
        playerBinding.setLifecycleOwner(this);

//        为什么发布的点击事件写到这呢？？因为xml里边此控件用databinding会出现ArrayIndexOutOfBoundsException，没找到好的解决办法
        playerBinding.tvRelease.setOnClickListener(view -> viewModel.releaseTesting());
        playerBinding.ivLoop.setImageResource(viewModel.isCycle.get() ? R.drawable.vector_drawable_single_cycle_true : R.drawable.vector_drawable_single_cycle);
        playerBinding.ivLoop1.setImageResource(viewModel.isCycle.get() ? R.drawable.vector_drawable_single_cycle_true : R.drawable.vector_drawable_single_cycle);

        playerBinding.ivLoop.setOnClickListener(view -> {
            setCycle(playerBinding.ivLoop);
        });
        playerBinding.ivLoop1.setOnClickListener(view -> {
            setCycle(playerBinding.ivLoop1);
        });

        if (!TextUtils.isEmpty(voaId)) {
            viewModel.voaid = voaId;
            viewModel.loadData(sound);
        }
        initRecManager();
    }

    private void setCycle(ImageView view) {
        viewModel.isCycle.set(!viewModel.isCycle.get());
        boolean b = viewModel.isCycle.get();
        ToastUtils.showShort("已%s单句循环", b ? "开启" : "关闭");
        view.setImageResource(b ? R.drawable.vector_drawable_single_cycle_true : R.drawable.vector_drawable_single_cycle);
        viewModel.mPlayer.setRepeatMode(b ? Player.REPEAT_MODE_ONE : Player.REPEAT_MODE_OFF);
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_detail_testing;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public TestingViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(TestingViewModel.class);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (viewModel == null) {
            return;
        }
        ExoPlayer mPlayer = viewModel.mPlayer;
        if (isVisibleToUser) {
            if (null != mPlayer) {
//                mPlayer.play();
            }
        } else {
            if (null != mPlayer) {
//                mPlayer.seekTo(0);
                mPlayer.stop();
            }
        }
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.UC.audioRecord.observe(this, new Observer<ImageView>() {
            @Override
            public void onChanged(ImageView s) {
                if (null == s) {
                    ToastUtils.showShort("停止录制");
                    stopRec();
                } else {
                    recDrawable = s.getDrawable();
                    TestingFragmentPermissionsDispatcher.startRecWithPermissionCheck(TestingFragment.this, (VoaText) s.getTag());
                }
            }
        });

        viewModel.UC.closeTimeBar.observe(this, b -> {
            if (b) {
                playerBinding.llMergeControl.setVisibility(View.GONE);
                playerBinding.llOtherControl.setVisibility(View.VISIBLE);
            } else {
                playerBinding.llMergeControl.setVisibility(View.VISIBLE);
                playerBinding.llOtherControl.setVisibility(View.GONE);
            }
        });

        viewModel.UC.correctDialog.observe(this, this::showCorrectDialog);
        viewModel.UC.breakCourse.observe(this, new Observer<TestingItemViewModel>() {
            @Override
            public void onChanged(TestingItemViewModel itemViewModel) {
                startActivity(MobClassActivity.buildIntent(Utils.getContext(), 3, true, null));
            }
        });
        viewModel.UC.showWordDialog.observe(this, xmlWord -> showWordDialog(xmlWord));
    }


    private void showWordDialog(XmlWord xmlWord) {
        playerWord = new ExoPlayer.Builder(getContext()).build();
        xmlWord.setCollect(viewModel.roomGetWord(xmlWord.getKey()) != null);

        DialogWordBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_word, null, false);
        ObservableField<XmlWord> field = new ObservableField<>();
        field.set(xmlWord);
        binding.setWordData(field);
        binding.tvPlay.setOnClickListener(view -> {
            if (TextUtils.isEmpty(xmlWord.getAudio())) {
                return;
            }
            viewModel.mPlayer.pause();
            MediaItem mediaItem = MediaItem.fromUri(xmlWord.getAudio());
            playerWord.setMediaItem(mediaItem);
            playerWord.prepare();
            playerWord.play();
        });
        binding.ivCollectWord.setOnClickListener(view -> viewModel.updateWordCollect(field));
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(xmlWord.getKey())
                .customView(binding.getRoot(), true)
                .negativeText("取消")
                .cancelListener(dialogInterface -> {
                    for (TestingItemViewModel testingItemViewModel : viewModel.observableList) {
                        SelectWordTextView selectContentWordTextView = testingItemViewModel.selectContentWordTextView;
                        if (selectContentWordTextView != null) {
                            selectContentWordTextView.setmTempPosition(new int[]{-1, -1});  //重置单词点击位置
                            selectContentWordTextView.setText(selectContentWordTextView.getText());
                        }
                    }
                    if (null != playerWord) {
                        playerWord.stop();
                        playerWord.release();
                        playerWord = null;
                    }
                })
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();
        binding.tvPlay.callOnClick();
    }

    private void showCorrectDialog(TestingItemViewModel itemViewModel) {
        playerWord = new ExoPlayer.Builder(getContext()).build();
//        xmlWord.setCollect(viewModel.roomGetWord(xmlWord.getKey()) != null);
        DialogCorrectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_correct, null, false);
//        binding.setViewModel(new CorrectDialogViewModel(viewModel, itemViewModel.entity.get()));
        binding.setViewModel(itemViewModel);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        binding.correctClose.setOnClickListener(view -> alertDialog.cancel());
        if (window != null) {
            window.setContentView(binding.getRoot());
            window.setGravity(Gravity.CENTER);
        }
    }

    // 初始化录音组件
    void initRecManager() {
        listener = new AudioRecorderUtils.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                System.out.println("cur db---------------------------->" + db);
                if (recDrawable != null) {
                    playerBinding.exoProgress.setPosition(time);
                    recDrawable.setLevel((int) db * 75);
                }
            }

            @Override
            public void onStop(String filePath) {
                System.out.println("============> 录音保存地址: " + filePath);
                if (recDrawable != null) {
                    playerBinding.exoProgress.setPosition(0);
                    recDrawable.setLevel(0);
                }
                recFilePath = filePath;
            }
        };
        recorderManager = AudioRecorderUtils.getInstance();
        recorderManager.setOnAudioStatusUpdateListener(listener);

    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    public void recordPermissionFail() {
        ToastUtils.showShort("请开启录音权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TestingFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // 开始录音
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void startRec(VoaText voaText) {
        this.voaText = voaText;
        viewModel.mPlayer.pause();
//        requestPermission();
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)) {
            ToastUtils.showShort("请打开录音权限继续");
        } else {
            ToastUtils.showShort("开始录音，再次点击结束录音并打分");
            voaText.setRecording(true);
            AudioRecorderUtils.getInstance().startRecord();
        }
    }

    // 停止录音
    void stopRec() {
        recorderManager.stopRecord();
        if (!"".equals(recFilePath)) {
            voaText.setAudioPath(recFilePath);
            // 成功录音则调用评测接口
            viewModel.uploadTesting(new File(recFilePath), voaText);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recorderManager.cancelRecord();
        recorderManager.removeListener();
    }
}