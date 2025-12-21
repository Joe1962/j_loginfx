/*
 * Copyright Joe1962
 * https://github.com/Joe1962
 */
package cu.jsoft.j_loginfx.users;

import cu.jsoft.j_dbfx.RS;
import cu.jsoft.j_utilsfx.global.FLAGS;
import cu.jsoft.j_utilsfx.security.SUB_Protect;
import static cu.jsoft.j_utilsfx.subs.SUB_UtilsNotifications.echoClassMethodComment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * @author Joe1962
 *
 * This class should encapsulate all work with the sys_users table.
 *
 */
public class RS_users extends RS {
	private UUID MyID;

	public RS_users() {
		super();

		setDbTableName("sys_users");
//		setDbTableFull(DBSchema + "." + "public.sys_users");
		setDbTableFull("public" + "." + "public.sys_users");

		SQLSelectAll = "SELECT uuid, name, password, admin FROM public.sys_users ";
		SQLSelectByID = "SELECT uuid, name, password, admin FROM public.sys_users WHERE uuid = ? ";
	}

	public int CountUsers() throws SQLException {
		String SQL = "SELECT COUNT(name) FROM DBTABLE;";
		return Count(SQL, getDbTableFull());
	}

	public void selectByAdminState(boolean IsAdmin, String OrderByString) throws SQLException {
		String QuerySQL = "SELECT uuid, name, password, admin FROM sys_users WHERE admin = true " + OrderByString;
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		setRST(getDBConnHandler().doQuery(pstmt));
		getRST().first();
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
	}

	public UUID getIDByName(String MyCashier) throws SQLException {
		// This uses a temp ResultSet...
		ResultSet MyRST;

		String QuerySQL = "SELECT uuid FROM sys_users WHERE name=? ";
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		int n = 1;
		pstmt.setString(n++, MyCashier);
		MyRST = getDBConnHandler().doQuery(pstmt);
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
		MyRST.first();
		MyRST.last();
		if (MyRST.getRow() > 0) {
			return (UUID) MyRST.getObject("uuid");
		} else {
			return null;
		}
	}

	public void selectByName(String MyName) throws SQLException {
		String QuerySQL = "SELECT uuid, name, password, admin FROM sys_users WHERE name = ? ";
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		pstmt.setString(1, MyName);
		setRST(getDBConnHandler().doQuery(pstmt));
		getRST().first();
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
	}

	@Override
	public TYP_user getCurrent() throws SQLException {
		SUB_Protect Protection = new SUB_Protect();
		TYP_user MyRec = new TYP_user();

		if (getRST().getRow() > 0) {
			MyRec.setUuid((UUID) getRST().getObject("uuid"));
			MyRec.setName(getRST().getString("name"));
			MyRec.setPassword(getRST().getString("password"));
			MyRec.setAdmin(getRST().getBoolean("admin"));

			return MyRec;
		} else {
			return null;
		}
	}

	@Override
	public boolean appendRow(Object MyRow) throws SQLException {
		SUB_Protect Protection = new SUB_Protect();
		TYP_user MyRec = (TYP_user) MyRow;

		String QuerySQL = "INSERT INTO public.sys_users (name, password, admin) VALUES (?, ?, ?)";
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		int n = 1;
		pstmt.setString(n++, MyRec.getName());
		pstmt.setString(n++, (MyRec.getPassword()));
		pstmt.setBoolean(n++, MyRec.isAdmin());

		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
		return getDBConnHandler().doUpdate(pstmt) > 0;
	}

	@Override
	public boolean updateRow(Object MyRow, Object WhereParam) throws SQLException {
		SUB_Protect Protection = new SUB_Protect();
		TYP_user MyRec = (TYP_user) MyRow;

		String QuerySQL = "UPDATE sys_users SET name=?, password=?, admin=? WHERE " + (String) WhereParam;
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		int n = 1;
		pstmt.setString(n++, MyRec.getName());
		pstmt.setString(n++, (MyRec.getPassword()));
		pstmt.setBoolean(n++, MyRec.isAdmin());

		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
		return getDBConnHandler().doUpdate(pstmt) > 0;
	}

	public boolean isAdminByname(String MyName) throws SQLException {
		String QuerySQL = "SELECT admin FROM sys_users WHERE name = ? ";
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		pstmt.setString(1, MyName);
		setRST(getDBConnHandler().doQuery(pstmt));
		getRST().first();
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...

		if (getRST().getRow() > 0) {
			return getRST().getBoolean("admin");
		} else {
			return false;
		}
	}

	/**
	 * @return the MyID
	 */
	public UUID getUserID() {
		return MyID;
	}

	/**
	 * @param MyUserID
	 */
	public void setUserID(UUID MyUserID) {
		this.MyID = MyUserID;
	}


}
