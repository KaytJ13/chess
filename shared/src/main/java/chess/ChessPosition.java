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

    public String drawSquare(boolean highlight) {
        String bgLightGrey = "\u001b" + "[48;5;" + "242m";
        String bgDarkGreen = "\u001b" + "[48;5;" + "22m";
        String bgWhite = "\u001b" + "[48;5;" + "15m";
        String bgLightGreen= "\u001b" + "[48;5;" + "46m";
        String textBlack = "\u001b" + "[38;5;" + "0m";
        String textWhite = "\u001b" + "[38;5;" + "15m";

        String whiteKing = " ♔ ";
        String whiteQueen = " ♕ ";
        String whiteBishop = " ♗ ";
        String whiteKnight = " ♘ ";
        String whiteRook = " ♖ ";
        String whitePawn = " ♙ ";
        String blackKing = " ♚ ";
        String blackQueen = " ♛ ";
        String blackBishop = " ♝ ";
        String blackKnight = " ♞ ";
        String blackRook = " ♜ ";
        String blackPawn = " ♟ ";
        String empty = " \u2003 ";

        StringBuilder square = new StringBuilder();
        if (highlight && color == PositionColor.WHITE) {
            square.append(bgWhite);
        } else if (highlight) {
            square.append(bgLightGreen);
        } else if (color == PositionColor.WHITE) {
            square.append(bgLightGrey);
        } else {
            square.append(bgDarkGreen);
        }

        if (piece == null) {
            square.append(empty);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            drawPiece(textWhite, whiteKing, whiteQueen, whiteBishop, whiteKnight, whiteRook,
                    whitePawn, square);
        } else {
            drawPiece(textBlack, blackKing, blackQueen, blackBishop, blackKnight, blackRook,
                    blackPawn, square);
        }
        square.append("\u001b" + "[49m");
        return square.toString();
    }

    private void drawPiece(String textColor, String kingText, String queenText, String bishopText, String knightText,
                           String rookText, String pawnText, StringBuilder square) {
        square.append(textColor);
        switch (piece.getPieceType()) {
            case KING -> square.append(kingText);
            case QUEEN -> square.append(queenText);
            case ROOK -> square.append(rookText);
            case BISHOP -> square.append(bishopText);
            case KNIGHT -> square.append(knightText);
            case PAWN -> square.append(pawnText);
            default -> square.append(" \u2003 ");
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
        char[] columnOptions = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        return String.valueOf(columnOptions[col-1]) + row;
//        return "{" + row + ", " + col + '}';
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
