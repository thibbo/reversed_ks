package fr.vigicorp.tlelievre.application_test;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class FindActivity
        extends AppCompatActivity
        implements View.OnClickListener
{
    public static BluetoothLeService mBluetoothLeService;
    private final int MSG_CONNECTING = 1;
    private final int MSG_CONNECT_FAILURE = 3;
    private final int MSG_CONNECT_SUCCESS = 2;
    private Animation an;
    private Button btnScan;
    private boolean connectState = false;
    private ArrayList<String> devicesAddrList = new ArrayList();
    private ImageView findGuide;
    private String mDeviceAddrString = "";
    private ListView mListView;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent paramIntent) {
            String str1 = paramIntent.getAction();
            if (str1.equals("bluetooth_connection_state")) {}
            String name;
            String address;
            do
            {
                do
                {
                    return;
                } while (!str1.equals("scan_devices"));
                name = paramIntent.getStringExtra("deviceName");
                address = paramIntent.getStringExtra("deviceAddr");
            } while (devicesAddrList.contains(address));
            devicesAddrList.add(address);
            mUnPairedAdapter.devices.add(new BluetoothDeviceDetail(name, address));
            mUnPairedAdapter.notifyDataSetChanged();
        }
    };

    private class BluetoothDeviceDetail {
        BluetoothDeviceDetail(String name, String address) {
            this.address = address;
            this.name = name;
        }

        private String address;
        private String name;

        String getAddress() {
            return address;
        }

        String getName() {
            return name;
        }
    };

    private class MyListAdapter extends BaseAdapter
    {
        private ArrayList<BluetoothDeviceDetail> devices;
        private Context mContext;
        private int mFlag = 0;
        private LayoutInflater mInflator;

        public MyListAdapter(ArrayList<BluetoothDeviceDetail> paramArrayList, Context paramContext)
        {
            this.devices = paramArrayList;
            this.mContext = paramContext;
            this.mInflator = getLayoutInflater();
        }

        public int getCount()
        {
            if (this.devices != null) {
                return this.devices.size();
            }
            return 0;
        }

        public Object getItem(int paramInt)
        {
            return this.devices.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return 0L;
        }

        @Override
        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            ViewHolder localViewHolder;
            String str2;
            if (paramView == null)
            {
                paramView = this.mInflator.inflate(R.layout.activity_find, null);
                localViewHolder = new ViewHolder();
                localViewHolder.deviceName.setText("Device name");
                localViewHolder.deviceAddr.setText("Device address");
                paramView.setTag(localViewHolder);
                BluetoothDeviceDetail localBluetoothDeviceDetail = (BluetoothDeviceDetail)this.devices.get(paramInt);
                String str1 = localBluetoothDeviceDetail.getName();
                str2 = localBluetoothDeviceDetail.getAddress();
                Log.i("anqii", "name=" + str1 + ", deviceAddr=" + str2);
                if ((str1 == null) || (str1.length() <= 0)) {
                    break label181;
                }
                localViewHolder.deviceName.setText(str1);
            }
            for (;;)
            {
                if ((str2 == null) || (str2.length() <= 0)) {
                    break label201;
                }
                localViewHolder.deviceAddr.setText(str2);
                return paramView;
                localViewHolder = (ViewHolder)paramView.getTag();
                break;
                label181:
                localViewHolder.deviceName.setText(this.this$0.getString(2131099794));
            }
            label201:
            localViewHolder.deviceAddr.setText(this.this$0.getString(2131099793));
            return paramView;
        }
    }

    private class ViewHolder {
        TextView deviceAddr;
        TextView deviceName;
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
        {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)paramIBinder).getService();
            if (!mBluetoothLeService.initialize())
            {
                Log.e("DeviceControlActivity", "Unable to initialize Bluetooth");
                this.this$0.finish();
                return;
            }
            SharefMgr.getLastAddr(this.this$0);
            mBluetoothLeService.scanLeDevice(true);
            access$700(this.this$0).sendEmptyMessage(3);
        }

        @Override
        public void onServiceDisconnected(ComponentName paramComponentName)
        {
            mBluetoothLeService = null;
        }

    };
    private MyListAdapter mUnPairedAdapter;
    private ArrayList<BluetoothDeviceDetail> mUnPairedDevices = new ArrayList();
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message paramMessage)
        {
            switch (paramMessage.what)
            {
                default:
                    return;
                case 1:
                    access$300(this.this$0).setVisibility(0);
                    access$300(this.this$0).setText(this.this$0.getString(2131099692));
                    access$400(this.this$0).setVisibility(8);
                    access$500(this.this$0).clearAnimation();
                    access$600(this.this$0).setVisibility(8);
                    return;
                case 3:
                    access$400(this.this$0).setVisibility(0);
                    access$300(this.this$0).setVisibility(8);
                    access$600(this.this$0).setVisibility(8);
                    return;
            }
            this.this$0.startActivity(new Intent(this.this$0, MainActivity.class));
            this.this$0.finish();
        }

    };

    private TextView txtTip;

    @Override
    public void onClick(View paramView)
    {
        if (paramView.getId() == 2131492970)
        {
            mBluetoothLeService.scanLeDevice(true);
            this.findGuide.startAnimation(this.an);
            this.btnScan.setVisibility(8);
            this.txtTip.setVisibility(0);
            this.mListView.setVisibility(0);
            this.txtTip.setText(getString(2131099678));
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2130968601);
        bindService(new Intent(this, BluetoothLeService.class), this.mServiceConnection, 1);
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("scan_devices");
        registerReceiver(this.mReceiver, localIntentFilter);
        this.mListView = ((ListView)findViewById(2131492972));
        this.mUnPairedAdapter = new MyListAdapter(this, this.mUnPairedDevices, this);
        this.mListView.setAdapter(this.mUnPairedAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
            {
                if ((paramView.getTag() instanceof ViewHolder))
                {
                    ViewHolder localViewHolder = (ViewHolder)paramView.getTag();
                    Intent localIntent = new Intent();
                    localIntent.putExtra("addr", localViewHolder.deviceAddr.getText());
                    this.this$0.setResult(1, localIntent);
                    this.this$0.finish();
                }
            }

        });
        this.txtTip = ((TextView)findViewById(2131492971));
        this.btnScan = ((Button)findViewById(2131492970));
        this.findGuide = ((ImageView)findViewById(2131492969));
        this.an = new RotateAnimation(0.0F, 360.0F, 1, 0.5F, 1, 0.5F);
        this.an.setInterpolator(new LinearInterpolator());
        this.an.setRepeatCount(-1);
        this.an.setFillAfter(true);
        this.an.setDuration(4000L);
        this.an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
        unbindService(this.mServiceConnection);
    }
}