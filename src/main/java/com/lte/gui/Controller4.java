package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import com.lte.controller.MainController;
import com.lte.models.GameInfo;
import com.lte.models.Settings;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller4 Class for Player-Player
 * @author FelixH
 *
 *
 *TODO: Siegmustererkennung!
 *Line155: Uebernahme des GegnerNamen funktioniert noch nicht, ebenso in Con3
 *Außerdem wird Name1 ncoh auf default LTE gelassen
 *Selbiger Fehler sollte ggf auch für unser Hauptprogramm KI-KI angepasst werden
 *Siegmustererkennung muss RadioButtons wieder freigeben
 *Logik für gewonnene Sätze?!
 *
 *
 *Problem: nach ein paar geworfenen Steinen -> back to Startmenu -> wieder auf Screen4 -> keine Steine können mehr geworfen werden
 *
 */
public class Controller4 {
	
	//FXML Declarations
	@FXML 
	Pane gameSet;

	@FXML
	Text ltePoints;

	@FXML
	Text opponentPoints;

	@FXML
	Text namePlayerO;

	@FXML
	Label namePlayerX;

	@FXML
	Text set;

	@FXML
	GridPane gameGrid;

	@FXML
	Button backToStart;

	@FXML
	ImageView imageView;
	
	@FXML
	ImageView row1;
	
	@FXML
	ImageView row2;
	
	@FXML
	ImageView row3;
	
	@FXML
	ImageView row4;
	
	@FXML
	ImageView row5;
	
	@FXML
	ImageView row6;
	
	@FXML
	ImageView row7;
	
	@FXML
	RadioButton radioPlayer1;
	
	@FXML
	RadioButton radioPlayer2;
	
	// non-FXML Declarations
	private MainController controller;
	private ToggleGroup tgroup;
	// private ThreadReconstruct controller;
	private Settings settings;
	private GameInfo gameInfo;
	
	//Integer Stones per column -> hight of the row
	int rowHigh1 = 0;
	int rowHigh2 = 0;
	int rowHigh3 = 0;
	int rowHigh4 = 0;
	int rowHigh5 = 0;
	int rowHigh6 = 0;
	int rowHigh7 = 0;
	
	//Initial Player
	char player = 'X';
	
	public Controller4(MainController mainController) {
		this.controller = mainController;
		this.settings = mainController.getSettings();
		this.gameInfo = mainController.getGameInfo();
	}
	
	// Getter and Setter
	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
	
	
	/**
	 * JavaFX initializations
	 */
	// *******FXML-Methoden************
	@FXML
	public void initialize() {

		set.setText("0");

		//set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		namePlayerX.setText("LTE");
		//namePlayerO.setText(controller.getGameInfo().getOpponentName());
		
		//imageView to let the player choose the row to throw stones
		File file2 = new File("files/images/Pfeil_unten_bearbeitet.png");
		Image image2 = new Image(file2.toURI().toString());
		row1.setImage(image2);
		row2.setImage(image2);
		row3.setImage(image2);
		row4.setImage(image2);
		row5.setImage(image2);
		row6.setImage(image2);
		row7.setImage(image2);
		
		//RadioButton ToggleGroup
		tgroup = new ToggleGroup();
		radioPlayer1.setToggleGroup(tgroup);
		radioPlayer1.setSelected(true);
		radioPlayer2.setToggleGroup(tgroup);
	}
	
	/**
	 * Back to Screen0
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		//set rowHigh back to zero
		int rowHigh1 = 0;
		int rowHigh2 = 0;
		int rowHigh3 = 0;
		int rowHigh4 = 0;
		int rowHigh5 = 0;
		int rowHigh6 = 0;
		int rowHigh7 = 0;		
		
		Stage stage;
		stage = (Stage) backToStart.getScene().getWindow();

		// set Icon
		File file = new File("files/images/icon.png");
		Image image = new Image(file.toURI().toString());
		stage.getIcons().add(image);

		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		loader.setController(controller.getController0());
		stage.setScene(new Scene((AnchorPane) loader.load()));

		stage.show();

	}
	
	
	/**
	 * gameOver-method shows the game-result and asks
	 * for the next steps (play new set, ...)
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo) {
		highlightWinning(winningCombo);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		if (winningPlayer == 1) {
			alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
		} else if (winningPlayer == 2) {
			alert.setHeaderText("Sie haben verloren!" + "\n" + "Was nun?");
		} else {
			alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
		}

		ButtonType weiter = new ButtonType("Weiter spielen");
		ButtonType beenden = new ButtonType("Beenden");

		alert.getButtonTypes().setAll(weiter, beenden);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == weiter) {
			clearGrid();

			// Winner gets one point
			if (winningPlayer == 1) {
				int playerX = Integer.parseInt(ltePoints.getText());
				ltePoints.setText(String.valueOf(playerX + 1));
				controller.getGameInfo().setOwnPoints(playerX);
			} else if (winningPlayer == 2) {
				int playerO = Integer.parseInt(opponentPoints.getText());
				opponentPoints.setText(String.valueOf(playerO + 1));
				controller.getGameInfo().setOpponentPoints(playerO);
			}

			// raise set 
			int satz = Integer.parseInt(set.getText());
			set.setText(String.valueOf(satz + 1));
			controller.getGameInfo().setSet(satz);
			
			//Neuen Satz starten
			controller.playSet();

		} else if (result.get() == beenden) {
			// TODO altes Controller Modell verwerfen und dem Agenten mitteilen
			// TODO ggf. Spiel zu Rekonstruieren speichern

			// back to Screen0
			Stage stage;
			stage = (Stage) backToStart.getScene().getWindow();
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/layout0.fxml"));
			loader.setController(controller.getController0());
			try {
				stage.setScene(new Scene((AnchorPane) loader.load()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			stage.show();
		}
	}
	
	
	/**
	 * Shows the stones corresponding to their position in the field
	 * 
	 */

	// ************************Fill-Methode***************************
	public void fill(int columnIndex, int rowIndex, char player, boolean endGame) {
		// player 0 = red, player 1 = yellow
		Circle circle = new Circle();
		circle.setRadius(35.0);

		if (player == 'X') {
			circle.setFill(Color.web("#62dbee", 0.85));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		} else if (player == 'O') {
			circle.setFill(Color.web("#46c668", 0.8));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		}
	}
	
	
	/**
	 * clears the field
	 */
	@FXML
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0);
	    gameGrid.getChildren().clear();
	    gameGrid.getChildren().add(0,node);
		
	}
	
	/**
	 * highlights the winning-combo
	 * @param woGewonnen
	 */
	public void highlightWinning(int[][] woGewonnen){
		//Get the positions from the array
		for(int i = 0; i<=3; i++){
			int column = woGewonnen[i][0];
			int row = woGewonnen[i][1];
			setHighlight(column, row);
		}
	}
	
	/**
	 * changes color to highlight the winning-combo
	 * @param column
	 * @param row
	 */
	public void setHighlight(int column, int row){
		//new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);
		
		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		gameGrid.getChildren().add(circle2);
		gameGrid.setHalignment(circle2, HPos.CENTER);
	}
	
	/**
	 * Event for leaving the application
	 * @param event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
	
	/**
	 * Mouse Event to let the User select the row<br>
	 * to throw the stone<br>
	 * @param e
	 */
	@FXML
	private void mouseClicked(MouseEvent e) {
		//fill(int columnIndex, int rowIndex, char player, boolean endGame)
		Node node = (Node) e.getSource();
		if(node.getId().equals("row1")){
			if(rowHigh1<=5){
				fill(0,rowHigh1,player,false);
				rowHigh1++;
				changePlayer();
			}
		}else if(node.getId().equals("row2")){
			if(rowHigh2<=5){
				fill(1,rowHigh2,player,false);
				rowHigh2++;
				changePlayer();
			}
		}else if (node.getId().equals("row3")){
			if(rowHigh3<=5){
				fill(2,rowHigh3,player,false);
				rowHigh3++;
				changePlayer();
			}
		}else if (node.getId().equals("row4")){
			if(rowHigh4<=5){
				fill(3,rowHigh4,player,false);
				rowHigh4++;
				changePlayer();
			}
		}else if (node.getId().equals("row5")){
			if(rowHigh5<=5){
				fill(4,rowHigh5,player,false);
				rowHigh5++;
				changePlayer();
			}
		}else if(node.getId().equals("row6")){
			if(rowHigh6<=5){
				fill(5,rowHigh6,player,false);
				rowHigh6++;
				changePlayer();
			}
		}else if(node.getId().equals("row7")){
			if(rowHigh7<=5){
				fill(6,rowHigh7,player,false);
				rowHigh7++;
				changePlayer();
			}
		}
    }
	
	/**
	 * changes the Player
	 */
	private void changePlayer(){
		if(player == 'X'){
			player = 'O';
		}else if(player == 'O'){
			player = 'X';
		}
	}
	
	/**
	 * fixes the player choice
	 */
	@FXML
	private void fixPlayerChoice(){
		if(radioPlayer1.isSelected()==true){
			player = 'X';
		}else if(radioPlayer2.isSelected()==true){
			player = 'O';
		}
		
		//Disable RadioButtons
		radioPlayer1.setDisable(true);
		radioPlayer2.setDisable(true);
	}
}
