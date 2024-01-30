import java.util.Scanner;

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
