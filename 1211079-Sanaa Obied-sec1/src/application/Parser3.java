package application;



import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The Parser3 class is responsible for parsing a tokenized input file based on
 * a predefined grammar. It uses the output from TokenScanner2 and implements a
 * recursive descent parsing method.
 */
public class Parser3 {
	// Static variables to keep track of the current line and token numbers
	static int lineNumber;
	static int tokenNumber;
	// Current token being processed
	private String token;
	// List of tokens grouped by lines; each inner ArrayList represents tokens from
	// a single line
	private final ArrayList<ArrayList<String>> tokens;

	public Parser3(String file) throws IOException {
		tokens = new TokenScanner2(file).scanFile();// Tokenize the input file

	}

	public void parse() throws FileNotFoundException {
		Vult.init(); // Initialize the parsing environment or any global settings
		getToken(); // Fetch the first token to start parsing
		program();// Begin parsing from the top-level 'program' rule

	}

	// program => lib-decl declarations ( function-decl )* block exit
	private void program() {
		lib_decl(); // Parse library declarations
		declarations(); // Parse general declarations
		// Parse function declarations if any
		while (token.equals("function")) {
			function_decl(); // Parse function declaration
		}
		block(); // Parse the main block

		// Expect 'exit' at the end
		if (!token.equals("exit")) {

			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

// lib-decl =>  # include  <   file-name  >    ;     lib-decl     |       lambda
	private void lib_decl() {
		// If the token is '#' (start of a library declaration)
		if (token.equals("#")) { // Check if it's the start of an include directive
			getNext(); // Move to the next token (expecting 'include')

			if (token.equals("include")) {
				getNext(); // Move to the next token (expecting '<')

				if (token.equals("<")) {
					getNext(); // Move to the next token (expecting file-name)

					// Expecting the file name token, make sure it’s valid
					if (checkName(token)) {
						getNext(); // Move past the file-name
					} else {

						Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
						Main.writeEnabled = false; // Stop further writes
						return;

					}

					if (token.equals(">")) {
						getNext(); // Move to the next token (expecting ';')

						if (token.equals(";")) {
							getNext(); // Move past the ';' and continue parsing
							lib_decl(); // Recursively parse the next lib-decl (if any)
							if (token.equals("const") || token.equals("var") || token.equals("function")
									|| token.equals("newb") || token.equals("exit")) {
								return;
							} else {
								// If token is not part of the expected tokens, report an error
								Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
								Main.writeEnabled = false; // Stop further writes
								return;
							}
						} else {
							Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
							Main.writeEnabled = false; // Stop further writes
							return;

						}
					} else {
						Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
						Main.writeEnabled = false; // Stop further writes
						return;
					}
				} else {
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
	}

	// declarations => const-decl var-decl
	private void declarations() {
		const_decl(); // Parse constant declarations
		var_decl(); // Parse variable declarations
	}

	// const-decl => const data-type const-name = value ; const-decl | lambda
	private void const_decl() {
		// Check if the token is 'const' (start of a constant declaration)
		if (token.equals("const")) {
			getNext(); // Move to the next token (expecting data-type)
			// Expect a valid data-type (e.g., int, float)
			data_type();

			// Check if the token is a valid constant name (const-name)
			if (checkName(token)) {
				getNext(); // Consume the constant name

				// Check if the token is '=' (indicating assignment of a value)
				if (token.equals("=")) {
					getNext(); // Move to the next token (expecting value)

					// Process the value assigned to the constant
					if (checkValue(token)) {
						getNext(); // Move past the file-name
					} else {

						Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
						Main.writeEnabled = false; // Stop further writes
						return;

					}
					// Check if the token is ';' (end of declaration)
					if (token.equals(";")) {
						getNext(); // Move past the ';' and continue parsing

						// Recursively process any additional constant declarations
						if (token.equals("const")) {
							const_decl(); // Continue if there's another constant declaration
						}

						// Check if the token is not part of the expected tokens
						if (!(token.equals("var") || token.equals("function") || token.equals("newb")
								|| token.equals("exit"))) {
							// Report error if an unexpected token is encountered
							Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
							Main.writeEnabled = false; // Stop further writes
							return;
						}
					} else {
						// If no ';' found, report an error
						Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));

						Main.writeEnabled = false; // Stop further writes
						return;
					}
				} else {
					// If no '=' found, report an error
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				// If invalid constant name, report error
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
	}

// var-decl  =>  var    data-type    name-list     ;      var-decl      |      lambda
	private void var_decl() {
		// Check if the token is 'var' (start of a variable declaration)
		if (token.equals("var")) {
			getNext(); // Move to the next token (expecting data-type)
			// Expect a valid data-type (e.g., int, float)
			data_type(); // Call data_type() to handle the data type
			// Expect a valid name-list (list of variable names)
			name_list(); // Call name_list() to handle the list of variable names

			// Check if the token is ';' (end of declaration)
			if (token.equals(";")) {
				getNext(); // Move past the ';' and continue parsing

				// Recursively process any additional variable declarations
				var_decl();

				if (!(token.equals("function") || token.equals("newb") || token.equals("exit"))) {
					// If token is not part of the expected tokens, report an error
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
	}

	// name-list => var-name more-names
	private void name_list() {
		if (checkName(token)) {
			getNext();
			more_names();// Consume the variable name and move to the next token
		}
	}

//  more-names =>      ,     name-list       |         lambda
	private void more_names() {
		// Case 1: If the token is a comma, process the name-list
		if (token.equals(",")) {
			getNext(); // Move past the comma

			name_list(); // Parse the name-list after the comma
			if (!(token.equals(";") || token.equals("function") || token.equals("newb") || token.equals("exit"))) {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;

			}
		}
	}

// data-type =>   int       |       float      |     char             
	private void data_type() {
		// Case 1: If the token is "int", handle it
		if (token.equals("int")) {
			getNext(); // Move past the "int" token
		}
		// Case 2: If the token is "float", handle it
		else if (token.equals("float")) {
			getNext(); // Move past the "float" token
		}
		// Case 3: If the token is "char", handle it
		else if (token.equals("char")) {
			getNext(); // Move past the "char" token
		}
		// Error Case: If the token is not one of the valid data types, report an error
		else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//   function-decl =>   function-heading        declarations        block       ;
	private void function_decl() {
		// Step 1: Process function-heading
		function_heading();

		// Step 2: Process declarations
		declarations();

		// Step 3: Process block
		block();

		// Step 4: Match the semicolon ";"
		if (token.equals(";")) {
			getNext(); // Move past the semicolon
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//   function-heading  =>   function        function-name      ; 
	private void function_heading() {
		// Step 1: Match the keyword "function"
		if (token.equals("function")) {
			getNext(); // Move past the "function" token
			if (checkName(token)) { // Check if the token is a valid function name
				getNext(); // Move past the function name

				if (token.equals(";")) {
					getNext(); // Move past the semicolon
				} else {
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//   block =>  newb    stmt-list    endb
	private void block() {
		// Step 1: Match the keyword "newb"
		if (token.equals("newb")) {
			getNext(); // Move past the "newb" token
			stmt_list(); // Assuming a separate method for parsing stmt-list
			if (token.equals("endb")) {
				getNext(); // Move past the "endb" token
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// stmt-list => statement ; stmt-list | lambda
	private void stmt_list() {
		// Lambda case: return if the token is in FOLLOW(stmt_list), e.g., 'endb',
		// 'exit', or 'until'
		if (token.equals("endb") || token.equals(";") || token.equals("until")) {
			return; // Nothing to do, stmt_list -> λ
		}
		// Check if the token is in the FIRST set of statement
		if (checkName(token) || token.equals("cin") || token.equals("cout") || token.equals("if")
				|| token.equals("while") || token.equals("newb") || token.equals("repeat") || token.equals("call")) {

			statement(); // Parse the statement

			// After parsing a statement, expect a semicolon
			if (token.equals(";")) {
				getNext(); // Move to the next token
				stmt_list(); // Recursively process the next part of stmt_list

			} else {
				// Syntax error: missing semicolon after a statement
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			// If token doesn't match FIRST(stmt_list) or FOLLOW(stmt_list), report an error
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// statement => ass-stmt | inout-stmt | if-stmt | while-stmt | block |
	// repeat-stmt | function-call-stmt
	private void statement() {
		// Determine the type of statement to parse based on the current token
		if (checkName(token) && !token.equals("cin") && !token.equals("cout") && !token.equals("if")
				&& !token.equals("while") && !token.equals("newb") && !token.equals("repeat")
				&& !token.equals("call")) {
			ass_stmt();
		} else if (token.equals("cin") || token.equals("cout")) {
			inout_stmt(); // Parse input/output statement
		} else if (token.equals("if")) {
			if_stmt(); // Parse if statement
		} else if (token.equals("while")) {
			while_stmt(); // Parse while statement
		} else if (token.equals("newb")) {
			block(); // Parse block
		} else if (token.equals("repeat")) {
			repeat_stmt(); // Parse repeat statement
		} else if (token.equals("call")) {
			function_call_stmt(); // Parse function call statement

		} else {
			// Report an error for unexpected tokens
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));

			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//  ass-stmt =>  var-name     :=      exp
	private void ass_stmt() {
		// Step 1: Match and validate var-name (a valid variable name)
		if (checkName(token)) {
			getNext(); // Move to the next token (expecting ':=')
			if (token.equals(":=")) {
				getNext(); // Move to the next token (expecting an expression)
				exp();
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// exp => term exp-prime
	private void exp() {
		term(); // Parse the first term
		exp_prime(); // Parse the continuation (exp-prime)
	}

// exp-prime => add-oper     term     exp-prime       |      lambda	
	private void exp_prime() {
		// Check if the token is in the FIRST set of the alternative "add-oper term
		// exp-prime"
		if (token.equals("+") || token.equals("-")) {
			// Parse the addition/subtraction operator
			add_oper();
			// Parse the next term
			term();
			// Recursively parse more expressions (exp-prime)
			exp_prime();
			if (token.equals("+") || token.equals("-") || token.equals(")") || token.equals("*") || token.equals(";")
					|| token.equals("else") || token.equals("/") || token.equals("mod") || token.equals("div")) {
				// If it is in the FOLLOW set, we do nothing (λ case), simply return
				return;
			} else {
				// Report an error if the token is not expected
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
	}

	// term => factor term-prime
	private void term() {
		factor(); // Parse factor
		term_prime(); // Parse term prime
	}

	// term-prime => mul-oper factor term-prime | lambda
	private void term_prime() {
		// Step 1: Check if the token is in the FIRST set of the alternative "mul-oper
		// factor term-prime"
		if (token.equals("*") || token.equals("/") || token.equals("mod") || token.equals("div")) {
			// Parse the multiplication/division operator
			mul_oper();
			// Parse the next factor
			factor();
			// Recursively parse more terms (term-prime)
			term_prime();
			if (token.equals("+") || token.equals("-") || token.equals(")") || token.equals("*") || token.equals(";")
					|| token.equals("else") || token.equals("/") || token.equals("mod") || token.equals("div")) {
				// If it is in the FOLLOW set, do nothing (λ case), simply return
				return;
			} else {
				// Step 3: Report an error if the token is unexpected
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
	}

	// factor => ( exp ) | “var-name” | “const-name” | value
	private void factor() {
		// Handle based on the token being in the FIRST set of a specific production
		if (token.equals("(")) {
			handleParenthesizedExpression(); // FIRST = '('
		} else if (checkName(token)) {
			handleVariableName(); // FIRST = valid variable names

		} else if (checkValue(token)) {
			handleValue(); // FIRST = valid values
		} else {
			// If no match is found, report an error
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	private void handleVariableName() {
		// Validate if the token is a proper variable name
		if (!checkName(token)) {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
		// Advance to the next token
		getNext();
	}

	private void handleValue() {
		if (!checkValue(token)) { // Check if the token is a valid value
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
		getNext(); // Move to the next token
	}

	private void handleParenthesizedExpression() {
		getNext(); // Consume '('
		exp(); // Parse the expression
		if (token.equals(")")) {
			getNext(); // Consume ')'
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// add-oper => + | -
	private void add_oper() {
		if (token.equals("+")) {
			getNext(); // Consume the '+' operator
		} else if (token.equals("-")) {
			getNext(); // Consume the '-' operator
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//  mul-oper =>  *     |     /       |      mod     |    div
	// Function to parse the 'mul-oper' rule
	private void mul_oper() {
		if (token.equals("*")) {
			getNext(); // Consume the '*' operator
		} else if (token.equals("/")) {
			getNext(); // Consume the '/' operator
		} else if (token.equals("mod")) {
			getNext(); // Consume the 'mod' operator
		} else if (token.equals("div")) {
			getNext(); // Consume the 'div' operator
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// inout-stmt => cin >> var-name | cout << name-value
	private void inout_stmt() {
		if (token.equals("cin")) {
			getNext(); // Consume 'cin'

			if (token.equals(">>")) {
				getNext(); // Consume '>>'

				// Now check if the next token is a valid variable name
				if (checkName(token)) {
					// Handle variable name input
					getNext(); // Consume the valid variable name
				} else {
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else if (token.equals("cout")) {
			getNext(); // Consume 'cout'

			if (token.equals("<<")) {
				getNext(); // Consume '<<'
				name_value();

			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// if-stmt => if (condition) statement else-part
	private void if_stmt() {
		if (token.equals("if")) { // Check if the token is "if"
			getNext(); // Move to the next token

			// Check for opening parenthesis "("
			if (token.equals("(")) {
				getNext(); // Move to the next token (start of condition)

				// Parse the condition (assuming condition is already defined in grammar)
				condition(); // Parse the condition part

				// Check for closing parenthesis ")"
				if (token.equals(")")) {
					getNext(); // Move to the next token (after condition)

					// Parse the statement part (assuming statement function is already defined)
					statement(); // Parse the statement part

					// Parse the optional else-part
					else_part();
				} else {
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

	// else-part => else statement | lambda
	private void else_part() {
		if (token.equals("else")) { // Check if the token is "else"
			getNext(); // Move to the next token (start of statement)
			statement();
		}
	}

	// Helper function to check for end-of-statement tokens (FOLLOW set)
	private boolean isEndOfStatement() {
		return token.equals(";") || token.equals("else");
	}

	// while-stmt => while ( condition ) newb stmt-list endb
	private void while_stmt() {
		// Check if the token is 'while'
		if (token.equals("while")) {
			getNext(); // Move to the next token (after 'while')

			// Check for opening parenthesis "(" for condition
			if (token.equals("(")) {
				getNext(); // Move to the next token (start of condition)

				// Parse the condition part (assuming 'condition' function is already defined)
				condition(); // Parse the condition

				// Check for closing parenthesis ")"
				if (token.equals(")")) {
					getNext(); // Move to the next token (after condition)

					// Check for 'newb' indicating the start of the block
					if (token.equals("newb")) {
						getNext(); // Move to the next token (start of block)

						// Parse the statement list (assuming 'stmt-list' function is defined)
						stmt_list(); // Parse the statement list within the block

						// Check for 'endb' indicating the end of the block
						if (token.equals("endb")) {
							getNext(); // Move to the next token (after endb)
						} else {
							Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
							Main.writeEnabled = false; // Stop further writes
							return;
						}
					} else {
						Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
						Main.writeEnabled = false; // Stop further writes
						return;
					}
				} else {
					Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
					Main.writeEnabled = false; // Stop further writes
					return;
				}
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

// repeat-stmt  =>  repeat      stmt-list       until        condition   
	private void repeat_stmt() {
		// Check if the token is 'repeat'
		if (token.equals("repeat")) {
			getNext(); // Move to the next token (after 'repeat')

			// Parse the statement list (assuming 'stmt-list' function is defined)
			stmt_list(); // Parse the statements inside the repeat block

			// Check for the 'until' keyword
			if (token.equals("until")) {
				getNext(); // Move to the next token (after 'until')

				// Parse the condition (assuming 'condition' function is already defined)
				condition(); // Parse the condition

			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

// condition => name-value       relational-oper        name-value 
	private void condition() {
		name_value();
		relational_oper();
		name_value();

	}

//  name-value =>  “var-name”    |    “const-name”    |      value 
	private void name_value() {

		if (checkName(token)) {
			getNext(); // Consume "/" and move to the next token

		} else if (checkValue(token)) {
			getNext(); // Consume "/" and move to the next token
		} else {
			// If none of the above, report syntax error
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));

			Main.writeEnabled = false; // Stop further writes
			return;
		}

	}

//   relational-oper =>  =      |       =!         |     <     |       =<     |     >     |     =>
	private void relational_oper() {
		if (token.equals("=")) { // Check if the token is the equality operator
			getNext(); // Move to the next token
		} else if (token.equals("=!")) { // Check if the token is the inequality operator
			getNext(); // Move to the next token
		} else if (token.equals("<")) { // Check if the token is the less-than operator
			getNext(); // Move to the next token
		} else if (token.equals("=<")) { // Check if the token is the less-than-or-equal operator
			getNext(); // Move to the next token
		} else if (token.equals(">")) { // Check if the token is the greater-than operator
			getNext(); // Move to the next token
		} else if (token.equals("=>")) { // Check if the token is the greater-than-or-equal operator
			getNext(); // Move to the next token
		} else {
			// If the token is not one of the expected relational operators, log an error
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//function-call-stmt  =>  call function-name
	private void function_call_stmt() {
		if (token.equals("call")) { // Check if the token is 'call'
			getNext(); // Move to the next token (which should be the function name)

			// Ensure the token is a valid function name
			if (checkName(token)) {
				getNext(); // Move to the next token after the function name
			} else {
				Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		} else {
			Main.appendOutput("Syntax Error: Token: '" + token + "' at line " + (lineNumber + 1));
			Main.writeEnabled = false; // Stop further writes
			return;
		}
	}

//name =>  letter ( letter | digit )*
	// Method to check if a token is a valid name based on the rule: name => letter
	// ( letter | digit )*

	private boolean checkName(String token) {
		// Check if the token is a reserved word
		// Reserved words cannot be used as variable names
		if (Vult.reserved.containsKey(token)) {
			return false;
		}

		// Check if the token starts with a number
		// Variable names must start with a letter
		if (token.charAt(0) >= '0' && token.charAt(0) <= '9') {
			return false;
		}
		// Check if the token contains only valid characters (letters and digits)
		// Iterate through each character in the token
		for (int i = 0; i < token.length(); i++) {
			if (!(token.charAt(i) >= 'a' && token.charAt(i) <= 'z')// Lowercase letters
					&& !(token.charAt(i) >= 'A' && token.charAt(i) <= 'Z')// Uppercase letters
					&& !(token.charAt(i) >= '0' && token.charAt(i) <= '9')) {// Digits
				return false;// Invalid character found
			}
		}

		return true; // If all checks pass, the token is a valid name

	}

	// Method to check if a token represents a valid value (integer or real number)
	private boolean checkValue(String token) {
		// Check if the token contains a dot (indicating a real number)
		if (token.contains(".")) {
			return checkForRealValue(token);
		} else {
			return checkForIntValue(token);
		}
	}

	// Method to check if the value is a real (floating-point) number
	private boolean checkForRealValue(String token) {
		// Real numbers cannot start with a dot
		if (token.startsWith(".")) {
			return false;
		}
		// Check each character in the token for validity
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isDigit(token.charAt(i)) && !(token.charAt(i) == '.')) {
				return false;// Invalid character
			}
			try {
				// Ensure there is a digit after a dot
				if (token.charAt(i) == '.' && !Character.isDigit(token.charAt(i + 1))) {
					return false;
				}
			} catch (Exception e) {
				return false;// Handle out-of-bound errors
			}
		}
		return true;
	}

	// Method to check if the value is an integer
	private boolean checkForIntValue(String token) {
		// Check each character in the token for digits only
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isDigit(token.charAt(i))) {
				return false;// Non-digit character found
			}
		}
		return true;

	}

	private void getToken() {
		// Call the helper method to fetch the next token
		getToken_1();
		// Ensure the token is not null, empty, or whitespace-only
		while (this.token == null || this.token.isEmpty() || this.token.equals(" ")) {
			// Call getToken_1() to attempt fetching a valid token
			getToken_1();
			// If the token remains null, it indicates parsing has failed
			if (this.token == null) {
				// Display an error message in the main text area
				Main.textArea.appendText("Parsing failed: No valid token found.");
				// Disable further writes to prevent infinite loops or unnecessary operations
				Main.writeEnabled = false; // Stop further writes
				return;
			}
		}
		// If writing is enabled, append the fetched token to the main text area
		if (Main.writeEnabled) {
			Main.textArea.appendText(this.token + "\n"); // Line number starts at 1
		}
	}

	boolean first = true;

	private void getToken_1() {
		// Check if the current token number has reached the end of the current line
		if (tokenNumber >= tokens.get(lineNumber).size() - 1) {
			// Move to the next line
			lineNumber++;
			// Skip empty lines until a valid line is found or the end of the list is
			// reached
			while (lineNumber < tokens.size() && tokens.get(lineNumber).isEmpty()) {
				// Skip empty lines
				lineNumber++;
			}
			// If no more valid lines are available, return without setting a token
			if (lineNumber >= tokens.size()) {
				// No more lines available
				return;
			}
			// Reset the token number for the new line
			tokenNumber = 0;
		} else {
			// Increment token number if not processing the first token
			if (!first) {
				tokenNumber++;
			} else {
				first = !first;// Set first to false after the initial token
			}
		}
		// Retrieve the next token from the current line if a valid line exists
		if (lineNumber < tokens.size() && !tokens.get(lineNumber).isEmpty()) {
			this.token = tokens.get(lineNumber).get(tokenNumber);
		}
	}

	public void resetState() {
		// Reset static variables to their initial state
		lineNumber = 0;
		tokenNumber = 0;

		// Clear the current token (optional) but only proceed with operations if
		// necessary
		if (token != null) {
			token = null; // Optional: clear token if needed
		}

		// Clear the TextArea
		Main.textArea.clear();

		// Set writeEnabled to true
		Main.writeEnabled = true;

		// Ensure tokens list is clear if required (for demonstration)
		if (tokens != null) {
			tokens.clear(); // Clear tokens only if the list is initialized
		}
	}

	private void getNext() {
		// Move to the next token or the next valid line if necessary
		if (tokenNumber >= tokens.get(lineNumber).size() - 1) {
			lineNumber++;
			// Skip empty or whitespace-only lines until a valid line is found
			while (lineNumber < tokens.size()
					&& (tokens.get(lineNumber).isEmpty() || isLineOnlySpaces(tokens.get(lineNumber)))) {
				// Skip empty lines or lines with only spaces
				lineNumber++;
			}
			// If no more valid lines exist, set token to null and exit
			if (lineNumber >= tokens.size()) {
				// No more lines available
				token = null;
				return;
			}
			// Reset token number for the new line
			tokenNumber = 0;
		} else {
			tokenNumber++;// Move to the next token in the current line
		}
		// Retrieve the current token if a valid line is found
		if (lineNumber < tokens.size() && !tokens.get(lineNumber).isEmpty()) {
			this.token = tokens.get(lineNumber).get(tokenNumber);
		}
		// If the current token is invalid, call this method recursively to find a valid
		// one
		if (token == null || token.isEmpty() || token.trim().isEmpty()) {
			getNext();
		}

		// Print the current token for debugging
		if (token != null) {
			if (Main.writeEnabled) {
				Main.textArea.appendText(token + "\n"); // Line number starts at 1
			}
		}
	}

	// Helper method to check if a line contains only spaces
	private boolean isLineOnlySpaces(ArrayList<String> lineTokens) {
		for (String token : lineTokens) {
			if (!token.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
