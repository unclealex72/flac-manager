package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

@Service
@Transactional(rollbackFor=IOException.class)
public class AlbumThumbnailDataManager extends AbstractAlbumDataManager {

	@Override
	protected DataBean getDataBean(AlbumCoverBean keyedBean) {
		return keyedBean.getThumbnailDataBean();
	}

	@Override
	protected void setDataBean(AlbumCoverBean keyedBean, DataBean dataBean) {
		keyedBean.setThumbnailDataBean(dataBean);
	}

	
}
