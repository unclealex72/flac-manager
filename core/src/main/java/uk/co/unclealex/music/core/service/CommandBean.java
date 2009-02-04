package uk.co.unclealex.music.core.service;

public abstract class CommandBean<E extends CommandBean<E>> implements Comparable<E> {

	public abstract boolean isEndOfWorkBean();

}