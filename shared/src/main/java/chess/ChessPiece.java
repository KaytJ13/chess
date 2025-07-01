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
        int counter = 1;
        //Checking spaces up and right
        while (verifyMove(board, myPosition, validMoves, myPosition.getRow()+counter,
                myPosition.getColumn()+counter)) {
            counter++;
        }
        //Checking spaces up and left
        counter = 1;
        while (verifyMove(board, myPosition, validMoves, myPosition.getRow()-counter,
                myPosition.getColumn()+counter)) {
            counter++;
        }
        //Checking spaces down and right
        counter = 1;
        while (verifyMove(board, myPosition, validMoves, myPosition.getRow()+counter,
                myPosition.getColumn()-counter)) {
            counter++;
        }
        //Checking spaces down and left
        counter = 1;
        while (verifyMove(board, myPosition, validMoves, myPosition.getRow()-counter,
                myPosition.getColumn()-counter)) {
            counter++;
        }

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
            verifyMove(board, myPosition, validMoves, myPosition.getRow() + i[0],
                    myPosition.getColumn() + i[1]);
        }

        return validMoves;
    }

    /**
     * Checks to see if a piece can move to a certain location.
     * If it can, it adds the location. Returns whether it can go farther or not (rooks, bishops, queens).
     * @param board the current gameboard
     * @param myPosition the current position of the piece
     * @param validMoves the collection of valid moves to be added to
     * @param xPosition the x coordinate of the potential end position
     * @param yPosition the y coordinate of the potential end position
     * @return whether the piece should stop moving (blocked, etc.) (true) or keep going (false)
     */
    private boolean verifyMove(ChessBoard board, ChessPosition myPosition,
                               HashSet<ChessMove> validMoves, int xPosition, int yPosition) {
        ChessPosition endOption = new ChessPosition(xPosition, yPosition);
        if (checkBounds(endOption)) {
            if (board.getSquare(endOption).getPiece() == null) {
                validMoves.add(new ChessMove(myPosition, endOption, null));
                return true;
            } else if (board.getSquare(endOption).getPiece().getTeamColor() != pieceColor){
                validMoves.add(new ChessMove(myPosition, endOption, null));
            }
        }
        return false;
    }

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        int counter = 1;
        // Checking all positions in the same column at higher rows than current position
        while(verifyMove(board, myPosition, validMoves, myPosition.getRow()+counter, myPosition.getColumn())) {
            counter++;
        }
        // Checking positions in lower rows
        counter = 1;
        while(verifyMove(board, myPosition, validMoves, myPosition.getRow()-counter, myPosition.getColumn())) {
            counter++;
        }
        // Checking columns to the right
        counter = 1;
        while(verifyMove(board, myPosition, validMoves, myPosition.getRow(), myPosition.getColumn()+counter)) {
            counter++;
        }
        // Checking columns to the left
        counter = 1;
        while(verifyMove(board, myPosition, validMoves, myPosition.getRow(), myPosition.getColumn()-counter)) {
            counter++;
        }
        return validMoves;
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
         HashSet<ChessMove> validMoves = new HashSet<>();
         ChessPosition endOption;
         if (pieceColor == ChessGame.TeamColor.WHITE) {
             if (myPosition.getRow() == 7) {
                 // promotion
                 validatePromotionMove(board, myPosition, validMoves, myPosition.getRow()+1,
                         myPosition.getColumn());
                 // capturing promotion
                 validatePromotionCapture(board, myPosition, validMoves, myPosition.getRow()+1,
                         myPosition.getColumn()+1, pieceColor);
                 validatePromotionCapture(board, myPosition, validMoves, myPosition.getRow()+1,
                         myPosition.getColumn()-1, pieceColor);
             } else {
                 if (myPosition.getRow() == 2) {
                     // first move white
                     endOption = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                     ChessPosition blockSquare = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                     if (checkBounds(endOption) &&
                             board.getSquare(endOption).getPiece() == null &&
                             board.getSquare(blockSquare).getPiece() == null) {
                         validMoves.add(new ChessMove(myPosition, endOption, null));
                     }
                 }
                 // normal
                 endOption = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                 if (checkBounds(endOption) &&
                         board.getSquare(endOption).getPiece() == null) {
                     validMoves.add(new ChessMove(myPosition, endOption, null));
                 }
                 // capturing (non-promotion)
                 validateCapture(board, myPosition, validMoves, myPosition.getRow()+1,
                         myPosition.getColumn()+1, pieceColor);
                 validateCapture(board, myPosition, validMoves, myPosition.getRow()+1,
                         myPosition.getColumn()-1, pieceColor);
             }
         } else {
             if (myPosition.getRow() == 2) {
                 // promotion
                 validatePromotionMove(board, myPosition, validMoves, myPosition.getRow()-1,
                         myPosition.getColumn());
                 // capturing promotion
                 validatePromotionCapture(board, myPosition, validMoves, myPosition.getRow()-1,
                         myPosition.getColumn()+1, pieceColor);
                 validatePromotionCapture(board, myPosition, validMoves, myPosition.getRow()-1,
                         myPosition.getColumn()-1, pieceColor);
             } else {
                 if (myPosition.getRow() == 7) {
                     // first move black
                     endOption = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                     ChessPosition blockSquare = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                     if (checkBounds(endOption) &&
                             board.getSquare(endOption).getPiece() == null &&
                             board.getSquare(blockSquare).getPiece() == null) {
                         validMoves.add(new ChessMove(myPosition, endOption, null));
                     }
                 }
                 // normal
                 endOption = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                 if (checkBounds(endOption) &&
                         board.getSquare(endOption).getPiece() == null) {
                     validMoves.add(new ChessMove(myPosition, endOption, null));
                 }
                 // capturing (non-promotion)
                 validateCapture(board, myPosition, validMoves, myPosition.getRow()-1,
                         myPosition.getColumn()+1, pieceColor);
                 validateCapture(board, myPosition, validMoves, myPosition.getRow()-1,
                         myPosition.getColumn()-1, pieceColor);
             }
         }
        return validMoves;
    }

    private void validateCapture(ChessBoard board, ChessPosition myPosition,
                                 HashSet<ChessMove> validMoves, int xEndPos, int yEndPos, ChessGame.TeamColor color) {
        ChessPosition endOption = new ChessPosition(xEndPos, yEndPos);
        if (checkBounds(endOption) && board.getSquare(endOption).getPiece() != null) {
            if (board.getSquare(endOption).getPiece().getTeamColor() != color) {
                validMoves.add(new ChessMove(myPosition, endOption, null));
            }
        }
    }

    private void validatePromotionCapture(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves,
                                          int xEndPos, int yEndPos, ChessGame.TeamColor color) {
        ChessPosition endOption = new ChessPosition(xEndPos, yEndPos);
        if (checkBounds(endOption) && board.getSquare(endOption).getPiece() != null) {
            if (board.getSquare(endOption).getPiece().getTeamColor() != color) {
                addPromotion(myPosition, endOption, validMoves);                     }
        }
    }

    private void validatePromotionMove(ChessBoard board, ChessPosition myPosition,
                                       HashSet<ChessMove> validMoves, int xEndPos, int yEndPos) {
        ChessPosition endOption = new ChessPosition(xEndPos, yEndPos);
        if (checkBounds(endOption) && board.getSquare(endOption).getPiece() == null) {
            addPromotion(myPosition, endOption, validMoves);
        }
    }

    private void addPromotion(ChessPosition myPosition, ChessPosition endOption, HashSet<ChessMove> validMoves) {
        validMoves.add(new ChessMove(myPosition, endOption, PieceType.QUEEN));
        validMoves.add(new ChessMove(myPosition, endOption, PieceType.ROOK));
        validMoves.add(new ChessMove(myPosition, endOption, PieceType.KNIGHT));
        validMoves.add(new ChessMove(myPosition, endOption, PieceType.BISHOP));
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
