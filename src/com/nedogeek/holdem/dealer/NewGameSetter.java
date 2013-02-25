package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.PlayerStatus;
import com.nedogeek.holdem.gamingStuff.Player;
import com.nedogeek.holdem.gamingStuff.PlayersList;

/**
 * User: Konstantin Demishev
 * Date: 21.11.12
 * Time: 23:42
 */
public class NewGameSetter {
    private final Dealer dealer;
    private final MoveManager moveManager;
    private final PlayersList playersManager;

    public NewGameSetter(Dealer dealer, PlayersList playersManager, MoveManager moveManager) {
        this.dealer = dealer;
        this.playersManager = playersManager;
        this.moveManager = moveManager;
    }

    void setNewGame() {
        dealer.resetCards();
        playersManager.changeDealer();
        resetPlayers();
        makeInitialBets();

        dealer.setNextGameRound();
    }

    private void resetPlayers() {
        for (Player player: playersManager) {
            if (player.getStatus() != PlayerStatus.Lost) {
                dealer.giveCardsToPlayer(player);
                player.setStatus(PlayerStatus.NotMoved);
            }
        }
    }

    private void makeInitialBets() {
        moveManager.makeSmallBlind(playersManager.smallBlindPlayer());
        System.out.println("Small blind player is: " + playersManager.smallBlindPlayer());
        moveManager.makeBigBlind(playersManager.bigBlindPlayer());
        System.out.println("Big blind player is: " + playersManager.bigBlindPlayer());
    }
}
