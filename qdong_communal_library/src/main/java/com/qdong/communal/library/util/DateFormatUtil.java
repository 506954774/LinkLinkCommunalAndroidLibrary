package com.qdong.communal.library.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期、时间差工具了
 *
 * @author LHD
 */
@SuppressLint("SimpleDateFormat")
public class DateFormatUtil {

    /**
     * 年-月-日 yyyy-MM-dd
     */
    public static String YMD = "yyyy-MM-dd";
    /**
     * 年-月-日 时:分 yyyy-MM-dd HH:mm
     */
    public static String YMDHM = "yyyy-MM-dd HH:mm";
    /**
     * 年-月-日 时:分:秒 yyyy-MM-dd HH:mm:ss
     */
    public static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 年-月-日 时:分:秒 yyyy-MM-dd HH:mm:ss
     */
    public static String YMDHMSS = "yyyy/MM/dd HH:mm:ss";

    //billUtime=2017-03-24 18:56:37

    /**
     * 根据两个时间段，获取时间差
     *
     * @param oldTime 以前的时间
     * @return 提示字符串
     */
    public static String getGoByTime(long oldTime) {
        long notTime = System.currentTimeMillis();
        if (!isEqualsTime(oldTime, notTime, "yyyy")) {
            return longToString(oldTime, "MM-dd HH:mm");
        }
        if (isEqualsTime(oldTime, notTime, "yyyy-MM")) {
            switch (getGoByTime(oldTime, notTime, "dd")) {
                case 0:
                    int goByHour = getGoByTime(oldTime, notTime, "HH");
                    if (goByHour > 0) {
                        return "今天" + longToString(oldTime, "HH:mm");
                    } else {
                        int goByMinute = getGoByTime(oldTime, notTime, "mm");
                        if (goByMinute <= 0) {
                            return "刚刚";
                        }
                        return goByMinute + "分钟前";
                    }
                case 1:
                    return "昨天 " + longToString(oldTime, "HH:mm");
                case 2:
                    return "前天 " + longToString(oldTime, "HH:mm");
            }
        }
        return longToString(oldTime, "MM-dd HH:mm");
    }

    /**
     * 根据距离今天的日期
     *
     * @param oldTime 以前的时间
     * @return 提示字符串
     */
    public static String getGoByDay(long oldTime) {
        long notTime = System.currentTimeMillis();
        if (!isEqualsTime(oldTime, notTime, "yyyy")) {
            return longToString(oldTime, "MM-dd HH:mm");
        }
        if (isEqualsTime(oldTime, notTime, "yyyy-MM")) {
            switch (getGoByTime(oldTime, notTime, "dd")) {
                case 0:
                    return "今日";
                case 1:
                    return "昨天 ";
                case 2:
                    return "前天 ";
            }
        }
        return longToString(oldTime, "MM月dd日");
    }

    /**
     * 按指定格式验证两时间值是否相等
     *
     * @param oldTime
     * @param notTime
     * @return
     */
    public static boolean isEqualsTime(long oldTime, long notTime, String formatType) {
        return longToString(oldTime, formatType).equals(longToString(notTime, formatType));
    }

    /**
     * 计算指定格式时间差
     *
     * @param oldTime 过去的时间
     * @param notTime 现在的时间
     * @return 相差天数
     */
    public static int getGoByTime(long oldTime, long notTime, String formatType) {
        int oldHour = stringChangeInt(longToString(oldTime, formatType));
        int notHour = stringChangeInt(longToString(notTime, formatType));
        return notHour - oldHour;
    }

    /**
     * 从字符串转为int类型
     *
     * @param str
     * @return
     */
    public static int stringChangeInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * 获取指定格式的当前系统时间字符串
     *
     * @param fmt
     * @return
     */
    public static String getDate(String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(System.currentTimeMillis());
    }

    /**
     * 获取指定格式的指定时间字符串
     *
     * @param fmt  匹配规格
     * @param time 时间毫秒值
     * @return 格式化后的字符串
     */
    public static String getDate(String fmt, long time) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(time);
    }
    /**
     * 获取指定格式的指定时间字符串
     *
     * @param fmt  匹配规格
     * @param time 时间毫秒值
     * @return 格式化后的字符串
     */
    public static String getDate(String fmt, Long time) {
        if(time==null){
            return "无";
        }
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(time);
    }

    /**
     * 获取指定格式的指定时间字符串
     *
     * @param fmt  匹配规格
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String getDate(Date date, String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(date);
    }

    /**
     * 按指定格式格式化long时间值
     *
     * @param time
     * @return
     */
    public static String longToString(long time, String formatType) {
        SimpleDateFormat format = new SimpleDateFormat(formatType);
        return format.format(time);
    }

    /**
     * 秒转换为时间
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToMin(int time) {
        int hour = time / 3600;
        int minute = time / 60;
        if (hour > 0 && minute > 0)
            minute = minute - (hour * 60);
        return formaTime(hour) + ":" + formaTime(minute);
    }

    /**
     * 秒转换为时:分:秒
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToDHM(int time) {
        int hour = time / 3600;
        int minute = time / 60;
        int secs = time - minute * 60;
        if (hour > 0 && minute > 0)
            minute = minute - (hour * 60);
        return formaTime(hour) + ":" + formaTime(minute) + ":" + formaTime(secs);
    }

    /**
     * 秒转换为时间
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToHMin(int time) {
        int hour = time / 3600;
        int minute = time / 60;
        if (hour > 0 && minute > 0)
            minute = minute - (hour * 60);
        /** Bugfix-325-20160428-yyh-START */
        if (hour == 0 && minute == 0)
            return "0";
        /** Bugfix-325-20160428-yyh-END */
        else if (hour == 0)
            return minute + "分钟";
        else
            return hour + "小时" + minute + "分钟";

    }

    /**
     * 秒转换为时间
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToTime(int time) {
        int hour = time / 3600;
        int minute = time / 60;
        if (hour > 0 && minute > 0)
            minute = minute - (hour * 60);
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-START */
        return formaTime(hour) + "." + formaTime(minute);
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-END */
    }

    /**
     * 秒转换为时间
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToTime(long time) {
        int day = (int) (time / (24 * 3600));
        int hour = (int) (time / 3600);
        int minute = (int) (time / 60);
        if (hour > 0)
            minute = minute - (hour * 60);
        if (day > 0) {
            hour = hour - (day * 24);
            return day + "天" + formaTime(hour) + "时" + formaTime(minute) + "分";
        }
        return formaTime(hour) + "时" + formaTime(minute) + "分";
    }

    /**
     * 时间拼接
     *
     * @param num 数值
     * @return 格式化好的时间字符串
     */
    public static String formaTime(int num) {
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-START */
        DecimalFormat format = new DecimalFormat("00");
        return format.format(num);
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-END */
    }

    /**
     * 根据毫秒获得 某年某月某日 星期几
     *
     * @param time
     * @param week
     * @return
     */
    public static String longToWMY(long time, boolean week) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月"
                + cal.get(Calendar.DAY_OF_MONTH) + "日";
        if (week) {
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            switch (w) {
                case 0:
                    return date + " " + "星期日";
                case 1:
                    return date + " " + "星期一";
                case 2:
                    return date + " " + "星期二";
                case 3:
                    return date + " " + "星期三";
                case 4:
                    return date + " " + "星期四";
                case 5:
                    return date + " " + "星期五";
                case 6:
                    return date + " " + "星期六";
                default:
                    break;
            }
        }
        return date;
    }

    /**
     * 时间加减法
     *
     * @param value 加减值
     * @return 格式(yyyyMMdd)的日期字符串
     */
    public static String addAndSubtractString(int value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(addAndSubtractLong(value));
    }

    /**
     * 时间加减法
     *
     * @param value 加减值
     * @return long类型数据
     */
    public static long addAndSubtractLong(int value) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date()); // 获取现在时间
        rightNow.add(Calendar.DATE, value);// 你要加减的日期
        return rightNow.getTime().getTime();
    }

    /**
     * 根据生日获取年龄
     *
     * @return 年龄
     */
    public static int getAge(String birthday) {
        if (TextUtils.isEmpty(birthday))
            return 0;
        String year = birthday.substring(0, birthday.indexOf("-"));
        int y = stringChangeInt(year);
        if (y <= 0)
            return 0;
        int age = stringChangeInt(getDate("yyyy")) - stringChangeInt(year);
        return age <= 0 ? 0 : age;
    }

    /**
     * 时间是否合法
     *
     * @param choiceTime 选择时间
     * @param contTime   对比时间
     * @return 结果
     */
    public static boolean isLegalTime(String choiceTime, String contTime, String fmt) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(fmt);
            Date choice = format.parse(choiceTime);
            Date cont = format.parse(contTime);
            return choice.before(cont);
        } catch (ParseException e) {

        }
        return true;
    }

    /**
     * String 类型的日期转换为Date对象
     *
     * @param dateStr 日期字符串
     * @param fmt     解析方式
     * @return
     */
    public static Date toDate(String dateStr, String fmt) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(fmt);
            return format.parse(dateStr);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * 检测时间是否为当天时间
     *
     * @param time 时间值
     * @return 判断结果
     */
    public static boolean isToday(long time) {
        return getToDayStartTime() <= time && time <= getToDayEndTime();
    }

    /**
     * 获取当前的开始时间
     *
     * @return long类型
     */
    public static long getToDayStartTime() {
        return getTimeToStart(System.currentTimeMillis());
    }

    /**
     * 获取当前的结束时间
     *
     * @return long类型
     */
    public static long getToDayEndTime() {
        return getTimeToEnd(System.currentTimeMillis());
    }

    /**
     * 获取所传时间的0点0时0分
     */
    public static long getTimeToStart(long time) {
        String time1 = longToString(time, YMD) + " 00:00:00";
        return toDate(time1, YMDHMS).getTime();
    }

    /**
     * 获取所传时间的23点59时59分
     */
    public static long getTimeToEnd(long time) {
        String time1 = longToString(time, YMD) + " 23:59:59";
        return toDate(time1, YMDHMS).getTime();
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 确定是否年满指定周岁
     *
     * @param date  需要验证的日期
     * @param years 周岁值
     * @return true 已年满，false 未满
     */
    public static boolean over(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        Calendar now = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int nowYear = now.get(Calendar.YEAR);
        if (year < nowYear) {
            return true;
        } else if (year == nowYear && cal.get(Calendar.MONTH) < now.get(Calendar.MONTH)) {
            return true;
        } else if (year == nowYear && cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                && cal.get(Calendar.DATE) <= now.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    /**
     * 取得一年的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week_of_year = c.get(Calendar.WEEK_OF_YEAR);
        return week_of_year;
    }

    /**
     * 取得当天日期是周几
     *
     * @param date
     * @return
     */
    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }


    public static long getTime(String dateStr, String fmt) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(fmt);
            return format.parse(dateStr).getTime();
        } catch (ParseException e) {
        }
        return 0;
    }

    public static String[] getDateAndTime(String time){
        //YMDHMS
        String [] result={"",""};
        try {
            Date date= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            if(System.currentTimeMillis()-date.getTime()<24*3600*1000){
                result[0]="今天";
            }
            if(System.currentTimeMillis()-date.getTime()>24*3600*1000){
                result[0]="昨天";
            }
            if(System.currentTimeMillis()-date.getTime()>2*24*3600*1000){
                result[0]="前天";
            }
            if(System.currentTimeMillis()-date.getTime()>3*24*3600*1000){
                result[0]=new SimpleDateFormat("MM-dd").format(date);
            }

            result[1]=new SimpleDateFormat("HH:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            return  result;
        }
    }

    public static String[] getDateAndTime2(String time){
        //YMDHMS cmtDate=2017-03-09 11:12
        String [] result={"",""};
        try {
            Date date= new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
            if(System.currentTimeMillis()-date.getTime()<24*3600*1000){
                result[0]="今天";
            }
            if(System.currentTimeMillis()-date.getTime()>24*3600*1000){
                result[0]="昨天";
            }
            if(System.currentTimeMillis()-date.getTime()>2*24*3600*1000){
                result[0]="前天";
            }
            if(System.currentTimeMillis()-date.getTime()>3*24*3600*1000){
                result[0]=new SimpleDateFormat("MM-dd").format(date);
            }

            result[1]=new SimpleDateFormat("HH:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            return  result;
        }
    }
    public static String getTimeNow(){
        //YMDHMS cmtDate=2017-03-09 11:12
        String result="";
        try {
            result= new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return  result;
        }
    }

    public static boolean judgeTime(Context context,String startTime, String endTime){
        boolean result=false;
        try {

            if(!TextUtils.isEmpty(startTime)&&!TextUtils.isEmpty(endTime)){
                SimpleDateFormat format=new SimpleDateFormat(DateFormatUtil.YMD);
                Date startTimeDate=format.parse(startTime);
                Date endTimeDate=format.parse(endTime);
                if (startTimeDate.getTime()>endTimeDate.getTime()) {
                    ToastUtil.showCustomMessage(context, "结束时间不能早于开始时间!");
                    result=false;
                }
                else{
                    result=true;
                }
            }
            else {
                ToastUtil.showCustomMessage(context, "开始时间,结束时间都不能为空!");
                result=false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }


    /**
     * @method name:getHourMinSecond
     * @des:通过秒数获取时分秒
     * @param :[second]
     * @return type:java.lang.String
     * @date 创建时间:2017/5/20
     * @author Chuck
     **/

    public static String getHourMinSecond(long second){

        long hour=second/3600;
        long min=(second-hour*3600)/60;
        long sec=second-hour*3600-min*60;

        return     hour>0?String.format("%1$02d:%2$02d:%3$02d", hour,min,sec):String.format("%1$02d:%2$02d", min,sec);
    }


    public static String getTimeNowYMDHMS(){
      return   new SimpleDateFormat(DateFormatUtil.YMDHMS).format(new Date());
    }

}
