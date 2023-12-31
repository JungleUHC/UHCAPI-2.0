/*     */ package com.fasterxml.jackson.databind.jsontype;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import java.io.Closeable;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultBaseTypeLimitingValidator
/*     */   extends PolymorphicTypeValidator
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
/*  31 */     if (isUnsafeBaseType(config, baseType)) {
/*  32 */       return PolymorphicTypeValidator.Validity.DENIED;
/*     */     }
/*     */ 
/*     */     
/*  36 */     return PolymorphicTypeValidator.Validity.INDETERMINATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName) {
/*  43 */     return PolymorphicTypeValidator.Validity.INDETERMINATE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> config, JavaType baseType, JavaType subType) {
/*  50 */     return isSafeSubType(config, baseType, subType) ? PolymorphicTypeValidator.Validity.ALLOWED : PolymorphicTypeValidator.Validity.DENIED;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isUnsafeBaseType(MapperConfig<?> config, JavaType baseType) {
/*  77 */     return UnsafeBaseTypes.instance.isUnsafeBaseType(baseType.getRawClass());
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
/*     */   protected boolean isSafeSubType(MapperConfig<?> config, JavaType baseType, JavaType subType) {
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   private static final class UnsafeBaseTypes
/*     */   {
/*     */     private UnsafeBaseTypes() {
/*  98 */       this.UNSAFE = new HashSet<>();
/*     */ 
/*     */       
/* 101 */       this.UNSAFE.add(Object.class.getName());
/* 102 */       this.UNSAFE.add(Closeable.class.getName());
/* 103 */       this.UNSAFE.add(Serializable.class.getName());
/* 104 */       this.UNSAFE.add(AutoCloseable.class.getName());
/* 105 */       this.UNSAFE.add(Cloneable.class.getName());
/*     */ 
/*     */ 
/*     */       
/* 109 */       this.UNSAFE.add("java.util.logging.Handler");
/* 110 */       this.UNSAFE.add("javax.naming.Referenceable");
/* 111 */       this.UNSAFE.add("javax.sql.DataSource");
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isUnsafeBaseType(Class<?> rawBaseType) {
/* 116 */       return this.UNSAFE.contains(rawBaseType.getName());
/*     */     }
/*     */     
/*     */     public static final UnsafeBaseTypes instance = new UnsafeBaseTypes();
/*     */     private final Set<String> UNSAFE;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/DefaultBaseTypeLimitingValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */