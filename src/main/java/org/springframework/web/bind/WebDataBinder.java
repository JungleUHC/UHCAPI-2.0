/*     */ package org.springframework.web.bind;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.PropertyValue;
/*     */ import org.springframework.core.CollectionFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.validation.DataBinder;
/*     */ import org.springframework.web.multipart.MultipartFile;
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
/*     */ public class WebDataBinder
/*     */   extends DataBinder
/*     */ {
/*     */   public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";
/*     */   public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";
/*     */   @Nullable
/*  78 */   private String fieldMarkerPrefix = "_";
/*     */   
/*     */   @Nullable
/*  81 */   private String fieldDefaultPrefix = "!";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bindEmptyMultipartFiles = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebDataBinder(@Nullable Object target) {
/*  94 */     super(target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebDataBinder(@Nullable Object target, String objectName) {
/* 104 */     super(target, objectName);
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
/*     */   public void setFieldMarkerPrefix(@Nullable String fieldMarkerPrefix) {
/* 130 */     this.fieldMarkerPrefix = fieldMarkerPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getFieldMarkerPrefix() {
/* 138 */     return this.fieldMarkerPrefix;
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
/*     */   public void setFieldDefaultPrefix(@Nullable String fieldDefaultPrefix) {
/* 156 */     this.fieldDefaultPrefix = fieldDefaultPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getFieldDefaultPrefix() {
/* 164 */     return this.fieldDefaultPrefix;
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
/*     */   public void setBindEmptyMultipartFiles(boolean bindEmptyMultipartFiles) {
/* 176 */     this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBindEmptyMultipartFiles() {
/* 183 */     return this.bindEmptyMultipartFiles;
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
/*     */   protected void doBind(MutablePropertyValues mpvs) {
/* 195 */     checkFieldDefaults(mpvs);
/* 196 */     checkFieldMarkers(mpvs);
/* 197 */     adaptEmptyArrayIndices(mpvs);
/* 198 */     super.doBind(mpvs);
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
/*     */   protected void checkFieldDefaults(MutablePropertyValues mpvs) {
/* 210 */     String fieldDefaultPrefix = getFieldDefaultPrefix();
/* 211 */     if (fieldDefaultPrefix != null) {
/* 212 */       PropertyValue[] pvArray = mpvs.getPropertyValues();
/* 213 */       for (PropertyValue pv : pvArray) {
/* 214 */         if (pv.getName().startsWith(fieldDefaultPrefix)) {
/* 215 */           String field = pv.getName().substring(fieldDefaultPrefix.length());
/* 216 */           if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
/* 217 */             mpvs.add(field, pv.getValue());
/*     */           }
/* 219 */           mpvs.removePropertyValue(pv);
/*     */         } 
/*     */       } 
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
/*     */   protected void checkFieldMarkers(MutablePropertyValues mpvs) {
/* 237 */     String fieldMarkerPrefix = getFieldMarkerPrefix();
/* 238 */     if (fieldMarkerPrefix != null) {
/* 239 */       PropertyValue[] pvArray = mpvs.getPropertyValues();
/* 240 */       for (PropertyValue pv : pvArray) {
/* 241 */         if (pv.getName().startsWith(fieldMarkerPrefix)) {
/* 242 */           String field = pv.getName().substring(fieldMarkerPrefix.length());
/* 243 */           if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
/* 244 */             Class<?> fieldType = getPropertyAccessor().getPropertyType(field);
/* 245 */             mpvs.add(field, getEmptyValue(field, fieldType));
/*     */           } 
/* 247 */           mpvs.removePropertyValue(pv);
/*     */         } 
/*     */       } 
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
/*     */   protected void adaptEmptyArrayIndices(MutablePropertyValues mpvs) {
/* 262 */     for (PropertyValue pv : mpvs.getPropertyValues()) {
/* 263 */       String name = pv.getName();
/* 264 */       if (name.endsWith("[]")) {
/* 265 */         String field = name.substring(0, name.length() - 2);
/* 266 */         if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
/* 267 */           mpvs.add(field, pv.getValue());
/*     */         }
/* 269 */         mpvs.removePropertyValue(pv);
/*     */       } 
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
/*     */   @Nullable
/*     */   protected Object getEmptyValue(String field, @Nullable Class<?> fieldType) {
/* 284 */     return (fieldType != null) ? getEmptyValue(fieldType) : null;
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
/*     */   @Nullable
/*     */   public Object getEmptyValue(Class<?> fieldType) {
/*     */     try {
/* 304 */       if (boolean.class == fieldType || Boolean.class == fieldType)
/*     */       {
/* 306 */         return Boolean.FALSE;
/*     */       }
/* 308 */       if (fieldType.isArray())
/*     */       {
/* 310 */         return Array.newInstance(fieldType.getComponentType(), 0);
/*     */       }
/* 312 */       if (Collection.class.isAssignableFrom(fieldType)) {
/* 313 */         return CollectionFactory.createCollection(fieldType, 0);
/*     */       }
/* 315 */       if (Map.class.isAssignableFrom(fieldType)) {
/* 316 */         return CollectionFactory.createMap(fieldType, 0);
/*     */       }
/*     */     }
/* 319 */     catch (IllegalArgumentException ex) {
/* 320 */       if (logger.isDebugEnabled()) {
/* 321 */         logger.debug("Failed to create default value - falling back to null: " + ex.getMessage());
/*     */       }
/*     */     } 
/*     */     
/* 325 */     return null;
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
/*     */   protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
/* 340 */     multipartFiles.forEach((key, values) -> {
/*     */           if (values.size() == 1) {
/*     */             MultipartFile value = values.get(0);
/*     */             if (isBindEmptyMultipartFiles() || !value.isEmpty())
/*     */               mpvs.add(key, value); 
/*     */           } else {
/*     */             mpvs.add(key, values);
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/WebDataBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */