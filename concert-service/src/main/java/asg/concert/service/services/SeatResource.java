package asg.concert.service.services;

import javax.persistence.EntityManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import asg.concert.common.dto.SeatDTO;
import asg.concert.common.types.BookingStatus;
import asg.concert.service.domain.*;
import asg.concert.service.jaxrs.LocalDateTimeParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/concert-service/seats/{date}")
public class SeatResource {
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSeats(@PathParam("date") LocalDateTimeParam date, @QueryParam("status") String status) {

		EntityManager em = PersistenceManager.instance().createEntityManager();
		List<Seat> query;
		try {
			em.getTransaction().begin();
			LocalDateTime parsedDate = date.getLocalDateTime();

			switch (BookingStatus.valueOf(status)) {
			case Any:
				query = em.createQuery("select s from Seat s where s.date = '" + parsedDate + "'").getResultList();
				break;
			case Booked:
				query = em.createQuery("select s from Seat s where s.isBooked = true and s.date = '" + parsedDate + "'")
						.getResultList();
				break;
			case Unbooked:
				query = em
						.createQuery("select s from Seat s where s.isBooked = false and s.date = '" + parsedDate + "'")
						.getResultList();
				break;
			default:
				return Response.status(400).build();
			}

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}

		List<SeatDTO> converted = new ArrayList<>();

		for (Seat result : query) {
			converted.add(result.convertToSeatDTO());
		}
		return Response.ok().entity(converted).build();
	}
}
