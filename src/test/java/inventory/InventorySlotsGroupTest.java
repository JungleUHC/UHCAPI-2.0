package inventory;

import fr.altaks.uhcapi2.core.util.inventory.InventorySlotsGroup;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to assert the InventorySlotsGroup class functionalities.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventorySlotsGroupTest {

    private ArrayList<Integer> slots;

    @BeforeAll
    public void setUp(){
        slots = new ArrayList<>();
    }

    @BeforeEach
    public void beforeEach(){
        slots.clear();
    }

    @Test
    @DisplayName("Assert the ALL slots includes every single slots from a 6x9 inventory")
    public void testAllInventorySlotGroup(){

        for(int i = 0; i < 54; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of ALL InventorySlotsGroup",
                () -> assertEquals(54, InventorySlotsGroup.ALL.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.ALL.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.ALL.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FIRST_ROW slots includes every single slots from a 6x9 inventory")
    public void testFirstRowInventorySlotGroup(){

        for(int i = 0; i < 9; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of FIRST_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.FIRST_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FIRST_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FIRST_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }


    @Test
    @DisplayName("Assert the SECOND_ROW slots includes every single slots from a 6x9 inventory")
    public void testSecondRowInventorySlotGroup(){

        for(int i = 9; i < 18; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of SECOND_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.SECOND_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.SECOND_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.SECOND_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the THIRD_ROW slots includes every single slots from a 6x9 inventory")
    public void testThirdRowInventorySlotGroup(){

        for(int i = 18; i < 27; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of THIRD_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.THIRD_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.THIRD_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.THIRD_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FOURTH_ROW slots includes every single slots from a 6x9 inventory")
    public void testFourthRowInventorySlotGroup(){

        for(int i = 27; i < 36; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of FOURTH_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.FOURTH_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FOURTH_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FOURTH_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FIFTH_ROW slots includes every single slots from a 6x9 inventory")
    public void testFifthRowInventorySlotGroup(){

        for(int i = 36; i < 45; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of FIFTH_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.FIFTH_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FIFTH_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FIFTH_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the SIXTH_ROW slots includes every single slots from a 6x9 inventory")
    public void testSixthRowInventorySlotGroup(){

        for(int i = 45; i < 54; i++){
            slots.add(i);
        }

        assertAll(
                "Asserts of SIXTH_ROW InventorySlotsGroup",
                () -> assertEquals(9, InventorySlotsGroup.SIXTH_ROW.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.SIXTH_ROW.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.SIXTH_ROW.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FIRST_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testFirstColumnInventorySlotGroup(){

        for(int i = 0; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of FIRST_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.FIRST_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FIRST_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FIRST_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the SECOND_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testSecondColumnInventorySlotGroup(){

        for(int i = 1; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of SECOND_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.SECOND_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.SECOND_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.SECOND_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the THIRD_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testThirdColumnInventorySlotGroup(){

        for(int i = 2; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of THIRD_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.THIRD_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.THIRD_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.THIRD_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FOURTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testFourthColumnInventorySlotGroup(){

        for(int i = 3; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of FOURTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.FOURTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FOURTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FOURTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the FIFTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testFifthColumnInventorySlotGroup(){

        for(int i = 4; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of FIFTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.FIFTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.FIFTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.FIFTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the SIXTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testSixthColumnInventorySlotGroup(){

        for(int i = 5; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of SIXTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.SIXTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.SIXTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.SIXTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the SEVENTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testSeventhColumnInventorySlotGroup(){

        for(int i = 6; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of SEVENTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.SEVENTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.SEVENTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.SEVENTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the EIGHTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testEighthColumnInventorySlotGroup(){

        for(int i = 7; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of EIGHTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.EIGHTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.EIGHTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.EIGHTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the NINTH_COLUMN slots includes every single slots from a 6x9 inventory")
    public void testNinthColumnInventorySlotGroup(){

        for(int i = 8; i < 54; i+=9){
            slots.add(i);
        }

        assertAll(
                "Asserts of NINTH_COLUMN InventorySlotsGroup",
                () -> assertEquals(6, InventorySlotsGroup.NINTH_COLUMN.getSlots().size()),
                () -> assertEquals(slots, InventorySlotsGroup.NINTH_COLUMN.getSlots()),
                () -> {
                    for(int slot : InventorySlotsGroup.NINTH_COLUMN.getSlots()){
                        assert 0 <= slot && slot < 54;
                    }
                }
        );

    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup add method works correctly")
    public void testAddMethodOfInventorySlotsGroup(){

        InventorySlotsGroup group = new InventorySlotsGroup();

        assertEquals(0, group.getSlots().size());
        assertEquals(new ArrayList<Integer>(), group.getSlots());

        group.add(1, 2, 3, 4, 5, 6, 7, 8, 9);
        for(int i = 1; i < 10; i++) slots.add(i);

        assertEquals(9, group.getSlots().size());
        assertEquals(slots, group.getSlots());

    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup remove method works correctly")
    public void testRemoveMethodOfInventorySlotsGroup(){

        InventorySlotsGroup group = new InventorySlotsGroup(1, 2, 3, 4, 5, 6, 7, 8, 9);
        for(int i = 1; i < 10; i++) slots.add(i);

        assertEquals(9, group.getSlots().size());
        assertEquals(slots, group.getSlots());

        group.remove(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(0, group.getSlots().size());
        assertEquals(new ArrayList<Integer>(), group.getSlots());

    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup merge method works correctly")
    public void testMergeMethodOfInventorySlotsGroup(){

        InventorySlotsGroup group = new InventorySlotsGroup(1, 2, 3, 4, 5, 6, 7, 8, 9);
        InventorySlotsGroup group2 = new InventorySlotsGroup(10, 11, 12, 13, 14, 15, 16, 17, 18);

        for(int i = 1; i < 10; i++) slots.add(i);

        assertEquals(9, group.getSlots().size());
        assertEquals(slots, group.getSlots());

        group.merge(group2);
        for(int i = 10; i < 19; i++) slots.add(i);

        assertEquals(18, group.getSlots().size());
        assertEquals(slots, group.getSlots());

    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup inner rectangle constant is right")
    public void testInnerRectangleConstant(){
        assertEquals(10, InventorySlotsGroup.INNER_RECTANGLE.getSlots().size());
        assertEquals(new InventorySlotsGroup(
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33
        ).getSlots(), InventorySlotsGroup.INNER_RECTANGLE.getSlots());
        for(int slot : InventorySlotsGroup.INNER_RECTANGLE.getSlots()){
            assert 0 <= slot && slot < 54;
        }
    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup inner rectangle constant is right")
    public void testOuterRectangleConstant(){
        assertEquals(26, InventorySlotsGroup.OUTER_RECTANGLE.getSlots().size());
        assertEquals(new InventorySlotsGroup(
                0,  1,  2,  3,  4,  5,  6,  7,  8,
                9,                              17,
                18,                             26,
                27,                             35,
                36,                             44,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        ).getSlots(), InventorySlotsGroup.OUTER_RECTANGLE.getSlots());
        for(int slot : InventorySlotsGroup.OUTER_RECTANGLE.getSlots()){
            assert 0 <= slot && slot < 54;
        }
    }

    @Test
    @DisplayName("Assert the InventorySlotsGroup inner rectangle constant is right")
    public void testMiddleRectangleConstant(){
        assertEquals(18, InventorySlotsGroup.MIDDLE_RECTANGLE.getSlots().size());
        assertEquals(new InventorySlotsGroup(
                10, 11, 12, 13, 14, 15, 16,
                19,                     25,
                28,                     34,
                37, 38, 39, 40, 41, 42, 43
        ).getSlots(), InventorySlotsGroup.MIDDLE_RECTANGLE.getSlots());
        for(int slot : InventorySlotsGroup.MIDDLE_RECTANGLE.getSlots()){
            assert 0 <= slot && slot < 54;
        }
    }

    @Test
    @DisplayName("Assert the left-offset checkboard constant is right")
    public void testLeftCheckBoardConstant() {
        for(int i = 0; i < 54; i++){
            if(i % 2 == 0) slots.add(i);
        }

        assertEquals(27, InventorySlotsGroup.LEFT_OFFSET_CHECKBOARD.getSlots().size());
        assertEquals(slots, InventorySlotsGroup.LEFT_OFFSET_CHECKBOARD.getSlots());
        for(int slot : InventorySlotsGroup.LEFT_OFFSET_CHECKBOARD.getSlots()){
            assert 0 <= slot && slot < 54;
        }
    }

    @Test
    @DisplayName("Assert the right-offset checkboard constant is right")
    public void testRightCheckBoardConstant() {
        for(int i = 0; i < 54; i++){
            if(i % 2 == 1) slots.add(i);
        }

        assertEquals(27, InventorySlotsGroup.RIGHT_OFFSET_CHECKBOARD.getSlots().size());
        assertEquals(slots, InventorySlotsGroup.RIGHT_OFFSET_CHECKBOARD.getSlots());
        for(int slot : InventorySlotsGroup.RIGHT_OFFSET_CHECKBOARD.getSlots()){
            assert 0 <= slot && slot < 54;
        }
    }


}
