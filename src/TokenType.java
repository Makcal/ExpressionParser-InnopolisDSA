public enum TokenType {
    NUMBER("[0-9]+"),
    FUNCTION("min|max"),
    OPERATOR("[+\\-*/]"),
    SYMBOL(",|\\(|\\)");

    final String regexp;

    TokenType(String pattern) {
        regexp = pattern;
    }

    public static TokenType parse(String s) {
        for (TokenType type : TokenType.values()) {
            if (s.matches(type.regexp)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown token");
    }
}

enum TokenSymbolType {
    COMMA(","),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")");

    final String symbol;

    TokenSymbolType(String symbol) {
        this.symbol = symbol;
    }

    public static TokenSymbolType parse(String s) {
        for (TokenSymbolType type : TokenSymbolType.values()) {
            if (type.symbol.equals(s)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown symbol");
    }
}
