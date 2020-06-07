package su.svn.hiload.socialnetwork.consistenthash;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LabelNodeTest {

    static final UUID uuid1 = UUID.fromString("402dd91b-7a14-46ef-adf6-1707135426ee");

    static final UUID uuid2 = UUID.fromString("e13cc11b-dee7-4190-8225-3c08caece235");

    LabelNode labelNode1;

    LabelNode labelNode2;

    @BeforeEach
    void setUp() {
        labelNode1 = new LabelNode(uuid1);
        labelNode2 = new LabelNode(uuid2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void uuidToString() {
        System.err.println("labelNode1 = " + labelNode1);
        System.err.println("labelNode2 = " + labelNode2);
    }
}