package fr.vigicorp.tlelievre.application_test;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.support.v4.view.ViewPager;
import android.os.Handler;
import android.util.Log;
import java.math.BigDecimal;
import android.view.animation.RotateAnimation;
import android.content.res.Resources;
import android.content.res.Configuration;
import java.util.Locale;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.content.IntentFilter;
import android.os.Process;
import android.view.KeyEvent;
import android.content.Context;
import android.widget.Toast;

import fr.vigicorp.tlelievre.application_test.BluetoothLeService;
import fr.vigicorp.tlelievre.application_test.FindActivity;
import fr.vigicorp.tlelievre.application_test.R;
import fr.vigicorp.tlelievre.application_test.SettingsActivity;
import fr.vigicorp.tlelievre.application_test.SharefMgr;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean DEBUG;
    private final int MSG_BEEING;
    private final int MSG_CONNECTING;
    private final int MSG_CONNECT_FAILURE;
    private final int MSG_CONNECT_SUCCESS;
    private final int MSG_INVALID_DEVICE;
    private final int MSG_MSSZ_FAILURE;
    private final int MSG_MSSZ_SUCCESS;
    private final int MSG_NOT_CONNECTED;
    private final int MSG_REFRESH;
    private final int MSG_SPJZ_FAILURE;
    private final int MSG_SPJZ_SUCCESS;
    private final int MSG_XSSZ_FAILURE;
    private final int MSG_XSSZ_SUCCESS;
    private int SCAN_DEVICES_RESULT;
    private String TAG;
    private Animation animation;
    private static ImageView battery_guide;
    private boolean connectState;
    private int coolstatus;
    private float curCurrent;
    private float current;
    private float currentMile;
    private float currentSpeed;
    private int currentTime;
    private float currentVoltage;
    private static TextView detail_aveSpeed;
    private static TextView detail_currentMile;
    private static TextView detail_maxSpeed;
    private static TextView detail_time;
    private static TextView detail_totalMile;
    private long exitTime;
    private static TextView home_battery;
    private static TextView home_canrun;
    private static TextView home_coolstatus;
    private static TextView home_speed;
    private static TextView home_temp;
    private boolean isInvalidDevice;
    private boolean isRootUser;
    private float lastspeed;
    public static BluetoothLeService mBluetoothLeService;
    private String mDeviceAddrString;
    private String mDeviceNameString;
    BroadcastReceiver mReceiver;
    private MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private final ServiceConnection mServiceConnection;
    private ViewPager mViewPager;
    private static TextView mainTip;
    private float maxSpeed;
    private Handler myHandler;
    private float speed;
    private static ImageView speed_guide;
    public static int temperatureValue;
    private float totalMile;
    private static TextView vehicoName;
    private float voltage;

    public MainActivity() {
        TAG = MainActivity.class.getName();
        DEBUG = true;
        connectState = false;
        isRootUser = false;
        voltage = 0.0f;
        currentVoltage = 0.0f;
        current = 0.0f;
        curCurrent = 0.0f;
        speed = 0.0f;
        lastspeed = 0.0f;
        currentSpeed = 0.0f;
        totalMile = 0.0f;
        currentMile = 0.0f;
        maxSpeed = 0.0f;
        coolstatus = 0x0;
        isInvalidDevice = true;
        mDeviceAddrString = "";
        mDeviceNameString = "";
        SCAN_DEVICES_RESULT = 0x1;
        exitTime = 0x0;
        MSG_REFRESH = 0x0;
        MSG_CONNECTING = 0x1;
        MSG_CONNECT_SUCCESS = 0x2;
        MSG_CONNECT_FAILURE = 0x3;
        MSG_XSSZ_SUCCESS = 0x4;
        MSG_XSSZ_FAILURE = 0x5;
        MSG_MSSZ_SUCCESS = 0x6;
        MSG_MSSZ_FAILURE = 0x7;
        MSG_SPJZ_SUCCESS = 0x8;
        MSG_SPJZ_FAILURE = 0x9;
        MSG_NOT_CONNECTED = 0xa;
        MSG_INVALID_DEVICE = 0xb;
        MSG_BEEING = 0xc;
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("bluetooth_connection_state")) {
                    connectState = intent.getBooleanExtra("bluetooth_connection_state", false);
                    if(connectState) {
                        myHandler.sendEmptyMessage(0x2);
                        SharefMgr.setLastAddr(getApplicationContext(), mDeviceAddrString);
                        return;
                    }
                    myHandler.sendEmptyMessage(0x3);
                    return;
                }
                if(action.equals("bluetooth_data")) {
                    byte[] data = intent.getByteArrayExtra("data");
                }
            }
        };
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                MainActivity.mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
                if(!MainActivity.mBluetoothLeService.initialize()) {
                    Log.e("DeviceControlActivity", "Unable to initialize Bluetooth");
                    finish();
                    return;
                }
                String lastAddr = SharefMgr.getLastAddr(getApplicationContext());
                if(!TextUtils.isEmpty(lastAddr)) {
                    MainActivity.mBluetoothLeService.connect(lastAddr);
                    mDeviceAddrString = lastAddr;
                    myHandler.sendEmptyMessage(0x1);
                    myHandler.postDelayed(new Runnable() {

                        public void run() {
                            if(!connectState) {
                                myHandler.sendEmptyMessage(0x3);
                            }
                        }
                    }, 0xfa0);
                    return;
                }
                myHandler.sendEmptyMessage(0x3);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                MainActivity.mBluetoothLeService = null;
            }
        };
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent();
                switch(msg.what) {
                    case 1:
                    {
                        mainTip.setVisibility(View.VISIBLE);
                        mainTip.setText(getString(R.string.connecting));
                        return;
                    }
                    case 3:
                    {
                        if(connectState) {
                            if(MainActivity.mBluetoothLeService != null) {
                                MainActivity.mBluetoothLeService.disconnect();
                            }
                        }
                        intent.setClass(getApplicationContext(), FindActivity.class);
                        intent.addFlags(Intent.FILL_IN_DATA);
                        intent.addFlags(Intent.FILL_IN_CATEGORIES);
                        startActivityForResult(intent, SCAN_DEVICES_RESULT);
                        return;
                    }
                    case 2:
                    {
                        mainTip.setVisibility(View.GONE);
                        byte[] data = new byte[0x14];
                        data[0x0] = -0x56;
                        data[0x1] = 0x55;
                        data[0x10] = -0x65;
                        data[0x11] = 0x14;
                        data[0x12] = 0x5a;
                        data[0x13] = 0x5a;
                        MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                        return;
                    }
                    case 0:
                    {
                        try {
                            return;
                        } catch(Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        };
    }
    public static int currentMode = -0x1;
    public static int mVersion = -0x1;
    public static String mUnicycleType = "";
    public static String mUnicycleSN = "";

    private void doHomeGuide() {
        if(speed > 0x4270) {
            speed = 0.0f;
        }
        if(speed < 0x0) {
            speed = 0.0f;
        }
        animation = new RotateAnimation(((lastspeed * 270.0f) / 60.0f), ((speed * 270.0f) / 60.0f), 0x1, 0.5f, 0x1, 0.5f);
        animation.setDuration(0x32);
        animation.setFillAfter(true);
        speed_guide.startAnimation(animation);
        lastspeed = speed;
        BigDecimal bd = new BigDecimal((double)speed);
        bd = bd.setScale(0x2, 0x4);
        home_speed.setText(bd.toString());
        if(voltage > 75.0f) {
            voltage = 75.0f;
        } else if(voltage < 45.0f) {
            voltage = 45.0f;
        }
        bd = new BigDecimal((double)voltage);
        bd = bd.setScale(0x1, 0x4);
        int percent;
        if(voltage < 50.0f) {
            percent = 0x0;
        } else if(voltage >= 66.0f) {
            percent = 0x64;
        } else {
            percent = (int)(((voltage - 50.0f) * 100.0f) / 16.0f);
        }
        home_battery.setText(percent + "%");
        if(percent >= 0x64) {
            battery_guide.setImageResource(R.mipmap.home_battery_100);
        } else if(percent >= 0x5a) {
            battery_guide.setImageResource(R.mipmap.home_battery_90);
        } else if(percent >= 0x50) {
            battery_guide.setImageResource(R.mipmap.home_battery_80);
        } else if(percent >= 0x46) {
            battery_guide.setImageResource(R.mipmap.home_battery_70);
        } else if(percent >= 0x3c) {
            battery_guide.setImageResource(R.mipmap.home_battery_60);
        } else if(percent >= 0x32) {
            battery_guide.setImageResource(R.mipmap.home_battery_50);
        } else if(percent >= 0x28) {
            battery_guide.setImageResource(R.mipmap.home_battery_40);
        } else if(percent >= 0x1e) {
            battery_guide.setImageResource(R.mipmap.home_battery_30);
        } else if(percent >= 0x14) {
            battery_guide.setImageResource(R.mipmap.home_battery_20);
        } else if(percent >= 0xa) {
            battery_guide.setImageResource(R.mipmap.home_battery_10);
        } else {
            battery_guide.setImageResource(R.mipmap.home_battery_10);
        }
        if(temperatureValue > 0x64) {
            temperatureValue = 0x64;
        } else if(temperatureValue < 0) {
            temperatureValue = 0x0;
        }
        home_temp.setText(temperatureValue + "\u2103");
        if(coolstatus == 0x1) {
            home_coolstatus.setVisibility(View.VISIBLE);
        } else {
            home_coolstatus.setVisibility(View.GONE);
        }
        int totalmile = 0x3c;
        if(mUnicycleSN.contains("KS14B1")) {
            totalmile = 0xf;
        } else if(mUnicycleSN.contains("KS14C1")) {
            totalmile = 0x1e;
        } else if(mUnicycleSN.contains("KS14C2")) {
            totalmile = 0x2d;
        } else if(mUnicycleSN.contains("KS14C3")) {
            totalmile = 0x3c;
        } else if(mUnicycleSN.contains("KS16D1")) {
            totalmile = 0x1e;
        } else if(mUnicycleSN.contains("KS16D2")) {
            totalmile = 0x2d;
        } else if(mUnicycleSN.contains("KS16D3")) {
            totalmile = 0x3c;
        } else if(mUnicycleSN.contains("KS16D4")) {
            totalmile = 0x4a;
        } else if(mUnicycleSN.contains("KS18A1")) {
            totalmile = 0x2d;
        } else if(mUnicycleSN.contains("KS18A2")) {
            totalmile = 0x3c;
        } else if(mUnicycleSN.contains("KS18A3")) {
            totalmile = 0x78;
        } else if(mUnicycleSN.contains("KS18A4")) {
            totalmile = 0x2d;
        } else if(mUnicycleSN.contains("KS18A5")) {
            totalmile = 0x3c;
        } else if(mUnicycleSN.contains("KS18A6")) {
            totalmile = 0x78;
        }
        bd = new BigDecimal(((percent * totalmile) / 0x64));
        bd = bd.setScale(0x0, 0x4);
        home_canrun.setText(bd.toString());
    }

    private void doDetailGuide() {
        BigDecimal bd = new BigDecimal((double)totalMile);
        bd = bd.setScale(0x0, 0x4);
        detail_totalMile.setText(bd.toString());
        bd = new BigDecimal((double)currentMile);
        bd = bd.setScale(0x2, 0x4);
        detail_currentMile.setText(bd.toString());
        bd = new BigDecimal((double)maxSpeed);
        bd = bd.setScale(0x2, 0x4);
        detail_maxSpeed.setText(bd.toString());
        int hour = currentTime / 0xe10;
        if(hour < 0) {
            hour = 0x0;
        } else if(hour > 0x63) {
            hour = 0x63;
        }
        String time = hour < 0xa ? "0" + hour : "" + hour;
        int minute = currentTime / 0x3c;
        if(minute < 0) {
            minute = 0x0;
        } else if(minute > 0x3b) {
            minute = minute % 0x3c;
        }
        time = time + ":".concat(minute < 0xa ? "0" + minute : "" + minute);
        detail_time.setText(time);
        float avespeed = (float)(((double)(currentMile * 1000.0f) * 3.6) / (double)currentTime);
        bd = new BigDecimal((double)avespeed);
        bd = bd.setScale(0x2, 0x4);
        detail_aveSpeed.setText(bd.toString());
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPagerAdapter = new MainActivity.SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(0x1);
        Intent service = new Intent(this, BluetoothLeService.class);
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("bluetooth_connection_state");
        intentFilter.addAction("bluetooth_data");
        registerReceiver(mReceiver, intentFilter);
    }

    protected void onResume() {
        super.onResume();
        isRootUser = SharefMgr.getRootUser(this).contains(mDeviceAddrString);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
        Process.killProcess(Process.myPid());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == 0x4) && (event.getAction() == 0)) {
            if(mViewPager.getCurrentItem() != 0x1) {
                mViewPager.setCurrentItem(0x1);
                return true;
            }
            if((System.currentTimeMillis() - exitTime) > 0x7d0) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.exit), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return true;
            }
            finish();
            System.exit(0x0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SCAN_DEVICES_RESULT) {
            if(resultCode == 0x1) {
                mDeviceAddrString = data.getStringExtra("addr");
                vehicoName.setText(mDeviceNameString);
                mBluetoothLeService.scanLeDevice(false);
                mBluetoothLeService.connect(mDeviceAddrString);
                myHandler.sendEmptyMessage(0x1);
                return;
            }
            finish();
        }
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        byte[] data = new byte[0x14];
        switch(v.getId()) {
            case R.id.home_bee:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x10] = -0x78;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                return;
            }
            case R.id.spjz:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.xssz:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                intent.putExtra("id", v.getId());
                intent.putExtra("isRootUser", isRootUser);
                startActivity(intent);
                return;
            }
            case R.id.xsjm:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                if(isRootUser) {
                    intent.putExtra("id", 0x7f0c008f);
                    intent.putExtra("isRootUser", isRootUser);
                    startActivity(intent);
                    return;
                }
                intent.putExtra("id", v.getId());
                intent.putExtra("mac_addr", mDeviceAddrString);
                startActivity(intent);
                return;
            }
            case R.id.gjsj:
            {
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.dgkz | R.id.cdkz | R.id.qxsz | R.id.yykz:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.jbxx:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.dcxx:
            {
                if(!connectState) {
                    myHandler.sendEmptyMessage(0xa);
                    return;
                }
                if(isInvalidDevice) {
                    myHandler.sendEmptyMessage(0xb);
                    return;
                }
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.fwgw | R.id.bbjc | R.id.xgn | R.id.wtfk | R.id.tlq |R.id.zxdh | R.id.sms | R.id.appwtfk:
            {
                intent.putExtra("id", v.getId());
                startActivity(intent);
                return;
            }
            case R.id.detail_btnright:
            {
                mViewPager.setCurrentItem(1);
                return;
            }
            case R.id.home_btnright:
            {
                mViewPager.setCurrentItem(2);
                return;
            }
            case R.id.home_btnleft:
            {
                mViewPager.setCurrentItem(0);
                return;
            }
            case R.id.menu_btnleft:
            {
                mViewPager.setCurrentItem(1);
                break;
            }
        }
    }

    private byte[] intToByteArray(int speed) {
        byte[] data = new byte[0x2];
        data[0x0] = (byte)(speed & 0xff);
        data[0x1] = (byte)((speed >> 0x10) & 0xff);
        return data;
    }

    private int byteArrayInt2(byte low, byte high) {
        int result = 0x0;
        result = (low & 0xff) + ((high & 0xff) * 0x100);
        return result;
    }

    private long byteArrayInt4(byte value1, byte value2, byte value3, byte value4) {
        long result = 0x0;
        int a = value1 & 0xff;
        result |= (long)(a << 0x10);
        a = value2 & 0xff;
        result |= (long)(a << 0x18);
        a = value3 & 0xff;
        result |= (long)a;
        a = value4 & 0xff;
        result |= (long)(a << 0x8);
        return result;
    }

    private char byteToChar(byte value) {
        if((value < 0) || (value > 0xff)) {
            return 0x20;
        }
        int result = value & 0xff;
        char s = (char)result;
        return s;
    }

    private void LOGI(String log) {
        if(DEBUG) {
            Log.i(TAG, log);
        }
    }

    private void analysiResponse(byte[] data) {
        if(data.length < 20) {
            return;
        }
        int a1 = data[0x0] & 0xff;
        int a2 = data[0x1] & 0xff;
        LOGI("a1=" + a1 + ",a2=" + a2);
        if((a1 == 0xaa) && (a2 == 0x55)) {
            if((data[0x10] & 0xff) == 0xa9) {
                voltage = ((float)byteArrayInt2(data[0x2], data[0x3]) / 100.0f);
                speed = ((float)byteArrayInt2(data[0x4], data[0x5]) / 100.0f);
                LOGI("voltage =" + byteArrayInt4(data[0x6], data[0x7], data[0x8], data[0x9]));
                totalMile = ((float)byteArrayInt4(data[0x6], data[0x7], data[0x8], data[0x9]) / 1000.0f);
                current = ((float)byteArrayInt2(data[0xa], data[0xb]) / 100.0f);
                temperatureValue = (byteArrayInt2(data[0xc], data[0xd]) / 0x64);
                currentMode = -0x1;
                if((data[0xf] & 0xff) == 0xe0) {
                    currentMode = data[0xe];
                }
                LOGI("voltage = " + voltage + ", speed = " + speed + ", totalMile = " + totalMile);
                LOGI("electricity = " + current + ", temprature = " + temperatureValue + ", mode = " + currentMode);
                myHandler.sendEmptyMessage(0x0);
                return;
            }
            if((data[0x10] & 0xff) == 0xb9) {
                currentMile = ((float)byteArrayInt4(data[0x2], data[0x3], data[0x4], data[0x5]) / 1000.0f);
                currentTime = byteArrayInt2(data[0x6], data[0x7]);
                maxSpeed = ((float)byteArrayInt2(data[0x8], data[0x9]) / 100.0f);
                LOGI("\u672c\u6b21\u5f00\u673a currentMile = " + currentMile + ", currentTime(" + "s) = " + currentTime + ", curentMostSpeed = " + maxSpeed);
                if(coolstatus != data[0xc]) {
                    coolstatus = data[0xc];
                }
                myHandler.sendEmptyMessage(0x0);
                return;
            }
            if((data[0x10] & 0xff) == 0xbb) {
                int end = 0x0;
                int check = byteArrayInt2(data[0x12], data[0x13]) & 0xffff;
                int checksum = 0x0;
                for(int i = 0x0; i < 0xe; i = i + 0x1) {
                    if(data[(i + 0x2)] != 0) {
                        end = end + 0x1;
                        checksum += data[(i + 0x2)];
                    }
                }
                checksum = (0xffff - checksum) & 0xffff;
                mDeviceNameString = new String(data, 0x2, end);
                mDeviceNameString.trim();
                if(mDeviceNameString.substring(0x0, 0x2).equals("KS")) {
                    isInvalidDevice = false;
                } else {
                    isInvalidDevice = true;
                }
                mUnicycleType = "";
                String[] ss = mDeviceNameString.split("-");
                for(int i = 0x0; i < (ss.length - 0x1); i = i + 0x1) {
                    if(i != 0) {
                        mUnicycleType = mUnicycleType + "-";
                    }
                    mUnicycleType = mUnicycleType + ss[i];
                }
                try {
                    mVersion = Integer.parseInt(ss[(ss.length - 0x1)]);
                } catch(Exception localException1) {
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                if(mVersion < 0x75) {
                    data[0x10] = 0x63;
                } else if(checksum == check) {
                    data[0x10] = 0x63;
                } else {
                    data[0x10] = -0x65;
                }
                mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                return;
            }
            if((data[0x10] & 0xff) == 0xb3) {
                byte[] sndata = new byte[0x12];
                for(int i = 0x0; i < 0xe; i = i + 0x1) {
                    sndata[i] = data[(i + 0x2)];
                }
                for(int i = 0xe; i < 0x11; i = i + 0x1) {
                    sndata[i] = data[(i + 0x3)];
                }
                sndata[0x11] = 0x0;
                mUnicycleSN = new String(sndata);
                vehicoName.setText(new String(sndata));
                if(isZh()) {
                    data[0x2] = 0x1;
                } else {
                    data[0x2] = 0x0;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x10] = 0x69;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                mBluetoothLeService.writeBluetoothGattCharacteristic(data);
            }
        }
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.endsWith("zh")) {
            return true;
        }
        return false;
    }

    public class SectionsPagerAdapter
            extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(MainActivity paramMainActivity, FragmentManager paramFragmentManager)
        {
            super(paramFragmentManager);
        }

        public int getCount()
        {
            return 3;
        }

        public Fragment getItem(int paramInt)
        {
            if (paramInt == 0) {
                return MainActivity.DetailFragment.newInstance(paramInt + 1);
            }
            if (paramInt == 1) {
                return MainActivity.HomeFragment.newInstance(paramInt + 1);
            }
            return MainActivity.SettingFragment.newInstance(paramInt + 1);
        }

        public CharSequence getPageTitle(int paramInt)
        {
            switch (paramInt)
            {
                default:
                    return null;
                case 0:
                    return "Detail";
                case 1:
                    return "Home";
                case 2:
                    return "Settings";
            }
        }
    }

    public static class DetailFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static DetailFragment newInstance(int paramInt)
        {
            DetailFragment localDetailFragment = new DetailFragment();
            Bundle localBundle = new Bundle();
            localBundle.putInt("section_number", paramInt);
            localDetailFragment.setArguments(localBundle);
            return localDetailFragment;
        }

        public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
        {
            View localView = paramLayoutInflater.inflate(R.layout.fragment_detail, paramViewGroup, false);
            detail_currentMile = ((TextView)localView.findViewById(R.id.current_mile));
            detail_maxSpeed = ((TextView)localView.findViewById(R.id.max_speed));
            detail_aveSpeed = ((TextView)localView.findViewById(R.id.ave_speed));
            detail_totalMile = ((TextView)localView.findViewById(R.id.total_mile));
            detail_time = ((TextView)localView.findViewById(R.id.time));
            return localView;
        }
    }

    public static class HomeFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static HomeFragment newInstance(int paramInt)
        {
            HomeFragment localHomeFragment = new HomeFragment();
            Bundle localBundle = new Bundle();
            localBundle.putInt("section_number", paramInt);
            localHomeFragment.setArguments(localBundle);
            return localHomeFragment;
        }

        public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
        {
            View localView = paramLayoutInflater.inflate(R.layout.fragment_home, paramViewGroup, false);
            vehicoName = ((TextView)localView.findViewById(R.id.vehico_name));
            mainTip = ((TextView)localView.findViewById(R.id.maintip));
            speed_guide = ((ImageView)localView.findViewById(R.id.speed_guide));
            home_speed = ((TextView)localView.findViewById(R.id.home_speed));
            home_battery = ((TextView)localView.findViewById(R.id.home_battery));
            battery_guide = ((ImageView)localView.findViewById(R.id.battery_guide));
            home_temp = ((TextView)localView.findViewById(R.id.home_temp));
            home_canrun = ((TextView)localView.findViewById(R.id.home_canrun));
            home_coolstatus = ((TextView)localView.findViewById(R.id.home_coolstatus));
            return localView;
        }
    }

    public static class SettingFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static SettingFragment newInstance(int paramInt)
        {
            SettingFragment localSettingFragment = new SettingFragment();
            Bundle localBundle = new Bundle();
            localBundle.putInt("section_number", paramInt);
            localSettingFragment.setArguments(localBundle);
            return localSettingFragment;
        }

        public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
        {
            return paramLayoutInflater.inflate(R.layout.fragment_setting, paramViewGroup, false);
        }
    }




}
