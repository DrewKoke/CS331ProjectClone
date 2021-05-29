package asg.concert.service.util;

import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Cookie;

import asg.concert.service.domain.Users;
import asg.concert.service.services.PersistenceManager;

public class CookieUtil {

	public static String[] decodeCookie(Cookie cookie) {
		String[] userString = {"",""};
		EntityManager em = PersistenceManager.instance().createEntityManager();
		try{
			em.getTransaction().begin();
			List<Long> userIDList = em.createQuery("select userId from AuthTokens where token = '"+cookie.getValue()+"'").getResultList();
			Long userID = userIDList.get(0);
			if (userIDList.isEmpty()) {
				userString[0] = "Unauthorized";
				return userString;
			}
			Users user = em.find(Users.class, userID);
			userString[0] = user.getUsername();
			userString[1] = user.getPassword();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		return userString;
	}
}
