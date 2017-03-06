package fr.vigicorp.tlelievre.application_test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HardwareUpdateService
        extends Service
{
    private static final String SD_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String SOFRWARE_DOWNLOAD_PATH = SD_FILE_PATH + File.separator + "kingsong" + File.separator + "download";
    public static final String VERSIONURL_STRING_KEY = "versionurl_string_key";
    private boolean isShowNotification = false;
    private HardwareUpdateService.SoftwareUploadBinder mBinder = new HardwareUpdateService.SoftwareUploadBinder(this, null);
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message paramMessage)
        {
            switch (paramMessage.what)
            {
                default:
                    return;
                case 0:
                    this.this$0.stopSelf();
                    return;
            }
            Uri localUri = Uri.fromFile(new File(HardwareUpdateService.getSoftwareDownloadPath() + File.separator + new File(HardwareUpdateService.access$100(this.this$0)).getName()));
            Intent localIntent = new Intent("android.intent.action.VIEW");
            localIntent.setFlags(268435456);
            localIntent.setDataAndType(localUri, "application/vnd.android.package-archive");
            this.this$0.startActivity(localIntent);
            this.this$0.stopSelf();
        }
    };

    private List<ISoftwareUpdateServiceCallBack> mISoftwareUploadCallBackService = new ArrayList();
    private boolean mIsCancelUpdate = false;
    private int mNotifyId = 0;
    private String mURLString;
    private HardwareUpdateService.UpdateThread mUpdateThread;
    private AtomicInteger ratio = new AtomicInteger(0);

    public static String getSoftwareDownloadPath()
    {
        File localFile = new File(SOFRWARE_DOWNLOAD_PATH);
        if (!localFile.exists()) {
            localFile.mkdirs();
        }
        return localFile.getAbsolutePath();
    }

    public static boolean isNetEnable(Context paramContext)
    {
        ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
        if (localConnectivityManager != null)
        {
            NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
            if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()) && (localNetworkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                return true;
            }
        }
        Toast.makeText(paramContext, "Network has been disconnected, please check the network settings", 0).show();
        return false;
    }

    public IBinder onBind(Intent paramIntent)
    {
        return this.mBinder;
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
    {
        if (paramIntent != null)
        {
            this.mURLString = paramIntent.getStringExtra("versionurl_string_key");
            this.mIsCancelUpdate = false;
            this.mUpdateThread = new HardwareUpdateService.UpdateThread(this);
            this.ratio.set(0);
            HardwareUpdateService.DownloadAsyncTask localDownloadAsyncTask = new HardwareUpdateService.DownloadAsyncTask(this);
            String[] arrayOfString = new String[1];
            arrayOfString[0] = this.mURLString;
            localDownloadAsyncTask.execute(arrayOfString);
        }
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    final class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... paramVarArgs)
        {
            HttpURLConnection localHttpURLConnection = null;
            try
            {
                localHttpURLConnection = (HttpURLConnection)new URL(paramVarArgs[0]).openConnection();
                localHttpURLConnection.setConnectTimeout(30000);
                localHttpURLConnection.setReadTimeout(30000);
                if (localHttpURLConnection.getResponseCode() == 200)
                {
                    long l1 = localHttpURLConnection.getContentLength();
                    File localFile = new File(HardwareUpdateService.getSoftwareDownloadPath() + File.separator + new File(paramVarArgs[0]).getName());
                    if (!localFile.exists()) {
                        localFile.createNewFile();
                    }
                    byte[] arrayOfByte = new byte['?'];
                    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                    InputStream localInputStream = localHttpURLConnection.getInputStream();
                    HardwareUpdateService.access$700(this.this$0).start();
                    long l2 = 0L;
                    for (;;)
                    {
                        int i = localInputStream.read(arrayOfByte);
                        if (i <= 0) {
                            break;
                        }
                        localFileOutputStream.write(arrayOfByte, 0, i);
                        l2 += i;
                        Integer[] arrayOfInteger = new Integer[1];
                        arrayOfInteger[0] = Integer.valueOf((int)(100L * l2 / l1));
                        publishProgress(arrayOfInteger);
                        if (HardwareUpdateService.access$400(this.this$0)) {}
                    }
                    localFileOutputStream.close();
                    localInputStream.close();
                    if (l1 == l2)
                    {
                        if (localHttpURLConnection != null) {
                            localHttpURLConnection.disconnect();
                        }
                        Boolean localBoolean = Boolean.valueOf(true);
                        return localBoolean;
                    }
                }
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
                if (localHttpURLConnection != null) {
                    localHttpURLConnection.disconnect();
                }
            }
            return Boolean.valueOf(false);
        }

        @Override
        protected void onPostExecute(Boolean paramBoolean)
        {
            super.onPostExecute(paramBoolean);
        }

        @Override
        protected void onPreExecute()
        {
            if (!HardwareUpdateService.isNetEnable(this.this$0)) {
                cancel(true);
            }
            for (;;)
            {
                return;
                if (HardwareUpdateService.access$700(this.this$0).isAlive()) {
                    HardwareUpdateService.access$700(this.this$0).interrupt();
                }
                HardwareUpdateService.access$302(this.this$0, false);
                Iterator localIterator = HardwareUpdateService.access$500(this.this$0).iterator();
                while (localIterator.hasNext()) {
                    ((ISoftwareUpdateServiceCallBack)localIterator.next()).preparationUI();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... paramVarArgs)
        {
            HardwareUpdateService.access$200(this.this$0).set(paramVarArgs[0].intValue());
            Iterator localIterator = HardwareUpdateService.access$500(this.this$0).iterator();
            while (localIterator.hasNext()) {
                ((ISoftwareUpdateServiceCallBack)localIterator.next()).updateProgressUI(paramVarArgs[0].intValue());
            }
        }
    }

    class SoftwareUploadBinder extends Binder implements ISoftwareUpdateService
    {
        @Override
        public void CancelUpdate()
        {
            HardwareUpdateService.access$402(this.this$0, true);
        }

        @Override
        public void HideNoitfication()
        {
            HardwareUpdateService.access$302(this.this$0, false);
        }

        @Override
        public void RegisterCallBack(ISoftwareUpdateServiceCallBack paramISoftwareUpdateServiceCallBack)
        {
            HardwareUpdateService.access$500(this.this$0).add(paramISoftwareUpdateServiceCallBack);
        }

        @Override
        public void SetContext(Context paramContext) {}

        @Override
        public void ShowNotification() {}

        @Override
        public void UnRegisterCallBack(ISoftwareUpdateServiceCallBack paramISoftwareUpdateServiceCallBack)
        {
            HardwareUpdateService.access$500(this.this$0).remove(paramISoftwareUpdateServiceCallBack);
        }
    }

    final class UpdateThread extends Thread
    {
        @Override
        public void run()
        {
            while (HardwareUpdateService.access$200(this.this$0).get() < 100)
            {
                if (HardwareUpdateService.access$300(this.this$0)) {}
                try
                {
                    Thread.sleep(500L);
                    if (HardwareUpdateService.access$400(this.this$0)) {
                        return;
                    }
                }
                catch (InterruptedException localInterruptedException)
                {
                    localInterruptedException.printStackTrace();
                    return;
                }
            }
            Iterator localIterator = HardwareUpdateService.access$500(this.this$0).iterator();
            while (localIterator.hasNext()) {
                ((ISoftwareUpdateServiceCallBack)localIterator.next()).completedUI(HardwareUpdateService.getSoftwareDownloadPath() + File.separator + new File(HardwareUpdateService.access$100(this.this$0)).getName());
            }
            HardwareUpdateService.access$600(this.this$0).sendEmptyMessage(0);
        }
    }



}
