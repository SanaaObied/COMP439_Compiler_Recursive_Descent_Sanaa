package application;

import java.util.*;
import java.util.regex.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.*;

public class TokenScanner2 {

	private final BufferedReader fileReader;

	// No-argument constructor for JavaFX
	public TokenScanner2() {
		this.fileReader = null; // Default constructor behavior
	}

	// Constructor with file path
	public TokenScanner2(String file) throws FileNotFoundException {
		this.fileReader = new BufferedReader(new FileReader(new File(file)));
	}

	public ArrayList<ArrayList<String>> scanFile() throws IOException {
		ArrayList<ArrayList<String>> linesTokens = new ArrayList<>();

		// Define the regular expressions for valid tokens
		String numberPattern = "[0-9]+(?:\\.[0-9]+)?"; // Matches integers and decimals
		String identifierPattern = "[a-zA-Z_][a-zA-Z0-9_]*"; // Matches identifiers
		String keywordPattern = "\\b(exit|include|const|var|int|float|char|function|newb|endb|mod|div|cin|cout|else|while|repeat|until|call|if)\\b";
		String operatorPattern = "[;.,()+\\-*/=<>:!#]"; // Matches individual operators
		// Pattern to combine all token types: numbers, keywords, identifiers, and
		// operators
		String tokenPattern = numberPattern + "|" + keywordPattern + "|" + identifierPattern + "|" + operatorPattern;
		// Compile the combined token pattern into a regex pattern
		Pattern pattern = Pattern.compile(tokenPattern);
		// Variables to process each line from the input
		String line;
		int lineNumber = 0;// Keeps track of the current line number for error reporting
		// Read the file line by line
		while ((line = fileReader.readLine()) != null) {
			lineNumber++;// Increment line number for each new line
			ArrayList<String> lineTokens = new ArrayList<>();// List to store tokens found in the current line
			Matcher matcher = pattern.matcher(line);// Matcher to find tokens in the current line

			int lastMatchEnd = 0; // Keeps track of the end position of the last matched token
			// Process all matches in the line
			while (matcher.find()) {
				int matchStart = matcher.start();// Start position of the current match
				int matchEnd = matcher.end(); // End position of the current match

				// Check for unmatched characters before the current match
				if (matchStart > lastMatchEnd) {
					// Extract the unmatched substring between last match and current match
					String unmatched = line.substring(lastMatchEnd, matchStart).trim();
					if (!unmatched.isEmpty()) {
						// Validate each character in the unmatched substring
						for (char ch : unmatched.toCharArray()) {
							if (!String.valueOf(ch).matches(tokenPattern)) { // Check each character
								// Report a lexical error if an invalid token is found
								String errorMessage = "Lexical error at line " + lineNumber + ": unexpected token '"
										+ ch + "'";
								Main.textArea.appendText(errorMessage + "\n");
								Main.writeEnabled = false;// Disable writing due to error
								return null;// Exit the method due to error
							}
						}
					}
				}

				// Process the matched token and categorize it
				String token = matcher.group().trim();// Extract the matched token
				if (token.matches(keywordPattern)) {// Check if token is a keyword
					lineTokens.add(token);
				} else if (token.matches(numberPattern)) {// Check if token is a number
					lineTokens.add(token);
				} else if (token.matches(identifierPattern)) { // Check if token is an identifier
					lineTokens.add(token);
				} else if (token.matches(operatorPattern)) { // Check if token is an operator
					lineTokens.add(token);
				}
				// Update the end position of the last match
				lastMatchEnd = matchEnd; // Update the end position of the last match
			}

			// Check for unmatched characters after the last match
			if (lastMatchEnd < line.length()) {
				// Extract the unmatched substring after the last match
				String unmatched = line.substring(lastMatchEnd).trim();
				if (!unmatched.isEmpty()) {
					// Validate each character in the unmatched substring
					for (char ch : unmatched.toCharArray()) {
						if (!String.valueOf(ch).matches(tokenPattern)) { // Check each character
							// Report a lexical error if an invalid token is found
							String errorMessage = "Lexical error at line " + lineNumber + ": unexpected token '" + ch
									+ "'";
							Main.textArea.appendText(errorMessage + "\n");
							Main.writeEnabled = false; // Disable writing due to error
							return null;// Exit the method due to error
						}
					}
				}
			}

			// Handle special cases like merging `!=`, `=>`, `<<`, `>>` etc.
			for (int i = 0; i < lineTokens.size() - 2; i++) {
				String currentToken = lineTokens.get(i);
				String nextToken = lineTokens.get(i + 1);
				String followingToken = lineTokens.get(i + 2);

				// Check if the middle token is a dot
				if (nextToken.equals(".")) {
					// Merge cases for number.number, character.character, character.number
					if ((isNumber(currentToken) && isNumber(followingToken))
							|| (isAlphabetic(currentToken) && isAlphabetic(followingToken))
							|| (isAlphabetic(currentToken) && isNumber(followingToken))
							|| (isNumber(currentToken) && isAlphabetic(followingToken))) {
						lineTokens.set(i, currentToken + "." + followingToken);
						lineTokens.remove(i + 2); // Remove the following token
						lineTokens.remove(i + 1); // Remove the dot token
						i--; // Step back to re-evaluate after merging
					}
				}
			}

			// Handle special cases like merging operators (e.g., ==, =>, :=)
			for (int i = 0; i < lineTokens.size() - 1; i++) {
				String currentToken = lineTokens.get(i);
				String nextToken = lineTokens.get(i + 1);

				// Merging "=>" into a single token
				if (currentToken.equals("=") && nextToken.equals(">")) {
					lineTokens.set(i, "=>");
					lineTokens.remove(i + 1); // Remove the ">" token
					i--; // Adjust index after merging
				} else if (currentToken.equals("=") && nextToken.equals("!")) {
					lineTokens.set(i, "=!");
					lineTokens.remove(i + 1); // Remove the ">" token
					i--; // Adjust index after merging
				} else if (currentToken.equals("=") && nextToken.equals("<")) {
					lineTokens.set(i, "=<");
					lineTokens.remove(i + 1); // Remove the ">" token
					i--; // Adjust index after merging
				}
				// Merging ":=" into a single token
				else if (currentToken.equals(":") && nextToken.equals("=")) {
					lineTokens.set(i, ":=");
					lineTokens.remove(i + 1); // Remove the "=" token
					i--; // Adjust index after merging
				}
				// Handling other specific operators (<<, >>, etc.)
				else if (currentToken.equals("<") && nextToken.equals("<")) {
					lineTokens.set(i, "<<");
					lineTokens.remove(i + 1);
					i--; // Adjust index after merging
				} else if (currentToken.equals(">") && nextToken.equals(">")) {
					lineTokens.set(i, ">>");
					lineTokens.remove(i + 1);
					i--; // Adjust index after merging
				}
			}

			// Add processed tokens to the final list
			linesTokens.add(lineTokens);
		}

		return linesTokens;
	}

	private boolean isAlphabetic(String token) {
		return token.matches("[a-zA-Z_]|[a-zA-Z0-9_]+"); // Matches only alphabetic characters (no digits, no symbols)
	}

	public boolean isIdentifier(String token) {
		// A valid identifier must not be empty and must start with a letter or an
		// underscore
		if (token == null || token.isEmpty()) {
			return false;
		}

		// Check if the first character is a letter or an underscore
		if (!Character.isLetter(token.charAt(0)) && token.charAt(0) != '_') {
			return false;
		}

		// Check if the rest of the characters are either letters, digits, or
		// underscores
		for (int i = 1; i < token.length(); i++) {
			char c = token.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_') {
				return false;
			}
		}
		// If all checks pass, it's a valid identifier
		return true;
	}

	// Helper function to check if a token is a number (integer or floating-point)
	private boolean isNumber(String token) {
		try {
			// Try parsing both integer and float values
			Double.parseDouble(token); // If it's a valid number, it's a number
			return true;
		} catch (NumberFormatException e) {
			return false; // If parsing fails, it's not a number
		}
	}
}
