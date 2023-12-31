/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LRUMap<K, V>
/*     */   implements LookupCache<K, V>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final transient int _maxEntries;
/*     */   protected final transient ConcurrentHashMap<K, V> _map;
/*     */   protected transient int _jdkSerializeMaxEntries;
/*     */   
/*     */   public LRUMap(int initialEntries, int maxEntries) {
/*  37 */     this._map = new ConcurrentHashMap<>(initialEntries, 0.8F, 4);
/*  38 */     this._maxEntries = maxEntries;
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/*  43 */     if (this._map.size() >= this._maxEntries)
/*     */     {
/*  45 */       synchronized (this) {
/*  46 */         if (this._map.size() >= this._maxEntries) {
/*  47 */           clear();
/*     */         }
/*     */       } 
/*     */     }
/*  51 */     return this._map.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V putIfAbsent(K key, V value) {
/*  61 */     if (this._map.size() >= this._maxEntries) {
/*  62 */       synchronized (this) {
/*  63 */         if (this._map.size() >= this._maxEntries) {
/*  64 */           clear();
/*     */         }
/*     */       } 
/*     */     }
/*  68 */     return this._map.putIfAbsent(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  73 */     return this._map.get(key);
/*     */   }
/*     */   public void clear() {
/*  76 */     this._map.clear();
/*     */   }
/*     */   public int size() {
/*  79 */     return this._map.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException {
/*  96 */     this._jdkSerializeMaxEntries = in.readInt();
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/* 100 */     out.writeInt(this._jdkSerializeMaxEntries);
/*     */   }
/*     */   
/*     */   protected Object readResolve() {
/* 104 */     return new LRUMap(this._jdkSerializeMaxEntries, this._jdkSerializeMaxEntries);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/LRUMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */