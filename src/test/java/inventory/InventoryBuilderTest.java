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
        InventoryBuilder inv = new InventoryBuilder(9);
        assertEquals(9, inv.getSize());

        for(InventoryType type : InventoryType.values()){
            InventoryBuilder typedInv = new InventoryBuilder("Test", type);
            assertEquals(type.getDefaultSize(), typedInv.getSize());
        }
    }

    @Test
    @DisplayName("Test the setMaxStackSize method")
    public void testSetMaxStackSize(){
        InventoryBuilder inv = new InventoryBuilder(9);
        inv.setMaxStackSize(32);

        assertEquals(32, inv.getMaxStackSize());
        assertEquals(32, inv.build().getMaxStackSize());

        for(int i = 1; i <= 64; i++){
            int size = i;
            assertDoesNotThrow(() -> inv.setMaxStackSize(size));
        }

        assertThrows(IllegalArgumentException.class, () -> inv.setMaxStackSize(0));
        assertThrows(IllegalArgumentException.class, () -> inv.setMaxStackSize(65));
    }

    @Test
    @DisplayName("Test the getName method")
    public void testGetName(){
        InventoryBuilder inv = new InventoryBuilder("Inventaire de test", 9);

        assertEquals("Inventaire de test", inv.getName());
    }

    @Test
    @DisplayName("Test the getItem method")
    public void testGetItem(){
        Inventory defaultInv = Bukkit.createInventory(null, 9);
        defaultInv.addItem(new ItemStack(Material.DIAMOND, 1));

        InventoryBuilder inv = new InventoryBuilder(defaultInv, true);

        assertEquals(new ItemStack(Material.DIAMOND, 1), inv.getItem(0));
        assertNull(inv.getItem(8));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.getItem(9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.getItem(-1));
    }

    @Test
    @DisplayName("Test the setItem method")
    public void testSetItem(){
        InventoryBuilder inv = new InventoryBuilder(9);

        inv.setItem(0, new ItemStack(Material.DIAMOND, 1));

        assertEquals(new ItemStack(Material.DIAMOND, 1), inv.getItem(0));
        assertEquals(new ItemStack(Material.DIAMOND, 1), inv.build().getItem(0));

        for(int i = 0; i < 9; i++){
            int slot = i;
            assertDoesNotThrow(() -> inv.setItem(slot, new ItemStack(Material.DIAMOND, 1)));
        }

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.setItem(9, new ItemStack(Material.DIAMOND, 1)));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.setItem(-1, new ItemStack(Material.DIAMOND, 1)));
    }

    @Test
    @DisplayName("Test the addItem method")
    public void testAddItem(){
        InventoryBuilder inv = new InventoryBuilder(9);

        inv.addItem(new ItemStack(Material.DIAMOND, 1));
        inv.addItem(new ItemStack(Material.GOLD_INGOT, 1));

        assertEquals(new ItemStack(Material.DIAMOND, 1), inv.getItem(0));
        assertEquals(new ItemStack(Material.DIAMOND, 1), inv.build().getItem(0));

        assertEquals(new ItemStack(Material.GOLD_INGOT, 1), inv.getItem(1));
        assertEquals(new ItemStack(Material.GOLD_INGOT, 1), inv.build().getItem(1));
    }

    @Test
    @DisplayName("Test the addItem method when the inventory is full")
    public void testSafeAddItemWhenInvFull(){
        InventoryBuilder inv = new InventoryBuilder(9);

        for(int i = 0; i < 9; i++){
            inv.setItem(i, new ItemStack(Material.DIAMOND, 1));
        }

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.safeAddItem(new ItemStack(Material.GOLD_INGOT, 1)));
    }

    @Test
    @DisplayName("Test the removeItem method")
    public void testRemoveItem(){
        Inventory baseInv = Bukkit.createInventory(null, 9);
        baseInv.addItem(new ItemStack(Material.DIAMOND, 1));

        InventoryBuilder inv = new InventoryBuilder(baseInv, false);

        assertDoesNotThrow(() -> inv.removeItem(new ItemStack(Material.DIAMOND, 1)));
        assertNull(inv.getItem(0));

        assertThrows(IllegalArgumentException.class, () -> baseInv.removeItem(null));
        assertThrows(IllegalArgumentException.class, () -> baseInv.removeItem(null, null));
        assertThrows(IllegalArgumentException.class, () -> baseInv.removeItem(new ItemStack[]{null}));
    }

    @Test
    @DisplayName("Test the getContents method")
    public void testGetContents(){
        Inventory baseInv = Bukkit.createInventory(null, 9);

        baseInv.addItem(new ItemStack(Material.DIAMOND, 1));
        baseInv.addItem(new ItemStack(Material.GOLD_INGOT, 1));

        InventoryBuilder inv = new InventoryBuilder(baseInv, true);
        assertArrayEquals(baseInv.getContents(), inv.getContents());

        Inventory emptyInv = Bukkit.createInventory(null, 9);
        assertArrayEquals(emptyInv.getContents(), new InventoryBuilder(emptyInv, true).getContents());
    }

    @Test
    @DisplayName("Test the setContents method")
    public void testSetContents(){
        ItemStack[] items = {
                new ItemStack(Material.DIAMOND, 1),
                new ItemStack(Material.GOLD_INGOT, 1)
        };

        InventoryBuilder inv = new InventoryBuilder(9);
        inv.setContents(items);
        assertArrayEquals(items, inv.getContents());
    }

    @Test
    @DisplayName("Test the contains(int) method")
    public void testContainsUsingID(){
        InventoryBuilder inv = new InventoryBuilder(9)
                                .addItem(new ItemStack(Material.DIAMOND, 1));

        assertTrue(inv.contains(Material.DIAMOND.getId()));
        assertFalse(inv.contains(Material.GOLD_INGOT.getId()));
        assertThrows(IllegalArgumentException.class, () -> inv.contains(-1));
    }

    @Test
    @DisplayName("Test the contains(Material) method")
    public void testContainsUsingMaterial(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        assertTrue(inv.contains(Material.DIAMOND));
        assertFalse(inv.contains(Material.GOLD_INGOT));
    }

    @Test
    @DisplayName("Test the contains(ItemStack) method")
    public void testContainsUsingItemStack(){
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemStack itemNotIn = new ItemStack(Material.GOLD_INGOT, 1);

        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(item);

        assertTrue(inv.contains(item));
        assertFalse(inv.contains(itemNotIn));
    }

    @Test
    @DisplayName("Test the contains(int,int) method")
    public void testContainsUsingIDAndAmount(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 26));

        assertTrue(inv.contains(Material.DIAMOND.getId(), 26));
        assertFalse(inv.contains(Material.GOLD_INGOT.getId(), 1));
        assertThrows(IllegalArgumentException.class, () -> inv.contains(-1, 1));
    }

    @Test
    @DisplayName("Test the contains(Material,int) method")
    public void testContainsUsingMaterialAndAmount(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 29));

        assertTrue(inv.contains(Material.DIAMOND, 29));
        assertFalse(inv.contains(Material.GOLD_INGOT, 1));
    }

    @Test
    @DisplayName("Test the contains(ItemStack,int) method")
    public void testContainsUsingItemStackAndAmount(){
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemStack itemNotIn = new ItemStack(Material.GOLD_INGOT, 34);

        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(item);

        assertTrue(inv.contains(item, 34));
        assertFalse(inv.contains(itemNotIn));
    }

    @Test
    @DisplayName("Test the containsAtLeast(ItemStack,int) method")
    public void testContainsAtLeast(){
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemStack itemNotIn = new ItemStack(Material.GOLD_INGOT, 34);

        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(item);

        for(int i = 1; i <= 34; i++){
            assertTrue(inv.containsAtLeast(item, i));
        }
        assertFalse(inv.containsAtLeast(itemNotIn, 1));
    }

    @Test
    @DisplayName("Test the first(int) method")
    public void testFirstFromID(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        assertEquals(0, inv.first(Material.DIAMOND.getId()));
        assertEquals(-1, inv.first(Material.GOLD_INGOT.getId()));
    }

    @Test
    @DisplayName("Test the first(Material) method")
    public void testFirstFromMaterial(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        assertEquals(0, inv.first(Material.DIAMOND));
        assertEquals(-1, inv.first(Material.GOLD_INGOT));
    }

    @Test
    @DisplayName("Test the first(ItemStack) method")
    public void testFirstFromItemStack(){
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemStack itemNotIn = new ItemStack(Material.GOLD_INGOT, 1);

        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(item);

        assertEquals(0, inv.first(item));
        assertEquals(-1, inv.first(itemNotIn));
    }

    @Test
    @DisplayName("Test the firstEmpty method")
    public void testFirstEmpty(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        assertEquals(1, inv.firstEmpty());
        inv.clear();
        assertEquals(0, inv.firstEmpty());

    }

    @Test
    @DisplayName("Test the remove(int) method")
    public void testRemoveFromID(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        inv.remove(Material.DIAMOND.getId());
        assertNull(inv.getItem(0));
    }

    @Test
    @DisplayName("Test the remove(Material) method")
    public void testRemoveFromMaterial(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        inv.remove(Material.DIAMOND);
        assertNull(inv.getItem(0));
    }

    @Test
    @DisplayName("Test the clear(int) method")
    public void testClearFromID(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        inv.clear(Material.DIAMOND.getId());
        assertEquals(0, inv.firstEmpty());
        assertNull(inv.getItem(0));
    }

    @Test
    @DisplayName("Test the clear method")
    public void testClear(){
        InventoryBuilder inv = new InventoryBuilder(9)
                .addItem(new ItemStack(Material.DIAMOND, 1));

        inv.clear();
        assertEquals(0, inv.firstEmpty());
        assertNull(inv.getItem(0));
    }

    @Test
    @DisplayName("Test the getViewers method")
    public void testGetViewers(){
        InventoryBuilder inv = new InventoryBuilder(9);

        assertEquals(0, inv.getViewers().size());

        Player player = server.addPlayer();
        player.openInventory(inv.build());

        assertEquals(1, inv.getViewers().size());
        assertEquals(player, inv.getViewers().get(0));
    }

    @Test
    @DisplayName("Test the getTitle method")
    public void testGetTitle(){
        InventoryBuilder inv = new InventoryBuilder("Test", 9);
        assertEquals("Test", inv.getTitle());
    }

    @Test
    @DisplayName("Test the getType method")
    public void testGetType(){
        InventoryBuilder inv = new InventoryBuilder("Test", InventoryType.CHEST);
        assertEquals(InventoryType.CHEST, inv.getType());
    }

    @Test
    @DisplayName("Test the getHolder method")
    public void testGetHolder(){
        InventoryBuilder inv = new InventoryBuilder("Test", 9);
        assertNull(inv.getHolder());

        Player player = server.addPlayer();
        inv = new InventoryBuilder("Test", 9, player);
        assertEquals(player, inv.getHolder());
    }

    @Test
    @DisplayName("Test the iterator getter method")
    public void testIterator(){
        InventoryBuilder inv = new InventoryBuilder("Test", 9);
        assertNotNull(inv.iterator());
    }


    @AfterAll
    public void tearDown() {
        if(MockBukkit.isMocked()) MockBukkit.unload();
    }


}
