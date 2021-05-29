package asg.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import asg.concert.common.dto.SeatDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SEAT")
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String label;
	private boolean isBooked;
	private LocalDateTime date;
	private BigDecimal price;
	@Version
	private Long version;

	public Seat() {
	}

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public boolean isBooked() {
		return isBooked;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getLabel() {
		return label;
	}

	public void setBooked(boolean booked) {
		isBooked = booked;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public SeatDTO convertToSeatDTO() {
		SeatDTO seatDTO = new SeatDTO();
		seatDTO.setLabel(this.getLabel());
		seatDTO.setPrice(this.getPrice());

		return seatDTO;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;
		if (obj == this)
			return true;

		Seat rhs = (Seat) obj;
		return new EqualsBuilder().append(label, rhs.label).isEquals();
	}

	@Override
	public int hashCode() {
		// Hash-code value is derived from the value of the title field. It's
		// good practice for the hash code to be generated based on a value
		// that doesn't change.
		return new HashCodeBuilder(17, 31).append(label).hashCode();
	}

	SeatDTO convertToSeat() {
		return new SeatDTO(label, price);
	}
}
