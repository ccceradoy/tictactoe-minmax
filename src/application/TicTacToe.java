package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class TicTacToe {
	private String human;
	private String ai;
	private String[] config; // 0-9
	private Button[] buttons; // so the ai can change the text of the button
	private boolean aiFirst;
	private Stage stage;
	TicTacToe(Stage stage) {
		config = new String[9];
		buttons = new Button[9];
		this.stage = stage;
		initConfig();
	}

	// make the window for choosing x or o
	// when the button is clicked, change scene to the board
	void startGame() {
		VBox vbox = new VBox();
		Label label = new Label("Choose X or O:");

		HBox hbox = new HBox();
		ToggleGroup tg = new ToggleGroup();
		RadioButton x = new RadioButton("X");
		RadioButton o = new RadioButton("O");
		x.setToggleGroup(tg);
		o.setToggleGroup(tg);
		Button button = new Button("Select");
		button.setOnAction(e -> {
			if (x.isSelected()) {
				human = "X";
				ai = "O";
				aiFirst = false;
			} else {
				human = "O";
				ai = "X";
				aiFirst = true;
			}
			// show the board here
			GridPane board = setBoard();
			Scene gameScene = new Scene(board, 300, 300);
			stage.setScene(gameScene);
			stage.setTitle("TicTacToe");
			stage.show();

			// if the ai is the 'X', then make the first move
			if (aiFirst) {
				aiMove();
				aiFirst = false;
			}
		});

		hbox.getChildren().addAll(x, o, button);
		hbox.setSpacing(35);
		vbox.getChildren().addAll(label, hbox);
		vbox.setSpacing(5);

		Scene scene = new Scene(vbox, 200, 50);
		stage.setScene(scene);
		stage.show();
	}
	// the board that will be played on
	private GridPane setBoard() {
		GridPane board = new GridPane();
		for (int i = 0; i < 9; i++) {
			Button b = new Button(" ");
			b.setMinSize(100, 100);
			buttons[i] = b;
			int finalI = i;

			// event listeners on button
			// when it is clicked, the ai will make a move immediately after that
			b.setOnAction(e -> {
				if (b.getText().equals(" ")) {
					b.setText(human);
					// update the config
					config[finalI] = human;
					aiMove();
				}
			});
		}

		// add buttons to gridpane
		int buttonCounter = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board.add(buttons[buttonCounter], j, i);
				buttonCounter++;
			}
		}
		return board;
	}
	public void initConfig() {
		for (int i = 0; i < 9; i++) {
			config[i] = " ";
		}
	}
	// this function will be called after every move of the human
	public void aiMove() {
		int m = (int) Double.NEGATIVE_INFINITY;
		int bestIndex = 0;
		for (int i = 0; i < 9; i++) {
			if (config[i].equals(" ")) {
				config[i] = ai;
				int v = minimax(config, false, (int) Double.NEGATIVE_INFINITY, (int) Double.POSITIVE_INFINITY);
				config[i] = " ";
				if (v > m) {
					m = v;
					bestIndex = i;
				}
			}
		}

		// insert
		if (config[bestIndex].equals(" ")) {
			config[bestIndex] = ai;
			buttons[bestIndex].setText(ai);
		}
		// check conditions
		if (checkDraw()) {
			showResult("Draw");
		}
		if (checkWinner(ai)) {
			showResult("AI won");
		}
		if (checkWinner(human)) {
			showResult("Human won");
		}
	}

	// change scene when game ends
	private void showResult(String result) {
		VBox vbox = new VBox();
		Text text = new Text(result);
		Button exit = new Button("Exit");
		exit.setOnAction(e -> {
			System.exit(1);
		});
		vbox.getChildren().addAll(text, exit);
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(10);
		Scene scene = new Scene(vbox, 200, 50);
		stage.setScene(scene);
		stage.show();
	}

	// auxiliary for checking terminal
	private boolean checkWinner(String player) {
		if (config[0] == config[1] && config[1] == config[2] && config[2] == player) return true;
		if (config[3] == config[4] && config[4] == config[5] && config[5] == player) return true;
		if (config[6] == config[7] && config[7] == config[8] && config[8] == player) return true;
		if (config[0] == config[3] && config[3] == config[6] && config[6] == player) return true;
		if (config[1] == config[4] && config[4] == config[7] && config[7] == player) return true;
		if (config[2] == config[5] && config[5] == config[8] && config[8] == player) return true;
		if (config[0] == config[4] && config[4] == config[8] && config[8] == player) return true;
		if (config[2] == config[4] && config[4] == config[6] && config[6] == player) return true;
		return false;
	}
	// auxiliary for checking terminal
	private boolean checkDraw() {
		for (int i = 0; i < 9; i++) {
			if (config[i].equals(" ")) return false;
		}
		return true;
	}
	private int checkTerminal() {
		if (checkWinner(ai)) return 1;
		if (checkWinner(human)) return -1;
		if (checkDraw()) return 0;
		// game is still going
		return 999;
	}

	// Reference: https://www.youtube.com/watch?v=2Tr8LkyU78c
	public int minimax(String[] board, boolean isMaximizing, int alpha, int beta) {
		int terminal = checkTerminal();
		if (terminal != 999) return terminal;
		if (isMaximizing) return getMax(alpha, beta);
		return getMin(alpha, beta);
	}

	// ai is always the maximizer, human is the minimizer
	private int getMax(int alpha, int beta) {
		int m = (int) Double.NEGATIVE_INFINITY;
		for (int i = 0; i < config.length; i++) {
			if (config[i].equals(" ")) {
				config[i] = ai;
				int v = minimax(config,false, alpha, beta);
				config[i] = " ";
				m = Math.max(v, m);
				if (v >= beta) {
					return m;
				}
				alpha = Math.max(alpha, m);
			}
		}
		return m;
	}
	private int getMin(int alpha, int beta) {
		int m = (int) Double.POSITIVE_INFINITY;
		for (int i = 0; i < 9; i++) {
			if (config[i].equals(" ")) {
				config[i] = human;
				int v = minimax(config,true, alpha, beta);
				config[i] = " ";
				m = Math.min(v, m);
				if (v <= alpha) return m;
				beta = Math.min(beta, m);
			}
		}
		return m;
	}
}