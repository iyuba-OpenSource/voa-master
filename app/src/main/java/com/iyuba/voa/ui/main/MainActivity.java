package com.iyuba.voa.ui.main;

import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_0;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_1;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_2;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.elvishew.xlog.XLog;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.movies.ui.series.SeriesActivity;
import com.iyuba.sdk.data.iyu.IyuAdClickEvent;
import com.iyuba.trainingcamp.data.model.VoaInfo;
import com.iyuba.trainingcamp.event.PayEvent;
import com.iyuba.trainingcamp.event.PlayLessonEvent;
import com.iyuba.trainingcamp.event.SearchWordEvent;
import com.iyuba.trainingcamp.event.StartMicroEvent;
import com.iyuba.voa.BR;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.databinding.ActivityMainBinding;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.main.course.MicroClassFragment;
import com.iyuba.voa.ui.main.home.HomeFragment;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
import com.iyuba.voa.ui.main.person.PersonFragment;
import com.iyuba.voa.ui.main.search.SearchFragment;
import com.iyuba.voa.ui.main.video.VideoFragment;
import com.iyuba.voa.ui.main.voice.VoiceFragment;
import com.iyuba.voa.ui.vip.VipFragment;
import com.iyuba.voa.ui.vip.order.OrderActivity;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;
import personal.iyuba.personalhomelibrary.event.UserPhotoChangeEvent;

public class MainActivity extends BaseActivity<ActivityMainBinding, BaseTitleViewModel> {


    private List<Fragment> mFragments;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public MainViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    @Override
    public void initData() {
//        AppUtils.getFlavorInfo();
        initBottomTab();
        initFragment();
        EventBus.getDefault().register(this);


    }

