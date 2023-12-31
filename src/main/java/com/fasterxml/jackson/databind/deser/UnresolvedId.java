/*    */ package com.fasterxml.jackson.databind.deser;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonLocation;
/*    */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnresolvedId
/*    */ {
/*    */   private final Object _id;
/*    */   private final JsonLocation _location;
/*    */   private final Class<?> _type;
/*    */   
/*    */   public UnresolvedId(Object id, Class<?> type, JsonLocation where) {
/* 17 */     this._id = id;
/* 18 */     this._type = type;
/* 19 */     this._location = where;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getId() {
/* 25 */     return this._id;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getType() {
/* 30 */     return this._type; } public JsonLocation getLocation() {
/* 31 */     return this._location;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 35 */     return String.format("Object id [%s] (for %s) at %s", new Object[] { this._id, 
/* 36 */           ClassUtil.nameOf(this._type), this._location });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/UnresolvedId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */