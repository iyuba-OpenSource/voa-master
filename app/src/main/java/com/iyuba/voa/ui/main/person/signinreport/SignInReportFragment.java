package com.iyuba.voa.ui.main.person.signinreport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.haibin.calendarview.Calendar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.ShareInfoRecord;
import com.iyuba.voa.databinding.FragmentSigninReportBinding;
import com.iyuba.voa.ui.widget.ColorfulMonthView;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.weiget.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.http.NetworkUtil;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/16
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class SignInReportFragment extends BaseFragment<FragmentSigninReportBinding, SignInReportViewModel> {
    private int[] cDate = CalendarUtil.getCurrentDate();
    private TextView title;
    private TextView chooseDate;
    private int flag;
    private Calendar calendar;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_signin_report;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        viewModel.setIsShowBack(true);
        viewModel.setTitleText("打卡报告");
        chooseDate = binding.chooseDate;
        chooseDate.setText("今天的日期：" + cDate[0] + "年" + cDate[1] + "月" + cDate[2] + "日");

        viewModel.loadData(cDate);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.UC.loadDataSuccess.observe(this, sr -> showRankings(sr));

    }

    @Override
    public SignInReportViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(SignInReportViewModel.class);
    }

    public void showRankings(List<ShareInfoRecord> ranking) {
        HashMap<String, Calendar> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        for (ShareInfoRecord record : ranking) {
            try {
                Date date = sdf.parse(record.getCreatetime());
                String item = (date.getYear() + 1900) + "." + (date.getMonth() + 1) + "." + date.getDate();
//                calendar = getSchemeCalendar(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), ColorfulMonthView.scan);
                if (record.getScan() > 1) {
                    calendar = getSchemeCalendar(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), ColorfulMonthView.scan);
                } else {
                    calendar = getSchemeCalendar(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), ColorfulMonthView.unScan);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            map.put(calendar.toString(), calendar);
        }
      /*  CalendarPagerAdapter adapter = (CalendarPagerAdapter) calendarView.getAdapter();
        for (int i = 0; i < adapter.getViews().size(); i++) {
            MonthView monthView = adapter.getViews().valueAt(i);
            int[] startDate = new int[]{1900, 1};
            int[] endDate = new int[]{2049, 12};
            int[] date = CalendarUtil.positionToDate(i, startDate[0], startDate[1]);
            List<DateBean> monthDate = CalendarUtil.getMonthDate(date[0], date[1], null, map);
            for (DateBean dateBean : monthDate) {
                dateBean.setClickInStatus(2);
            }
            monthView.setDateList(monthDate, SolarUtil.getMonthDays(date[0], date[1]));
        }
        adapter.notifyDataSetChanged();*/
        binding.calendarNew.setSchemeDate(map);
    /*    calendarView.setClockInStatus(map);
        if (flag > 0) {
            calendarView.nextMonth();
        } else if (flag < 0) {
            calendarView.lastMonth();
        } else {
            calendarView.setStartEndDate("2016.1", "2028.12")
                    .setDisableStartEndDate("2016.10.10", "2028.10.10")
                    .setInitDate(cDate[0] + "." + cDate[1])
                    .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                    .init();
        }*/
    }


    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }
}
