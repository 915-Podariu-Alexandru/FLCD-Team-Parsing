import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

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
        System.out.println("7. Print First(X) where X is a non-terminal");
        System.out.println("8. Print Follow(X) where X is a non-terminal");
        System.out.println("9. Print Parsing Tree Table for grammar 5");
    }

    public static void runGrammar(String filepath) throws IOException {
        Grammar grammar = new Grammar(filepath);
        boolean done = false;
        while(!done) {
            printMenu();
            Scanner scanner;
            String nonTerminal;
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
                    scanner= new Scanner(System.in);
                    System.out.print("Enter a non-terminal: ");
                    nonTerminal= scanner.nextLine();
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
                case 7:
                    scanner= new Scanner(System.in);
                    System.out.print("Enter a non-terminal: ");
                    nonTerminal= scanner.nextLine();
                    System.out.println("\n\n First set for the non-terminal: " + nonTerminal);
                    try
                    {
                        System.out.println(grammar.getFirstForSymbol(nonTerminal));
                    }
                    catch (NullPointerException e)
                    {
                        System.out.println("This is not a defined non-terminal");
                    }
                    break;
                case 8:
                    scanner= new Scanner(System.in);
                    System.out.print("Enter a non-terminal: ");
                    nonTerminal= scanner.nextLine();
                    System.out.println("\n\n Follow set for the non-terminal: " + nonTerminal);
                    try
                    {
                        System.out.println(grammar.getFollowForSymbol(nonTerminal));
                    }
                    catch (NullPointerException e)
                    {
                        System.out.println("This is not a defined non-terminal");
                    }
                    break;
                case 9:
                    Map<String, Map<String, List<String>>> ll1Table = grammar.buildLL1Table();
                    System.out.println("Is Table LL1 compatible? " + grammar.checkLL1());
                    System.out.println("LL(1) Table:");
                    for (String s : ll1Table.keySet()) {
                        System.out.println(s + ": " + ll1Table.get(s));
                    }

                    ParseTreeNode parseTreeRoot = grammar.buildParseTree("S", 0, ll1Table, new ArrayList<>());
                    if (parseTreeRoot == null) {
                        System.out.println("The sequence " + grammar.getSequence().toString() + " is invalid");
                        break;
                    }

                    // Create ParserOutput instance with the parse tree
                    ParserOutput parserOutput = new ParserOutput(parseTreeRoot);

                    // Perform required operations on the ParserOutput instance
                    Map<String, Object> transformedRepresentation = parserOutput.transformTreeRepresentation();
                    System.out.println("Transformed Tree Representation:");
                    System.out.println(transformedRepresentation);

                    // print to screen
                    parserOutput.printToScreen(0);

                    parserOutput.printToFile("output.txt");
            }

        }

        start();
    }


    public static void start() throws IOException {
        System.out.println("0. Exit");
        System.out.println("1. Use Grammar 1");
        System.out.println("2. Use Grammar 2");
        System.out.println("3. Use Grammar 3");
        System.out.println("4. Use Grammar 4");
        System.out.println("5. Use Grammar 5");
        System.out.println("6. Run Tests For g3.txt");
        System.out.println("Your option: ");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        Tests Test = new Tests();

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
            case 4:
                runGrammar("io/g4.txt");
                break;
            case 5:
                runGrammar("io/g5.txt");
            case 6:
                Test.runTests();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid command!");
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}