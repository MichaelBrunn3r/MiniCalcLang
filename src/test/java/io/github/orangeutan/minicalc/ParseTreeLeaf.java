package io.github.orangeutan.minicalc;

public class ParseTreeLeaf extends ParseTreeElement {

    public String text;

    public ParseTreeLeaf(String type, String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("T[%s]", this.text);
    }

    @Override
    String multiLineString() {
        return multiLineString("");
    }

	@Override
	String multiLineString(String indentation) {
		return String.format("%s%s%s", indentation, this.toString(), this.text.equals("<EOF>") ? "" : "\n");
	}
}