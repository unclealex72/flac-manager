package uk.co.unclealex.flacconverter.encoded.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

@Table(name="owned_artists")
@Entity
public class OwnedArtistBean extends KeyedBean<OwnedArtistBean> {

	private String i_name;
	private OwnerBean i_ownerBean;
	
	@Override
	public int compareTo(OwnedArtistBean o) {
		int cmp = getOwnerBean().compareTo(o.getOwnerBean());
		if (cmp != 0) { return cmp; }
		cmp = getName().compareTo(o.getName());
		return cmp;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return "(" + getOwnerBean().getName() + ":" + getName() + ")";
	}


	@ManyToOne
	public OwnerBean getOwnerBean() {
		return i_ownerBean;
	}

	public void setOwnerBean(OwnerBean ownerBean) {
		i_ownerBean = ownerBean;
	}

	@NotEmpty(message="You must supply an artist.")
	@Lob
	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}
	
}
