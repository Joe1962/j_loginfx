/*
 * Copyright Joe1962
 */
package cu.jsoft.j_loginfx.users;



/**
 *
 * @author joe1962
 */
public class CLS_SQLStrings {

	public String getSQLSelectAll() {
		return "SELECT uuid, "
			+ "  name, "
			+ "  admin, "
			+ "  pbkdf2_pass, "
			+ "  pbkdf2_salt "
			+ "FROM "
			+ "  sys_users ";
	}

	public String getSQLSelectByPK() {
		return "SELECT uuid, "
			+ "  name, "
			+ "  admin, "
			+ "  pbkdf2_pass, "
			+ "  pbkdf2_salt "
			+ "FROM "
			+ "  sys_users "
			+ "WHERE "
			+ "  uuid = ? ";
	}

	public String getSQLSelectByAdminState() {
		return "SELECT "
			+ "  uuid, "
			+ "  name, "
			+ "  admin, "
			+ "  pbkdf2_pass, "
			+ "  pbkdf2_salt "
			+ "FROM "
			+ "  sys_users "
			+ "WHERE "
			+ "  admin = true, "
			+ "ORDER BY ? ";
	}
}
