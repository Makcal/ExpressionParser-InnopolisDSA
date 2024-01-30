// Author: Maxim Fomin
import java.util.Iterator;
import java.util.Scanner;
import java.util.List;

public final class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        IList<Token> result = ShutingYard.process(ShutingYard.parse(line));
        for (Token token : result) {
            System.out.print(token + " ");
        }
                System.out.println();
    }
}

interface ICollection {
    int size();
}

interface IDeque<T> extends IQueue<T> {
    T head();

    T tail();

    void putFront(T e);

    T popBack();
}

interface IList<T> extends IDeque<T> {

}

interface IQueue<T> extends ICollection, Iterable<T> {

    void putBack(T e);

    T popFront();
}

interface IStack<T> extends ICollection {
    T top();

    T pop();

    void push(T elem);
}

class LinkedList<T> implements IStack<T>, IList<T> {
    private final Node sentinel = new Node();
    int size = 0;

    @Override
    public T head() {
        if (size == 0) {
            throw new RuntimeException("The list is empty");
        }

        return sentinel.next.element;
    }

    @Override
    public T tail() {
        if (size == 0) {
            throw new RuntimeException("The list is empty");
        }

        return sentinel.prev.element;
    }

    @Override
    public void putFront(T e) {
        Node new_node = new Node();
        new_node.element = e;
        size++;

        new_node.prev = sentinel;
        new_node.next = sentinel.next;
        sentinel.next.prev = new_node;
        sentinel.next = new_node;
    }

    @Override
    public void putBack(T e) {
        Node new_node = new Node();
        new_node.element = e;
        size++;

        new_node.next = sentinel;
        new_node.prev = sentinel.prev;
        sentinel.prev.next = new_node;
        sentinel.prev = new_node;
    }

    @Override
    public T popFront() {
        if (size == 0) {
            throw new RuntimeException("The list is empty");
        }

        T last = head();
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return last;
    }

    @Override
    public T popBack() {
        if (size == 0) {
            throw new RuntimeException("The list is empty");
        }

        T last = tail();
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return last;
    }

    @Override
    public T top() {
        return tail();
    }

    @Override
    public T pop() {
        return popBack();
    }

    @Override
    public void push(T element) {
        putBack(element);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        for (T e : this) {
            builder.append(e.toString()).append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    private class Node {
        T element;
        Node next, prev;

        Node() {
            element = null;
            next = prev = this;
        }
    }

    private class MyIterator implements Iterator<T> {
        Node current;

        public MyIterator() {
            current = sentinel;
        }

        @Override
        public boolean hasNext() {
            return current.next != sentinel;
        }

        @Override
        public T next() {
            current = current.next;
            return current.element;
        }
    }
}

class ShutingYard {
    public static IList<Token> process(IQueue<Token> tokens) {
        IStack<Token> temp = new LinkedList<>();
        IList<Token> output = new LinkedList<>();

        /*
        Details: https://en.wikipedia.org/wiki/Shunting_yard_algorithm
                 https://ru.wikipedia.org/wiki/Алгоритм_сортировочной_станции
         */
        for (Token token : tokens) {
            if (token.getType() == TokenType.NUMBER) {
                output.putBack(token);
            }
            else if (token.getType() == TokenType.FUNCTION) {
                temp.push(token);
            }
            else if (token.getType() == TokenType.SYMBOL
                    && ((TokenSymbol) token).getSymbolType() == TokenSymbolType.COMMA) {
                while (temp.size() != 0 && (
                        temp.top().getType() != TokenType.SYMBOL
                                || ((TokenSymbol) temp.top()).getSymbolType() != TokenSymbolType.LEFT_PARENTHESIS
                )) {
                    output.putBack(temp.pop());
                }
            }
            else if (token.getType() == TokenType.OPERATOR) {
                while (temp.size() != 0
                        && temp.top().getType() == TokenType.OPERATOR
                        && ((TokenOperator<?>) temp.top()).compareTo((TokenOperator<?>) token) >= 0) {
                    output.putBack(temp.pop());
                }
                temp.push(token);
            }
            else if (token.getType() == TokenType.SYMBOL
                    && ((TokenSymbol) token).getSymbolType() == TokenSymbolType.LEFT_PARENTHESIS) {
                temp.push(token);
            }
            else if (token.getType() == TokenType.SYMBOL
                    && ((TokenSymbol) token).getSymbolType() == TokenSymbolType.RIGHT_PARENTHESIS) {
                while (temp.size() != 0 && (
                        temp.top().getType() != TokenType.SYMBOL
                                || ((TokenSymbol) temp.top()).getSymbolType() != TokenSymbolType.LEFT_PARENTHESIS
                )) {
                    output.putBack(temp.pop());
                }
                temp.pop();  // should be a left parenthesis
                if (temp.size() != 0 && temp.top().getType() == TokenType.FUNCTION) {
                    output.putBack(temp.pop());
                }
            }
        }

        while (temp.size() != 0) {
            output.putBack(temp.pop());
        }

        return output;
    }

    public static IList<Token> parse(String input) {
        Scanner scanner = new Scanner(input);
        IList<Token> tokens = new LinkedList<>();
        while (scanner.hasNext()) {
            tokens.putBack(Token.parse(scanner.next()));
        }
        return tokens;
    }
}

abstract class Token {
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

enum TokenType {
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

