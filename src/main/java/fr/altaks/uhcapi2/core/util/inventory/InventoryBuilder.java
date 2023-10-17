package fr.altaks.uhcapi2.core.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public class InventoryBuilder {

    private Inventory inventory;

    public InventoryBuilder(Inventory inv, boolean deepCopy){

        if(deepCopy){
            // Copy inventory nature
            if(inv.getType() == InventoryType.CHEST){
                this.inventory = Bukkit.createInventory(inv.getHolder(), inv.getSize(), inv.getTitle());
            } else {
                this.inventory = Bukkit.createInventory(inv.getHolder(), inv.getType(), inv.getTitle());
            }

            // Copy inventory content
            for(int slotIndex = 0; slotIndex < inv.getSize(); slotIndex++){
                this.inventory.setItem(slotIndex, inv.getItem(slotIndex).clone());
            }
        } else {
            this.inventory = inv;
        }
    }

    private void checkInventorySize(int size) throws IllegalArgumentException {
        if(size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Invalid inventory size on creation : " + size + " (size has to be between 9 and 54, and be a multiple of 9");
        }
    }

    public InventoryBuilder(String title, int size, InventoryHolder holder) throws IllegalArgumentException {
        checkInventorySize(size);
        this.inventory = Bukkit.createInventory(holder, size, title);
    }

    public InventoryBuilder(String title, int size) throws IllegalArgumentException {
        checkInventorySize(size);
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public InventoryBuilder(String title, InventoryType type, InventoryHolder holder){
        this.inventory = Bukkit.createInventory(holder, type, title);
    }

    public InventoryBuilder(String title, InventoryType type){
        this.inventory = Bukkit.createInventory(null, type, title);
    }

    public InventoryBuilder(int size) throws IllegalArgumentException {
        checkInventorySize(size);
        this.inventory = Bukkit.createInventory(null, size);
    }

    public Inventory build(){
        return this.inventory;
    }

    public void addStaticItem(ItemStack item, boolean canBeMoved){

    }

    public void addInteractiveItem(ItemStack item, Consumer<InventoryClickEvent> consumer, boolean canBeMoved){

    }

    public void addRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, boolean canBeMoved){

    }

    public void addRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, Predicate<ItemStack> rotationCondition, boolean canBeMoved){

    }

    public void addSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, boolean canBeMoved){

    }

    public void addSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, Predicate<ItemStack> swapCondition, boolean canBeMoved){

    }

    public void setStaticItem(ItemStack item, boolean canBeMoved){

    }

    public void setInteractiveItem(ItemStack item, Consumer<InventoryClickEvent> consumer, boolean canBeMoved){

    }

    public void setRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, boolean canBeMoved){

    }

    public void setRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, Predicate<ItemStack> rotationCondition, boolean canBeMoved){

    }

    public void setSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, boolean canBeMoved){

    }

    public void setSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, Predicate<ItemStack> swapCondition, boolean canBeMoved){

    }

    public void dispatchStaticItem(ItemStack item, boolean canBeMoved, int... slots){

    }

    public void dispatchInteractiveItem(ItemStack item, Consumer<InventoryClickEvent> consumer, boolean canBeMoved, int... slots){

    }

    public void dispatchRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, boolean canBeMoved, int... slots){

    }

    public void dispatchRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, Predicate<ItemStack> rotationCondition, boolean canBeMoved, int... slots){

    }

    public void dispatchSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, boolean canBeMoved, int... slots){

    }

    public void dispatchSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, Predicate<ItemStack> swapCondition, boolean canBeMoved, int... slots){

    }


    public void dispatchStaticItem(ItemStack item, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    public void dispatchInteractiveItem(ItemStack item, Consumer<InventoryClickEvent> consumer, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    public void dispatchRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    public void dispatchRotatingItem(List<ItemStack> itemRotation, int rotationPeriod, Predicate<ItemStack> rotationCondition, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    public void dispatchSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    public void dispatchSwappingItem(ItemStack defaultItem, ItemStack otherItem, int swapPeriod, Predicate<ItemStack> swapCondition, boolean canBeMoved, InventorySlotsGroup... slots){

    }

    
    public int getSize() {
        return 0;
    }

    
    public int getMaxStackSize() {
        return 0;
    }

    
    public void setMaxStackSize(int i) {

    }

    
    public String getName() {
        return null;
    }

    
    public ItemStack getItem(int i) {
        return null;
    }

    
    public void setItem(int i, ItemStack itemStack) {

    }

    
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    
    public ItemStack[] getContents() {
        return new ItemStack[0];
    }

    
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {

    }

    
    public boolean contains(int i) {
        return false;
    }

    
    public boolean contains(Material material) throws IllegalArgumentException {
        return false;
    }

    
    public boolean contains(ItemStack itemStack) {
        return false;
    }


    public boolean contains(int i, int i1) {
        return false;
    }

    
    public boolean contains(Material material, int i) throws IllegalArgumentException {
        return false;
    }

    
    public boolean contains(ItemStack itemStack, int i) {
        return false;
    }

    
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return false;
    }

    
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        return null;
    }

    
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return null;
    }

    
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return null;
    }

    
    public int first(int i) {
        return 0;
    }

    
    public int first(Material material) throws IllegalArgumentException {
        return 0;
    }

    
    public int first(ItemStack itemStack) {
        return 0;
    }

    
    public int firstEmpty() {
        return 0;
    }

    
    public void remove(int i) {

    }

    
    public void remove(Material material) throws IllegalArgumentException {

    }

    
    public void remove(ItemStack itemStack) {

    }

    
    public void clear(int i) {

    }

    
    public void clear() {

    }

    
    public List<HumanEntity> getViewers() {
        return null;
    }

    
    public String getTitle() {
        return null;
    }

    
    public InventoryType getType() {
        return null;
    }

    
    public InventoryHolder getHolder() {
        return null;
    }

    
    public ListIterator<ItemStack> iterator() {
        return null;
    }

    
    public ListIterator<ItemStack> iterator(int i) {
        return null;
    }

    
    public Inventory getInventory() {
        return null;
    }
}
