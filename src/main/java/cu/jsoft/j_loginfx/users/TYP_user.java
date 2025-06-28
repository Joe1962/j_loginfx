/*
 * Copyright Joe1962
 */
package cu.jsoft.j_loginfx.users;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Joe1962
 */
public class TYP_user {
	private UUID uuid;
	private String name;
	private String password;
	private boolean admin;

	public TYP_user() {
	}

	public TYP_user(UUID uuid, String name, String password, boolean admin) {
		this.uuid = uuid;
		this.name = name;
		this.password = password;
		this.admin = admin;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.uuid);
		hash = 37 * hash + Objects.hashCode(this.name);
		hash = 37 * hash + Objects.hashCode(this.password);
		hash = 37 * hash + (this.admin ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TYP_user other = (TYP_user) obj;
		if (this.admin != other.admin) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.password, other.password)) {
			return false;
		}
		return Objects.equals(this.uuid, other.uuid);
	}

}
