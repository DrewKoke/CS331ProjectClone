package asg.concert.service.domain;

import asg.concert.common.dto.BookingDTO;
import asg.concert.common.dto.SeatDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOKINGS")
@Proxy(lazy = false)
public class Bookings {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long concertId;
	private LocalDateTime date;
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<Seat> seats = new ArrayList<>();
	@ManyToOne(fetch = FetchType.LAZY)
	private Users user;

	protected Bookings() {
	}

	public Bookings(Long concertId, LocalDateTime date, List<Seat> seats, Users user) {
		this.concertId = concertId;
		this.date = date;
		this.seats = seats;
		this.user = user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public Long getConcertId() {
		return concertId;
	}

	public Long getId() {
		return id;
	}

	public Users getUser() {
		return user;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setConcertId(Long concertId) {
		this.concertId = concertId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Bookings bookings = (Bookings) o;

		return new EqualsBuilder().append(concertId, bookings.concertId).append(date, bookings.date)
				.append(seats, bookings.seats).append(user, bookings.user).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).append(concertId).append(date).append(seats).append(user)
				.toHashCode();
	}

	public BookingDTO convertToDTO() {
		List<SeatDTO> seatDTOs = new ArrayList<>();
		for (Seat seat : seats) {
			seatDTOs.add(seat.convertToSeat());
		}
		return new BookingDTO(concertId, date, seatDTOs);
	}
}
