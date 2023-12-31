/*     */ package com.fasterxml.jackson.core.filter;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.util.JsonParserDelegate;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilteringParserDelegate
/*     */   extends JsonParserDelegate
/*     */ {
/*     */   protected TokenFilter rootFilter;
/*     */   protected boolean _allowMultipleMatches;
/*     */   protected TokenFilter.Inclusion _inclusion;
/*     */   protected JsonToken _currToken;
/*     */   protected JsonToken _lastClearedToken;
/*     */   protected TokenFilterContext _headContext;
/*     */   protected TokenFilterContext _exposedContext;
/*     */   protected TokenFilter _itemFilter;
/*     */   protected int _matchCount;
/*     */   
/*     */   @Deprecated
/*     */   public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
/* 108 */     this(p, f, includePath ? TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH : TokenFilter.Inclusion.ONLY_INCLUDE_ALL, allowMultipleMatches);
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
/*     */   public FilteringParserDelegate(JsonParser p, TokenFilter f, TokenFilter.Inclusion inclusion, boolean allowMultipleMatches) {
/* 120 */     super(p);
/* 121 */     this.rootFilter = f;
/*     */     
/* 123 */     this._itemFilter = f;
/* 124 */     this._headContext = TokenFilterContext.createRootContext(f);
/* 125 */     this._inclusion = inclusion;
/* 126 */     this._allowMultipleMatches = allowMultipleMatches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenFilter getFilter() {
/* 135 */     return this.rootFilter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMatchCount() {
/* 144 */     return this._matchCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken getCurrentToken() {
/* 153 */     return this._currToken; } public JsonToken currentToken() {
/* 154 */     return this._currToken;
/*     */   }
/*     */   @Deprecated
/*     */   public final int getCurrentTokenId() {
/* 158 */     return currentTokenId();
/*     */   }
/*     */   public final int currentTokenId() {
/* 161 */     JsonToken t = this._currToken;
/* 162 */     return (t == null) ? 0 : t.id();
/*     */   }
/*     */   public boolean hasCurrentToken() {
/* 165 */     return (this._currToken != null);
/*     */   } public boolean hasTokenId(int id) {
/* 167 */     JsonToken t = this._currToken;
/* 168 */     if (t == null) {
/* 169 */       return (0 == id);
/*     */     }
/* 171 */     return (t.id() == id);
/*     */   }
/*     */   
/*     */   public final boolean hasToken(JsonToken t) {
/* 175 */     return (this._currToken == t);
/*     */   }
/*     */   
/* 178 */   public boolean isExpectedStartArrayToken() { return (this._currToken == JsonToken.START_ARRAY); } public boolean isExpectedStartObjectToken() {
/* 179 */     return (this._currToken == JsonToken.START_OBJECT);
/*     */   } public JsonLocation getCurrentLocation() {
/* 181 */     return this.delegate.getCurrentLocation();
/*     */   }
/*     */   
/*     */   public JsonStreamContext getParsingContext() {
/* 185 */     return _filterContext();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentName() throws IOException {
/* 191 */     JsonStreamContext ctxt = _filterContext();
/* 192 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 193 */       JsonStreamContext parent = ctxt.getParent();
/* 194 */       return (parent == null) ? null : parent.getCurrentName();
/*     */     } 
/* 196 */     return ctxt.getCurrentName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String currentName() throws IOException {
/* 202 */     JsonStreamContext ctxt = _filterContext();
/* 203 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 204 */       JsonStreamContext parent = ctxt.getParent();
/* 205 */       return (parent == null) ? null : parent.getCurrentName();
/*     */     } 
/* 207 */     return ctxt.getCurrentName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearCurrentToken() {
/* 218 */     if (this._currToken != null) {
/* 219 */       this._lastClearedToken = this._currToken;
/* 220 */       this._currToken = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public JsonToken getLastClearedToken() {
/* 225 */     return this._lastClearedToken;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void overrideCurrentName(String name) {
/* 232 */     throw new UnsupportedOperationException("Can not currently override name during filtering read");
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
/*     */   public JsonToken nextToken() throws IOException {
/*     */     boolean returnEnd;
/*     */     String name;
/* 252 */     if (!this._allowMultipleMatches && this._currToken != null && this._exposedContext == null)
/*     */     {
/*     */       
/* 255 */       if (this._currToken.isScalarValue() && !this._headContext.isStartHandled() && this._inclusion == TokenFilter.Inclusion.ONLY_INCLUDE_ALL && this._itemFilter == TokenFilter.INCLUDE_ALL)
/*     */       {
/*     */         
/* 258 */         return this._currToken = null;
/*     */       }
/*     */     }
/*     */     
/* 262 */     TokenFilterContext ctxt = this._exposedContext;
/*     */     
/* 264 */     if (ctxt != null) {
/*     */       while (true) {
/* 266 */         JsonToken jsonToken = ctxt.nextTokenToRead();
/* 267 */         if (jsonToken != null) {
/* 268 */           this._currToken = jsonToken;
/* 269 */           return jsonToken;
/*     */         } 
/*     */         
/* 272 */         if (ctxt == this._headContext) {
/* 273 */           this._exposedContext = null;
/* 274 */           if (ctxt.inArray()) {
/* 275 */             jsonToken = this.delegate.getCurrentToken();
/*     */ 
/*     */             
/* 278 */             this._currToken = jsonToken;
/* 279 */             return jsonToken;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 285 */           jsonToken = this.delegate.currentToken();
/* 286 */           if (jsonToken != JsonToken.FIELD_NAME) {
/* 287 */             this._currToken = jsonToken;
/* 288 */             return jsonToken;
/*     */           } 
/*     */           
/*     */           break;
/*     */         } 
/* 293 */         ctxt = this._headContext.findChildOf(ctxt);
/* 294 */         this._exposedContext = ctxt;
/* 295 */         if (ctxt == null) {
/* 296 */           throw _constructError("Unexpected problem: chain of filtered context broken");
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 302 */     JsonToken t = this.delegate.nextToken();
/* 303 */     if (t == null) {
/*     */       
/* 305 */       this._currToken = t;
/* 306 */       return t;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 312 */     switch (t.id())
/*     */     { case 3:
/* 314 */         f = this._itemFilter;
/* 315 */         if (f == TokenFilter.INCLUDE_ALL) {
/* 316 */           this._headContext = this._headContext.createChildArrayContext(f, true);
/* 317 */           return this._currToken = t;
/*     */         } 
/* 319 */         if (f == null) {
/* 320 */           this.delegate.skipChildren();
/*     */         }
/*     */         else {
/*     */           
/* 324 */           f = this._headContext.checkValue(f);
/* 325 */           if (f == null) {
/* 326 */             this.delegate.skipChildren();
/*     */           } else {
/*     */             
/* 329 */             if (f != TokenFilter.INCLUDE_ALL) {
/* 330 */               f = f.filterStartArray();
/*     */             }
/* 332 */             this._itemFilter = f;
/* 333 */             if (f == TokenFilter.INCLUDE_ALL) {
/* 334 */               this._headContext = this._headContext.createChildArrayContext(f, true);
/* 335 */               return this._currToken = t;
/* 336 */             }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*     */               
/* 338 */               this._headContext = this._headContext.createChildArrayContext(f, true);
/* 339 */               return this._currToken = t;
/*     */             } 
/* 341 */             this._headContext = this._headContext.createChildArrayContext(f, false);
/*     */ 
/*     */             
/* 344 */             if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
/* 345 */               t = _nextTokenWithBuffering(this._headContext);
/* 346 */               if (t != null) {
/* 347 */                 this._currToken = t;
/* 348 */                 return t;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 470 */         return _nextToken2();case 1: f = this._itemFilter; if (f == TokenFilter.INCLUDE_ALL) { this._headContext = this._headContext.createChildObjectContext(f, true); return this._currToken = t; }  if (f == null) { this.delegate.skipChildren(); } else { f = this._headContext.checkValue(f); if (f == null) { this.delegate.skipChildren(); } else { if (f != TokenFilter.INCLUDE_ALL) f = f.filterStartObject();  this._itemFilter = f; if (f == TokenFilter.INCLUDE_ALL) { this._headContext = this._headContext.createChildObjectContext(f, true); return this._currToken = t; }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) { this._headContext = this._headContext.createChildObjectContext(f, true); return this._currToken = t; }  this._headContext = this._headContext.createChildObjectContext(f, false); if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) { t = _nextTokenWithBuffering(this._headContext); if (t != null) { this._currToken = t; return t; }  }  }  }  return _nextToken2();case 2: case 4: returnEnd = this._headContext.isStartHandled(); f = this._headContext.getFilter(); if (f != null && f != TokenFilter.INCLUDE_ALL) f.filterFinishArray();  this._headContext = this._headContext.getParent(); this._itemFilter = this._headContext.getFilter(); if (returnEnd) return this._currToken = t;  return _nextToken2();case 5: name = this.delegate.getCurrentName(); f = this._headContext.setFieldName(name); if (f == TokenFilter.INCLUDE_ALL) { this._itemFilter = f; return this._currToken = t; }  if (f == null) { this.delegate.nextToken(); this.delegate.skipChildren(); } else { f = f.includeProperty(name); if (f == null) { this.delegate.nextToken(); this.delegate.skipChildren(); } else { this._itemFilter = f; if (f == TokenFilter.INCLUDE_ALL) if (_verifyAllowedMatches()) { if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) return this._currToken = t;  } else { this.delegate.nextToken(); this.delegate.skipChildren(); }   if (this._inclusion != TokenFilter.Inclusion.ONLY_INCLUDE_ALL) { t = _nextTokenWithBuffering(this._headContext); if (t != null) { this._currToken = t; return t; }  }  }  }  return _nextToken2(); }  TokenFilter f = this._itemFilter; if (f == TokenFilter.INCLUDE_ALL) return this._currToken = t;  if (f != null) { f = this._headContext.checkValue(f); if ((f == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate))) && _verifyAllowedMatches()) return this._currToken = t;  }  return _nextToken2();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonToken _nextToken2() throws IOException {
/*     */     while (true) {
/*     */       boolean returnEnd;
/*     */       String name;
/* 481 */       JsonToken t = this.delegate.nextToken();
/* 482 */       if (t == null) {
/* 483 */         this._currToken = t;
/* 484 */         return t;
/*     */       } 
/*     */ 
/*     */       
/* 488 */       switch (t.id()) {
/*     */         case 3:
/* 490 */           f = this._itemFilter;
/* 491 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 492 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 493 */             return this._currToken = t;
/*     */           } 
/* 495 */           if (f == null) {
/* 496 */             this.delegate.skipChildren();
/*     */             
/*     */             continue;
/*     */           } 
/* 500 */           f = this._headContext.checkValue(f);
/* 501 */           if (f == null) {
/* 502 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 505 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 506 */             f = f.filterStartArray();
/*     */           }
/* 508 */           this._itemFilter = f;
/* 509 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 510 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 511 */             return this._currToken = t;
/* 512 */           }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/* 513 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 514 */             return this._currToken = t;
/*     */           } 
/* 516 */           this._headContext = this._headContext.createChildArrayContext(f, false);
/*     */           
/* 518 */           if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
/* 519 */             t = _nextTokenWithBuffering(this._headContext);
/* 520 */             if (t != null) {
/* 521 */               this._currToken = t;
/* 522 */               return t;
/*     */             } 
/*     */           } 
/*     */           continue;
/*     */         
/*     */         case 1:
/* 528 */           f = this._itemFilter;
/* 529 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 530 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 531 */             return this._currToken = t;
/*     */           } 
/* 533 */           if (f == null) {
/* 534 */             this.delegate.skipChildren();
/*     */             
/*     */             continue;
/*     */           } 
/* 538 */           f = this._headContext.checkValue(f);
/* 539 */           if (f == null) {
/* 540 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 543 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 544 */             f = f.filterStartObject();
/*     */           }
/* 546 */           this._itemFilter = f;
/* 547 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 548 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 549 */             return this._currToken = t;
/* 550 */           }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/* 551 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 552 */             return this._currToken = t;
/*     */           } 
/* 554 */           this._headContext = this._headContext.createChildObjectContext(f, false);
/* 555 */           if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
/* 556 */             t = _nextTokenWithBuffering(this._headContext);
/* 557 */             if (t != null) {
/* 558 */               this._currToken = t;
/* 559 */               return t;
/*     */             } 
/*     */           } 
/*     */           continue;
/*     */ 
/*     */         
/*     */         case 2:
/*     */         case 4:
/* 567 */           returnEnd = this._headContext.isStartHandled();
/* 568 */           f = this._headContext.getFilter();
/* 569 */           if (f != null && f != TokenFilter.INCLUDE_ALL) {
/* 570 */             f.filterFinishArray();
/*     */           }
/* 572 */           this._headContext = this._headContext.getParent();
/* 573 */           this._itemFilter = this._headContext.getFilter();
/* 574 */           if (returnEnd) {
/* 575 */             return this._currToken = t;
/*     */           }
/*     */           continue;
/*     */ 
/*     */ 
/*     */         
/*     */         case 5:
/* 582 */           name = this.delegate.getCurrentName();
/* 583 */           f = this._headContext.setFieldName(name);
/* 584 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 585 */             this._itemFilter = f;
/* 586 */             return this._currToken = t;
/*     */           } 
/* 588 */           if (f == null) {
/* 589 */             this.delegate.nextToken();
/* 590 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 593 */           f = f.includeProperty(name);
/* 594 */           if (f == null) {
/* 595 */             this.delegate.nextToken();
/* 596 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 599 */           this._itemFilter = f;
/* 600 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 601 */             if (_verifyAllowedMatches()) {
/* 602 */               if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH)
/* 603 */                 return this._currToken = t; 
/*     */               continue;
/*     */             } 
/* 606 */             this.delegate.nextToken();
/* 607 */             this.delegate.skipChildren();
/*     */             
/*     */             continue;
/*     */           } 
/* 611 */           if (this._inclusion != TokenFilter.Inclusion.ONLY_INCLUDE_ALL) {
/* 612 */             t = _nextTokenWithBuffering(this._headContext);
/* 613 */             if (t != null) {
/* 614 */               this._currToken = t;
/* 615 */               return t;
/*     */             } 
/*     */           } 
/*     */           continue;
/*     */       } 
/*     */ 
/*     */       
/* 622 */       TokenFilter f = this._itemFilter;
/* 623 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 624 */         return this._currToken = t;
/*     */       }
/* 626 */       if (f != null) {
/* 627 */         f = this._headContext.checkValue(f);
/* 628 */         if ((f == TokenFilter.INCLUDE_ALL || (f != null && f
/* 629 */           .includeValue(this.delegate))) && 
/* 630 */           _verifyAllowedMatches()) {
/* 631 */           return this._currToken = t;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException {
/*     */     while (true) {
/*     */       boolean gotEnd;
/*     */       String name;
/*     */       boolean returnEnd;
/* 647 */       JsonToken t = this.delegate.nextToken();
/* 648 */       if (t == null) {
/* 649 */         return t;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 657 */       switch (t.id()) {
/*     */         case 3:
/* 659 */           f = this._headContext.checkValue(this._itemFilter);
/* 660 */           if (f == null) {
/* 661 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 664 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 665 */             f = f.filterStartArray();
/*     */           }
/* 667 */           this._itemFilter = f;
/* 668 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 669 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 670 */             return _nextBuffered(buffRoot);
/* 671 */           }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*     */             
/* 673 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 674 */             return _nextBuffered(buffRoot);
/*     */           } 
/* 676 */           this._headContext = this._headContext.createChildArrayContext(f, false);
/*     */           continue;
/*     */         
/*     */         case 1:
/* 680 */           f = this._itemFilter;
/* 681 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 682 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 683 */             return t;
/*     */           } 
/* 685 */           if (f == null) {
/* 686 */             this.delegate.skipChildren();
/*     */             
/*     */             continue;
/*     */           } 
/* 690 */           f = this._headContext.checkValue(f);
/* 691 */           if (f == null) {
/* 692 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 695 */           if (f != TokenFilter.INCLUDE_ALL) {
/* 696 */             f = f.filterStartObject();
/*     */           }
/* 698 */           this._itemFilter = f;
/* 699 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 700 */             this._headContext = this._headContext.createChildObjectContext(f, true);
/* 701 */             return _nextBuffered(buffRoot);
/* 702 */           }  if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*     */             
/* 704 */             this._headContext = this._headContext.createChildArrayContext(f, true);
/* 705 */             return _nextBuffered(buffRoot);
/*     */           } 
/* 707 */           this._headContext = this._headContext.createChildObjectContext(f, false);
/*     */           continue;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/*     */         case 4:
/* 715 */           f = this._headContext.getFilter();
/* 716 */           if (f != null && f != TokenFilter.INCLUDE_ALL) {
/* 717 */             f.filterFinishArray();
/*     */           }
/* 719 */           gotEnd = (this._headContext == buffRoot);
/* 720 */           returnEnd = (gotEnd && this._headContext.isStartHandled());
/*     */           
/* 722 */           this._headContext = this._headContext.getParent();
/* 723 */           this._itemFilter = this._headContext.getFilter();
/*     */           
/* 725 */           if (returnEnd) {
/* 726 */             return t;
/*     */           }
/*     */           continue;
/*     */ 
/*     */ 
/*     */         
/*     */         case 5:
/* 733 */           name = this.delegate.getCurrentName();
/* 734 */           f = this._headContext.setFieldName(name);
/* 735 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 736 */             this._itemFilter = f;
/* 737 */             return _nextBuffered(buffRoot);
/*     */           } 
/* 739 */           if (f == null) {
/* 740 */             this.delegate.nextToken();
/* 741 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 744 */           f = f.includeProperty(name);
/* 745 */           if (f == null) {
/* 746 */             this.delegate.nextToken();
/* 747 */             this.delegate.skipChildren();
/*     */             continue;
/*     */           } 
/* 750 */           this._itemFilter = f;
/* 751 */           if (f == TokenFilter.INCLUDE_ALL) {
/* 752 */             if (_verifyAllowedMatches()) {
/* 753 */               return _nextBuffered(buffRoot);
/*     */             }
/*     */ 
/*     */             
/* 757 */             this._itemFilter = this._headContext.setFieldName(name);
/*     */           } 
/*     */           continue;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 764 */       TokenFilter f = this._itemFilter;
/* 765 */       if (f == TokenFilter.INCLUDE_ALL) {
/* 766 */         return _nextBuffered(buffRoot);
/*     */       }
/* 768 */       if (f != null) {
/* 769 */         f = this._headContext.checkValue(f);
/* 770 */         if ((f == TokenFilter.INCLUDE_ALL || (f != null && f
/* 771 */           .includeValue(this.delegate))) && 
/* 772 */           _verifyAllowedMatches()) {
/* 773 */           return _nextBuffered(buffRoot);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JsonToken _nextBuffered(TokenFilterContext buffRoot) throws IOException {
/* 785 */     this._exposedContext = buffRoot;
/* 786 */     TokenFilterContext ctxt = buffRoot;
/* 787 */     JsonToken t = ctxt.nextTokenToRead();
/* 788 */     if (t != null) {
/* 789 */       return t;
/*     */     }
/*     */     
/*     */     while (true) {
/* 793 */       if (ctxt == this._headContext) {
/* 794 */         throw _constructError("Internal error: failed to locate expected buffered tokens");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 801 */       ctxt = this._exposedContext.findChildOf(ctxt);
/* 802 */       this._exposedContext = ctxt;
/* 803 */       if (ctxt == null) {
/* 804 */         throw _constructError("Unexpected problem: chain of filtered context broken");
/*     */       }
/* 806 */       t = this._exposedContext.nextTokenToRead();
/* 807 */       if (t != null) {
/* 808 */         return t;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private final boolean _verifyAllowedMatches() throws IOException {
/* 814 */     if (this._matchCount == 0 || this._allowMultipleMatches) {
/* 815 */       this._matchCount++;
/* 816 */       return true;
/*     */     } 
/* 818 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken nextValue() throws IOException {
/* 824 */     JsonToken t = nextToken();
/* 825 */     if (t == JsonToken.FIELD_NAME) {
/* 826 */       t = nextToken();
/*     */     }
/* 828 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 839 */     if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY)
/*     */     {
/* 841 */       return (JsonParser)this;
/*     */     }
/* 843 */     int open = 1;
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 848 */       JsonToken t = nextToken();
/* 849 */       if (t == null) {
/* 850 */         return (JsonParser)this;
/*     */       }
/* 852 */       if (t.isStructStart()) {
/* 853 */         open++; continue;
/* 854 */       }  if (t.isStructEnd() && 
/* 855 */         --open == 0) {
/* 856 */         return (JsonParser)this;
/*     */       }
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
/*     */   public String getText() throws IOException {
/* 872 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 873 */       return currentName();
/*     */     }
/* 875 */     return this.delegate.getText();
/*     */   }
/*     */   
/*     */   public boolean hasTextCharacters() {
/* 879 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 880 */       return false;
/*     */     }
/* 882 */     return this.delegate.hasTextCharacters();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] getTextCharacters() throws IOException {
/* 888 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 889 */       return currentName().toCharArray();
/*     */     }
/* 891 */     return this.delegate.getTextCharacters();
/*     */   }
/*     */   
/*     */   public int getTextLength() throws IOException {
/* 895 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 896 */       return currentName().length();
/*     */     }
/* 898 */     return this.delegate.getTextLength();
/*     */   }
/*     */   public int getTextOffset() throws IOException {
/* 901 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 902 */       return 0;
/*     */     }
/* 904 */     return this.delegate.getTextOffset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getBigIntegerValue() throws IOException {
/* 914 */     return this.delegate.getBigIntegerValue();
/*     */   }
/*     */   public boolean getBooleanValue() throws IOException {
/* 917 */     return this.delegate.getBooleanValue();
/*     */   }
/*     */   public byte getByteValue() throws IOException {
/* 920 */     return this.delegate.getByteValue();
/*     */   }
/*     */   public short getShortValue() throws IOException {
/* 923 */     return this.delegate.getShortValue();
/*     */   }
/*     */   public BigDecimal getDecimalValue() throws IOException {
/* 926 */     return this.delegate.getDecimalValue();
/*     */   }
/*     */   public double getDoubleValue() throws IOException {
/* 929 */     return this.delegate.getDoubleValue();
/*     */   }
/*     */   public float getFloatValue() throws IOException {
/* 932 */     return this.delegate.getFloatValue();
/*     */   }
/*     */   public int getIntValue() throws IOException {
/* 935 */     return this.delegate.getIntValue();
/*     */   }
/*     */   public long getLongValue() throws IOException {
/* 938 */     return this.delegate.getLongValue();
/*     */   }
/*     */   public JsonParser.NumberType getNumberType() throws IOException {
/* 941 */     return this.delegate.getNumberType();
/*     */   }
/*     */   public Number getNumberValue() throws IOException {
/* 944 */     return this.delegate.getNumberValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt() throws IOException {
/* 952 */     return this.delegate.getValueAsInt();
/* 953 */   } public int getValueAsInt(int defaultValue) throws IOException { return this.delegate.getValueAsInt(defaultValue); }
/* 954 */   public long getValueAsLong() throws IOException { return this.delegate.getValueAsLong(); }
/* 955 */   public long getValueAsLong(long defaultValue) throws IOException { return this.delegate.getValueAsLong(defaultValue); }
/* 956 */   public double getValueAsDouble() throws IOException { return this.delegate.getValueAsDouble(); }
/* 957 */   public double getValueAsDouble(double defaultValue) throws IOException { return this.delegate.getValueAsDouble(defaultValue); }
/* 958 */   public boolean getValueAsBoolean() throws IOException { return this.delegate.getValueAsBoolean(); } public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
/* 959 */     return this.delegate.getValueAsBoolean(defaultValue);
/*     */   }
/*     */   public String getValueAsString() throws IOException {
/* 962 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 963 */       return currentName();
/*     */     }
/* 965 */     return this.delegate.getValueAsString();
/*     */   }
/*     */   public String getValueAsString(String defaultValue) throws IOException {
/* 968 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 969 */       return currentName();
/*     */     }
/* 971 */     return this.delegate.getValueAsString(defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmbeddedObject() throws IOException {
/* 980 */     return this.delegate.getEmbeddedObject();
/* 981 */   } public byte[] getBinaryValue(Base64Variant b64variant) throws IOException { return this.delegate.getBinaryValue(b64variant); }
/* 982 */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException { return this.delegate.readBinaryValue(b64variant, out); } public JsonLocation getTokenLocation() {
/* 983 */     return this.delegate.getTokenLocation();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonStreamContext _filterContext() {
/* 992 */     if (this._exposedContext != null) {
/* 993 */       return this._exposedContext;
/*     */     }
/* 995 */     return this._headContext;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/filter/FilteringParserDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */