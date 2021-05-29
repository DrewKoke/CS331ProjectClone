package asg.concert.service.services;

import asg.concert.common.dto.BookingDTO;
import asg.concert.common.dto.BookingRequestDTO;
import asg.concert.service.domain.Bookings;
import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Seat;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import asg.concert.service.util.CookieUtil;
import asg.concert.service.domain.Users;

@Path("/concert-service/bookings")
public class BookingResource {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBookings(@CookieParam("auth") Cookie authCookie) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		if (authCookie == null) {
			return Response.status(401).build();
		}

		String[] userInfo = CookieUtil.decodeCookie(authCookie);
		if (userInfo[0].equalsIgnoreCase("Unauthorized")) {
			return Response.status(403).build();
		}
		List<BookingDTO> bookingDTOs = new ArrayList<>();

		try {
			em.getTransaction().begin();
			for (Object bookings : em.createQuery("select b from Bookings b where b.user.username = '" + userInfo[0]
					+ "' and b.user.password = '" + userInfo[1] + "'").getResultList()) {
				bookingDTOs.add(((Bookings) bookings).convertToDTO());
			}
			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}

		return Response.ok(bookingDTOs).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response getBooking(@CookieParam("auth") Cookie authCookie, @PathParam(value = "id") Long id) {
		EntityManager em = PersistenceManager.instance().createEntityManager();

		String[] userInfo = CookieUtil.decodeCookie(authCookie);

		if (userInfo[0].equalsIgnoreCase("Unauthorized")) {
			return Response.status(403).build();
		}
		List<Users> users = em.createQuery(
				"select u from Users u where u.username = '" + userInfo[0] + "' and u.password = '" + userInfo[1] + "'")
				.getResultList();
		if (users.isEmpty()) {
			return Response.status(401).build();
		}
		Users user = users.get(0);
		Bookings booking;

		try {
			em.getTransaction().begin();
			booking = em.find(Bookings.class, id);

			if (booking == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			if (booking.getUser() != user) {
				return Response.status(403).build();
			}

			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}

		return Response.ok(booking.convertToDTO()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBooking(BookingRequestDTO bookingRequest, @CookieParam("auth") Cookie authCookie) {
		EntityManager em = PersistenceManager.instance().createEntityManager();

		if (bookingRequest.getSeatLabels().isEmpty()) {
			return Response.notModified().build();
		}

		long concertId = bookingRequest.getConcertId();
		long bookingId;

		try {
			em.getTransaction().begin();
			Concert concert = em.find(Concert.class, concertId);

			if (concert == null) {
				return Response.status(400).build();
			}

			if (!concert.getDates().contains(bookingRequest.getDate())) {
				return Response.status(400).build();
			}

			List<Seat> updateSeats = new ArrayList<>();
			List<Bookings> bookings = em.createQuery("select b from Bookings b").getResultList();
			if (authCookie == null) {
				return Response.status(401).build();
			}
			String[] userInfo = CookieUtil.decodeCookie(authCookie);

			if (userInfo[0].equalsIgnoreCase("Unauthorized")) {
				return Response.status(403).build();
			}

			List<Users> users = em.createQuery("select u from Users u where u.username = '" + userInfo[0]
					+ "' and u.password = '" + userInfo[1] + "'").getResultList();
			if (users.isEmpty()) {

				return Response.status(401).build();
			}

			Users user = users.get(0);
			List<Seat> seats = em.createQuery("select s from Seat s where s.date = '" + bookingRequest.getDate() + "'")
					.getResultList();
			for (String label : bookingRequest.getSeatLabels()) {
				Seat seat = seats.stream().filter(s -> s.getLabel().equals(label)).findFirst().orElse(null);
				if (seat == null) {
					return Response.status(404).build();
				}

				if (seat.isBooked() == true) {
					return Response.status(403).build();
				}

				seat.setBooked(true);
				updateSeats.add(seat);
			}
			for (Seat changeSeat : updateSeats) {
				em.merge(changeSeat);
			}

			Bookings booking = new Bookings(bookingRequest.getConcertId(), bookingRequest.getDate(), updateSeats, user);
			em.persist(booking);
			bookingId = booking.getId();
			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
			SubscribeResource.newbooking(bookingRequest);
		}
		Response.ResponseBuilder response = Response
				.created(URI.create("http://localhost:10000/services/concert-service/bookings/" + bookingId));
		return response.build();
	}
}
