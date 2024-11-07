package control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Message;
import model.User;
import utils.Util;

public class RegisterController {

	private Communication commu;

	@FXML
	private PasswordField passwordText;

	@FXML
	private Button registerButton;

	@FXML
	private TextField userNameText;

	@FXML
	private Button chooseImage;
	@FXML
	private Label pathToImage;
	@FXML
	private ImageView avatarPreview;
	
	private Image image;
	private File imageFile;
	@FXML
	void NavToLogin(ActionEvent event) {
		commu.getNavigation().switchTo("Login.fxml");

	}

	@FXML
	void Register(ActionEvent event) {
		String username = userNameText.getText();
		String password = passwordText.getText();
		System.out.println("DangNhapClick" + username + password);
		User user = new User(username, password);
		sendRegisterMessage(user);



	}

	public void sendRegisterMessage(User u) {
		try {
			Message msg = new Message("REGISTER", u);
			commu.sendMessage(msg);

			
			System.out.println("Gui yeu cau dang ki");
		} catch (Exception e) {
			e.printStackTrace();
			Util.showError("Không thể kết nối đến server");
		}

	}

	@FXML
	void chooseImageClick(ActionEvent event) {
		// Tạo FileChooser để chọn file ảnh
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Ảnh", "*.jpg", "*.png", "*.gif", "*.jpeg"));

		// Mở hộp thoại chọn file
		Stage stage = (Stage) avatarPreview.getScene().getWindow();
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			// Nếu người dùng chọn file, hiển thị ảnh lên ImageView và đường dẫn lên Label
			image = new Image(selectedFile.toURI().toString());
			imageFile = selectedFile;
			avatarPreview.setImage(image);
			pathToImage.setText(selectedFile.getAbsolutePath());
		}

	}
	

	public Communication getCommu() {
		return commu;
	}

	public void setCommu(Communication commu) {
		this.commu = commu;
	}


}
