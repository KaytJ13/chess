package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int i = myPosition.getRow()-1; i <= myPosition.getRow()+1; i++) {
            for (int j = myPosition.getColumn()-1; j <= myPosition.getColumn()+1; j++) {
                verifyMove(board, myPosition, validMoves, i, j);
            }
        }
        return validMoves;
    }

    /**
     * Calculates all the positions a queen can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = rookMoves(board, myPosition);
        validMoves.addAll(bishopMoves(board, myPosition));
        return validMoves;
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();


        return validMoves;
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        int[][] potentialMoves = {{2,1}, {2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}, {-2,1}, {-2,-1}};

        for (int[] i : potentialMoves) {
            int xPosition = myPosition.getRow() + i[0];
            int yPosition = myPosition.getColumn() + i[1];
            verifyMove(board, myPosition, validMoves, xPosition, yPosition);
        }

        return validMoves;
    }

    private void verifyMove(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves, int xPosition, int yPosition) {
        ChessPosition endOption = new ChessPosition(xPosition, yPosition);
        if (checkBounds(endOption)) {
            if (board.getBoard()[xPosition-1][yPosition-1].getPiece() == null) {
                validMoves.add(new ChessMove(myPosition, endOption, null));
            } else if (board.getBoard()[xPosition-1][yPosition-1].getPiece().getTeamColor() != pieceColor){
                validMoves.add(new ChessMove(myPosition, endOption, null));
            }
        }
    }

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        // two separate for loops, breaks when hits a piece (adds move if opposite color, doesn't if the same)
        // one loop checks moves with row adjustments, one checks moves with column adjustments
        // don't include or break at its own position
        HashSet<ChessMove> validMoves = new HashSet<>();

        // Checking all positions in the same column at higher rows than current position
        for (int i = myPosition.getRow()+1; i <= 8; i++) {
            if (checkValid(myPosition, validMoves, i,
                    board.getBoard()[i-1][myPosition.getColumn()-1].getPiece(), true)) {
                break;
            }
        }
        // Checking positions in lower rows
        for (int i = myPosition.getRow()-1; i > 0; i--) {
            if (checkValid(myPosition, validMoves, i,
                    board.getBoard()[i-1][myPosition.getColumn()-1].getPiece(), true)) {
                break;
            }
        }
        // Checking columns to the left
        for (int i = myPosition.getColumn()-1; i > 0; i--) {
            if (checkValid(myPosition, validMoves, i,
                    board.getBoard()[myPosition.getRow()-1][i-1].getPiece(), false)) {
                break;
            }
        }
        // Checking columns to the right
        for (int i = myPosition.getColumn()+1; i <= 8; i++) {
            if (checkValid(myPosition, validMoves, i,
                    board.getBoard()[myPosition.getRow()-1][i-1].getPiece(), false)) {
                break;
            }
        }
        return validMoves;
    }

    /**
     * Checks to see if a piece can move to a certain location.
     * If it can, it adds the location. Returns whether it can go farther or not.
     * @param position the current position of the piece
     * @param validMoves the collection of valid moves to be added to
     * @param i how much the position is being incremented by
     * @param newPiece the piece in the new/end position
     * @param row whether checking a row (true) or a column (false)
     * @return whether the piece should stop moving (blocked, etc.) (true) or keep going (false)
     */
    private boolean checkValid(ChessPosition position, HashSet<ChessMove> validMoves, int i, ChessPiece newPiece, boolean row) {
        ChessPosition endOption;
        if (row) {
            endOption = new ChessPosition(i, position.getColumn());
        } else {
            endOption = new ChessPosition(position.getRow(), i);
        }
        if (newPiece == null) {
            validMoves.add(new ChessMove(position, endOption, null));
            return false;
        } else if (newPiece.getTeamColor() != pieceColor){
            validMoves.add(new ChessMove(position, endOption, null));
            return true;
        } else return true;
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
        // HashSet<ChessMove> validMoves = new HashSet<>();
    }

    /**
     * Checks whether a specific position is within the bounds of the board
     * @param position the position to be checked
     * @return whether the position is in bounds
     */
    private boolean checkBounds(ChessPosition position) {
        return position.getRow() <= 8 && position.getRow() >= 1 &&
                position.getColumn() <= 8 && position.getColumn() >= 1;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
