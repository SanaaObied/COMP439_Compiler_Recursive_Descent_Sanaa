package application;
	
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

//Main class extending Application to create a JavaFX GUI application
public class Main extends Application {
	// Static TextArea to display output messages
	static TextArea textArea;
	// Instance of the Parser3 class to handle parsing
	Parser3 parser;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create the main layout (VBox) for the application
		VBox vbox = createVBox(primaryStage);
		// Set up the scene with the VBox and a specified size
		Scene scene = new Scene(vbox, 900, 700);
		primaryStage.setTitle("Sanaa Obied 1211079"); // Set the title of the application window
		primaryStage.setScene(scene);
		primaryStage.show();// Display the application window

	}

	// Method to create and set up the VBox layout
	public VBox createVBox(Stage primaryStage) {
		VBox vbox = new VBox(30);// Vertical box with 30px spacing between elements
		vbox.setPadding(new Insets(10));
		vbox.setAlignment(Pos.CENTER);// Center align all elements within the VBox

		// Title Label
		Label titleLabel = new Label("Recursive Descent Parsing");
		titleLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 35));
		titleLabel.setTextFill(Color.GREEN);
		vbox.setStyle("-fx-background-color: lightblue;"); // Set background color to light blue

		// Load File Button
		Button loadButton = new Button("Load File");
		loadButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 35));
		loadButton.setTextFill(Color.GREEN);
		// Restart Button
		Button restart = new Button("ReStart");
		restart.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 35));
		restart.setTextFill(Color.GREEN);
		// Author Label
		Label sanaa = new Label("Sanaa Obied 1211079");
		sanaa.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 25));
		sanaa.setTextFill(Color.GREEN);
		// Horizontal box (HBox) to hold Load and Restart buttons
		HBox h = new HBox(15);
		h.getChildren().addAll(loadButton, restart);
		h.setAlignment(Pos.CENTER);

		// TextArea for output
		textArea = new TextArea();
		textArea.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 35));
		textArea.setPadding(new Insets(15, 15, 15, 15));
		textArea.setStyle("-fx-text-fill: green;");
		textArea.setEditable(false);// Make TextArea read-only
		// Restart button action: reset the parser and clear output
		restart.setOnAction(event -> {
			if (parser != null) {
				// If parser is already initialized, reset its state
				parser.resetState();
			} else {
				// Reset static variables in Parser3 and clear the TextArea
				Parser3.lineNumber = 0;
				Parser3.tokenNumber = 0;
				textArea.clear();

				writeEnabled = true;// Enable writing to TextArea

			}
		});
		// Load button action: open a file and parse its content
		loadButton.setOnAction(event -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

			// Show the file chooser and get the result
			File selectedFile = fileChooser.showOpenDialog(primaryStage);

			// Check if the user selected a file
			if (selectedFile != null) {
				// Get the selected file path
				String filePath = selectedFile.getAbsolutePath();

				try {
					// Initialize TokenScanner with the selected file path
					TokenScanner2 tokenScanner = new TokenScanner2(filePath);
					ArrayList<ArrayList<String>> linesTokens = tokenScanner.scanFile();

					if (linesTokens == null || linesTokens.isEmpty()) {
						appendOutput("Error: No tokens extracted from the file.");
						writeEnabled = false; // Stop further writes
						return; // Optionally, handle the error by returning or setting a flag
					}

					try {
						// Initialize and parse
						parser = new Parser3(filePath);
						parser.parse();

						// If successful, append success message to TextArea
						appendOutput("Parsing completed successfully.\n");
					} catch (Exception e) {
						// If an error occurs during parsing, append error message to TextArea
						appendOutput("Parsing failed: " + e.getMessage() + "\n");
						e.printStackTrace(); // Optional: Print stack trace for debugging
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				appendOutput("No file selected.");// Handle case when no file is selected
			}

		});
		// Add all elements to the VBox
		vbox.getChildren().addAll(titleLabel, sanaa, h, textArea);

		return vbox;// Return the created VBox
	}

	// Flag to control whether text can be written to the TextArea
	static boolean writeEnabled = true;

	// Writing to the TextArea
	public static void appendOutput(String text) {
		if (writeEnabled) {
			Main.textArea.appendText(text);// Append text only if writing is enabled
		}
	}

	// Main method to launch the JavaFX application
	public static void main(String[] args) {
		launch(args); // Launch the JavaFX application
	}
}
