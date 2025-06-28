package cu.jsoft.j_loginfx.users;

import cu.jsoft.j_utilsfxlite.security.SUB_Protect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

public class AdduserController {
	private Dialog dialog;
	private String dlgTitle;
	private String AESSalt;
	private String SecKeyStr;
	private byte[] IV;

	@FXML
	private Label lblTitle;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField1;
	@FXML
	private PasswordField passwordField2;
	@FXML
	private CheckBox adminCheckBox;
	@FXML
	private Label errorLabel;

	public void initialize() {
		// Set up password validation:
		passwordField1.textProperty().addListener((obs, oldVal, newVal) -> validatePasswordMatch());
		passwordField2.textProperty().addListener((obs, oldVal, newVal) -> validatePasswordMatch());
	}

	public void setDialog(Dialog MyDialog) {
		dialog = MyDialog;
		dialog.setTitle(dlgTitle);

		// Set focus on password field:
		Platform.runLater(() -> passwordField1.requestFocus());

		final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {
			if (!validatePasswordMatch()) {
				event.consume();
			}
		});
	}

	public void setTitle(String theTitle) {
		dlgTitle = theTitle;
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

	public void setHeader(String theTitle) {
		lblTitle.setText(theTitle);
	}

	public void setSuperAdminMode() {
		usernameField.setEditable(false);
		usernameField.setText("superadmin");
		adminCheckBox.setDisable(true);
		adminCheckBox.setSelected(true);
	}

	private boolean validatePasswordMatch() {
		if (passwordField1.getText().isEmpty() & passwordField2.getText().isEmpty()) {
			return false;
		}

		if (passwordField1.getText().equals(passwordField2.getText())) {
			errorLabel.setTextFill(Paint.valueOf("BLACK"));
			errorLabel.setBackground(Background.EMPTY);
			errorLabel.setText("Contraseñas coinciden");
		} else {
			errorLabel.setTextFill(Paint.valueOf("WHITE"));
			errorLabel.setBackground(Background.fill(Paint.valueOf("RED")));
			errorLabel.setText("Contraseñas difieren");
		}
		
		return passwordField1.getText().equals(passwordField2.getText());
	}

	// Call this when dialog is saved
	public TYP_user getNewUser() {
		SUB_Protect Protection = new SUB_Protect();

		// Encrypt password and return a TYP_user struct:
		String myEncString = Protection.getEncryptedString(passwordField1.getText(), AESSalt, SecKeyStr, IV);

		return new TYP_user(
			null,
			usernameField.getText(),
			myEncString,
			adminCheckBox.isSelected()
		);
	}

}
