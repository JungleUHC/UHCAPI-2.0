/*    */ package org.springframework.core;
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
/*    */ public class DefaultParameterNameDiscoverer
/*    */   extends PrioritizedParameterNameDiscoverer
/*    */ {
/*    */   public DefaultParameterNameDiscoverer() {
/* 44 */     if (KotlinDetector.isKotlinReflectPresent() && !NativeDetector.inNativeImage()) {
/* 45 */       addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
/*    */     }
/* 47 */     addDiscoverer(new StandardReflectionParameterNameDiscoverer());
/* 48 */     addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/DefaultParameterNameDiscoverer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */