/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.Preferences;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class PreferencesPlaceholderConfigurer
/*     */   extends PropertyPlaceholderConfigurer
/*     */   implements InitializingBean
/*     */ {
/*     */   @Nullable
/*     */   private String systemTreePath;
/*     */   @Nullable
/*     */   private String userTreePath;
/*  56 */   private Preferences systemPrefs = Preferences.systemRoot();
/*     */   
/*  58 */   private Preferences userPrefs = Preferences.userRoot();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSystemTreePath(String systemTreePath) {
/*  66 */     this.systemTreePath = systemTreePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserTreePath(String userTreePath) {
/*  74 */     this.userTreePath = userTreePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/*  84 */     if (this.systemTreePath != null) {
/*  85 */       this.systemPrefs = this.systemPrefs.node(this.systemTreePath);
/*     */     }
/*  87 */     if (this.userTreePath != null) {
/*  88 */       this.userPrefs = this.userPrefs.node(this.userTreePath);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String resolvePlaceholder(String placeholder, Properties props) {
/*  99 */     String path = null;
/* 100 */     String key = placeholder;
/* 101 */     int endOfPath = placeholder.lastIndexOf('/');
/* 102 */     if (endOfPath != -1) {
/* 103 */       path = placeholder.substring(0, endOfPath);
/* 104 */       key = placeholder.substring(endOfPath + 1);
/*     */     } 
/* 106 */     String value = resolvePlaceholder(path, key, this.userPrefs);
/* 107 */     if (value == null) {
/* 108 */       value = resolvePlaceholder(path, key, this.systemPrefs);
/* 109 */       if (value == null) {
/* 110 */         value = props.getProperty(placeholder);
/*     */       }
/*     */     } 
/* 113 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String resolvePlaceholder(@Nullable String path, String key, Preferences preferences) {
/* 125 */     if (path != null) {
/*     */       
/*     */       try {
/* 128 */         if (preferences.nodeExists(path)) {
/* 129 */           return preferences.node(path).get(key, null);
/*     */         }
/*     */         
/* 132 */         return null;
/*     */       
/*     */       }
/* 135 */       catch (BackingStoreException ex) {
/* 136 */         throw new BeanDefinitionStoreException("Cannot access specified node path [" + path + "]", ex);
/*     */       } 
/*     */     }
/*     */     
/* 140 */     return preferences.get(key, null);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/PreferencesPlaceholderConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */