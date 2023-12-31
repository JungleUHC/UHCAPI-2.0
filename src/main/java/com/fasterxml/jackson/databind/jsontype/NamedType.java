/*    */ package com.fasterxml.jackson.databind.jsontype;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NamedType
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final Class<?> _class;
/*    */   protected final int _hashCode;
/*    */   protected String _name;
/*    */   
/*    */   public NamedType(Class<?> c) {
/* 18 */     this(c, null);
/*    */   }
/*    */   public NamedType(Class<?> c, String name) {
/* 21 */     this._class = c;
/* 22 */     this._hashCode = c.getName().hashCode() + ((name == null) ? 0 : name.hashCode());
/* 23 */     setName(name);
/*    */   }
/*    */   
/* 26 */   public Class<?> getType() { return this._class; }
/* 27 */   public String getName() { return this._name; } public void setName(String name) {
/* 28 */     this._name = (name == null || name.isEmpty()) ? null : name;
/*    */   } public boolean hasName() {
/* 30 */     return (this._name != null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 37 */     if (o == this) return true; 
/* 38 */     if (o == null) return false; 
/* 39 */     if (o.getClass() != getClass()) return false; 
/* 40 */     NamedType other = (NamedType)o;
/* 41 */     return (this._class == other._class && 
/* 42 */       Objects.equals(this._name, other._name));
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 46 */     return this._hashCode;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 50 */     return "[NamedType, class " + this._class.getName() + ", name: " + ((this._name == null) ? "null" : ("'" + this._name + "'")) + "]";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/NamedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */