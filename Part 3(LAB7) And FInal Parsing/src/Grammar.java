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
    private boolean NoConflict = true;
    private Stack<String> ParsingStack;
    private ArrayList<String> sequence;


    public ArrayList<String> getSequence(){
        return this.sequence;
    }

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

    private void readGrammarFile(String file) {
        try (Scanner scanner = new Scanner(new File(file)))
        {
            this.nonTerminals = new HashSet<>(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            var terminalList = List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR));
            this.terminals = new HashSet<>(terminalList);
            this.sequence = new ArrayList<>(terminalList);
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

    private void readSequenceFile(String file){
        file = "io/seq.txt";
        try (Scanner scanner = new Scanner(new File(file)))
        {
            var seq = List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR));
            this.sequence = new ArrayList<>(seq);
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
        this.readGrammarFile(filePath);
        this.computeFirstSets();
        this.ParsingStack = new Stack<>();
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
            int i = 0;
            finalString += " { ";
            for (String element : list)
                if (!element.equals(""))
                {
                    i++;
                    if( i == list.size()-1 )
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
                        for (var i = 0; i < RHPart.size()-1; i++)
                        {
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

    private Map<String, Set<String>> followSets()
    {
        Map<String, Set<String>> followSets = new HashMap<>();

        for (String A : nonTerminals) {
            followSets.put(A, getFollowForSymbol(A));
        }
        return followSets;
    }

    public Map<String, Map<String, List<String>>> buildLL1Table() {
        Map<String, Map<String, List<String>>> ll1Table = new HashMap<>();

        Map<String, Set<String>> follow = followSets();

        for (String A : nonTerminals)
        {
            ll1Table.put(A, new HashMap<>());

            for (String terminal : terminals)
                ll1Table.get(A).put(terminal, null);

            ll1Table.get(A).put(EPSILON, null);
        }

        for (String A : nonTerminals)
        {
            List<String> aa = new ArrayList<>();
            aa.add(A);
            for (List<String> alpha : productions.get(aa)) {
                Set<String> firstAlpha = new HashSet<>();
                int i = 0;
                while (i < alpha.size())
                {
                    String B = alpha.get(i);
                    if (!B.equals(EPSILON)) {
                        firstAlpha.addAll(firstSets.get(B));
                        if (!firstSets.get(B).contains(EPSILON)) {
                            break;
                        }
                        i++;
                    }
                    else {
                        if(i == 0)
                            firstAlpha.add(EPSILON);
                    }
                }
                if (i == alpha.size())
                    firstAlpha.add(EPSILON);

                for (String terminal : firstAlpha)
                {
                    if (!EPSILON.equals(terminal))
                    {
                        if (ll1Table.get(A).get(terminal) == null) {
                            ll1Table.get(A).put(terminal, alpha);
                        }
                        else
                        {
                            // Conflict detected
                            System.out.println("Conflict at (" + A + ", " + terminal + "): " +
                                    ll1Table.get(A).get(terminal) + " vs " + alpha);
                            this.NoConflict = false;
                        }
                    }

                    if (firstAlpha.contains(EPSILON)) {
                        for (String followTerminal : follow.get(A))
                        {
                            if (ll1Table.get(A).get(followTerminal) == null) {
                                ll1Table.get(A).put(followTerminal, alpha);
                            }
                            else
                            {
                                // Conflict detected
                                System.out.println("Conflict at (" + A + ", " + followTerminal + "): " +
                                        ll1Table.get(A).get(followTerminal) + " vs " + alpha);
                                this.NoConflict = false;
                            }
                        }
                    }
                }
            }
        }

        return ll1Table;
    }

    public boolean checkLL1()
    {
        return NoConflict;
    }

    public ParseTreeNode buildParseTree(String root, int index, Map<String, Map<String, List<String>>> ll1Table, ArrayList<ParseTreeNode> children)
    {
        var ListToPush = ll1Table.get(root).get(sequence.get(index));
        if (ListToPush == null){
            return null;
        }
        int ListSize = ListToPush.size();

        ParsingStack.push("$");
        for (int i = 0; i < ListSize; i++)
            ParsingStack.push(ListToPush.get(ListSize - i - 1));

        String x = ParsingStack.pop();
        while (!x.equals("$")) {

            if (terminals.contains(x)) {
                if (x.equals(sequence.get(index)))
                    index++;
                children.add(new ParseTreeNode(x));
            }
            else if (nonTerminals.contains(x)) {
                var subtree = buildParseTree(x, index, ll1Table, new ArrayList<>());
                if (subtree == null){
                    return null;
                }
                children.add(subtree);
            }

            x = ParsingStack.pop();
        }
        return new ParseTreeNode(root, children);
    }


}