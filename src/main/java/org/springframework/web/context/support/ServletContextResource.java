/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.core.io.AbstractFileResolvingResource;
/*     */ import org.springframework.core.io.ContextResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ResourceUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.util.WebUtils;
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
/*     */ public class ServletContextResource
/*     */   extends AbstractFileResolvingResource
/*     */   implements ContextResource
/*     */ {
/*     */   private final ServletContext servletContext;
/*     */   private final String path;
/*     */   
/*     */   public ServletContextResource(ServletContext servletContext, String path) {
/*  70 */     Assert.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
/*  71 */     this.servletContext = servletContext;
/*     */ 
/*     */     
/*  74 */     Assert.notNull(path, "Path is required");
/*  75 */     String pathToUse = StringUtils.cleanPath(path);
/*  76 */     if (!pathToUse.startsWith("/")) {
/*  77 */       pathToUse = "/" + pathToUse;
/*     */     }
/*  79 */     this.path = pathToUse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServletContext getServletContext() {
/*  87 */     return this.servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getPath() {
/*  94 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exists() {
/*     */     try {
/* 104 */       URL url = this.servletContext.getResource(this.path);
/* 105 */       return (url != null);
/*     */     }
/* 107 */     catch (MalformedURLException ex) {
/* 108 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadable() {
/* 119 */     InputStream is = this.servletContext.getResourceAsStream(this.path);
/* 120 */     if (is != null) {
/*     */       try {
/* 122 */         is.close();
/*     */       }
/* 124 */       catch (IOException iOException) {}
/*     */ 
/*     */       
/* 127 */       return true;
/*     */     } 
/*     */     
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFile() {
/*     */     try {
/* 137 */       URL url = this.servletContext.getResource(this.path);
/* 138 */       if (url != null && ResourceUtils.isFileURL(url)) {
/* 139 */         return true;
/*     */       }
/*     */       
/* 142 */       return (this.servletContext.getRealPath(this.path) != null);
/*     */     
/*     */     }
/* 145 */     catch (MalformedURLException ex) {
/* 146 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 157 */     InputStream is = this.servletContext.getResourceAsStream(this.path);
/* 158 */     if (is == null) {
/* 159 */       throw new FileNotFoundException("Could not open " + getDescription());
/*     */     }
/* 161 */     return is;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getURL() throws IOException {
/* 171 */     URL url = this.servletContext.getResource(this.path);
/* 172 */     if (url == null) {
/* 173 */       throw new FileNotFoundException(
/* 174 */           getDescription() + " cannot be resolved to URL because it does not exist");
/*     */     }
/* 176 */     return url;
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
/*     */   public File getFile() throws IOException {
/* 188 */     URL url = this.servletContext.getResource(this.path);
/* 189 */     if (url != null && ResourceUtils.isFileURL(url))
/*     */     {
/* 191 */       return super.getFile();
/*     */     }
/*     */     
/* 194 */     String realPath = WebUtils.getRealPath(this.servletContext, this.path);
/* 195 */     return new File(realPath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Resource createRelative(String relativePath) {
/* 206 */     String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
/* 207 */     return (Resource)new ServletContextResource(this.servletContext, pathToUse);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getFilename() {
/* 218 */     return StringUtils.getFilename(this.path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescription() {
/* 227 */     return "ServletContext resource [" + this.path + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPathWithinContext() {
/* 232 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 241 */     if (this == other) {
/* 242 */       return true;
/*     */     }
/* 244 */     if (!(other instanceof ServletContextResource)) {
/* 245 */       return false;
/*     */     }
/* 247 */     ServletContextResource otherRes = (ServletContextResource)other;
/* 248 */     return (this.servletContext.equals(otherRes.servletContext) && this.path.equals(otherRes.path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 257 */     return this.path.hashCode();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */