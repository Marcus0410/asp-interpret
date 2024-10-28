// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.scanner;

import java.io.*;
import java.util.*;

import no.uio.ifi.asp.main.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class Scanner {
	private LineNumberReader sourceFile = null;
	private String curFileName;
	private ArrayList<Token> curLineTokens = new ArrayList<>();
	private Stack<Integer> indents = new Stack<>();
	private final int TABDIST = 4;

	public Scanner(String fileName) {
		curFileName = fileName;
		indents.push(0);

		try {
			sourceFile = new LineNumberReader(
					new InputStreamReader(
							new FileInputStream(fileName),
							"UTF-8"));
		} catch (IOException e) {
			scannerError("Cannot read " + fileName + "!");
		}
	}

	private void scannerError(String message) {
		String m = "Asp scanner error";
		if (curLineNum() > 0)
			m += " on line " + curLineNum();
		m += ": " + message;

		Main.error(m);
	}

	public Token curToken() {
		while (curLineTokens.isEmpty()) {
			readNextLine();
		}
		return curLineTokens.get(0);
	}

	public void readNextToken() {
		if (!curLineTokens.isEmpty())
			curLineTokens.remove(0);
	}

	private void readNextLine() {
		curLineTokens.clear();

		// Read the next line:
		String line = null;
		try {
			line = sourceFile.readLine();
			if (line == null) {
				sourceFile.close();
				sourceFile = null;
			} else {
				Main.log.noteSourceLine(curLineNum(), line);
			}
		} catch (IOException e) {
			sourceFile = null;
			scannerError("Unspecified I/O error!");
		}

		// if we have reached the end of the file
		if (line == null) {
			// add dedents before E-o-F if necessary
			for (Integer i : indents) {
				if (i > 0) {
					Token dedToken = new Token(dedentToken, curLineNum());
					curLineTokens.add(dedToken);
				}
			}
			// add E-o-F token
			curLineTokens.add(new Token(eofToken));

			// log tokens
			for (Token t : curLineTokens)
				Main.log.noteToken(t);
			return;
		}

		line = expandLeadingTabs(line); // change TABs to blanks

		int pos = 0; // position in line

		// loop through characters in line
		while (pos < line.length()) {
			char c = line.charAt(pos++); // get char at pos
			String curString = "";

			// Check for whitespace and skip it
			if (Character.isWhitespace(c)) {
				continue;
			}

			// check for comment
			if (c == '#') {
				break; // stop reading line
			}

			// check if this is an integer og float
			if (isDigit(c)) {
				// either integer or float

				// add all digits and '.' to curString
				while (isDigit(c) || c == '.') {
					curString += c;

					// if valid index
					if (pos < line.length())
						c = line.charAt(pos++);
					// if pos is not an index in line
					else
						break;
				}

				// if c is not a . or digit, go back one char for next token
				if (!(isDigit(c) || c == '.'))
					pos--;

				// if float
				if (curString.contains(".")) {
					Token t = new Token(floatToken, curLineNum());
					t.floatLit = Double.parseDouble(curString);
					curLineTokens.add(t);
				}
				// if integer
				else {
					Token t = new Token(integerToken, curLineNum());
					t.integerLit = Long.parseLong(curString);
					curLineTokens.add(t);
				}
			}

			// Check if this is a keyword or name token
			else if (isLetterAZ(c)) {
				// while c is a letter or a digit -> add c to curString and go to next c
				while (isLetterAZ(c) || isDigit(c)) {
					curString += c; // add current char to curString

					// if valid index
					if (pos < line.length())
						c = line.charAt(pos++);
					// if pos is not an index in line
					else
						break;
				}

				// if c is not a letter or digit, go back one char for next token
				if (!(isLetterAZ(c) || isDigit(c)))
					pos--;

				Token t = findToken(curString);

				// if no keyword token was found
				if (t == null) {
					// this is a name token
					t = new Token(nameToken, curLineNum());
					t.name = curString;
				}
				curLineTokens.add(t);
			}
			// Check if string
			else if (c == '"' || c == '\'') {
				char end = c; // what the end of the string will be
				c = line.charAt(pos++);

				// loop until c == end
				while (c != end && pos < line.length()) {
					curString += c;
					c = line.charAt(pos++);
				}

				// add to list of tokens
				Token t = new Token(stringToken, curLineNum());
				t.stringLit = curString;
				curLineTokens.add(t);
			}
			// check if operator
			else {
				Token t = null;
				char nextC;

				// look for two char tokens if there is a next char on the line
				if (pos < line.length()) {
					// find next c
					nextC = line.charAt(pos);
					// make a string with c and nextC
					String twoC = Character.toString(c).concat(Character.toString(nextC));

					t = findToken(twoC);
				}
				// if not a two char token, look for single char token
				if (t == null) {
					t = findToken(Character.toString(c));
				}
				// found two char token, skip next char
				else {
					pos++;
				}

				// add token if found
				if (t != null)
					curLineTokens.add(t);
			}

		}

		// add indents or dedents if necessary
		// Terminate line if there are tokens on the line, else dont add NEWLINE token
		if (curLineTokens.size() != 0) {
			int n = findIndent(line);
			// Checking if n is bigger than top of indents, and adds indentToken to
			// curLineTokens
			if (n > indents.peek()) {
				indents.push(n);
				Token t = new Token(indentToken, curLineNum());
				curLineTokens.add(0, t);
			} else {
				while (n < indents.peek()) {
					indents.pop();
					Token dt = new Token(dedentToken, curLineNum());
					curLineTokens.add(0, dt);
				}
			}
			curLineTokens.add(new Token(newLineToken, curLineNum()));
		}

		// log tokens
		for (Token t : curLineTokens)
			Main.log.noteToken(t);

	}

	// return a token if found, else return null
	private Token findToken(String s) {
		for (TokenKind t : TokenKind.values()) {
			if (t.toString().equals(s)) {
				return new Token(t, curLineNum());
			}
		}
		return null;
	}

	public int curLineNum() {
		return sourceFile != null ? sourceFile.getLineNumber() : 0;
	}

	private int findIndent(String s) {
		int indent = 0;

		while (indent < s.length() && s.charAt(indent) == ' ')
			indent++;
		return indent;
	}

	private String expandLeadingTabs(String s) {
		if (s == null) {
			return null;
		}

		String newS = "";
		int n = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == ' ') {
				newS += " ";
				n++;
			} else if (c == '\t') {
				int nReplace = 4 - (n % 4);
				for (int j = 0; j < nReplace; j++) {
					newS += " ";
				}
				n += nReplace;
			} else {
				newS += s.substring(i);
				break;
			}

		}

		return newS;
	}

	private boolean isLetterAZ(char c) {
		return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || (c == '_');
	}

	private boolean isDigit(char c) {
		return '0' <= c && c <= '9';
	}

	public boolean isCompOpr() {
		TokenKind k = curToken().kind;

		if (k == TokenKind.lessToken ||
				k == TokenKind.greaterToken ||
				k == TokenKind.doubleEqualToken ||
				k == TokenKind.greaterEqualToken ||
				k == TokenKind.lessEqualToken ||
				k == TokenKind.notEqualToken) {
			return true;
		}

		return false;
	}

	public boolean isFactorPrefix() {
		TokenKind k = curToken().kind;

		if (k == TokenKind.plusToken || k == TokenKind.minusToken)
			return true;

		return false;
	}

	public boolean isFactorOpr() {
		TokenKind k = curToken().kind;

		if (k == TokenKind.astToken ||
				k == TokenKind.slashToken ||
				k == TokenKind.percentToken ||
				k == TokenKind.doubleSlashToken)
			return true;

		return false;
	}

	public boolean isTermOpr() {
		TokenKind k = curToken().kind;

		if (k == TokenKind.plusToken || k == TokenKind.minusToken)
			return true;

		return false;
	}

	public boolean anyEqualToken() {
		for (Token t : curLineTokens) {
			if (t.kind == equalToken)
				return true;
			if (t.kind == semicolonToken)
				return false;
		}
		return false;
	}

	// checks if current token is a possible atom
	public boolean isAtom() {
		TokenKind k = curToken().kind;

		if (k == nameToken
				|| k == integerToken || k == floatToken
				|| k == stringToken || k == trueToken || k == falseToken
				|| k == noneToken || k == leftParToken
				|| k == leftBracketToken || k == leftBracketToken)
			return true;

		return false;
	}
}
