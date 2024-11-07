package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;
import model.Message;
import model.User;
import utils.Util;

public class RoomController {
	

	
	
	
	private List<User> u;
    private PauseTransition popupDelay;

	public List<User> getU() {
		return u;
	}

	public void setU(List<User> u) {
		this.u = u;
	}

	private Communication commu;

	public Communication getCommu() {
		return commu;
	}

	public void setCommu(Communication commu) {
		this.commu = commu;
	}

	@FXML
	private Button StartButton;

	@FXML
	private Label UserName1, UserName2, UserName3, UserName4;

	@FXML
	private Text score1, score2, score3, score4;
	@FXML
	private Text textCoTam01, textCoTam02, textCoTam03, textCoTam04;
	@FXML
	private ImageView avatar1, avatar2, avatar3, avatar4;
	@FXML
	private ListView<String> chatList;
	@FXML
	private TextField chatTextField;
	@FXML
	private Button sendChatBtn;
	private Popup popup;

	@FXML
	void showPopup(MouseEvent event) {
		// Kiểm tra xem nguồn của sự kiện có phải là một ImageView hay không
		if (event.getSource() instanceof ImageView) {
			ImageView avatar = (ImageView) event.getSource();

			// Lấy tên id của ImageView (vd: "avatar2")
			String avatarId = avatar.getId();

			// Trích xuất số ở cuối id để biết chỉ số người dùng
			int userIndex = Integer.parseInt(avatarId.replaceAll("[^0-9]", "")) - 1;

			// Kiểm tra chỉ số hợp lệ với danh sách `u`
			if (userIndex >= 0 && userIndex < u.size()) {
				final User selectedUser = u.get(userIndex);
				System.out.println(selectedUser);
				// Nếu popup chưa được tạo, hãy tạo popup và nội dung bên trong
				if (popup == null) {
					popup = new Popup();
					VBox vbox = new VBox(10);

					Button btnFriendRequest = new Button("Kết bạn");
					btnFriendRequest.setOnAction(e -> sendFriendRequest(selectedUser));

					Button btnViewInfo = new Button("Xem thông tin");
					btnViewInfo.setOnAction(e -> viewUserInfo(selectedUser));

					vbox.getChildren().addAll(btnFriendRequest, btnViewInfo);
					vbox.setAlignment(Pos.CENTER);
					vbox.setStyle(
							"-fx-background-color: #fff; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #ccc;");

					popup.getContent().add(vbox);
				}

				// Hiển thị Popup gần vị trí chuột
				popup.show(avatar, event.getScreenX() + 10, event.getScreenY() + 10);
			}
		}
	}

    @FXML
    void hidePopup(MouseEvent event) {
        if (popup != null) {
            // Bắt đầu bộ đếm thời gian 3 giây trước khi ẩn popup
            popupDelay.playFromStart();
        }
    }
//    @FXML
//    void sendFriendRequest(ActionEvent event) {
//    	if(u.size()>1) {
//    		User sendUser = new User(null, null);
//    		for (User user: u) {
//    			if (user.getUid() != commu.getCurrentUser().getUid()) {
//    				sendUser = user;
//    				break;
//    			}
//    		}
//    		
//    		
//        	if (sendUser != null) {
//            	try {
//        			commu.sendMessage(new Message("SEND_FRIEND_REQUEST", sendUser));
//        			Util.showError("Đã gửi lời mời tới " + sendUser.getUsername());
//        		} catch (IOException e) {
//        			// TODO Auto-generated catch block
//        			e.printStackTrace();
//        		}
//        	}
//    	}
//
//    }

	void sendFriendRequest(User u) {
		try {
			commu.sendMessage(new Message("SEND_FRIEND_REQUEST", u));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void viewUserInfo(User u) {
		System.out.println("Xem infro" + u);

	}

	private ObservableList<String> chatListObsl;

	public RoomController() {
		super();
		chatListObsl = FXCollections.observableArrayList();
        // Khởi tạo PauseTransition để trì hoãn đóng popup 3 giây
        popupDelay = new PauseTransition(Duration.seconds(3));
        popupDelay.setOnFinished(event -> {
            if (popup != null) {
                popup.hide();
            }
        });
	}

	@FXML
	void initialize() {
		chatList.setItems(chatListObsl);
	}

	public void addChatRoomMsg(String chatMsg) {
		chatListObsl.add(0, chatMsg);
	}

	@FXML
	void startButtonClick(ActionEvent event) {
		start();
	}

	@FXML
	void goBackButtunClick(ActionEvent event) {
		leaveRoom();
		HomeController homeController = commu.getNavigation().switchTo("Home.fxml");
		homeController.updateUserInfor(commu.getCurrentUser().getUsername(), commu.getCurrentUser().getScore());
		UserName1.setText("Waiting1");
		UserName2.setText("Waiting2");
		UserName3.setText("Waiting3");
		UserName4.setText("Waiting4");
		homeController.sendReLoadTopPlayerRequest();
		commu.getNavigation().resetScene("Room.fxml");
	}

	public void setUserName1(String userName1) {
		UserName1.setText(userName1);
	}

	public void setUserName2(String userName2) {
		UserName2.setText(userName2);
	}

	private void leaveRoom() {
		try {
			commu.sendMessage(new Message("LEAVE_ROOM", commu.getCurrentUser().getUsername()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUserText() {
		// Đặt danh sách
		ImageView[] avatars = { avatar1, avatar2, avatar3, avatar4 };
		Text[] coTamTexts = { textCoTam01, textCoTam02, textCoTam03, textCoTam04 };
		Label[] userLabels = { UserName1, UserName2, UserName3, UserName4 };
		Text[] scoreTexts = { score1, score2, score3, score4 };
		System.out.println(u);
		// Hiển thị thông tin cho từng người dùng trong danh sách `u`
		for (int i = 0; i < userLabels.length; i++) {
			if (i < u.size()) {
				User user = u.get(i);
				userLabels[i].setText(user.getUsername());
				scoreTexts[i].setText(String.valueOf(user.getScore()));

				// Hiển thị nhãn và điểm của người dùng
				avatars[i].setVisible(true);
				coTamTexts[i].setVisible(true);
				userLabels[i].setVisible(true);
				scoreTexts[i].setVisible(true);
			} else {
				// Ẩn nhãn và điểm nếu không có người dùng
				avatars[i].setVisible(false);
				coTamTexts[i].setVisible(false);
				userLabels[i].setVisible(false);
				scoreTexts[i].setVisible(false);
			}
		}
	}

	public void start() {
		if (u.size() <= 1) {
			Platform.runLater(() -> {
				Util.showError("Phong chua du nguoi");
			});
		} else {
			try {
				commu.sendMessage(new Message("START", commu.getCurrentUser().getUsername()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void setButtonStartText(String txt) {
		StartButton.setText(txt);
	}

	@FXML
	void sendChatBtnClick(ActionEvent event) {
		String chatMessage = commu.getCurrentUser().getUsername() + ": " + chatTextField.getText().trim(); // Lấy và
																											// loại bỏ
																											// khoảng
																											// trắng

		if (!chatMessage.isEmpty()) {
			try {
				// Tạo thông điệp chat để gửi đến server
				Message message = new Message("CHAT_TO_ROOM_MESSAGE", chatMessage);

				// Gửi thông điệp qua lớp Communication
				commu.sendMessage(message);

				// Thêm tin nhắn vào ListView để hiển thị
//                chatList.getItems().add("You: " + chatMessage);

				// Xóa nội dung trong chatTextField sau khi gửi
				chatTextField.clear();
			} catch (IOException e) {
				e.printStackTrace();
				Util.showError("Không thể gửi tin nhắn. Vui lòng thử lại.");
			}
		}
	}

}
