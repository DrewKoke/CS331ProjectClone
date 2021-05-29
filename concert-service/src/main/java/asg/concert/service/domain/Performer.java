package asg.concert.service.domain;

import asg.concert.common.dto.PerformerDTO;
import asg.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.persistence.*;

/**
 * Class to represent a Performer (an artist or band that plays at Concerts). A
 * Performer object has an ID (a database primary key value), a name, the name
 * of an image file, and a genre.
 */
@Entity
@Table(name = "PERFORMERS")
public class Performer implements Comparable<Performer> {
	@Id
	private Long id;
	private String name;
	@Column(name = "image_name")
	private String imageName;
	@Enumerated(EnumType.STRING)
	private Genre genre;
	@Column(length = 1000)
	private String blurb;

	public Performer() {
	}

	public Performer(Long id, String name, String imageName, Genre genre, String blurb) {
		this.id = id;
		this.name = name;
		this.imageName = imageName;
		this.genre = genre;
		this.blurb = blurb;
	}

	public Performer(String name, String imageName, Genre genre, String blurb) {
		this.name = name;
		this.imageName = imageName;
		this.genre = genre;
		this.blurb = blurb;
	}

	public Performer(String name, String imageUri, Genre genre) {
		this(null, name, imageUri, genre, null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Performer that = (Performer) o;

		return new EqualsBuilder().append(id, that.id).append(name, that.name).append(imageName, that.imageName)
				.append(genre, that.genre).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).append(name).append(imageName).append(genre).toHashCode();
	}

	@Override
	public int compareTo(Performer other) {
		return other.getName().compareTo(getName());
	}

	public PerformerDTO convertToDTO() {
		PerformerDTO performerDTO = new PerformerDTO();
		performerDTO.setName(this.getName());
		performerDTO.setGenre(this.getGenre());
		performerDTO.setBlurb(this.getBlurb());
		performerDTO.setImageName(this.getImageName());
		performerDTO.setId(this.getId());
		return performerDTO;
	}
}