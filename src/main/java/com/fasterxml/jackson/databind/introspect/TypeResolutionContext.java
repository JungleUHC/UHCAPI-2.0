/*    */ package com.fasterxml.jackson.databind.introspect;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.type.TypeBindings;
/*    */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface TypeResolutionContext
/*    */ {
/*    */   JavaType resolveType(Type paramType);
/*    */   
/*    */   public static class Basic
/*    */     implements TypeResolutionContext
/*    */   {
/*    */     private final TypeFactory _typeFactory;
/*    */     private final TypeBindings _bindings;
/*    */     
/*    */     public Basic(TypeFactory tf, TypeBindings b) {
/* 25 */       this._typeFactory = tf;
/* 26 */       this._bindings = b;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public JavaType resolveType(Type type) {
/* 33 */       return this._typeFactory.resolveMemberType(type, this._bindings);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static class Empty
/*    */     implements TypeResolutionContext
/*    */   {
/*    */     private final TypeFactory _typeFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public Empty(TypeFactory tf) {
/* 56 */       this._typeFactory = tf;
/*    */     }
/*    */ 
/*    */     
/*    */     public JavaType resolveType(Type type) {
/* 61 */       return this._typeFactory.constructType(type);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/TypeResolutionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */