#My notes:
-
Cut code that may be needed later . . .
ServerFacadeTests:
//    @Test
//    public void testGetGameStatePositive() {
//        try {
//            AuthData auth = facade.register(new RegisterRequest("hermy", "wiz", "email"));
//            facade.createGame(new CreateGameRequest("game1"), auth.authToken());
//            ChessGame game = facade.getGameState(new GetGameStateRequest(1), auth.authToken());
//            System.out.printf(game.toString());
//            assert game.equals(new ChessGame());
//            assert true;
//        } catch (Exception e) {
//            assert false;
//        }
//    }

ServerFacade:
// import chess.ChessGame

//    public ChessGame getGameState(GetGameStateRequest request, String authToken) throws ResponseException {
//        var path = "/session";
//        return makeRequest("GET", path, new Gson().toJson(request), ChessGame.class, authToken);
//    }

- Things to think about after talking with the TAs:
  - Potentially change the whole structure for Phases 0 & 1. Instead of a board being an array of arrays of positions:
    - The ChessBoard would have 2 arrays: white pieces and black pieces
    - Each piece would have a position variable
    - Position would no longer keep track of what piece is there
  - The things that would have to be updated:
    - ChessBoard:
      - Constructor
      - Instance Variables
      - Board getters and setters (delete & write new ones for white and black pieces)
      - AddPiece
      - GetPiece
      - GetSquare (could be deleted)
      - ResetBoard (would be way simpler)
      - Clone
      - Hash & equals
    - ChessPosition:
      - Instance Variables (no piece)
      - Get/SetPiece (delete)
      - Clone (wouldn't really need it . . .)
      - Hash & equals
    - ChessMove:
      - Nothing
    - ChessPiece:
      - Instance Variables (add a position, default null)
      - Getters and setters for position
      - Consider adding a "first move" boolean that then updates in the setter if the position is null
      - ToString, equals, & hash
    - ChessGame:
      - IsInCheckHelper (would loop through pieces, not the board)
      - FindKing (would be way simpler, loop through pieces)
      - CannotMove (again, way simpler)