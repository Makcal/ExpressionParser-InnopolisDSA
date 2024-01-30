import java.util.List;

public abstract class Token {
    private final TokenType type;

    protected Token(TokenType type) {
        this.type = type;
    }

    TokenType getType() {
        return type;
    }

    public abstract String toString();

    public static Token parse(String s) {
        return switch (TokenType.parse(s)) {
            case NUMBER -> new TokenNumber<>(Integer.parseInt(s));
            case SYMBOL -> new TokenSymbol(TokenSymbolType.parse(s));
            case OPERATOR -> TokenOperator.parse(s);
            case FUNCTION -> TokenFunction.parse(s);
        };
    }
}

class TokenSymbol extends Token {
    private final TokenSymbolType type;

    protected TokenSymbol(TokenSymbolType symbol) {
        super(TokenType.SYMBOL);
        this.type = symbol;
    }

    public TokenSymbolType getSymbolType() {
        return type;
    }

    @Override
    public String toString() {
        return type.symbol;
    }
}

class TokenNumber<T extends Number> extends Token {
    private final T value;

    protected TokenNumber(T value) {
        super(TokenType.NUMBER);
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public T getValue() {
        return value;
    }
}

interface IComputer<R, T> {
    int argsNumber();

    R compute(List<T> args);
}

class OperatorPrecedence {
    public static final int ADDITION = 1;
    public static final int MULTIPLICATION = ADDITION + 1;
}

abstract class TokenOperator<T> extends Token implements IComputer<T, T>, Comparable<TokenOperator<?>> {
    private final int priority;

    protected TokenOperator(int priority) {
        super(TokenType.OPERATOR);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(TokenOperator<?> o) {
        return priority - o.priority;
    }

    public static TokenOperator<?> parse(String s) {
        if (s.matches("[+\\-*/]")) {
            return TokenBinaryOperator.parse(s);
        }
        throw new RuntimeException("Unknown operator");
    }
}

abstract class TokenBinaryOperator<T> extends TokenOperator<T> {
    protected TokenBinaryOperator(int priority) {
        super(priority);
    }

    @Override
    public final int argsNumber() {
        return 2;
    }

    @Override
    public final T compute(List<T> args) {
        return compute(args.get(0), args.get(1));
    }

    protected abstract T compute(T left, T right);

    public static TokenBinaryOperator<?> parse(String s) {
        return switch (s) {
            case TokenPlus.SYMBOL -> TokenPlus.getPlus();
            case TokenMinus.SYMBOL -> TokenMinus.getMinus();
            case TokenMultiply.SYMBOL -> TokenMultiply.getMultiply();
            case TokenDivide.SYMBOL -> TokenDivide.getDivide();
            default -> throw new RuntimeException("Unknown operator");
        };
    }
}

class TokenPlus extends TokenBinaryOperator<Integer> {
    public static final String SYMBOL = "+";

    private TokenPlus() {
        super(OperatorPrecedence.ADDITION);
    }

    @Override
    public Integer compute(Integer left, Integer right) {
        return left + right;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    static TokenPlus singleton = null;

    static TokenPlus getPlus() {
        if (singleton == null) {
            return singleton = new TokenPlus();
        }
        return singleton;
    }
}

class TokenMinus extends TokenBinaryOperator<Integer> {
    public static final String SYMBOL = "-";

    private TokenMinus() {
        super(OperatorPrecedence.ADDITION);
    }

    @Override
    public Integer compute(Integer left, Integer right) {
        return left - right;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    static TokenMinus singleton = null;

    static TokenMinus getMinus() {
        if (singleton == null) {
            return singleton = new TokenMinus();
        }
        return singleton;
    }
}

class TokenMultiply extends TokenBinaryOperator<Integer> {
    public static final String SYMBOL = "*";

    private TokenMultiply() {
        super(OperatorPrecedence.MULTIPLICATION);
    }

    @Override
    public Integer compute(Integer left, Integer right) {
        return left * right;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    static TokenMultiply singleton = null;

    static TokenMultiply getMultiply() {
        if (singleton == null) {
            return singleton = new TokenMultiply();
        }
        return singleton;
    }
}

class TokenDivide extends TokenBinaryOperator<Integer> {
    public static final String SYMBOL = "/";

    private TokenDivide() {
        super(OperatorPrecedence.MULTIPLICATION);
    }

    @Override
    public Integer compute(Integer left, Integer right) {
        return left / right;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    static TokenDivide singleton = null;

    static TokenDivide getDivide() {
        if (singleton == null) {
            return singleton = new TokenDivide();
        }
        return singleton;
    }
}

abstract class TokenFunction<R, T> extends Token implements IComputer<R, T> {
    protected TokenFunction() {
        super(TokenType.FUNCTION);
    }

    public static TokenFunction<?, ?> parse(String s) {
        if (TokenMin.SYMBOL.equals(s)) {
            return TokenMin.getMin();
        }
        else if (TokenMax.SYMBOL.equals(s)) {
            return TokenMax.getMax();
        }
        throw new RuntimeException("Unknown operator");
    }
}

class TokenMin extends TokenFunction<Integer, Integer> {
    public static final String SYMBOL = "min";

    @Override
    public int argsNumber() {
        return 2;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    @Override
    public Integer compute(List<Integer> args) {
        return Math.min(args.get(0), args.get(1));
    }

    static TokenMin singleton = null;

    static TokenMin getMin() {
        if (singleton == null) {
            return singleton = new TokenMin();
        }
        return singleton;
    }
}

class TokenMax extends TokenFunction<Integer, Integer> {
    public static final String SYMBOL = "max";

    @Override
    public int argsNumber() {
        return 2;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    @Override
    public Integer compute(List<Integer> args) {
        return Math.max(args.get(0), args.get(1));
    }

    static TokenMax singleton = null;

    static TokenMax getMax() {
        if (singleton == null) {
            return singleton = new TokenMax();
        }
        return singleton;
    }
}
