package io.github.orangeutan.minicalc;

abstract class ParseTreeElement {
    abstract String multiLineString();
    abstract String multiLineString(String indentation);
}