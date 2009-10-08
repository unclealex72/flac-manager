package uk.co.unclealex.music.base.model;

import java.util.SortedSet;

public interface OwnedEncodedBean extends EncodedBean {

	public SortedSet<OwnerBean> getOwnerBeans();
	public void setOwnerBeans(SortedSet<OwnerBean> ownerBeans);
}
