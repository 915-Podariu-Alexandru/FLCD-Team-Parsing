import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParseTreeNode {
    private String value;
    private List<ParseTreeNode> children;

    public ParseTreeNode(String value, List<ParseTreeNode> children) {
        this.value = value;
        this.children = children;
    }
    public ParseTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public String getValue() {
        return value;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }
}

class ParserOutput {
    private ParseTreeNode treeRoot;

    public ParserOutput(ParseTreeNode treeRoot) {
        this.treeRoot = treeRoot;
    }

    public Map<String, Object> transformTreeRepresentation() {
        return transformTree(treeRoot);
    }

    private Map<String, Object> transformTree(ParseTreeNode node) {
        if (node == null) {
            return null;
        }

        Map<String, Object> transformedNode = new HashMap<>();
        transformedNode.put("value", node.getValue());
        List<Map<String, Object>> children = new ArrayList<>();

        for (ParseTreeNode child : node.getChildren())
            children.add(transformTree(child));

        transformedNode.put("children", children);

        return transformedNode;
    }

    public void printToScreen(int depth) {
        printTree(treeRoot, depth);
    }

    private void printTree(ParseTreeNode node, int depth) {
        if (node != null)
        {
            System.out.println("  ".repeat(depth) + node.getValue());
            for (ParseTreeNode child : node.getChildren()) {
                printTree(child, depth + 1);
            }
        }
    }

    public void printToFile(String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writeTreeToFile(writer, treeRoot, 0);
        }
    }

    private void writeTreeToFile(FileWriter writer, ParseTreeNode node, int depth) throws IOException {
        if (node != null) {
            writer.write("  ".repeat(depth) + node.getValue() + "\n");
            for (ParseTreeNode child : node.getChildren()) {
                writeTreeToFile(writer, child, depth + 1);
            }
        }
    }
}