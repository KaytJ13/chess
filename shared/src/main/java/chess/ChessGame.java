package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPieceOriginal = board.getPiece(startPosition);
        if (currentPieceOriginal == null) {
            return null;
        }
        Collection<ChessMove> potentialMoves = currentPieceOriginal.pieceMoves(board, startPosition);
        HashSet<ChessMove> verifiedMoves = new HashSet<>();
        // take out everything that could lead to check
        for (ChessMove move : potentialMoves) {
            ChessBoard boardCopy = board.clone();
            ChessPiece currentPiece = boardCopy.getPiece(move.getStartPosition());
            boardCopy.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() != null) {
                currentPiece = new ChessPiece(currentPieceOriginal.getTeamColor(), move.getPromotionPiece());
            }
            boardCopy.addPiece(move.getEndPosition(), currentPiece);
            if (!isInCheckHelper(currentPieceOriginal.getTeamColor(), boardCopy)) {
                verifiedMoves.add(move);
            }
        }
        return verifiedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> validatedMoves = validMoves(move.getStartPosition());
        if (validatedMoves == null) {
            throw new InvalidMoveException("This is not the move you are looking for");
        } else if (!validatedMoves.contains(move)) {
            throw new InvalidMoveException("This is not the move you are looking for");
        } else if (teamTurn != currentPiece.getTeamColor()) {
            throw new InvalidMoveException("Patience, my young Padawan");
        }
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            currentPiece = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), currentPiece);
        if (teamTurn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(teamColor, board);
    }

    private boolean isInCheckHelper(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = findKing(teamColor, board);
        if (kingPosition == null) {
            return true; // This should never actually happen, but it yells at me if it's not there
        }
        for (ChessPosition[] row : board.getBoard()) {
            for (ChessPosition square : row) {
                if (square.getPiece() != null) {
                    if (square.getPiece().getTeamColor() != teamColor) {
                        Collection<ChessMove> potentialMoves = square.getPiece().pieceMoves(board, square); //validMoves(square);
                        for (ChessMove move : potentialMoves) {
                            if (move.getEndPosition().getColumn() == kingPosition.getColumn() &&
                                    move.getEndPosition().getRow() == kingPosition.getRow()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        for (ChessPosition[] row : board.getBoard()) {
            for (ChessPosition square : row) {
                if (square.getPiece() != null) {
                    if (square.getPiece().getPieceType() == ChessPiece.PieceType.KING &&
                            square.getPiece().getTeamColor() == teamColor) {
                        return square;
                    }
                }
            }
        }
        return null; // to clarify, this should never happen
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return cannotMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return cannotMove(teamColor);
    }

    private boolean cannotMove(TeamColor teamColor) {
        for (ChessPosition[] row : board.getBoard()) {
            for (ChessPosition square : row) {
                if (square.getPiece() != null) {
                    if (square.getPiece().getTeamColor() == teamColor) {
                        Collection<ChessMove> potentialMoves = validMoves(square);
                        if (!potentialMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
