package uk.co.unclealex.music.web.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import uk.co.unclealex.music.base.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.KeyedBean;

public abstract class KeyedConverter<K extends KeyedBean<K>> extends StrutsTypeConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		return getDao().findById(new Integer(values[0]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map context, Object o) {
		Integer id = ((KeyedBean) o).getId();
		return id == null?null:id.toString();
	}

	protected abstract KeyedReadOnlyDao<K> getDao();
	
}
