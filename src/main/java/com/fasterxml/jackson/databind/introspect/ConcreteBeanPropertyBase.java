/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ConcreteBeanPropertyBase
/*     */   implements BeanProperty, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final PropertyMetadata _metadata;
/*     */   protected transient List<PropertyName> _aliases;
/*     */   
/*     */   protected ConcreteBeanPropertyBase(PropertyMetadata md) {
/*  38 */     this._metadata = (md == null) ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : md;
/*     */   }
/*     */   
/*     */   protected ConcreteBeanPropertyBase(ConcreteBeanPropertyBase src) {
/*  42 */     this._metadata = src._metadata;
/*     */   }
/*     */   
/*     */   public boolean isRequired() {
/*  46 */     return this._metadata.isRequired();
/*     */   }
/*     */   public PropertyMetadata getMetadata() {
/*  49 */     return this._metadata;
/*     */   }
/*     */   public boolean isVirtual() {
/*  52 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr) {
/*  57 */     JsonFormat.Value f = null;
/*  58 */     if (intr != null) {
/*  59 */       AnnotatedMember member = getMember();
/*  60 */       if (member != null) {
/*  61 */         f = intr.findFormat(member);
/*     */       }
/*     */     } 
/*  64 */     if (f == null) {
/*  65 */       f = EMPTY_FORMAT;
/*     */     }
/*  67 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
/*  73 */     JsonFormat.Value v1 = config.getDefaultPropertyFormat(baseType);
/*  74 */     JsonFormat.Value v2 = null;
/*  75 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/*  76 */     if (intr != null) {
/*  77 */       AnnotatedMember member = getMember();
/*  78 */       if (member != null) {
/*  79 */         v2 = intr.findFormat(member);
/*     */       }
/*     */     } 
/*  82 */     if (v1 == null) {
/*  83 */       return (v2 == null) ? EMPTY_FORMAT : v2;
/*     */     }
/*  85 */     return (v2 == null) ? v1 : v1.withOverrides(v2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
/*  91 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/*  92 */     AnnotatedMember member = getMember();
/*  93 */     if (member == null) {
/*  94 */       JsonInclude.Value def = config.getDefaultPropertyInclusion(baseType);
/*  95 */       return def;
/*     */     } 
/*  97 */     JsonInclude.Value v0 = config.getDefaultInclusion(baseType, member.getRawType());
/*  98 */     if (intr == null) {
/*  99 */       return v0;
/*     */     }
/* 101 */     JsonInclude.Value v = intr.findPropertyInclusion(member);
/* 102 */     if (v0 == null) {
/* 103 */       return v;
/*     */     }
/* 105 */     return v0.withOverrides(v);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PropertyName> findAliases(MapperConfig<?> config) {
/* 111 */     List<PropertyName> aliases = this._aliases;
/* 112 */     if (aliases == null) {
/* 113 */       AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 114 */       if (intr != null) {
/* 115 */         AnnotatedMember member = getMember();
/* 116 */         if (member != null) {
/* 117 */           aliases = intr.findPropertyAliases(member);
/*     */         }
/*     */       } 
/* 120 */       if (aliases == null) {
/* 121 */         aliases = Collections.emptyList();
/*     */       }
/* 123 */       this._aliases = aliases;
/*     */     } 
/* 125 */     return aliases;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/ConcreteBeanPropertyBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */