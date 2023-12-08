import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static void printToFile(String filePath, Object object)
    {
        try (PrintStream printStream = new PrintStream(filePath))
        {
            printStream.println(object);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public static void printMenu()
    {
        System.out.println("\n0. Exit");
        System.out.println("1. Print non-terminals");
        System.out.println("2. Print terminals");
        System.out.println("3. Print starting symbol");
        System.out.println("4. Print all productions");
        System.out.println("5. Print all productions for a non terminal");
        System.out.println("6. Is the grammar a context free grammar (CFG) ?");

    }

    public static void runGrammar(String filepath)
    {
        Grammar grammar = new Grammar(filepath);
        boolean done = false;
        while(!done) {
            printMenu();
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Enter your option");
            int option = keyboard.nextInt();
            switch (option)
            {
                case 0:
                    done = true;
                    break;
                case 1:
                    System.out.println("\n\nNon-terminals -> " + grammar.setToString(grammar.getNonTerminals()));
                    break;
                case 2:
                    System.out.println("\n\nTerminals -> " + grammar.setToString(grammar.getTerminals()));
                    break;
                case 3:
                    System.out.println("\n\nStarting symbol -> " + grammar.getStartingSymbol());
                    break;
                case 4:
                    System.out.println("\n\nAll productions: ");
                    grammar.getProductions().forEach((lhs, rhs)-> System.out.println(grammar.listToString(lhs) + " -> " + grammar.rhsToString(rhs)));
                    break;
                case 5:
                    Scanner scanner= new Scanner(System.in); //System.in is a standard input stream.
                    System.out.print("Enter a non-terminal: ");
                    String nonTerminal= scanner.nextLine(); //reads string.
                    System.out.println("\n\n Productions for the non-terminal: " + nonTerminal);
                    List<String> key = new ArrayList<>();
                    key.add(nonTerminal);
                    try
                    {
                        grammar.getProductions().get(key).forEach((rhs) -> System.out.println(key + " -> " + grammar.listToString(rhs)));
                    }
                    catch (NullPointerException e)
                    {
                        System.out.println("This is not a defined non-terminal");
                    }
                    break;
                case 6:
                    System.out.println("\n\nIs it a context free grammar (CFG) ? " + grammar.isCFG());
                    break;
            }
        }

        start();
    }


    public static void start()
    {
        System.out.println("0. Exit");
        System.out.println("1. Use Grammar 1");
        System.out.println("2. Use Grammar 2");
        System.out.println("3. Use Grammar 3");
        System.out.println("Your option: ");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option)
        {
            case 1:
                runGrammar("io/g1.txt");
                break;
            case 2:
                runGrammar("io/g2.txt");
                break;
            case 3:
                runGrammar("io/g3.txt");
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid command!");
                break;
        }
    }

    public static void main(String[] args) {
        start();
    }
}