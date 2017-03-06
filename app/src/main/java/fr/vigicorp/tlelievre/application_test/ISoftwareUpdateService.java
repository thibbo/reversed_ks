package fr.vigicorp.tlelievre.application_test;

import android.content.Context;

public abstract interface ISoftwareUpdateService
{
    public abstract void CancelUpdate();

    public abstract void HideNoitfication();

    public abstract void RegisterCallBack(ISoftwareUpdateServiceCallBack paramISoftwareUpdateServiceCallBack);

    public abstract void SetContext(Context paramContext);

    public abstract void ShowNotification();

    public abstract void UnRegisterCallBack(ISoftwareUpdateServiceCallBack paramISoftwareUpdateServiceCallBack);
}
