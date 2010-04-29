// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConnectedDeviceFactoryImpl.java

package uk.co.unclealex.music.sync.service;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections15.*;
import uk.co.unclealex.music.base.RequiresPackages;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.model.*;
import uk.co.unclealex.music.base.process.service.ProcessResult;
import uk.co.unclealex.music.base.process.service.ProcessService;
import uk.co.unclealex.music.base.service.*;
import uk.co.unclealex.music.base.visitor.DeviceVisitor;

public class ConnectedDeviceFactoryImpl extends DeviceVisitor
    implements ConnectedDeviceFactory, RequiresPackages
{

    public ConnectedDeviceFactoryImpl()
    {
    }

    public SortedSet findAllConnectedDevices()
        throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[] {
            "lsusb"
        });
        ProcessResult processResult = getProcessService().run(processBuilder, false);
        Pattern usbIdPattern = Pattern.compile("ID ([0-9a-fA-F]{4}:[0-9a-fA-F]{4})");
        BufferedReader reader = new BufferedReader(new StringReader(processResult.getOutput()));
        final List usbIds = new ArrayList();
        do
        {
            String line;
            if((line = reader.readLine()) == null)
                break;
            Matcher matcher = usbIdPattern.matcher(line);
            if(matcher.matches())
                usbIds.add(matcher.group(1));
        } while(true);
        Predicate predicate = new Predicate() {

            public boolean evaluate(DeviceBean deviceBean)
            {
                return usbIds.contains(deviceBean.getDeviceId());
            }

            public volatile boolean evaluate(Object x0)
            {
                return evaluate((DeviceBean)x0);
            }

            final List val$usbIds;
            final ConnectedDeviceFactoryImpl this$0;

            
            {
                this$0 = ConnectedDeviceFactoryImpl.this;
                usbIds = list;
                super();
            }
        };
        SortedSet deviceBeans = (SortedSet)CollectionUtils.select(getDeviceDao().getAll(), predicate, new TreeSet());
        Transformer transformer = new Transformer() {

            public ConnectedDevice transform(DeviceBean deviceBean)
            {
                return (ConnectedDevice)deviceBean.accept(ConnectedDeviceFactoryImpl.this);
            }

            public volatile Object transform(Object x0)
            {
                return transform((DeviceBean)x0);
            }

            final ConnectedDeviceFactoryImpl this$0;

            
            {
                this$0 = ConnectedDeviceFactoryImpl.this;
                super();
            }
        };
        return (SortedSet)CollectionUtils.collect(deviceBeans, transformer, new TreeSet());
    }

    public ConnectedDevice visit(IpodDeviceBean ipodDeviceBean)
    {
        return new IpodConnectedDevice(ipodDeviceBean);
    }

    public ConnectedDevice visit(MtpDeviceBean mtpDeviceBean)
    {
        return new MtpConnectedDevice(mtpDeviceBean);
    }

    public ConnectedDevice visit(FileSystemDeviceBean fileSystemDeviceBean)
    {
        return new FileSystemConnectedDevice(fileSystemDeviceBean);
    }

    public String[] getRequiredPackageNames()
    {
        return (new String[] {
            "usbutils"
        });
    }

    public ProcessService getProcessService()
    {
        return i_processService;
    }

    public void setProcessService(ProcessService processService)
    {
        i_processService = processService;
    }

    public DeviceDao getDeviceDao()
    {
        return i_deviceDao;
    }

    public void setDeviceDao(DeviceDao deviceDao)
    {
        i_deviceDao = deviceDao;
    }

    public volatile Object visit(FileSystemDeviceBean x0)
    {
        return visit(x0);
    }

    public volatile Object visit(MtpDeviceBean x0)
    {
        return visit(x0);
    }

    public volatile Object visit(IpodDeviceBean x0)
    {
        return visit(x0);
    }

    private ProcessService i_processService;
    private DeviceDao i_deviceDao;
}
