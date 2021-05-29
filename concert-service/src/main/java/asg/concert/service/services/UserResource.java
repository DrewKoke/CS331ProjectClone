package asg.concert.service.services;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import asg.concert.common.dto.UserDTO;
import asg.concert.service.domain.AuthTokens;
import asg.concert.service.domain.Users;

@Path("/concert-service/login")
public class UserResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkUser(UserDTO user) {
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try {
			em.getTransaction().begin();
			List<Users> query = em
					.createQuery("select u from Users u where u.username = '" + user.getUsername() + "'", Users.class)
					.getResultList();
			if (query.isEmpty()) {
				return Response.status(401).build();
			}
			Users dbUser = query.get(0);

			if (dbUser.getPassword().equals(user.getPassword())) {
				return Response.ok().cookie(makeCookie(dbUser)).build();
			} else {
				return Response.status(401).build();
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

	private NewCookie makeCookie(Users user) {

		EntityManager em = PersistenceManager.instance().createEntityManager();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String token = Base64.getEncoder()
				.encodeToString((user.hashCode() + user.getUsername() + timestamp.getTime()).getBytes());
		AuthTokens auth = new AuthTokens(token, user.getID());
		try {
			em.getTransaction().begin();
			em.persist(auth);
			em.getTransaction().commit();

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}

		return new NewCookie("auth", token);
	}
}
