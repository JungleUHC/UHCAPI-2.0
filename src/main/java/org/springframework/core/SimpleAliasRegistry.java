/*     */ package org.springframework.core;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.util.StringValueResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleAliasRegistry
/*     */   implements AliasRegistry
/*     */ {
/*  47 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*  50 */   private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerAlias(String name, String alias) {
/*  55 */     Assert.hasText(name, "'name' must not be empty");
/*  56 */     Assert.hasText(alias, "'alias' must not be empty");
/*  57 */     synchronized (this.aliasMap) {
/*  58 */       if (alias.equals(name)) {
/*  59 */         this.aliasMap.remove(alias);
/*  60 */         if (this.logger.isDebugEnabled()) {
/*  61 */           this.logger.debug("Alias definition '" + alias + "' ignored since it points to same name");
/*     */         }
/*     */       } else {
/*     */         
/*  65 */         String registeredName = this.aliasMap.get(alias);
/*  66 */         if (registeredName != null) {
/*  67 */           if (registeredName.equals(name)) {
/*     */             return;
/*     */           }
/*     */           
/*  71 */           if (!allowAliasOverriding()) {
/*  72 */             throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
/*     */           }
/*     */           
/*  75 */           if (this.logger.isDebugEnabled()) {
/*  76 */             this.logger.debug("Overriding alias '" + alias + "' definition for registered name '" + registeredName + "' with new target name '" + name + "'");
/*     */           }
/*     */         } 
/*     */         
/*  80 */         checkForAliasCircle(name, alias);
/*  81 */         this.aliasMap.put(alias, name);
/*  82 */         if (this.logger.isTraceEnabled()) {
/*  83 */           this.logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean allowAliasOverriding() {
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasAlias(String name, String alias) {
/* 104 */     String registeredName = this.aliasMap.get(alias);
/* 105 */     return (ObjectUtils.nullSafeEquals(registeredName, name) || (registeredName != null && 
/* 106 */       hasAlias(name, registeredName)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAlias(String alias) {
/* 111 */     synchronized (this.aliasMap) {
/* 112 */       String name = this.aliasMap.remove(alias);
/* 113 */       if (name == null) {
/* 114 */         throw new IllegalStateException("No alias '" + alias + "' registered");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAlias(String name) {
/* 121 */     return this.aliasMap.containsKey(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getAliases(String name) {
/* 126 */     List<String> result = new ArrayList<>();
/* 127 */     synchronized (this.aliasMap) {
/* 128 */       retrieveAliases(name, result);
/*     */     } 
/* 130 */     return StringUtils.toStringArray(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void retrieveAliases(String name, List<String> result) {
/* 139 */     this.aliasMap.forEach((alias, registeredName) -> {
/*     */           if (registeredName.equals(name)) {
/*     */             result.add(alias);
/*     */             retrieveAliases(alias, result);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resolveAliases(StringValueResolver valueResolver) {
/* 155 */     Assert.notNull(valueResolver, "StringValueResolver must not be null");
/* 156 */     synchronized (this.aliasMap) {
/* 157 */       Map<String, String> aliasCopy = new HashMap<>(this.aliasMap);
/* 158 */       aliasCopy.forEach((alias, registeredName) -> {
/*     */             String resolvedAlias = valueResolver.resolveStringValue(alias);
/*     */             String resolvedName = valueResolver.resolveStringValue(registeredName);
/*     */             if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
/*     */               this.aliasMap.remove(alias);
/*     */             } else if (!resolvedAlias.equals(alias)) {
/*     */               String existingName = this.aliasMap.get(resolvedAlias);
/*     */               if (existingName != null) {
/*     */                 if (existingName.equals(resolvedName)) {
/*     */                   this.aliasMap.remove(alias);
/*     */                   return;
/*     */                 } 
/*     */                 throw new IllegalStateException("Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias + "') for name '" + resolvedName + "': It is already registered for name '" + registeredName + "'.");
/*     */               } 
/*     */               checkForAliasCircle(resolvedName, resolvedAlias);
/*     */               this.aliasMap.remove(alias);
/*     */               this.aliasMap.put(resolvedAlias, resolvedName);
/*     */             } else if (!registeredName.equals(resolvedName)) {
/*     */               this.aliasMap.put(alias, resolvedName);
/*     */             } 
/*     */           });
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkForAliasCircle(String name, String alias) {
/* 198 */     if (hasAlias(alias, name)) {
/* 199 */       throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Circular reference - '" + name + "' is a direct or indirect alias for '" + alias + "' already");
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
/*     */   public String canonicalName(String name) {
/* 211 */     String canonicalName = name;
/*     */ 
/*     */     
/*     */     while (true) {
/* 215 */       String resolvedName = this.aliasMap.get(canonicalName);
/* 216 */       if (resolvedName != null) {
/* 217 */         canonicalName = resolvedName;
/*     */       }
/*     */       
/* 220 */       if (resolvedName == null)
/* 221 */         return canonicalName; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/SimpleAliasRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */