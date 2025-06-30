package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    private ChessPiece piece = null;
    private final PositionColor color;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;

        if(row % 2 == 0 && col % 2 == 0){
            color = PositionColor.BLACK;
        } else if(row % 2 == 1 && col % 2 == 1) {
            color = PositionColor.BLACK;
        } else {
            color = PositionColor.WHITE;
        }
    }

    public enum PositionColor {
        WHITE,
        BLACK
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return what piece is currently at this position
     */
    public ChessPiece getPiece() {
        return piece;
    }

    /**
     * adds/moves a piece to this position
     * @param piece the piece to be moved to this position
     */
    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

//    /**
//     * @return the color of the position/square
//     */
//    public PositionColor getColor() {
//        return color;
//    }     // I might need this later (graphics, etc.) but it isn't helpful now

    @Override
    public String toString() {
        return '{' + row + ", " + col + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col && Objects.equals(piece, that.piece) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, piece, color);
    }
}
