// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SpringSynchroniserFactory.java

package uk.co.unclealex.music.sync.service;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import uk.co.unclealex.music.base.service.*;
import uk.co.unclealex.music.base.visitor.ConnectedDeviceVisitor;

public class SpringSynchroniserFactory extends ConnectedDeviceVisitor
    implements SynchroniserFactory, BeanFactoryAware
{

    public SpringSynchroniserFactory()
    {
    }

    public Synchroniser createSynchroniser(ConnectedDevice connectedDevice, SynchroniserCallback synchroniserCallback)
    {
        String beanName = (String)connectedDevice.accept(this);
        Synchroniser synchroniser = (Synchroniser)getBeanFactory().getBean(beanName);
        synchroniser.postConstruct(connectedDevice, synchroniserCallback);
        return synchroniser;
    }

    public String visit(MtpConnectedDevice mtpConnectedDevice)
    {
        return "mtpSynchroniser";
    }

    public String visit(IpodConnectedDevice ipodConnectedDevice)
    {
        return "ipodSynchroniser";
    }

    public String visit(FileSystemConnectedDevice fileSystemConnectedDevice)
    {
        return "fileSystemSynchroniser";
    }

    public BeanFactory getBeanFactory()
    {
        return i_beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory)
    {
        i_beanFactory = beanFactory;
    }

    public volatile Object visit(FileSystemConnectedDevice x0)
    {
        return visit(x0);
    }

    public volatile Object visit(IpodConnectedDevice x0)
    {
        return visit(x0);
    }

    public volatile Object visit(MtpConnectedDevice x0)
    {
        return visit(x0);
    }

    private BeanFactory i_beanFactory;
}
