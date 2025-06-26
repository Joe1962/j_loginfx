/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.jsoft.j_loginfx.users;

import cu.jsoft.j_dbfxlite.RS;
import cu.jsoft.j_utilsfxlite.global.FLAGS;
import cu.jsoft.j_utilsfxlite.security.SUB_Protect;
import static cu.jsoft.j_utilsfxlite.subs.SUB_UtilsNotifications.echoClassMethodComment;
import java.math.BigInteger;
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
	String dbTable = "public.sys_users";
	private int MyID;

	public RS_users() {
		super();
		SQLSelectAll = "SELECT uuid, name, password, admin FROM public.sys_users ";
		SQLSelectByPK = "SELECT uuid, name, password, admin FROM public.sys_users WHERE uuid = ? ";
	}

	public int CountUsers() throws SQLException {
		String SQL = "SELECT COUNT(name) FROM DBTABLE;";
		return Count(SQL, dbTable);
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

	@Override
	public void selectByDate(String OrderByString, Object MyMaster) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public void selectByPK(Object MyPK) throws SQLException {
		// Remember to setUserID(String MyID) first...
		String QuerySQL = SQLSelectByPK;
		PreparedStatement pstmt;
		pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		pstmt.setObject(1, (UUID) MyPK);
		setRST(getDBConnHandler().doQuery(pstmt));
		getRST().first();
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
	}

	@Override
	public void selectByMaster(String OrderByString, Object MyMaster) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public void selectByNameByActiveState(String MyName, String OrderByString, Object ActiveState) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
	public int deleteRow() throws SQLException {
		// Delete single record:
		String QuerySQL = "DELETE FROM sys_users WHERE uuid = ? ";
		PreparedStatement pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		int n = 1;
		pstmt.setInt(n++, MyID);
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
		return getDBConnHandler().doUpdate(pstmt);
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

	@Override
	public boolean deleteRowByID(int MyID) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public boolean deleteRowByID(String MyID) throws SQLException {
		// Delete single record:
		String QuerySQL = "DELETE FROM sys_users WHERE name = ? ";
		PreparedStatement pstmt = getMyConn().prepareStatement(QuerySQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		int n = 1;
		pstmt.setString(n++, MyID);
		echoClassMethodComment(pstmt.toString(), FLAGS.isDEBUG(), false);			// DEBUG...
		return getDBConnHandler().doUpdate(pstmt) > 0;
	}

	@Override
	public int deleteRowByID(BigInteger MyID) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public int deleteRowsByZeroQuant() throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int deleteByMaster() throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * @return the MyID
	 */
	public int getUserID() {
		return MyID;
	}

	/**
	 * @param MyUserID
	 */
	public void setUserID(int MyUserID) {
		this.MyID = MyUserID;
	}

}
