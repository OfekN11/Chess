package Business;

import Business.Boards.TwoPlayerChessBoard;
import Business.ChessPieces.ChessPiece;
import srv.api.UserMessageReceiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * this class is responsible for the creation and the flow of the game
 */
public class GameManager {
    private TwoPlayerChessBoard board;
    private Place src;
    private Color colorTurn;
    private ChessPiece chosenPiece;
    private final Map<UserMessageReceiver,Color> userColorMap;


    public GameManager(TwoPlayerChessBoard board,UserMessageReceiver whiteMassageReceiver) {
        this.board = board;
        this.src = null;
        this.colorTurn = Color.White;
        userColorMap = new HashMap<UserMessageReceiver,Color>();
        userColorMap.put(whiteMassageReceiver,Color.White);
    }

    public void start(UserMessageReceiver blackMassageReceiver){
        userColorMap.put(blackMassageReceiver,Color.Black);
        sendColorToThePlayers();
        sendTheBoardToThePlayers();
    }

    private void sendColorToThePlayers() {
        for (UserMessageReceiver userMessageReceiver :
                userColorMap.keySet()) {
            userMessageReceiver.receiveColor(userColorMap.get(userMessageReceiver));
        }
    }

    private void sendTheBoardToThePlayers() {
        for (UserMessageReceiver user :
                userColorMap.keySet()) {
            user.receiveBoardAsString(board.toString());
        }
    }

    /**
     *
     * @param chosenPlace the place that had been clicked
     * @return a collection of places to show the user where the piece he chose in the first click can move
     */
    public void userClick(Place chosenPlace, UserMessageReceiver userMessageReceiver) {

        synchronized (this) {
            if (userColorMap.get(userMessageReceiver) != colorTurn)
                return;
        }


        if (chosenPlace == src) { // double click to cancel
            userMessageReceiver.receiveBoardAsString(board.toString());
            resetVariables();
            return;
        }

        try {
            if (src == null)
                 handleFirstClick(chosenPlace,userMessageReceiver);

            else
                 handleSecondClick(chosenPlace,userMessageReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handleFirstClick(Place chosenPlace,UserMessageReceiver userMessageReceiver) {
        src = chosenPlace;
        chosenPiece = board.getPieceInPlace(chosenPlace);
        if (chosenPiece == null || chosenPiece.getColor() != colorTurn) {
            resetVariables();
        }
        else{
            short sh = 3;
            Collection<Place> movingOptions  =board.calculateMovingOptions(src);
            if (!movingOptions.isEmpty())
                userMessageReceiver.receiveCollection(movingOptions,sh);
            else
                resetVariables();
        }
    }

    private void handleSecondClick(Place chosenPlace,UserMessageReceiver userMessageReceiver) {
        if (board.isLegalMove(src, chosenPlace, colorTurn)) {
            board.moveAPiece(src, chosenPlace, () -> 'Q'); // that's a big bug, but I do not perfect with the Gui and I do not want to invest a lot of time on it.
            sendTheBoardToThePlayers();
            synchronized (this){
                colorTurn = Color.getOpponent(colorTurn, 2).get(0);

                if (board.isInCheckMate(colorTurn)) {
                    for (UserMessageReceiver receiver :
                            userColorMap.keySet()) {
                        receiver.receiveMsg(Color.getOpponent(colorTurn, 2).get(0) + " has won");
                        receiver.gameFinishCallback();
                    }

                } else if (board.isInPat(colorTurn)) {
                    for (UserMessageReceiver receiver :
                            userColorMap.keySet()) {
                        receiver.receiveMsg("its a tie!");
                        receiver.gameFinishCallback();
                    }
                }
            }

        }
            resetVariables();
    }

    private void resetVariables() {
        this.src = null;
        this.chosenPiece = null;
    }






        /**
         * this method should be called when a game is done
         */
 /*   private void finishGame(GameScore gameScore) {
        Color opponent = Color.getOpponent(gameScore.winner, 2).get(0);
        switch (gameScore.reason) {
            case GiveUp -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(String.format(MessagesLibrary.WIN_MASSAGE, "Your opponent has" + MessagesLibrary.GAVE_UP_MASSAGE));
                colorIOControllerMap.get(opponent).presentMsg(String.format(MessagesLibrary.LOSE_MASSAGE, "You" + MessagesLibrary.GAVE_UP_MASSAGE));
            }
            case CheckMate -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(String.format(MessagesLibrary.WIN_MASSAGE, MessagesLibrary.CHECKMATE_MASSAGE));
                colorIOControllerMap.get(opponent).presentMsg(String.format(MessagesLibrary.LOSE_MASSAGE, MessagesLibrary.CHECKMATE_MASSAGE));
            }
            case Pat -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(MessagesLibrary.PAT_MASSAGE);
                colorIOControllerMap.get(opponent).presentMsg(MessagesLibrary.PAT_MASSAGE);
            }
        }
        //finishCallBack.call();
    }*/

    /**
     * @param playerColor the player color who make the move
     * @param start       place of the piece you want to move
     * @param finish      where to move the piece
     */
    /*public void makeAMove(Color playerColor, Place start, Place finish) throws RuntimeException {


        if (!board.isLegalMove(start, finish, playerColor))
            throw new RuntimeException(MessagesLibrary.INVALID_CHOICE);

        board.moveAPiece(start, finish, () -> 'Q');
    }*/
}
