package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import com.lte.controller.MainController;
import com.lte.controller.ThreadPlayerKiNEW;
import com.lte.features.SoundManager;
import com.lte.models.GameInfo;
import com.lte.models.Settings;
import com.sun.corba.se.pept.transport.EventHandler;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Class for Screen3 AI vs. User
 * 
 * @author FelixH
 *
 *         TODO: Einbindung der KI, Nach dem Wurf des Spielers muss KI
 *         initialisiert werden <br>
 *         KI sollte ebenfalls rowHigh1 - rowHigh7 beim Wurf eines Steines
 *         hochzählen, damit der Player<br>
 *         nicht die Würfe der KI überschreiben kann. <br>
 *         Außerdem müssen die Würfe des Players an KI übergeben werden
 *
 *         Siegmustererkennung!
 *
 *
 */
public class Controller3 {

	// FXML Declarations
	@FXML
	Pane gameSet;

	@FXML
	Button startGame;

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
	Spinner<Double> timeSpinner;

	@FXML
	GridPane gameGrid;

	@FXML
	Button backToStart;

	@FXML
	ImageView imageView;

	@FXML
	RadioButton radioKi;

	@FXML
	RadioButton radioPlayer;

	@FXML
	Button muteButton;

	// non-FXML Declarations
	private MainController controller;
	ToggleGroup tgroup;
	private SoundManager soundManager;
	private HashMap<String, Image> images;
	
	private ThreadPlayerKiNEW threadPlayerKiNEW;

	// Integer Stones per column -> hight of the row
	private int rowHigh0 = 0;
	private int rowHigh1 = 0;
	private int rowHigh2 = 0;
	private int rowHigh3 = 0;
	private int rowHigh4 = 0;
	private int rowHigh5 = 0;
	private int rowHigh6 = 0;

	public Controller3(MainController mainController) {
		this.controller = mainController;
		this.soundManager = controller.getSoundManager();
		this.images = controller.getImages();
	}

