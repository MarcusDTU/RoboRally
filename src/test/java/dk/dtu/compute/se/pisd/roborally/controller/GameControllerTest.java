package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import com.beust.ah.A;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void shouldMoveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void shouldMoveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void shouldTurnRight(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setHeading(Heading.NORTH);

        gameController.turnRight(current);

        Assertions.assertEquals(current.getHeading(),Heading.EAST);
    }
   @Test
    void shouldTurnLeft(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setHeading(Heading.NORTH);

        gameController.turnLeft(current);

        Assertions.assertEquals(current.getHeading(),Heading.WEST);
    }
    @Test
    void shouldMoveFastForward(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void shouldAssignPlayerProgrammingCards() {
        Command[] commands = new Command[]{ Command.FAST_FORWARD, Command.FORWARD, Command.FORWARD, Command.LEFT, Command.RIGHT };
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        for (int i = 0; i < 5; i++) {
            current.getProgramField(i).setCard(new CommandCard(commands[i]));
        }
        // Given the following commands: FAST_FORWARD, FORWARD, FORWARD, LEFT, RIGHT
        // When the player finishes programming phase
        gameController.finishProgrammingPhase();
        // Then the player should have the following commands: FAST_FORWARD, FORWARD, FORWARD, LEFT, RIGHT
        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals(commands[i], current.getProgramField(i).getCard().command);
        }
    }

    @Test
    void shouldExecuteCommandsInSteps() {
        Command[] commands = new Command[]{ Command.FORWARD, Command.FORWARD, Command.FORWARD, Command.FORWARD, Command.FORWARD };
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        // Given the following commands: FAST_FORWARD, FORWARD, FORWARD, LEFT, RIGHT
        for (int i = 0; i < 5; i++) {
            current.getProgramField(i).setCard(new CommandCard(commands[i]));
        }
        // When the player finishes programming phase
        gameController.finishProgrammingPhase();
        // When the player executes all the commands one by one
        for (int i = 1; i <= 5; i++) {
            gameController.executeStep();
            // Then the player should be at Space (0, i) and heading SOUTH
            Assertions.assertEquals(current, board.getSpace(0, i).getPlayer(), "Player " + current.getName() + " should be at Space (0, " + i + ")!");
            Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
            skipPlayers();
        }
    }
    // Helper method to skip all players except the current player
    void skipPlayers() {
        Board board = gameController.board;
        for (int i = 0; i < board.getPlayersNumber() - 1; i++) {
            gameController.executeStep();
        }
    }

    @Test
    void shouldExecuteAllCommands() {
        Command[] commands = new Command[]{ Command.FAST_FORWARD, Command.FORWARD, Command.FORWARD, Command.LEFT, Command.RIGHT };
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        // Given the following commands: FAST_FORWARD, FORWARD, FORWARD, LEFT, RIGHT
        for (int i = 0; i < 5; i++) {
            current.getProgramField(i).setCard(new CommandCard(commands[i]));
        }
        // When the player finishes programming phase
        gameController.finishProgrammingPhase();
        // When the player executes all the commands
        gameController.executePrograms();
        // Then the player should be at Space (0, 4) and heading SOUTH
        Assertions.assertEquals(current, board.getSpace(0, 4).getPlayer(), "Player " + current.getName() + " should be at Space (0, 4)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
    }

}