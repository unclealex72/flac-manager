// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SpringSynchroniserFactory.java

package uk.co.unclealex.music.sync;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import uk.co.unclealex.music.Device;
import uk.co.unclealex.music.DeviceVisitor;
import uk.co.unclealex.music.FileSystemDevice;
import uk.co.unclealex.music.IpodDevice;
import uk.co.unclealex.music.MtpDevice;

public class SpringSynchroniserFactory implements DeviceVisitor<Synchroniser>, SynchroniserFactory, BeanFactoryAware {

	private BeanFactory i_beanFactory;
	private String i_mtpSynchroniserBeanName;
	private String i_ipodSynchroniserBeanName;
	private String i_filesystemSynchroniserBeanName;
	
	public Synchroniser createSynchroniser(Device device) {
		return device.accept(this);
	}

	@SuppressWarnings("unchecked")
	protected <D extends Device> Synchroniser create(String beanName, D device) {
		AbstractSynchroniser<D> synchroniser = (AbstractSynchroniser<D>) getBeanFactory().getBean(beanName);
		synchroniser.postConstruct(device);
		return synchroniser;	
	}
	
	public Synchroniser visit(MtpDevice mtpDevice) {
		return create(getMtpSynchroniserBeanName(), mtpDevice);
	}

	public Synchroniser visit(IpodDevice ipodDevice) {
		return create(getIpodSynchroniserBeanName(), ipodDevice);
	}

	public Synchroniser visit(FileSystemDevice fileSystemDevice) {
		return create(getFilesystemSynchroniserBeanName(), fileSystemDevice);
	}

	public BeanFactory getBeanFactory() {
		return i_beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		i_beanFactory = beanFactory;
	}

	public String getMtpSynchroniserBeanName() {
		return i_mtpSynchroniserBeanName;
	}

	public void setMtpSynchroniserBeanName(String mtpSynchroniserBeanName) {
		i_mtpSynchroniserBeanName = mtpSynchroniserBeanName;
	}

	public String getIpodSynchroniserBeanName() {
		return i_ipodSynchroniserBeanName;
	}

	public void setIpodSynchroniserBeanName(String ipodSynchroniserBeanName) {
		i_ipodSynchroniserBeanName = ipodSynchroniserBeanName;
	}

	public String getFilesystemSynchroniserBeanName() {
		return i_filesystemSynchroniserBeanName;
	}

	public void setFilesystemSynchroniserBeanName(String filesystemSynchroniserBeanName) {
		i_filesystemSynchroniserBeanName = filesystemSynchroniserBeanName;
	}
}
