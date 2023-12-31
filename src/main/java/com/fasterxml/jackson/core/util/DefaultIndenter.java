/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultIndenter
/*     */   extends DefaultPrettyPrinter.NopIndenter
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String SYS_LF;
/*     */   
/*     */   static {
/*     */     String lf;
/*     */     try {
/*  22 */       lf = System.getProperty("line.separator");
/*  23 */     } catch (Throwable t) {
/*  24 */       lf = "\n";
/*     */     } 
/*  26 */     SYS_LF = lf;
/*     */   }
/*     */   
/*  29 */   public static final DefaultIndenter SYSTEM_LINEFEED_INSTANCE = new DefaultIndenter("  ", SYS_LF);
/*     */ 
/*     */   
/*     */   private static final int INDENT_LEVELS = 16;
/*     */ 
/*     */   
/*     */   private final char[] indents;
/*     */ 
/*     */   
/*     */   private final int charsPerLevel;
/*     */   
/*     */   private final String eol;
/*     */ 
/*     */   
/*     */   public DefaultIndenter() {
/*  44 */     this("  ", SYS_LF);
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
/*     */   public DefaultIndenter(String indent, String eol) {
/*  56 */     this.charsPerLevel = indent.length();
/*     */     
/*  58 */     this.indents = new char[indent.length() * 16];
/*  59 */     int offset = 0;
/*  60 */     for (int i = 0; i < 16; i++) {
/*  61 */       indent.getChars(0, indent.length(), this.indents, offset);
/*  62 */       offset += indent.length();
/*     */     } 
/*     */     
/*  65 */     this.eol = eol;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultIndenter withLinefeed(String lf) {
/*  70 */     if (lf.equals(this.eol)) {
/*  71 */       return this;
/*     */     }
/*  73 */     return new DefaultIndenter(getIndent(), lf);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultIndenter withIndent(String indent) {
/*  78 */     if (indent.equals(getIndent())) {
/*  79 */       return this;
/*     */     }
/*  81 */     return new DefaultIndenter(indent, this.eol);
/*     */   }
/*     */   
/*     */   public boolean isInline() {
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeIndentation(JsonGenerator jg, int level) throws IOException {
/*  90 */     jg.writeRaw(this.eol);
/*  91 */     if (level > 0) {
/*  92 */       level *= this.charsPerLevel;
/*  93 */       while (level > this.indents.length) {
/*  94 */         jg.writeRaw(this.indents, 0, this.indents.length);
/*  95 */         level -= this.indents.length;
/*     */       } 
/*  97 */       jg.writeRaw(this.indents, 0, level);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getEol() {
/* 102 */     return this.eol;
/*     */   }
/*     */   
/*     */   public String getIndent() {
/* 106 */     return new String(this.indents, 0, this.charsPerLevel);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/DefaultIndenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */