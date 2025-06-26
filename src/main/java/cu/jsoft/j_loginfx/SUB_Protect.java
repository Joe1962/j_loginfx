/*
 * Copyright Joe1962
 */
package cu.jsoft.j_loginfx;

import cu.jsoft.j_dbfxlite.DBConnectionHandler;
import cu.jsoft.j_dbfxlite.types.TYP_DBStructCheck;
import cu.jsoft.j_loginfx.global.FLAGS;
import cu.jsoft.j_loginfx.users.AdduserController;
import cu.jsoft.j_loginfx.users.RS_users;
import cu.jsoft.j_loginfx.users.TYP_user;
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
public class SUB_Protect extends cu.jsoft.j_utilsfxlite.security.SUB_Protect {
	ArrayList<String> FailList = new ArrayList<>();

	public SUB_Protect() {
	}

	public String doLogin(Stage MyMainForm, String theTitle, String theSalt, String theSecKey, byte[] theIV, DBConnectionHandler DBConnHandler) throws IOException {
		boolean retBool = false;

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().setStyle("-fx-background-color: #d4ffbf");

		URL location = SUB_Protect.class.getResource("/fxml/login.fxml");
		FXMLLoader loader = new FXMLLoader(location);

		dialog.getDialogPane().setContent(loader.load());
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set login dialog title and fill user list combo:
		LoginController loginController = loader.getController();
		loginController.setTitle(theTitle);
		loginController.setAESSalt(theSalt);
		loginController.setSecKeyStr(theSecKey);
		loginController.setIV(theIV);
		loginController.setDBConnHandler(DBConnHandler);
		loginController.setDialog(dialog);

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

	public boolean checkUsers(Class logClass, Stage MyMainForm, String theDialogTitle, String theHeader, boolean isSuperAdmin, String theSalt, String theSecKey, byte[] theIV, DBConnectionHandler DBConnHandler) throws IOException {
		RS_users MyRSUsers = new RS_users();
		MyRSUsers.setDBConnHandler(DBConnHandler);

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
			AdduserController adduserController = loader2.getController();
			adduserController.setTitle(theDialogTitle);
			adduserController.setHeader(theHeader);
			adduserController.setAESSalt(theSalt);
			adduserController.setSecKeyStr(theSecKey);
			adduserController.setIV(theIV);
			if (isSuperAdmin) {
				adduserController.setSuperAdminMode();
			}
			adduserController.setDialog(dialog2);

			// Show new user dialog and wait for result:
			Optional<ButtonType> result = dialog2.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				TYP_user newUser = adduserController.getNewUser();
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

	public ArrayList<String> checkUsersTable(DBConnectionHandler dbConn, String theDB) throws SQLException {

		if (!dbConn.isTable(theDB, "public", "sys_users")) {
			FailList.add("La tabla " + theDB + "no existe");
		} else {
			FailList = dbConn.DBStructCheck(theDB, "public", "sys_users", getDBStruct(), false);
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
