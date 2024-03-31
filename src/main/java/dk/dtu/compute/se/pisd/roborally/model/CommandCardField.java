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

/**
 *The CommandCardField class extends Subject class.
 * The field is observable for any change to its state,
 * such as when a card is placed on it or its visibility changes.
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CommandCardField extends Subject {

    final public Player player;

    private CommandCard card;

    private boolean visible;


    /**
     * A constructor associated with a specific player.
     * @param player the player to whom this field is associated
     * @author Ekkart Kindler
     */

    public CommandCardField(Player player) {
        this.player = player;
        this. card = null;
        this.visible = true;
    }

    /**
     * @return Return the command card.
     * @author Ekkart Kindler
     */
    public CommandCard getCard() {
        return card;
    }


    /**
     * @param card The command card to place on the field.
     * @author Ekkart Kindler, ekki@dtu.dk
     */

    public void setCard(CommandCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    /**
     * Checks this field id visible.
     * @return true if the field is visible, false otherwise.
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * this method sets the visibility of this field and notifies observers
     *if there is a change.
     * @param visible The new visibility state of this field, and have boolean type. .
     * @author Ekkart Kindler, ekki@dtu.dk
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
}
