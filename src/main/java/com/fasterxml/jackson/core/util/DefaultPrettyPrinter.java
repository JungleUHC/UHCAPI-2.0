/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.io.SerializedString;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultPrettyPrinter
/*     */   implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  31 */   public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   protected Indenter _arrayIndenter = FixedSpaceIndenter.instance;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   protected Indenter _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SerializableString _rootSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _spacesInObjectEntries = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient int _nesting;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Separators _separators;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _objectFieldValueSeparatorWithSpaces;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter() {
/* 104 */     this((SerializableString)DEFAULT_ROOT_VALUE_SEPARATOR);
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
/*     */   public DefaultPrettyPrinter(String rootSeparator) {
/* 117 */     this((rootSeparator == null) ? null : (SerializableString)new SerializedString(rootSeparator));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter(SerializableString rootSeparator) {
/* 127 */     this._rootSeparator = rootSeparator;
/* 128 */     withSeparators(DEFAULT_SEPARATORS);
/*     */   }
/*     */   
/*     */   public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
/* 132 */     this(base, base._rootSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
/* 138 */     this._arrayIndenter = base._arrayIndenter;
/* 139 */     this._objectIndenter = base._objectIndenter;
/* 140 */     this._spacesInObjectEntries = base._spacesInObjectEntries;
/* 141 */     this._nesting = base._nesting;
/*     */     
/* 143 */     this._separators = base._separators;
/* 144 */     this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
/*     */     
/* 146 */     this._rootSeparator = rootSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
/* 151 */     if (this._rootSeparator == rootSeparator || (rootSeparator != null && rootSeparator
/* 152 */       .equals(this._rootSeparator))) {
/* 153 */       return this;
/*     */     }
/* 155 */     return new DefaultPrettyPrinter(this, rootSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
/* 166 */     return withRootSeparator((rootSeparator == null) ? null : (SerializableString)new SerializedString(rootSeparator));
/*     */   }
/*     */   
/*     */   public void indentArraysWith(Indenter i) {
/* 170 */     this._arrayIndenter = (i == null) ? NopIndenter.instance : i;
/*     */   }
/*     */   
/*     */   public void indentObjectsWith(Indenter i) {
/* 174 */     this._objectIndenter = (i == null) ? NopIndenter.instance : i;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
/* 179 */     if (i == null) {
/* 180 */       i = NopIndenter.instance;
/*     */     }
/* 182 */     if (this._arrayIndenter == i) {
/* 183 */       return this;
/*     */     }
/* 185 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 186 */     pp._arrayIndenter = i;
/* 187 */     return pp;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
/* 192 */     if (i == null) {
/* 193 */       i = NopIndenter.instance;
/*     */     }
/* 195 */     if (this._objectIndenter == i) {
/* 196 */       return this;
/*     */     }
/* 198 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 199 */     pp._objectIndenter = i;
/* 200 */     return pp;
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
/*     */   public DefaultPrettyPrinter withSpacesInObjectEntries() {
/* 214 */     return _withSpaces(true);
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
/*     */   public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
/* 228 */     return _withSpaces(false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected DefaultPrettyPrinter _withSpaces(boolean state) {
/* 233 */     if (this._spacesInObjectEntries == state) {
/* 234 */       return this;
/*     */     }
/* 236 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 237 */     pp._spacesInObjectEntries = state;
/* 238 */     return pp;
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
/*     */   public DefaultPrettyPrinter withSeparators(Separators separators) {
/* 251 */     this._separators = separators;
/* 252 */     this._objectFieldValueSeparatorWithSpaces = " " + separators.getObjectFieldValueSeparator() + " ";
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter createInstance() {
/* 264 */     if (getClass() != DefaultPrettyPrinter.class) {
/* 265 */       throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName() + " does not override method; it has to");
/*     */     }
/*     */     
/* 268 */     return new DefaultPrettyPrinter(this);
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
/*     */   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
/* 280 */     if (this._rootSeparator != null) {
/* 281 */       g.writeRaw(this._rootSeparator);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartObject(JsonGenerator g) throws IOException {
/* 288 */     g.writeRaw('{');
/* 289 */     if (!this._objectIndenter.isInline()) {
/* 290 */       this._nesting++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeObjectEntries(JsonGenerator g) throws IOException {
/* 297 */     this._objectIndenter.writeIndentation(g, this._nesting);
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
/*     */   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
/* 312 */     if (this._spacesInObjectEntries) {
/* 313 */       g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
/*     */     } else {
/* 315 */       g.writeRaw(this._separators.getObjectFieldValueSeparator());
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
/*     */   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
/* 331 */     g.writeRaw(this._separators.getObjectEntrySeparator());
/* 332 */     this._objectIndenter.writeIndentation(g, this._nesting);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
/* 338 */     if (!this._objectIndenter.isInline()) {
/* 339 */       this._nesting--;
/*     */     }
/* 341 */     if (nrOfEntries > 0) {
/* 342 */       this._objectIndenter.writeIndentation(g, this._nesting);
/*     */     } else {
/* 344 */       g.writeRaw(' ');
/*     */     } 
/* 346 */     g.writeRaw('}');
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartArray(JsonGenerator g) throws IOException {
/* 352 */     if (!this._arrayIndenter.isInline()) {
/* 353 */       this._nesting++;
/*     */     }
/* 355 */     g.writeRaw('[');
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeArrayValues(JsonGenerator g) throws IOException {
/* 360 */     this._arrayIndenter.writeIndentation(g, this._nesting);
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
/*     */   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
/* 375 */     g.writeRaw(this._separators.getArrayValueSeparator());
/* 376 */     this._arrayIndenter.writeIndentation(g, this._nesting);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
/* 382 */     if (!this._arrayIndenter.isInline()) {
/* 383 */       this._nesting--;
/*     */     }
/* 385 */     if (nrOfValues > 0) {
/* 386 */       this._arrayIndenter.writeIndentation(g, this._nesting);
/*     */     } else {
/* 388 */       g.writeRaw(' ');
/*     */     } 
/* 390 */     g.writeRaw(']');
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Indenter
/*     */   {
/*     */     void writeIndentation(JsonGenerator param1JsonGenerator, int param1Int) throws IOException;
/*     */ 
/*     */     
/*     */     boolean isInline();
/*     */   }
/*     */   
/*     */   public static class NopIndenter
/*     */     implements Indenter, Serializable
/*     */   {
/* 405 */     public static final NopIndenter instance = new NopIndenter();
/*     */ 
/*     */     
/*     */     public void writeIndentation(JsonGenerator g, int level) throws IOException {}
/*     */     
/*     */     public boolean isInline() {
/* 411 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class FixedSpaceIndenter
/*     */     extends NopIndenter
/*     */   {
/* 421 */     public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();
/*     */ 
/*     */ 
/*     */     
/*     */     public void writeIndentation(JsonGenerator g, int level) throws IOException {
/* 426 */       g.writeRaw(' ');
/*     */     }
/*     */     
/*     */     public boolean isInline() {
/* 430 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/DefaultPrettyPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */