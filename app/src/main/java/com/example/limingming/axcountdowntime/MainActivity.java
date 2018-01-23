package com.example.limingming.axcountdowntime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.limingming.axcountdowntime";
    public static final String EXTRA_BEFORE_TIME = "com.example.limingming.beforetime";
    TextView tv;
    Button btn;
    Button certainBtn;
    TextView timeLeftLab;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.editText);
        timeLeftLab = findViewById(R.id.label);

        SharedPreferences share = getPreferences(Context.MODE_PRIVATE);
        final String  str = share.getString(EXTRA_BEFORE_TIME,"");
        tv.setText(str);

        startTimer();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected  void  onStop() {
        super.onStop();
        timer.cancel();
        timerTask.cancel();
    }
// 点击方法
    public void sendMessage(View view)  {
        Toast.makeText(MainActivity.this,"😙",Toast.LENGTH_SHORT).show();

        String str = tv.getText().toString();
        SharedPreferences share = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(EXTRA_BEFORE_TIME, str);
        editor.apply();

        startTimer();


        // 跳转页面
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);


    }
    // 计算倒计时
    public void calculateTimes(String str) throws ParseException {
        long currentTime = System.currentTimeMillis() / 1000;

        String desTimeStr = str + " 00:00:00";
        long desTime = dateToStamp(desTimeStr) / 1000;
        String str1 = String.valueOf(currentTime);//stampToDate(currentTime);

        String resultStr;

        if (isvalidDate(str)) {
            if (desTime > currentTime) {
                long leftTime = desTime - currentTime;

                int day = (int) leftTime / (24 * 60 * 60);
                leftTime -= day * 24 * 60 * 60;
                int hour = (int) leftTime / (60 * 60);
                leftTime -= hour * 60 * 60;
                int min = (int) leftTime / 60;
                int sec = (int) leftTime - min * 60;

                resultStr = String.valueOf(day) + "天" + String.valueOf(hour) + "小时" + String.valueOf(min) + "分" + String.valueOf(sec) + "秒";
            }else {
                resultStr = "时间不正确";
                onStop();
            }
        }else {
            resultStr = "所输入日期不存在";
            onStop();
        }


        timeLeftLab.setText(resultStr);

    }
    // 将 时间格式 转为时间戳
    public long dateToStamp(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = simpleDateFormat.parse(time);
        long ts = date.getTime();
        return ts;
    }

    //  更新UI
    @SuppressLint("HandlerLeak")
    private Handler updateTime;

    {
        updateTime = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                SharedPreferences share = getPreferences(Context.MODE_PRIVATE);
                final String str = share.getString(EXTRA_BEFORE_TIME, "");
                try {
                    calculateTimes(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    // 开始timer
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void  run() {
                Message message = new Message();
                updateTime.sendMessage(message);

            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }
// 判断日期是否存在
    private boolean isvalidDate(String dateStr) {
        if (dateStr.length() != 8) {
            return false;
        }else {
            boolean result = false;
            String yearStr = dateStr.substring(0,4);
            String monthStr = dateStr.substring(4,6);
            String dayStr = dateStr.substring(6);
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            if (month == 2) {
                if (((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) && day <= 29) {
                    result = true;
                }else if (!((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) && day <= 28) {
                    result = true;
                }else {
                    result = false;
                }
            }else if (month <= 12 && month > 0) {

                boolean isThirtyOne = false;
                int[] thirtyones = {1,3,5,7,8,10,12};
                for (int i = 0; i < thirtyones.length; i++) {
                    if (thirtyones[i] == month) {
                        isThirtyOne = true;
                        break;
                    }
                }

                if (isThirtyOne) {
                    result = day <= 31;
                }else {
                    result = day <= 30;
                }

            }else {
                result = false;
            }
            return result;
        }

    }

}
