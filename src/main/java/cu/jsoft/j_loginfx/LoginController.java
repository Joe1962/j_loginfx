/*
 * Copyright Joe1962
 * https://github.com/Joe1962
 */
package cu.jsoft.j_loginfx;

import cu.jsoft.j_dbfxlite.DBConnectionHandler;
import static cu.jsoft.j_dbfxlite.DBNotifications.NotifyErrorDB;
import cu.jsoft.j_loginfx.users.RS_users;
import cu.jsoft.j_loginfx.users.TYP_user;
import cu.jsoft.j_utilsfxlite.security.SUB_Protect;
import cu.jsoft.j_utilsfxlite.security.types.TYP_ParamDLG_Login;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LoginController {
	private ArrayList<TYP_ParamDLG_Login> lstUsers = new ArrayList<>();
	private Dialog dialog;
	private String AESSalt;
	private String SecKeyStr;
	private byte[] IV;
	private DBConnectionHandler DBConnHandler;

	@FXML
	private Label lblTitle;
	@FXML
	private ComboBox<String> cmbUser;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label errorLabel;

	public void initialize() throws IOException {
		String LastUser = "";
		String VersionString = "";
	}

	public void setDialog(Dialog MyDialog) {
		// Check for existing user names in DB table sys_users:
		if (countUsers() == 0) {
			return;
		}

		// There are users, continuing on to login dialog:

		// Get list of user names:
		setUserList(loadUsers());

		// Set focus on password field:
		Platform.runLater(() -> passwordField.requestFocus());

		dialog = MyDialog;
		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {
			if (!handleLogin()) {
				event.consume();
			}
		});
	}

	public void setTitle(String thetitle) {
		lblTitle.setText(thetitle);
	}

	public void setAESSalt(String AESSalt) {
		this.AESSalt = AESSalt;
	}

	public void setSecKeyStr(String SecKeyStr) {
		this.SecKeyStr = SecKeyStr;
	}

	public void setIV(byte[] IV) {
		this.IV = IV;
	}

	/**
	 * @param DBConnHandler the DBConnHandler to set
	 */
	public void setDBConnHandler(DBConnectionHandler DBConnHandler) {
		this.DBConnHandler = DBConnHandler;
	}

	private int countUsers() {
		// Count user names from DB table sys_users:
		RS_users MyRSUsers = new RS_users();
		MyRSUsers.setMyConn(DBConnHandler.getMyConn());

		int MyUserCount = 0;
		try {
			MyUserCount = MyRSUsers.CountUsers();
		} catch (SQLException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		return MyUserCount;
	}

	private ArrayList<TYP_ParamDLG_Login> loadUsers() {
		TYP_user MyRow = new TYP_user();
		RS_users MyRSUsers = new RS_users();
		MyRSUsers.setDBConnHandler(DBConnHandler);

		// Query DB for list of user names:
		try {
			MyRSUsers.selectAll(" ORDER BY name ");
		} catch (SQLException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
			NotifyErrorDB(ex.getMessage());
			// TODO: do sommink here...!!!
		}

		try {
			MyRow = MyRSUsers.getCurrent();
		} catch (SQLException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}

		// Collect user names in a list:
		while (MyRow != null) {
			lstUsers.add(new TYP_ParamDLG_Login(MyRow.getName(), MyRow.isAdmin(), MyRow.getPassword()));
			try {
				boolean tmpBool = MyRSUsers.goNext();
				MyRow = MyRSUsers.getCurrent();
			} catch (SQLException ex) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
			}
		}

		return lstUsers;
	}

	private void setUserList(ArrayList<TYP_ParamDLG_Login> lstUsers) {
		// TODO: Populate user list from lstUsers:
		for (TYP_ParamDLG_Login lstUser : lstUsers) {
			cmbUser.getItems().add(lstUser.getName());
		}
		// TODO: Select last logged-on user instead of first...???:
		cmbUser.getSelectionModel().selectFirst();
	}

	public String getSelectedUser() {
		return cmbUser.getSelectionModel().getSelectedItem();
	}

	public String getPassword() {
		return passwordField.getText();
	}

	private boolean handleLogin() {
		String user = cmbUser.getValue();
		String password = passwordField.getText();

		if (user == null || password.isEmpty()) {
			showError("Seleccione un usuario e introduzca la contraseña...");
			return false;
		}

		if (doValidate()) {
			System.out.println("Login exitoso para: " + user);
			return true;
		} else {
			showError("Contraseña inválida para: " + user);
			passwordField.clear();
			// Set focus on password field:
			passwordField.requestFocus();
			return false;
		}
	}

	private boolean doValidate() {
		SUB_Protect Protection = new SUB_Protect();
		String userName = cmbUser.getValue();
		String userPass = passwordField.getText();

		int index = getArrayListIndexByname(lstUsers, userName);
		if (index < 0) {
			// We're fscked...
		}
		boolean isAdmin = lstUsers.get(index).isAdmin();

		// This password comes encrypted:
		String pass = lstUsers.get(index).getPassword();

		//Encrypt typed password to compare with encrypted one from DB:
		String myEncString = Protection.getEncryptedString(userPass, AESSalt, SecKeyStr, IV);

		return ((myEncString.equals(pass)));
	}

	private int getArrayListIndexByname(ArrayList<TYP_ParamDLG_Login> lstCashiers, String MyName) {
		for (int i = 0; i < lstCashiers.size(); i++) {
			if (lstCashiers.get(i).getName().equals(MyName)) {
				return i;
			}
		}
		return -1;
	}

	private void showError(String message) {
		// TODO: Implement a timer to clear this:
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	@FXML
	private void cmbUserOnActionHandler(ActionEvent event) {
		passwordField.requestFocus();
	}

}
