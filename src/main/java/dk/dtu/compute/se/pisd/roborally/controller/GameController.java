/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * The game controller for the game which is responsible for the gameplay logic.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     * @author Asma Maryam, s230716@dtu.dk
     * @author Turan Talayhan, s224746@dtu.dk
     * @author Daniel Overballe Lerche, s235095@dtu.dk
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        Player currentPlayer = board.getCurrentPlayer();
        if(space.getPlayer() == null){
            currentPlayer.setSpace(space);
            space.setPlayer(currentPlayer);
            board.setTotalMoves(board.getTotalMoves() + 1);

            int nextPlayerNum = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();

            Player nextPlayer = board.getPlayer(nextPlayerNum);
            board.setCurrentPlayer(nextPlayer);
        }
    }

    /**
     * Starts the programming phase of the game.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Generates a random command card.
     * @return a random command card
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Finishes the programming phase of the game and starts the activation phase.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * @param register allows you to select which set of cards should be displayed on the user interface.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Makes the program fields invisible.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes all the program registers of the players.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes the next step in the program registers of the players.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continues the execution of the program registers of the players.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes the next step in the program registers of the players.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * @param player represent the player, who is currently taking their turn in the game.
     * @param command that lists the possible actions a player's robot can take on their turn.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Moves the player according the current heading of the player.
     * @param player the player that shall move
     * @author Marcus Reiner Langkilde, s195080@dtu.dk
     * @author Haleef Abu Talib, s224523@dtu.dk
     * @author Daniel Overballe Lerche, s235095@dtu.dk
     */
    public void moveForward(@NotNull Player player) {
        final Space currentSpace = player.getSpace();
        final Heading currentHeading = player.getHeading();

        player.setSpace(this.board.getNeighbour(currentSpace, currentHeading));
    }

    /**
     * Moves the player two steps forward according the current heading of the player.
     * @param player the player that shall move
     * @author Marcus Reiner Langkilde, s195080@dtu.dk
     * @author Haleef Abu Talib, s224523@dtu.dk
     * @author Daniel Overballe Lerche, s235095@dtu.dk
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     * Turn the player to the right according to its heading
     * @param player the player that shall turn right
     * @author Marcus Reiner Langkilde, s195080@dtu.dk
     * @author Haleef Abu Talib, s224523@dtu.dk
     * @author Daniel Overballe Lerche, s235095@dtu.dk
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    /**
     * Turns the player to the left according to its heading
     * @param player the player that shall turn left
     * @author Marcus Reiner Langkilde, s195080@dtu.dk
     * @author Haleef Abu Talib, s224523@dtu.dk
     * @author Daniel Overballe Lerche, s235095@dtu.dk
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    /**
     * Moves a command card from source to target.
     * @param source represents the  field from which a command card will be moved.
     * @param target represents the target field or "for example discard " to which the command card will be moved.
     * @return true if the card was moved, false otherwise.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
