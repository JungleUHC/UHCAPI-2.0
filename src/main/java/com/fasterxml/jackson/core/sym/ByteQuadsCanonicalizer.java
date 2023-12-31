/*      */ package com.fasterxml.jackson.core.sym;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.util.InternCache;
/*      */ import java.util.Arrays;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ByteQuadsCanonicalizer
/*      */ {
/*      */   private static final int DEFAULT_T_SIZE = 64;
/*      */   private static final int MAX_T_SIZE = 65536;
/*      */   private static final int MIN_HASH_SIZE = 16;
/*      */   protected static final int MAX_ENTRIES_FOR_REUSE = 6000;
/*      */   protected final ByteQuadsCanonicalizer _parent;
/*      */   protected final AtomicReference<TableInfo> _tableInfo;
/*      */   protected final int _seed;
/*      */   protected final boolean _intern;
/*      */   protected final boolean _failOnDoS;
/*      */   protected int[] _hashArea;
/*      */   protected int _hashSize;
/*      */   protected int _secondaryStart;
/*      */   protected int _tertiaryStart;
/*      */   protected int _tertiaryShift;
/*      */   protected int _count;
/*      */   protected String[] _names;
/*      */   protected int _spilloverEnd;
/*      */   protected int _longNameOffset;
/*      */   protected boolean _hashShared;
/*      */   private static final int MULT = 33;
/*      */   private static final int MULT2 = 65599;
/*      */   private static final int MULT3 = 31;
/*      */   
/*      */   private ByteQuadsCanonicalizer(int sz, int seed) {
/*  229 */     this._parent = null;
/*  230 */     this._count = 0;
/*      */ 
/*      */     
/*  233 */     this._hashShared = true;
/*  234 */     this._seed = seed;
/*  235 */     this._intern = false;
/*  236 */     this._failOnDoS = true;
/*      */     
/*  238 */     if (sz < 16) {
/*  239 */       sz = 16;
/*      */ 
/*      */     
/*      */     }
/*  243 */     else if ((sz & sz - 1) != 0) {
/*  244 */       int curr = 16;
/*  245 */       while (curr < sz) {
/*  246 */         curr += curr;
/*      */       }
/*  248 */       sz = curr;
/*      */     } 
/*      */     
/*  251 */     this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(sz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, int seed, TableInfo state, boolean intern, boolean failOnDoS) {
/*  261 */     this._parent = parent;
/*  262 */     this._seed = seed;
/*  263 */     this._intern = intern;
/*  264 */     this._failOnDoS = failOnDoS;
/*  265 */     this._tableInfo = null;
/*      */ 
/*      */     
/*  268 */     this._count = state.count;
/*  269 */     this._hashSize = state.size;
/*  270 */     this._secondaryStart = this._hashSize << 2;
/*  271 */     this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/*  272 */     this._tertiaryShift = state.tertiaryShift;
/*      */     
/*  274 */     this._hashArea = state.mainHash;
/*  275 */     this._names = state.names;
/*      */     
/*  277 */     this._spilloverEnd = state.spilloverEnd;
/*  278 */     this._longNameOffset = state.longNameOffset;
/*      */ 
/*      */     
/*  281 */     this._hashShared = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteQuadsCanonicalizer(TableInfo state) {
/*  295 */     this._parent = null;
/*  296 */     this._seed = 0;
/*  297 */     this._intern = false;
/*  298 */     this._failOnDoS = true;
/*  299 */     this._tableInfo = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  305 */     this._count = -1;
/*      */     
/*  307 */     this._hashArea = state.mainHash;
/*  308 */     this._names = state.names;
/*      */     
/*  310 */     this._hashSize = state.size;
/*      */ 
/*      */ 
/*      */     
/*  314 */     int end = this._hashArea.length;
/*  315 */     this._secondaryStart = end;
/*  316 */     this._tertiaryStart = end;
/*  317 */     this._tertiaryShift = 1;
/*      */     
/*  319 */     this._spilloverEnd = end;
/*  320 */     this._longNameOffset = end;
/*      */ 
/*      */     
/*  323 */     this._hashShared = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteQuadsCanonicalizer createRoot() {
/*  341 */     long now = System.currentTimeMillis();
/*      */     
/*  343 */     int seed = (int)now + (int)(now >>> 32L) | 0x1;
/*  344 */     return createRoot(seed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ByteQuadsCanonicalizer createRoot(int seed) {
/*  350 */     return new ByteQuadsCanonicalizer(64, seed);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteQuadsCanonicalizer makeChild(int flags) {
/*  362 */     return new ByteQuadsCanonicalizer(this, this._seed, this._tableInfo
/*  363 */         .get(), JsonFactory.Feature.INTERN_FIELD_NAMES
/*  364 */         .enabledIn(flags), JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW
/*  365 */         .enabledIn(flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteQuadsCanonicalizer makeChildOrPlaceholder(int flags) {
/*  381 */     if (JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags))
/*      */     {
/*  383 */       return new ByteQuadsCanonicalizer(this, this._seed, this._tableInfo
/*  384 */           .get(), JsonFactory.Feature.INTERN_FIELD_NAMES
/*  385 */           .enabledIn(flags), JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW
/*  386 */           .enabledIn(flags));
/*      */     }
/*  388 */     return new ByteQuadsCanonicalizer(this._tableInfo.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void release() {
/*  401 */     if (this._parent != null && maybeDirty()) {
/*  402 */       this._parent.mergeChild(new TableInfo(this));
/*      */ 
/*      */       
/*  405 */       this._hashShared = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeChild(TableInfo childState) {
/*  411 */     int childCount = childState.count;
/*  412 */     TableInfo currState = this._tableInfo.get();
/*      */ 
/*      */ 
/*      */     
/*  416 */     if (childCount == currState.count) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  424 */     if (childCount > 6000)
/*      */     {
/*  426 */       childState = TableInfo.createInitial(64);
/*      */     }
/*  428 */     this._tableInfo.compareAndSet(currState, childState);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/*  442 */     if (this._tableInfo != null) {
/*  443 */       return ((TableInfo)this._tableInfo.get()).count;
/*      */     }
/*      */     
/*  446 */     return this._count;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int bucketCount() {
/*  452 */     return this._hashSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean maybeDirty() {
/*  461 */     return !this._hashShared;
/*      */   } public int hashSeed() {
/*  463 */     return this._seed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCanonicalizing() {
/*  473 */     return (this._parent != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int primaryCount() {
/*  485 */     int count = 0;
/*  486 */     for (int offset = 3, end = this._secondaryStart; offset < end; offset += 4) {
/*  487 */       if (this._hashArea[offset] != 0) {
/*  488 */         count++;
/*      */       }
/*      */     } 
/*  491 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int secondaryCount() {
/*  501 */     int count = 0;
/*  502 */     int offset = this._secondaryStart + 3;
/*  503 */     for (int end = this._tertiaryStart; offset < end; offset += 4) {
/*  504 */       if (this._hashArea[offset] != 0) {
/*  505 */         count++;
/*      */       }
/*      */     } 
/*  508 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int tertiaryCount() {
/*  518 */     int count = 0;
/*  519 */     int offset = this._tertiaryStart + 3;
/*  520 */     for (int end = offset + this._hashSize; offset < end; offset += 4) {
/*  521 */       if (this._hashArea[offset] != 0) {
/*  522 */         count++;
/*      */       }
/*      */     } 
/*  525 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int spilloverCount() {
/*  536 */     return this._spilloverEnd - _spilloverStart() >> 2;
/*      */   }
/*      */ 
/*      */   
/*      */   public int totalCount() {
/*  541 */     int count = 0;
/*  542 */     for (int offset = 3, end = this._hashSize << 3; offset < end; offset += 4) {
/*  543 */       if (this._hashArea[offset] != 0) {
/*  544 */         count++;
/*      */       }
/*      */     } 
/*  547 */     return count;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/*  552 */     int pri = primaryCount();
/*  553 */     int sec = secondaryCount();
/*  554 */     int tert = tertiaryCount();
/*  555 */     int spill = spilloverCount();
/*  556 */     int total = totalCount();
/*  557 */     return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[] {
/*  558 */           getClass().getName(), Integer.valueOf(this._count), Integer.valueOf(this._hashSize), 
/*  559 */           Integer.valueOf(pri), Integer.valueOf(sec), Integer.valueOf(tert), Integer.valueOf(spill), Integer.valueOf(pri + sec + tert + spill), Integer.valueOf(total)
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String findName(int q1) {
/*  570 */     int offset = _calcOffset(calcHash(q1));
/*      */     
/*  572 */     int[] hashArea = this._hashArea;
/*      */     
/*  574 */     int len = hashArea[offset + 3];
/*      */     
/*  576 */     if (len == 1) {
/*  577 */       if (hashArea[offset] == q1) {
/*  578 */         return this._names[offset >> 2];
/*      */       }
/*  580 */     } else if (len == 0) {
/*  581 */       return null;
/*      */     } 
/*      */     
/*  584 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  586 */     len = hashArea[offset2 + 3];
/*      */     
/*  588 */     if (len == 1) {
/*  589 */       if (hashArea[offset2] == q1) {
/*  590 */         return this._names[offset2 >> 2];
/*      */       }
/*  592 */     } else if (len == 0) {
/*  593 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  597 */     return _findSecondary(offset, q1);
/*      */   }
/*      */ 
/*      */   
/*      */   public String findName(int q1, int q2) {
/*  602 */     int offset = _calcOffset(calcHash(q1, q2));
/*      */     
/*  604 */     int[] hashArea = this._hashArea;
/*      */     
/*  606 */     int len = hashArea[offset + 3];
/*      */     
/*  608 */     if (len == 2) {
/*  609 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
/*  610 */         return this._names[offset >> 2];
/*      */       }
/*  612 */     } else if (len == 0) {
/*  613 */       return null;
/*      */     } 
/*      */     
/*  616 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  618 */     len = hashArea[offset2 + 3];
/*      */     
/*  620 */     if (len == 2) {
/*  621 */       if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
/*  622 */         return this._names[offset2 >> 2];
/*      */       }
/*  624 */     } else if (len == 0) {
/*  625 */       return null;
/*      */     } 
/*  627 */     return _findSecondary(offset, q1, q2);
/*      */   }
/*      */ 
/*      */   
/*      */   public String findName(int q1, int q2, int q3) {
/*  632 */     int offset = _calcOffset(calcHash(q1, q2, q3));
/*  633 */     int[] hashArea = this._hashArea;
/*  634 */     int len = hashArea[offset + 3];
/*      */     
/*  636 */     if (len == 3) {
/*  637 */       if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
/*  638 */         return this._names[offset >> 2];
/*      */       }
/*  640 */     } else if (len == 0) {
/*  641 */       return null;
/*      */     } 
/*      */     
/*  644 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  646 */     len = hashArea[offset2 + 3];
/*      */     
/*  648 */     if (len == 3) {
/*  649 */       if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
/*  650 */         return this._names[offset2 >> 2];
/*      */       }
/*  652 */     } else if (len == 0) {
/*  653 */       return null;
/*      */     } 
/*  655 */     return _findSecondary(offset, q1, q2, q3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String findName(int[] q, int qlen) {
/*  664 */     if (qlen < 4) {
/*  665 */       switch (qlen) {
/*      */         case 3:
/*  667 */           return findName(q[0], q[1], q[2]);
/*      */         case 2:
/*  669 */           return findName(q[0], q[1]);
/*      */         case 1:
/*  671 */           return findName(q[0]);
/*      */       } 
/*  673 */       return "";
/*      */     } 
/*      */     
/*  676 */     int hash = calcHash(q, qlen);
/*  677 */     int offset = _calcOffset(hash);
/*      */     
/*  679 */     int[] hashArea = this._hashArea;
/*      */     
/*  681 */     int len = hashArea[offset + 3];
/*      */     
/*  683 */     if (hash == hashArea[offset] && len == qlen)
/*      */     {
/*  685 */       if (_verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  686 */         return this._names[offset >> 2];
/*      */       }
/*      */     }
/*  689 */     if (len == 0) {
/*  690 */       return null;
/*      */     }
/*      */     
/*  693 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  695 */     int len2 = hashArea[offset2 + 3];
/*  696 */     if (hash == hashArea[offset2] && len2 == qlen && 
/*  697 */       _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
/*  698 */       return this._names[offset2 >> 2];
/*      */     }
/*      */     
/*  701 */     return _findSecondary(offset, hash, q, qlen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _calcOffset(int hash) {
/*  709 */     int ix = hash & this._hashSize - 1;
/*      */     
/*  711 */     return ix << 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1) {
/*  726 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  727 */     int[] hashArea = this._hashArea;
/*  728 */     int bucketSize = 1 << this._tertiaryShift;
/*  729 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  730 */       int len = hashArea[offset + 3];
/*  731 */       if (q1 == hashArea[offset] && 1 == len) {
/*  732 */         return this._names[offset >> 2];
/*      */       }
/*  734 */       if (len == 0) {
/*  735 */         return null;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  741 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  742 */       if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
/*  743 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  746 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2) {
/*  751 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  752 */     int[] hashArea = this._hashArea;
/*      */     
/*  754 */     int bucketSize = 1 << this._tertiaryShift;
/*  755 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  756 */       int len = hashArea[offset + 3];
/*  757 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
/*  758 */         return this._names[offset >> 2];
/*      */       }
/*  760 */       if (len == 0) {
/*  761 */         return null;
/*      */       }
/*      */     } 
/*  764 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  765 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
/*  766 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  769 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2, int q3) {
/*  774 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  775 */     int[] hashArea = this._hashArea;
/*      */     
/*  777 */     int bucketSize = 1 << this._tertiaryShift;
/*  778 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  779 */       int len = hashArea[offset + 3];
/*  780 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
/*  781 */         return this._names[offset >> 2];
/*      */       }
/*  783 */       if (len == 0) {
/*  784 */         return null;
/*      */       }
/*      */     } 
/*  787 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  788 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3])
/*      */       {
/*  790 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  793 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
/*  798 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  799 */     int[] hashArea = this._hashArea;
/*      */     
/*  801 */     int bucketSize = 1 << this._tertiaryShift;
/*  802 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  803 */       int len = hashArea[offset + 3];
/*  804 */       if (hash == hashArea[offset] && qlen == len && 
/*  805 */         _verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  806 */         return this._names[offset >> 2];
/*      */       }
/*      */       
/*  809 */       if (len == 0) {
/*  810 */         return null;
/*      */       }
/*      */     } 
/*  813 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  814 */       if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && 
/*  815 */         _verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  816 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*      */     
/*  820 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _verifyLongName(int[] q, int qlen, int spillOffset) {
/*  825 */     int[] hashArea = this._hashArea;
/*      */     
/*  827 */     int ix = 0;
/*      */     
/*  829 */     switch (qlen)
/*      */     { default:
/*  831 */         return _verifyLongName2(q, qlen, spillOffset);
/*      */       case 8:
/*  833 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 7:
/*  835 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 6:
/*  837 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 5:
/*  839 */         if (q[ix++] != hashArea[spillOffset++]) return false;  break;
/*      */       case 4:
/*  841 */         break; }  if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  842 */     if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  843 */     if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  844 */     if (q[ix++] != hashArea[spillOffset++]) return false;
/*      */     
/*  846 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
/*  851 */     int ix = 0;
/*      */     while (true) {
/*  853 */       if (q[ix++] != this._hashArea[spillOffset++]) {
/*  854 */         return false;
/*      */       }
/*  856 */       if (ix >= qlen) {
/*  857 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String addName(String name, int q1) {
/*  867 */     _verifySharing();
/*  868 */     if (this._intern) {
/*  869 */       name = InternCache.instance.intern(name);
/*      */     }
/*  871 */     int offset = _findOffsetForAdd(calcHash(q1));
/*  872 */     this._hashArea[offset] = q1;
/*  873 */     this._hashArea[offset + 3] = 1;
/*  874 */     this._names[offset >> 2] = name;
/*  875 */     this._count++;
/*  876 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2) {
/*  880 */     _verifySharing();
/*  881 */     if (this._intern) {
/*  882 */       name = InternCache.instance.intern(name);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  889 */     int hash = calcHash(q1, q2);
/*  890 */     int offset = _findOffsetForAdd(hash);
/*  891 */     this._hashArea[offset] = q1;
/*  892 */     this._hashArea[offset + 1] = q2;
/*  893 */     this._hashArea[offset + 3] = 2;
/*  894 */     this._names[offset >> 2] = name;
/*  895 */     this._count++;
/*  896 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2, int q3) {
/*  900 */     _verifySharing();
/*  901 */     if (this._intern) {
/*  902 */       name = InternCache.instance.intern(name);
/*      */     }
/*  904 */     int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/*  905 */     this._hashArea[offset] = q1;
/*  906 */     this._hashArea[offset + 1] = q2;
/*  907 */     this._hashArea[offset + 2] = q3;
/*  908 */     this._hashArea[offset + 3] = 3;
/*  909 */     this._names[offset >> 2] = name;
/*  910 */     this._count++;
/*  911 */     return name;
/*      */   }
/*      */ 
/*      */   
/*      */   public String addName(String name, int[] q, int qlen) {
/*  916 */     _verifySharing();
/*  917 */     if (this._intern) {
/*  918 */       name = InternCache.instance.intern(name);
/*      */     }
/*      */ 
/*      */     
/*  922 */     switch (qlen)
/*      */     
/*      */     { case 1:
/*  925 */         offset = _findOffsetForAdd(calcHash(q[0]));
/*  926 */         this._hashArea[offset] = q[0];
/*  927 */         this._hashArea[offset + 3] = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  957 */         this._names[offset >> 2] = name;
/*      */ 
/*      */         
/*  960 */         this._count++;
/*  961 */         return name;case 2: offset = _findOffsetForAdd(calcHash(q[0], q[1])); this._hashArea[offset] = q[0]; this._hashArea[offset + 1] = q[1]; this._hashArea[offset + 3] = 2; this._names[offset >> 2] = name; this._count++; return name;case 3: offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2])); this._hashArea[offset] = q[0]; this._hashArea[offset + 1] = q[1]; this._hashArea[offset + 2] = q[2]; this._hashArea[offset + 3] = 3; this._names[offset >> 2] = name; this._count++; return name; }  int hash = calcHash(q, qlen); int offset = _findOffsetForAdd(hash); this._hashArea[offset] = hash; int longStart = _appendLongName(q, qlen); this._hashArea[offset + 1] = longStart; this._hashArea[offset + 3] = qlen; this._names[offset >> 2] = name; this._count++; return name;
/*      */   }
/*      */ 
/*      */   
/*      */   private void _verifySharing() {
/*  966 */     if (this._hashShared) {
/*      */ 
/*      */       
/*  969 */       if (this._parent == null) {
/*  970 */         if (this._count == 0) {
/*  971 */           throw new IllegalStateException("Cannot add names to Root symbol table");
/*      */         }
/*  973 */         throw new IllegalStateException("Cannot add names to Placeholder symbol table");
/*      */       } 
/*      */       
/*  976 */       this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/*  977 */       this._names = Arrays.<String>copyOf(this._names, this._names.length);
/*  978 */       this._hashShared = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _findOffsetForAdd(int hash) {
/*  988 */     int offset = _calcOffset(hash);
/*  989 */     int[] hashArea = this._hashArea;
/*  990 */     if (hashArea[offset + 3] == 0)
/*      */     {
/*  992 */       return offset;
/*      */     }
/*      */ 
/*      */     
/*  996 */     if (_checkNeedForRehash()) {
/*  997 */       return _resizeAndFindOffsetForAdd(hash);
/*      */     }
/*      */ 
/*      */     
/* 1001 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 1002 */     if (hashArea[offset2 + 3] == 0)
/*      */     {
/* 1004 */       return offset2;
/*      */     }
/*      */ 
/*      */     
/* 1008 */     offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 1009 */     int bucketSize = 1 << this._tertiaryShift; int end;
/* 1010 */     for (end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/* 1011 */       if (hashArea[offset2 + 3] == 0)
/*      */       {
/* 1013 */         return offset2;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1018 */     offset = this._spilloverEnd;
/* 1019 */     this._spilloverEnd += 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1029 */     end = this._hashSize << 3;
/* 1030 */     if (this._spilloverEnd >= end) {
/* 1031 */       if (this._failOnDoS) {
/* 1032 */         _reportTooManyCollisions();
/*      */       }
/* 1034 */       return _resizeAndFindOffsetForAdd(hash);
/*      */     } 
/* 1036 */     return offset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _resizeAndFindOffsetForAdd(int hash) {
/* 1043 */     rehash();
/*      */ 
/*      */     
/* 1046 */     int offset = _calcOffset(hash);
/* 1047 */     int[] hashArea = this._hashArea;
/* 1048 */     if (hashArea[offset + 3] == 0) {
/* 1049 */       return offset;
/*      */     }
/* 1051 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 1052 */     if (hashArea[offset2 + 3] == 0) {
/* 1053 */       return offset2;
/*      */     }
/* 1055 */     offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 1056 */     int bucketSize = 1 << this._tertiaryShift;
/* 1057 */     for (int end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/* 1058 */       if (hashArea[offset2 + 3] == 0) {
/* 1059 */         return offset2;
/*      */       }
/*      */     } 
/* 1062 */     offset = this._spilloverEnd;
/* 1063 */     this._spilloverEnd += 4;
/* 1064 */     return offset;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean _checkNeedForRehash() {
/* 1070 */     if (this._count > this._hashSize >> 1) {
/* 1071 */       int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/* 1072 */       if (spillCount > 1 + this._count >> 7 || this._count > this._hashSize * 0.8D)
/*      */       {
/* 1074 */         return true;
/*      */       }
/*      */     } 
/* 1077 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private int _appendLongName(int[] quads, int qlen) {
/* 1082 */     int start = this._longNameOffset;
/*      */ 
/*      */     
/* 1085 */     if (start + qlen > this._hashArea.length) {
/*      */       
/* 1087 */       int toAdd = start + qlen - this._hashArea.length;
/*      */       
/* 1089 */       int minAdd = Math.min(4096, this._hashSize);
/*      */       
/* 1091 */       int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/* 1092 */       this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*      */     } 
/* 1094 */     System.arraycopy(quads, 0, this._hashArea, start, qlen);
/* 1095 */     this._longNameOffset += qlen;
/* 1096 */     return start;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int calcHash(int q1) {
/* 1121 */     int hash = q1 ^ this._seed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1127 */     hash += hash >>> 16;
/* 1128 */     hash ^= hash << 3;
/* 1129 */     hash += hash >>> 12;
/* 1130 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int calcHash(int q1, int q2) {
/* 1137 */     int hash = q1;
/*      */     
/* 1139 */     hash += hash >>> 15;
/* 1140 */     hash ^= hash >>> 9;
/* 1141 */     hash += q2 * 33;
/* 1142 */     hash ^= this._seed;
/* 1143 */     hash += hash >>> 16;
/* 1144 */     hash ^= hash >>> 4;
/* 1145 */     hash += hash << 3;
/*      */     
/* 1147 */     return hash;
/*      */   }
/*      */ 
/*      */   
/*      */   public int calcHash(int q1, int q2, int q3) {
/* 1152 */     int hash = q1 ^ this._seed;
/* 1153 */     hash += hash >>> 9;
/* 1154 */     hash *= 31;
/* 1155 */     hash += q2;
/* 1156 */     hash *= 33;
/* 1157 */     hash += hash >>> 15;
/* 1158 */     hash ^= q3;
/*      */     
/* 1160 */     hash += hash >>> 4;
/*      */     
/* 1162 */     hash += hash >>> 15;
/* 1163 */     hash ^= hash << 9;
/*      */     
/* 1165 */     return hash;
/*      */   }
/*      */ 
/*      */   
/*      */   public int calcHash(int[] q, int qlen) {
/* 1170 */     if (qlen < 4) {
/* 1171 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1178 */     int hash = q[0] ^ this._seed;
/* 1179 */     hash += hash >>> 9;
/* 1180 */     hash += q[1];
/* 1181 */     hash += hash >>> 15;
/* 1182 */     hash *= 33;
/* 1183 */     hash ^= q[2];
/* 1184 */     hash += hash >>> 4;
/*      */     
/* 1186 */     for (int i = 3; i < qlen; i++) {
/* 1187 */       int next = q[i];
/* 1188 */       next ^= next >> 21;
/* 1189 */       hash += next;
/*      */     } 
/* 1191 */     hash *= 65599;
/*      */ 
/*      */     
/* 1194 */     hash += hash >>> 19;
/* 1195 */     hash ^= hash << 5;
/* 1196 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void rehash() {
/* 1208 */     this._hashShared = false;
/*      */ 
/*      */ 
/*      */     
/* 1212 */     int[] oldHashArea = this._hashArea;
/* 1213 */     String[] oldNames = this._names;
/* 1214 */     int oldSize = this._hashSize;
/* 1215 */     int oldCount = this._count;
/* 1216 */     int newSize = oldSize + oldSize;
/* 1217 */     int oldEnd = this._spilloverEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1222 */     if (newSize > 65536) {
/* 1223 */       nukeSymbols(true);
/*      */       
/*      */       return;
/*      */     } 
/* 1227 */     this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/* 1228 */     this._hashSize = newSize;
/* 1229 */     this._secondaryStart = newSize << 2;
/* 1230 */     this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/* 1231 */     this._tertiaryShift = _calcTertiaryShift(newSize);
/*      */ 
/*      */     
/* 1234 */     this._names = new String[oldNames.length << 1];
/* 1235 */     nukeSymbols(false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1242 */     int copyCount = 0;
/* 1243 */     int[] q = new int[16];
/* 1244 */     for (int offset = 0, end = oldEnd; offset < end; offset += 4) {
/* 1245 */       int len = oldHashArea[offset + 3];
/* 1246 */       if (len != 0) {
/*      */         int qoff;
/*      */         
/* 1249 */         copyCount++;
/* 1250 */         String name = oldNames[offset >> 2];
/* 1251 */         switch (len) {
/*      */           case 1:
/* 1253 */             q[0] = oldHashArea[offset];
/* 1254 */             addName(name, q, 1);
/*      */             break;
/*      */           case 2:
/* 1257 */             q[0] = oldHashArea[offset];
/* 1258 */             q[1] = oldHashArea[offset + 1];
/* 1259 */             addName(name, q, 2);
/*      */             break;
/*      */           case 3:
/* 1262 */             q[0] = oldHashArea[offset];
/* 1263 */             q[1] = oldHashArea[offset + 1];
/* 1264 */             q[2] = oldHashArea[offset + 2];
/* 1265 */             addName(name, q, 3);
/*      */             break;
/*      */           default:
/* 1268 */             if (len > q.length) {
/* 1269 */               q = new int[len];
/*      */             }
/*      */             
/* 1272 */             qoff = oldHashArea[offset + 1];
/* 1273 */             System.arraycopy(oldHashArea, qoff, q, 0, len);
/* 1274 */             addName(name, q, len);
/*      */             break;
/*      */         } 
/*      */ 
/*      */       
/*      */       } 
/*      */     } 
/* 1281 */     if (copyCount != oldCount) {
/* 1282 */       throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void nukeSymbols(boolean fill) {
/* 1291 */     this._count = 0;
/*      */     
/* 1293 */     this._spilloverEnd = _spilloverStart();
/*      */     
/* 1295 */     this._longNameOffset = this._hashSize << 3;
/* 1296 */     if (fill) {
/* 1297 */       Arrays.fill(this._hashArea, 0);
/* 1298 */       Arrays.fill((Object[])this._names, (Object)null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _spilloverStart() {
/* 1314 */     int offset = this._hashSize;
/* 1315 */     return (offset << 3) - offset;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportTooManyCollisions() {
/* 1321 */     if (this._hashSize <= 1024) {
/*      */       return;
/*      */     }
/* 1324 */     throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions. You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int _calcTertiaryShift(int primarySlots) {
/* 1333 */     int tertSlots = primarySlots >> 2;
/*      */ 
/*      */     
/* 1336 */     if (tertSlots < 64) {
/* 1337 */       return 4;
/*      */     }
/* 1339 */     if (tertSlots <= 256) {
/* 1340 */       return 5;
/*      */     }
/* 1342 */     if (tertSlots <= 1024) {
/* 1343 */       return 6;
/*      */     }
/*      */     
/* 1346 */     return 7;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class TableInfo
/*      */   {
/*      */     public final int size;
/*      */ 
/*      */     
/*      */     public final int count;
/*      */ 
/*      */     
/*      */     public final int tertiaryShift;
/*      */ 
/*      */     
/*      */     public final int[] mainHash;
/*      */ 
/*      */     
/*      */     public final String[] names;
/*      */ 
/*      */     
/*      */     public final int spilloverEnd;
/*      */ 
/*      */     
/*      */     public final int longNameOffset;
/*      */ 
/*      */     
/*      */     public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset) {
/* 1375 */       this.size = size;
/* 1376 */       this.count = count;
/* 1377 */       this.tertiaryShift = tertiaryShift;
/* 1378 */       this.mainHash = mainHash;
/* 1379 */       this.names = names;
/* 1380 */       this.spilloverEnd = spilloverEnd;
/* 1381 */       this.longNameOffset = longNameOffset;
/*      */     }
/*      */ 
/*      */     
/*      */     public TableInfo(ByteQuadsCanonicalizer src) {
/* 1386 */       this.size = src._hashSize;
/* 1387 */       this.count = src._count;
/* 1388 */       this.tertiaryShift = src._tertiaryShift;
/* 1389 */       this.mainHash = src._hashArea;
/* 1390 */       this.names = src._names;
/* 1391 */       this.spilloverEnd = src._spilloverEnd;
/* 1392 */       this.longNameOffset = src._longNameOffset;
/*      */     }
/*      */     
/*      */     public static TableInfo createInitial(int sz) {
/* 1396 */       int hashAreaSize = sz << 3;
/* 1397 */       int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*      */       
/* 1399 */       return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/sym/ByteQuadsCanonicalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */