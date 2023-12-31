/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CharacterEncodingFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*     */   @Nullable
/*     */   private String encoding;
/*     */   private boolean forceRequestEncoding = false;
/*     */   private boolean forceResponseEncoding = false;
/*     */   
/*     */   public CharacterEncodingFilter() {}
/*     */   
/*     */   public CharacterEncodingFilter(String encoding) {
/*  72 */     this(encoding, false);
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
/*     */   public CharacterEncodingFilter(String encoding, boolean forceEncoding) {
/*  85 */     this(encoding, forceEncoding, forceEncoding);
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
/*     */   public CharacterEncodingFilter(String encoding, boolean forceRequestEncoding, boolean forceResponseEncoding) {
/* 101 */     Assert.hasLength(encoding, "Encoding must not be empty");
/* 102 */     this.encoding = encoding;
/* 103 */     this.forceRequestEncoding = forceRequestEncoding;
/* 104 */     this.forceResponseEncoding = forceResponseEncoding;
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
/*     */   public void setEncoding(@Nullable String encoding) {
/* 116 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getEncoding() {
/* 125 */     return this.encoding;
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
/*     */   public void setForceEncoding(boolean forceEncoding) {
/* 141 */     this.forceRequestEncoding = forceEncoding;
/* 142 */     this.forceResponseEncoding = forceEncoding;
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
/*     */   public void setForceRequestEncoding(boolean forceRequestEncoding) {
/* 155 */     this.forceRequestEncoding = forceRequestEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isForceRequestEncoding() {
/* 163 */     return this.forceRequestEncoding;
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
/*     */   public void setForceResponseEncoding(boolean forceResponseEncoding) {
/* 175 */     this.forceResponseEncoding = forceResponseEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isForceResponseEncoding() {
/* 183 */     return this.forceResponseEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 192 */     String encoding = getEncoding();
/* 193 */     if (encoding != null) {
/* 194 */       if (isForceRequestEncoding() || request.getCharacterEncoding() == null) {
/* 195 */         request.setCharacterEncoding(encoding);
/*     */       }
/* 197 */       if (isForceResponseEncoding()) {
/* 198 */         response.setCharacterEncoding(encoding);
/*     */       }
/*     */     } 
/* 201 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/CharacterEncodingFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */