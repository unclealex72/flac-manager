package uk.co.unclealex.flacconverter.model;

import junit.framework.TestCase;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;

public class KeyedBeanTest extends TestCase {

	public void testEquality() {
		DeviceBean deviceBean = new DeviceBean();
		OwnerBean ownerBean = new OwnerBean();
		deviceBean.setId(1);
		ownerBean.setId(1);
		assertEquals("Equality is not reflexive.", deviceBean, deviceBean);
		assertFalse("Two different classes should not be equal.", deviceBean.equals(ownerBean));
	}
}
