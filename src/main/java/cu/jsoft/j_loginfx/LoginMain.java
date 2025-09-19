/*
 * Copyright Joe1962
 * https://github.com/Joe1962
 */
package cu.jsoft.j_loginfx;

import cu.jsoft.j_dbfx.DBConnectionHandler;
import cu.jsoft.j_dbfx.types.TYP_DBStructCheck;
import cu.jsoft.j_loginfx.users.AdduserController;
import cu.jsoft.j_loginfx.users.RS_users;
import cu.jsoft.j_loginfx.users.TYP_user;
import cu.jsoft.j_utilsfx.global.CONSTS;
import static cu.jsoft.j_utilsfx.global.CONSTS.NEW_LINE;
import static cu.jsoft.j_utilsfx.subs.SUB_PopupsFX.MsgErrorOKFX;
import static cu.jsoft.j_utilsfx.subs.SUB_PopupsFX.MsgErrorYesNoFX;
import static cu.jsoft.j_utilsfx.subs.SUB_PopupsFX.SimpleDialog;
import static cu.jsoft.j_utilsfx.subs.SUB_UtilsFXResources.getResourceImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author joe1962
 */
public class LoginMain {
	private Class logClass;
	private DBConnectionHandler dbConn;
	private String dbName;
	private Stage theOwnerForm;
	private Font errHeaderFont;
	private Font errContentFont;
	private String theTitle;
	private String theSalt;
	private String theSecKey;
	private byte[] theIV;
	private String dbOwner;
	private boolean dbErrorMissingDB = false;
	private boolean dbErrorMissingExt = false;

	public LoginMain(Class logClass, DBConnectionHandler DBConnHandler, String dbName, String dbOwner, Font errHeaderFont, Font errContentFont) throws SQLException {
		this.logClass = logClass;
		this.dbConn = DBConnHandler;
		this.dbName = dbName;
		this.dbOwner = dbOwner;
		this.errHeaderFont = errHeaderFont;
		this.errContentFont = errContentFont;
	}

	public String doLogin(Stage MyMainForm, String theTitle, String theSalt, String theSecKey, byte[] theIV) throws IOException {
		this.theOwnerForm = MyMainForm;
		this.theTitle = theTitle;
		this.theSalt = theSalt;
		this.theSecKey = theSecKey;
		this.theIV = theIV;
		boolean retBool = false;

		if (!doChecks()) {

			// TODO: some of this stuff won't work without postgres user credentials...!!!

			StringBuilder sbMesg = new StringBuilder();
			sbMesg.append("Desea intentar reparar los errores encontrados en la base de datos?");
			sbMesg.append(CONSTS.NEW_LINE);
			sbMesg.append(CONSTS.NEW_LINE);
			sbMesg.append("El sistema intentará la reparación y terminará; si no hay mensajes de error, reintente de nuevo su ejecución.");
			retBool = MsgErrorYesNoFX(theOwnerForm, theTitle, "Reparación de la base de datos", sbMesg.toString());
			if (retBool) {
//				retBool = makeRole();
//				if (!retBool) {MsgErrorOKFX(theOwnerForm, theTitle, "Error intentando crear el rol dueño de la base de datos!", "");}
				
				if (dbErrorMissingExt) {
					retBool = makeExt();
					if (!retBool) {MsgErrorOKFX(theOwnerForm, theTitle, "Error intentando instalar la extensión para generar identificadores UUID!", "");}
				}

				if (dbErrorMissingDB) {
//					retBool = makeTable();
//					if (!retBool) {MsgErrorOKFX(theOwnerForm, theTitle, "Error intentando crea la tabla de usuarios!", "");}
				} else {
				}

				// TODO: Try to fix the default value for sys_users.uuid anyway:
				retBool = makeDefValueUUID();
				if (!retBool) {MsgErrorOKFX(theOwnerForm, theTitle, "Error intentando arreglar el valor por defecto del campo UUID!", "");}
			}

			return "";
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().setStyle("-fx-background-color: #d4ffbf");

		URL location = LoginMain.class.getResource("/fxml/login.fxml");
		FXMLLoader loader = new FXMLLoader(location);

		dialog.getDialogPane().setContent(loader.load());
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.initModality(Modality.WINDOW_MODAL);

		// Set login dialog title and fill user list combo:
		LoginController loginController = loader.getController();
		loginController.setTitle(theTitle);
		loginController.setAESSalt(theSalt);
		loginController.setSecKeyStr(theSecKey);
		loginController.setIV(theIV);
		loginController.setDBConnHandler(dbConn);
		loginController.setDialog(dialog);

		// Show login dialog and wait for result:
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Login succesaful:
			return loginController.getSelectedUser();
		} else {
			// Login cancelled:
			return "";
		}
	}

