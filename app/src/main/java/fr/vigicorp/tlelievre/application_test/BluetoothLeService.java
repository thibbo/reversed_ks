package fr.vigicorp.tlelievre.application_test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

@SuppressLint({"NewApi"})
public class BluetoothLeService extends Service
{
    public class LocalBinder extends Binder
    {
        private BluetoothLeService mBluetoothService;
        public LocalBinder(BluetoothLeService paramBluetoothLeService) {
            super();
            mBluetoothService = paramBluetoothLeService;
        }

        public BluetoothLeService getService()
        {
            return mBluetoothService;
        }
    };

    private static final String DESCRIPTER_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String NOTITY_CHARACTER_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTER_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG = BluetoothLeService.class.getSimpleName();
    private boolean DEBUG = false;
    private long currentTime = 0L;
    private final IBinder mBinder = new LocalBinder(this);
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter.LeScanCallback mCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte) {
            Log.d(TAG, "onLeScan callback device address =" + paramBluetoothDevice.getAddress());
            Log.d(TAG, "onLeScan callback device name =" + paramBluetoothDevice.getName());
            String str = paramBluetoothDevice.getName();
            Intent localIntent = new Intent("scan_devices");
            localIntent.putExtra("deviceName", str);
            localIntent.putExtra("deviceAddr", paramBluetoothDevice.getAddress());
            getApplicationContext().sendBroadcast(localIntent);

        }
    };
    public String mConnectedDeviceAddress = "";
    private int mConnectionState = 0;
    private String mConnecttingDeviceAddress = "";
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int status, int newState)
        {
            Log.d(TAG, "onConnectionStateChange called");
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Disconnecting! status: " + status + " newState " + newState);
                paramBluetoothGatt.disconnect();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                mConnectedDeviceAddress = "";

                Log.e(TAG, "Connected to service!");
            paramBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "Link disconnected");
                mConnectionState = STATE_DISCONNECTED;
                BluetoothLeService.this.broadcastUpdate("bluetooth_connection_state", false);

                paramBluetoothGatt.close();
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt)
        {
            Log.d(TAG, "onServicesDiscovered called");
            if (paramInt == 0)
            {
                Log.d(TAG, "onServicesDiscovered called, status == BluetoothGatt.GATT_SUCCESS");
                BluetoothGattService localBluetoothGattService = paramBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                if (localBluetoothGattService == null)
                {
                    Log.d(TAG, "targetService == null");
                    return;
                }
                BluetoothGattCharacteristic localBluetoothGattCharacteristic1 = localBluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                BluetoothGattCharacteristic localBluetoothGattCharacteristic2 = localBluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (localBluetoothGattCharacteristic2 == null)
                {
                    Log.d(TAG, "not found target notify writecharacter");
                    return;
                }
                Log.d(TAG, "writeCharacteristic write =" + localBluetoothGattCharacteristic2.getWriteType());
                if (localBluetoothGattCharacteristic1 == null)
                {
                    Log.d("BluetoothGattCallback", "not found target notify character");
                    return;
                }
                Log.d(TAG, "writeCharacteristic write =" + localBluetoothGattCharacteristic2.getWriteType());
                Log.d(TAG, "found target notify character");
                paramBluetoothGatt.setCharacteristicNotification(localBluetoothGattCharacteristic1, true);
                BluetoothGattDescriptor localBluetoothGattDescriptor = localBluetoothGattCharacteristic1.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                paramBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor);
                mConnectionState = STATE_CONNECTED;
                return;
            }
            Log.d(TAG, "onServicesDiscovered called, status == BluetoothGatt.GATT_FAILURE");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
        {
            Log.d(TAG, "onCharacteristicRead called");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
        {
            Log.d(TAG, "onCharacteristicWrite called=" + paramBluetoothGattCharacteristic.getUuid().toString());
            if (paramBluetoothGattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
                paramBluetoothGattCharacteristic.getValue();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
        {
            Log.d(TAG, "onCharacteristicChanged called" + paramBluetoothGattCharacteristic.getUuid().toString());
            if (paramBluetoothGattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb"))
            {
                byte[] arrayOfByte = paramBluetoothGattCharacteristic.getValue();
                StringBuilder localStringBuilder = new StringBuilder(20);
                for (int i = 0; i < arrayOfByte.length; i++)
                {
                    Object[] arrayOfObject = new Object[1];
                    arrayOfObject[0] = Byte.valueOf(arrayOfByte[i]);
                    localStringBuilder.append(String.format("%02X ", arrayOfObject));
                }
                Log.d(TAG, "received data = " + localStringBuilder);
                Intent localIntent = new Intent("bluetooth_data");
                localIntent.putExtra("data", arrayOfByte);
                getApplicationContext().sendBroadcast(localIntent);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor, int paramInt)
        {
            Log.d(TAG, "onDescriptorWrite");
            if (paramInt == 0)
            {
                Log.d(TAG, "onDescriptorWrite success!");
                BluetoothLeService.this.broadcastUpdate("bluetooth_connection_state", true);
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context paramContext, Intent paramIntent) {
            String action = paramIntent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                Log.d(TAG, BluetoothAdapter.ACTION_STATE_CHANGED);
                if (paramIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 10) == 12) {
                    Log.d(TAG, "BluetoothAdapte scanLeDevice");
                }
            }
            else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                return;

            else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
            {
                BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "remote device ACTION_ACL_DISCONNECTED" + localBluetoothDevice.getAddress());
                return;
            }
            String str2 = paramIntent.getStringExtra("deviceAddr");
            connect(str2);
        }
    };

    private void LOGI(String paramString)
    {
        if (this.DEBUG) {
            Log.i(TAG, paramString);
        }
    }

    private void broadcastBluetoothCmd(String paramString, boolean paramBoolean)
    {
        Intent localIntent = new Intent(paramString);
        localIntent.putExtra("bluetooth_cmd", paramBoolean);
        sendBroadcast(localIntent);
    }

    private void broadcastUpdate(String paramString, boolean paramBoolean)
    {
        Intent localIntent = new Intent(paramString);
        localIntent.putExtra("bluetooth_connection_state", paramBoolean);
        sendBroadcast(localIntent);
    }

    public void close()
    {
        if (this.mBluetoothGatt == null) {
            return;
        }
        this.mBluetoothGatt.close();
        this.mBluetoothGatt = null;
    }

    public boolean connect(String paramString)
    {
        if ((this.mBluetoothAdapter == null) || (paramString == null) || (paramString.equals("")))
        {
            LOGI("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        if ((this.mConnectedDeviceAddress != null) && (paramString.equals(this.mConnectedDeviceAddress)) && (this.mBluetoothGatt != null))
        {
            LOGI("Trying to use an existing mBluetoothGatt for connection.");
            if (this.mBluetoothGatt.connect())
            {
                LOGI("mBluetoothGatt.connect()");
                this.mConnectionState = 1;
                return true;
            }
            return false;
        }
        BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(paramString);
        if (localBluetoothDevice == null)
        {
            LOGI("Device not found.  Unable to connect.");
            return false;
        }
        this.mBluetoothGatt = localBluetoothDevice.connectGatt(this, false, this.mGattCallback);
        LOGI("Trying to create a new connection.");
        this.mConnectionState = 1;
        this.mConnecttingDeviceAddress = localBluetoothDevice.getAddress();
        return true;
    }

    public void disconnect()
    {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
        {
            LOGI("BluetoothAdapter not initialized-disconnect");
            return;
        }
        this.mConnectedDeviceAddress = "host_disconnect";
        this.mBluetoothGatt.disconnect();
        this.mConnectionState = 0;
    }

    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }

    public boolean initialize()
    {
        if (this.mBluetoothManager == null)
        {
            this.mBluetoothManager = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE));
            if (this.mBluetoothManager == null)
            {
                LOGI("Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null)
        {
            LOGI("Unable to obtain a BluetoothAdapter.");
            return false;
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            this.mBluetoothAdapter.enable();
        }
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        localIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        localIntentFilter.addAction("connect_device");
        registerReceiver(this.receiver, localIntentFilter);
        if (this.mBluetoothAdapter.isEnabled()) {
            LOGI("init scanLeDevice");
        }
        return true;
    }

    public IBinder onBind(Intent paramIntent)
    {
        return this.mBinder;
    }

    public void onCreate()
    {
        Log.i(TAG, "ExampleService-onCreate");
        super.onCreate();
    }

    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    public void onStart(Intent paramIntent, int paramInt)
    {
        Log.i(TAG, "ExampleService-onStart");
        super.onStart(paramIntent, paramInt);
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
    {
        Log.i(TAG, "ExampleService-onStartCommand");
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    public boolean onUnbind(Intent paramIntent)
    {
        close();
        stopSelf();
        return super.onUnbind(paramIntent);
    }

    public void readCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
    {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
        {
            LOGI("BluetoothAdapter not initialized- readCharacteristic");
            return;
        }
        this.mBluetoothGatt.readCharacteristic(paramBluetoothGattCharacteristic);
    }

    public void scanLeDevice(boolean paramBoolean)
    {
        if (paramBoolean)
        {
            this.mBluetoothAdapter.stopLeScan(this.mCallback);
            this.mBluetoothAdapter.startLeScan(this.mCallback);
            LOGI("scanLeDevice true");
            return;
        }
        this.mBluetoothAdapter.stopLeScan(this.mCallback);
        LOGI("scanLeDevice false");
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean)
    {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null))
        {
            LOGI("BluetoothAdapter not initialized- setNotify");
            return;
        }
        this.mBluetoothGatt.setCharacteristicNotification(paramBluetoothGattCharacteristic, paramBoolean);
    }

    public boolean writeBluetoothGattCharacteristic(byte[] paramArrayOfByte)
    {
        if (this.mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService localBluetoothGattService = this.mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
        if (localBluetoothGattService == null)
        {
            LOGI("writeBluetoothGattCharacteristic service == null");
            return false;
        }
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
        if (localBluetoothGattCharacteristic == null)
        {
            LOGI("writeBluetoothGattCharacteristic characteristic == null");
            return false;
        }
        localBluetoothGattCharacteristic.setValue(paramArrayOfByte);
        LOGI("writeBluetoothGattCharacteristic writeType = " + localBluetoothGattCharacteristic.getWriteType());
        localBluetoothGattCharacteristic.setWriteType(1);
        return this.mBluetoothGatt.writeCharacteristic(localBluetoothGattCharacteristic);
    }
}
