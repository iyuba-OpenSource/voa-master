package com.iyuba.voa.app.service;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.receiver.NotificationClickReceiver;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.SPUtils;


/**
 * 音乐后台服务
 *
 * @author llw
 */
public class MusicService extends LifecycleService {

    private static final String TAG = "MusicService";
    /**
     * 歌曲播放
     */
    public static final String PLAY = "play";
    /**
     * 歌曲暂停
     */
    public static final String PAUSE = "pause";
    /**
     * 上一曲
     */
    public static final String PREV = "prev";
    /**
     * 下一曲
     */
    public static final String NEXT = "next";
    /**
     * 关闭通知栏
     */
    public static final String CLOSE = "close";
    /**
     * 进度变化
     */
    public static final String PROGRESS = "progress";

    /**
     * 用于判断当前滑动歌名改变的通知栏播放状态
     */
    public static final String IS_CHANGE = "isChange";
    /**
     * 歌曲间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    /**
     * 歌曲列表
     */
    private static List<TitleTed> mList = new ArrayList<>();

    /**
     * 音乐播放器
     */
    public ExoPlayer mediaPlayer;
    /**
     * 记录播放的位置
     */
    int playPosition = -1;

    /**
     * 通知
     */
    private static Notification notification;
    /**
     * 通知栏视图
     */
    private static RemoteViews remoteViews;
    /**
     * 通知ID
     */
    private int NOTIFICATION_ID = 1;
    /**
     * 通知管理器
     */
    private static NotificationManager manager;
    /**
     * 音乐广播接收器
     */
    private MusicReceiver musicReceiver;

    public String beginPlayTime;


    public long endTiming;  //定时结束时间-
    /**
     * 通知栏控制Activity页面UI
     */
//    private LiveDataBus.BusMutableLiveData<String> activityLiveData;

    /**
     * Activity控制通知栏UI
     */
//    private LiveDataBus.BusMutableLiveData<String> notificationLiveData;
    private Disposable mActivitySubscription;
    private Player.Listener playerListener;
    private Disposable mDisposable;

    public class MusicBinder extends Binder {
        public MusicBinder() {
            ((AppApplication) getApplication()).setMusicBinder(this);
            XLog.d("serviceonCreate");
        }

        public MusicService getService() {
            return MusicService.this;
        }

        public void setValue(List<TitleTed> datas) {
            mList.addAll(datas);
        }

        public List<TitleTed> getList() {
            return mList;
        }

        public TitleTed getIsPlaySource() {
            if (mList.size() > 0 && playPosition != -1)
                return mList.get(playPosition);
            else {
                return null;
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mList = LitePal.findAll(Song.class);
        //初始化RemoteViews配置
        initRemoteViews();
        //初始化通知
        initNotification();
        initExoPlayer();
        //Activity的观察者
//        activityObserver();

        //注册动态广播
        registerMusicReceiver();
//        startForeground(1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化通知
     */
    @SuppressLint("NotificationTrampoline")
    public void initNotification() {

        String channelId = "play_control";
        String channelName = "播放控制";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);

/*        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        setNoticeIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_MUTABLE);*/
        //初始化通知
        notification = new NotificationCompat.Builder(this, "play_control")
//                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

    }

    public void setNoticeIntent() {
        //通知点击事项
        //点击整个通知时发送广播
        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        if (mList.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE.KEY, mList.get(playPosition));
            bundle.putInt(Constants.BUNDLE.KEY_0, playPosition);
            intent.putExtras(bundle);
        }

        //pendingintent传值经常获取到的值是第一次的值或者null，这个跟第二个参数和最后一个参数选择有关系。
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notification.contentIntent = pendingIntent;
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void initExoPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new ExoPlayer.Builder(this).build();
        }
        playerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (!isPlaying) {  //暂停
//                    viewModel.uploadRecord(0);
//                    stopPlayerListener();
//                    viewModel.endTime = DateUtil.getNowTime();
                }
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onEvents(Player player, Player.Events events) {
                XLog.i("onEvents-" + events.toString());
                Player.Listener.super.onEvents(player, events);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                XLog.i("onPlaybackStateChanged-" + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
//                        viewModel.beginTime = DateUtil.getNowTime();
                        break;
                    case Player.STATE_ENDED:  //播放结束
//                        viewModel.endTime = DateUtil.getNowTime();
//                        viewModel.currentPlayWords = viewModel.observableList.size();
//                        viewModel.uploadRecord(1);
                        break;
                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }

        };
//        mediaPlayer.addListener(playerListener);
    }

