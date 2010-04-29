// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AbstractSynchroniser.java

package uk.co.unclealex.music.sync.service;

import uk.co.unclealex.music.base.service.*;

public abstract class AbstractSynchroniser
    implements Synchroniser
{

    public AbstractSynchroniser()
    {
    }

    public void postConstruct(ConnectedDevice connectedDevice, SynchroniserCallback synchroniserCallback)
    {
        setConnectedDevice(connectedDevice);
        setSynchroniserCallback(synchroniserCallback);
    }

    public abstract void doPostConstruct();

    public void initialise()
    {
        getSynchroniserCallback().deviceInitialised(getConnectedDevice().getDeviceBean(), "Initialising device");
        doInitialise();
    }

    protected void doInitialise()
    {
    }

    public void destroy()
    {
        getSynchroniserCallback().deviceDestroyed(getConnectedDevice().getDeviceBean(), "Removing device");
        doDestroy();
    }

    protected void doDestroy()
    {
    }

    public ConnectedDevice getConnectedDevice()
    {
        return i_connectedDevice;
    }

    public void setConnectedDevice(ConnectedDevice connectedDevice)
    {
        i_connectedDevice = connectedDevice;
    }

    public SynchroniserCallback getSynchroniserCallback()
    {
        return i_synchroniserCallback;
    }

    public void setSynchroniserCallback(SynchroniserCallback synchroniserCallback)
    {
        i_synchroniserCallback = synchroniserCallback;
    }

    private ConnectedDevice i_connectedDevice;
    private SynchroniserCallback i_synchroniserCallback;
}
