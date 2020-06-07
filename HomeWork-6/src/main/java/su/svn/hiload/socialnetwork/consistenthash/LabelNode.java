package su.svn.hiload.socialnetwork.consistenthash;

import java.util.UUID;

public class LabelNode implements Node {

    private final UUID label;

    public LabelNode(UUID label) {
        this.label = label;
    }

    @Override
    public String getKey() {
        return label.toString();
    }

    @Override
    public String toString() {
        return "LabelNode{" +
                "label=" + label +
                '}';
    }
}
