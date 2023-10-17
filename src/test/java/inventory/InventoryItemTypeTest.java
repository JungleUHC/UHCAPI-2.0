package inventory;

import fr.altaks.uhcapi2.core.util.inventory.InventoryItemType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the plugin using MockBukkit.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class InventoryItemTypeTest {

    @Test
    @DisplayName("Test if the interactive mode casts in the correct String value as human readable format")
    public void testInteractiveCast(){
        assertEquals("Interactive", InventoryItemType.INTERACTIVE.toString());
    }

    @Test
    @DisplayName("Test if the rotating mode casts in the correct String value as human readable format")
    public void testRotatingCast(){
        assertEquals("Rotating", InventoryItemType.ROTATING.toString());
    }

    @Test
    @DisplayName("Test if the swapping mode casts in the correct String value as human readable format")
    public void testSwappingCast(){
        assertEquals("Swapping", InventoryItemType.SWAPPING.toString());
    }

    @Test
    @DisplayName("Test if the static mode casts in the correct String value as human readable format")
    public void testStaticCast(){
        assertEquals("Static", InventoryItemType.STATIC.toString());
    }

}
