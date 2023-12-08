import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    private final String ELEMENT_SEPARATOR = "@";

    private final String SEPARATOR_OR_TRANSITION = "\\|";
    private final String TRANSITION_CONCATENATION = " ";
    private final String EPSILON = "EPS";
    private final String SEPARATOR_LEFT_RIGHT_HAND_SIDE = "->";

    // LL(1)
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<List<String>, Set<List<String>>> productions;
    private String startingSymbol;
    private boolean isCFG;


    private void processProduction(String production) {
        if (production.equals(""))
            return;

        String[] productionString = production.split(this.SEPARATOR_LEFT_RIGHT_HAND_SIDE);
        List<String> splitLeft = List.of(productionString[0].split(this.TRANSITION_CONCATENATION));
        String[] splitRight = productionString[1].split(this.SEPARATOR_OR_TRANSITION);

        this.productions.putIfAbsent(splitLeft, new HashSet<>());

        for (String RHElement : splitRight)
            if (!RHElement.equals(" "))
                this.productions
                        .get(splitLeft)
                        .add(Arrays.stream(RHElement.split(this.TRANSITION_CONCATENATION)).filter(Objects::nonNull).collect(Collectors.toList()));

    }

    private void readFile(String file) {
        try (Scanner scanner = new Scanner(new File(file)))
        {
            this.nonTerminals = new HashSet<>(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.terminals = new HashSet<>(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.startingSymbol = scanner.nextLine();
            this.productions = new HashMap<>();

            while (scanner.hasNextLine())
                this.processProduction(scanner.nextLine());

            this.isCFG = this.checkIfCFG();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private boolean checkIfCFG() {
        if (!this.nonTerminals.contains(this.startingSymbol))
            return false;

        for (List<String> leftHandSide : this.productions.keySet())
        {
            if (leftHandSide.size() != 1 || !this.nonTerminals.contains(leftHandSide.get(0))) {
                return false;
            }

            for (List<String> possibleNextMoves : this.productions.get(leftHandSide))
                for (String possibleNextMove : possibleNextMoves)
                    if (possibleNextMove.equals("")
                            && !this.nonTerminals.contains(possibleNextMove)
                            && !this.terminals.contains(possibleNextMove)
                            && !possibleNextMove.equals(this.EPSILON)) {
                        return false;
                    }
        }
        return true;
    }

    public Grammar(String filePath) {
        this.readFile(filePath);
    }

    public Set<String> getNonTerminals() {
        return this.nonTerminals;
    }

    public String setToString(Set<String> set) {
        String finalString = "";

        for (String element : set)
            if (!element.equals(""))
                finalString += element + ", ";

        return finalString.replaceAll(", $", "");
    }

    public String listToString(List<String> list) {
        String finalString = "";

        for (String element : list)
            if (!element.equals(""))
                finalString += element + ", ";

        return finalString.replaceAll(", $", "");
    }

    public String rhsToString(Set<List<String>> rhs){
        String finalString = "";

        for (List<String> list : rhs)
        {
            finalString += " { ";
            for (String element : list)
                if (!element.equals(""))
                {
                    if( element.equals(list.getLast()) )
                        finalString += element;
                    else
                        finalString += element + " ";
                }
            finalString += " } ";
        }

        return finalString.replaceAll(", $", "");
    }

    public Set<String> getTerminals() {
        return this.terminals;
    }

    public Map<List<String>, Set<List<String>>> getProductions() {
        return this.productions;
    }

    public String getStartingSymbol() {
        return this.startingSymbol;
    }

    public boolean isCFG() {
        return this.isCFG;
    }

}