    public void play(TitleTed data) {
        for (int i = 0; i < mList.size(); i++) {
            TitleTed titleTed = mList.get(i);
            if (titleTed.getVoaId().equals(data.getVoaId())) {
                play(i);
                return;
            }
        }
        mList.add(data);
        play(mList.size() - 1);
    }

    /**
     * 播放
     */
    public void play(int position) {
        if (mediaPlayer == null) {
            mediaPlayer = new ExoPlayer.Builder(this).build();
            //监听音乐播放完毕事件，自动下一曲
//            mediaPlayer.setOnCompletionListener(this);
        }
        //播放时 获取当前歌曲列表是否有歌曲
//        mList = LitePal.findAll(Song.class);
        if (mList.size() <= 0) {
            return;
        }
        if (playPosition != -1 && mList.get(position).getVoaId().equals(mList.get(playPosition).getVoaId())) {  //2次播放的是同一篇文章
            return;
        }

        //切歌前先重置，释放掉之前的资源
//        mediaPlayer.release();
        mediaPlayer.stop();
        playPosition = position;
        //设置播放音频的资源路径
        MediaItem mediaItem = MediaItem.fromUri(mList.get(position).getSound());
        mediaPlayer.setMediaItem(mediaItem);
        mediaPlayer.prepare();
        mediaPlayer.play();

        //显示通知
        updateNotificationShow(position);

        //更新到Activity
        RxBus.getDefault().post(PLAY);

        //更新播放进度
        updateProgress();

    }

    //延迟停止
    public void startPlayerListener(int min) {
        mDisposable = Observable.timer(min, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> {
                    XLog.i((endTiming - DateUtil.getNowTimeS()) + "s后播放结束");
                }).subscribe(aLong -> {
                    XLog.i((endTiming - DateUtil.getNowTimeS()) + "s后播放结束" + Thread.currentThread().getName());
                    if (mediaPlayer.isPlaying()) {
                        XLog.i("播放结束");
                        pauseOrContinueMusic();
                        mDisposable.dispose();
                        mDisposable = null;
                    }
                });
    }


