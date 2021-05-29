package asg.concert.service.services;

import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Performer;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/concert-service/performers")
public class PerformerResource {

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformer(@PathParam("id") long id) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		Performer found;
		try {
			em.getTransaction().begin();
			found = em.find(Performer.class, id);
			em.getTransaction().commit();

		}  catch(Exception e) {
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
				}
			return Response.status(500).build();
		} finally {
			em.close();
		}
		if(found == null) {
			return Response.status(404).build();
		}
		else {
			return Response.ok().entity(found.convertToDTO()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformers() {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Performer> query = em.createQuery("select p from Performer p", Performer.class);
			List<Performer> results = query.getResultList();
			List<PerformerDTO> performers = new ArrayList<>();
			for(Performer result: results) {
				performers.add(result.convertToDTO());
			}
			return Response.ok().entity(performers).build();

		}  catch(Exception e) {
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
				}
			return Response.status(500).build();
		} finally {
			em.close();
		}
	}

}

