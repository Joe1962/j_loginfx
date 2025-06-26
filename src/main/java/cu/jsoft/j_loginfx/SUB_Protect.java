/*
 * Copyright Joe1962
 */
package cu.jsoft.j_loginfx;

import static cu.jsoft.j_dbfxlite.DBActions.DBStructCheck;
import cu.jsoft.j_dbfxlite.DBConnectionHandler;
import cu.jsoft.j_dbfxlite.types.TYP_DBStructCheck;
import cu.jsoft.j_loginfx.global.FLAGS;
import cu.jsoft.j_loginfx.users.RS_users;
import cu.jsoft.j_loginfx.users.TYP_user;
import cu.jsoft.j_loginfx.users.adduserController;
import cu.jsoft.j_utilsfxlite.security.CLS_AES_Utils;
import cu.jsoft.j_utilsfxlite.security.types.TYP_AES_Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 *
 * @author joe1962
 */
public class SUB_Protect {
	ArrayList<String> FailList = new ArrayList<>();

	public SUB_Protect() {
	}

	public String getEncryptedString(String toEncrypt, String theSalt, String theSecKey, byte[] theIV) {
		TYP_AES_Utils MyTyp = CLS_AES_Utils.strEncryptHelper(toEncrypt, theSalt, theSecKey, theIV);
		return MyTyp.getEncryptedStr();
	}

	public String getDecryptedString(String theEncKey, String theSecKey, byte[] theIV) {
		TYP_AES_Utils MyTyp = new TYP_AES_Utils();
		MyTyp.setSecKeyStr(theSecKey);
		MyTyp.setIv(theIV);
		MyTyp.setEncryptedStr(theEncKey);
		return CLS_AES_Utils.strDecrypthelper(MyTyp);
	}

	public String doLogin(Stage MyMainForm, String theTitle) throws IOException {
		boolean retBool = false;

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().setStyle("-fx-background-color: #d4ffbf");

		URL location = SUB_Protect.class.getResource("/fxml/login.fxml");
		FXMLLoader loader = new FXMLLoader(location);

		dialog.getDialogPane().setContent(loader.load());
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set login dialog title and fill user list combo:
		LoginController loginController = loader.getController();
		loginController.setDialog(dialog);
		loginController.setTitle(theTitle);

		// Show login dialog and wait for result:
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// TODO: do sommink with this logged-in user...
			FLAGS.setLOGGEDIN(true);
		} else {
			// Login cancelled:
			FLAGS.setLOGGEDIN(false);			// For future use...???
			System.exit(111);			// TODO: Fix exit code...
		}

		return loginController.getSelectedUser();
	}

	public boolean checkUsers(Class logClass, Stage MyMainForm, String theDialogTitle, String theHeader, boolean isSuperAdmin) throws IOException {
		RS_users MyRSUsers = new RS_users();

		int MyUserCount = 0;
		try {
			MyUserCount = MyRSUsers.CountUsers();
		} catch (SQLException ex) {
			Logger.getLogger(logClass.getName()).log(Level.SEVERE, null, ex);
		}

		if (MyUserCount == 0) {
			// TODO: ask for superadmin password and create user:
			TYP_user MyRow = new TYP_user();

			Dialog<ButtonType> dialog2 = new Dialog<>();
			dialog2.getDialogPane().setStyle("-fx-background-color: #d4ffbf");

			URL location2 = SUB_Protect.class.getResource("/fxml/adduser.fxml");
			FXMLLoader loader2 = new FXMLLoader(location2);

			dialog2.getDialogPane().setContent(loader2.load());
			dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

			// Set login dialog title and fill user list combo:
			adduserController loginController2 = loader2.getController();
			loginController2.setTitle(theDialogTitle);
			loginController2.setHeader(theHeader);
			if (isSuperAdmin) {
				loginController2.setSuperAdminMode();
			}
			loginController2.setDialog(dialog2);

			// Show new user dialog and wait for result:
			Optional<ButtonType> result = dialog2.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				TYP_user newUser = loginController2.getNewUser();
				// TODO: do sommink with this new user...
				MyRow.setName(newUser.getName());
				MyRow.setAdmin(newUser.isAdmin());
				MyRow.setPassword(newUser.getPassword());
				try {
					// Update to DB table:
					MyRSUsers.appendRow(MyRow);
				} catch (SQLException ex) {
					// TODO: Handle error...
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
					return false;
				}
			} else {
				// new user dialog cancelled:
				return false;
			}
		}

		return true;
	}

	public ArrayList<String> checkUsersTable(String theDB) throws SQLException {
		DBConnectionHandler dbConn = new DBConnectionHandler();

		if (dbConn.isTable(theDB, "public", "sys_users")) {
			FailList.add("La tabla " + theDB + "no existe");
		} else {
			FailList = DBStructCheck(theDB, "public", "sys_users", getDBStruct(), false);
		}

		return FailList;
	}

	private static ArrayList<TYP_DBStructCheck> getDBStruct() {
		TYP_DBStructCheck DBStructParams = new TYP_DBStructCheck();
		ArrayList<TYP_DBStructCheck> DBStruct = new ArrayList();

		DBStructParams.setColumn_name("uuid");
		DBStructParams.setColumn_default("uuid_generate_v4()");
		DBStructParams.setIs_nullable("NO");
		DBStructParams.setData_type("uuid");
		DBStructParams.setCharacter_maximum_length(0);
		DBStruct.add(DBStructParams);

		DBStructParams = new TYP_DBStructCheck();
		DBStructParams.setColumn_name("name");
		DBStructParams.setColumn_default(null);
		DBStructParams.setIs_nullable("NO");
		DBStructParams.setData_type("character varying");
		DBStructParams.setCharacter_maximum_length(64);
		DBStruct.add(DBStructParams);

		DBStructParams = new TYP_DBStructCheck();
		DBStructParams.setColumn_name("password");
		DBStructParams.setColumn_default(null);
		DBStructParams.setIs_nullable("NO");
		DBStructParams.setData_type("character varying");
		DBStructParams.setCharacter_maximum_length(64);
		DBStruct.add(DBStructParams);

		DBStructParams.setColumn_name("admin");
		DBStructParams.setColumn_default("false");
		DBStructParams.setIs_nullable("NO");
		DBStructParams.setData_type("boolean");
		DBStructParams.setCharacter_maximum_length(0);
		DBStruct.add(DBStructParams);

		return DBStruct;
	}

}