	private boolean doChecks() throws IOException {
		ArrayList<String> UserTableErrorsCheck = new ArrayList();
		boolean retBool = false;

		try {
			UserTableErrorsCheck = checkUsersTable();
		} catch (SQLException ex) {
			System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
		}

		if (!UserTableErrorsCheck.isEmpty()) {
			StringBuilder ErrorLines = new StringBuilder();

			for (String string : UserTableErrorsCheck) {
				ErrorLines.append(string);
				ErrorLines.append(NEW_LINE);
			}

			// Show simple window with tbl_sys_users.sql:
			// Prep header String:
			String MyHeader = "Se encontraron los siguientes errores en la tabla de usuarios:"
				+ NEW_LINE
				+ NEW_LINE
				+ ErrorLines;
			Label LabelHeader = new Label();
			LabelHeader.setFont(errHeaderFont);
			LabelHeader.setText(MyHeader);
			LabelHeader.setPadding(new Insets(10, 15, 15, 15));

			// Prep ERROR icon:
			ImageView IconError = getResourceImage("icons/64/dialog-error.png");

			// Set up HBOX with Labelheader and IconError:
			HBox theHeader = new HBox();
			theHeader.setAlignment(Pos.CENTER);
			theHeader.getChildren().add(LabelHeader);
			theHeader.getChildren().add(IconError);
			HBox.setMargin(IconError, new Insets(25, 25, 25, 25));

			// Get sql code in String:
			StringBuilder contentSB = new StringBuilder();
			var tempSB = new StringBuilder();
			try {
				tempSB.append(getSQLText("sql/tbl_sys_users_full.sql"));
			} catch (IOException ex) {
				System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			}
			// Replace '$OWNER' substrings with 'siguapa_owner'
			Pattern p = Pattern.compile("\\$OWNER");
			Matcher m = p.matcher(tempSB);
			contentSB.append(m.replaceAll("skelfx"));

			// Set up sql code in TextArea:
			TextArea TextAreaContent = new TextArea();
			TextAreaContent.setEditable(false);
			TextAreaContent.setFont(errContentFont);
			TextAreaContent.setText(contentSB.toString());

			// Encapsulate TextArea in TitledPane:
			TitledPane MyTitledPane = new TitledPane();
			MyTitledPane.setContent(TextAreaContent);
			MyTitledPane.setMaxHeight(1.7976931348623157E308);
			MyTitledPane.setText("SQL code...");
			MyTitledPane.setExpanded(false);
			MyTitledPane.setAnimated(false);
			MyTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
				Platform.runLater(() -> {
					MyTitledPane.requestLayout();
					MyTitledPane.getScene().getWindow().sizeToScene();
				});
			});

			// Encapsulate header and content in a BorderPane to handle resizing:
			BorderPane MyBorderPane = new BorderPane();
//			MyBorderPane.setTop(LabelHeader);
			MyBorderPane.setTop(theHeader);
			MyBorderPane.setCenter(MyTitledPane);

			// Make TitledPane the SimpleDialog content:
			Node MyContent = MyBorderPane;
//				Node MyContent = LabelHeader;
			// Show SimpleDialog:
			SimpleDialog("ERROR...!!!", MyContent, "TERMINAR", 0, 0);

