package com.iyuba.voa.ui.main.home.detail;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elvishew.xlog.XLog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.databinding.ActivityDetailBinding;
import com.iyuba.voa.ui.base.BaseTitleActivity;
import com.iyuba.voa.ui.base.adapter.BaseFragmentPagerAdapter;
import com.iyuba.voa.ui.main.home.detail.content.ContentFragment;
import com.iyuba.voa.ui.main.home.detail.exercise.ExerciseFragment;
import com.iyuba.voa.ui.main.home.detail.ranking.RankFragment;
import com.iyuba.voa.ui.main.home.detail.testing.TestingFragment;
import com.iyuba.voa.ui.main.home.detail.word.WordFragment;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.FileUtils;
import com.iyuba.voa.utils.ShareUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.SDCardUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class DetailActivity extends BaseTitleActivity<ActivityDetailBinding, DetailViewModel> {

    private List<Fragment> mFragments;
    private List<String> titlePager;
    private String[] stringArray;
    private MenuItem menuItem;
    private Bundle extras;
    private Menu menu;

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();

        viewModel.setIsShowBack(true);
        viewModel.setIsShowRightMenu(true);
        extras = getIntent().getExtras();
        viewModel.titleTed = extras.getParcelable(Constants.BUNDLE.KEY);

//        viewModel.saveTitleTed();
        viewModel.setTitleText(viewModel.titleTed.getTitle());
//        String voaid = extras.getString(Constants.BUNDLE.KEY);
//        String title = extras.getString(Constants.BUNDLE.KEY_0);

        loadTitle();
        viewModel.loadData();

    }


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public DetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(DetailViewModel.class);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.getRightView.observe(this, view -> view.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(this, view1);//1.实例化PopupMenu
            menu = popupMenu.getMenu();
            getMenuInflater().inflate(R.menu.menu_detail, menu);
            menu.findItem(R.id.menu_collect).setTitle(viewModel.isCollect.get() ? "取消收藏" : "收藏");
            menu.findItem(R.id.menu_download).setTitle(viewModel.isDownload.get() ? "已下载" : "下载");

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_exportpdf:
                        MaterialDialog dialog = new MaterialDialog.Builder(DetailActivity.this)
                                .title("请选择导出形式")
                                .items(R.array.export_type)
                                .itemsCallback((dialog1, itemView, position, text) -> {
                                    if (!viewModel.checkIsVIP(null))
                                        new MaterialDialog.Builder(DetailActivity.this)
                                                .content("生成PDF每篇文章将消耗20积分,是否生成？")
                                                .positiveText("确定")
                                                .negativeText("取消")
                                                .onPositive((dialog2, which) -> {
                                                    viewModel.startInterfaceAddScore(position == 0 ? 1 : 3, 40);  //1英文 3中英双语
                                                })
                                                .canceledOnTouchOutside(false)//点击外部不取消对话框
                                                .build().show();
                                    else viewModel.exportPdf(position == 0 ? 1 : 3);
                                })
                                .canceledOnTouchOutside(true)//点击外部取消对话框
                                .build();
                        dialog.show();
                        break;
                    case R.id.menu_collect:
                        this.menuItem = item;
                        viewModel.collect();
                        break;
                    case R.id.menu_share:
//                        String imageUrl = Constants.CONFIG.IMAGE_URL + article.getPic();
                        String content = "我正在读:" + viewModel.titleTed.getTitle_cn();
                        String siteUrl = "http://m." + Constants.CONFIG.DOMAIN + "/news.html?type=voa&id=" + viewModel.titleTed.getVoaId();
                        ShareUtils.showShare(DetailActivity.this, viewModel.titleTed.getPic(), siteUrl, viewModel.titleTed.getTitle(), content, new PlatformActionListener() {
                            @Override
                            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                if (platform.getName().equals("QQ") || platform.getName().equals("Wechat") || platform.getName().equals("WechatFavorite")) {
                                    viewModel.startInterfaceAddScore(-2, 7);
                                } else if (platform.getName().equals("QZone") || platform.getName().equals("WechatMoments") || platform.getName().equals("SinaWeibo") || platform.getName().equals("TencentWeibo")) {
                                    viewModel.startInterfaceAddScore(-2, 19);
                                }
                            }

                            @Override
                            public void onError(Platform platform, int i, Throwable throwable) {

                            }

                            @Override
                            public void onCancel(Platform platform, int i) {

                            }
                        });
                        break;
                    case R.id.menu_download:
                        if (!viewModel.isDownload.get()) {
                            if (!viewModel.checkIsVIP(null)) {
                                new MaterialDialog.Builder(DetailActivity.this)
                                        .content("下载文章将消耗20积分,是否下载？")
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .onPositive((dialog2, which) -> {
                                            viewModel.startInterfaceAddScore(-1, 40);  //1英文 3中英双语  -1下载文章  -2增加积分
                                        })
                                        .canceledOnTouchOutside(false)//点击外部不取消对话框
                                        .build().show();
                            } else downloadArticle();
                        } else {
                            ToastUtils.showShort("已下载");
                        }
                        break;
                    case R.id.menu_timing:
                        new MaterialDialog.Builder(DetailActivity.this)
                                .title("定时播放")
                                .content("设置10，表示10分钟后停止播放")
                                .input("请输入定时时长", "10", (dialog12, input) -> {
                                    ToastUtils.showShort("文章将在" + input + "分钟后" +
                                            "停止播放");
                                    AppApplication application = (AppApplication) getApplication();
                                    MusicService musicService = application.getMusicBinder().getService();
                                    String trim = input.toString().trim();
                                    int min = Integer.parseInt(TextUtils.isEmpty(trim) ? "0" : trim);
                                    if (min > 0 && min < 1000)
                                        musicService.startPlayerListener(min);
                                    else {
                                        ToastUtils.showShort("输入不合法");
                                    }
                                })
                                .inputType(InputType.TYPE_CLASS_NUMBER)
                                .positiveText("确定")
                                .negativeText("取消")
                                .contentColor(getResources().getColor(R.color.black))
                                .canceledOnTouchOutside(false)//点击外部不取消对话框
                                .build().show();
                        break;
                }
                return false;
            });
            popupMenu.show();
        }));

        viewModel.UC.loadDataSuccess.observe(this, datas -> {
            mFragments = pagerFragment(datas);
            titlePager = pagerTitleString();
            //设置Adapterx
          /*  BaseFragmentPager2Adapter pagerAdapter = new BaseFragmentPager2Adapter(DetailActivity.this, mFragments, titlePager);
            binding.viewPager.setAdapter(pagerAdapter);
            binding.viewPager.setOffscreenPageLimit(1);
            new TabLayoutMediator(binding.tabs, binding.viewPager,
                    (tab, position) -> tab.setText(titlePager.get(position))
            ).attach();*/
            BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments, titlePager);
            binding.viewPager.setAdapter(pagerAdapter);
            binding.viewPager.setOffscreenPageLimit(1);
            binding.tabs.setupWithViewPager(binding.viewPager);
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        });

        viewModel.UC.collectSuccess.observe(this, s -> menuItem.setTitle(viewModel.isCollect.get() ? "收藏" : "取消收藏"));
        viewModel.UC.pdfSuccessDialog.observe(this, url -> {
            showPdfDialog(url);
        });
        viewModel.UC.startDownloadArticle.observe(this, s -> downloadArticle());
    }

    private void downloadArticle() {
        String destFileDir = SDCardUtils.getFileDataPath() + "/media_music/";
        String destFileName = DateUtil.getNowTimeS() + ".mp3";
        DownLoadManager.getInstance().load(viewModel.titleTed.getSound(), new ProgressCallBack(destFileDir, destFileName) {
            @Override
            public void onSuccess(Object o) {
                ToastUtils.showShort("下载完成");
                viewModel.titleTed.setSound(destFileDir + destFileName);
                viewModel.titleTed.setHotFlg(viewModel.titleTed.getHotFlg() + ",3");

                viewModel.saveTitleTed();
                viewModel.isDownload.set(true);
            }

            @Override
            public void progress(long progress, long total) {
                menu.findItem(R.id.menu_download).setTitle("下载中");
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort("下载出错");
            }
        });
    }

    private void showPdfDialog(String path) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("已为您导出PDF链接")
                .content(path)
                .positiveText("复制")
                .negativeText("发送")
                .onPositive((dialog1, which) -> FileUtils.copyStr(DetailActivity.this, path))
                .onNegative((dialog12, which) -> {
                    ShareUtils.showShare(DetailActivity.this, viewModel.titleTed.getPic(),
                            path, "单词本PDF", "-北京爱语吧科技有限公司-", new PlatformActionListener() {
                                @Override
                                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                }

                                @Override
                                public void onError(Platform platform, int i, Throwable throwable) {
                                    XLog.i("okCallbackonError ,onError" + throwable.getMessage());
                                }

                                @Override
                                public void onCancel(Platform platform, int i) {
                                    XLog.i("okCallbackonCancel ,onCancel");
                                }
                            });
                })
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }

    protected List<Fragment> pagerFragment(JsonObject jo) {
        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            switch (i) {
                case 0:
                    ContentFragment contentFragment = new ContentFragment();
                    contentFragment.setArguments(extras);
                    list.add(contentFragment);
                    break;
                case 1:
                    TestingFragment testingFragment = new TestingFragment();
                    testingFragment.setArguments(extras);
                    list.add(testingFragment);
                    break;
                case 2:
                    ExerciseFragment exerciseFragment = new ExerciseFragment();
                    JsonElement voaexam = jo.get("voaexam");
                    if (null != voaexam) {
                        extras.putString(Constants.BUNDLE.KEY_3, voaexam.toString());
                        exerciseFragment.setArguments(extras);
                    }
                    list.add(exerciseFragment);
                    break;
                case 3:
                    WordFragment wordFragment = new WordFragment();
                    JsonElement voawords = jo.get("voawords");
                    if (null != voawords) {
                        extras.putString(Constants.BUNDLE.KEY_4, voawords.toString());
                        wordFragment.setArguments(extras);
                    }
                    list.add(wordFragment);
                    break;
                case 4:
                    RankFragment rankFragment = new RankFragment();
                    rankFragment.setArguments(extras);
                    list.add(rankFragment);
            }

        }
        return list;
    }

    protected List<String> pagerTitleString() {
        return new ArrayList<>(Arrays.asList(stringArray));
    }


    public void loadTitle() {
        stringArray = Utils.getContext().getResources().getStringArray(R.array.home_detail);

    }

}