    //有道
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(IyuAdClickEvent iyuAdClickEvent) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "推广");
        bundle.putString(Constants.BUNDLE.KEY_0, iyuAdClickEvent.info.linkUrl);
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)  //vip跳转
    public void fromMoocBuyGoldenVip(ImoocBuyVIPEvent event) {
     /*   intent.putExtra("isLogin", isLogin);
        intent.putExtra("endTime", vipTime);*/
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE.KEY, 2);  //2 默认选中黄金vip
        startContainerActivity(VipFragment.class.getCanonicalName(), bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)  //vip跳转
    public void fromHeadlineBuyGoldenVip(HeadlineGoVIPEvent event) {
     /*   intent.putExtra("isLogin", isLogin);
        intent.putExtra("endTime", vipTime);*/
        startContainerActivity(VipFragment.class.getCanonicalName());
    }

    @Subscribe
    public void onEvent(PayEvent event) {
        if (viewModel.checkIsLogin()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(KEY_0, event.getSubject());
        bundle.putString(KEY_1, event.getPrice());
        bundle.putInt(KEY_2, 3);
        bundle.putInt(KEY_3, event.getAmount());
        startActivity(OrderActivity.class, bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StartMicroEvent event) {
        Intent intent = MobClassActivity.buildIntent(this, 3, true, null);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(getBaseContext(), event);
    }

    @Subscribe
    public void onEvent(PlayLessonEvent event) {
        VoaInfo bean = event.info;
        TitleTed titleTed = toTitleted(bean);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE.KEY, titleTed);
        startActivity(DetailActivity.class, bundle);
    }

    public void jumpToCorrectDLActivityByCate(Context context, BasicDLPart basicHDsDLPart) {
        switch (basicHDsDLPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.buildIntent(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "song":
                startActivity(AudioContentActivity.buildIntent(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "japanvideos":
            case "topvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn(), ""));
                break;
            case "smallvideo":
                startActivity(VideoMiniContentActivity.buildIntentForOne(this, basicHDsDLPart.getId(), 0, 1, 1));
                break;
        }
    }


    //视频下载后点击
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.buildIntent(this, dlPart.getCategoryName(),
                        dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "song":
                startActivity(AudioContentActivity.buildIntent(this, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvwideos":
            case "japanvideos":
                startActivity(VideoContentActivity.buildIntent(this, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "smallvideo":
                startActivity(VideoMiniContentActivity.buildIntentForOne(this, dlPart.getId(), 0, 1, 1));
                break;
        }
    }

    //新版个人中心点击列表项
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArtDataSkipEvent event) {
        BasicFavorPart fPart = new BasicFavorPart();
        fPart.setCategoryName(event.voa.categoryString);
        fPart.setTitle(event.voa.title);
        fPart.setTitleCn(event.voa.title_cn);
        fPart.setPic(event.voa.pic);
        fPart.setType(event.type);
        fPart.setId(event.voa.voaid + "");
        fPart.setSound(event.voa.sound);
        goFavorItem(fPart);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(UserPhotoChangeEvent event) {
        Glide.get(getBaseContext()).clearMemory();
        Glide.get(getBaseContext()).clearDiskCache();
    }

    //训练营查词
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SearchWordEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, event.getWord());
        startContainerActivity(SearchFragment.class.getCanonicalName(), bundle);
    }

    //收藏
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        if (fEvent == null) {
            ToastUtils.showShort("目前暂时不支持跳转");
            return;
        }
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);
    }

    private void goFavorItem(BasicFavorPart part) {
        switch (part.getType()) {
            case "news":
            case "voa":
            case "csvoa":
                TitleTed titleTed = toTitleted(part);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE.KEY, titleTed);
                startActivity(DetailActivity.class, bundle);
                break;
            case "bbc":
                startActivity(AudioContentActivityNew.buildIntent(this, part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "song":
                startActivity(AudioContentActivity.buildIntent(this, part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.buildIntent(this, part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "series":
                Intent intent = SeriesActivity.buildIntent(this, part.getSeriesId(), part.getId());
                startActivity(intent);
                break;
            case "smallvideo":
                startActivity(VideoMiniContentActivity.buildIntentForOne(this, part.getId(), 0, 1, 1));
                break;
        }
    }

    public TitleTed toTitleted(BasicFavorPart part) {
        TitleTed article = new TitleTed();
        article.setVoaId(part.getId());
        article.setCategory(part.getCategory());
        article.setCreatTime(part.getCreateTime());
        article.setDescCn(part.getOther1());
        if (part.getPic().startsWith(Constants.CONFIG.IMAGE_URL)) {
            article.setPic(part.getPic().replace(Constants.CONFIG.IMAGE_URL, ""));
        } else if (part.getPic().contains("cms/news/image/")) {
            article.setPic(part.getPic().substring(part.getPic().indexOf("cms/news/image/") + "cms/news/image/".length()));
        } else if (part.getSound().contains(Constants.CONFIG.SOUND_IP)) {
            article.setSound(part.getSound().replace(Constants.CONFIG.SOUND_IP, ""));
        }
        article.setTitle(part.getTitle());
        article.setTitle_cn(part.getTitleCn());
        return article;
    }

    public TitleTed toTitleted(VoaInfo part) {
        TitleTed article = new TitleTed();
        article.setVoaId(String.valueOf(part.id));
        article.setCategory(part.Category);
        article.setCreatTime(part.CreatTime);
        article.setDescCn(part.DescCn);
        if (part.Pic.startsWith(Constants.CONFIG.IMAGE_URL)) {
            article.setPic(part.Pic.replace(Constants.CONFIG.IMAGE_URL, ""));
        } else if (part.Pic.contains("cms/news/image/")) {
            article.setPic(part.Pic.substring(part.Pic.indexOf("cms/news/image/") + "cms/news/image/".length()));
        } else {
            article.setPic(part.Pic);
        }
        if (part.Sound.contains(Constants.CONFIG.SOUND_IP)) {
            article.setSound(part.Sound.replace(Constants.CONFIG.SOUND_IP, ""));
        } else article.setSound(part.Sound);
        article.setTitle(part.Title);
        article.setTitle_cn(part.Title_cn);
        article.setHotFlg(part.HotFlg);
        article.setUrl(part.Url);
        return article;
    }

    private void initFragment() {
      /*  ArrayList<Integer> tempList = new ArrayList<>();
        tempList.add(21);
        tempList.add(24);
        tempList.add(25);
        Bundle bundle = MobClassFragment.buildArguments(3, false, null, tempList);
        MobClassFragment mobClassFragment = MobClassFragment.newInstance(bundle);
*/
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new MicroClassFragment());
        mFragments.add(new VideoFragment());
        mFragments.add(new VoiceFragment());
        mFragments.add(new PersonFragment());
//        mFragments.add(new TabMeFragment());
        //默认选中第一个
        switch (BuildConfig.FLAVOR) {
            case "other":
            case "cnn":
                if (DateUtil.getNowTimeMM() <
                        DateUtil.stringToDate("2023-01-15", DateUtil.YEAR_MONTH_DAY).getTime()) {
                    mFragments.clear();
                    mFragments.add(new VideoFragment());
//            mFragments.add(new VoiceFragment());
                    mFragments.add(new PersonFragment());
                }
                break;
            case "meiyu":
                break;
            default:
                //绑定服务
                Intent serviceIntent = new Intent(this, MusicService.class);
                bindService(serviceIntent, connection, BIND_AUTO_CREATE);
                break;
        }

        commitAllowingStateLoss(0);
    }

    private void initBottomTab() {
        NavigationController navigationController = binding.mainTab.material()
                .addItem(R.drawable.ic_baseline_home_24, "首页")
                .addItem(R.drawable.ic_baseline_menu_book_24, "微课")
                .addItem(R.drawable.ic_baseline_ondemand_video_24, "视频")
                .addItem(R.drawable.ic_baseline_keyboard_voice_24, "配音")
                .addItem(R.drawable.ic_baseline_person_24, "我的")
//                .addItem(R.`mipmap.wode_select, "个人中心")
                .setDefaultColor(ContextCompat.getColor(this, R.color.textColorVice))
                .build();

        switch (BuildConfig.FLAVOR) {
            case "other":
            case "cnn":
                if (DateUtil.getNowTimeMM() <
                        DateUtil.stringToDate("2023-01-15", DateUtil.YEAR_MONTH_DAY).getTime()) {
                    navigationController.removeItem(1);
                    navigationController.removeItem(1);
                    navigationController.removeItem(1);
                }
                break;
            case "meiyu":
                break;
        }


        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frameLayout, mFragments.get(index));
//                transaction.commitAllowingStateLoss();
                switch (index) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        if (musicBinder != null) {
                            musicBinder.getService().mediaPlayer.pause();
                            musicBinder.getService().updatePlayAndPause();
                        }
                        break;
                }
                commitAllowingStateLoss(index);
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }


    private MusicService.MusicBinder musicBinder;
    /**
     * 服务连接
     */
    private ServiceConnection connection = new ServiceConnection() {
        /**
         * 连接服务
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = ((AppApplication) getApplication()).getMusicBinder();

            XLog.d("Service与Activity已连接");
        }

        //断开服务
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ((AppApplication) getApplication()).setMusicBinder(null);
        }
    };

    public void onBackPressed() {  //返回键退退会桌面，保证后台播放服务不杀掉
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XLog.d("Service与Activity已断开");
        unbindService(connection);
    }

}


