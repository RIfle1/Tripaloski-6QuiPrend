package project.classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void initializePlayers() {
        List<Player> playerList = Player.initializePlayers(3, 0);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, playerList.size());
            Assertions.assertEquals("Player-1", playerList.get(0).getCharacterName());
            Assertions.assertEquals(Player.class, playerList.get(0).getClass());
        });
    }
}