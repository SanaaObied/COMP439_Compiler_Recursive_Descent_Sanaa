package application;

import java.util.HashMap;
import java.util.Map;

public class Vult {
	// Map to store reserved keywords and their corresponding tokens
	public static Map<String, String> reserved = new HashMap<>();
	// Map to store supported data types and their corresponding representations
	public static Map<String, String> data_type = new HashMap<>();

	// Method to initialize reserved keywords and data types
	public static void init() {
		// Add reserved keywords and tokens based on the language grammar
		reserved.put("exit", "exit");// Keyword for program termination
		reserved.put("include", "include");// Keyword for including external files
		reserved.put("const", "const");// Keyword for defining constants
		reserved.put("var", "var");// Keyword for defining variables
		reserved.put("int", "int"); // Data type keyword: integer
		reserved.put("float", "float");// Data type keyword: floating-point
		reserved.put("char", "char");// Data type keyword: character
		reserved.put("function", "function"); // Keyword for defining functions
		reserved.put("newb", "newb"); // Block start keyword
		reserved.put("endb", "endb"); // Block end keyword
		reserved.put("mod", "mod"); // Keyword for modulus operation
		reserved.put("div", "div");// Keyword for division operation
		reserved.put("cin", "cin"); // Keyword for input
		reserved.put("cout", "cout"); // Keyword for output
		reserved.put("else", "else"); // Conditional keyword
		reserved.put("while", "while");// Loop keyword
		reserved.put("repeat", "repeat");// Loop keyword
		reserved.put("until", "until"); // Loop termination keyword
		reserved.put("call", "call"); // Keyword for function calls
		reserved.put("if", "if"); // Conditional keyword

		// Add reserved symbols and operators based on the grammar
		reserved.put(";", ";"); // End of statement
		reserved.put("(", "("); // Open parenthesis
		reserved.put(")", ")"); // Close parenthesis
		reserved.put("[", "["); // Open square bracket
		reserved.put("]", "]"); // Close square bracket
		reserved.put("{", "{"); // Open curly brace
		reserved.put("}", "}"); // Close curly brace
		reserved.put(",", ","); // Comma separator
		reserved.put("+", "+");// Addition operator
		reserved.put("-", "-");// Subtraction operator
		reserved.put("*", "*"); // Multiplication operator
		reserved.put("/", "/");// Division operator
		reserved.put("%", "%");// Modulus operator
		reserved.put("=", "="); // Assignment or equality operator
		reserved.put("<", "<"); // Less-than operator
		reserved.put(">", ">"); // Greater-than operator
		reserved.put("=<", "=<"); // Less-than-or-equal operator
		reserved.put("=>", "=>"); // Greater-than-or-equal operator
		reserved.put("=!", "=!"); // Not-equal operator
		reserved.put(":=", ":="); // Assignment operator
		reserved.put(">>", ">>"); // Right shift operator
		reserved.put("<<", "<<");// Left shift operator
		reserved.put("#", "#"); // Special token or symbol
		// Add supported data types
		data_type.put("int", "int"); // Integer type
		data_type.put("float", "float"); // Floating-point type
		data_type.put("char", "char"); // Character type

	}

}
