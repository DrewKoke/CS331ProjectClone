package asg.concert.service.domain;

import java.time.LocalDateTime;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.ConcertSummaryDTO;
import asg.concert.common.dto.PerformerDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "CONCERTS")
@Proxy(lazy = false)
public class Concert implements Comparable<Concert> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	@Column(name = "image_name")
	private String imageName;
	@Column(length = 1000)
	private String blurb;
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "DATE")
	private List<LocalDateTime> dates = new ArrayList<>();
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "CONCERT_PERFORMER", joinColumns = { @JoinColumn(name = "CONCERT_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "PERFORMER_ID") })
	private List<Performer> performers = new ArrayList<>();
	@Version
	private Long version;

	public Concert() {
	}

	public Concert(Long id, String title, String imageName, String blurb) {
		this.id = id;
		this.title = title;
		this.imageName = imageName;
		this.blurb = blurb;
	}

	public Concert(String title, String imageName, String blurb) {
		this.title = title;
		this.imageName = imageName;
		this.blurb = blurb;
	}

	public Concert(String title, String imageName) {
		this.title = title;
		this.imageName = imageName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}

	public List<LocalDateTime> getDates() {
		return dates;
	}

	public void setDates(List<LocalDateTime> dates) {
		this.dates = dates;
	}

	public List<Performer> getPerformers() {
		return performers;
	}

	public void setPerformers(List<Performer> performers) {
		this.performers = performers;
	}

	@Override
	public boolean equals(Object obj) {
		// Implement value-equality based on a Concert's title alone. ID isn't
		// included in the equality check because two Concert objects could
		// represent the same real-world Concert, where one is stored in the
		// database (and therefore has an ID - a primary key) and the other
		// doesn't (it exists only in memory).
		if (!(obj instanceof Concert))
			return false;
		if (obj == this)
			return true;

		Concert rhs = (Concert) obj;
		return new EqualsBuilder().append(title, rhs.title).isEquals();
	}

	@Override
	public int hashCode() {
		// Hash-code value is derived from the value of the title field. It's
		// good practice for the hash code to be generated based on a value
		// that doesn't change.
		return new HashCodeBuilder(17, 31).append(title).hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return title.compareTo(concert.getTitle());
	}

	public ConcertDTO convertToConcertDTO() {
		ConcertDTO concertDTO = new ConcertDTO();
		concertDTO.setId(this.getId());
		concertDTO.setTitle(this.getTitle());
		concertDTO.setBlurb(this.getBlurb());
		concertDTO.setDates(this.getDates());
		concertDTO.setImageName(this.getImageName());
		List<PerformerDTO> performerDTOs = new ArrayList<>();
		for (Performer performer : this.performers) {
			performerDTOs.add(performer.convertToDTO());
		}
		concertDTO.setPerformers(performerDTOs);

		return concertDTO;
	}

	public ConcertSummaryDTO convertToSummaryDTO() {
		ConcertSummaryDTO summaryDTO = new ConcertSummaryDTO();
		summaryDTO.setId(this.getId());
		summaryDTO.setTitle(this.getTitle());
		summaryDTO.setImageName(this.getImageName());

		return summaryDTO;
	}

}
