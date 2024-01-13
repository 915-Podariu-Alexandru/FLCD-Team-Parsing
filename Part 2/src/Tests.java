public class Tests {
    Grammar grammar = new Grammar("io/g3.txt");

    public void runTests()
    {
        //For Non-Terminal A
        if ((!grammar.getFirstForSymbol("A").toString().equals("[]")))
            throw new AssertionError();
        if ((!grammar.getFollowForSymbol("A").toString().equals("[b, $]")))
            throw new AssertionError();

        //For Non-Terminal B
        if ((!grammar.getFirstForSymbol("B").toString().equals("[b]")))
            throw new AssertionError();
        if ((!grammar.getFollowForSymbol("B").toString().equals("[b, $]")))
            throw new AssertionError();

        //For Non-Terminal C
        if ((!grammar.getFirstForSymbol("C").toString().equals("[a]")))
            throw new AssertionError();
        if ((!grammar.getFollowForSymbol("C").toString().equals("[$]")))
            throw new AssertionError();

        //For Non-Terminal D
        if ((!grammar.getFirstForSymbol("D").toString().equals("[EPS]")))
            throw new AssertionError();
        if ((!grammar.getFollowForSymbol("D").toString().equals("[$]")))
            throw new AssertionError();

        //For Non-Terminal S
        if ((!grammar.getFirstForSymbol("S").toString().equals("[a]")))
            throw new AssertionError();
        if ((!grammar.getFollowForSymbol("S").toString().equals("[$]")))
            throw new AssertionError();
    }

}
