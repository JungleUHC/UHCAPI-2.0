/*     */ package org.springframework.http.converter.json;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Type;
/*     */ import javax.json.bind.Jsonb;
/*     */ import javax.json.bind.JsonbBuilder;
/*     */ import javax.json.bind.JsonbConfig;
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
/*     */ public class JsonbHttpMessageConverter
/*     */   extends AbstractJsonHttpMessageConverter
/*     */ {
/*     */   private Jsonb jsonb;
/*     */   
/*     */   public JsonbHttpMessageConverter() {
/*  55 */     this(JsonbBuilder.create());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonbHttpMessageConverter(JsonbConfig config) {
/*  63 */     this.jsonb = JsonbBuilder.create(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonbHttpMessageConverter(Jsonb jsonb) {
/*  71 */     Assert.notNull(jsonb, "A Jsonb instance is required");
/*  72 */     this.jsonb = jsonb;
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
/*     */   public void setJsonb(Jsonb jsonb) {
/*  86 */     Assert.notNull(jsonb, "A Jsonb instance is required");
/*  87 */     this.jsonb = jsonb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jsonb getJsonb() {
/*  94 */     return this.jsonb;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
/* 100 */     return getJsonb().fromJson(reader, resolvedType);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
/* 105 */     if (type instanceof java.lang.reflect.ParameterizedType) {
/* 106 */       getJsonb().toJson(object, type, writer);
/*     */     } else {
/*     */       
/* 109 */       getJsonb().toJson(object, writer);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/json/JsonbHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */