/*     */ package org.springframework.web.util.pattern;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class InternalPathPatternParser
/*     */ {
/*     */   private final PathPatternParser parser;
/*  39 */   private char[] pathPatternData = new char[0];
/*     */ 
/*     */ 
/*     */   
/*     */   private int pathPatternLength;
/*     */ 
/*     */   
/*     */   int pos;
/*     */ 
/*     */   
/*     */   private int singleCharWildcardCount;
/*     */ 
/*     */   
/*     */   private boolean wildcard = false;
/*     */ 
/*     */   
/*     */   private boolean isCaptureTheRestVariable = false;
/*     */ 
/*     */   
/*     */   private boolean insideVariableCapture = false;
/*     */ 
/*     */   
/*  61 */   private int variableCaptureCount = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private int pathElementStart;
/*     */ 
/*     */ 
/*     */   
/*     */   private int variableCaptureStart;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private List<String> capturedVariableNames;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PathElement headPE;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PathElement currentPE;
/*     */ 
/*     */ 
/*     */   
/*     */   InternalPathPatternParser(PathPatternParser parentParser) {
/*  87 */     this.parser = parentParser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathPattern parse(String pathPattern) throws PatternParseException {
/*  95 */     Assert.notNull(pathPattern, "Path pattern must not be null");
/*     */     
/*  97 */     this.pathPatternData = pathPattern.toCharArray();
/*  98 */     this.pathPatternLength = this.pathPatternData.length;
/*  99 */     this.headPE = null;
/* 100 */     this.currentPE = null;
/* 101 */     this.capturedVariableNames = null;
/* 102 */     this.pathElementStart = -1;
/* 103 */     this.pos = 0;
/* 104 */     resetPathElementState();
/*     */     
/* 106 */     while (this.pos < this.pathPatternLength) {
/* 107 */       char ch = this.pathPatternData[this.pos];
/* 108 */       char separator = this.parser.getPathOptions().separator();
/* 109 */       if (ch == separator) {
/* 110 */         if (this.pathElementStart != -1) {
/* 111 */           pushPathElement(createPathElement());
/*     */         }
/* 113 */         if (peekDoubleWildcard()) {
/* 114 */           pushPathElement(new WildcardTheRestPathElement(this.pos, separator));
/* 115 */           this.pos += 2;
/*     */         } else {
/*     */           
/* 118 */           pushPathElement(new SeparatorPathElement(this.pos, separator));
/*     */         } 
/*     */       } else {
/*     */         
/* 122 */         if (this.pathElementStart == -1) {
/* 123 */           this.pathElementStart = this.pos;
/*     */         }
/* 125 */         if (ch == '?') {
/* 126 */           this.singleCharWildcardCount++;
/*     */         }
/* 128 */         else if (ch == '{') {
/* 129 */           if (this.insideVariableCapture) {
/* 130 */             throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_NESTED_CAPTURE, new Object[0]);
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 138 */           this.insideVariableCapture = true;
/* 139 */           this.variableCaptureStart = this.pos;
/*     */         }
/* 141 */         else if (ch == '}') {
/* 142 */           if (!this.insideVariableCapture) {
/* 143 */             throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_OPEN_CAPTURE, new Object[0]);
/*     */           }
/*     */           
/* 146 */           this.insideVariableCapture = false;
/* 147 */           if (this.isCaptureTheRestVariable && this.pos + 1 < this.pathPatternLength) {
/* 148 */             throw new PatternParseException(this.pos + 1, this.pathPatternData, PatternParseException.PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST, new Object[0]);
/*     */           }
/*     */           
/* 151 */           this.variableCaptureCount++;
/*     */         }
/* 153 */         else if (ch == ':') {
/* 154 */           if (this.insideVariableCapture && !this.isCaptureTheRestVariable) {
/* 155 */             skipCaptureRegex();
/* 156 */             this.insideVariableCapture = false;
/* 157 */             this.variableCaptureCount++;
/*     */           }
/*     */         
/* 160 */         } else if (ch == '*') {
/* 161 */           if (this.insideVariableCapture && this.variableCaptureStart == this.pos - 1) {
/* 162 */             this.isCaptureTheRestVariable = true;
/*     */           }
/* 164 */           this.wildcard = true;
/*     */         } 
/*     */         
/* 167 */         if (this.insideVariableCapture) {
/* 168 */           if (this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) == this.pos && 
/* 169 */             !Character.isJavaIdentifierStart(ch)) {
/* 170 */             throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, new Object[] {
/*     */                   
/* 172 */                   Character.toString(ch)
/*     */                 });
/*     */           }
/* 175 */           if (this.pos > this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) && 
/* 176 */             !Character.isJavaIdentifierPart(ch) && ch != '-')
/* 177 */             throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR, new Object[] {
/*     */                   
/* 179 */                   Character.toString(ch)
/*     */                 }); 
/*     */         } 
/*     */       } 
/* 183 */       this.pos++;
/*     */     } 
/* 185 */     if (this.pathElementStart != -1) {
/* 186 */       pushPathElement(createPathElement());
/*     */     }
/* 188 */     return new PathPattern(pathPattern, this.parser, this.headPE);
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
/*     */   private void skipCaptureRegex() {
/* 201 */     int regexStart = ++this.pos;
/* 202 */     int curlyBracketDepth = 0;
/* 203 */     boolean previousBackslash = false;
/*     */     
/* 205 */     while (this.pos < this.pathPatternLength) {
/* 206 */       char ch = this.pathPatternData[this.pos];
/* 207 */       if (ch == '\\' && !previousBackslash) {
/* 208 */         this.pos++;
/* 209 */         previousBackslash = true;
/*     */         continue;
/*     */       } 
/* 212 */       if (ch == '{' && !previousBackslash) {
/* 213 */         curlyBracketDepth++;
/*     */       }
/* 215 */       else if (ch == '}' && !previousBackslash) {
/* 216 */         if (curlyBracketDepth == 0) {
/* 217 */           if (regexStart == this.pos) {
/* 218 */             throw new PatternParseException(regexStart, this.pathPatternData, PatternParseException.PatternMessage.MISSING_REGEX_CONSTRAINT, new Object[0]);
/*     */           }
/*     */           
/*     */           return;
/*     */         } 
/* 223 */         curlyBracketDepth--;
/*     */       } 
/* 225 */       if (ch == this.parser.getPathOptions().separator() && !previousBackslash) {
/* 226 */         throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
/*     */       }
/*     */       
/* 229 */       this.pos++;
/* 230 */       previousBackslash = false;
/*     */     } 
/*     */     
/* 233 */     throw new PatternParseException(this.pos - 1, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean peekDoubleWildcard() {
/* 242 */     if (this.pos + 2 >= this.pathPatternLength) {
/* 243 */       return false;
/*     */     }
/* 245 */     if (this.pathPatternData[this.pos + 1] != '*' || this.pathPatternData[this.pos + 2] != '*') {
/* 246 */       return false;
/*     */     }
/* 248 */     char separator = this.parser.getPathOptions().separator();
/* 249 */     if (this.pos + 3 < this.pathPatternLength && this.pathPatternData[this.pos + 3] == separator) {
/* 250 */       throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST, new Object[0]);
/*     */     }
/*     */     
/* 253 */     return (this.pos + 3 == this.pathPatternLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void pushPathElement(PathElement newPathElement) {
/* 261 */     if (newPathElement instanceof CaptureTheRestPathElement) {
/*     */ 
/*     */       
/* 264 */       if (this.currentPE == null) {
/* 265 */         this.headPE = newPathElement;
/* 266 */         this.currentPE = newPathElement;
/*     */       }
/* 268 */       else if (this.currentPE instanceof SeparatorPathElement) {
/* 269 */         PathElement peBeforeSeparator = this.currentPE.prev;
/* 270 */         if (peBeforeSeparator == null) {
/*     */           
/* 272 */           this.headPE = newPathElement;
/* 273 */           newPathElement.prev = null;
/*     */         } else {
/*     */           
/* 276 */           peBeforeSeparator.next = newPathElement;
/* 277 */           newPathElement.prev = peBeforeSeparator;
/*     */         } 
/* 279 */         this.currentPE = newPathElement;
/*     */       } else {
/*     */         
/* 282 */         throw new IllegalStateException("Expected SeparatorPathElement but was " + this.currentPE);
/*     */       }
/*     */     
/*     */     }
/* 286 */     else if (this.headPE == null) {
/* 287 */       this.headPE = newPathElement;
/* 288 */       this.currentPE = newPathElement;
/*     */     }
/* 290 */     else if (this.currentPE != null) {
/* 291 */       this.currentPE.next = newPathElement;
/* 292 */       newPathElement.prev = this.currentPE;
/* 293 */       this.currentPE = newPathElement;
/*     */     } 
/*     */ 
/*     */     
/* 297 */     resetPathElementState();
/*     */   }
/*     */   
/*     */   private char[] getPathElementText() {
/* 301 */     char[] pathElementText = new char[this.pos - this.pathElementStart];
/* 302 */     System.arraycopy(this.pathPatternData, this.pathElementStart, pathElementText, 0, this.pos - this.pathElementStart);
/*     */     
/* 304 */     return pathElementText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PathElement createPathElement() {
/* 313 */     if (this.insideVariableCapture) {
/* 314 */       throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
/*     */     }
/*     */     
/* 317 */     PathElement newPE = null;
/* 318 */     char separator = this.parser.getPathOptions().separator();
/*     */     
/* 320 */     if (this.variableCaptureCount > 0) {
/* 321 */       if (this.variableCaptureCount == 1 && this.pathElementStart == this.variableCaptureStart && this.pathPatternData[this.pos - 1] == '}') {
/*     */         
/* 323 */         if (this.isCaptureTheRestVariable) {
/*     */ 
/*     */           
/* 326 */           newPE = new CaptureTheRestPathElement(this.pathElementStart, getPathElementText(), separator);
/*     */         } else {
/*     */ 
/*     */           
/*     */           try {
/*     */             
/* 332 */             newPE = new CaptureVariablePathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), separator);
/*     */           }
/* 334 */           catch (PatternSyntaxException pse) {
/* 335 */             throw new PatternParseException(pse, 
/* 336 */                 findRegexStart(this.pathPatternData, this.pathElementStart) + pse.getIndex(), this.pathPatternData, PatternParseException.PatternMessage.REGEX_PATTERN_SYNTAX_EXCEPTION, new Object[0]);
/*     */           } 
/*     */           
/* 339 */           recordCapturedVariable(this.pathElementStart, ((CaptureVariablePathElement)newPE)
/* 340 */               .getVariableName());
/*     */         } 
/*     */       } else {
/*     */         
/* 344 */         if (this.isCaptureTheRestVariable) {
/* 345 */           throw new PatternParseException(this.pathElementStart, this.pathPatternData, PatternParseException.PatternMessage.CAPTURE_ALL_IS_STANDALONE_CONSTRUCT, new Object[0]);
/*     */         }
/*     */ 
/*     */         
/* 349 */         RegexPathElement newRegexSection = new RegexPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, separator);
/*     */         
/* 351 */         for (String variableName : newRegexSection.getVariableNames()) {
/* 352 */           recordCapturedVariable(this.pathElementStart, variableName);
/*     */         }
/* 354 */         newPE = newRegexSection;
/*     */       }
/*     */     
/*     */     }
/* 358 */     else if (this.wildcard) {
/* 359 */       if (this.pos - 1 == this.pathElementStart) {
/* 360 */         newPE = new WildcardPathElement(this.pathElementStart, separator);
/*     */       }
/*     */       else {
/*     */         
/* 364 */         newPE = new RegexPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, separator);
/*     */       }
/*     */     
/* 367 */     } else if (this.singleCharWildcardCount != 0) {
/*     */       
/* 369 */       newPE = new SingleCharWildcardedPathElement(this.pathElementStart, getPathElementText(), this.singleCharWildcardCount, this.parser.isCaseSensitive(), separator);
/*     */     }
/*     */     else {
/*     */       
/* 373 */       newPE = new LiteralPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), separator);
/*     */     } 
/*     */ 
/*     */     
/* 377 */     return newPE;
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
/*     */   private int findRegexStart(char[] data, int offset) {
/* 389 */     int pos = offset;
/* 390 */     while (pos < data.length) {
/* 391 */       if (data[pos] == ':') {
/* 392 */         return pos + 1;
/*     */       }
/* 394 */       pos++;
/*     */     } 
/* 396 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resetPathElementState() {
/* 403 */     this.pathElementStart = -1;
/* 404 */     this.singleCharWildcardCount = 0;
/* 405 */     this.insideVariableCapture = false;
/* 406 */     this.variableCaptureCount = 0;
/* 407 */     this.wildcard = false;
/* 408 */     this.isCaptureTheRestVariable = false;
/* 409 */     this.variableCaptureStart = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void recordCapturedVariable(int pos, String variableName) {
/* 416 */     if (this.capturedVariableNames == null) {
/* 417 */       this.capturedVariableNames = new ArrayList<>();
/*     */     }
/* 419 */     if (this.capturedVariableNames.contains(variableName)) {
/* 420 */       throw new PatternParseException(pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_DOUBLE_CAPTURE, new Object[] { variableName });
/*     */     }
/*     */     
/* 423 */     this.capturedVariableNames.add(variableName);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/InternalPathPatternParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */