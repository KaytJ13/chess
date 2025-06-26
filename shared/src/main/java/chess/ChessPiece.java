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
        ChessPosition endOption;

        for (int i = myPosition.getRow()-1; i <= myPosition.getRow()+1; i++) {
            for (int j = myPosition.getColumn()-1; j <= myPosition.getColumn()+1; j++) {
                endOption = new ChessPosition(i, j);
                if (checkBounds(endOption)) {
                    if (board.getBoard()[i-1][j-1].getPiece() == null) {
                        validMoves.add(new ChessMove(myPosition, endOption, null));
                    } else if (board.getBoard()[i-1][j-1].getPiece().getTeamColor() != pieceColor){
                        validMoves.add(new ChessMove(myPosition, endOption, null));
                    }
                }
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
        throw new RuntimeException("Not implemented");
        // HashSet<ChessMove> validMoves = new HashSet<>();
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
        // HashSet<ChessMove> validMoves = new HashSet<>();
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
        // HashSet<ChessMove> validMoves = new HashSet<>();
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
            if (checkValidRow(board, myPosition, validMoves, i)) {
                break;
            }
        }
        // Checking positions in lower rows
        for (int i = myPosition.getRow()-1; i > 0; i--) {
            if (checkValidRow(board, myPosition, validMoves, i)) {
                break;
            }
        }
        // Checking columns to the left
        for (int i = myPosition.getColumn()-1; i > 0; i--) {
            if (checkValidColumn(board, myPosition, validMoves, i)) {
                break;
            }
        }
        // Checking columns to the right
        for (int i = myPosition.getColumn()+1; i <= 8; i++) {
            if (checkValidColumn(board, myPosition, validMoves, i)) {
                break;
            }
        }
        return validMoves;
    }

    private boolean checkValidColumn(ChessBoard board, ChessPosition position, HashSet<ChessMove> validMoves, int i) {
        ChessPosition endOption;
        endOption = new ChessPosition(position.getRow(), i);
        if (board.getBoard()[position.getRow()-1][i-1].getPiece() == null) {
            validMoves.add(new ChessMove(position, endOption, null));
            return false;
        } else if (board.getBoard()[position.getRow()-1][i-1].getPiece().getTeamColor() != pieceColor){
            validMoves.add(new ChessMove(position, endOption, null));
            return true;
        } else return true;
    }

    private boolean checkValidRow(ChessBoard board, ChessPosition position, HashSet<ChessMove> validMoves, int i) {
        ChessPosition endOption;
        endOption = new ChessPosition(i, position.getColumn());
        if (board.getBoard()[i-1][position.getColumn()-1].getPiece() == null) {
            validMoves.add(new ChessMove(position, endOption, null));
            return false;
        } else if (board.getBoard()[i-1][position.getColumn()-1].getPiece().getTeamColor() != pieceColor){
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
