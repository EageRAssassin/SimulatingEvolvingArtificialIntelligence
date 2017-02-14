package client.SimulationGUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.net.ssl.SSLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import client.world.WorldObject;
import client.world.ClientCritter;
import client.world.EmptySpace;
import client.world.Food;
import client.world.Rock;
import client.world.World;
import client.world.WorldConstants;

public class WorldGUI extends Application implements Initializable {
	@FXML
	private BorderPane MainBorderPane;
	@FXML
	private AnchorPane ap;
	@FXML
	private MenuItem NewRandomWorld, LoadWorld, LoadCritter, LoadCritterWithLocation, RemoveCritter, CreateCritter,
			CreateFood, CreateRock, UserGuide, about, close;
	@FXML
	private TabPane GeneralTabPane;
	@FXML
	private Tab WorldTab, HexTab, RulesTab;
	@FXML
	private Button start, stop, advance;
	@FXML
	private JFXButton NewRandomWorldButton, LoadWorldButton;
	@FXML
	private ScrollPane WorldPane;
	@FXML
	private ScrollBar velocityBar;
	@FXML
	private TextField stepVelocity;
	@FXML
	private JFXTextArea LastRule, CritterProgram, ErrorMessageArea;
	@FXML
	private TextField advanceSteps;
	@FXML
	private JFXTextField WorldName, WorldSize, StepsTaken, CritterNum, ServerStepVelocity;
	@FXML
	private VBox HexInfoBox, CritterListVBox;

	private World world;
	Pane worldGrid = new Pane();
	int velocityint;
	int currentHexClickedX, currentHexClickedY;
	int HexX = -1, HexY = -1; // The location that the user clicked at last
	Polygon[][] hexagons;
	Image RockImage, FoodImage, CritterImage;
	ArrayList<Node> CritterNodeList = new ArrayList<Node>();
	ArrayList<Node> FoodNodeList = new ArrayList<Node>();
	ArrayList<Node> RockNodeList = new ArrayList<Node>();
	Timeline repaintContent;
	StackPane WorldObjectPane;
	boolean staticState; // To indicate whether the simulation is auto-stepping
							// or type-stepping
	static String ServerAddress;
	URL url;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	boolean loginSuccess = false;
	static int session_id;
	int version_number;

