import java.util.Scanner;

public class Main {
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
