/*
 * Copyright Joe1962
 * https://github.com/Joe1962
 */
package cu.jsoft.j_loginfx.global;

/**
 *
 * @author joe1962
 */
public class FLAGS extends cu.jsoft.j_utilsfxlite.global.FLAGS {
	
	private static boolean LOGGEDIN;

	/**
	 * @return the LOGGEDIN
	 */
	public static boolean isLOGGEDIN() {
		return LOGGEDIN;
	}

	/**
	 * @param aLOGGEDIN the LOGGEDIN to set
	 */
	public static void setLOGGEDIN(boolean aLOGGEDIN) {
		LOGGEDIN = aLOGGEDIN;
	}

}
