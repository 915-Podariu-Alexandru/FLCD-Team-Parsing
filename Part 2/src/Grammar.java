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
    private Map<String, Set<String>> firstSets;
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<List<String>, Set<List<String>>> productions;
    private String startingSymbol;
    private boolean isCFG;

    private void computeFirstSets() {
        this.firstSets = new HashMap<>();
        boolean changes;

        //Initialize First sets for Terminals
        for (String terminal : this.terminals)
            this.firstSets.put(terminal, new HashSet<>(Collections.singletonList(terminal)));

        //Initialize First sets for Non-Terminals
        for (String nonTerminal : this.nonTerminals)
            this.firstSets.put(nonTerminal, new HashSet<>());

        do
        {
            changes = false;
            //Iterate through each Production
            for (Map.Entry<List<String>, Set<List<String>>> entry : this.productions.entrySet())
            {
                List<String> leftHandSide = entry.getKey();
                Set<List<String>> rightHandSides = entry.getValue();

                //Iterate through each right-hand-side of the Production
                for (List<String> rightHandSide : rightHandSides)
                {
                    int i = 0;

                    //For each symbol
                    while (i < rightHandSide.size()) {
                        String symbol = rightHandSide.get(i);

                        // If the Symbol is a non-terminal, add its First set to the current non-terminal's First set
                        if (this.nonTerminals.contains(symbol))
                        {
                            Set<String> firstSetOfSymbol = this.firstSets.get(symbol);
                            Set<String> firstSetOfNonTerminal = this.firstSets.get(leftHandSide.get(0));

                            if (firstSetOfNonTerminal.addAll(firstSetOfSymbol))
                                changes = true;

                            // If EPSILON is not in the First set of the symbol, then stop
                            if (!firstSetOfSymbol.contains(this.EPSILON))
                                break;

                        }
                        // If the Symbol is a terminal, then add it to the current non-terminal's First set
                        else if (this.terminals.contains(symbol)) {
                            Set<String> firstSetOfNonTerminal = this.firstSets.get(leftHandSide.get(0));

                            if (firstSetOfNonTerminal.add(symbol))
                                changes = true;

                            break;
                        }
                        //If the Symbol is Epsilon, then add it to the current non-terminal's First set
                        else
                        {
                            Set<String> firstSetOfNonTerminal = this.firstSets.get(leftHandSide.get(0));

                            if (firstSetOfNonTerminal.add(this.EPSILON))
                                changes = true;

                            i++;
                        }
                    }

                    // If the whole right-hand side can derive Epsilon, then add Epsilon to the First set of the non-terminal
                    if (i == rightHandSide.size())
                    {
                        Set<String> firstSetOfNonTerminal = this.firstSets.get(leftHandSide.get(0));

                        if (firstSetOfNonTerminal.add(this.EPSILON))
                            changes = true;
                    }
                }
            }
        }
        while (changes);

    }

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
        this.computeFirstSets();
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
                    if( element.equals(list.get(list.size()-1)) )
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

    public Set<String> getFirst(String symbol) {
        return this.firstSets.getOrDefault(symbol, new HashSet<>());
    }

    public Set<String> getFirstForSymbol(String symbol) {
        Set<String> firstSet = new HashSet<>();

        if (terminals.contains(symbol))
            firstSet.add(symbol);

        else if (nonTerminals.contains(symbol))
        {
            Set<String> symbolFirstSet = getFirst(symbol);
            firstSet.addAll(symbolFirstSet);
        }

        return firstSet;
    }

    public Set<String> getFollowForSymbol(String symbol) {
        Set<String> symbolFollowSet = new HashSet<>();

        if (symbol.equals(startingSymbol)){
            symbolFollowSet.add("$");
        }
        if (nonTerminals.contains(symbol)){

            for (var lhs : this.productions.keySet()){
                var rhs = this.productions.get(lhs).stream().toList();
                var rhsString = this.productions.get(lhs).toString();

                if (rhsString.contains(symbol)){
                    for (var RHPart : rhs) {
                        for (var i = 0; i < RHPart.size()-1; i++) {
                            //If symbol is followed by something,
                            //we add FIRST of the following element in rhs, except for epsilon.
                            //If the first contains epsilon, we also add FOLLOW (lhs) to it
                            if (RHPart.get(i).equals(symbol)) {
                                var firstOfNextElement = getFirstForSymbol(RHPart.get(i + 1));
                                for (var elem : firstOfNextElement){
                                    if (!Objects.equals(elem, EPSILON))
                                        symbolFollowSet.add(elem);
                                }
                                if (firstOfNextElement.contains(EPSILON) && !lhs.contains(symbol)){
                                    var followOfLHS = getFollowForSymbol(lhs.get(0));
                                    symbolFollowSet.addAll(followOfLHS);
                                }
                            }
                        }
                        //if symbol is last in rhs, we call follow(lhs)
                        if (RHPart.get(RHPart.size()-1).equals(symbol) && !lhs.contains(symbol)){
                            var followOfLHS = getFollowForSymbol(lhs.get(0));
                            symbolFollowSet.addAll(followOfLHS);
                        }
                    }

                }
            }
        }

        return symbolFollowSet;
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