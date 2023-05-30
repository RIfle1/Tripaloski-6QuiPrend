package project.classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class NpcTest {

    @Test
    void initializeNpcs() {
        List<Npc> npcList = Npc.initializeNpcs(3, 0);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(3, npcList.size());
            Assertions.assertEquals("Npc-1", npcList.get(0).getCharacterName());
            Assertions.assertEquals(Npc.class, npcList.get(0).getClass());
        });
    }
}