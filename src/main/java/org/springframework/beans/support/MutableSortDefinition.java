/*     */ package org.springframework.beans.support;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class MutableSortDefinition
/*     */   implements SortDefinition, Serializable
/*     */ {
/*  36 */   private String property = "";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ignoreCase = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ascending = true;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean toggleAscendingOnProperty = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableSortDefinition() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableSortDefinition(SortDefinition source) {
/*  61 */     this.property = source.getProperty();
/*  62 */     this.ignoreCase = source.isIgnoreCase();
/*  63 */     this.ascending = source.isAscending();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableSortDefinition(String property, boolean ignoreCase, boolean ascending) {
/*  73 */     this.property = property;
/*  74 */     this.ignoreCase = ignoreCase;
/*  75 */     this.ascending = ascending;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutableSortDefinition(boolean toggleAscendingOnSameProperty) {
/*  85 */     this.toggleAscendingOnProperty = toggleAscendingOnSameProperty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProperty(String property) {
/*  96 */     if (!StringUtils.hasLength(property)) {
/*  97 */       this.property = "";
/*     */     }
/*     */     else {
/*     */       
/* 101 */       if (isToggleAscendingOnProperty()) {
/* 102 */         this.ascending = (!property.equals(this.property) || !this.ascending);
/*     */       }
/* 104 */       this.property = property;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProperty() {
/* 110 */     return this.property;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreCase(boolean ignoreCase) {
/* 117 */     this.ignoreCase = ignoreCase;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIgnoreCase() {
/* 122 */     return this.ignoreCase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAscending(boolean ascending) {
/* 129 */     this.ascending = ascending;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAscending() {
/* 134 */     return this.ascending;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setToggleAscendingOnProperty(boolean toggleAscendingOnProperty) {
/* 145 */     this.toggleAscendingOnProperty = toggleAscendingOnProperty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isToggleAscendingOnProperty() {
/* 153 */     return this.toggleAscendingOnProperty;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 159 */     if (this == other) {
/* 160 */       return true;
/*     */     }
/* 162 */     if (!(other instanceof SortDefinition)) {
/* 163 */       return false;
/*     */     }
/* 165 */     SortDefinition otherSd = (SortDefinition)other;
/* 166 */     return (getProperty().equals(otherSd.getProperty()) && 
/* 167 */       isAscending() == otherSd.isAscending() && 
/* 168 */       isIgnoreCase() == otherSd.isIgnoreCase());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 173 */     int hashCode = getProperty().hashCode();
/* 174 */     hashCode = 29 * hashCode + (isIgnoreCase() ? 1 : 0);
/* 175 */     hashCode = 29 * hashCode + (isAscending() ? 1 : 0);
/* 176 */     return hashCode;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/support/MutableSortDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */