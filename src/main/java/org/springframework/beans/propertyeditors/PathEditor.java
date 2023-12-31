/*     */ package org.springframework.beans.propertyeditors;
/*     */ 
/*     */ import java.beans.PropertyEditorSupport;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.file.FileSystemNotFoundException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceEditor;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathEditor
/*     */   extends PropertyEditorSupport
/*     */ {
/*     */   private final ResourceEditor resourceEditor;
/*     */   
/*     */   public PathEditor() {
/*  62 */     this.resourceEditor = new ResourceEditor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathEditor(ResourceEditor resourceEditor) {
/*  70 */     Assert.notNull(resourceEditor, "ResourceEditor must not be null");
/*  71 */     this.resourceEditor = resourceEditor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsText(String text) throws IllegalArgumentException {
/*  77 */     boolean nioPathCandidate = !text.startsWith("classpath:");
/*  78 */     if (nioPathCandidate && !text.startsWith("/")) {
/*     */       try {
/*  80 */         URI uri = new URI(text);
/*  81 */         if (uri.getScheme() != null) {
/*  82 */           nioPathCandidate = false;
/*     */           
/*  84 */           setValue(Paths.get(uri).normalize());
/*     */           
/*     */           return;
/*     */         } 
/*  88 */       } catch (URISyntaxException ex) {
/*     */ 
/*     */         
/*  91 */         nioPathCandidate = !text.startsWith("file:");
/*     */       }
/*  93 */       catch (FileSystemNotFoundException fileSystemNotFoundException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     this.resourceEditor.setAsText(text);
/* 100 */     Resource resource = (Resource)this.resourceEditor.getValue();
/* 101 */     if (resource == null) {
/* 102 */       setValue(null);
/*     */     }
/* 104 */     else if (nioPathCandidate && !resource.exists()) {
/* 105 */       setValue(Paths.get(text, new String[0]).normalize());
/*     */     } else {
/*     */       
/*     */       try {
/* 109 */         setValue(resource.getFile().toPath());
/*     */       }
/* 111 */       catch (IOException ex) {
/* 112 */         throw new IllegalArgumentException("Failed to retrieve file for " + resource, ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAsText() {
/* 119 */     Path value = (Path)getValue();
/* 120 */     return (value != null) ? value.toString() : "";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/propertyeditors/PathEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */