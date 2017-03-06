package fr.vigicorp.tlelievre.application_test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final int ACK = 6;
    public static final int CA = 24;
    public static final byte CMD_MARK = -1;
    public static final int CRC16 = 67;
    public static final int EOT = 4;
    public static final String KINGSONG_URL = "http://apkdomain.duapp.com/kingsong/versions.json";
    public static final int NAK = 21;
    public static final int PACKET_HEADER = 2;
    public static final int PACKET_OVERHEAD = 4;
    public static final int PACKET_SIZE = 16;
    public static final int PACKET_TRAILER = 2;
    public static final int SOH = 1;
    public static final String VERSIONURL_STRING_KEY = "versionurl_string_key";
    private static final String TAG = SettingActivity.class.getSimpleName();
    public short mBlkNumber;
    public short mBlkNumber_temp;
    public String mFileName;
    public String mFileName_temp;
    public int mFileSize;
    public int mFileSize_temp;
    public byte[] mFile_buf;
    public byte[] mFile_buf_temp;
    public Handler mHandler;
    public boolean mIsZh;
    public String mUnicycleType;
    public int mYRetry;
    public SettingActivity.eYM_STAT state;
    int currentMode;
    BroadcastReceiver mReceiver;
    int speedLevel1;
    int speedLevel2;
    int speedLevel3;
    int waneSpeed;
    private boolean DEBUG;
    private int Toggle_Light_Auto;
    private int Toggle_Light_OFF;
    private int Toggle_Light_ON;
    private TextView alarm_speed1;
    private TextView alarm_speed2;
    private TextView alarm_speed3;
    private TextView alarm_speed4;
    private EditText authEditText;
    private int bluemode;
    private int colorledmode;
    private int coolstatus;
    private int currentSettingId;
    private TextView dcxxdl;
    private TextView dcxxdy;
    private TextView dcxxgl;
    private JSONArray devicesFromNet;
    private int discolorled;
    private boolean isBinder;
    private boolean isRootUser;
    private TextView jbxxctwd;
    private TextView jbxxgjbb;
    private TextView jbxxqxms;
    private TextView jbxxsn;
    private int lightmode;
    private SettingActivity.MyListAdapter mAdapter;
    private Button mBTNDownloadHW;
    private Button mBTNUpdateHW;
    private ISoftwareUpdateServiceCallBack mCallBack;
    private TextView mDesTextView;
    private String mDeviceAddrString;
    private ArrayList<SettingActivity.UnicycleDeviceDetail> mDevices;
    private TextView mDiscoverTextView;
    private ServiceConnection mDownServiceConnection;
    private ProgressBar mHWProgressBar;
    private int mHardVersion;
    private String mHardwareFilePath;
    private TextView mHardwareTextView;
    private RequestQueue mRequestQueue;
    private ListView mSelectListView;
    private TextView mSelectTextView;
    private int mSeleletype;
    private int mVersion;
    private Handler mYM_Handler;
    private Boolean mYmodem;
    private byte[] mYmodemReceive;
    private Button menu18yykz;
    private Button menu7qx;
    private Button menu7wj;
    private Button menu7xx;
    private SeekBar seebar_cdlight;
    private SeekBar seebar_cdlight2;
    private SeekBar seebar_light;
    private SeekBar seebar_light2;
    private SeekBar seebar_speed1;
    private SeekBar seebar_speed2;
    private SeekBar seebar_speed3;
    private SeekBar seebar_speed4;
    private int[] serialNum;
    private String[] serialString;
    private ISoftwareUpdateService serviceInstance;
    private EditText snEditText;
    private int voicemodeoff;

    public SettingActivity() {
        speedLevel1 = 0x0;
        speedLevel2 = 0x0;
        speedLevel3 = 0x0;
        waneSpeed = 0x0;
        isRootUser = false;
        serialNum = new int[0x6];
        serialString = new String[0x6];
        state = SettingActivity.eYM_STAT.eYM_RECE_HEAD_PACKET;
        mBlkNumber = 0x1;
        mYRetry = 0x0;
        mYmodem = Boolean.valueOf(false);
        mYmodemReceive = new byte[0x400];
        mDevices = new ArrayList();
        mSeleletype = -0x1;
        mHandler = new SettingActivity.CmdHandler(this);
        mVersion = -0x1;
        mHardVersion = 0x0;
        mUnicycleType = "";
        DEBUG = true;
        Toggle_Light_ON = 0x12;
        Toggle_Light_OFF = 0x13;
        Toggle_Light_Auto = 0x14;
        bluemode = 0x0;
        lightmode = 0x0;
        voicemodeoff = 0x0;
        coolstatus = 0x0;
        currentMode = -0x1;
        discolorled = 0x0;
        colorledmode = 0x0;
        isBinder = false;
        mCallBack = new ISoftwareUpdateServiceCallBack() {

            public void completedUI(String string) {
                SharefMgr.setHardwarePath(getApplicationContext(), string);
                mHandler.postDelayed(new Runnable() {

                    public void run() {
                        mBTNUpdateHW.setVisibility(View.VISIBLE);
                        mHWProgressBar.setVisibility(View.INVISIBLE);
                    }
                }, 0x0);
            }

            public void preparationUI() {
            }

            public void updateProgressUI(int paramInt) {
                mHWProgressBar.setProgress(paramInt);
            }
        };
        mDownServiceConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder service) {
                isBinder = true;
                serviceInstance = (ISoftwareUpdateService)service;
                if (serviceInstance != null) {
                    serviceInstance.HideNoitfication();
                }
                serviceInstance.RegisterCallBack(mCallBack);
            }

            public void onServiceDisconnected(ComponentName name) {
                serviceInstance.UnRegisterCallBack(mCallBack);
            }
        };
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("bluetooth_data")) {
                    if (mYmodem.booleanValue()) {
                        byte[] data = intent.getByteArrayExtra("data");
                        StringBuilder stringBuilder = new StringBuilder(20);

                        for (int i = 0x0; i < data.length; i = i + 0x1) {
                            stringBuilder.append(String.format("%02X ", Byte.valueOf(data[i])));
                        }
                        System.arraycopy(data, 0x0, mYmodemReceive, 0x0, data.length);
                        mYM_Handler.sendEmptyMessage(-1);
                        return;
                    }
                }
            }
        };
        mYM_Handler = new Handler() {

            public void handleMessage(Message paramMessage)
            {
                if (SettingActivity.access$700(this.this$0) == null) {
                    return;
                }
                switch (SettingActivity.9.$SwitchMap$com$kingsong$unicycle$SettingActivity$eYM_STAT[this.this$0.state.ordinal()])
                {
                }
                for (;;)
                {
                    super.handleMessage(paramMessage);
                    return;
                    if (SettingActivity.access$700(this.this$0)[0] == 67) {
                        if (this.this$0.mFile_buf == null)
                        {
                            try
                            {
                                this.this$0.mFile_buf = SettingActivity.getBytesFromFile(new File(SettingActivity.access$2100(this.this$0)));
                                this.this$0.mFileName = new File(SettingActivity.access$2100(this.this$0)).getName();
                                this.this$0.mFileSize = ((int)new File(SettingActivity.access$2100(this.this$0)).length());
                                SettingActivity.access$100(this.this$0).setMax(this.this$0.mFileSize);
                            }
                            catch (IOException localIOException)
                            {
                                this.this$0.mFile_buf = null;
                                localIOException.printStackTrace();
                            }
                        }
                        else
                        {
                            this.this$0.mFile_buf_temp = new byte[this.this$0.mFile_buf.length];
                            System.arraycopy(this.this$0.mFile_buf, 0, this.this$0.mFile_buf_temp, 0, this.this$0.mFile_buf_temp.length);
                            this.this$0.mFileName_temp = this.this$0.mFileName;
                            this.this$0.mFileSize_temp = this.this$0.mFileSize;
                            this.this$0.mBlkNumber_temp = this.this$0.mBlkNumber;
                            SettingActivity.access$100(this.this$0).setProgress(0);
                            this.this$0.mFileName_temp = "pwm.bin";
                            this.this$0.Ymodem_Transmit_HEAD(this.this$0.mFileName_temp, this.this$0.mFileSize_temp);
                            this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_DATA_START;
                            continue;
                            if (SettingActivity.access$700(this.this$0)[0] != 67) {
                                if ((SettingActivity.access$700(this.this$0)[0] == 6) && (SettingActivity.access$700(this.this$0)[1] == 67))
                                {
                                    this.this$0.Ymodem_Transmit_DATA(this.this$0.mFile_buf_temp, this.this$0.mBlkNumber_temp, this.this$0.mFileSize_temp);
                                    this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_DATA;
                                }
                                else if ((SettingActivity.access$700(this.this$0)[0] == 6) && (SettingActivity.access$700(this.this$0)[1] == 24) && (SettingActivity.access$700(this.this$0)[2] == 24))
                                {
                                    this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_HEAD_PACKET;
                                    continue;
                                    if ((SettingActivity.access$700(this.this$0)[0] == 6) && (SettingActivity.access$700(this.this$0)[1] == 24) && (SettingActivity.access$700(this.this$0)[2] == 24))
                                    {
                                        this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_HEAD_PACKET;
                                    }
                                    else
                                    {
                                        if (SettingActivity.access$700(this.this$0)[0] == 6)
                                        {
                                            if (this.this$0.mFileSize_temp > 16)
                                            {
                                                this.this$0.mFile_buf_temp = SettingActivity.subBytes(this.this$0.mFile_buf_temp, 16, -16 + this.this$0.mFile_buf_temp.length);
                                                SettingActivity localSettingActivity2 = this.this$0;
                                                localSettingActivity2.mFileSize_temp = (-16 + localSettingActivity2.mFileSize_temp);
                                                SettingActivity localSettingActivity3 = this.this$0;
                                                localSettingActivity3.mBlkNumber_temp = ((short)(1 + localSettingActivity3.mBlkNumber_temp));
                                                SettingActivity.access$100(this.this$0).setProgress(this.this$0.mFileSize - this.this$0.mFileSize_temp);
                                                this.this$0.Ymodem_Transmit_DATA(this.this$0.mFile_buf_temp, this.this$0.mBlkNumber_temp, this.this$0.mFileSize_temp);
                                            }
                                            for (;;)
                                            {
                                                this.this$0.mYRetry = 0;
                                                break;
                                                this.this$0.Send_Byte((byte)4);
                                                this.this$0.state = SettingActivity.eYM_STAT.eYM_END;
                                            }
                                        }
                                        if (SettingActivity.access$700(this.this$0)[0] == 21)
                                        {
                                            this.this$0.Ymodem_Transmit_DATA(this.this$0.mFile_buf_temp, this.this$0.mBlkNumber_temp, this.this$0.mFileSize_temp);
                                            SettingActivity localSettingActivity1 = this.this$0;
                                            int i = localSettingActivity1.mYRetry;
                                            localSettingActivity1.mYRetry = (i + 1);
                                            if (i > 5)
                                            {
                                                this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_HEAD_PACKET;
                                                this.this$0.mYRetry = 0;
                                            }
                                        }
                                        else if (SettingActivity.access$700(this.this$0)[0] == 67)
                                        {
                                            continue;
                                            if (SettingActivity.access$700(this.this$0)[0] == 21)
                                            {
                                                this.this$0.Send_Byte((byte)4);
                                            }
                                            else if (SettingActivity.access$700(this.this$0)[0] == 67)
                                            {
                                                this.this$0.Ymodem_Transmit_END();
                                                this.this$0.state = SettingActivity.eYM_STAT.eYM_RECE_HEAD_PACKET;
                                                SettingActivity.access$502(this.this$0, Boolean.valueOf(false));
                                                SettingActivity.access$100(this.this$0).setVisibility(8);
                                            }
                                            else if (SettingActivity.access$700(this.this$0)[0] != 6) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        };
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > 0x7fffffff) {
            is.close();
            throw new IOException("File is to large " + file.getName());
        }
        byte[] arrayOfByte = new byte[(int)length];
        int i = 0;
        while (i < arrayOfByte.length)
        {
            int j = is.read(arrayOfByte, i, arrayOfByte.length - i);
            if (j < 0) {
                break;
            }
            i += j;
        }
        if (i < arrayOfByte.length)
        {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return arrayOfByte;
    }

    public static final short evalCRC16(byte[] data) {
        int crc = 0x0;
        for (int i = 0x0; i < data.length; i++) {
            crc ^= (data[i] << 8);
            for (int j = 0; j < 8; j++) {
                if (0x8000 != 0) {
                    continue;
                }
                crc = crc << 0x1;
                continue;
            }
        }
        return (short) 0xffff;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) {
            bs[(i - begin)] = src[i];
        }

        return bs;
    }

    private void LOGI(String log) {
        if (DEBUG) {
            Log.i(TAG, log);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        display();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("bluetooth_data");
        registerReceiver(mReceiver, intentFilter);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (isBinder) {
            unbindService(mDownServiceConnection);
        }
    }

    void display() {
        TextView title = (TextView) findViewById(R.id.submenutitle);
        Intent intent = getIntent();
        currentSettingId = intent.getIntExtra("id", 0x0);
        byte[] data = new byte[0x14];
        switch (currentSettingId) {
            case 2131493006: {
                title.setText(getString(R.string.spjz));
                findViewById(R.id.layout_submenuspjz).setVisibility(View.VISIBLE);
                break;
            }
            case 2131493007: {
                title.setText(getString(R.string.xssz));
                findViewById(R.id.layout_submenuxssz).setVisibility(View.VISIBLE);
                alarm_speed1 = (TextView) findViewById(R.id.submenutxtlevel1);
                alarm_speed2 = (TextView) findViewById(R.id.submenutxtlevel2);
                alarm_speed3 = (TextView) findViewById(R.id.submenutxtlevel3);
                alarm_speed4 = (TextView) findViewById(R.id.submenutxtrockerspeed);
                seebar_speed1 = (SeekBar) findViewById(R.id.submenuseekbarlevel1);
                seebar_speed2 = (SeekBar) findViewById(R.id.submenuseekbarlevel2);
                seebar_speed3 = (SeekBar) findViewById(R.id.submenuseekbarlevel3);
                seebar_speed4 = (SeekBar) findViewById(R.id.submenuseekbarrockerspeed);
                seebar_speed1.setOnSeekBarChangeListener(this);
                seebar_speed2.setOnSeekBarChangeListener(this);
                seebar_speed3.setOnSeekBarChangeListener(this);
                seebar_speed4.setOnSeekBarChangeListener(this);
                isRootUser = getIntent().getBooleanExtra("isRootUser", false);
                readAlarmParas();
                break;
            }
            case 2131493008: {
                title.setText(getString(R.string.xsjm));
                findViewById(R.id.layout_submenuxsjm).setVisibility(View.VISIBLE);
                snEditText = (EditText) findViewById(R.id.isn);
                authEditText = (EditText) findViewById(R.id.auth_code);
                mDeviceAddrString = getIntent().getStringExtra("mac_addr");
                getSerialNum(mDeviceAddrString, serialNum, serialString);
                String string = serialString[0x0];
                for (int i = 0x1; i < serialString.length; i = i + 0x1) {
                    string = string + "-" + serialString[i];
                }
                snEditText.setText(string);
                break;
            }
            case 2131493009: {
                title.setText(getString(R.string.gjsj));
                findViewById(R.id.layout_submenugjsj).setVisibility(View.VISIBLE);
                mHardwareTextView = (TextView) findViewById(R.id.hardwareversion);
                mHardwareTextView.setText(getResources().getString(R.string.newsoftversion) + "--");
                mBTNUpdateHW = (Button) findViewById(R.id.update);
                mBTNDownloadHW = (Button) findViewById(R.id.download);
                mHWProgressBar = (ProgressBar) findViewById(R.id.hwprogress);
                mDiscoverTextView = (TextView) findViewById(R.id.updatediscover);
                mDiscoverTextView.setText(getResources().getString(R.string.currentsoftversion) + "--");
                mSelectTextView = (TextView) findViewById(R.id.selecttypeview);
                mSelectListView = (ListView) findViewById(R.id.selecttypelist);
                mDesTextView = (TextView) findViewById(R.id.description);
                mDesTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
                mAdapter = new SettingActivity.MyListAdapter(this, mDevices);
                mSelectListView.setAdapter(mAdapter);
                mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (view.getTag() instanceof SettingActivity.ViewHolder) {
                            SettingActivity.ViewHolder viewHolder = (SettingActivity.ViewHolder) view.getTag();
                            mSelectTextView.setText(getResources().getString(R.string.selectunicycle) + ":" + viewHolder.deviceName.getText());
                            mSeleletype = position;
                            try {
                                if (mIsZh) {
                                    mDesTextView.setText(devicesFromNet.getJSONObject(position).getString("des_ch"));
                                } else {
                                    mDesTextView.setText(devicesFromNet.getJSONObject(position).getString("des_en"));
                                }

                                int version = Integer.parseInt(devicesFromNet.getJSONObject(position).getString("versionCode"));
                                String str = String.format("%.2f", Float.valueOf(((float) version / 100.0f)));
                                mHardwareTextView.setText(getResources().getString(R.string.newsoftversion) + str);
                                return;
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mBTNUpdateHW.setVisibility(View.GONE);
                mBTNDownloadHW.setVisibility(View.GONE);
                mHardwareTextView.setVisibility(View.GONE);
                mHWProgressBar.setVisibility(View.INVISIBLE);
                mSelectTextView.setVisibility(View.GONE);
                mSelectListView.setVisibility(View.GONE);
                mDesTextView.setVisibility(View.GONE);
                mIsZh = isZh();
                getNetFirmwareVersion();
                mVersion = MainActivity.mVersion;
                mUnicycleType = MainActivity.mUnicycleType;
                if (mVersion != -0x1) {
                    String str = String.format("%.2f", Float.valueOf(((float) mVersion / 100.0f)));
                    mDiscoverTextView.setText(getResources().getString(R.string.currentsoftversion) + str);
                }
                mHandler.postDelayed(new Runnable() {

                    public void run() {
                        if (mVersion == -1) {
                            mBTNDownloadHW.setVisibility(View.VISIBLE);
                            mHardwareTextView.setVisibility(View.VISIBLE);
                            mDesTextView.setVisibility(View.GONE);
                            mSelectTextView.setVisibility(View.VISIBLE);
                            mSelectListView.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(SharefMgr.getHardwarePath(getApplicationContext()))) {
                                mBTNUpdateHW.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, 0x1388);
                break;
            }
            case 2131493010: {
                title.setText(getString(R.string.dgkz));
                findViewById(R.id.layout_submenudgkz).setVisibility(View.VISIBLE);
                seebar_light = (SeekBar) findViewById(R.id.submenuseekbarlight);
                seebar_light.setOnSeekBarChangeListener(this);
                seebar_light2 = (SeekBar) findViewById(R.id.submenuseekbarlight2);
                seebar_light2.setOnSeekBarChangeListener(this);
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                break;
            }
            case 2131493011: {
                title.setText(getString(R.string.cdkz));
                findViewById(R.id.layout_submenucdkz).setVisibility(View.VISIBLE);
                seebar_cdlight = (SeekBar) findViewById(R.id.submenucdkzmode);
                seebar_cdlight.setOnSeekBarChangeListener(this);
                seebar_cdlight2 = (SeekBar) findViewById(R.id.submenucdkzlight);
                seebar_cdlight2.setOnSeekBarChangeListener(this);
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                break;
            }
            case 2131493012: {
                title.setText(getString(R.string.qxsj));
                findViewById(R.id.layout_submenuqxsz).setVisibility(View.VISIBLE);
                menu7wj = (Button) findViewById(R.id.submenuqxszwj);
                menu7qx = (Button) findViewById(R.id.submenuqxszqx);
                menu7xx = (Button) findViewById(R.id.submenuqxszxx);
                break;
            }
            case 2131493013: {
                title.setText(getString(R.string.yykz));
                findViewById(R.id.layout_submenuyykz).setVisibility(View.VISIBLE);
                menu18yykz = (Button) findViewById(R.id.submenuyykz);
                break;
            }
            case 2131492999: {
                title.setText(getString(R.string.jbxx));
                findViewById(R.id.layout_submenujbxx).setVisibility(View.VISIBLE);
                jbxxsn = (TextView) findViewById(R.id.submenujbxxsn);
                jbxxgjbb = (TextView) findViewById(R.id.submenujbxxgjbb);
                jbxxqxms = (TextView) findViewById(R.id.submenujbxxqxms);
                jbxxctwd = (TextView) findViewById(R.id.submenujbxxwd);
                jbxxsn.setText(MainActivity.mUnicycleSN);
                if (MainActivity.mVersion != -1) {
                    String str = String.format("%s-V%.2f", Float.valueOf(((float) mVersion / 100.0f)), -0x56, 0x55, 0x54, 0x14, 0x5a, 0x5a, -0x56, 0x55, 0x6d, 0x14, 0x5a, 0x5a, MainActivity.mUnicycleType, Float.valueOf(((float) MainActivity.mVersion / 100.0f)));
                    jbxxgjbb.setText(str);
                }
                if (MainActivity.currentMode == 0) {
                    jbxxqxms.setText(getString(R.string.play_mode));
                } else if (MainActivity.currentMode == 0x1) {
                    jbxxqxms.setText(getString(R.string.ride_mode));
                } else if (MainActivity.currentMode == 0x2) {
                    jbxxqxms.setText(getString(R.string.study_mode));
                }
                jbxxctwd.setText(MainActivity.temperatureValue + "\u2103");
                break;
            }
            case 2131493000: {
                title.setText(getString(R.string.dcxx));
                findViewById(R.id.layout_submenudcxx).setVisibility(View.VISIBLE);
                dcxxdy = (TextView) findViewById(R.id.submenudcxxdy);
                dcxxdl = (TextView) findViewById(R.id.submenudcxxdl);
                dcxxgl = (TextView) findViewById(R.id.submenudcxxgl);
                break;
            }
            case 2131493003: {
                title.setText(getString(R.string.zxdh));
                findViewById(R.id.layout_submenuzxdh).setVisibility(View.VISIBLE);
                break;
            }
            case 2131493004: {
                title.setText(getString(R.string.tlq));
                findViewById(R.id.layout_submenutlq).setVisibility(View.VISIBLE);
                break;
            }
            case 2131492996: {
                title.setText(getString(R.string.xgn));
                findViewById(R.id.layout_submenuxgn).setVisibility(View.VISIBLE);
                break;
            }
            case 2131492998: {
                title.setText(getString(R.string.fwgw));
                findViewById(R.id.layout_submenufwgw).setVisibility(View.VISIBLE);
                break;
            }
            case 2131492997:
            case 2131493001:
            case 2131493002:
            case 2131493005: {
                break;
            }
        }
    }

    public void onClick(View paramView) {
        byte[] arrayOfByte = new byte[20];
        switch (paramView.getId())
        {
            case R.id.xgn:
            case R.id.bbjc:
            case R.id.jbxx:
            case R.id.dcxx:
            case R.id.appwtfk:
            case R.id.sms:
            case R.id.zxdh:
            case R.id.tlq:
            case R.id.wtfk:
            case R.id.dgkz:
            case R.id.cdkz:
            case R.id.qxsz:
            default:
            case R.id.back:
            case R.id.submenuspjz:
            case R.id.submenuxssz:
            case R.id.noteaccept:
            case R.id.notenotaccept:
            case R.id.snaccept:
            case R.id.snnotaccept:
            case R.id.download:
                do
                {
                    arrayOfByte[0] = -86;
                    arrayOfByte[1] = 85;
                    arrayOfByte[16] = -119;
                    arrayOfByte[17] = 20;
                    arrayOfByte[18] = 90;
                    arrayOfByte[19] = 90;
                    MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
                    if ((this.speedLevel1 > this.speedLevel2) || (this.speedLevel2 > this.speedLevel3))
                    {
                        Toast.makeText(this, R.string.parameter_invalid, Toast.LENGTH_LONG).show();
                    }
                    writeAlarmParas();
                    findViewById(R.id.layout_notes).setVisibility(View.GONE);
                    findViewById(R.id.layout_sn).setVisibility(View.VISIBLE);
                    if (verify())
                    {
                        Toast.makeText(this, getResources().getString(R.string.advance_setting_success), Toast.LENGTH_LONG).show();
                        SharefMgr.saveRootUser(getApplicationContext(), this.mDeviceAddrString);
                        finish();
                    }
                    Toast.makeText(this, getResources().getString(R.string.advance_setting_failure), Toast.LENGTH_LONG).show();
                    finish();
                } while (this.mSeleletype == -1);
                try
                {
                    this.mHWProgressBar.setVisibility(View.VISIBLE);
                    this.mHWProgressBar.setMax(100);
                    this.mHWProgressBar.setProgress(0);
                    this.mBTNUpdateHW.setVisibility(View.INVISIBLE);
                    SharefMgr.setHardwarePath(this, "");
                    Intent localIntent = new Intent(this, HardwareUpdateService.class);
                    localIntent.putExtra("versionurl_string_key", this.devicesFromNet.getJSONObject(this.mSeleletype).getString("app_url"));
                    startService(localIntent);
                    new Intent(this, HardwareUpdateService.class);
                    bindService(localIntent, this.mDownServiceConnection, Context.BIND_AUTO_CREATE);
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
                break;
            case R.id.update:
                if (!this.mYmodem.booleanValue()) {}
                for (boolean bool = true;; bool = false)
                {
                    this.mYmodem = Boolean.valueOf(bool);
                    if (!this.mYmodem.booleanValue()) {
                        break;
                    }
                    arrayOfByte[0] = -86;
                    arrayOfByte[1] = 85;
                    arrayOfByte[16] = 112;
                    arrayOfByte[17] = 20;
                    arrayOfByte[18] = 90;
                    arrayOfByte[19] = 90;
                    MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
                    this.mBTNUpdateHW.setVisibility(View.GONE);
                    this.mBTNDownloadHW.setVisibility(View.GONE);
                    this.mSelectTextView.setVisibility(View.GONE);
                    this.mSelectListView.setVisibility(View.GONE);
                    this.mDesTextView.setVisibility(View.VISIBLE);
                    this.mHWProgressBar.setVisibility(View.VISIBLE);
                    this.mHWProgressBar.setProgress(0);
                    try
                    {
                        this.mHardVersion = SharefMgr.getHardwareVersion(this);
                        this.mHardwareFilePath = SharefMgr.getHardwarePath(this);
                        if (!new File(this.mHardwareFilePath).exists()) {
                            SharefMgr.setHardwarePath(this, "");
                        }
                        this.mHardwareFilePath = SharefMgr.getHardwarePath(this);
                        this.mFile_buf = getBytesFromFile(new File(this.mHardwareFilePath));
                        this.mFileName = new File(this.mHardwareFilePath).getName();
                        this.mFileSize = ((int)new File(this.mHardwareFilePath).length());
                        this.mHWProgressBar.setMax(this.mFileSize);
                    }
                    catch (IOException localIOException)
                    {
                        this.mFile_buf = null;
                        localIOException.printStackTrace();
                    }
                }
                this.mHWProgressBar.setVisibility(View.GONE);
                break;
            case R.id.submenuqxszwj:
                arrayOfByte[0] = -86;
                arrayOfByte[1] = 85;
                arrayOfByte[2] = 0;
                arrayOfByte[3] = -32;
                arrayOfByte[16] = -121;
                arrayOfByte[17] = 20;
                arrayOfByte[18] = 90;
                arrayOfByte[19] = 90;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
                break;
            case R.id.submenuqxszqx:
                arrayOfByte[0] = -86;
                arrayOfByte[1] = 85;
                arrayOfByte[2] = 1;
                arrayOfByte[3] = -32;
                arrayOfByte[16] = -121;
                arrayOfByte[17] = 20;
                arrayOfByte[18] = 90;
                arrayOfByte[19] = 90;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
                break;
            case R.id.submenuyykz:
                arrayOfByte[0] = -86;
                arrayOfByte[1] = 85;
                arrayOfByte[2] = ((byte)this.bluemode);
                int i = this.voicemodeoff;
                int j = 0;
                if (i > 0) {}
                for (;;)
                {
                    arrayOfByte[3] = ((byte)j);
                    arrayOfByte[16] = 115;
                    arrayOfByte[17] = 20;
                    arrayOfByte[18] = 90;
                    arrayOfByte[19] = 90;
                    MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
                    j = 1;
                }
        }
        arrayOfByte[0] = -86;
        arrayOfByte[1] = 85;
        arrayOfByte[2] = 2;
        arrayOfByte[3] = -32;
        arrayOfByte[16] = -121;
        arrayOfByte[17] = 20;
        arrayOfByte[18] = 90;
        arrayOfByte[19] = 90;
        MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(arrayOfByte);
    }

    private boolean verify() {
        if ((this.authEditText == null) || (TextUtils.isEmpty(this.authEditText.getText()))) {
            return false;
        }
        String str = this.authEditText.getText().toString().replace("-", "");
        if (str.length() != 12) {
            return false;
        }
        int[] arrayOfInt = new int[6];
        for (int i = 0; i < 6; i++) {
            arrayOfInt[i] = hexStringToInt(str.substring(i * 2, 2 + i * 2));
            Log.v("MainActivity", arrayOfInt[i] + "");
        }
        int j = 0xFF & arrayOfInt[0] + arrayOfInt[1] + arrayOfInt[2] + arrayOfInt[3] + arrayOfInt[4];
        int k = arrayOfInt[5];
        boolean bool;
        int m;
        if (j == k) {
            m = 0;
        }
        for (int n = 0; ; n++) {
            if (n < 6) {
                if (arrayOfInt[0] + this.serialNum[n] == 255) {
                    m++;
                }
                if (m < 3) {
                }
            } else {
                if (m < 3) {
                    break;
                }
                bool = true;
                return bool;
            }
        }
        for (int i1 = 0; ; i1++) {
            if (i1 < 6) {
                if (arrayOfInt[2] + this.serialNum[i1] == 255) {
                    m++;
                }
                if (m < 3) {
                }
            } else {
                if (m < 3) {
                    break label269;
                }
                bool = true;
                break;
            }
        }
        label269:
        for (int i2 = 0; ; i2++) {
            if (i2 < 6) {
                if (arrayOfInt[4] + this.serialNum[i2] == 255) {
                    m++;
                }
                if (m < 3) {
                }
            } else {
                bool = false;
                if (m < 3) {
                    break;
                }
                bool = true;
                break;
            }
        }
    }

    private int hexStringToInt(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++){
            if(s.length() == 2) {
                char c = s.charAt(i);
                int h = 0;
                if((48 <= c) && (c <= 57)){
                    h = c - 48;
                }
                else if ((97 <= c) && (c <= 102)) {
                    h = c - 0x57;
                }
                else if ((65 <= c) && (c <= 70)) {
                    h = c - 55;
                }
                result += (i > 0 ? 1 : 16 * h);
            }
        }
        return result;
    }

    private void getSerialNum(String macAddr, int[] serialNum, String[] serialString) {
        String[] s = macAddr.split(":");
        if (s.length < 0x6) {
            return;
        }
        for (int i = 5; i >= 0; i--) {
            serialNum[s.length] = hexStringToInt(s[i]);
            serialString[s.length] = s[i].toString();
        }
    }

    private void readAlarmParas() {
        byte[] data = new byte[0x14];
        data[0x0] = -0x56;
        data[0x1] = 0x55;
        data[0x10] = -0x68;
        data[0x11] = 0x14;
        data[0x12] = 0x5a;
        data[0x13] = 0x5a;
        MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
    }

    private void writeAlarmParas() {
        byte[] data = new byte[0x14];
        data[0x0] = -0x56;
        data[0x1] = 0x55;
        data[0x2] = (byte) speedLevel1;
        data[0x3] = 0x0;
        data[0x4] = (byte) speedLevel2;
        data[0x5] = 0x0;
        data[0x6] = (byte) speedLevel3;
        data[0x7] = 0x0;
        data[0x8] = (byte) waneSpeed;
        data[0x9] = 0x0;
        data[0x10] = -0x7b;
        data[0x11] = 0x14;
        data[0x12] = 0x5a;
        data[0x13] = 0x5a;
        MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
    }

    private int byteArrayInt2(byte low, byte high) {
        int result = 0x0;
        result = (low & 0xff) + ((high & 0xff) * 0x100);
        return result;
    }

    private void analysiResponse(byte[] data) {
        if (data.length < 20) {
            return;
        }
        int a1 = data[0x0] & 0xff;
        int a2 = data[0x1] & 0xff;
        if ((a1 == 0xaa) && (a2 == 0x55)) {
            if (((data[0x10] & 0xff) == 0xb5) && (currentSettingId == 0x7f0c008f)) {
                for (int i = 0; i < data.length; i++) {
                    stringBuilder.append(String.format("%02X ", Byte.valueOf(data[i])));
                }
                speedLevel1 = byteArrayInt2(data[0x4], data[0x5]);
                speedLevel2 = byteArrayInt2(data[0x6], data[0x7]);
                speedLevel3 = byteArrayInt2(data[0x8], data[0x9]);
                waneSpeed = byteArrayInt2(data[0xa], data[0xb]);
                alarm_speed1.setText("" + speedLevel1);
                alarm_speed2.setText("" + speedLevel2);
                alarm_speed3.setText("" + speedLevel3);
                alarm_speed4.setText("" + waneSpeed);
                seebar_speed1.setProgress(speedLevel1);
                seebar_speed2.setProgress(speedLevel2);
                seebar_speed3.setProgress(speedLevel3);
                seebar_speed4.setProgress(waneSpeed);
                return;
            }
            if ((data[0x10] & 0xff) == 0xa4) {
                for (int i = 0x0; i<data.length ; i++){
                    stringBuilder.append(String.format("%02X ", Byte.valueOf(data[i]), Byte.valueOf(data[i])));
                }
                if (((data[0x2] & 0xff) == 0x1) && ((data[0x3] & 0xff) == 0)) {
                    speedLevel1 = byteArrayInt2(data[0x4], data[0x5]);
                    speedLevel2 = byteArrayInt2(data[0x6], data[0x7]);
                    speedLevel3 = byteArrayInt2(data[0x8], data[0x9]);
                    waneSpeed = byteArrayInt2(data[0xa], data[0xb]);
                    alarm_speed1.setText("" + speedLevel1);
                    alarm_speed2.setText("" + speedLevel2);
                    alarm_speed3.setText("" + speedLevel3);
                    alarm_speed4.setText("" + waneSpeed);
                    seebar_speed1.setProgress(speedLevel1);
                    seebar_speed2.setProgress(speedLevel2);
                    seebar_speed3.setProgress(speedLevel3);
                    seebar_speed4.setProgress(waneSpeed);
                    Toast.makeText(getApplicationContext(), R.string.setting_success, Toast.LENGTH_LONG).show();
                    return;
                }
                if (((data[0x2] & 0xff) == 0) && ((data[0x3] & 0xff) == 0)) {
                    Toast.makeText(getApplicationContext(), R.string.setting_failed, Toast.LENGTH_LONG).show();
                }
                return;
            }
            if ((data[0x10] & 0xff) == 0xb9) {
                if ((bluemode != data[0xa]) && (currentSettingId == 0x7f0c0092)) {
                    bluemode = data[0xa];
                    if (bluemode == Toggle_Light_ON) {
                        seebar_light.setProgress(0x0);
                    } else if (bluemode == Toggle_Light_OFF) {
                        seebar_light.setProgress(0x2);
                    } else if (bluemode == Toggle_Light_Auto) {
                        seebar_light.setProgress(0x1);
                    }
                }
                bluemode = data[0xa];
                voicemodeoff = data[0xb];
                if (currentSettingId == 0x7f0c0095) {
                    if (voicemodeoff == 0x1) {
                        menu18yykz.setBackgroundDrawable(getResources().getDrawable(R.mipmap.sound_off));
                    } else {
                        menu18yykz.setBackgroundDrawable(getResources().getDrawable(R.mipmap.sound_on));
                    }
                }
                if (coolstatus != data[0xc]) {
                    coolstatus = data[0xc];
                }
                return;
            }
            if ((data[0x10] & 0xff) == 0xa9) {
                currentMode = -0x1;
                if (((data[0xf] & 0xff) == 0xe0) && (currentSettingId == 0x7f0c0094)) {
                    currentMode = data[0xe];
                    if (currentMode == 0) {
                        menu7wj.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_selecte));
                        menu7qx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                        menu7xx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                    } else if (currentMode == 0x1) {
                        menu7wj.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                        menu7qx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_selecte));
                        menu7xx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                    } else if (currentMode == 0x2) {
                        menu7wj.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                        menu7qx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_bg));
                        menu7xx.setBackgroundDrawable(getResources().getDrawable(R.mipmap.submenu7_selecte));
                    }
                }
                if (currentSettingId == R.id.dcxx) {
                    float voltage = (float) byteArrayInt2(data[0x2], data[0x3]) / 100.0f;
                    float current = (float) byteArrayInt2(data[0xa], data[0xb]) / 100.0f;
                    if (voltage > 75.0f) {
                        voltage = 75.0f;
                    } else if (voltage < 45.0f) {
                        voltage = 45.0f;
                    }
                    BigDecimal bd = new BigDecimal((double) voltage);
                    bd = bd.setScale(0x1, 0x4);
                    dcxxdy.setText(bd.toString() + "V");
                    if (current > 70.0f) {
                        current = 70.0f;
                    } else if (current < 0x0) {
                        current = 0.0f;
                    }
                    bd = new BigDecimal((double) current);
                    bd = bd.setScale(0x1, 0x4);
                    dcxxdl.setText(bd.toString() + "A");
                    bd = new BigDecimal((double) (voltage * current));
                    bd = bd.setScale(0x1, 0x4);
                    dcxxgl.setText(bd.toString() + "W");
                }
                return;
            }
            if ((data[0x10] & 0xff) == 0x6e) {
                if (currentSettingId == R.id.cdkz) {
                    discolorled = data[0x2];
                    if (discolorled == 0x1) {
                        seebar_cdlight2.setProgress(0x1);
                    } else {
                        seebar_cdlight2.setProgress(0x0);
                    }
                    MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
                }
                return;
            }
            if ((data[0x10] & 0xff) == 0x52) {
                if (currentSettingId == R.id.cdkz) {
                    colorledmode = data[0x2];
                    if (colorledmode == 0) {
                        seebar_cdlight.setProgress(0x0);
                        return;
                    }
                    if (colorledmode == 0x1) {
                        seebar_cdlight.setProgress(0x1);
                        return;
                    }
                    seebar_cdlight.setProgress(0x2);
                }
                return;
            }
            if ((data[0x10] & 0xff) == 0x55) {
                if (currentSettingId == R.id.dgkz) {
                    lightmode = data[0x2];
                    if (lightmode == 0x1) {
                        seebar_light2.setProgress(0x1);
                        return;
                    }
                    seebar_light2.setProgress(0x0);
                }
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.submenuseekbarlevel1) {
            if ((!isRootUser) && (progress > 0x12)) {
                progress = 0x12;
            }
            speedLevel1 = progress;
            alarm_speed1.setText("" + speedLevel1);
            return;
        }
        if (seekBar.getId() == R.id.submenuseekbarlevel2) {
            if ((!isRootUser) && (progress > 0x13)) {
                progress = 0x13;
            }
            speedLevel2 = progress;
            alarm_speed2.setText("" + speedLevel2);
            return;
        }
        if (seekBar.getId() == R.id.submenuseekbarlevel3) {
            if ((!isRootUser) && (progress > 0x14)) {
                progress = 0x14;
            }
            speedLevel3 = progress;
            alarm_speed3.setText("" + speedLevel3);
            return;
        }
        if (seekBar.getId() == R.id.submenuseekbarrockerspeed) {
            if ((!isRootUser) && (progress > 0x14)) {
                progress = 0x14;
            }
            waneSpeed = progress;
            alarm_speed4.setText("" + waneSpeed);
            return;
        }
        if (seekBar.getId() == R.id.submenuseekbarlight) {
            if (fromUser) {
                byte[] data = new byte[0x14];
                if (progress == 0x2) {
                    data[0x2] = (byte) Toggle_Light_OFF;
                } else if (progress == 0x1) {
                    data[0x2] = (byte) Toggle_Light_Auto;
                } else if (progress == 0) {
                    data[0x2] = (byte) Toggle_Light_ON;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x3] = (byte) voicemodeoff > 0 ? 0x1 : 0x0;
                data[0x10] = 0x73;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
            }
            return;
        }
        if (seekBar.getId() == R.id.submenuseekbarlight2) {
            if (fromUser) {
                byte[] data = new byte[0x14];
                if (progress == 0x1) {
                    data[0x2] = 0x1;
                } else if (progress == 0) {
                    data[0x2] = 0x0;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x10] = 0x53;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
            }
            return;
        }
        if (seekBar.getId() == R.id.submenucdkzmode) {
            if (fromUser) {
                byte[] data = new byte[0x14];
                if (progress == 0x2) {
                    data[0x2] = 0x2;
                } else if (progress == 0x1) {
                    data[0x2] = 0x1;
                } else {
                    data[0x2] = 0x0;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x10] = 0x50;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
            }
            return;
        }
        if (seekBar.getId() == R.id.submenucdkzlight) {
            if (fromUser) {
                byte[] data = new byte[0x14];
                if (progress == 0x1) {
                    data[0x2] = 0x1;
                } else if (progress == 0) {
                    data[0x2] = 0x0;
                }
                data[0x0] = -0x56;
                data[0x1] = 0x55;
                data[0x10] = 0x6c;
                data[0x11] = 0x14;
                data[0x12] = 0x5a;
                data[0x13] = 0x5a;
                MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(data);
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        }
        return false;
    }

    public void getNetFirmwareVersion() {
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.getCache().clear();
        objRequest.setTag("checkNewVision");
        mRequestQueue.add(objRequest);
    }

    public void Int2Str(byte[] str, int intnum) {
        int Div = 0x3b9aca00, j = 0x0, Status = 0x0;
        for (int i = 0x0, j = j; i < 0xa; i = i + 0x1) {
            str[(j++)] = (byte) ((intnum / Div) + 0x30);
            Div = Div / 0xa;
            Status != 0 ? 0x0 : (0x0 != null)){
                j = 0x0;
                continue;
            }
            Status = Status + 0x1;
        }
    }

    public void Ymodem_PrepareIntialPacket(byte[] data, String strfileName, int length) {
        byte[] arrayOfByte1 = new byte[10];
        for (;;)
        {
            int m;
            try
            {
                paramArrayOfByte[0] = 1;
                paramArrayOfByte[1] = 0;
                byte[] arrayOfByte2 = paramString.getBytes("ISO-8859-1");
                int i = 0;
                if ((i < arrayOfByte2.length) && (i < 256))
                {
                    paramArrayOfByte[(i + 2)] = arrayOfByte2[i];
                    i++;
                    continue;
                }
                paramArrayOfByte[(i + 2)] = 0;
                Int2Str(arrayOfByte1, paramInt);
                int j = 1 + (i + 2);
                int k = 0;
                m = j;
                if (arrayOfByte1[k] != 0)
                {
                    int n = m + 1;
                    int i1 = k + 1;
                    paramArrayOfByte[m] = arrayOfByte1[k];
                    k = i1;
                    m = n;
                    continue;
                    if (i2 < 18)
                    {
                        paramArrayOfByte[i2] = 0;
                        i2++;
                        continue;
                    }
                    return;
                }
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException)
            {
                localUnsupportedEncodingException.printStackTrace();
            }
            int i2 = m;
        }
    }

    public void Send_Byte(byte c) {
        byte[] packet_data = new byte[0x14];
        packet_data[0x0] = c;
        for (int i = 0x2; i < 0x12; i = i + 0x1) {
            packet_data[i] = 0x0;
        }
        Ymodem_SendPacket(packet_data, 0x12);
    }

    public void Ymodem_SendPacket(byte[] data, int length) {
        byte[] buf = new byte[(length + 0x2)];
        if (buf != null) {
            System.arraycopy(data, 0x0, buf, 0x0, length);
        }
        short tempCRC = evalCRC16(subBytes(data, 0x2, 0x10));
        buf[length] = (byte) (tempCRC >> 0x8);
        buf[(length + 0x1)] = (byte) (tempCRC & 0xff);
        MainActivity.mBluetoothLeService.writeBluetoothGattCharacteristic(buf);
    }

    void Ymodem_PreparePacket(byte[] SourceBuf, byte[] data, byte pktNo, int sizeBlk) {
        int packetSize = 0x10;
        int size = sizeBlk < packetSize ? sizeBlk : packetSize;
        data[0x0] = 0x1;
        data[0x1] = pktNo;
        byte[] file_ptr = SourceBuf;
        for (int i = 0x2; i < (size + 0x2); i = i + 0x1) {
            data[i] = file_ptr[(i - 0x2)];
        }
        if (size <= packetSize) {
            for (; i < 0x12; i = i + 0x1) {
                data[i] = 0x1a;
            }
        }
    }

    public void Ymodem_Transmit_HEAD(String sendFileName, int sizeFile) {
        byte[] packet_data = new byte[0x14];
        Ymodem_PrepareIntialPacket(packet_data, sendFileName, sizeFile);
        Ymodem_SendPacket(packet_data, 0x12);
    }

    public void Ymodem_Transmit_DATA(byte[] buf, short blkNumber, int size) {
        byte[] packet_data = new byte[0x14];
        Ymodem_PreparePacket(buf, packet_data, (byte) blkNumber, size);
        Ymodem_SendPacket(packet_data, 0x12);
    }

    public void Ymodem_Transmit_END() {
        byte[] packet_data = new byte[0x14];
        packet_data[0x0] = 0x1;
        packet_data[0x1] = 0x0;
        for (int i = 0x2; i < 0x12; i = i + 0x1) {
            packet_data[i] = 0x0;
        }
        Ymodem_SendPacket(packet_data, 0x12);
    }

    public enum eYM_STAT {
        eYM_INIT,
        eYM_RECE_HEAD_PACKET,
        eYM_RECE_DATA_START,
        eYM_RECE_DATA,
        eYM_END,
    }

    class UnicycleDeviceDetail {
        String name;

        UnicycleDeviceDetail(SettingActivity p1, String nam) {
            name = nam;
        }

        String getName() {
            return name;
        }
    }

    class MyListAdapter extends BaseAdapter {
        private ArrayList<SettingActivity.UnicycleDeviceDetail> devices;
        private LayoutInflater mInflator;
        private int mFlag = 0;

        public MyListAdapter(Context context, ArrayList<SettingActivity.UnicycleDeviceDetail> devicesList) {
            devices = devicesList;
            mInflator = ((Activity)context).getLayoutInflater();
        }

        public int getCount() {
            if (devices != null) {
                return devices.size();
            }
            return 0x0;
        }

        public Object getItem(int position) {
            return devices.get(position);
        }

        public long getItemId(int position) {
            return 0x0;
        }

        public View getView(int position, View view, ViewGroup parent) {
            SettingActivity.ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.scan_devices_item, parent, false);
                viewHolder = new SettingActivity.ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.name);
                viewHolder.deviceAddr = (TextView) view.findViewById(R.id.add);
                view.setTag(viewHolder);
            } else {
                viewHolder = (SettingActivity.ViewHolder) view.getTag();
            }
            SettingActivity.UnicycleDeviceDetail device = devices.get(position);
            viewHolder.deviceName.setText(device.getName());
            viewHolder.deviceName.setTextColor(getResources().getColor(R.color.detail_color));
            viewHolder.deviceAddr.setVisibility(View.GONE);
            return view;
        }
    }

    class ViewHolder {
        TextView deviceAddr;
        TextView deviceName;
    }

    public class CmdHandler extends Handler {
        private WeakReference<SettingActivity> mWeak;

        public CmdHandler(SettingActivity paramSettingActivity) {
            this.mWeak = new WeakReference(paramSettingActivity);
        }

        @Override
        public void handleMessage(Message paramMessage) {
            SettingActivity activity = (SettingActivity) mWeak.get();
            if (activity == null)
                super.handleMessage(paramMessage);
        }
    }
}
