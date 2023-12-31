/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import org.springframework.beans.BeanMetadataElement;
/*     */ import org.springframework.beans.factory.BeanFactoryUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanDefinitionHolder
/*     */   implements BeanMetadataElement
/*     */ {
/*     */   private final BeanDefinition beanDefinition;
/*     */   private final String beanName;
/*     */   @Nullable
/*     */   private final String[] aliases;
/*     */   
/*     */   public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
/*  56 */     this(beanDefinition, beanName, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
/*  66 */     Assert.notNull(beanDefinition, "BeanDefinition must not be null");
/*  67 */     Assert.notNull(beanName, "Bean name must not be null");
/*  68 */     this.beanDefinition = beanDefinition;
/*  69 */     this.beanName = beanName;
/*  70 */     this.aliases = aliases;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
/*  81 */     Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
/*  82 */     this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
/*  83 */     this.beanName = beanDefinitionHolder.getBeanName();
/*  84 */     this.aliases = beanDefinitionHolder.getAliases();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinition getBeanDefinition() {
/*  92 */     return this.beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBeanName() {
/*  99 */     return this.beanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getAliases() {
/* 108 */     return this.aliases;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSource() {
/* 118 */     return this.beanDefinition.getSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchesName(@Nullable String candidateName) {
/* 126 */     return (candidateName != null && (candidateName.equals(this.beanName) || candidateName
/* 127 */       .equals(BeanFactoryUtils.transformedBeanName(this.beanName)) || 
/* 128 */       ObjectUtils.containsElement((Object[])this.aliases, candidateName)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortDescription() {
/* 138 */     if (this.aliases == null) {
/* 139 */       return "Bean definition with name '" + this.beanName + "'";
/*     */     }
/* 141 */     return "Bean definition with name '" + this.beanName + "' and aliases [" + StringUtils.arrayToCommaDelimitedString((Object[])this.aliases) + ']';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLongDescription() {
/* 151 */     return getShortDescription() + ": " + this.beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 162 */     return getLongDescription();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 168 */     if (this == other) {
/* 169 */       return true;
/*     */     }
/* 171 */     if (!(other instanceof BeanDefinitionHolder)) {
/* 172 */       return false;
/*     */     }
/* 174 */     BeanDefinitionHolder otherHolder = (BeanDefinitionHolder)other;
/* 175 */     return (this.beanDefinition.equals(otherHolder.beanDefinition) && this.beanName
/* 176 */       .equals(otherHolder.beanName) && 
/* 177 */       ObjectUtils.nullSafeEquals(this.aliases, otherHolder.aliases));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 182 */     int hashCode = this.beanDefinition.hashCode();
/* 183 */     hashCode = 29 * hashCode + this.beanName.hashCode();
/* 184 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object[])this.aliases);
/* 185 */     return hashCode;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/BeanDefinitionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */