/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.DigestUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.context.request.ServletWebRequest;
/*     */ import org.springframework.web.util.ContentCachingResponseWrapper;
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
/*     */ public class ShallowEtagHeaderFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*     */   private static final String DIRECTIVE_NO_STORE = "no-store";
/*  62 */   private static final String STREAMING_ATTRIBUTE = ShallowEtagHeaderFilter.class.getName() + ".STREAMING";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean writeWeakETag = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteWeakETag(boolean writeWeakETag) {
/*  76 */     this.writeWeakETag = writeWeakETag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteWeakETag() {
/*  84 */     return this.writeWeakETag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterAsyncDispatch() {
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*     */     ConditionalContentCachingResponseWrapper conditionalContentCachingResponseWrapper;
/* 101 */     HttpServletResponse responseToUse = response;
/* 102 */     if (!isAsyncDispatch(request) && !(response instanceof ConditionalContentCachingResponseWrapper)) {
/* 103 */       conditionalContentCachingResponseWrapper = new ConditionalContentCachingResponseWrapper(response, request);
/*     */     }
/*     */     
/* 106 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)conditionalContentCachingResponseWrapper);
/*     */     
/* 108 */     if (!isAsyncStarted(request) && !isContentCachingDisabled(request)) {
/* 109 */       updateResponse(request, (HttpServletResponse)conditionalContentCachingResponseWrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
/* 115 */     ConditionalContentCachingResponseWrapper wrapper = (ConditionalContentCachingResponseWrapper)WebUtils.getNativeResponse((ServletResponse)response, ConditionalContentCachingResponseWrapper.class);
/* 116 */     Assert.notNull(wrapper, "ContentCachingResponseWrapper not found");
/* 117 */     HttpServletResponse rawResponse = (HttpServletResponse)wrapper.getResponse();
/*     */     
/* 119 */     if (isEligibleForEtag(request, (HttpServletResponse)wrapper, wrapper.getStatus(), wrapper.getContentInputStream())) {
/* 120 */       String eTag = wrapper.getHeader("ETag");
/* 121 */       if (!StringUtils.hasText(eTag)) {
/* 122 */         eTag = generateETagHeaderValue(wrapper.getContentInputStream(), this.writeWeakETag);
/* 123 */         rawResponse.setHeader("ETag", eTag);
/*     */       } 
/* 125 */       if ((new ServletWebRequest(request, rawResponse)).checkNotModified(eTag)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 130 */     wrapper.copyBodyToResponse();
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
/*     */ 
/*     */   
/*     */   protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response, int responseStatusCode, InputStream inputStream) {
/* 151 */     if (!response.isCommitted() && responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET
/*     */       
/* 153 */       .matches(request.getMethod())) {
/*     */       
/* 155 */       String cacheControl = response.getHeader("Cache-Control");
/* 156 */       return (cacheControl == null || !cacheControl.contains("no-store"));
/*     */     } 
/*     */     
/* 159 */     return false;
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
/*     */   protected String generateETagHeaderValue(InputStream inputStream, boolean isWeak) throws IOException {
/* 172 */     StringBuilder builder = new StringBuilder(37);
/* 173 */     if (isWeak) {
/* 174 */       builder.append("W/");
/*     */     }
/* 176 */     builder.append("\"0");
/* 177 */     DigestUtils.appendMd5DigestAsHex(inputStream, builder);
/* 178 */     builder.append('"');
/* 179 */     return builder.toString();
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
/*     */   public static void disableContentCaching(ServletRequest request) {
/* 193 */     Assert.notNull(request, "ServletRequest must not be null");
/* 194 */     request.setAttribute(STREAMING_ATTRIBUTE, Boolean.valueOf(true));
/*     */   }
/*     */   
/*     */   private static boolean isContentCachingDisabled(HttpServletRequest request) {
/* 198 */     return (request.getAttribute(STREAMING_ATTRIBUTE) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ConditionalContentCachingResponseWrapper
/*     */     extends ContentCachingResponseWrapper
/*     */   {
/*     */     private final HttpServletRequest request;
/*     */ 
/*     */ 
/*     */     
/*     */     ConditionalContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
/* 211 */       super(response);
/* 212 */       this.request = request;
/*     */     }
/*     */ 
/*     */     
/*     */     public ServletOutputStream getOutputStream() throws IOException {
/* 217 */       return (ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || hasETag()) ? 
/* 218 */         getResponse().getOutputStream() : super.getOutputStream();
/*     */     }
/*     */ 
/*     */     
/*     */     public PrintWriter getWriter() throws IOException {
/* 223 */       return (ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || hasETag()) ? 
/* 224 */         getResponse().getWriter() : super.getWriter();
/*     */     }
/*     */     
/*     */     private boolean hasETag() {
/* 228 */       return StringUtils.hasText(getHeader("ETag"));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/ShallowEtagHeaderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */