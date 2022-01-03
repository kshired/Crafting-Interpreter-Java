package com.kshired.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kshired.lox.TokenType.*;
import static java.lang.Character.isDigit;

public class Scanner {
    private final String src;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // reserved word
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public Scanner(String src) {
        this.src = src;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if(match('*')){
                    while (peek() != '*' && !isAtEnd()) {
                        advance();
                    }
                    if (isAtEnd()){
                        Lox.error(line, "Unexpected character.");
                        break;
                    }
                    advance();
                    if(peek() != '/'){
                        Lox.error(line, "Unexpected character.");
                        break;
                    }
                    advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            case 'o':
                if (match('r')) {
                    addToken(OR);
                }
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        while(isAlphaNumeric(peek())){
            advance();
        }
        addToken(IDENTIFIER);

        String text = src.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null){
            type = IDENTIFIER;
        }
        addToken(type);
    }


    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(src.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= src.length()) {
            return '\0';
        }
        return src.charAt(current + 1);
    }

    private void string() {
        // find end of the string
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        // if not terminate with '"'
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // good terminate
        advance();

        String value = src.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return src.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (src.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private boolean isAtEnd() {
        return current >= src.length();
    }

    private char advance() {
        return src.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = src.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}


