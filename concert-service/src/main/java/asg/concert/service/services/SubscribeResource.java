package asg.concert.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import asg.concert.common.dto.BookingRequestDTO;
import asg.concert.common.dto.ConcertInfoNotificationDTO;
import asg.concert.common.dto.ConcertInfoSubscriptionDTO;
import asg.concert.service.domain.*;

@Path("/concert-service/subscribe/concertInfo")
public class SubscribeResource {

	protected static Map<Integer, AsyncResponse> responseObjects = new HashMap<>();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void createSubscrption(ConcertInfoSubscriptionDTO concertsub, @CookieParam("auth") Cookie authCookie,
			@Suspended AsyncResponse response) {

		EntityManager em = PersistenceManager.instance().createEntityManager();

		if (authCookie == null) {
			response.resume(Response.status(401).build());
		} else {
			try {
				em.getTransaction().begin();

				Concert concert = em.find(Concert.class, concertsub.getConcertId());

				if (concert == null) {
					response.resume(Response.status(400).build());
					return;
				}

				if (!concert.getDates().contains(concertsub.getDate())) {
					response.resume(Response.status(400).build());
					return;
				}
				Subscriptions subsciption = new Subscriptions(concertsub.getConcertId(), concertsub.getDate(),
						concertsub.getPercentageBooked());
				em.persist(subsciption);
				em.flush();
				responseObjects.put(subsciption.getId().intValue(), response);
				em.getTransaction().commit();
			} catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} finally {
				em.close();
			}
		}
	}

	public static void newbooking(BookingRequestDTO concertInfo) {
		if (!responseObjects.isEmpty()) {
			EntityManager em = PersistenceManager.instance().createEntityManager();
			try {
				em.getTransaction().begin();

				int bookedSeats = em.createQuery(
						"select s from Seat s where s.isBooked = true and s.date = '" + concertInfo.getDate() + "'")
						.getResultList().size();
				List<Subscriptions> subsToCheck = em
						.createQuery("select s from Subscriptions s where s.date = '" + concertInfo.getDate() + "'",
								Subscriptions.class)
						.getResultList();

				for (Subscriptions sub : subsToCheck) {
					int numOfSeatsToAlert = (120 * sub.getPercentBooked()) / 100;
					if (numOfSeatsToAlert < bookedSeats) {
						ConcertInfoNotificationDTO notificationDTO = new ConcertInfoNotificationDTO(120 - bookedSeats);
						responseObjects.get(sub.getId().intValue()).resume(notificationDTO);

					}
				}

			} catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} finally {
				em.close();
			}
		}
	}
}
