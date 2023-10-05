import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.altaks.uhcapi2.Main;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the plugin using MockBukkit.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MockBukkitTest {

    // the available asserts are:
    //  - assertEquals (for primitives)
    //  - assertTrue (for booleans)
    //  - assertFalse (for booleans)
    //  - assertNull (for objects)
    //  - assertNotNull (for objects)
    //  - assertThrows (to test if a task throws an exception)
    //  - assertAll (to group asserts)
    //  - assertTimeout (to test if a task is executed within a given time)

    // To invalidate a test for a certain reason, use the following:
    // Assumptions.assumeTrue( <condition> );
    // Assumptions.assumeFalse( <condition> );

    /**
     * Define the server and plugin variables. DO NOT REMOVE !
     */
    private Main plugin;
    private ServerMock server;

    @BeforeAll
    public void setUp() {
        server = MockBukkit.mock();
        plugin = (Main) MockBukkit.load(Main.class);
    }

    @BeforeEach
    public void beforeEach(){
        server.setPlayers(0);
    }

    @Test
    @DisplayName("Test if the plugin enables itself correctly")
    public void testPluginEnabled() {
        assertTrue(plugin.isEnabled());
    }

    @Test
    @DisplayName("Test if the server registers when a PlayerMock joins in")
    public void testPlayerJoin() {
        server.addPlayer();
        assertEquals(1, server.getOnlinePlayers().size());
    }

    @AfterAll
    public void tearDown() {
        MockBukkit.unload();
    }


}
