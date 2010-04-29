// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DeviceSyncServiceImpl.java

package uk.co.unclealex.music.sync.service;

import java.util.*;
import uk.co.unclealex.hibernate.service.DataService;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.*;
import uk.co.unclealex.music.base.service.*;

public class DeviceSyncServiceImpl
    implements DeviceSyncService
{

    public DeviceSyncServiceImpl()
    {
    }

    public void synchronise(ConnectedDevice connectedDevice, SynchroniserCallback synchroniserCallback)
        throws SynchronisationException
    {
        DeviceBean deviceBean;
        SortedSet deviceFileBeans;
        SortedSet deviceFilesToDelete;
        DeviceDao deviceDao;
        SortedMap deviceFilesToAdd;
        Synchroniser synchroniser;
        deviceBean = connectedDevice.getDeviceBean();
        deviceFileBeans = deviceBean.getDeviceFileBeans();
        EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
        OwnerBean ownerBean = deviceBean.getOwnerBean();
        EncoderBean encoderBean = deviceBean.getEncoderBean();
        SortedMap trackBeansByDeviceFileBean = new TreeMap();
        Iterator i$ = deviceFileBeans.iterator();
        do
        {
            if(!i$.hasNext())
                break;
            DeviceFileBean deviceFileBean = (DeviceFileBean)i$.next();
            EncodedTrackBean encodedTrackBean = encodedTrackDao.findByCodesAndEncoderAndOwner(deviceFileBean.getArtistCode(), deviceFileBean.getAlbumCode(), deviceFileBean.getTrackNumber(), deviceFileBean.getTrackCode(), ownerBean, encoderBean);
            if(encodedTrackBean != null)
                trackBeansByDeviceFileBean.put(deviceFileBean, encodedTrackBean);
        } while(true);
        deviceFilesToDelete = new TreeSet(deviceFileBeans);
        deviceFilesToDelete.removeAll(trackBeansByDeviceFileBean.keySet());
        Long lastSyncTimestamp = deviceBean.getLastSyncTimestamp();
        if(lastSyncTimestamp == null)
            lastSyncTimestamp = Long.valueOf(-1L);
        Iterator i$ = trackBeansByDeviceFileBean.entrySet().iterator();
        do
        {
            if(!i$.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
            DeviceFileBean deviceFileBean = (DeviceFileBean)entry.getKey();
            EncodedTrackBean encodedTrackBean = (EncodedTrackBean)entry.getValue();
            if(encodedTrackBean.getTimestamp().longValue() > lastSyncTimestamp.longValue())
                deviceFilesToDelete.add(deviceFileBean);
        } while(true);
        deviceFileBeans.removeAll(deviceFilesToDelete);
        deviceDao = getDeviceDao();
        deviceDao.store(deviceBean);
        DataService dataService = getDataService();
        SortedSet encodedTrackBeans = encodedTrackDao.findTracksEncodedAfter(lastSyncTimestamp.longValue(), ownerBean, encoderBean);
        deviceFilesToAdd = new TreeMap();
        EncodedTrackBean encodedTrackBean;
        DeviceFileBean deviceFileBean;
        for(Iterator i$ = encodedTrackBeans.iterator(); i$.hasNext(); deviceFilesToAdd.put(deviceFileBean, dataService.findFile(encodedTrackBean.getTrackDataBean())))
        {
            encodedTrackBean = (EncodedTrackBean)i$.next();
            deviceFileBean = new DeviceFileBean();
            EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
            deviceFileBean.setArtistCode(encodedAlbumBean.getEncodedArtistBean().getCode());
            deviceFileBean.setAlbumCode(encodedAlbumBean.getCode());
            deviceFileBean.setTrackCode(encodedTrackBean.getCode());
            deviceFileBean.setTrackNumber(encodedTrackBean.getTrackNumber().intValue());
        }

        synchroniser = getSynchroniserFactory().createSynchroniser(connectedDevice, synchroniserCallback);
        synchroniser.initialise();
        synchroniser.synchronise(deviceFilesToDelete, deviceFilesToAdd);
        synchroniser.destroy();
        break MISSING_BLOCK_LABEL_485;
        Exception exception;
        exception;
        synchroniser.destroy();
        throw exception;
        deviceFileBeans.addAll(deviceFilesToAdd.keySet());
        deviceBean.setLastSyncTimestamp(Long.valueOf(System.currentTimeMillis()));
        deviceDao.store(deviceBean);
        return;
    }

    public DeviceDao getDeviceDao()
    {
        return i_deviceDao;
    }

    public void setDeviceDao(DeviceDao deviceDao)
    {
        i_deviceDao = deviceDao;
    }

    public SynchroniserFactory getSynchroniserFactory()
    {
        return i_synchroniserFactory;
    }

    public void setSynchroniserFactory(SynchroniserFactory synchroniserFactory)
    {
        i_synchroniserFactory = synchroniserFactory;
    }

    public EncodedTrackDao getEncodedTrackDao()
    {
        return i_encodedTrackDao;
    }

    public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao)
    {
        i_encodedTrackDao = encodedTrackDao;
    }

    public DataService getDataService()
    {
        return i_dataService;
    }

    public void setDataService(DataService dataService)
    {
        i_dataService = dataService;
    }

    private DataService i_dataService;
    private DeviceDao i_deviceDao;
    private SynchroniserFactory i_synchroniserFactory;
    private EncodedTrackDao i_encodedTrackDao;
}
