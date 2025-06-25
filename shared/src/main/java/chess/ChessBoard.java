package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition[][] board;

    public ChessBoard() { //This be nasty and I can probably do it nicer with a nester for loop . . .
        ChessPosition[] rowOne = {new ChessPosition(1,1),
                new ChessPosition(1, 2), new ChessPosition(1, 3),
                new ChessPosition(1, 4), new ChessPosition(1, 5),
                new ChessPosition(1,6), new ChessPosition(1,7), new ChessPosition(1,8)};
        ChessPosition[] rowTwo = {new ChessPosition(2,1),
                new ChessPosition(2, 2), new ChessPosition(2, 3),
                new ChessPosition(2, 4), new ChessPosition(2, 5),
                new ChessPosition(2,6), new ChessPosition(2,7), new ChessPosition(2,8)};
        ChessPosition[] rowThree = {new ChessPosition(3,1),
                new ChessPosition(3, 2), new ChessPosition(3, 3),
                new ChessPosition(3, 4), new ChessPosition(3, 5),
                new ChessPosition(3,6), new ChessPosition(3,7), new ChessPosition(3,8)};
        ChessPosition[] rowFour = {new ChessPosition(4,1),
                new ChessPosition(4, 2), new ChessPosition(4, 3),
                new ChessPosition(4, 4), new ChessPosition(4, 5),
                new ChessPosition(4, 6), new ChessPosition(4,7), new ChessPosition(4,8)};
        ChessPosition[] rowFive = {new ChessPosition(5,1),
                new ChessPosition(5, 2), new ChessPosition(5, 3),
                new ChessPosition(5, 4), new ChessPosition(5, 5),
                new ChessPosition(5,6), new ChessPosition(5,7), new ChessPosition(5,8)};
        ChessPosition[] rowSix = {new ChessPosition(6,1),
                new ChessPosition(6, 2), new ChessPosition(6, 3),
                new ChessPosition(6, 4), new ChessPosition(6, 5),
                new ChessPosition(6,6), new ChessPosition(6,7), new ChessPosition(6,8)};
        ChessPosition[] rowSeven = {new ChessPosition(7,1),
                new ChessPosition(7, 2), new ChessPosition(7, 3),
                new ChessPosition(7, 4), new ChessPosition(7, 5),
                new ChessPosition(7,6), new ChessPosition(7,7), new ChessPosition(7,8)};
        ChessPosition[] rowEight = {new ChessPosition(8,1),
                new ChessPosition(8, 2), new ChessPosition(8, 3),
                new ChessPosition(8, 4), new ChessPosition(8, 5),
                new ChessPosition(8,6), new ChessPosition(8,7), new ChessPosition(8,8)};
        this.board = new ChessPosition[][]{rowOne, rowTwo, rowThree, rowFour, rowFive, rowSix, rowSeven, rowEight};
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        ChessPosition currentSquare = this.board[position.getRow()-1][position.getColumn()-1];
        currentSquare.addPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //throw new RuntimeException("Not implemented");
        ChessPosition currentSquare = this.board[position.getRow()-1][position.getColumn()-1];
        return currentSquare.getPiece();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //remove all the pieces
        for (ChessPosition[] boardRow : this.board) {
            for (ChessPosition square : boardRow) {
                square.removePiece();
            }
        }

        //add new pieces at all the spots
        //the PAWNS
        for (ChessPosition square : this.board[1]) {
            square.addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for (ChessPosition square : this.board[6]) {
            square.addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //rooks
        this.board[0][0].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.board[0][7].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.board[7][0].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.board[7][7].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        //knights
        this.board[0][1].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.board[0][6].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.board[7][1].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.board[7][6].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        //bishops
        this.board[0][2].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.board[0][5].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.board[7][2].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.board[7][5].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        //queens
        this.board[0][3].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.board[7][3].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        //kings
        this.board[0][4].addPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        this.board[7][4].addPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

//        throw new RuntimeException("Not implemented");
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
