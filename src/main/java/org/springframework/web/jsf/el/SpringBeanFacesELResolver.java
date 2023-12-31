/*     */ package org.springframework.web.jsf.el;
/*     */ 
/*     */ import java.beans.FeatureDescriptor;
/*     */ import java.util.Iterator;
/*     */ import javax.el.ELContext;
/*     */ import javax.el.ELException;
/*     */ import javax.el.ELResolver;
/*     */ import javax.el.PropertyNotWritableException;
/*     */ import javax.faces.context.FacesContext;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ import org.springframework.web.jsf.FacesContextUtils;
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
/*     */ public class SpringBeanFacesELResolver
/*     */   extends ELResolver
/*     */ {
/*     */   @Nullable
/*     */   public Object getValue(ELContext elContext, @Nullable Object base, Object property) throws ELException {
/*  77 */     if (base == null) {
/*  78 */       String beanName = property.toString();
/*  79 */       WebApplicationContext wac = getWebApplicationContext(elContext);
/*  80 */       if (wac.containsBean(beanName)) {
/*  81 */         elContext.setPropertyResolved(true);
/*  82 */         return wac.getBean(beanName);
/*     */       } 
/*     */     } 
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getType(ELContext elContext, @Nullable Object base, Object property) throws ELException {
/*  91 */     if (base == null) {
/*  92 */       String beanName = property.toString();
/*  93 */       WebApplicationContext wac = getWebApplicationContext(elContext);
/*  94 */       if (wac.containsBean(beanName)) {
/*  95 */         elContext.setPropertyResolved(true);
/*  96 */         return wac.getType(beanName);
/*     */       } 
/*     */     } 
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(ELContext elContext, @Nullable Object base, Object property, Object value) throws ELException {
/* 104 */     if (base == null) {
/* 105 */       String beanName = property.toString();
/* 106 */       WebApplicationContext wac = getWebApplicationContext(elContext);
/* 107 */       if (wac.containsBean(beanName)) {
/* 108 */         if (value == wac.getBean(beanName)) {
/*     */           
/* 110 */           elContext.setPropertyResolved(true);
/*     */         } else {
/*     */           
/* 113 */           throw new PropertyNotWritableException("Variable '" + beanName + "' refers to a Spring bean which by definition is not writable");
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly(ELContext elContext, @Nullable Object base, Object property) throws ELException {
/* 122 */     if (base == null) {
/* 123 */       String beanName = property.toString();
/* 124 */       WebApplicationContext wac = getWebApplicationContext(elContext);
/* 125 */       if (wac.containsBean(beanName)) {
/* 126 */         return true;
/*     */       }
/*     */     } 
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, @Nullable Object base) {
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getCommonPropertyType(ELContext elContext, @Nullable Object base) {
/* 140 */     return Object.class;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
/* 151 */     FacesContext facesContext = FacesContext.getCurrentInstance();
/* 152 */     return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/jsf/el/SpringBeanFacesELResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */