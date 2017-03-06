package fr.vigicorp.tlelievre.application_test;


import android.content.Context;
import android.content.SharedPreferences;

public class SharefMgr
{
    private static String user = "user";

    public static String getHardwarePath(Context paramContext)
    {
        return paramContext.getSharedPreferences(user, 0).getString("hardwarepath", "");
    }

    public static int getHardwareVersion(Context paramContext)
    {
        return paramContext.getSharedPreferences(user, 0).getInt("hardwareversion", 0);
    }

    public static String getLastAddr(Context paramContext)
    {
        SharedPreferences localSharedPreferences = paramContext.getSharedPreferences(user, 0);
        if (localSharedPreferences.contains("last_addr")) {
            return localSharedPreferences.getString("last_addr", "null");
        }
        return "";
    }

    public static String getRootUser(Context paramContext)
    {
        SharedPreferences localSharedPreferences = paramContext.getSharedPreferences(user, 0);
        if (localSharedPreferences.contains("mac_addr")) {
            return localSharedPreferences.getString("mac_addr", "null");
        }
        return "";
    }

    public static void saveRootUser(Context paramContext, String paramString)
    {
        SharedPreferences localSharedPreferences = paramContext.getSharedPreferences(user, 0);
        String str = localSharedPreferences.getString("mac_addr", "");
        SharedPreferences.Editor localEditor = localSharedPreferences.edit();
        localEditor.putString("mac_addr", paramString + str);
        localEditor.commit();
    }

    public static void setHardwarePath(Context paramContext, String paramString)
    {
        SharedPreferences.Editor localEditor = paramContext.getSharedPreferences(user, 0).edit();
        localEditor.putString("hardwarepath", paramString);
        localEditor.commit();
    }

    public static void setHardwareVersion(Context paramContext, int paramInt)
    {
        SharedPreferences.Editor localEditor = paramContext.getSharedPreferences(user, 0).edit();
        localEditor.putInt("hardwareversion", paramInt);
        localEditor.commit();
    }

    public static void setLastAddr(Context paramContext, String paramString)
    {
        SharedPreferences.Editor localEditor = paramContext.getSharedPreferences(user, 0).edit();
        localEditor.putString("last_addr", paramString);
        localEditor.commit();
    }
}
