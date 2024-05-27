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

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\": \"").append(type).append("\", ");
        jsonBuilder.append("\"startLine\": ").append(startLine).append(", ");
        jsonBuilder.append("\"endLine\": ").append(endLine).append(", ");
        jsonBuilder.append("\"children\": [");
        for (int i = 0; i < children.size(); i++) {
            jsonBuilder.append(children.get(i).toJson());
            if (i < children.size() - 1) {
                jsonBuilder.append(", ");
            }
        }
        jsonBuilder.append("]");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}