    /**
     * 暂停/继续 音乐
     */
    public void pauseOrContinueMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            RxBus.getDefault().post(PAUSE);
        } else {
            mediaPlayer.play();
            RxBus.getDefault().post(PLAY);
        }
        //更改通知栏播放状态
        updateNotificationShow(playPosition);
    }

    /**
     * 关闭音乐
     */
    public void closeMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            //释放
            mediaPlayer.release();
        }
    }

    /**
     * 关闭音乐通知栏
     */
    public void closeNotification() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        manager.cancel(NOTIFICATION_ID);

        RxBus.getDefault().post(CLOSE);
    }

    /**
     * 下一首
     */
    public void nextMusic() {
        if (playPosition >= mList.size() - 1) {
            playPosition = 0;
        } else {
            playPosition += 1;
        }
        RxBus.getDefault().post(NEXT);
        play(playPosition);
    }

    /**
     * 上一首
     */
    public void previousMusic() {
        if (playPosition <= 0) {
            playPosition = mList.size() - 1;
        } else {
            playPosition -= 1;
        }
        RxBus.getDefault().post(PREV);
        play(playPosition);
    }


    /**
     * 获取当前播放位置
     *
     * @return
     */
    public int getPlayPosition() {
        return playPosition;
    }


    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    private void initRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_music);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        //为prev控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);


        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        //为play控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        //为close控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent);

    }

    public void updatePlayAndPause() {
        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_pause);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_play);
        }
        manager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 更改通知的信息和UI
     *
     * @param position
     */
    public void updateNotificationShow(int position) {
        //播放状态判断
        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_pause);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_play);
        }
        //封面专辑
        //歌曲名
        remoteViews.setTextViewText(R.id.tv_notification_song_name, mList.get(position).getTitle_cn());
        //歌手名
        remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getDescCn());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);
        try {
            Glide.with(this)
                    .load(mList.get(position).getPic())
                    .asBitmap() //必须
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            remoteViews.setImageViewBitmap(R.id.iv_album_cover, resource);
                            //封面专辑
                            //歌曲名
                            remoteViews.setTextViewText(R.id.tv_notification_song_name, mList.get(position).getTitle());
                            //歌手名
                            remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getDescCn());
                            //发送通知
                            manager.notify(NOTIFICATION_ID, notification);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 更改通知的信息和UI
     *
     * @param titleTed
     */
    public void updateNotificationShow(TitleTed titleTed) {
        //播放状态判断
        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_pause);
        } else {
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.exo_styled_controls_play);
        }
        //封面专辑
        //歌曲名
        remoteViews.setTextViewText(R.id.tv_notification_song_name, titleTed.getTitle_cn());
        //歌手名
        remoteViews.setTextViewText(R.id.tv_notification_singer, titleTed.getDescCn());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);
        Bitmap bitmap = null;
        /*  try {
            Glide.with(this)
                    .load(mList.get(position).getPic())
                    .asBitmap() //必须
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            remoteViews.setImageViewBitmap(R.id.iv_album_cover, bitmap);
                            //封面专辑
                            //歌曲名
                            remoteViews.setTextViewText(R.id.tv_notification_song_name, mList.get(position).getTitle());
                            //歌手名
                            remoteViews.setTextViewText(R.id.tv_notification_singer, mList.get(position).getDescCn());
                            //发送通知
                            manager.notify(NOTIFICATION_ID, notification);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        manager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        if (SDK_INT < Build.VERSION_CODES.O)
            return;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        manager.createNotificationChannel(channel);
    }


    /**
     * 注册动态广播
     */
    private void registerMusicReceiver() {
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }

    /**
     * 广播接收器 （内部类）
     */
    public class MusicReceiver extends BroadcastReceiver {

        public static final String TAG = "MusicReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            SPUtils.getInstance().put(IS_CHANGE, true);
            //UI控制
            UIControl(intent.getAction());
        }

    }

    /**
     * Activity的观察者
     */
    private void activityObserver() {
        mActivitySubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(state -> {
//                    loadData();
                    UIControl(state);
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mActivitySubscription);
    }

    /**
     * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
     *
     * @param state 状态码
     */
    private void UIControl(String state) {
        switch (state) {
            case PLAY:
                //暂停或继续
                pauseOrContinueMusic();
                XLog.d(PLAY + " or " + PAUSE);
                break;
            case PREV:
                previousMusic();
                XLog.d(PREV);
                break;
            case NEXT:

                nextMusic();
                XLog.d(NEXT);
                break;
            case CLOSE:
                closeNotification();
                AppManager.getAppManager().AppExit();
                XLog.d(CLOSE);
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //进度发生改变时，
            RxBus.getDefault().post(PROGRESS);

            //更新进度
//            updateProgress();
            return true;
        }
    });

    /**
     * 更新进度
     */
    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        // 获取一个现成的消息
        Message msg = Message.obtain();
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = (int) mediaPlayer.getCurrentPosition();
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicReceiver != null) {
            //解除动态注册的广播
            closeNotification();
            unregisterReceiver(musicReceiver);
        }
    }

}
