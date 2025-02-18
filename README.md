Recursive Descent Parser for a Programming Language
This project implements a recursive descent parser for a programming language defined by a given grammar. The parser is written in Java and follows a top-down parsing approach, where each non-terminal in the grammar is implemented as a separate recursive method. The parser reads an input program and verifies whether it adheres to the grammar rules.

Key Features:
1-Lexical Analysis: The parser scans the input program, identifying tokens such as keywords, identifiers, operators, and literals.
2-Syntax Analysis: It checks the structure of the program based on the given grammar and ensures that all rules are followed.
3-Error Handling: If a syntax error occurs, the parser reports it clearly with the line number and token where the error was detected. The program then exits in panic mode to prevent further incorrect parsing.
4-Recursive Parsing: The parser includes methods for handling different grammatical structures such as declarations, function definitions, expressions, conditions, and control statements (if, while, repeat).
5-Semantic Checks: Basic validation, such as ensuring correct data types and proper function calls, is also included.

How It Works:
1-Tokenization: The program is read from a file, and tokens are extracted.
2-Parsing: The parser calls recursive functions to match tokens against the grammar rules.
3-Error Detection: If an unexpected token is encountered, an error is reported.
4-Program Validation: If no errors occur, the program is successfully parsed.

This project provides a foundation for building compilers or interpreters for custom programming languages. The implementation ensures strict syntax validation and serves as an educational tool for understanding recursive descent parsing techniques in Java.

<img width="677" alt="image" src="https://github.com/user-attachments/assets/82e4dec5-e76c-45b9-babe-29dabaa21021" />

<img width="677" alt="image" src="https://github.com/user-attachments/assets/1c1d79a6-00c6-492f-84b7-e3992bbfc453" />




