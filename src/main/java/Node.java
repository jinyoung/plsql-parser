import java.util.ArrayList;

class Node {
    String type;
    int startLine;
    int endLine;
    Node parent;
    ArrayList<Node> children = new ArrayList<>();

    public Node(String type, int startLine, Node parent) {
        this.type = type;
        this.startLine = startLine;
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }
}