package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition implements Cloneable {

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

    public String drawSquare() {
        String SET_BG_COLOR_LIGHT_GREY = "\u001b" + "[48;5;" + "242m";
        String SET_BG_COLOR_BLACK = "\u001b" + "[48;5;" + "0m";
        String SET_BG_COLOR_DARK_GREEN = "\u001b" + "[48;5;" + "22m";
        String SET_TEXT_COLOR_BLACK = "\u001b" + "[38;5;" + "0m";
        String SET_TEXT_COLOR_WHITE = "\u001b" + "[38;5;" + "15m";

        String WHITE_KING = " ♔ ";
        String WHITE_QUEEN = " ♕ ";
        String WHITE_BISHOP = " ♗ ";
        String WHITE_KNIGHT = " ♘ ";
        String WHITE_ROOK = " ♖ ";
        String WHITE_PAWN = " ♙ ";
        String BLACK_KING = " ♚ ";
        String BLACK_QUEEN = " ♛ ";
        String BLACK_BISHOP = " ♝ ";
        String BLACK_KNIGHT = " ♞ ";
        String BLACK_ROOK = " ♜ ";
        String BLACK_PAWN = " ♟ ";
        String EMPTY = " \u2003 ";

        StringBuilder square = new StringBuilder();
        if (color == PositionColor.WHITE) {
            square.append(SET_BG_COLOR_LIGHT_GREY);
        } else {
            square.append(SET_BG_COLOR_DARK_GREEN);
        }
        if (piece == null) {
            square.append(EMPTY);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            drawPiece(SET_TEXT_COLOR_WHITE, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN, square);
        } else {
            drawPiece(SET_TEXT_COLOR_BLACK, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN, square);
        }
        return square.toString();
    }

    private void drawPiece(String textColor, String kingText, String queenText, String bishopText, String knightText, String rookText, String pawnText, StringBuilder square) {
        square.append(textColor);
        switch (piece.getPieceType()) {
            case KING -> square.append(kingText);
            case QUEEN -> square.append(queenText);
            case ROOK -> square.append(rookText);
            case BISHOP -> square.append(bishopText);
            case KNIGHT -> square.append(knightText);
            case PAWN -> square.append(pawnText);
        }
    }

    @Override
    protected ChessPosition clone() throws CloneNotSupportedException {
        ChessPosition cloned = (ChessPosition) super.clone();

        if (getPiece() != null) {
            ChessPiece clonedPiece = getPiece().clone();
            cloned.setPiece(clonedPiece);
        } else {
            cloned.setPiece(null);
        }

        return cloned;
    }

    @Override
    public String toString() {
        return "{" + row + ", " + col + '}';
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
