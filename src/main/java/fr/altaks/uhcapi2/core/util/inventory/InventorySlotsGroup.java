package fr.altaks.uhcapi2.core.util.inventory;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class InventorySlotsGroup {

    /*
            0,   1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42 ,43, 44,
            45, 46, 47, 48, 49, 50 ,51, 52, 53
     */

    public static final InventorySlotsGroup ALL = new InventorySlotsGroup(
            0,   1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42 ,43, 44,
            45, 46, 47, 48, 49, 50 ,51, 52, 53
    );

    public static final InventorySlotsGroup FIRST_ROW = new InventorySlotsGroup(
            0,   1,  2,  3,  4,  5,  6,  7,  8
    );

    public static final InventorySlotsGroup SECOND_ROW = new InventorySlotsGroup(
            9,  10, 11, 12, 13, 14, 15, 16, 17
    );

    public static final InventorySlotsGroup THIRD_ROW = new InventorySlotsGroup(
            18, 19, 20, 21, 22, 23, 24, 25, 26
    );

    public static final InventorySlotsGroup FOURTH_ROW = new InventorySlotsGroup(
            27, 28, 29, 30, 31, 32, 33, 34, 35
    );

    public static final InventorySlotsGroup FIFTH_ROW = new InventorySlotsGroup(
            36, 37, 38, 39, 40, 41, 42 ,43, 44
    );

    public static final InventorySlotsGroup SIXTH_ROW = new InventorySlotsGroup(
            45, 46, 47, 48, 49, 50 ,51, 52, 53
    );

    public static final InventorySlotsGroup FIRST_COLUMN = new InventorySlotsGroup(
            0,  9, 18, 27, 36, 45
    );

    public static final InventorySlotsGroup SECOND_COLUMN = new InventorySlotsGroup(
            1, 10, 19, 28, 37, 46
    );

    public static final InventorySlotsGroup THIRD_COLUMN = new InventorySlotsGroup(
            2, 11, 20, 29, 38, 47
    );

    public static final InventorySlotsGroup FOURTH_COLUMN = new InventorySlotsGroup(
            3, 12, 21, 30, 39, 48
    );

    public static final InventorySlotsGroup FIFTH_COLUMN = new InventorySlotsGroup(
            4, 13, 22, 31, 40, 49
    );

    public static final InventorySlotsGroup SIXTH_COLUMN = new InventorySlotsGroup(
            5, 14, 23, 32, 41, 50
    );

    public static final InventorySlotsGroup SEVENTH_COLUMN = new InventorySlotsGroup(
            6, 15, 24, 33, 42, 51
    );

    public static final InventorySlotsGroup EIGHTH_COLUMN = new InventorySlotsGroup(
            7, 16, 25, 34, 43, 52
    );

    public static final InventorySlotsGroup NINTH_COLUMN = new InventorySlotsGroup(
            8, 17, 26, 35, 44, 53
    );

    public static final InventorySlotsGroup INNER_RECTANGLE = new InventorySlotsGroup(
            20, 21, 22, 23, 24,
            29, 30, 31, 32, 33
    );

    public static final InventorySlotsGroup OUTER_RECTANGLE = new InventorySlotsGroup(
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,                              17,
            18,                             26,
            27,                             35,
            36,                             44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    );

    public static final InventorySlotsGroup MIDDLE_RECTANGLE = new InventorySlotsGroup(
            10, 11, 12, 13, 14, 15, 16,
            19,                     25,
            28,                     34,
            37, 38, 39, 40, 41, 42, 43

    );

    public static final InventorySlotsGroup LEFT_OFFSET_CHECKBOARD = new InventorySlotsGroup(
             0, 2,  4,  6,  8,
              10, 12, 14, 16,
            18, 20, 22, 24, 26,
               28, 30, 32, 34,
            36, 38, 40, 42, 44,
               46, 48, 50, 52
    );

    public static final InventorySlotsGroup RIGHT_OFFSET_CHECKBOARD = new InventorySlotsGroup(
                1, 3, 5, 7,
            9, 11, 13, 15, 17,
              19, 21, 23, 25,
            27,29, 31, 33, 35,
              37, 39, 41, 43,
            45,47, 49, 51, 53
    );



    private final ArrayList<Integer> slots = new ArrayList<>();

    public InventorySlotsGroup(int... slots){
        for(int slot : slots){
            this.slots.add(slot);
        }
    }

    public void remove(int... slots){
        for(int slot : slots){
            this.slots.remove((Integer) slot);
        }
    }

    public void remove(InventorySlotsGroup... slots){
        for(InventorySlotsGroup slot : slots){
            this.slots.removeAll(slot.getSlots());
        }
    }

    public void add(int... slots){
        for(int slot : slots){
            this.slots.add(slot);
        }
    }

    public void add(InventorySlotsGroup... slots){
        for(InventorySlotsGroup slot : slots){
            this.slots.addAll(slot.getSlots());
        }
    }

    public void merge(int ... slots){
        for(int slot : slots){
            if(!this.slots.contains(slot)){
                this.slots.add(slot);
            }
        }
    }

    public void merge(InventorySlotsGroup... slots){
        for(InventorySlotsGroup slot : slots){
            for(int s : slot.getSlots()){
                if(!this.slots.contains(s)){
                    this.slots.add(s);
                }
            }
        }
    }

    public ArrayList<Integer> getSlots(){
        return slots;
    }
}
