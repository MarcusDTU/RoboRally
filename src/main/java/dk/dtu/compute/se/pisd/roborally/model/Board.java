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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * The Board class is responsible for representing a gameboard. Each board is composed of a number of spaces, based on a width and a height. 
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    /**
     * @param width of board (int)
     * @param height of board (int)
     * @param boardName of board (String)
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }
    /**
     * @param width of board (int)
     * @param height of board (int)
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    /**
     * Returns gameId
     * @return gameId of the board (Integer)
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Set gameId based on int parameter, if gameId is null. Otherwise, throw IllegalStateException
     * @param gameId int 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     *  Returns a space with the given x and y coordinates if it exists within the board, otherwise returns null
     *  @param x x-coordinate of space
     *  @param y y-coordinate of space
     *  @return space if the space exists within the board
     *  @author Nikolaj Schæbel, s220471@dtu.dk (javadoc only)
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Returns the number of players on the board.
     * @return number of players on the board (int).
     * @author Nikolaj Schæbel, s220471@dtu.dk (javadoc only).
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * @param player the player to add to the board
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /** 
     * Returns the player with the index of the int parameter if it exists within players list, otherwise returns null.
     * 
     * @param i index of player
     * @return player
     * @author Nikolaj Schæbel, s220471@dtu.dk (javadoc only)
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Returns the current player.
     * @return player current player
     * @author Nikolaj Schæbel, s220471@dtu.dk (javadoc only)
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Changes current player based on the player passed as a parameter. 
     * @param player to be designated as current player
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Returns current phase
     * 
     * @return phase current phase
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Changes phase based on the phase passed as a parameter.
     * 
     * @param phase to change to
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Returns step
     * 
     * @return step (int)
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public int getStep() {
        return step;
    }

    /**
     * Sets step based on the int passed as a parameter
     * 
     * @param step the integer to set step to
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Returns a boolean indicating whether stepMode is enabled or not
     * 
     * @return stepMode (boolean)
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Set StepMode based on boolean passed as a parameter
     * 
     * @param stepMode (boolean)
     * 
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Returns the player number (index) of a given player
     * 
     * @param player who you want to find the player number / index of
     * @return int player number / index
     * @author Nikolaj Schæbel s220471@dtu.dk (javadoc only)
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * @return the message for the status bar as a String
     */
    public String getStatusMessage() {
        // This is actually a view aspect, but for making the first task easy for
        // the students, this method gives a string representation of the current
        // status of the game (specifically, it shows the phase, the player and the step)

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep();

    }


}
