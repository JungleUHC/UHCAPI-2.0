/*     */ package com.fasterxml.jackson.core.sym;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.util.InternCache;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ 
/*     */ 
/*     */ public final class CharsToNameCanonicalizer
/*     */ {
/*     */   public static final int HASH_MULT = 33;
/*     */   private static final int DEFAULT_T_SIZE = 64;
/*     */   private static final int MAX_T_SIZE = 65536;
/*     */   static final int MAX_ENTRIES_FOR_REUSE = 12000;
/*     */   static final int MAX_COLL_CHAIN_LENGTH = 150;
/*     */   protected final CharsToNameCanonicalizer _parent;
/*     */   protected final AtomicReference<TableInfo> _tableInfo;
/*     */   protected final int _seed;
/*     */   protected final int _flags;
/*     */   protected boolean _canonicalize;
/*     */   protected String[] _symbols;
/*     */   protected Bucket[] _buckets;
/*     */   protected int _size;
/*     */   protected int _sizeThreshold;
/*     */   protected int _indexMask;
/*     */   protected int _longestCollisionList;
/*     */   protected boolean _hashShared;
/*     */   protected BitSet _overflows;
/*     */   
/*     */   private CharsToNameCanonicalizer(int seed) {
/* 232 */     this._parent = null;
/* 233 */     this._seed = seed;
/*     */ 
/*     */     
/* 236 */     this._canonicalize = true;
/* 237 */     this._flags = -1;
/*     */     
/* 239 */     this._hashShared = false;
/* 240 */     this._longestCollisionList = 0;
/*     */     
/* 242 */     this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(64));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, TableInfo parentState) {
/* 253 */     this._parent = parent;
/* 254 */     this._seed = seed;
/* 255 */     this._tableInfo = null;
/* 256 */     this._flags = flags;
/* 257 */     this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
/*     */ 
/*     */     
/* 260 */     this._symbols = parentState.symbols;
/* 261 */     this._buckets = parentState.buckets;
/*     */     
/* 263 */     this._size = parentState.size;
/* 264 */     this._longestCollisionList = parentState.longestCollisionList;
/*     */ 
/*     */     
/* 267 */     int arrayLen = this._symbols.length;
/* 268 */     this._sizeThreshold = _thresholdSize(arrayLen);
/* 269 */     this._indexMask = arrayLen - 1;
/*     */ 
/*     */     
/* 272 */     this._hashShared = true;
/*     */   }
/*     */   private static int _thresholdSize(int hashAreaSize) {
/* 275 */     return hashAreaSize - (hashAreaSize >> 2);
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
/*     */ 
/*     */   
/*     */   public static CharsToNameCanonicalizer createRoot() {
/* 294 */     long now = System.currentTimeMillis();
/*     */     
/* 296 */     int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 297 */     return createRoot(seed);
/*     */   }
/*     */   
/*     */   protected static CharsToNameCanonicalizer createRoot(int seed) {
/* 301 */     return new CharsToNameCanonicalizer(seed);
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
/*     */ 
/*     */   
/*     */   public CharsToNameCanonicalizer makeChild(int flags) {
/* 320 */     return new CharsToNameCanonicalizer(this, flags, this._seed, this._tableInfo.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 331 */     if (!maybeDirty()) {
/*     */       return;
/*     */     }
/* 334 */     if (this._parent != null && this._canonicalize) {
/* 335 */       this._parent.mergeChild(new TableInfo(this));
/*     */ 
/*     */       
/* 338 */       this._hashShared = true;
/*     */     } 
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
/*     */   private void mergeChild(TableInfo childState) {
/* 351 */     int childCount = childState.size;
/* 352 */     TableInfo currState = this._tableInfo.get();
/*     */ 
/*     */ 
/*     */     
/* 356 */     if (childCount == currState.size) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 363 */     if (childCount > 12000)
/*     */     {
/* 365 */       childState = TableInfo.createInitial(64);
/*     */     }
/* 367 */     this._tableInfo.compareAndSet(currState, childState);
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
/*     */   public int size() {
/* 380 */     if (this._tableInfo != null) {
/* 381 */       return ((TableInfo)this._tableInfo.get()).size;
/*     */     }
/*     */     
/* 384 */     return this._size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int bucketCount() {
/* 393 */     return this._symbols.length;
/* 394 */   } public boolean maybeDirty() { return !this._hashShared; } public int hashSeed() {
/* 395 */     return this._seed;
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
/*     */   public int collisionCount() {
/* 407 */     int count = 0;
/*     */     
/* 409 */     for (Bucket bucket : this._buckets) {
/* 410 */       if (bucket != null) {
/* 411 */         count += bucket.length;
/*     */       }
/*     */     } 
/* 414 */     return count;
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
/*     */   public int maxCollisionLength() {
/* 426 */     return this._longestCollisionList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String findSymbol(char[] buffer, int start, int len, int h) {
/* 436 */     if (len < 1) {
/* 437 */       return "";
/*     */     }
/* 439 */     if (!this._canonicalize) {
/* 440 */       return new String(buffer, start, len);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 448 */     int index = _hashToIndex(h);
/* 449 */     String sym = this._symbols[index];
/*     */ 
/*     */     
/* 452 */     if (sym != null) {
/*     */       
/* 454 */       if (sym.length() == len) {
/* 455 */         int i = 0;
/* 456 */         while (sym.charAt(i) == buffer[start + i]) {
/*     */           
/* 458 */           if (++i == len) {
/* 459 */             return sym;
/*     */           }
/*     */         } 
/*     */       } 
/* 463 */       Bucket b = this._buckets[index >> 1];
/* 464 */       if (b != null) {
/* 465 */         sym = b.has(buffer, start, len);
/* 466 */         if (sym != null) {
/* 467 */           return sym;
/*     */         }
/* 469 */         sym = _findSymbol2(buffer, start, len, b.next);
/* 470 */         if (sym != null) {
/* 471 */           return sym;
/*     */         }
/*     */       } 
/*     */     } 
/* 475 */     return _addSymbol(buffer, start, len, h, index);
/*     */   }
/*     */   
/*     */   private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
/* 479 */     while (b != null) {
/* 480 */       String sym = b.has(buffer, start, len);
/* 481 */       if (sym != null) {
/* 482 */         return sym;
/*     */       }
/* 484 */       b = b.next;
/*     */     } 
/* 486 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private String _addSymbol(char[] buffer, int start, int len, int h, int index) {
/* 491 */     if (this._hashShared) {
/* 492 */       copyArrays();
/* 493 */       this._hashShared = false;
/* 494 */     } else if (this._size >= this._sizeThreshold) {
/* 495 */       rehash();
/*     */ 
/*     */       
/* 498 */       index = _hashToIndex(calcHash(buffer, start, len));
/*     */     } 
/*     */     
/* 501 */     String newSymbol = new String(buffer, start, len);
/* 502 */     if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
/* 503 */       newSymbol = InternCache.instance.intern(newSymbol);
/*     */     }
/* 505 */     this._size++;
/*     */     
/* 507 */     if (this._symbols[index] == null) {
/* 508 */       this._symbols[index] = newSymbol;
/*     */     } else {
/* 510 */       int bix = index >> 1;
/* 511 */       Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
/* 512 */       int collLen = newB.length;
/* 513 */       if (collLen > 150) {
/*     */ 
/*     */         
/* 516 */         _handleSpillOverflow(bix, newB, index);
/*     */       } else {
/* 518 */         this._buckets[bix] = newB;
/* 519 */         this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
/*     */       } 
/*     */     } 
/* 522 */     return newSymbol;
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
/*     */   private void _handleSpillOverflow(int bucketIndex, Bucket newBucket, int mainIndex) {
/* 534 */     if (this._overflows == null) {
/* 535 */       this._overflows = new BitSet();
/* 536 */       this._overflows.set(bucketIndex);
/*     */     }
/* 538 */     else if (this._overflows.get(bucketIndex)) {
/*     */       
/* 540 */       if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
/* 541 */         _reportTooManyCollisions(150);
/*     */       }
/*     */ 
/*     */       
/* 545 */       this._canonicalize = false;
/*     */     } else {
/* 547 */       this._overflows.set(bucketIndex);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 552 */     this._symbols[mainIndex] = newBucket.symbol;
/* 553 */     this._buckets[bucketIndex] = null;
/*     */     
/* 555 */     this._size -= newBucket.length;
/*     */     
/* 557 */     this._longestCollisionList = -1;
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
/*     */   public int _hashToIndex(int rawHash) {
/* 570 */     rawHash += rawHash >>> 15;
/* 571 */     rawHash ^= rawHash << 7;
/* 572 */     rawHash += rawHash >>> 3;
/* 573 */     return rawHash & this._indexMask;
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
/*     */   public int calcHash(char[] buffer, int start, int len) {
/* 589 */     int hash = this._seed;
/* 590 */     for (int i = start, end = start + len; i < end; i++) {
/* 591 */       hash = hash * 33 + buffer[i];
/*     */     }
/*     */     
/* 594 */     return (hash == 0) ? 1 : hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public int calcHash(String key) {
/* 599 */     int len = key.length();
/*     */     
/* 601 */     int hash = this._seed;
/* 602 */     for (int i = 0; i < len; i++) {
/* 603 */       hash = hash * 33 + key.charAt(i);
/*     */     }
/*     */     
/* 606 */     return (hash == 0) ? 1 : hash;
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
/*     */   private void copyArrays() {
/* 620 */     String[] oldSyms = this._symbols;
/* 621 */     this._symbols = Arrays.<String>copyOf(oldSyms, oldSyms.length);
/* 622 */     Bucket[] oldBuckets = this._buckets;
/* 623 */     this._buckets = Arrays.<Bucket>copyOf(oldBuckets, oldBuckets.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void rehash() {
/* 634 */     int size = this._symbols.length;
/* 635 */     int newSize = size + size;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 641 */     if (newSize > 65536) {
/*     */ 
/*     */       
/* 644 */       this._size = 0;
/* 645 */       this._canonicalize = false;
/*     */       
/* 647 */       this._symbols = new String[64];
/* 648 */       this._buckets = new Bucket[32];
/* 649 */       this._indexMask = 63;
/* 650 */       this._hashShared = false;
/*     */       
/*     */       return;
/*     */     } 
/* 654 */     String[] oldSyms = this._symbols;
/* 655 */     Bucket[] oldBuckets = this._buckets;
/* 656 */     this._symbols = new String[newSize];
/* 657 */     this._buckets = new Bucket[newSize >> 1];
/*     */     
/* 659 */     this._indexMask = newSize - 1;
/* 660 */     this._sizeThreshold = _thresholdSize(newSize);
/*     */     
/* 662 */     int count = 0;
/*     */ 
/*     */ 
/*     */     
/* 666 */     int maxColl = 0;
/* 667 */     for (int i = 0; i < size; i++) {
/* 668 */       String symbol = oldSyms[i];
/* 669 */       if (symbol != null) {
/* 670 */         count++;
/* 671 */         int index = _hashToIndex(calcHash(symbol));
/* 672 */         if (this._symbols[index] == null) {
/* 673 */           this._symbols[index] = symbol;
/*     */         } else {
/* 675 */           int bix = index >> 1;
/* 676 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 677 */           this._buckets[bix] = newB;
/* 678 */           maxColl = Math.max(maxColl, newB.length);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 683 */     int bucketSize = size >> 1;
/* 684 */     for (int j = 0; j < bucketSize; j++) {
/* 685 */       Bucket b = oldBuckets[j];
/* 686 */       while (b != null) {
/* 687 */         count++;
/* 688 */         String symbol = b.symbol;
/* 689 */         int index = _hashToIndex(calcHash(symbol));
/* 690 */         if (this._symbols[index] == null) {
/* 691 */           this._symbols[index] = symbol;
/*     */         } else {
/* 693 */           int bix = index >> 1;
/* 694 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 695 */           this._buckets[bix] = newB;
/* 696 */           maxColl = Math.max(maxColl, newB.length);
/*     */         } 
/* 698 */         b = b.next;
/*     */       } 
/*     */     } 
/* 701 */     this._longestCollisionList = maxColl;
/* 702 */     this._overflows = null;
/*     */     
/* 704 */     if (count != this._size) {
/* 705 */       throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", new Object[] {
/*     */               
/* 707 */               Integer.valueOf(this._size), Integer.valueOf(count)
/*     */             }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportTooManyCollisions(int maxLen) {
/* 717 */     throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
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
/*     */   protected void verifyInternalConsistency() {
/* 729 */     int count = 0;
/* 730 */     int size = this._symbols.length;
/*     */     
/* 732 */     for (int i = 0; i < size; i++) {
/* 733 */       String symbol = this._symbols[i];
/* 734 */       if (symbol != null) {
/* 735 */         count++;
/*     */       }
/*     */     } 
/*     */     
/* 739 */     int bucketSize = size >> 1;
/* 740 */     for (int j = 0; j < bucketSize; j++) {
/* 741 */       for (Bucket b = this._buckets[j]; b != null; b = b.next) {
/* 742 */         count++;
/*     */       }
/*     */     } 
/* 745 */     if (count != this._size) {
/* 746 */       throw new IllegalStateException(String.format("Internal error: expected internal size %d vs calculated count %d", new Object[] {
/* 747 */               Integer.valueOf(this._size), Integer.valueOf(count)
/*     */             }));
/*     */     }
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
/*     */ 
/*     */   
/*     */   static final class Bucket
/*     */   {
/*     */     public final String symbol;
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
/*     */     public final Bucket next;
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
/*     */     public final int length;
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
/*     */     public Bucket(String s, Bucket n) {
/* 817 */       this.symbol = s;
/* 818 */       this.next = n;
/* 819 */       this.length = (n == null) ? 1 : (n.length + 1);
/*     */     }
/*     */     
/*     */     public String has(char[] buf, int start, int len) {
/* 823 */       if (this.symbol.length() != len) {
/* 824 */         return null;
/*     */       }
/* 826 */       int i = 0;
/*     */       while (true) {
/* 828 */         if (this.symbol.charAt(i) != buf[start + i]) {
/* 829 */           return null;
/*     */         }
/* 831 */         if (++i >= len) {
/* 832 */           return this.symbol;
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class TableInfo
/*     */   {
/*     */     final int size;
/*     */ 
/*     */     
/*     */     final int longestCollisionList;
/*     */     
/*     */     final String[] symbols;
/*     */     
/*     */     final CharsToNameCanonicalizer.Bucket[] buckets;
/*     */ 
/*     */     
/*     */     public TableInfo(int size, int longestCollisionList, String[] symbols, CharsToNameCanonicalizer.Bucket[] buckets) {
/* 853 */       this.size = size;
/* 854 */       this.longestCollisionList = longestCollisionList;
/* 855 */       this.symbols = symbols;
/* 856 */       this.buckets = buckets;
/*     */     }
/*     */ 
/*     */     
/*     */     public TableInfo(CharsToNameCanonicalizer src) {
/* 861 */       this.size = src._size;
/* 862 */       this.longestCollisionList = src._longestCollisionList;
/* 863 */       this.symbols = src._symbols;
/* 864 */       this.buckets = src._buckets;
/*     */     }
/*     */     
/*     */     public static TableInfo createInitial(int sz) {
/* 868 */       return new TableInfo(0, 0, new String[sz], new CharsToNameCanonicalizer.Bucket[sz >> 1]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/sym/CharsToNameCanonicalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */