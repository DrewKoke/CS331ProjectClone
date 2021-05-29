package asg.concert.service.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "SUBSCRIPTIONS")
@Proxy(lazy = false)
public class Subscriptions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long concertId;
	private LocalDateTime date;
	private int percentageBooked;

	protected Subscriptions() {
	}

	public Subscriptions(Long concertId, LocalDateTime date, int percentageBooked) {
		this.concertId = concertId;
		this.date = date;
		this.percentageBooked = percentageBooked;
	}

	public Long getId() {
		return id;
	}

	public Long getConcertId() {
		return concertId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getPercentBooked() {
		return percentageBooked;
	}

}
