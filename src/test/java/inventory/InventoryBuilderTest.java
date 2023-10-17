package inventory;

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
        assertNotEquals(inv, inv2);
    }

    @Test
    @DisplayName("Test the copy constructor without deepCopy enabled")
    public void testCopyConstructorWithoutDeepCopy(){

        Inventory inv = Bukkit.createInventory(null, 9, "Test");
        Inventory inv2  = new InventoryBuilder(inv, false).build();

        inv.setItem(0, new ItemStack(Material.DIAMOND, 1));
        assertEquals(new ItemStack(Material.DIAMOND, 1), inv2.getItem(0));
        assertEquals(inv, inv2);
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
                assertDoesNotThrow(() -> new InventoryBuilder("Test", size, null));
                assertDoesNotThrow(() -> new InventoryBuilder("Test", size, player));
            }
        }

        Inventory inv = new InventoryBuilder("Test", 9, null).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(9, inv.getSize());
        assertNull(inv.getHolder());
        assertArrayEquals(new ItemStack[9], inv.getContents());

        // With a valid holder
        Inventory inv2 = new InventoryBuilder("Test", 9, player).build();

        assertEquals("Test", inv2.getTitle());
        assertEquals(9, inv2.getSize());
        assertEquals(player, inv2.getHolder());
        assertArrayEquals(new ItemStack[9], inv2.getContents());

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
        assertArrayEquals(new ItemStack[9], inv.getContents());
    }

    @Test
    @DisplayName("Test the constructor with title, type and holder")
    public void testConstructorWithTitleTypeHolder(){

        // With an invalid holder
        Inventory inv = new InventoryBuilder("Test", InventoryType.CHEST, null).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv.getSize());
        assertNull(inv.getHolder());
        assertArrayEquals(new ItemStack[InventoryType.CHEST.getDefaultSize()], inv.getContents());

        // Emulate a player
        Player player = server.addPlayer();

        // With a valid holder
        Inventory inv2 = new InventoryBuilder("Test", InventoryType.CHEST, player).build();

        assertEquals("Test", inv2.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv2.getSize());
        assertEquals(player, inv2.getHolder());
        assertArrayEquals(new ItemStack[InventoryType.CHEST.getDefaultSize()], inv2.getContents());

    }

    @Test
    @DisplayName("Test the constructor with title and type")
    public void testConstructorWithTitleType(){


        Inventory inv = new InventoryBuilder("Test", InventoryType.CHEST).build();

        assertEquals("Test", inv.getTitle());
        assertEquals(InventoryType.CHEST.getDefaultSize(), inv.getSize());
        assertNull(inv.getHolder());

        assertArrayEquals(new ItemStack[InventoryType.CHEST.getDefaultSize()], inv.getContents());
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

        assertArrayEquals(new ItemStack[9], inv.getContents());
    }

    @Test
    @DisplayName("Test the getSize method")
    public void testGetSize(){

    }

    @Test
    @DisplayName("Test the getMaxStackSize method")
    public void testGetMaxStackSize(){

    }

    @Test
    @DisplayName("Test the setMaxStackSize method")
    public void testSetMaxStackSize(){

    }

    @Test
    @DisplayName("Test the getName method")
    public void testGetName(){

    }

    @Test
    @DisplayName("Test the getItem method")
    public void testGetItem(){

    }

    @Test
    @DisplayName("Test the setItem method")
    public void testSetItem(){

    }

    @Test
    @DisplayName("Test the addItem method")
    public void testAddItem(){

    }

    @Test
    @DisplayName("Test the removeItem method")
    public void testRemoveItem(){

    }

    @Test
    @DisplayName("Test the getContents method")
    public void testGetContents(){

    }

    @Test
    @DisplayName("Test the setContents method")
    public void testSetContents(){

    }

    @Test
    @DisplayName("Test the contains(int) method")
    public void testContainsUsingID(){

    }

    @Test
    @DisplayName("Test the contains(Material) method")
    public void testContainsUsingMaterial(){

    }

    @Test
    @DisplayName("Test the contains(ItemStack) method")
    public void testContainsUsingItemStack(){

    }

    @Test
    @DisplayName("Test the contains(int,int) method")
    public void testContainsUsingIDAndAmount(){

    }

    @Test
    @DisplayName("Test the contains(Material,int) method")
    public void testContainsUsingMaterialAndAmount(){

    }

    @Test
    @DisplayName("Test the contains(ItemStack,int) method")
    public void testContainsUsingItemStackAndAmount(){

    }

    @Test
    @DisplayName("Test the containsAtLeast(ItemStack,int) method")
    public void testContainsAtLeast(){

    }

    @Test
    @DisplayName("Test the all(int) method")
    public void testAllFromID(){

    }

    @Test
    @DisplayName("Test the all(Material) method")
    public void testAllFromMaterial(){

    }

    @Test
    @DisplayName("Test the all(ItemStack) method")
    public void testAllFromItemStack(){

    }

    @Test
    @DisplayName("Test the first(int) method")
    public void testFirstFromID(){

    }

    @Test
    @DisplayName("Test the first(Material) method")
    public void testFirstFromMaterial(){

    }

    @Test
    @DisplayName("Test the first(ItemStack) method")
    public void testFirstFromItemStack(){

    }

    @Test
    @DisplayName("Test the firstEmpty method")
    public void testFirstEmpty(){

    }

    @Test
    @DisplayName("Test the remove(int) method")
    public void testRemoveFromID(){

    }

    @Test
    @DisplayName("Test the remove(Material) method")
    public void testRemoveFromMaterial(){

    }

    @Test
    @DisplayName("Test the clear(int) method")
    public void testClearFromID(){

    }

    @Test
    @DisplayName("Test the clear method")
    public void testClear(){

    }

    @Test
    @DisplayName("Test the getViewers method")
    public void testGetViewers(){

    }

    @Test
    @DisplayName("Test the getTitle method")
    public void testGetTitle(){

    }

    @Test
    @DisplayName("Test the getType method")
    public void testGetType(){

    }

    @Test
    @DisplayName("Test the getHolder method")
    public void testGetHolder(){

    }

    @Test
    @DisplayName("Test the iterator getter method")
    public void testIterator(){

    }


    @AfterAll
    public void tearDown() {
        if(MockBukkit.isMocked()) MockBukkit.unload();
    }


}
