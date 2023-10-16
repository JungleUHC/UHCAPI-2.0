import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.util.inventory.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the {@link fr.altaks.uhcapi2.core.util.inventory.InventoryBuilder} class.
 */
@SuppressWarnings("deprecation")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventoryBuilderTest {

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

    public boolean areInventoriesEqual(Inventory expected, Inventory value){
        if(expected.getSize() != value.getSize()) return false;
        if(expected.getTitle() != null){
            if(!expected.getTitle().equals(value.getTitle())) return false;
        } else {
            if(value.getTitle() != null) return false;
        }
        if(expected.getHolder() != null) {
            if(!expected.getHolder().equals(value.getHolder())) return false;
        } else if(value.getHolder() != null) return false;
        for(int slot = 0; slot < expected.getSize(); slot++){
            if(expected.getItem(slot) != null){
                if(!expected.getItem(slot).equals(value.getItem(slot))) return false;
            }
        }
        return true;
    }

    @Test
    @DisplayName("Test the copy constructor with deepCopy enabled")
    public void testCopyConstructorWithDeepCopy(){

        Inventory inv = Bukkit.createInventory(null, 9, "Test");
        Inventory inv2  = new InventoryBuilder(inv, true).build();

        assertTrue(areInventoriesEqual(inv, inv2));
    }

    @Test
    @DisplayName("Test the copy constructor without deepCopy enabled")
    public void testCopyConstructorWithoutDeepCopy(){

        Inventory inv = Bukkit.createInventory(null, 9, "Test");
        Inventory inv2  = new InventoryBuilder(inv, false).build();

        inv.setItem(0, new ItemStack(Material.DIAMOND, 1));
        assertEquals(new ItemStack(Material.DIAMOND, 1), inv2.getItem(0));

        inv.setMaxStackSize(16);
        assertEquals(16, inv2.getMaxStackSize());
    }

    @Test
    @DisplayName("Test the constructor with title, size and holder")
    public void testConstructorWithTitleSizeHolder(){

        // Emulate a player
        Player player = server.addPlayer();

        for(int i = 0; i <= 6*9; i++){
            int size = i;
            if(i % 9 != 0){
                assertThrows(IllegalArgumentException.class, () -> new InventoryBuilder("Test", size, null));
                assertThrows(IllegalArgumentException.class, () -> new InventoryBuilder("Test", size, player));
            } else {
                assertDoesNotThrow(() -> new InventoryBuilder(size));
            }
        }

        Inventory inv = new InventoryBuilder("Test", 9, null).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(9, inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(inv.getContents(), new ItemStack[]{ null, null, null, null, null, null,null,null,null });
        assertEquals(0, inv.firstEmpty());
        assertEquals(0, inv.getViewers().size());

        // With a valid holder
        Inventory inv2 = new InventoryBuilder("Test", 9, player).build();

        assertEquals("Test", inv2.getTitle());
        assertEquals(9, inv2.getSize());
        assertEquals(player, inv2.getHolder());

        assertArrayEquals(inv2.getContents(), new ItemStack[]{});
        assertEquals(0, inv2.firstEmpty());
        assertEquals(0, inv2.getViewers().size());
    }

    @Test
    @DisplayName("Test the constructor with title and size")
    public void testConstructorWithTitleSize(){

        for(int i = 0; i <= 6*9; i++){
            int size = i;
            if(i % 9 != 0){
                assertThrows(IllegalArgumentException.class, () -> new InventoryBuilder("Test", size));
            } else {
                assertDoesNotThrow(() -> new InventoryBuilder(size));
            }
        }

        Inventory inv = new InventoryBuilder("Test", 9).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(9, inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(inv.getContents(), new ItemStack[]{null, null, null, null, null, null, null, null, null});
        assertEquals(0, inv.firstEmpty());
        assertEquals(0, inv.getViewers().size());
    }

    @Test
    @DisplayName("Test the constructor with title, type and holder")
    public void testConstructorWithTitleTypeHolder(){

        // With an invalid holder
        Inventory inv = new InventoryBuilder("Test", InventoryType.CHEST, null).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(inv.getContents(), new ItemStack[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null});
        assertEquals(0, inv.firstEmpty());
        assertEquals(0, inv.getViewers().size());

        // Emulate a player
        Player player = server.addPlayer();

        // With a valid holder
        Inventory inv2 = new InventoryBuilder("Test", InventoryType.CHEST, player).build();

        assertEquals("Test", inv2.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv2.getSize());
        assertEquals(player, inv2.getHolder());

        assertArrayEquals(inv2.getContents(), new ItemStack[]{});
        assertEquals(0, inv2.firstEmpty());
        assertEquals(0, inv2.getViewers().size());
    }

    @Test
    @DisplayName("Test the constructor with title and type")
    public void testConstructorWithTitleType(){

        Inventory inv = new InventoryBuilder("Test", InventoryType.CHEST).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(inv.getContents(), new ItemStack[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null});
        assertEquals(0, inv.firstEmpty());
        assertEquals(0, inv.getViewers().size());
    }

    @Test
    @DisplayName("Test the constructor with size")
    public void testConstructorWithSize(){

        for(int i = 0; i <= 6*9; i++){
            int size = i;
            if(i % 9 != 0){
                assertThrows(IllegalArgumentException.class, () -> new InventoryBuilder(size));
            } else {
                assertDoesNotThrow(() -> new InventoryBuilder(size));
            }
        }

        Inventory inv = new InventoryBuilder(9).build();

        assertEquals(9, inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(inv.getContents(), new ItemStack[]{null, null, null, null, null, null, null, null, null});
        assertEquals(0, inv.firstEmpty());
        assertEquals(0, inv.getViewers().size());
    }

    @AfterAll
    public void tearDown() {
        if(MockBukkit.isMocked()) MockBukkit.unload();
    }


}