			return false;
		}

		if (checkUsers() == 0) {
			retBool = makeSuperAdmin();

			if(!retBool) {
				// TODO: ERROR message and exit with error status code...
				MsgErrorOKFX(null, "AVISO", null, "No se creó el primer usuario," + NEW_LINE + "El sistema terminará.");
				return false;
			}
		}
	
		return true;
	}

	public int checkUsers() throws IOException {
		RS_users MyRSUsers = new RS_users();
		MyRSUsers.setDBConnHandler(dbConn);
		boolean retBool = false;

		int MyUserCount = 0;
		try {
			MyUserCount = MyRSUsers.CountUsers();
		} catch (SQLException ex) {
			System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
		}

		return MyUserCount;
	}

	public ArrayList<String> checkUsersTable() throws SQLException {
		boolean retBool = false;
		ArrayList<String> FailList = new ArrayList<>();
		StringBuilder extError = new StringBuilder();

		if (!dbConn.isTable(dbName, "public", "sys_users")) {
			FailList.add("La tabla sys_users no existe");
			dbErrorMissingDB = true;
		} else {
			if (!dbConn.isExtension("uuid-ossp")) {
				extError.append("No está instalada la extensión 'uuid-ossp'.");
				dbErrorMissingExt = true;
			}
			FailList = dbConn.DBStructCheck(dbName, "public", "sys_users", getDBStruct(), false);
			if (!extError.isEmpty()) {
				FailList.addFirst(extError.toString());
			}
		}

		return FailList;
	}

	private ArrayList<TYP_DBStructCheck> getDBStruct() {
		ArrayList<TYP_DBStructCheck> DBStruct = new ArrayList();

		TYP_DBStructCheck DBStructParams = new TYP_DBStructCheck();
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

		DBStructParams = new TYP_DBStructCheck();
		DBStructParams.setColumn_name("admin");
		DBStructParams.setColumn_default("false");
		DBStructParams.setIs_nullable("NO");
		DBStructParams.setData_type("boolean");
		DBStructParams.setCharacter_maximum_length(0);
		DBStruct.add(DBStructParams);

		return DBStruct;
	}

	public String getSQLText(String sqlFile) throws IOException {
		InputStream resource = this.getClass().getClassLoader().getResourceAsStream(sqlFile);

		if (resource != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		} else {
			return null;
		}
		
	}

	private boolean makeRole() {
		StringBuilder sbSQL = new StringBuilder();
		var tempSB = new StringBuilder();
		Pattern p = Pattern.compile("\\$OWNER");
		Matcher m = p.matcher(tempSB);
		PreparedStatement pstmt;

		try {
			// Create the role:
			try {
				tempSB.append(getSQLText("sql/rol_owner.sql"));
			} catch (IOException ex) {
				System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			}
			// Replace '$OWNER' substrings with the real DB owner:
			sbSQL.append(m.replaceAll(dbOwner));
			pstmt = dbConn.getMyConn().prepareStatement(sbSQL.toString());
			dbConn.doUpdate(pstmt);
		} catch (SQLException ex) {
			System.getLogger(LoginMain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			return false;
		}
			
		return true;
	}

	private boolean makeExt() {
		StringBuilder sbSQL;

		try {
			// Create the table:
			sbSQL = new StringBuilder();
			try {
				sbSQL.append(getSQLText("sql/ext_uuid_ossp.sql"));
			} catch (IOException ex) {
				System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			}
			PreparedStatement pstmt = dbConn.getMyConn().prepareStatement(sbSQL.toString());
			dbConn.doUpdate(pstmt);
		} catch (SQLException ex) {
			System.getLogger(LoginMain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			return false;
		}

		return true;
	}

	private boolean makeTable() {
		StringBuilder sbSQL = new StringBuilder();
		var tempSB = new StringBuilder();
		Pattern p = Pattern.compile("\\$OWNER");
		Matcher m = p.matcher(tempSB);
		PreparedStatement pstmt;

		try {
			// Create the extension:
			sbSQL = new StringBuilder();
			tempSB = new StringBuilder();
			try {
				tempSB.append(getSQLText("sql/tbl_sys_users.sql"));
			} catch (IOException ex) {
				System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			}
			// Replace '$OWNER' substrings with the real DB owner:
			//Pattern p = Pattern.compile("\\$OWNER");
			//Matcher m = p.matcher(tempSB);
			sbSQL.append(m.replaceAll(dbOwner));
			pstmt = dbConn.getMyConn().prepareStatement(sbSQL.toString());
			dbConn.doUpdate(pstmt);
		} catch (SQLException ex) {
			System.getLogger(LoginMain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			return false;
		}
			
		return true;
	}

	private boolean makeDefValueUUID() {
		String SQL = "ALTER TABLE sys_users ALTER COLUMN uuid SET DEFAULT uuid_generate_v4();";
		PreparedStatement pstmt;
		try {
			pstmt = dbConn.getMyConn().prepareStatement(SQL);
			dbConn.doUpdate(pstmt);
		} catch (SQLException ex) {
			System.getLogger(LoginMain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
			return false;
		}

		return true;
	}

	private boolean makeSuperAdmin() throws IOException {
		RS_users MyRSUsers = new RS_users();
		MyRSUsers.setDBConnHandler(dbConn);

		// TODO: ask for superadmin password and create user:
		TYP_user MyRow = new TYP_user();

		Dialog<ButtonType> dialog2 = new Dialog<>();
		dialog2.getDialogPane().setStyle("-fx-background-color: #d4ffbf");

		URL location2 = LoginMain.class.getResource("/fxml/adduser.fxml");
		FXMLLoader loader2 = new FXMLLoader(location2);

		dialog2.getDialogPane().setContent(loader2.load());
		dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog2.initModality(Modality.WINDOW_MODAL);

		// Set login dialog title and fill user list combo:
		AdduserController adduserController = loader2.getController();
		adduserController.setTitle(theTitle);
		adduserController.setHeader("Crear primer usuario (superadmin)...");
		adduserController.setAESSalt(theSalt);
		adduserController.setSecKeyStr(theSecKey);
		adduserController.setIV(theIV);
		adduserController.setSuperAdminMode();
		adduserController.setDialog(dialog2);

		// Show new user dialog and wait for result:
		Optional<ButtonType> result = dialog2.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			TYP_user newUser = adduserController.getNewUser();
			MyRow.setName(newUser.getName());
			MyRow.setAdmin(newUser.isAdmin());
			MyRow.setPassword(newUser.getPassword());
			try {
				// Update to DB table:
				MyRSUsers.appendRow(MyRow);
			} catch (SQLException ex) {
				// TODO: Handle error...
				System.getLogger(logClass.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
				return false;
			}
		} else {
			// new user dialog cancelled:
			return false;
		}

		return true;
	}

}
