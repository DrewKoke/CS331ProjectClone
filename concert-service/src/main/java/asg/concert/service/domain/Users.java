package asg.concert.service.domain;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "USERS")
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	@Version
	private Long version;

	protected Users() {
	}

	public Users(Long id, String username, String password, Long version) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.version = version;
	}

	public Users(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Long getID() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Users users = (Users) o;

		return new EqualsBuilder().append(username, users.username).append(password, users.password).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(username).append(password).toHashCode();
	}

	@Override
	public String toString() {
		return "UserDTO{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
	}

}
