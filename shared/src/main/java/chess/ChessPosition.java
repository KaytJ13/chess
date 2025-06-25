package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;
    private ChessPiece piece = null;
    private PositionColor color;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;

        if(row % 2 == 0 && col % 2 == 0){
            this.color = PositionColor.BLACK;
        } else if(row % 2 == 1 && col % 2 == 1) {
            this.color = PositionColor.BLACK;
        } else {
            this.color = PositionColor.WHITE;
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
        //throw new RuntimeException("Not implemented");
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        //throw new RuntimeException("Not implemented");
        return this.col;
    }

    /**
     * @return what piece is currently at this position
     */
    public ChessPiece getPiece() {
        return this.piece;
    }

    /**
     * adds/moves a piece to this position
     * @param newPiece the piece to be moved to this position
     */
    public void addPiece(ChessPiece newPiece) {
        this.piece = newPiece;
    }

    /**
     * removes a piece from this position
     */
    public void removePiece() {
        this.piece = null;
    }

    /**
     * @return the color of the position/square
     */
    public PositionColor getColor() {
        return this.color;
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