	// Getter and Setter
	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}

	/**
	 * JavaFX initializations
	 */
	@FXML
	public void initialize() {
		Status status = soundManager.getStatus();
		if (status == Status.PAUSED) {
			muteButton.setGraphic(new ImageView(images.get("speaker1-mute")));
		} else if (status == Status.PLAYING) {
			muteButton.setGraphic(new ImageView(images.get("speaker1")));
		}
		muteButton.setStyle("-fx-background-color: transparent;");

		set.setText("0");

		// set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// TimeSpinner initialization + ChangeListener
		timeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10, 2, 0.1));
		timeSpinner.setEditable(false);
		ChangeListener<Number> listener2 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println(timeSpinner.getValue());
				controller.getSettings().setCalculationTime(timeSpinner.getValue());
			}
		};
		timeSpinner.valueProperty().addListener(listener2);

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);

		namePlayerX.setText("LTE");


		// RadioButton ToggleGroup
		tgroup = new ToggleGroup();
		radioKi.setToggleGroup(tgroup);
		radioKi.setSelected(true);
		radioPlayer.setToggleGroup(tgroup);

		// set Player name
		namePlayerO.setText(controller.getGameInfo().getOpponentName());
	}

	@FXML
	private void mute(ActionEvent event) {
		Status status = soundManager.playPause();
		if (status != null) {
			if (status == Status.PAUSED) {
				muteButton.setGraphic(new ImageView(images.get("speaker1-mute")));
			} else if (status == Status.PLAYING) {
				muteButton.setGraphic(new ImageView(images.get("speaker1")));
			}
		}
	}

	/**
	 * Back to Screen0
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		// TODO: End the running Thread

		// Integer Stones per column -> hight of the row
		rowHigh0 = 0;
		rowHigh1 = 0;
		rowHigh2 = 0;
		rowHigh3 = 0;
		rowHigh4 = 0;
		rowHigh5 = 0;
		rowHigh6 = 0;

		// DB: delete unfinished game
		if (!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)) {
			controller.deleteUnfinishedGame();
		}

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
	 * starts the game set
	 * 
	 * @param event
	 */
	@FXML
	private void startSet(ActionEvent event) {
		threadPlayerKiNEW = controller.getThreadPlayerKiNEW();
		
		// RadioButton
		if (radioKi.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('X');
			controller.getGameInfo().setStartingPlayer('X');
			while (true){
				synchronized (threadPlayerKiNEW) {
					if(threadPlayerKiNEW.isReady()){
						try {
							threadPlayerKiNEW.setNextMove(-1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						threadPlayerKiNEW.notify();
						break;
					}
				}
			}
			
		} else if (radioPlayer.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('O');
			controller.getGameInfo().setStartingPlayer('O');
		}

		controller.getGameInfo().setSet(controller.getGameInfo().getSet() + 1);
		set.setText(String.valueOf(controller.getGameInfo().getSet()));

		// PlayerChoice disabled
		radioKi.setDisable(true);
		radioPlayer.setDisable(true);

		// startButton disabled
		startGame.setDisable(true);
		
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				addListener(i, j);
			}
		}
		
		controller.getGameInfo().setGameInProgress(true);
	}

	/**
	 * gameOver-method shows the game-result and asks for the next steps (play
	 * new set, ...)
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 * @throws IOException
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo){
		// highlights the winning-combo
		highlightWinning(winningCombo);

		// Winner gets one point
		if (winningPlayer == 1) {
			int playerX = Integer.parseInt(ltePoints.getText());
			ltePoints.setText(String.valueOf(playerX + 1));
		} else if (winningPlayer == 2) {
			int playerO = Integer.parseInt(opponentPoints.getText());
			opponentPoints.setText(String.valueOf(playerO + 1));
		}

		// Alert-Dialog (Confirmation-Options: Go on with next Set || exit to Startmenu)
		if (!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Game Over"); // Ask the user what the next steps are
			alert.setContentText("Unvollständige Spiele werden nicht gespeichert!");
			if (winningPlayer == 1) {
				alert.setHeaderText("Die KI hat gewonnen!" + "\n" + "Was nun?");
			} else if (winningPlayer == 2) {
				alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
			} else {
				alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
			}

			ButtonType weiter = new ButtonType("Weiter spielen");
			ButtonType beenden = new ButtonType("Beenden");

			alert.getButtonTypes().setAll(weiter, beenden);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == weiter) {
				clearGrid();

				// raise set
				int satz = Integer.parseInt(set.getText());
				set.setText(String.valueOf(satz + 1));
				controller.getGameInfo().setSet(satz);

				startNewSet();
			}
			if (result.get() == beenden) {
				// DB: delete unfinished game
				if (!(controller.getGameInfo().getOwnPoints() == 3
						|| controller.getGameInfo().getOpponentPoints() == 3)) {
					controller.deleteUnfinishedGame();
				}

				Stage stage;
				stage = (Stage) backToStart.getScene().getWindow();

				// set Icon
				File file = new File("files/images/icon.png");
				Image image = new Image(file.toURI().toString());
				stage.getIcons().add(image);

				// FXMLLoader
				FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
				loader.setController(controller.getController0());
				try {
					stage.setScene(new Scene((AnchorPane) loader.load()));
				} catch (IOException e) {
					e.printStackTrace();
				}

				stage.show();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Game Over"); // Ask the user what the next steps are
			if (winningPlayer == 1) {
				alert.setHeaderText("Die KI hat gewonnen!" + "\n" + "Das Spiel ist nun entschieden.");
			} else if (winningPlayer == 2) {
				alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Das Spiel ist nun entschieden.");
			} else {
				alert.setHeaderText("Unentschieden!" + "\n" + "Das Spiel ist nun entschieden.");
			}
			ButtonType beenden = new ButtonType("Beenden");

			alert.getButtonTypes().setAll(beenden);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == beenden) {

				Stage stage;
				stage = (Stage) backToStart.getScene().getWindow();

				// set Icon
				File file = new File("files/images/icon.png");
				Image image = new Image(file.toURI().toString());
				stage.getIcons().add(image);

				// FXMLLoader
				FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
				loader.setController(controller.getController0());
				try {
					stage.setScene(new Scene((AnchorPane) loader.load()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				stage.show();
			}
		}
	}

	private void startNewSet() {
		threadPlayerKiNEW = controller.getThreadPlayerKiNEW();
		if(controller.getGameInfo().getStartingPlayer() == 'X'){
			while (true){
				synchronized (threadPlayerKiNEW) {
					if(threadPlayerKiNEW.isReady()){
						try {
							threadPlayerKiNEW.setNextMove(-1);
							threadPlayerKiNEW.notify();
							break;
						} catch (Exception e) {
							System.out.println("Zug nicht möglich!");
						}
					}
				}
			}
		}

		// PlayerChoice disabled
		radioKi.setDisable(true);
		radioPlayer.setDisable(true);

		// startButton disabled
		startGame.setDisable(true);
		
		controller.getGameInfo().setGameInProgress(true);
	}

	/**
	 * Shows the stones corresponding to their position in the field
	 * 
	 */
	public void fill(int columnIndex, int rowIndex, char player) {
		// player 0 = red, player 1 = yellow
		Circle circle = new Circle();
		circle.setRadius(35.0);
		addListener((Node) circle, columnIndex, rowIndex);

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
		if (columnIndex == 0) {
			rowHigh0++;
		} else if (columnIndex == 1) {
			rowHigh1++;
		} else if (columnIndex == 2) {
			rowHigh2++;
		} else if (columnIndex == 3) {
			rowHigh3++;
		} else if (columnIndex == 4) {
			rowHigh4++;
		} else if (columnIndex == 5) {
			rowHigh5++;
		} else if (columnIndex == 6) {
			rowHigh6++;
		}

		System.out.println(rowHigh0 + " " + rowHigh1 + " " + rowHigh2 + " " + rowHigh3 + " " + rowHigh4 + " " + rowHigh5
				+ " " + rowHigh6);
	}

	/**
	 * clears the field
	 */
	@FXML
	private void clearGrid() {
		Node node = gameGrid.getChildren().get(0);

	    gameGrid.getChildren().clear();
	    gameGrid.getChildren().add(0,node);	
	    
	    for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				addListener(i, j);
			}
		}
	}

	/**
	 * highlights the winning-combo
	 * 
	 * @param woGewonnen
	 */
	private void highlightWinning(int[][] woGewonnen) {
		// Get the positions from the array
		for (int i = 0; i <= 3; i++) {
			int column = woGewonnen[i][0];
			int row = woGewonnen[i][1];
			setHighlight(column, row);
		}
	}

	/**
	 * changes color to highlight the winning-combo
	 * 
	 * @param column
	 * @param row
	 */
	private void setHighlight(int column, int row) {
		// new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);

		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		gameGrid.getChildren().add(circle2);
		gameGrid.setHalignment(circle2, HPos.CENTER);
	}

	private int getRow(int column) {
		if (column == 0) {
			return rowHigh0;
		}
		if (column == 1) {
			return rowHigh1;
		}
		if (column == 2) {
			return rowHigh2;
		}
		if (column == 3) {
			return rowHigh3;
		}
		if (column == 4) {
			return rowHigh4;
		}
		if (column == 5) {
			return rowHigh5;
		}
		if (column == 6) {
			return rowHigh6;
		}
		return 0;
	}
	
	private void addListener(int colIndex, int rowIndex) {
		Pane pane = new Pane();
		pane.setOnMouseClicked(e -> {
			if (controller.getGameInfo().isGameInProgress() && controller.getGameInfo().getNextPlayer() == 'O') {
				synchronized(threadPlayerKiNEW){
					try {
						threadPlayerKiNEW.setNextMove(colIndex);
						controller.getGameInfo().setNextPlayer('X');
						
						if(threadPlayerKiNEW.getState() == Thread.State.WAITING){
							threadPlayerKiNEW.notify();
						}
					} catch (Exception e1) {
						System.out.println("Zug nicht möglich!");
					}
					
				}
			}
		});

		pane.setOnMouseEntered(e -> {
			highlightColumn(colIndex);
		});

		pane.setOnMouseExited(e -> {
			deHighlightColumn(colIndex);
		});

		gameGrid.add(pane, colIndex, rowIndex);
	}
	
	private void addListener(Node node, int colIndex, int rowIndex){
		node.setOnMouseClicked(e -> {
			if (controller.getGameInfo().isGameInProgress() && controller.getGameInfo().getNextPlayer() == 'O') {
				synchronized(threadPlayerKiNEW){
					try {
						threadPlayerKiNEW.setNextMove(colIndex);
						controller.getGameInfo().setNextPlayer('X');
						
						if(threadPlayerKiNEW.getState() == Thread.State.WAITING){
							threadPlayerKiNEW.notify();
						}
					} catch (Exception e1) {
						System.out.println("Zug nicht möglich!");
					}
				}
			}
		});
		
		node.setOnMouseEntered(e -> {
			highlightColumn(colIndex);
		});

		node.setOnMouseExited(e -> {
			deHighlightColumn(colIndex);
		});
	}
	
	private void highlightColumn(int column) {
		for (Node n : gameGrid.getChildren()) {
			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (gameGrid.getColumnIndex(pane) == column) {
					pane.setStyle("-fx-background-color: #46c668; -fx-opacity: 0.7");
				}
			}
		}
	}

	private void deHighlightColumn(int column) {
		for (Node n : gameGrid.getChildren()) {

			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (gameGrid.getColumnIndex(pane) == column) {
					pane.setStyle(null);
				}
			}
		}
	}
	
	/**
	 * Event for leaving the application
	 * 
	 * @param event
	 */
	public void exitApplication() {
		if(threadPlayerKiNEW != null){
			threadPlayerKiNEW.stop();
		}
		Platform.exit();
	}
}
