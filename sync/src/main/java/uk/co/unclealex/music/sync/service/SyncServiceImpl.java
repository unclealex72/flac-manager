// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SyncServiceImpl.java

package uk.co.unclealex.music.sync.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import org.apache.log4j.Logger;
import uk.co.unclealex.music.base.service.*;

public class SyncServiceImpl
    implements SyncService
{

    public SyncServiceImpl()
    {
    }

    public void synchronise(SynchroniserCallback synchroniserCallback)
        throws IOException
    {
        DeviceSyncService deviceSyncService = getDeviceSyncService();
        for(Iterator i$ = getConnectedDeviceFactory().findAllConnectedDevices().iterator(); i$.hasNext();)
        {
            ConnectedDevice connectedDevice = (ConnectedDevice)i$.next();
            try
            {
                deviceSyncService.synchronise(connectedDevice, synchroniserCallback);
            }
            catch(SynchronisationException e)
            {
                log.error((new StringBuilder()).append("Synchronising device ").append(connectedDevice.getDeviceBean()).append(" failed.").toString(), e);
            }
        }

    }

    public ConnectedDeviceFactory getConnectedDeviceFactory()
    {
        return i_connectedDeviceFactory;
    }

    public void setConnectedDeviceFactory(ConnectedDeviceFactory connectedDeviceFactory)
    {
        i_connectedDeviceFactory = connectedDeviceFactory;
    }

    public DeviceSyncService getDeviceSyncService()
    {
        return i_deviceSyncService;
    }

    public void setDeviceSyncService(DeviceSyncService deviceSyncService)
    {
        i_deviceSyncService = deviceSyncService;
    }

    private static final Logger log = Logger.getLogger(uk/co/unclealex/music/sync/service/SyncServiceImpl);
    private ConnectedDeviceFactory i_connectedDeviceFactory;
    private DeviceSyncService i_deviceSyncService;

}
