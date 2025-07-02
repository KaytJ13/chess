package chess;

import java.util.Arrays;
import java.util.Objects;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPosition[][] board;

    public ChessBoard() {
        board = new ChessPosition[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPosition(i+1, j+1);
            }
        }
    }

    /**
     * Gets the chess board
     * @return the array serving as the board
     */
    public ChessPosition[][] getBoard() {
        return board;
    }

    public void setBoard(ChessPosition[][] board) {
        this.board = board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        ChessPosition currentSquare = board[position.getRow()-1][position.getColumn()-1];
        currentSquare.setPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPosition currentSquare = board[position.getRow()-1][position.getColumn()-1];
        return currentSquare.getPiece();
    }

    public ChessPosition getSquare(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //remove all the pieces
        for (ChessPosition[] boardRow : board) {
            for (ChessPosition square : boardRow) {
                if (square.getPiece() != null){
                    square.setPiece(null);
                }
            }
        }

        //add new pieces at all the spots
        //the PAWNS
        for (ChessPosition square : board[1]) {
            square.setPiece(new ChessPiece(TeamColor.WHITE, PieceType.PAWN));
        }
        for (ChessPosition square : board[6]) {
            square.setPiece(new ChessPiece(TeamColor.BLACK, PieceType.PAWN));
        }
        //rooks
        board[0][0].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        board[0][7].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        board[7][0].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        board[7][7].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        //knights
        board[0][1].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        board[0][6].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        board[7][1].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        board[7][6].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        //bishops
        board[0][2].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        board[0][5].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        board[7][2].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        board[7][5].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        //queens
        board[0][3].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.QUEEN));
        board[7][3].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.QUEEN));
        //kings
        board[0][4].setPiece(new ChessPiece(TeamColor.WHITE, PieceType.KING));
        board[7][4].setPiece(new ChessPiece(TeamColor.BLACK, PieceType.KING));
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard cloned = (ChessBoard) super.clone();

            ChessPosition[][] clonedBoard = new ChessPosition[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    clonedBoard[i][j] = getSquare(new ChessPosition(i+1, j+1).clone());
                }
            }
            cloned.setBoard(clonedBoard);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
