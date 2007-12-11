package uk.co.unclealex.music.core.encoded.model;

import java.util.Formatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.log4j.Logger;

@Table(name="track_data",
		uniqueConstraints = {@UniqueConstraint(columnNames={"sequence", "encodedTrack"})})
@Entity
public class TrackDataBean extends KeyedBean<TrackDataBean> {

	private static long s_count = 0;
	private static long s_memory = 0;
	private static boolean s_logging = false;
	
	private boolean i_logged = false;
	private byte[] i_track;
	private EncodedTrackBean i_encodedTrackBean;
	private Integer i_sequence;
	
	public TrackDataBean() {
		super();
	}

	@Override
	public void setId(Integer id) {
		if (s_logging && id != null && !i_logged) { s_count++; i_logged = true; }
		super.setId(id);
	}
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		EncodedTrackBean encodedTrackBean = getEncodedTrackBean();
		return "{" + encodedTrackBean.getEncoderBean().getExtension() + ":" + encodedTrackBean.getFlacUrl() + "}";
	}
	
	@Override
	public int compareTo(TrackDataBean o) {
		int cmp = getEncodedTrackBean().compareTo(o.getEncodedTrackBean());
		return cmp == 0?getSequence().compareTo(o.getSequence()):cmp;
	}
	
	@Lob()
	@Column()
	public byte[] getTrack() {
		return i_track;
	}

	public void setTrack(byte[] track) {
		if (s_logging) {
			if (i_track != null) { s_memory -= i_track.length; }
			s_memory += track == null?0:track.length;
		}
		i_track = track;
	}

	@ManyToOne
	@JoinColumn(name="encodedTrack")
	public EncodedTrackBean getEncodedTrackBean() {
		return i_encodedTrackBean;
	}

	public void setEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		i_encodedTrackBean = encodedTrackBean;
	}

	public Integer getSequence() {
		return i_sequence;
	}

	public void setSequence(Integer sequence) {
		i_sequence = sequence;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (s_logging) {
			if (i_track != null) { s_memory -= i_track.length; }
			if (i_logged) { s_count--; }
			if (getId() != null) {
				Logger log = Logger.getLogger(getClass());
				Formatter formatter = new Formatter();
				formatter.format(
						"Finalising %s: %d; Memory %02.2f Mb, %d remaining",
						getClass().getName(), getId(), (double) s_memory / (double) (1024 * 1024), s_count);
				log.info(formatter.toString());
			}
		}
		super.finalize();
  }
}
