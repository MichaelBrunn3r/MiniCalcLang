package io.github.orangeutan.minicalc;

import java.util.LinkedList;
import java.util.List;

public class ParseTreeNode extends ParseTreeElement {

    public String name;
    public List<ParseTreeElement> children;

    public ParseTreeNode(String name) {
        this.name = name;
        this.children = new LinkedList<ParseTreeElement>();
    }

    public ParseTreeNode addChild(ParseTreeElement child) {
        this.children.add(child);
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.name, this.children.toString());
    }

    @Override
    String multiLineString() {
        return multiLineString("");
    }

	@Override
	String multiLineString(String indentation) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s%s\n", indentation, this.name));
        for(ParseTreeElement child : this.children) {
            sb.append(child.multiLineString(indentation + "    "));
        }
        return sb.toString();
	}
}