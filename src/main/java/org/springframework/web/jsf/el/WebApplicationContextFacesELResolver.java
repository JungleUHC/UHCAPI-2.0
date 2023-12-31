/*     */ package org.springframework.web.jsf.el;
/*     */ 
/*     */ import java.beans.FeatureDescriptor;
/*     */ import java.util.Iterator;
/*     */ import javax.el.ELContext;
/*     */ import javax.el.ELException;
/*     */ import javax.el.ELResolver;
/*     */ import javax.faces.context.FacesContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeansException;
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
/*     */ public class WebApplicationContextFacesELResolver
/*     */   extends ELResolver
/*     */ {
/*     */   public static final String WEB_APPLICATION_CONTEXT_VARIABLE_NAME = "webApplicationContext";
/*  67 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getValue(ELContext elContext, @Nullable Object base, Object property) throws ELException {
/*  73 */     if (base != null) {
/*  74 */       if (base instanceof WebApplicationContext) {
/*  75 */         WebApplicationContext wac = (WebApplicationContext)base;
/*  76 */         String beanName = property.toString();
/*  77 */         if (this.logger.isTraceEnabled()) {
/*  78 */           this.logger.trace("Attempting to resolve property '" + beanName + "' in root WebApplicationContext");
/*     */         }
/*  80 */         if (wac.containsBean(beanName)) {
/*  81 */           if (this.logger.isDebugEnabled()) {
/*  82 */             this.logger.debug("Successfully resolved property '" + beanName + "' in root WebApplicationContext");
/*     */           }
/*  84 */           elContext.setPropertyResolved(true);
/*     */           try {
/*  86 */             return wac.getBean(beanName);
/*     */           }
/*  88 */           catch (BeansException ex) {
/*  89 */             throw new ELException(ex);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/*  94 */         return null;
/*     */       
/*     */       }
/*     */     
/*     */     }
/*  99 */     else if ("webApplicationContext".equals(property)) {
/* 100 */       elContext.setPropertyResolved(true);
/* 101 */       return getWebApplicationContext(elContext);
/*     */     } 
/*     */ 
/*     */     
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getType(ELContext elContext, @Nullable Object base, Object property) throws ELException {
/* 111 */     if (base != null) {
/* 112 */       if (base instanceof WebApplicationContext) {
/* 113 */         WebApplicationContext wac = (WebApplicationContext)base;
/* 114 */         String beanName = property.toString();
/* 115 */         if (this.logger.isDebugEnabled()) {
/* 116 */           this.logger.debug("Attempting to resolve property '" + beanName + "' in root WebApplicationContext");
/*     */         }
/* 118 */         if (wac.containsBean(beanName)) {
/* 119 */           if (this.logger.isDebugEnabled()) {
/* 120 */             this.logger.debug("Successfully resolved property '" + beanName + "' in root WebApplicationContext");
/*     */           }
/* 122 */           elContext.setPropertyResolved(true);
/*     */           try {
/* 124 */             return wac.getType(beanName);
/*     */           }
/* 126 */           catch (BeansException ex) {
/* 127 */             throw new ELException(ex);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 132 */         return null;
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 137 */     else if ("webApplicationContext".equals(property)) {
/* 138 */       elContext.setPropertyResolved(true);
/* 139 */       return WebApplicationContext.class;
/*     */     } 
/*     */ 
/*     */     
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(ELContext elContext, Object base, Object property, Object value) throws ELException {}
/*     */ 
/*     */   
/*     */   public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException {
/* 152 */     if (base instanceof WebApplicationContext) {
/* 153 */       elContext.setPropertyResolved(true);
/* 154 */       return true;
/*     */     } 
/* 156 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
/* 167 */     return Object.class;
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
/*     */   @Nullable
/*     */   protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
/* 181 */     FacesContext facesContext = FacesContext.getCurrentInstance();
/* 182 */     return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/jsf/el/WebApplicationContextFacesELResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */