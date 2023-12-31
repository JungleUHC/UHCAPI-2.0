/*    */ package org.springframework.beans.factory.support;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.springframework.beans.factory.config.BeanDefinition;
/*    */ import org.springframework.core.io.AbstractResource;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
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
/*    */ class BeanDefinitionResource
/*    */   extends AbstractResource
/*    */ {
/*    */   private final BeanDefinition beanDefinition;
/*    */   
/*    */   public BeanDefinitionResource(BeanDefinition beanDefinition) {
/* 46 */     Assert.notNull(beanDefinition, "BeanDefinition must not be null");
/* 47 */     this.beanDefinition = beanDefinition;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final BeanDefinition getBeanDefinition() {
/* 54 */     return this.beanDefinition;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean exists() {
/* 60 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isReadable() {
/* 65 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream() throws IOException {
/* 70 */     throw new FileNotFoundException("Resource cannot be opened because it points to " + 
/* 71 */         getDescription());
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 76 */     return "BeanDefinition defined in " + this.beanDefinition.getResourceDescription();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 85 */     return (this == other || (other instanceof BeanDefinitionResource && ((BeanDefinitionResource)other).beanDefinition
/* 86 */       .equals(this.beanDefinition)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 94 */     return this.beanDefinition.hashCode();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */