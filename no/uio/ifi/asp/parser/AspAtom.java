package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

abstract class AspAtom extends AspSyntax {

    AspAtom(int n) {
        super(n);
    }

    static AspAtom parse(Scanner s) {
        enterParser("atom");

        AspAtom aa = null;

        // find what type of Atom this is
        if (s.curToken().kind == nameToken) {
            aa = AspName.parse(s);
        } else if (s.curToken().kind == integerToken) {
            aa = AspIntegerLiteral.parse(s);
        } else if (s.curToken().kind == floatToken) {
            aa = AspFloatLiteral.parse(s);
        } else if (s.curToken().kind == stringToken) {
            aa = AspStringLiteral.parse(s);
        } else if (s.curToken().kind == trueToken || s.curToken().kind == falseToken) {
            aa = AspBooleanLiteral.parse(s);
        } else if (s.curToken().kind == noneToken) {
            aa = AspNoneLiteral.parse(s);
        } else if (s.curToken().kind == leftParToken) {
            aa = AspInnerExpr.parse(s);
        } else if (s.curToken().kind == leftBracketToken) {
            aa = AspListDisplay.parse(s);
        } else if (s.curToken().kind == leftBraceToken) {
            aa = AspDictDisplay.parse(s);
        } else {
            parserError("Expected an atom but found a " + s.curToken().kind, s.curLineNum());
        }

        leaveParser("atom");
        return aa;
    }
}
