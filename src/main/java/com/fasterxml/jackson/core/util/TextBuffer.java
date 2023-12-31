/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ public final class TextBuffer
/*     */ {
/*  29 */   static final char[] NO_CHARS = new char[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int MIN_SEGMENT_LEN = 500;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int MAX_SEGMENT_LEN = 65536;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final BufferRecycler _allocator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _inputBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _inputStart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _inputLen;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArrayList<char[]> _segments;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _hasSegments;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _segmentSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _currentSegment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _currentSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String _resultString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _resultArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextBuffer(BufferRecycler allocator) {
/* 124 */     this._allocator = allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   protected TextBuffer(BufferRecycler allocator, char[] initialSegment) {
/* 129 */     this._allocator = allocator;
/* 130 */     this._currentSegment = initialSegment;
/* 131 */     this._currentSize = initialSegment.length;
/* 132 */     this._inputStart = -1;
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
/*     */   public static TextBuffer fromInitial(char[] initialSegment) {
/* 147 */     return new TextBuffer(null, initialSegment);
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
/*     */   public void releaseBuffers() {
/* 165 */     this._inputStart = -1;
/* 166 */     this._currentSize = 0;
/* 167 */     this._inputLen = 0;
/*     */     
/* 169 */     this._inputBuffer = null;
/*     */ 
/*     */     
/* 172 */     this._resultArray = null;
/*     */     
/* 174 */     if (this._hasSegments) {
/* 175 */       clearSegments();
/*     */     }
/*     */ 
/*     */     
/* 179 */     if (this._allocator != null && 
/* 180 */       this._currentSegment != null) {
/*     */       
/* 182 */       char[] buf = this._currentSegment;
/* 183 */       this._currentSegment = null;
/* 184 */       this._allocator.releaseCharBuffer(2, buf);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetWithEmpty() {
/* 195 */     this._inputStart = -1;
/* 196 */     this._currentSize = 0;
/* 197 */     this._inputLen = 0;
/*     */     
/* 199 */     this._inputBuffer = null;
/* 200 */     this._resultString = null;
/* 201 */     this._resultArray = null;
/*     */ 
/*     */     
/* 204 */     if (this._hasSegments) {
/* 205 */       clearSegments();
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
/*     */   public void resetWith(char ch) {
/* 219 */     this._inputStart = -1;
/* 220 */     this._inputLen = 0;
/*     */     
/* 222 */     this._resultString = null;
/* 223 */     this._resultArray = null;
/*     */     
/* 225 */     if (this._hasSegments) {
/* 226 */       clearSegments();
/* 227 */     } else if (this._currentSegment == null) {
/* 228 */       this._currentSegment = buf(1);
/*     */     } 
/* 230 */     this._currentSegment[0] = ch;
/* 231 */     this._currentSize = this._segmentSize = 1;
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
/*     */   public void resetWithShared(char[] buf, int offset, int len) {
/* 247 */     this._resultString = null;
/* 248 */     this._resultArray = null;
/*     */ 
/*     */     
/* 251 */     this._inputBuffer = buf;
/* 252 */     this._inputStart = offset;
/* 253 */     this._inputLen = len;
/*     */ 
/*     */     
/* 256 */     if (this._hasSegments) {
/* 257 */       clearSegments();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetWithCopy(char[] buf, int offset, int len) {
/* 263 */     this._inputBuffer = null;
/* 264 */     this._inputStart = -1;
/* 265 */     this._inputLen = 0;
/*     */     
/* 267 */     this._resultString = null;
/* 268 */     this._resultArray = null;
/*     */ 
/*     */     
/* 271 */     if (this._hasSegments) {
/* 272 */       clearSegments();
/* 273 */     } else if (this._currentSegment == null) {
/* 274 */       this._currentSegment = buf(len);
/*     */     } 
/* 276 */     this._currentSize = this._segmentSize = 0;
/* 277 */     append(buf, offset, len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetWithCopy(String text, int start, int len) {
/* 283 */     this._inputBuffer = null;
/* 284 */     this._inputStart = -1;
/* 285 */     this._inputLen = 0;
/*     */     
/* 287 */     this._resultString = null;
/* 288 */     this._resultArray = null;
/*     */     
/* 290 */     if (this._hasSegments) {
/* 291 */       clearSegments();
/* 292 */     } else if (this._currentSegment == null) {
/* 293 */       this._currentSegment = buf(len);
/*     */     } 
/* 295 */     this._currentSize = this._segmentSize = 0;
/* 296 */     append(text, start, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetWithString(String value) {
/* 301 */     this._inputBuffer = null;
/* 302 */     this._inputStart = -1;
/* 303 */     this._inputLen = 0;
/*     */     
/* 305 */     this._resultString = value;
/* 306 */     this._resultArray = null;
/*     */     
/* 308 */     if (this._hasSegments) {
/* 309 */       clearSegments();
/*     */     }
/* 311 */     this._currentSize = 0;
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
/*     */   public char[] getBufferWithoutReset() {
/* 324 */     return this._currentSegment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] buf(int needed) {
/* 331 */     if (this._allocator != null) {
/* 332 */       return this._allocator.allocCharBuffer(2, needed);
/*     */     }
/* 334 */     return new char[Math.max(needed, 500)];
/*     */   }
/*     */ 
/*     */   
/*     */   private void clearSegments() {
/* 339 */     this._hasSegments = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 347 */     this._segments.clear();
/* 348 */     this._currentSize = this._segmentSize = 0;
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
/* 361 */     if (this._inputStart >= 0) {
/* 362 */       return this._inputLen;
/*     */     }
/* 364 */     if (this._resultArray != null) {
/* 365 */       return this._resultArray.length;
/*     */     }
/* 367 */     if (this._resultString != null) {
/* 368 */       return this._resultString.length();
/*     */     }
/*     */     
/* 371 */     return this._segmentSize + this._currentSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextOffset() {
/* 379 */     return (this._inputStart >= 0) ? this._inputStart : 0;
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
/*     */   public boolean hasTextAsCharacters() {
/* 392 */     if (this._inputStart >= 0 || this._resultArray != null) return true;
/*     */     
/* 394 */     if (this._resultString != null) return false; 
/* 395 */     return true;
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
/*     */   public char[] getTextBuffer() {
/* 408 */     if (this._inputStart >= 0) return this._inputBuffer; 
/* 409 */     if (this._resultArray != null) return this._resultArray; 
/* 410 */     if (this._resultString != null) {
/* 411 */       return this._resultArray = this._resultString.toCharArray();
/*     */     }
/*     */     
/* 414 */     if (!this._hasSegments) {
/* 415 */       return (this._currentSegment == null) ? NO_CHARS : this._currentSegment;
/*     */     }
/*     */     
/* 418 */     return contentsAsArray();
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
/*     */   public String contentsAsString() {
/* 436 */     if (this._resultString == null)
/*     */     {
/* 438 */       if (this._resultArray != null) {
/* 439 */         this._resultString = new String(this._resultArray);
/*     */       
/*     */       }
/* 442 */       else if (this._inputStart >= 0) {
/* 443 */         if (this._inputLen < 1) {
/* 444 */           return this._resultString = "";
/*     */         }
/* 446 */         this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
/*     */       } else {
/*     */         
/* 449 */         int segLen = this._segmentSize;
/* 450 */         int currLen = this._currentSize;
/*     */         
/* 452 */         if (segLen == 0) {
/* 453 */           this._resultString = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/*     */         } else {
/* 455 */           StringBuilder sb = new StringBuilder(segLen + currLen);
/*     */           
/* 457 */           if (this._segments != null) {
/* 458 */             for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 459 */               char[] curr = this._segments.get(i);
/* 460 */               sb.append(curr, 0, curr.length);
/*     */             } 
/*     */           }
/*     */           
/* 464 */           sb.append(this._currentSegment, 0, this._currentSize);
/* 465 */           this._resultString = sb.toString();
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 471 */     return this._resultString;
/*     */   }
/*     */   
/*     */   public char[] contentsAsArray() {
/* 475 */     char[] result = this._resultArray;
/* 476 */     if (result == null) {
/* 477 */       this._resultArray = result = resultArray();
/*     */     }
/* 479 */     return result;
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
/*     */   public BigDecimal contentsAsDecimal() throws NumberFormatException {
/* 493 */     if (this._resultArray != null) {
/* 494 */       return NumberInput.parseBigDecimal(this._resultArray);
/*     */     }
/*     */     
/* 497 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 498 */       return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     }
/*     */     
/* 501 */     if (this._segmentSize == 0 && this._currentSegment != null) {
/* 502 */       return NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize);
/*     */     }
/*     */     
/* 505 */     return NumberInput.parseBigDecimal(contentsAsArray());
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
/*     */   public double contentsAsDouble() throws NumberFormatException {
/* 517 */     return NumberInput.parseDouble(contentsAsString());
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
/*     */   public int contentsAsInt(boolean neg) {
/* 536 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 537 */       if (neg) {
/* 538 */         return -NumberInput.parseInt(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
/*     */       }
/* 540 */       return NumberInput.parseInt(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     } 
/* 542 */     if (neg) {
/* 543 */       return -NumberInput.parseInt(this._currentSegment, 1, this._currentSize - 1);
/*     */     }
/* 545 */     return NumberInput.parseInt(this._currentSegment, 0, this._currentSize);
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
/*     */   public long contentsAsLong(boolean neg) {
/* 564 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 565 */       if (neg) {
/* 566 */         return -NumberInput.parseLong(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
/*     */       }
/* 568 */       return NumberInput.parseLong(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     } 
/* 570 */     if (neg) {
/* 571 */       return -NumberInput.parseLong(this._currentSegment, 1, this._currentSize - 1);
/*     */     }
/* 573 */     return NumberInput.parseLong(this._currentSegment, 0, this._currentSize);
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
/*     */   public int contentsToWriter(Writer w) throws IOException {
/* 589 */     if (this._resultArray != null) {
/* 590 */       w.write(this._resultArray);
/* 591 */       return this._resultArray.length;
/*     */     } 
/* 593 */     if (this._resultString != null) {
/* 594 */       w.write(this._resultString);
/* 595 */       return this._resultString.length();
/*     */     } 
/*     */     
/* 598 */     if (this._inputStart >= 0) {
/* 599 */       int i = this._inputLen;
/* 600 */       if (i > 0) {
/* 601 */         w.write(this._inputBuffer, this._inputStart, i);
/*     */       }
/* 603 */       return i;
/*     */     } 
/*     */     
/* 606 */     int total = 0;
/* 607 */     if (this._segments != null) {
/* 608 */       for (int i = 0, end = this._segments.size(); i < end; i++) {
/* 609 */         char[] curr = this._segments.get(i);
/* 610 */         int currLen = curr.length;
/* 611 */         w.write(curr, 0, currLen);
/* 612 */         total += currLen;
/*     */       } 
/*     */     }
/* 615 */     int len = this._currentSize;
/* 616 */     if (len > 0) {
/* 617 */       w.write(this._currentSegment, 0, len);
/* 618 */       total += len;
/*     */     } 
/* 620 */     return total;
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
/*     */   public void ensureNotShared() {
/* 634 */     if (this._inputStart >= 0) {
/* 635 */       unshare(16);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void append(char c) {
/* 641 */     if (this._inputStart >= 0) {
/* 642 */       unshare(16);
/*     */     }
/* 644 */     this._resultString = null;
/* 645 */     this._resultArray = null;
/*     */     
/* 647 */     char[] curr = this._currentSegment;
/* 648 */     if (this._currentSize >= curr.length) {
/* 649 */       expand(1);
/* 650 */       curr = this._currentSegment;
/*     */     } 
/* 652 */     curr[this._currentSize++] = c;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(char[] c, int start, int len) {
/* 658 */     if (this._inputStart >= 0) {
/* 659 */       unshare(len);
/*     */     }
/* 661 */     this._resultString = null;
/* 662 */     this._resultArray = null;
/*     */ 
/*     */     
/* 665 */     char[] curr = this._currentSegment;
/* 666 */     int max = curr.length - this._currentSize;
/*     */     
/* 668 */     if (max >= len) {
/* 669 */       System.arraycopy(c, start, curr, this._currentSize, len);
/* 670 */       this._currentSize += len;
/*     */       
/*     */       return;
/*     */     } 
/* 674 */     if (max > 0) {
/* 675 */       System.arraycopy(c, start, curr, this._currentSize, max);
/* 676 */       start += max;
/* 677 */       len -= max;
/*     */     } 
/*     */ 
/*     */     
/*     */     do {
/* 682 */       expand(len);
/* 683 */       int amount = Math.min(this._currentSegment.length, len);
/* 684 */       System.arraycopy(c, start, this._currentSegment, 0, amount);
/* 685 */       this._currentSize += amount;
/* 686 */       start += amount;
/* 687 */       len -= amount;
/* 688 */     } while (len > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String str, int offset, int len) {
/* 694 */     if (this._inputStart >= 0) {
/* 695 */       unshare(len);
/*     */     }
/* 697 */     this._resultString = null;
/* 698 */     this._resultArray = null;
/*     */ 
/*     */     
/* 701 */     char[] curr = this._currentSegment;
/* 702 */     int max = curr.length - this._currentSize;
/* 703 */     if (max >= len) {
/* 704 */       str.getChars(offset, offset + len, curr, this._currentSize);
/* 705 */       this._currentSize += len;
/*     */       
/*     */       return;
/*     */     } 
/* 709 */     if (max > 0) {
/* 710 */       str.getChars(offset, offset + max, curr, this._currentSize);
/* 711 */       len -= max;
/* 712 */       offset += max;
/*     */     } 
/*     */ 
/*     */     
/*     */     do {
/* 717 */       expand(len);
/* 718 */       int amount = Math.min(this._currentSegment.length, len);
/* 719 */       str.getChars(offset, offset + amount, this._currentSegment, 0);
/* 720 */       this._currentSize += amount;
/* 721 */       offset += amount;
/* 722 */       len -= amount;
/* 723 */     } while (len > 0);
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
/*     */   public char[] getCurrentSegment() {
/* 738 */     if (this._inputStart >= 0) {
/* 739 */       unshare(1);
/*     */     } else {
/* 741 */       char[] curr = this._currentSegment;
/* 742 */       if (curr == null) {
/* 743 */         this._currentSegment = buf(0);
/* 744 */       } else if (this._currentSize >= curr.length) {
/*     */         
/* 746 */         expand(1);
/*     */       } 
/*     */     } 
/* 749 */     return this._currentSegment;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] emptyAndGetCurrentSegment() {
/* 755 */     this._inputStart = -1;
/* 756 */     this._currentSize = 0;
/* 757 */     this._inputLen = 0;
/*     */     
/* 759 */     this._inputBuffer = null;
/* 760 */     this._resultString = null;
/* 761 */     this._resultArray = null;
/*     */ 
/*     */     
/* 764 */     if (this._hasSegments) {
/* 765 */       clearSegments();
/*     */     }
/* 767 */     char[] curr = this._currentSegment;
/* 768 */     if (curr == null) {
/* 769 */       this._currentSegment = curr = buf(0);
/*     */     }
/* 771 */     return curr;
/*     */   }
/*     */   
/* 774 */   public int getCurrentSegmentSize() { return this._currentSize; } public void setCurrentLength(int len) {
/* 775 */     this._currentSize = len;
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
/*     */   public String setCurrentAndReturn(int len) {
/* 790 */     this._currentSize = len;
/*     */     
/* 792 */     if (this._segmentSize > 0) {
/* 793 */       return contentsAsString();
/*     */     }
/*     */     
/* 796 */     int currLen = this._currentSize;
/* 797 */     String str = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/* 798 */     this._resultString = str;
/* 799 */     return str;
/*     */   }
/*     */   
/*     */   public char[] finishCurrentSegment() {
/* 803 */     if (this._segments == null) {
/* 804 */       this._segments = (ArrayList)new ArrayList<char>();
/*     */     }
/* 806 */     this._hasSegments = true;
/* 807 */     this._segments.add(this._currentSegment);
/* 808 */     int oldLen = this._currentSegment.length;
/* 809 */     this._segmentSize += oldLen;
/* 810 */     this._currentSize = 0;
/*     */ 
/*     */     
/* 813 */     int newLen = oldLen + (oldLen >> 1);
/* 814 */     if (newLen < 500) {
/* 815 */       newLen = 500;
/* 816 */     } else if (newLen > 65536) {
/* 817 */       newLen = 65536;
/*     */     } 
/* 819 */     char[] curr = carr(newLen);
/* 820 */     this._currentSegment = curr;
/* 821 */     return curr;
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
/*     */   public char[] expandCurrentSegment() {
/* 834 */     char[] curr = this._currentSegment;
/*     */     
/* 836 */     int len = curr.length;
/* 837 */     int newLen = len + (len >> 1);
/*     */     
/* 839 */     if (newLen > 65536) {
/* 840 */       newLen = len + (len >> 2);
/*     */     }
/* 842 */     return this._currentSegment = Arrays.copyOf(curr, newLen);
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
/*     */   public char[] expandCurrentSegment(int minSize) {
/* 857 */     char[] curr = this._currentSegment;
/* 858 */     if (curr.length >= minSize) return curr; 
/* 859 */     this._currentSegment = curr = Arrays.copyOf(curr, minSize);
/* 860 */     return curr;
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
/*     */   public String toString() {
/* 874 */     return contentsAsString();
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
/*     */   private void unshare(int needExtra) {
/* 888 */     int sharedLen = this._inputLen;
/* 889 */     this._inputLen = 0;
/* 890 */     char[] inputBuf = this._inputBuffer;
/* 891 */     this._inputBuffer = null;
/* 892 */     int start = this._inputStart;
/* 893 */     this._inputStart = -1;
/*     */ 
/*     */     
/* 896 */     int needed = sharedLen + needExtra;
/* 897 */     if (this._currentSegment == null || needed > this._currentSegment.length) {
/* 898 */       this._currentSegment = buf(needed);
/*     */     }
/* 900 */     if (sharedLen > 0) {
/* 901 */       System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
/*     */     }
/* 903 */     this._segmentSize = 0;
/* 904 */     this._currentSize = sharedLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void expand(int minNewSegmentSize) {
/* 911 */     if (this._segments == null) {
/* 912 */       this._segments = (ArrayList)new ArrayList<char>();
/*     */     }
/* 914 */     char[] curr = this._currentSegment;
/* 915 */     this._hasSegments = true;
/* 916 */     this._segments.add(curr);
/* 917 */     this._segmentSize += curr.length;
/* 918 */     this._currentSize = 0;
/* 919 */     int oldLen = curr.length;
/*     */ 
/*     */     
/* 922 */     int newLen = oldLen + (oldLen >> 1);
/* 923 */     if (newLen < 500) {
/* 924 */       newLen = 500;
/* 925 */     } else if (newLen > 65536) {
/* 926 */       newLen = 65536;
/*     */     } 
/* 928 */     this._currentSegment = carr(newLen);
/*     */   }
/*     */ 
/*     */   
/*     */   private char[] resultArray() {
/* 933 */     if (this._resultString != null) {
/* 934 */       return this._resultString.toCharArray();
/*     */     }
/*     */     
/* 937 */     if (this._inputStart >= 0) {
/* 938 */       int len = this._inputLen;
/* 939 */       if (len < 1) {
/* 940 */         return NO_CHARS;
/*     */       }
/* 942 */       int start = this._inputStart;
/* 943 */       if (start == 0) {
/* 944 */         return Arrays.copyOf(this._inputBuffer, len);
/*     */       }
/* 946 */       return Arrays.copyOfRange(this._inputBuffer, start, start + len);
/*     */     } 
/*     */     
/* 949 */     int size = size();
/* 950 */     if (size < 1) {
/* 951 */       return NO_CHARS;
/*     */     }
/* 953 */     int offset = 0;
/* 954 */     char[] result = carr(size);
/* 955 */     if (this._segments != null) {
/* 956 */       for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 957 */         char[] curr = this._segments.get(i);
/* 958 */         int currLen = curr.length;
/* 959 */         System.arraycopy(curr, 0, result, offset, currLen);
/* 960 */         offset += currLen;
/*     */       } 
/*     */     }
/* 963 */     System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
/* 964 */     return result;
/*     */   }
/*     */   private char[] carr(int len) {
/* 967 */     return new char[len];
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/TextBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */