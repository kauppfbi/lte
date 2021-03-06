package com.lte.gui;

import java.io.IOException;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.lte.controller.MainController;
import com.lte.features.SoundManager;
import com.lte.models.*;


/**
 * Class Controller0 manages the first Screen of the application<br>
 * 
 * @author FelixH
 *
 */
public class ControllerStart {

	@FXML
	AnchorPane pane;
	
	@FXML
	Button toGame;

	@FXML
	Button reGame;

	@FXML
	TextField playerX;

	@FXML
	ComboBox<String> playerO;

	@FXML
	ImageView imageView;
	
	@FXML
	TableView<DBscoreboard> scoreBoard;
	
	@FXML
	TableColumn<DBscoreboard, String> opponentName;
	
	@FXML
	TableColumn<DBscoreboard, Integer> opponentScore;
	
	@FXML
	TableColumn<DBscoreboard, Integer> opponentWins;

	@FXML
	TableColumn<DBscoreboard, Integer> opponentLoses;

	@FXML
	RadioButton AiVsAi;
	
	@FXML
	RadioButton AiVsPlayer;
	
	@FXML
	RadioButton PlayerVsPlayer;
	
	@FXML
	Button muteButton;
	
	private MainController controller;
	private String errorPlayer;
	private ToggleGroup tgroup;
	private SoundManager soundManager;
	private HashMap<String, Image> images;
	
	/**
	 * constructor for Controller0<br>
	 * sets the mainController, soundManager and Images<br>
	 * 
	 * @param mainController
	 */
	public ControllerStart(MainController mainController) {
		this.controller = mainController;
		this.soundManager = controller.getSoundManager();
		soundManager.play();
		this.images = controller.getImages();
	}	
	
	/**
	 * JavaFX initializations 
	 */
	@FXML
	public void initialize() {
		Status status = soundManager.getStatus();
		System.out.println(status);
		if (status == Status.PAUSED) {
			muteButton.setGraphic(new ImageView(images.get("speaker-mute")));
		} else if (status == Status.PLAYING || status == Status.UNKNOWN) {
			muteButton.setGraphic(new ImageView(images.get("speaker")));
		}
		muteButton.setStyle("-fx-background-color: transparent;");
		
		// Background Image
		File file = new File("files/images/Screen0.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		//name playerX default LTE
		playerX.setEditable(false);
		playerX.setText("LTE");
		
		
		//set playerO editable
		playerO.setEditable(true);
		
		//initialize ToggleGroup for RadioButtons
		tgroup = new ToggleGroup();
		AiVsAi.setToggleGroup(tgroup);
		AiVsAi.setSelected(true);
		AiVsPlayer.setToggleGroup(tgroup);
		PlayerVsPlayer.setToggleGroup(tgroup);
		
		//load opponent-player names in comboBox
		String[] opponentNamesArray = controller.getOpponentNames();
		for(int i=0; i<opponentNamesArray.length; i++){
			playerO.getItems().add(opponentNamesArray[i]);
		}
		playerO.getSelectionModel().selectFirst();
		
		try {
			DBscoreboard[] info = controller.getScoreBoardInfo();
			ObservableList<DBscoreboard> tableData = FXCollections.observableArrayList();
			
			for(int i = 0; i < info.length; i++){
				tableData.add(info[i]);
			}
	
			opponentName.setCellValueFactory(
					new PropertyValueFactory<DBscoreboard,String>("opponentName")
			);
			opponentScore.setCellValueFactory(
				    new PropertyValueFactory<DBscoreboard,Integer>("score")
			);
			opponentWins.setCellValueFactory(
					new PropertyValueFactory<DBscoreboard,Integer>("wins")
			);
			opponentLoses.setCellValueFactory(
					new PropertyValueFactory<DBscoreboard,Integer>("loses")
			);
	
			scoreBoard.setItems(tableData);
			} catch(NullPointerException e){
				System.out.println("DB hat inkonsistente Daten...");
			}	
		}

	/**
	 * ComboBox Player X editable
	 */
	@FXML
	private void playerPlayerSelected(){
		playerX.setEditable(true);
	}


	/**
	 * ComboBox Player X editable false
	 */
	@FXML
	private void playerKiSelected(){
		playerX.setEditable(false);
		playerX.setText("LTE");
	}


	/**
	 * ComboBox Player X editable false
	 */
	@FXML
	private void kiKiSelected(){
		playerX.setEditable(false);
		playerX.setText("LTE");
	}


	/**
	 * buttonPressed event Handler<br>
	 * Changes Layout from Layout0 to Game-Screen<br>
	 * Button "Weiter zum Spiel" fires this event<br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void toGame(ActionEvent event) throws IOException {				
		//Is one player-name empty?
		if (playerX.getText().isEmpty()==true || playerO.getValue().isEmpty()==true) {
			//Error-Message: Name is null!
			Alert alert2 = new Alert(AlertType.WARNING);
			alert2.setTitle("Information");
			alert2.setHeaderText("Spielername leer!");
			alert2.setContentText("Ein Spielername wurde nicht gesetzt!");
			alert2.show();
		} else {
			// Are the playerNames shorter than 10 characters?
			if (playerX.getText().length()<=9 && playerO.getValue().length()<=9) {
				Stage stage;
				if (event.getSource() == toGame && AiVsAi.isSelected()==true) {
					// Set team-names
					String nameO = playerO.getValue();
			
					// new Settings object
					controller.setSettings(new Settings());
					// pass playernames
					controller.setGameInfo(new GameInfo(nameO));
			
					stage = (Stage) toGame.getScene().getWindow();
			
					// set Icon
					File file = new File("files/images/icon.png");
					Image image = new Image(file.toURI().toString());
					stage.getIcons().add(image);
			
					// FXMLLoader
					FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layoutKiKi.fxml"));
					loader.setController(controller.getControllerKiKi());
					
					// set new layout
					stage.setScene(new Scene((AnchorPane) loader.load()));
					stage.show();
				} else if(event.getSource() == toGame && AiVsPlayer.isSelected()==true){
					String nameO = playerO.getValue();
			
					// new Settings object
					controller.setSettings(new Settings());
					// pass playernames
					controller.setGameInfo(new GameInfo(nameO));
					
					stage = (Stage) toGame.getScene().getWindow();
					
					// set Icon
					File file = new File("files/images/icon.png");
					Image image = new Image(file.toURI().toString());
					stage.getIcons().add(image);

					// FXMLLoader
					FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layoutPlayerKi.fxml"));
					loader.setController(controller.getControllerPlayerKi());
					
					// set new layout
					stage.setScene(new Scene((AnchorPane) loader.load()));

					stage.show();
				} else if(event.getSource() == toGame && PlayerVsPlayer.isSelected()==true) {
					// setNames
					String nameO = playerO.getValue();
					String nameX = playerX.getText();
			
					// new Settings object
					controller.setSettings(new Settings());
					// pass playernames
					controller.setGameInfo(new GameInfo(nameO, nameX));
					
					stage = (Stage) toGame.getScene().getWindow();
					
					// set Icon
					File file = new File("files/images/icon.png");
					Image image = new Image(file.toURI().toString());
					stage.getIcons().add(image);

					// FXMLLoader
					FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layoutPlayerPlayer.fxml"));
					loader.setController(controller.getControllerPlayerPlayer());
					
					// set new layout
					stage.setScene(new Scene((AnchorPane) loader.load()));
					stage.show();
				}
			} else {
				// Which PlayerName is too long?
				if(playerX.getText().length()>9){
					errorPlayer = "Spieler 1";
				}else{
					errorPlayer = "Spieler 2";
				}
				
				//Error-Message: Names are too long!
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Information");
				alert.setHeaderText("Spielername von "+errorPlayer+" zu lang!");
				alert.setContentText("Die Spielernamen dürfen nicht länger als 9 Zeichen sein!");
				alert.show();
			}
		}
	}

	
	/**
	 * Change Screen from Screen0 to Reconstruction-Screen<br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void reconstructGame(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == reGame) {
			stage = (Stage) toGame.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);
			
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layoutReconstruct.fxml"));
			ControllerReconstruct controller2 = controller.getControllerReconstruct();
			loader.setController(controller2);

			//set new layout
			stage.setScene(new Scene((AnchorPane) loader.load()));
			
			// load recent Games
			controller2.getRecGameInfo();
			
			stage.show();
		}
	}

	/**
	 * pause the Sound<br>
	 * 
	 * @param event
	 */
	@FXML
	private void mute(ActionEvent event){
		Status status = soundManager.playPause();
		if (status != null) {
			if (status == Status.PAUSED) {
				muteButton.setGraphic(new ImageView(images.get("speaker-mute")));
			} else if (status == Status.PLAYING) {
				muteButton.setGraphic(new ImageView(images.get("speaker")));
			}
		}
	}	
}