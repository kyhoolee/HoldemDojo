package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.GameRound;
import com.nedogeek.holdem.GameStatus;
import com.nedogeek.holdem.gamingStuff.Card;
import com.nedogeek.holdem.gamingStuff.CardDeck;
import com.nedogeek.holdem.gamingStuff.Player;
import com.nedogeek.holdem.gamingStuff.PlayersList;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Konstantin Demishev
 * Date: 05.10.12
 * Time: 22:02
 */
public class Dealer implements Runnable {
    private final MoveManager moveManager;
    private final NewGameSetter newGameSetter;
    private final PlayersList playersList;
    private final EndGameManager endGameManager;
    private final EventManager eventManager;

    private GameStatus gameStatus = GameStatus.NOT_READY;
    private GameRound gameRound;
    private boolean isStopped;

    private int tickNumber = 0;
    private int callValue;

    private CardDeck cardDeck;

    private List<Card> deskCards;

    public Dealer(PlayersList playersList) {
        this.playersList = playersList;
        eventManager = EventManager.getInstance();

        eventManager.setPlayersList(playersList);
        eventManager.setDealer(this);

        moveManager = new MoveManager(this, playersList);

        newGameSetter = new NewGameSetter(this, playersList, moveManager);
        endGameManager = new EndGameManager(this, playersList);
        gameRound = GameRound.INITIAL;
    }

    /**
     * Test constructor
     */
    Dealer(MoveManager moveManager, NewGameSetter newGameSetter,
           PlayersList playersList, EndGameManager endGameManager,
           GameStatus gameStatus, GameRound gameRound, EventManager eventManager) {
        this.moveManager = moveManager;
        this.newGameSetter = newGameSetter;
        this.playersList = playersList;
        this.endGameManager = endGameManager;
        this.gameStatus = gameStatus;
        this.gameRound = gameRound;
        this.eventManager = eventManager;
    }

    public void run() {
        while (!isStopped) {
            tick();
        }
    }

    void tick() {
        switch (gameStatus) {
            case READY:
                setGameStarted();
                break;
            case STARTED:
                makeGameAction();
                break;
        }
        tickNumber++;
        for (Player player : playersList) {
            System.out.println(tickNumber + ":" + " Game status: " + gameStatus + " Game round: " + gameRound + " " + player);
        }
    }

    private void makeGameAction() {
        switch (gameRound) {
            case INITIAL:
                newGameSetter.setNewGame();
                break;
            case FINAL:
                endGameManager.endGame();
                break;
            default:
                if (playersList.hasAvailableMovers()) {
                    moveManager.makeMove(playersList.getMover());
                } else {
                    if (playersList.moreThanOnePlayerWithActiveStatus())
                        setNextGameRound();
                    else
                        goToFinalRound();
                }
                break;
        }
    }

    private void goToFinalRound() {
        while (gameRound != GameRound.FINAL) {
            setNextGameRound();
        }
    }

    void setGameStarted() {
        gameStatus = GameStatus.STARTED;
        newGameSetter.setNewGame();
    }

    public int getCallValue() {
        return callValue;
    }

    void setCallValue(int callValue) {
        this.callValue = callValue;

    }

    void resetCards() {
        cardDeck = new CardDeck();
        deskCards = new ArrayList<>();
    }

    void setNextGameRound() {

        gameRound = GameRound.next(gameRound);
        callValue = 0;

        switch (gameRound) {
            case THREE_CARDS:
                deskCards.add(cardDeck.getCard());
                deskCards.add(cardDeck.getCard());
                deskCards.add(cardDeck.getCard());
                break;
            case FOUR_CARDS:
            case FIVE_CARDS:
                deskCards.add(cardDeck.getCard());
        }

        System.out.println(gameRound + ": " + deskCards);

        if (gameRound != GameRound.BLIND) {
            playersList.setPlayersNotMoved();
        }

    }

    void giveCardsToPlayer(Player player) {
        player.setCards(new Card[]{cardDeck.getCard(), cardDeck.getCard()});
    }

    @Deprecated
    void setPlayerWin(Player winner) {

    }

    void setInitialGameRound() {
        gameRound = GameRound.INITIAL;
    }

    public void stop() {
        isStopped = true;
    }

    @Deprecated
    public void setGameReady() {
        gameStatus = GameStatus.READY;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GameRound getGameRound() {
        return gameRound;
    }

    public Card[] getDeskCards() {
        Card[] cards = new Card[deskCards.size()];

        for (int i = 0; i < deskCards.size(); i++) {
            cards[i] = deskCards.get(i);
        }

        return cards;
    }
}
