/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.StringJoiner;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyBatchUpdateException
/*     */   extends BeansException
/*     */ {
/*     */   private final PropertyAccessException[] propertyAccessExceptions;
/*     */   
/*     */   public PropertyBatchUpdateException(PropertyAccessException[] propertyAccessExceptions) {
/*  52 */     super((String)null, (Throwable)null);
/*  53 */     Assert.notEmpty((Object[])propertyAccessExceptions, "At least 1 PropertyAccessException required");
/*  54 */     this.propertyAccessExceptions = propertyAccessExceptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getExceptionCount() {
/*  62 */     return this.propertyAccessExceptions.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PropertyAccessException[] getPropertyAccessExceptions() {
/*  70 */     return this.propertyAccessExceptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PropertyAccessException getPropertyAccessException(String propertyName) {
/*  78 */     for (PropertyAccessException pae : this.propertyAccessExceptions) {
/*  79 */       if (ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) {
/*  80 */         return pae;
/*     */       }
/*     */     } 
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  89 */     StringJoiner stringJoiner = new StringJoiner("; ", "Failed properties: ", "");
/*  90 */     for (PropertyAccessException exception : this.propertyAccessExceptions) {
/*  91 */       stringJoiner.add(exception.getMessage());
/*     */     }
/*  93 */     return stringJoiner.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  98 */     StringBuilder sb = new StringBuilder();
/*  99 */     sb.append(getClass().getName()).append("; nested PropertyAccessExceptions (");
/* 100 */     sb.append(getExceptionCount()).append(") are:");
/* 101 */     for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
/* 102 */       sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
/* 103 */       sb.append(this.propertyAccessExceptions[i]);
/*     */     } 
/* 105 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintStream ps) {
/* 110 */     synchronized (ps) {
/* 111 */       ps.println(getClass().getName() + "; nested PropertyAccessException details (" + 
/* 112 */           getExceptionCount() + ") are:");
/* 113 */       for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
/* 114 */         ps.println("PropertyAccessException " + (i + 1) + ":");
/* 115 */         this.propertyAccessExceptions[i].printStackTrace(ps);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintWriter pw) {
/* 122 */     synchronized (pw) {
/* 123 */       pw.println(getClass().getName() + "; nested PropertyAccessException details (" + 
/* 124 */           getExceptionCount() + ") are:");
/* 125 */       for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
/* 126 */         pw.println("PropertyAccessException " + (i + 1) + ":");
/* 127 */         this.propertyAccessExceptions[i].printStackTrace(pw);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(@Nullable Class<?> exType) {
/* 134 */     if (exType == null) {
/* 135 */       return false;
/*     */     }
/* 137 */     if (exType.isInstance(this)) {
/* 138 */       return true;
/*     */     }
/* 140 */     for (PropertyAccessException pae : this.propertyAccessExceptions) {
/* 141 */       if (pae.contains(exType)) {
/* 142 */         return true;
/*     */       }
/*     */     } 
/* 145 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/PropertyBatchUpdateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */