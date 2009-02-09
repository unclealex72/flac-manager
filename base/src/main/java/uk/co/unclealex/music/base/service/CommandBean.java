package uk.co.unclealex.music.base.service;

public abstract class CommandBean<E extends CommandBean<E>> implements Comparable<E> {

	public abstract boolean isEndOfWorkBean();

}