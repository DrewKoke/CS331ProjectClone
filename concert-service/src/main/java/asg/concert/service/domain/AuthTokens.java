package asg.concert.service.domain;

import javax.persistence.*;

@Entity
@Table(name = "AUTHTOKENS")
public class AuthTokens {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	private Long userId;

	public AuthTokens(Long id, String token, Long userId) {
		this.id = id;
		this.token = token;
		this.userId = userId;
	}

	public AuthTokens(String token, Long userId) {
		this.token = token;
		this.userId = userId;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getUser() {
		return this.userId;
	}

	public void setUser(Long user) {
		this.userId = user;
	}

}
