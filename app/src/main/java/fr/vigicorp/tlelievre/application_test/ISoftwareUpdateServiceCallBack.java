package fr.vigicorp.tlelievre.application_test;

public abstract interface ISoftwareUpdateServiceCallBack
{
    public abstract void completedUI(String paramString);

    public abstract void preparationUI();

    public abstract void updateProgressUI(int paramInt);
}
