package io.github.orangeutan.minicalc;

class Point {
    public int line; /* The line at which the Point is positioned */
    public int column; /* The column at which the Point is positioned */

    public Point(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("Line %d, Column %d", this.line, this.column);
    }

    /* 
        Measures the position of this Point in a String.
        This method converts line number and column to the coresponding index/position in the given String.
    */
    public int getPosInStr(String str, char lineSeperator) {
        String[] lines = str.split("(?=" + lineSeperator +")"); /* Split the String into lines without removing the line seperators */
        int offset = column;
        for(int i=0; i<this.line; i++) {
            offset += lines[i].length();
        }
        return offset;
    }

    /* Determines if this Point is position before another Point */
    public boolean isBefore(Point p) {
        return (this.line < p.line) || (this.line == p.line && this.column < p.column);
    }
}