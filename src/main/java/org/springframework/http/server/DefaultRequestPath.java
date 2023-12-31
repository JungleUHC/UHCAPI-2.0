/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.util.List;
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
/*     */ class DefaultRequestPath
/*     */   implements RequestPath
/*     */ {
/*     */   private final PathContainer fullPath;
/*     */   private final PathContainer contextPath;
/*     */   private final PathContainer pathWithinApplication;
/*     */   
/*     */   DefaultRequestPath(String rawPath, @Nullable String contextPath) {
/*  40 */     this.fullPath = PathContainer.parsePath(rawPath);
/*  41 */     this.contextPath = initContextPath(this.fullPath, contextPath);
/*  42 */     this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
/*     */   }
/*     */   
/*     */   private DefaultRequestPath(RequestPath requestPath, String contextPath) {
/*  46 */     this.fullPath = requestPath;
/*  47 */     this.contextPath = initContextPath(this.fullPath, contextPath);
/*  48 */     this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
/*     */   }
/*     */   
/*     */   private static PathContainer initContextPath(PathContainer path, @Nullable String contextPath) {
/*  52 */     if (!StringUtils.hasText(contextPath) || StringUtils.matchesCharacter(contextPath, '/')) {
/*  53 */       return PathContainer.parsePath("");
/*     */     }
/*     */     
/*  56 */     validateContextPath(path.value(), contextPath);
/*     */     
/*  58 */     int length = contextPath.length();
/*  59 */     int counter = 0;
/*     */     
/*  61 */     for (int i = 0; i < path.elements().size(); i++) {
/*  62 */       PathContainer.Element element = path.elements().get(i);
/*  63 */       counter += element.value().length();
/*  64 */       if (length == counter) {
/*  65 */         return path.subPath(0, i + 1);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  70 */     throw new IllegalStateException("Failed to initialize contextPath '" + contextPath + "' for requestPath '" + path
/*  71 */         .value() + "'");
/*     */   }
/*     */   
/*     */   private static void validateContextPath(String fullPath, String contextPath) {
/*  75 */     int length = contextPath.length();
/*  76 */     if (contextPath.charAt(0) != '/' || contextPath.charAt(length - 1) == '/') {
/*  77 */       throw new IllegalArgumentException("Invalid contextPath: '" + contextPath + "': must start with '/' and not end with '/'");
/*     */     }
/*     */     
/*  80 */     if (!fullPath.startsWith(contextPath)) {
/*  81 */       throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': must match the start of requestPath: '" + fullPath + "'");
/*     */     }
/*     */     
/*  84 */     if (fullPath.length() > length && fullPath.charAt(length) != '/') {
/*  85 */       throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': must match to full path segments for requestPath: '" + fullPath + "'");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static PathContainer extractPathWithinApplication(PathContainer fullPath, PathContainer contextPath) {
/*  91 */     return fullPath.subPath(contextPath.elements().size());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String value() {
/*  99 */     return this.fullPath.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PathContainer.Element> elements() {
/* 104 */     return this.fullPath.elements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathContainer contextPath() {
/* 112 */     return this.contextPath;
/*     */   }
/*     */ 
/*     */   
/*     */   public PathContainer pathWithinApplication() {
/* 117 */     return this.pathWithinApplication;
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestPath modifyContextPath(String contextPath) {
/* 122 */     return new DefaultRequestPath(this, contextPath);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 128 */     if (this == other) {
/* 129 */       return true;
/*     */     }
/* 131 */     if (other == null || getClass() != other.getClass()) {
/* 132 */       return false;
/*     */     }
/* 134 */     DefaultRequestPath otherPath = (DefaultRequestPath)other;
/* 135 */     return (this.fullPath.equals(otherPath.fullPath) && this.contextPath
/* 136 */       .equals(otherPath.contextPath) && this.pathWithinApplication
/* 137 */       .equals(otherPath.pathWithinApplication));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 142 */     int result = this.fullPath.hashCode();
/* 143 */     result = 31 * result + this.contextPath.hashCode();
/* 144 */     result = 31 * result + this.pathWithinApplication.hashCode();
/* 145 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 150 */     return this.fullPath.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/DefaultRequestPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */