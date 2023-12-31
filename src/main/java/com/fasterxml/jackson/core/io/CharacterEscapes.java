/*    */ package com.fasterxml.jackson.core.io;
/*    */ 
/*    */ import com.fasterxml.jackson.core.SerializableString;
/*    */ import java.io.Serializable;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CharacterEscapes
/*    */   implements Serializable
/*    */ {
/*    */   public static final int ESCAPE_NONE = 0;
/*    */   public static final int ESCAPE_STANDARD = -1;
/*    */   public static final int ESCAPE_CUSTOM = -2;
/*    */   
/*    */   public abstract int[] getEscapeCodesForAscii();
/*    */   
/*    */   public abstract SerializableString getEscapeSequence(int paramInt);
/*    */   
/*    */   public static int[] standardAsciiEscapesForJSON() {
/* 75 */     int[] esc = CharTypes.get7BitOutputEscapes();
/* 76 */     return Arrays.copyOf(esc, esc.length);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/CharacterEscapes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */