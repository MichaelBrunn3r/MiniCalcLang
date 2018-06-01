package io.github.orangeutan.minicalc;

class Position {

    public Point start;
    public Point end;

    public Position(Point start, Point end) {
        this.start = start;
        this.end = end;

        if(this.end.isBefore(start)) throw new IllegalArgumentException("End Point can't be before start Point");
    }

    public Position(int startLine, int startCol, int endLine, int endCol) {
        this.start = new Point(startLine,startCol);
        this.end = new Point(endLine, endCol);
    }

    /* Extracts a substring located at this Position inside a String */
    public String substr(String str) {
        return str.substring(start.getPosInStr(str, '\n'), end.getPosInStr(str, '\n'));
    }

    /* Measures the lenght of this Position in a String */
    public int lenInStr(String str) {
        return end.getPosInStr(str, '\n') - start.getPosInStr(str, '\n');
    }

    
}