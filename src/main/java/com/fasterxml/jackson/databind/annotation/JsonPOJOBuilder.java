/*    */ package com.fasterxml.jackson.databind.annotation;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JacksonAnnotation;
/*    */ import java.lang.annotation.ElementType;
/*    */ import java.lang.annotation.Retention;
/*    */ import java.lang.annotation.RetentionPolicy;
/*    */ import java.lang.annotation.Target;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
/*    */ @Retention(RetentionPolicy.RUNTIME)
/*    */ @JacksonAnnotation
/*    */ public @interface JsonPOJOBuilder
/*    */ {
/*    */   public static final String DEFAULT_BUILD_METHOD = "build";
/*    */   public static final String DEFAULT_WITH_PREFIX = "with";
/*    */   
/*    */   String buildMethodName() default "build";
/*    */   
/*    */   String withPrefix() default "with";
/*    */   
/*    */   public static class Value
/*    */   {
/*    */     public final String buildMethodName;
/*    */     public final String withPrefix;
/*    */     
/*    */     public Value(JsonPOJOBuilder ann) {
/* 87 */       this(ann.buildMethodName(), ann.withPrefix());
/*    */     }
/*    */ 
/*    */     
/*    */     public Value(String buildMethodName, String withPrefix) {
/* 92 */       this.buildMethodName = buildMethodName;
/* 93 */       this.withPrefix = withPrefix;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/annotation/JsonPOJOBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */