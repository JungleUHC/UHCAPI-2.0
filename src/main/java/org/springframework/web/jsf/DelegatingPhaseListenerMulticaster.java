/*     */ package org.springframework.web.jsf;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import javax.faces.context.FacesContext;
/*     */ import javax.faces.event.PhaseEvent;
/*     */ import javax.faces.event.PhaseId;
/*     */ import javax.faces.event.PhaseListener;
/*     */ import org.springframework.beans.factory.BeanFactoryUtils;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.web.context.WebApplicationContext;
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
/*     */ public class DelegatingPhaseListenerMulticaster
/*     */   implements PhaseListener
/*     */ {
/*     */   public PhaseId getPhaseId() {
/*  68 */     return PhaseId.ANY_PHASE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforePhase(PhaseEvent event) {
/*  73 */     for (PhaseListener listener : getDelegates(event.getFacesContext())) {
/*  74 */       listener.beforePhase(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void afterPhase(PhaseEvent event) {
/*  80 */     for (PhaseListener listener : getDelegates(event.getFacesContext())) {
/*  81 */       listener.afterPhase(event);
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
/*     */   protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
/*  94 */     ListableBeanFactory bf = getBeanFactory(facesContext);
/*  95 */     return BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, PhaseListener.class, true, false).values();
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
/*     */   protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
/* 108 */     return (ListableBeanFactory)getWebApplicationContext(facesContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
/* 119 */     return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/jsf/DelegatingPhaseListenerMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */