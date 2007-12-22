package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class RootPathComponent extends AbstractPathComponent {

	private EncoderDao i_encoderDao;
	
	@Override
	public void accept(PathComponentVisitor pathComponentVisitor) {
		pathComponentVisitor.visit(this);
	}

	@Override
	public SortedSet<String> getChildren() {
		SortedSet<String> extensions = new TreeSet<String>();
		CollectionUtils.collect(
			getEncoderDao().getAll(),
			new Transformer<EncoderBean, String>() {
				@Override
				public String transform(EncoderBean encoderBean) {
					return encoderBean.getExtension();
				}
			},
			extensions);
		return extensions;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

}
