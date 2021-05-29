package asg.concert.service.services;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.ConcertSummaryDTO;
import asg.concert.service.domain.*;

import java.util.ArrayList;
import java.util.List;

@Path("/concert-service/concerts")
public class ConcertResource {

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConcert(@PathParam("id") long id) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		Concert found;
		try {
			em.getTransaction().begin();
			found = em.find(Concert.class, id);
			em.getTransaction().commit();

			if (found == null) {
				return Response.status(404).build();
			} else {
				return Response.ok().entity(found.convertToConcertDTO()).build();
			}

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConcerts() {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
			List<Concert> results = query.getResultList();
			List<ConcertDTO> concerts = new ArrayList<>();
			for (Concert result : results) {
				concerts.add(result.convertToConcertDTO());
			}
			return Response.ok().entity(concerts).build();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/summaries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConcertSummaries() {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
			List<Concert> results = query.getResultList();
			List<ConcertSummaryDTO> concerts = new ArrayList<ConcertSummaryDTO>();
			for (Concert result : results) {
				concerts.add(result.convertToSummaryDTO());
			}
			return Response.ok().entity(concerts).build();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAll() {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
			List<Concert> removals = concertQuery.getResultList();
			removals.forEach((concert) -> {
				em.remove(concert);
			});

			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
		return Response.status(204).build();

	}

	@DELETE
	@Path("{id}")
	public Response deleteConcert(@PathParam("id") long id) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		Boolean found = false;
		try {
			em.getTransaction().begin();
			Concert test = em.find(Concert.class, id);
			if (test != null) {
				em.remove(test);
				found = true;

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
		if (found) {
			return Response.status(204).build();
		}
		return Response.status(404).build();

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createConcert(Concert concertEntity) {
		EntityManager em = PersistenceManager.instance().createEntityManager();

		try {
			em.getTransaction().begin();
			em.persist(concertEntity);
			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
		Response.ResponseBuilder reponce = Response.status(201).header("location",
				"http://localhost:10000/services/concerts/" + concertEntity.getId());
		return reponce.build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateConcert(Concert replacementConcert) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		Boolean created = false;
		try {
			em.getTransaction().begin();
			Concert test = em.find(Concert.class, replacementConcert.getId());
			if (test != null) {
				em.merge(replacementConcert);
				em.getTransaction().commit();
				created = true;
			}

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			return Response.status(500).build();
		} finally {
			em.close();
		}
		if (created == false) {
			return Response.status(404).build();
		}
		return Response.status(204).build();
	}

}
