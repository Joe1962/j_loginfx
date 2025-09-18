/*
 * Copyright Joe1962
 * https://github.com/Joe1962
 */
package cu.jsoft.j_loginfx;

import static cu.jsoft.j_utilsfx.global.CONSTS.EMPTY_STRING;

/**
 *
 * @author Joe1962
 */
public class TYP_RetDLG_LoginFX {

	private int id;
	private String name = EMPTY_STRING;
	private boolean admin = false;
	private boolean valid = false;
	private boolean butOK = false;
	private boolean butDB = false;
	private boolean butExit = false;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the butOK
	 */
	public boolean isButOK() {
		return butOK;
	}

	/**
	 * @param butOK the butOK to set
	 */
	public void setButOK(boolean butOK) {
		this.butOK = butOK;
	}

	public boolean isButDB() {
		return butDB;
	}

	public void setButDB(boolean butDB) {
		this.butDB = butDB;
	}

	public boolean isButExit() {
		return butExit;
	}

	public void setButExit(boolean butExit) {
		this.butExit = butExit;
	}

}