	static String level;

	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// ***Server Setup Dialog setup***
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Connect to Server");
		dialog.setHeaderText("Enter server address");
		dialog.setContentText("Please enter the server address:");
		dialog.getEditor().setText("http://127.0.0.1:8080");
		boolean ServerAddressCorrect = false;
		PrintWriter w;
		while (!ServerAddressCorrect) {
			Optional<String> result = dialog.showAndWait();
			if (!result.isPresent())
				System.exit(0);
			if (result.isPresent()) {
				String errorMessage = "";
				ServerAddress = result.get();
				HttpURLConnection connection = null;
				try {
					url = new URL(result.get() + "/CritterWorld/login");
					connection = (HttpURLConnection) this.url.openConnection();
				} catch (Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Alert");
					alert.setHeaderText("Alert");
					alert.setContentText(e.toString());
					alert.showAndWait();
				}
				try {
					connection.setDoOutput(true); // send a POST message
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					w = new PrintWriter(connection.getOutputStream());
					PostUserNameAndPassword test = new PostUserNameAndPassword();
					test.level = "admin";
					test.password = "adminX";
					w.println(gson.toJson(test, PostUserNameAndPassword.class));
					w.flush();
					connection.getInputStream();
				} catch (Exception e) {
					if (e instanceof ConnectException || e instanceof SSLException) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Alert");
						alert.setHeaderText("Alert");
						alert.setContentText(e.toString());
						alert.showAndWait();
					} else {
						if (e instanceof IOException)
							if (connection.getResponseCode() == 401)
								loginSuccess = true;// if password not
													// right (refused)
													// the server is connected
					}
				}
				if (loginSuccess) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Connection Success");
					alert.setHeaderText("Server Connection Success");
					alert.setContentText("Connected to Server: " + ServerAddress);
					alert.showAndWait();
					ServerAddressCorrect = true;
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Connection Error");
					alert.setHeaderText("Cannot connect to the server");
					alert.setContentText(errorMessage);
					alert.showAndWait();
					ServerAddressCorrect = false;
				}
			}
		}

		// ***Login Dialog setup***
		Dialog<Pair<String, String>> loginDialog = new Dialog<>();
		loginDialog.setTitle("Login");
		loginDialog.setHeaderText("Enter Username and Password");
		ButtonType loginButtonType = new ButtonType("Load", ButtonData.OK_DONE);
		loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));
		TextField Username = new TextField();
		Username.setPromptText("Username");
		PasswordField Password = new PasswordField();
		Password.setPromptText("Password");
		grid.add(new Label("Username:"), 0, 0);
		grid.add(Username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(Password, 1, 1);
		// Enable/Disable button depending on whether data was
		// entered.
		Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		Username.textProperty().addListener((observable, oldValue, newValue) -> {
			if ((newValue.trim().isEmpty()) || (Password.getText().equals("")))
				loginButton.setDisable(true);
			else
				loginButton.setDisable(false);
		});
		Password.textProperty().addListener((observable, oldValue, newValue) -> {
			if ((newValue.trim().isEmpty()) || (Username.getText().equals("")))
				loginButton.setDisable(true);
			else
				loginButton.setDisable(false);
		});
		loginDialog.getDialogPane().setContent(grid);
		// Convert the result to a Column-Row-pair when the login
		// button is clicked.
		loginDialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(Username.getText(), Password.getText());
			}
			return null;
		});
		boolean UserPasswordCorrect = false;
		while (!UserPasswordCorrect) {
			Optional<Pair<String, String>> LoginPair = loginDialog.showAndWait();
			if (!LoginPair.isPresent())
				System.exit(0);
			if (LoginPair.isPresent()) {
				url = new URL(ServerAddress + "/CritterWorld/login");
				HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				PrintWriter w2 = new PrintWriter(connection.getOutputStream());
				String EnteredUserName = LoginPair.get().getKey();
				String EnteredPassword = LoginPair.get().getValue();
				PostUserNameAndPassword temp = new PostUserNameAndPassword();
				temp.level = EnteredUserName;
				temp.password = EnteredPassword;
				w2.println(gson.toJson(temp, PostUserNameAndPassword.class));
				w2.flush();
				int responseCode = connection.getResponseCode();
				if (responseCode == 200) {
					this.level = EnteredUserName;
					BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					GetSessionId bundle = gson.fromJson(r, GetSessionId.class);
					this.session_id = bundle.session_id;
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Login Successful");
					alert.setHeaderText("Login Successful");
					alert.setContentText("Login as " + EnteredUserName);
					alert.showAndWait();
					UserPasswordCorrect = true;
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Login Failed");
					alert.setHeaderText("Login Failed");
					alert.setContentText("Please enter the correct username and password.");
					alert.showAndWait();
					UserPasswordCorrect = false;
				}
			}
		}

		// *****Application setup*****
		File file = new File("src/main/resources/image/Critter.png");// set
																		// application
																		// icon
		primaryStage.getIcons().add(new Image(file.toURI().toString()));
		primaryStage.setMinWidth(650);
		primaryStage.setMinHeight(620);
		try {
			final URL r1 = getClass().getResource("Simulation.fxml");
			if (r1 == null) {
				System.out.println("No FXML resource found.");
				try {
					stop();
				} catch (final Exception e) {
				}
				return;
			}
			final Parent node = FXMLLoader.load(r1);
			final Scene scene = new Scene(node);
			primaryStage.setTitle("Critter World Simulation");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.show();
		} catch (final IOException ioe) {
			System.out.println("Can't load FXML file.");
			ioe.printStackTrace();
			try {
				stop();
			} catch (final Exception e) {
			}
		}
	}

	// *****FXML Button Events*****

	/**
	 * Start the simulation
	 */
	@FXML
	private void startAction() throws Exception {
		if (velocityint == 0) {
			return;
		}
		url = new URL(ServerAddress + "/CritterWorld/run?session_id=" + session_id);
		HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		PrintWriter w = new PrintWriter(connection.getOutputStream());
		PostRunTheWorld temp = new PostRunTheWorld();
		temp.rate = velocityint;
		w.println(gson.toJson(temp, PostRunTheWorld.class));
		w.flush();
		connection.getInputStream();
	}

	/**
	 * Pause the simulation
	 */
	@FXML
	private void stopAction() {
		try {
			url = new URL(ServerAddress + "/CritterWorld/run?session_id=" + session_id);
			HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			PostRunTheWorld temp = new PostRunTheWorld();
			temp.rate = 0;
			w.println(gson.toJson(temp, PostRunTheWorld.class));
			w.flush();
			velocityBar.setValue(0);
			stepVelocity.setText("0");
			connection.getInputStream();
		} catch (Exception e) {
			GUIAlert("Unable to stop the world.");
		}
	}

	/**
	 * Advance the simulation by the steps specified in advanceSteps Textfield
	 */
	@FXML
	private void advanceAction() {
		if (!advanceSteps.getText().equals("0")) {
			try {
				url = new URL(ServerAddress + "/CritterWorld/step?session_id=" + session_id);
				HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				PrintWriter w = new PrintWriter(connection.getOutputStream());
				PostAdvanceTheWorld temp = new PostAdvanceTheWorld();
				try {
					temp.count = Integer.parseInt(advanceSteps.getText());
				} catch (NumberFormatException e) {
					temp.count = 0;
				}
				w.println(gson.toJson(temp, PostAdvanceTheWorld.class));
				w.flush();
				connection.getInputStream();
			} catch (Exception e) {
				GUIAlert("Unable to advance World.");
			}
		}
	}

	/**
	 * Starts a new random world of col 50 row 68.
	 */
	@FXML
	private void NewRandomWorldAction() {
		try {
			url = new URL(ServerAddress + "/CritterWorld/world?session_id=" + session_id);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			PostMakeANewWorld temp = new PostMakeANewWorld();
			String RandomWorldDescription = "";
			RandomWorldDescription += "name RandomWorld\n";
			RandomWorldDescription += "size " + WorldConstants.COLUMNS + " " + WorldConstants.ROWS + "\n";
			// generate random rocks
			World worldSample = new World(50, 68);
			for (int i = 0; i < worldSample.cols; i++)
				for (int j = 0; j < worldSample.rows; j++)
					worldSample.worldArray[i][j] = new EmptySpace();
			Random random = new Random();
			int rockNum = (int) ((random.nextFloat() * 0.2 + 0.05) * (WorldConstants.COLUMNS * WorldConstants.ROWS));
			for (int i = 0; i < rockNum; i++) {
				int col = random.nextInt(WorldConstants.COLUMNS);
				int row = random.nextInt(WorldConstants.ROWS);
				if (worldSample.isValidHex(col, row))
					if (worldSample.worldArray[col][row] instanceof EmptySpace) {
						RandomWorldDescription += "rock " + col + " " + row + "\n";
						worldSample.addRock(col, row);
					}
			}
			temp.description = RandomWorldDescription;
			w.println(gson.toJson(temp, PostMakeANewWorld.class));
			w.flush();
			connection.getInputStream();
		} catch (Exception e) {
			GUIAlert("Unable to create a world");
		}
	}

	/**
	 * Activate the load world function the same as the one in the Menu
	 */
	@FXML
	private void LoadWorldAction() {
		LoadWorld.fire();
	}

	/**
	 * Starts a new dialog to show the About info for this software.
	 */
	@FXML
	private void aboutAction() {
		Alert d = new Alert(AlertType.INFORMATION);
		d.setTitle("About");
		d.setHeaderText("Critter World Simulation");
		d.setContentText("Critter World Simulation\rCopyright © 2016 Rong Tan, Andrew Lin \rAll rights reserved.");
		d.show();
	}

	/**
	 * Starts a new dialog to show the user guide.
	 */
	@FXML
	private void UserGuideAction() {
		Alert d = new Alert(AlertType.INFORMATION);
		d.setTitle("User Guide");
		d.setHeaderText("Critter World User Guide");
		d.setContentText(
				"To create a new world, go to file-new random world.\rTo load a world, go to file-load world, select a file, and click load.\rTo add critters, go to either file-load critter or file-load critter with location, select a file, and enter how many critters are to be added or the coordinates of the critter when prompted.\rTo advance a time step, enter a number in the box in front of steps, and click the Advance button right next to it.\rTo run the world continuously, click the start button.\rTo pause it, click the stop button.\rIf you wish to control how fast the world goes, enter a number in the box where it says steps/sec, or adjust the bar on top of it.");
		d.show();
	}

	/**
	 * Remove a critter on the hex selected by the User
	 */
	@FXML
	private void RemoveCritterAction() {
		if (world.isValidHex(HexX, HexY)) {
			if (world.worldArray[HexX][HexY] instanceof ClientCritter) {
				try {
					url = new URL(ServerAddress + "/CritterWorld/critter/"
							+ ((ClientCritter) world.worldArray[HexX][HexY]).id + "?session_id=" + session_id);
					HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("DELETE");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.getInputStream();
				} catch (Exception e) {
					GUIAlert("Unable to remove critter.");
				}
			} else
				GUIAlert("There is no critter on that hex. Remove critter failed.");
		} else
			GUIAlert("You choose an invalid hex location.");
	}

	/**
	 * Create a Critter on the hex selected by the User
	 */
	@FXML
	private void CreateCritterAction() {
		if (!world.isValidHex(HexX, HexY)) {
			GUIAlert("You choose an invalid hex location.");
			return;
		}
		if (world.worldArray[HexX][HexY] instanceof EmptySpace) {
			Stage stage = (Stage) ap.getScene().getWindow();
			// ***Use File Chooser to choose Critter File***
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Critter File");
			File critterFile = fileChooser.showOpenDialog(stage);
			if (critterFile != null) {
				PostCreateACritterA temp = new PostCreateACritterA();
				int[] ReadCritterFileResult = readCritter(critterFile);
				if (ReadCritterFileResult != null) {
					String critterSpecies = readCritterSpecies(critterFile);
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(critterFile));
					} catch (FileNotFoundException e) {
						System.out.println("File not found");
					}
					for (int i = 0; i < ReadCritterFileResult[0]; i++)
						try {
							reader.readLine();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					try {
						url = new URL(ServerAddress + "/CritterWorld/critters?session_id=" + session_id);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type", "application/json");
						PrintWriter w = new PrintWriter(connection.getOutputStream());
						// convert the rest of critter program to string
						StringBuilder builder = new StringBuilder();
						String tempString = "";
						while ((tempString = reader.readLine()) != null) {
							builder.append(tempString);
						}
						temp.program = builder.toString();
						temp.mem = new int[] { ReadCritterFileResult[1], ReadCritterFileResult[2],
								ReadCritterFileResult[3], ReadCritterFileResult[4], ReadCritterFileResult[5], 0, 0,
								ReadCritterFileResult[6] };
						PostRowCol tempRL = new PostRowCol();
						tempRL.col = HexX;
						tempRL.row = HexY;
						temp.positions = new PostRowCol[] { tempRL };
						temp.species_id = critterSpecies;
						w.println(gson.toJson(temp, PostCreateACritterA.class));
						w.flush();
						connection.getInputStream();
					} catch (Exception e) {
						GUIAlert("Unable to create a critter.");
					}
				}
			}
		} else
			GUIAlert("There is something on that hex. Add critter failed.");
	}

	/**
	 * Create a food on the hex selected by the User
	 */
	@FXML
	private void CreateFoodAction() {
		if (!world.isValidHex(HexX, HexY)) {
			GUIAlert("You choose an invalid hex location.");
			return;
		}
		if (world.worldArray[HexX][HexY] instanceof EmptySpace) {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Food Amount");
			dialog.setHeaderText("Food Amount");
			dialog.setContentText("Please enter food amount you want to put in the selected hex:");
			TextField tf = dialog.getEditor();
			tf.setText("1");
			tf.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obs, final String before,
						final String after) {
					String textInput = after;
					if ((textInput.equals("")) || (textInput.equals("0")))
						tf.setText("1");
					String num = textInput.replaceAll("[^\\d]", "");
					if (!textInput.matches("\\d*")) {
						tf.setText(num);
					}
				}
			});
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String FoodAmountText = result.get();
				if ((FoodAmountText.equals("0")) || (FoodAmountText.equals(""))) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Nothing added");
					alert.setHeaderText(null);
					alert.setContentText("You typed an illegal input");
					alert.showAndWait();
				} else {
					try {
						url = new URL(ServerAddress + "/CritterWorld/world/create_entity?session_id=" + session_id);
						HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type", "application/json");
						PrintWriter w = new PrintWriter(connection.getOutputStream());
						PostCreateObject temp = new PostCreateObject();
						temp.col = HexX;
						temp.row = HexY;
						temp.type = "food";
						temp.amount = Integer.parseInt(FoodAmountText);
						w.println(gson.toJson(temp, PostCreateObject.class));
						w.flush();
						connection.getInputStream();
					} catch (Exception e) {
						GUIAlert("Unable to add food.");
					}
				}
			}
		} else
			GUIAlert("There is something on that hex. Add food failed.");
	}

	/**
	 * Create a rock on the hex selected by the User
	 */
	@FXML
	private void CreateRockAction() {
		if (!world.isValidHex(HexX, HexY)) {
			GUIAlert("You choose an invalid hex location.");
			return;
		}
		if (world.worldArray[HexX][HexY] instanceof EmptySpace)
			try {
				url = new URL(ServerAddress + "/CritterWorld/world/create_entity?session_id=" + session_id);
				HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				PrintWriter w = new PrintWriter(connection.getOutputStream());
				PostCreateObject temp = new PostCreateObject();
				temp.col = HexX;
				temp.row = HexY;
				temp.type = "rock";
				temp.amount = 0;
				w.println(gson.toJson(temp, PostCreateObject.class));
				w.flush();
				connection.getInputStream();
			} catch (Exception e) {
				GUIAlert("Unable to add rock.");
			}
		else
			GUIAlert("There is something on that hex. Add rock failed.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File file1 = new File("src/main/resources/image/Rock.png");
		RockImage = new Image(file1.toURI().toString());
		File file2 = new File("src/main/resources/image/Food.png");
		FoodImage = new Image(file2.toURI().toString());
		File file3 = new File("src/main/resources/image/Critter.png");
		CritterImage = new Image(file3.toURI().toString());
		// ***Button set up***
		if (level.equals("read")) {
			start.setDisable(true);
			stop.setDisable(true);
			velocityBar.setDisable(true);
			stepVelocity.setDisable(true);
			advanceSteps.setDisable(true);
			NewRandomWorld.setDisable(true);
			LoadWorld.setDisable(true);
			LoadCritter.setDisable(true);
			LoadCritterWithLocation.setDisable(true);
			RemoveCritter.setDisable(true);
			CreateCritter.setDisable(true);
			CreateFood.setDisable(true);
			CreateRock.setDisable(true);
			RulesTab.setDisable(true);
		}
		if (level.equals("write")) {
			NewRandomWorld.setDisable(true);
			LoadWorld.setDisable(true);
		}
		velocityBar.setMin(0);
		velocityBar.setMax(100);
		advanceSteps.setText("1");
		velocityBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				velocityint = (int) (velocityBar.getValue());
				stepVelocity.setText(String.valueOf(velocityint));
			}
		});
		// stepVelocity button setup
		stepVelocity.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obs, final String before, final String after) {
				String textInput = after;
				if (textInput.equals(""))
					textInput = "0";
				String velocity = textInput.replaceAll("[^\\d]", "");
				if (!textInput.matches("\\d*")) {
					stepVelocity.setText(velocity);
				}
				velocityBar.setValue(Integer.parseInt(velocity));
				velocityint = Integer.parseInt(velocity);
			}
		});
		stepVelocity.setText("1");
		// advanceSteps button setup
		advanceSteps.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obs, final String before, final String after) {
				String textInput = after;
				if (textInput.equals(""))
					advanceSteps.setText("1");
				String steps = textInput.replaceAll("[^\\d]", "");
				if (!textInput.matches("\\d*")) {
					advanceSteps.setText(steps);
				}
			}
		});
		// loadWorld button setup
		LoadWorld.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Stage stage = (Stage) ap.getScene().getWindow();
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open World File");
				File worldFile = fileChooser.showOpenDialog(stage);
				ConsoleOutputCapturer c = new ConsoleOutputCapturer();
				c.start();
				if (worldFile != null) {
					try {
						url = new URL(ServerAddress + "/CritterWorld/world?session_id=" + session_id);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type", "application/json");
						PrintWriter w = new PrintWriter(connection.getOutputStream());
						PostMakeANewWorld temp = new PostMakeANewWorld();
						temp.description = new String(Files.readAllBytes(worldFile.toPath()));
						w.println(gson.toJson(temp, PostMakeANewWorld.class));
						w.flush();
						connection.getInputStream();
					} catch (Exception e) {
						GUIAlert("Unable to load a world");
					}
				}
			}
		});
		// LoadCritter button setup
		LoadCritter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Stage stage = (Stage) ap.getScene().getWindow();
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Critter File");
				File critterFile = fileChooser.showOpenDialog(stage);
				if (critterFile != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Critter Number");
					dialog.setHeaderText("Critter Number");
					dialog.setContentText("Please enter number of critter you want to put with the specified file:");
					TextField tf = dialog.getEditor();
					tf.setText("1");
					tf.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(final ObservableValue<? extends String> obs, final String before,
								final String after) {
							String textInput = after;
							if (textInput.equals(""))
								tf.setText("1");
							String num = textInput.replaceAll("[^\\d]", "");
							if (!textInput.matches("\\d*")) {
								tf.setText(num);
							}
						}
					});
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						String CritterNumText = result.get();
						if ((CritterNumText.equals("0")) || (CritterNumText.equals(""))) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Nothing added");
							alert.setHeaderText(null);
							alert.setContentText("You typed an illegal input");
							alert.showAndWait();
						} else {
							PostCreateACritterB temp = new PostCreateACritterB();
							int[] ReadCritterFileResult = readCritter(critterFile);
							if (ReadCritterFileResult != null) {
								String CritterSpecies = readCritterSpecies(critterFile);
								BufferedReader reader = null;
								try {
									reader = new BufferedReader(new FileReader(critterFile));
								} catch (FileNotFoundException e) {
									System.out.println("File not found");
								}
								for (int i = 0; i < ReadCritterFileResult[0]; i++)
									try {
										reader.readLine();
									} catch (IOException e1) {
										e1.printStackTrace();
									}

								try {
									url = new URL(ServerAddress + "/CritterWorld/critters?session_id=" + session_id);
									HttpURLConnection connection = (HttpURLConnection) url.openConnection();
									connection.setDoOutput(true);
									connection.setRequestMethod("POST");
									connection.setRequestProperty("Content-Type", "application/json");
									PrintWriter w = new PrintWriter(connection.getOutputStream());
									// convert the rest of critter program to
									// string
									StringBuilder builder = new StringBuilder();
									String tempString = "";
									while ((tempString = reader.readLine()) != null) {
										builder.append(tempString);
									}
									temp.program = builder.toString();
									temp.mem = new int[] { ReadCritterFileResult[1], ReadCritterFileResult[2],
											ReadCritterFileResult[3], ReadCritterFileResult[4],
											ReadCritterFileResult[5], 0, 0, ReadCritterFileResult[6] };
									temp.num = Integer.parseInt(CritterNumText);
									temp.species_id = CritterSpecies;
									w.println(gson.toJson(temp, PostCreateACritterB.class));
									w.flush();
									connection.getInputStream();
								} catch (Exception e) {
									GUIAlert("Unable to load a critter.");
								}
							}
						}
					}
				}
			}
		});
		// LoadCritterWithLocation button setup
		LoadCritterWithLocation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Stage stage = (Stage) ap.getScene().getWindow();
				// ***Use File Chooser to choose Critter File***
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Critter File");
				File critterFile = fileChooser.showOpenDialog(stage);
				if (critterFile != null) {
					// Create the custom dialog.
					Dialog<Pair<String, String>> dialog = new Dialog<>();
					dialog.setTitle("Load Critter");
					dialog.setHeaderText("Enter Critter Location");
					// Set the button types.
					ButtonType loginButtonType = new ButtonType("Load", ButtonData.OK_DONE);
					dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
					// Create the Column and Row labels and fields.
					GridPane grid = new GridPane();
					grid.setHgap(10);
					grid.setVgap(10);
					grid.setPadding(new Insets(20, 50, 10, 10));
					TextField Column = new TextField();
					Column.setPromptText("Column");
					TextField Row = new TextField();
					Row.setPromptText("Row");
					Column.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(final ObservableValue<? extends String> obs, final String before,
								final String after) {// NUMBERS ONLY
							String textInput = after;
							if (textInput.equals(""))
								Column.setText("0");
							String velocity = textInput.replaceAll("[^\\d]", "");
							if (!textInput.matches("\\d*")) {
								Column.setText(velocity);
							}
						}
					});
					Row.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(final ObservableValue<? extends String> obs, final String before,
								final String after) {// NUMBERS ONLY
							String textInput = after;
							if (textInput.equals(""))
								Row.setText("0");
							String velocity = textInput.replaceAll("[^\\d]", "");
							if (!textInput.matches("\\d*")) {
								Column.setText(velocity);
							}
						}
					});
					grid.add(new Label("Column:"), 0, 0);
					grid.add(Column, 1, 0);
					grid.add(new Label("Row:"), 0, 1);
					grid.add(Row, 1, 1);
					// Enable/Disable button depending on whether data was
					// entered.
					Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
					loginButton.setDisable(true);
					Column.textProperty().addListener((observable, oldValue, newValue) -> {
						if ((newValue.trim().isEmpty()) || (Row.getText().equals("")))
							loginButton.setDisable(true);
						else
							loginButton.setDisable(false);
					});
					Row.textProperty().addListener((observable, oldValue, newValue) -> {
						if ((newValue.trim().isEmpty()) || (Column.getText().equals("")))
							loginButton.setDisable(true);
						else
							loginButton.setDisable(false);
					});
					dialog.getDialogPane().setContent(grid);
					// Request focus on the Column field by default.
					Platform.runLater(() -> Column.requestFocus());
					// Convert the result to a Column-Row-pair when the login
					// button is clicked.
					dialog.setResultConverter(dialogButton -> {
						if (dialogButton == loginButtonType) {
							return new Pair<>(Column.getText(), Row.getText());
						}
						return null;
					});
					Optional<Pair<String, String>> result = dialog.showAndWait();
					int c = 0, r = 0;// the col and row number for the critter
					if (result.isPresent()) {
						try {
							c = (int) Double.parseDouble(result.get().getKey());
							r = (int) Double.parseDouble(result.get().getValue());
						} catch (NumberFormatException e) {
							c = 0;
							r = 0;
						}
					}
					PostCreateACritterA temp = new PostCreateACritterA();
					int[] ReadCritterFileResult = readCritter(critterFile);
					if (ReadCritterFileResult != null) {
						String CritterSpecies = readCritterSpecies(critterFile);
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new FileReader(critterFile));
						} catch (FileNotFoundException e) {
							System.out.println("File not found");
						}
						for (int i = 0; i < ReadCritterFileResult[0]; i++)
							try {
								reader.readLine();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						try {
							url = new URL(ServerAddress + "/CritterWorld/critters?session_id=" + session_id);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setDoOutput(true);
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Content-Type", "application/json");
							PrintWriter w = new PrintWriter(connection.getOutputStream());
							// convert the rest of critter program to string
							StringBuilder builder = new StringBuilder();
							String tempString = "";
							while ((tempString = reader.readLine()) != null) {
								builder.append(tempString);
							}
							temp.program = builder.toString();
							temp.mem = new int[] { ReadCritterFileResult[1], ReadCritterFileResult[2],
									ReadCritterFileResult[3], ReadCritterFileResult[4], ReadCritterFileResult[5], 0, 0,
									ReadCritterFileResult[6] };
							PostRowCol tempRL = new PostRowCol();
							tempRL.row = r;
							tempRL.col = c;
							temp.positions = new PostRowCol[] { tempRL };
							temp.species_id = CritterSpecies;
							w.println(gson.toJson(temp, PostCreateACritterA.class));
							w.flush();
							connection.getInputStream();
						} catch (Exception e) {
							GUIAlert("Unable to load a critter with location");
						}
					}
				}
			}
		});

		// Paint the canvas
		double TimelineDuration = 200;
		repaintContent = new Timeline(new KeyFrame(Duration.millis(TimelineDuration), new EventHandler<ActionEvent>() {
			int refreshGUIAlert = 0;

			@Override
			public void handle(ActionEvent arg0) {
				refreshGUIAlert++;// refresh the GUI alert after 5 seconds
				if (refreshGUIAlert == 30) {
					ErrorMessageArea.clear();
					refreshGUIAlert = 0;
				}
				try {
					url = new URL(ServerAddress + "/CritterWorld/world?session_id=" + session_id + "&update_since="
							+ version_number);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.connect();
					BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					GetWorldState bundle = gson.fromJson(r, GetWorldState.class);
					if (world == null || bundle.cols != world.worldArray.length
							|| bundle.rows != world.worldArray[0].length) {
						// Repaint the whole world
						worldGrid.getChildren().clear();
						world = new World(bundle.cols, bundle.rows);
						int radius = (bundle.cols < 7 || bundle.rows < 7) ? 50 : 20;
						paintBackground(bundle.cols, bundle.rows, radius);
						hexagonClick();
					}
					refreshWorldInfo(bundle.name, bundle.cols, bundle.rows, bundle.current_timestep, bundle.population);
					if (bundle.rate == 0 && !level.equals("read"))
						advance.setDisable(false);
					else
						advance.setDisable(true);
					ServerStepVelocity.setText(Integer.toString(bundle.rate));
					version_number = bundle.current_version_number;
					// update the canvas
					if (bundle.state.length > 0) {
						int radius = (world.worldArray.length < 7 || world.worldArray[0].length < 7) ? 50 : 20;
						for (int i = 0; i < bundle.state.length; i++) {
							if (bundle.state[i].type.equals("rock")) {
								// Add Rock to WorldPane
								world.addRock(bundle.state[i].col, bundle.state[i].row);
								double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
										world.worldArray.length, world.worldArray[0].length, radius);
								center[0] = center[0] - radius / 2 * 1.5;
								center[1] = center[1] - radius / 2 * 1.5;
								ImageView rock = new ImageView(RockImage);
								rock.setFitHeight(radius * 1.5);
								rock.setFitWidth(radius * 1.5);
								rock.setTranslateX(center[0]);
								rock.setTranslateY(center[1]);
								currentHexClickedX = bundle.state[i].col;
								currentHexClickedY = bundle.state[i].row;
								rock.setOnMouseClicked(new EventHandler<MouseEvent>() {
									int c = currentHexClickedX;
									int r = currentHexClickedY;

									@Override
									public void handle(MouseEvent event) {
										HexX = c;
										HexY = r;
										refreshHexInfo(world.worldArray[c][r], c, r);
										hexagons[c][r].setFill(Color.RED);
									}
								});
								worldGrid.getChildren().add(rock);
							}
							if (bundle.state[i].type.equals("food")) {
								// Add Food to WorldPane
								if (!(world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof EmptySpace)) {
									// repaint the food due to its changed size
									double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
											world.worldArray.length, world.worldArray[0].length, radius);
									if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof ClientCritter) {
										double size = radius * (1
												+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
														/ 20);
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									} else if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof Food) {
										double size = radius * (1
												+ 0.35 * ((Food) world.worldArray[bundle.state[i].col][bundle.state[i].row])
														.getAmount() / 1000);
										if (size > 1.35 * radius) {
											size = 1.35 * radius;
										}
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									}
									for (int x = 0; x < worldGrid.getChildren().size(); x++) {
										if (Math.abs(worldGrid.getChildren().get(x).getTranslateX() - center[0]) < 0.1
												&& Math.abs(worldGrid.getChildren().get(x).getTranslateY()
														- center[1]) < 0.1) {
											worldGrid.getChildren().remove(x);
										}
									}
								}
								world.addFood(bundle.state[i].col, bundle.state[i].row, bundle.state[i].value);
								double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
										world.worldArray.length, world.worldArray[0].length, radius);
								double size = radius * (1
										+ 0.35 * ((Food) world.worldArray[bundle.state[i].col][bundle.state[i].row])
												.getAmount() / 1000);
								if (size > 1.35 * radius) {
									size = 1.35 * radius;
								}
								center[0] = center[0] - size / 2;
								center[1] = center[1] - size / 2;
								ImageView food = new ImageView(FoodImage);
								food.setFitHeight(size);
								food.setFitWidth(size);
								food.setTranslateX(center[0]);
								food.setTranslateY(center[1]);
								currentHexClickedX = bundle.state[i].col;
								currentHexClickedY = bundle.state[i].row;
								food.setOnMouseClicked(new EventHandler<MouseEvent>() {
									int c = currentHexClickedX;
									int r = currentHexClickedY;

									@Override
									public void handle(MouseEvent event) {
										HexX = c;
										HexY = r;
										refreshHexInfo(world.worldArray[c][r], c, r);
										hexagons[c][r].setFill(Color.RED);
									}
								});
								worldGrid.getChildren().add(food);
							}
							if (bundle.state[i].type.equals("critter") && bundle.state[i].program == null) {
								// Add Critter to WorldPane
								ClientCritter c = new ClientCritter(bundle.state[i].col, bundle.state[i].row,
										bundle.state[i].direction, bundle.state[i].id, bundle.state[i].mem[3], null, 0);
								if (!(world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof EmptySpace)) {
									// just change the direction of the critter
									double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
											world.worldArray.length, world.worldArray[0].length, radius);
									if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof ClientCritter) {
										double size = radius * (1
												+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
														/ 20);
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									} else if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof Food) {
										double size = radius * (1
												+ 0.35 * ((Food) world.worldArray[bundle.state[i].col][bundle.state[i].row])
														.getAmount() / 1000);
										if (size > 1.35 * radius) {
											size = 1.35 * radius;
										}
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									}
									for (int x = 0; x < worldGrid.getChildren().size(); x++) {
										if (Math.abs(worldGrid.getChildren().get(x).getTranslateX() - center[0]) < 0.1
												&& Math.abs(worldGrid.getChildren().get(x).getTranslateY()
														- center[1]) < 0.1) {
											worldGrid.getChildren().remove(x);
										}
									}
								}
								world.addCritter(c);
								ImageView critter = new ImageView(CritterImage);
								double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
										world.worldArray.length, world.worldArray[0].length, radius);
								double size = radius * (1
										+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
												/ 20);
								if (size > 1.75 * radius) {
									size = 1.75 * radius;
								}
								critter.setFitHeight(size);
								critter.setFitWidth(size);
								center[0] = center[0] - size / 2;
								center[1] = center[1] - size / 2;
								critter.setTranslateX(center[0]);
								critter.setTranslateY(center[1]);
								critter.setRotate(
										((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).direction
												* 60);
								currentHexClickedX = bundle.state[i].col;
								currentHexClickedY = bundle.state[i].row;
								critter.setOnMouseClicked(new EventHandler<MouseEvent>() {
									int c = currentHexClickedX;
									int r = currentHexClickedY;

									@Override
									public void handle(MouseEvent event) {
										HexX = c;
										HexY = r;
										refreshHexInfo(world.worldArray[c][r], c, r);
										hexagons[c][r].setFill(Color.RED);
										if (!level.equals("read"))
											RulesTab.setDisable(false);
									}
								});
								worldGrid.getChildren().add(critter);
							}
							if (bundle.state[i].type.equals("critter") && bundle.state[i].program != null) {
								// Add Critter to WorldPane
								ClientCritter c = new ClientCritter(bundle.state[i].col, bundle.state[i].row,
										bundle.state[i].direction, bundle.state[i].id, bundle.state[i].mem[3],
										bundle.state[i].program, bundle.state[i].recently_executed_rule);
								if (!(world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof EmptySpace)) {
									// remove the original critter
									double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
											world.worldArray.length, world.worldArray[0].length, radius);
									if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof ClientCritter) {
										double size = radius * (1
												+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
														/ 20);
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									} else if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof Food) {
										double size = radius * (1
												+ 0.35 * ((Food) world.worldArray[bundle.state[i].col][bundle.state[i].row])
														.getAmount() / 1000);
										if (size > 1.35 * radius) {
											size = 1.35 * radius;
										}
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									}
									for (int x = 0; x < worldGrid.getChildren().size(); x++) {
										if (Math.abs(worldGrid.getChildren().get(x).getTranslateX() - center[0]) < 0.1
												&& Math.abs(worldGrid.getChildren().get(x).getTranslateY()
														- center[1]) < 0.1) {
											worldGrid.getChildren().remove(x);
										}
									}
								}
								world.addCritter(c);
								ImageView critter = new ImageView(CritterImage);
								double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
										world.worldArray.length, world.worldArray[0].length, radius);
								double size = radius * (1
										+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
												/ 20);
								if (size > 1.75 * radius) {
									size = 1.75 * radius;
								}
								critter.setFitHeight(size);
								critter.setFitWidth(size);
								center[0] = center[0] - size / 2;
								center[1] = center[1] - size / 2;
								critter.setTranslateX(center[0]);
								critter.setTranslateY(center[1]);
								critter.setRotate(
										((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).direction
												* 60);
								currentHexClickedX = bundle.state[i].col;
								currentHexClickedY = bundle.state[i].row;
								critter.setOnMouseClicked(new EventHandler<MouseEvent>() {
									int c = currentHexClickedX;
									int r = currentHexClickedY;

									@Override
									public void handle(MouseEvent event) {
										HexX = c;
										HexY = r;
										refreshHexInfo(world.worldArray[c][r], c, r);
										hexagons[c][r].setFill(Color.RED);
										if (!level.equals("read"))
											RulesTab.setDisable(false);
									}
								});
								worldGrid.getChildren().add(critter);
							}
							if (bundle.state[i].type.equals("nothing")) {
								// remove the image on the world
								if (world.isValidHex(bundle.state[i].col, bundle.state[i].row)) {
									double[] center = getCenterCoor(bundle.state[i].col, bundle.state[i].row,
											world.worldArray.length, world.worldArray[0].length, radius);
									if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof ClientCritter) {
										double size = radius * (1
												+ 0.75 * ((ClientCritter) world.worldArray[bundle.state[i].col][bundle.state[i].row]).size
														/ 20);
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									} else if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof Rock) {
										center[0] = center[0] - radius / 2 * 1.5;
										center[1] = center[1] - radius / 2 * 1.5;
									} else if (world.worldArray[bundle.state[i].col][bundle.state[i].row] instanceof Food) {
										double size = radius * (1
												+ 0.35 * ((Food) world.worldArray[bundle.state[i].col][bundle.state[i].row])
														.getAmount() / 1000);
										if (size > 1.35 * radius) {
											size = 1.35 * radius;
										}
										center[0] = center[0] - size / 2;
										center[1] = center[1] - size / 2;
									}
									for (int x = 0; x < worldGrid.getChildren().size(); x++) {
										if (Math.abs(worldGrid.getChildren().get(x).getTranslateX() - center[0]) < 0.1
												&& Math.abs(worldGrid.getChildren().get(x).getTranslateY()
														- center[1]) < 0.1) {
											worldGrid.getChildren().remove(x);
										}
									}
									world.worldArray[bundle.state[i].col][bundle.state[i].row] = new EmptySpace();
								}
							}
						}
					}
					if (HexX != -1 && HexY != -1) {
						refreshHexInfo(world.worldArray[HexX][HexY], HexX, HexY);
						if (world.worldArray[HexX][HexY] instanceof ClientCritter && !level.equals("read")) {
							RulesTab.setDisable(false);
							refreshRules(((ClientCritter) world.worldArray[HexX][HexY]).program,
									((ClientCritter) world.worldArray[HexX][HexY]).lastRule);
						} else
							RulesTab.setDisable(true);
						hexagons[HexX][HexY].setFill(Color.RED);
					}
					refreshCritterList();
				} catch (Exception e) {
					GUIAlert("Update World Error.");
				}
			}
		}));
		repaintContent.setCycleCount(Animation.INDEFINITE);
		repaintContent.play();
	}

	/**
	 * The actions performed when a hexagon is clicked
	 */
	private void hexagonClick() {
		for (int i = 0; i < hexagons.length; i++) {
			for (int j = 0; j < hexagons[0].length; j++) {
				if (!world.isValidHex(i, j)) {
					continue;
				}
				currentHexClickedX = i;
				currentHexClickedY = j;
				hexagons[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {
					int c = currentHexClickedX;
					int r = currentHexClickedY;

					@Override
					public void handle(MouseEvent event) {
						HexX = c;
						HexY = r;
						refreshHexInfo(world.worldArray[c][r], c, r);
						hexagons[c][r].setFill(Color.RED);
						if (!(world.worldArray[c][r] instanceof ClientCritter) || level.equals("read"))
							RulesTab.setDisable(true);
						else {
							RulesTab.setDisable(false);
							refreshRules(((ClientCritter) world.worldArray[c][r]).program,
									((ClientCritter) world.worldArray[c][r]).lastRule);
						}
					}
				});
			}
		}
	}

	/**
	 * refresh the Critter List
	 */
	private void refreshCritterList() {
		CritterListVBox.getChildren().clear();
		CritterListVBox.setPrefHeight(0);
		try {
			url = new URL(ServerAddress + "/CritterWorld/critters?session_id=" + session_id);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			CCInfo[] bundle = gson.fromJson(r, CCInfo[].class);
			if (bundle.length != 0) {
				for (int i = 0; i < bundle.length; i++) {
					JFXTextArea t = new JFXTextArea();
					String Jtext = "CRITTER id: " + bundle[i].id + "\nspecies:" + bundle[i].species_id + "\ncol: "
							+ bundle[i].col + "  row: " + bundle[i].row;
					for (int j = 0; j < bundle[i].mem.length; j++)
						Jtext += "\nmem[" + j + "]: " + bundle[i].mem[j];
					if (bundle[i].program != null) {
						String[] SingleRules = bundle[i].program.split(";");
						if (bundle[i].recently_executed_rule >= 0)
							Jtext += "\nProgram:\n" + bundle[i].program + "\nRecently Executed Rule:\n"
									+ SingleRules[bundle[i].recently_executed_rule] + ";";
						else
							Jtext += "\nProgram:\n" + bundle[i].program
									+ "\nRecently Executed Rule:\nNo Rule Executed;";
					}
					t.setText(Jtext);
					t.setEditable(false);
					t.setMaxWidth(CritterListVBox.getWidth());
					t.setPrefHeight(450);
					CritterListVBox.getChildren().add(t);
					CritterListVBox.setPrefHeight(CritterListVBox.getPrefHeight() + 450);
				}
			}
		} catch (Exception e) {
			GUIAlert("Refresh Critter List Error.");
		}
	}

	/**
	 * refresh the world info tab on the left
	 */
	private void refreshWorldInfo(String worldname, int col, int row, int stepstaken, int critternum) {
		WorldName.setText(worldname);
		WorldSize.setText(col + "*" + row);
		StepsTaken.setText(Integer.toString(stepstaken));
		CritterNum.setText(Integer.toString(critternum));
	}

	/**
	 * refresh the hex info tab on the left
	 */
	private void refreshHexInfo(WorldObject o, int c, int r) {
		if (!(world.worldArray[c][r] instanceof ClientCritter)) {
			SingleSelectionModel<Tab> selectionModel = GeneralTabPane.getSelectionModel();
			if (selectionModel.getSelectedIndex() == 2)
				selectionModel.select(1);
			RulesTab.setDisable(true);
		}
		HexTab.setDisable(false);
		HexInfoBox.getChildren().clear();
		Text t = new Text();
		if (o instanceof ClientCritter) {
			GetRetrieveACritter bundle = null;
			try {
				url = new URL(ServerAddress + "/CritterWorld/critter/" + ((ClientCritter) o).id + "?session_id="
						+ session_id);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				bundle = gson.fromJson(bf, GetRetrieveACritter.class);
			} catch (Exception e) {
				GUIAlert(e.toString());
			}
			ScrollPane sp = new ScrollPane();
			VBox critterInfo = new VBox();
			if (bundle != null) {
				t = new Text("   Critter  ( " + bundle.col + " , " + bundle.row + " )");
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("         SPECIES     ");
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				HBox h = new HBox();
				t = new Text(bundle.species_id);
				t.setTextAlignment(TextAlignment.CENTER);
				t.setFont(Font.font("System", 16));
				h.getChildren().add(t);
				h.setAlignment(Pos.CENTER);
				critterInfo.getChildren().add(h);
				critterInfo.getChildren().add(new Separator());
				t = new Text("   MEMSIZE     " + bundle.mem[0]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   DEFENSE     " + bundle.mem[1]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   OFFENSE     " + bundle.mem[2]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   SIZE             " + bundle.mem[3]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   ENERGY      " + bundle.mem[4]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   PASS            " + bundle.mem[5]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   TAG              " + bundle.mem[6]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);
				t = new Text("   POSTURE     " + bundle.mem[7]);
				t.setFont(Font.font("System", 16));
				critterInfo.getChildren().add(t);

				for (int i = 8; i < bundle.mem.length; i++) {
					t = new Text("   mem[" + i + "]         " + bundle.mem[i]);
					t.setFont(Font.font("System", 16));
					critterInfo.getChildren().add(t);
				}
				sp.setVvalue(167);
				sp.setHvalue(300);
				sp.setContent(critterInfo);
				HexInfoBox.getChildren().add(sp);
			}
		} else if (o instanceof EmptySpace) {
			t = new Text("      Empty (" + c + ", " + r + ")");
			t.setFont(Font.font("System", 16));
			HexInfoBox.getChildren().add(t);
		} else if (o instanceof Rock) {
			t = new Text("      Rock (" + c + ", " + r + ")");
			t.setFont(Font.font("System", 16));
			HexInfoBox.getChildren().add(t);
		} else if (o instanceof Food) {
			t = new Text("      Food (" + c + ", " + r + ")");
			t.setFont(Font.font("System", 16));
			HexInfoBox.getChildren().add(t);
			HexInfoBox.getChildren().add(new Separator());
			t = new Text("      Amount: " + ((Food) o).getAmount());
			t.setFont(Font.font("System", 16));
			HexInfoBox.getChildren().add(t);
		}
		for (int i = 0; i < hexagons.length; i++)
			for (int j = 0; j < hexagons[0].length; j++)
				if (world.isValidHex(i, j))
					hexagons[i][j].setFill(Color.AZURE);
	}

	/**
	 * refresh the rules info tab on the left
	 */
	private void refreshRules(String rules, int lastRule) {
		if (rules == null) {
			CritterProgram.setText("You don't have the authority to see its rules.");
			return;
		}
		if (lastRule == -1)
			LastRule.setText("No Rule Executed");
		else {
			String[] SingleRules = rules.split(";");
			LastRule.setText(SingleRules[lastRule] + ";");
		}
		CritterProgram.setText(rules);
	}

	/**
	 * paint the background for the critter world
	 */
	private void paintBackground(int c, int r, int radius) {
		hexagons = new Polygon[c][r];
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < r; j++) {
				if (!world.isValidHex(i, j)) {
					continue;
				}
				hexagons[i][j] = new Polygon(getHexCoordinates(i, j, c, r, radius));
				hexagons[i][j].setFill(Color.AZURE);
				hexagons[i][j].setStroke(Color.BLACK);
			}
		}
		for (Polygon[] hex : hexagons) {
			for (Polygon hexagon : hex) {
				if (hexagon != null) {
					worldGrid.getChildren().add(hexagon);
				}
			}
		}
		WorldPane.setContent(worldGrid);
	}

	/**
	 * Calculate the coordinates of the six points that make up a hexagon
	 * 
	 * @param x
	 *            c
	 * @param y
	 *            r
	 * @param width
	 *            width of the entire world
	 * @param height
	 *            height of the entire world
	 * @param radius
	 *            radius of each object
	 * @return The coordinates of the six points
	 */
	private double[] getHexCoordinates(int x, int y, int width, int height, int radius) {
		double[] coor = new double[12];

		double rToPoint = radius;
		double rToSide = rToPoint * Math.sin(Math.PI / 3);
		double lengthSide = rToPoint;
		double[] center = getCenterCoor(x, y, width, height, radius);
		double xCenter = center[0];
		double yCenter = center[1];

		coor[0] = xCenter - 0.5 * lengthSide;
		coor[1] = yCenter - rToSide;

		coor[2] = xCenter + 0.5 * lengthSide;
		coor[3] = yCenter - rToSide;

		coor[4] = xCenter + rToPoint;
		coor[5] = yCenter;

		coor[6] = xCenter + 0.5 * lengthSide;
		coor[7] = yCenter + rToSide;

		coor[8] = xCenter - 0.5 * lengthSide;
		coor[9] = yCenter + rToSide;

		coor[10] = xCenter - rToPoint;
		coor[11] = yCenter;

		return coor;
	}

	/**
	 * Calculate the center coordinates of a hexagon, rock, food, or critter
	 * 
	 * @param x
	 *            c
	 * @param y
	 *            r
	 * @param width
	 *            width of the entire world
	 * @param height
	 *            height of the entire world
	 * @param radius
	 *            radius of each object
	 * @return the coordinates of the center
	 */
	private double[] getCenterCoor(int x, int y, int width, int height, int radius) {
		double[] center = new double[2];

		double widthPixel = width * radius * 1.5;
		double heightPixel = 2 * (world.worldArray[0].length - (int) Math.floor(world.worldArray.length / 2)) * radius
				* Math.sin(Math.PI / 3);
		double centerX, centerY;
		centerX = widthPixel * ((double) x / width);
		centerY = heightPixel;
		// shift centerY based on coordinates
		if (x % 2 == 0) {
			centerY -= (y - x / 2) * 2 * radius * Math.sin(Math.PI / 3);
		} else {
			if (y - x / 2 == 1) {
				centerY -= radius * Math.sin(Math.PI / 3);
			} else {
				centerY -= radius * Math.sin(Math.PI / 3);
				centerY -= (y - x / 2 - 1) * 2 * radius * Math.sin(Math.PI / 3);
			}
		}

		centerX += radius;
		if (width % 2 != 0) {
			centerY -= radius * Math.sin(Math.PI / 3);
		}

		center[0] = centerX;
		center[1] = centerY;

		return center;
	}

	/**
	 * Starts a new dialog to warn people of closing the window.
	 */
	@FXML
	private void closeAction() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close");
		alert.setHeaderText("Close Confirmation");
		alert.setContentText("Are you sure you want to exit?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			System.exit(0);
		} else {
			alert.close();
		}
	}

	/**
	 * open up a window to alert the user about their actions.
	 */
	private void GUIAlert(String content) {
		ErrorMessageArea.setText(content);
	}

	int[] readCritter(File critterFile) {
		Scanner scan3 = null;
		try {
			scan3 = new Scanner(critterFile);
		} catch (FileNotFoundException e) {
			GUIAlert(e.toString());
		}
		boolean CritterFileCorrect = true;
		int skippedLinesForReader = 0;
		int memsize = 0;
		int defense = 0;
		int offense = 0;
		int size = 0;
		int energy = 0;
		int posture = 0;
		try {
			skippedLinesForReader = 0;
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("species: ");
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("memsize: ");
			memsize = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("defense: ");
			defense = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("offense: ");
			offense = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("size: ");
			size = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("energy: ");
			energy = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
			scan3.findInLine("posture: ");
			posture = scan3.nextInt();
			scan3.nextLine();
			while (scan3.next().contains("/") == true) {
				scan3.nextLine();
				skippedLinesForReader++;
			}
		} catch (Exception e) {
			Alert d = new Alert(AlertType.ERROR);
			d.setTitle("Critter File Error");
			d.setHeaderText("Critter File with Error");
			d.setContentText("Please provide a valid critter file");
			d.show();
			CritterFileCorrect = false;
		}
		skippedLinesForReader += 7; // the seven lines
									// containing seven
									// attributes
		if (CritterFileCorrect)
			return new int[] { skippedLinesForReader, memsize, defense, offense, size, energy, posture };
		else
			return null;
	}

	String readCritterSpecies(File critterFile) {
		Scanner scan3 = null;
		try {
			scan3 = new Scanner(critterFile);
		} catch (FileNotFoundException e) {
			GUIAlert(e.toString());
		}
		while (scan3.next().contains("/") == true)
			scan3.nextLine();
		scan3.findInLine("species: ");
		return scan3.next();
	}
}