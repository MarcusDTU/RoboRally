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
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     * @author Asma Maryam, Turan Talayhan
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Task1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved
        Player currentPlayer = board.getCurrentPlayer();
        if(space.getPlayer() == null){
            currentPlayer.setSpace(space);
            space.setPlayer(currentPlayer);
            board.setTotalMoves(board.getTotalMoves() + 1);

            int nextPlayerNum;

            if(board.getPlayersNumber() != board.getPlayerNumber(currentPlayer) + 1){
                nextPlayerNum = board.getPlayerNumber(currentPlayer) + 1;
            } else {
                nextPlayerNum = (board.getPlayerNumber(currentPlayer) + 1) - board.getPlayersNumber();
            }

            Player nextPlayer = board.getPlayer(nextPlayerNum);
            board.setCurrentPlayer(nextPlayer);
        }

    }


    // XXX: implemented in the current version
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

    // XXX: implemented in the current version
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: implemented in the current version
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: implemented in the current version
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: implemented in the current version
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: implemented in the current version
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: implemented in the current version
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

    // XXX: implemented in the current version
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

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

    // TODO Task2
    public void moveForward(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading currentHeading = player.getHeading();

        switch(currentHeading){

            case NORTH -> {
                if(currentSpace.y > 0)
                    player.setSpace(this.board.getNeighbour(currentSpace,Heading.NORTH));
            }
            case SOUTH -> {
                if(currentSpace.x <= board.height-1)player.setSpace(this.board.getNeighbour(currentSpace,Heading.SOUTH));
            }
            case EAST -> {
                if(currentSpace.x < 0)
                    player.setSpace(this.board.getNeighbour(currentSpace,Heading.EAST));
            }
            case WEST -> {
                if(currentSpace.x >= board.width-1)player.setSpace(this.board.getNeighbour(currentSpace,Heading.WEST));
            }

        }


    }

    // TODO Task2
    public void fastForward(@NotNull Player player) {

        Space currentSpace = player.getSpace();
        Heading currentHeading = player.getHeading();
        Space newSpace1;
        Space newSpace2;

        switch(currentHeading){
            case EAST:
                newSpace1 = board.getNeighbour(currentSpace, currentHeading);
                newSpace2 = board.getNeighbour(newSpace1, currentHeading);
                player.setSpace(newSpace2);
                break;
            case WEST:
                newSpace1 = board.getNeighbour(currentSpace, currentHeading);
                newSpace2 = board.getNeighbour(newSpace1, currentHeading);
                player.setSpace(newSpace2);
                break;
            case NORTH:
                newSpace1 = board.getNeighbour(currentSpace, currentHeading);
                newSpace2 = board.getNeighbour(newSpace1, currentHeading);
                player.setSpace(newSpace2);
                break;
            case SOUTH:
                newSpace1 = board.getNeighbour(currentSpace, currentHeading);
                newSpace2 = board.getNeighbour(newSpace1, currentHeading);
                player.setSpace(newSpace2);
                break;
        }

    }

    // TODO Task2
    public void turnRight(@NotNull Player player) {
        Heading currentHeading = player.getHeading();

        switch(currentHeading){

            case NORTH -> player.setHeading(Heading.EAST);
            case SOUTH -> player.setHeading(Heading.WEST);
            case EAST -> player.setHeading(Heading.SOUTH);
            case WEST -> player.setHeading(Heading.NORTH);
        }
    }

    // TODO Task2
    public void turnLeft(@NotNull Player player) {

        Heading currentHeading = player.getHeading();

        switch(currentHeading){
            case EAST:
                player.setHeading(Heading.NORTH);
                break;
            case WEST:
                player.setHeading(Heading.SOUTH);
                break;
            case NORTH:
                player.setHeading(Heading.WEST);
                break;
            case SOUTH:
                player.setHeading(Heading.EAST);
                break;
        }

    }

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
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
