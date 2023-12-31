/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StreamUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringHttpMessageConverter
/*     */   extends AbstractHttpMessageConverter<String>
/*     */ {
/*  46 */   private static final MediaType APPLICATION_PLUS_JSON = new MediaType("application", "*+json");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile List<Charset> availableCharsets;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean writeAcceptCharset = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public StringHttpMessageConverter() {
/*  65 */     this(DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringHttpMessageConverter(Charset defaultCharset) {
/*  73 */     super(defaultCharset, new MediaType[] { MediaType.TEXT_PLAIN, MediaType.ALL });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteAcceptCharset(boolean writeAcceptCharset) {
/*  84 */     this.writeAcceptCharset = writeAcceptCharset;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supports(Class<?> clazz) {
/*  90 */     return (String.class == clazz);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
/*  95 */     Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
/*  96 */     return StreamUtils.copyToString(inputMessage.getBody(), charset);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Long getContentLength(String str, @Nullable MediaType contentType) {
/* 101 */     Charset charset = getContentTypeCharset(contentType);
/* 102 */     return Long.valueOf((str.getBytes(charset)).length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addDefaultHeaders(HttpHeaders headers, String s, @Nullable MediaType type) throws IOException {
/* 108 */     if (headers.getContentType() == null && 
/* 109 */       type != null && type.isConcrete() && (type
/* 110 */       .isCompatibleWith(MediaType.APPLICATION_JSON) || type
/* 111 */       .isCompatibleWith(APPLICATION_PLUS_JSON)))
/*     */     {
/* 113 */       headers.setContentType(type);
/*     */     }
/*     */     
/* 116 */     super.addDefaultHeaders(headers, s, type);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
/* 121 */     HttpHeaders headers = outputMessage.getHeaders();
/* 122 */     if (this.writeAcceptCharset && headers.get("Accept-Charset") == null) {
/* 123 */       headers.setAcceptCharset(getAcceptedCharsets());
/*     */     }
/* 125 */     Charset charset = getContentTypeCharset(headers.getContentType());
/* 126 */     StreamUtils.copy(str, charset, outputMessage.getBody());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<Charset> getAcceptedCharsets() {
/* 137 */     List<Charset> charsets = this.availableCharsets;
/* 138 */     if (charsets == null) {
/* 139 */       charsets = new ArrayList<>(Charset.availableCharsets().values());
/* 140 */       this.availableCharsets = charsets;
/*     */     } 
/* 142 */     return charsets;
/*     */   }
/*     */   
/*     */   private Charset getContentTypeCharset(@Nullable MediaType contentType) {
/* 146 */     if (contentType != null) {
/* 147 */       Charset charset1 = contentType.getCharset();
/* 148 */       if (charset1 != null) {
/* 149 */         return charset1;
/*     */       }
/* 151 */       if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON) || contentType
/* 152 */         .isCompatibleWith(APPLICATION_PLUS_JSON))
/*     */       {
/* 154 */         return StandardCharsets.UTF_8;
/*     */       }
/*     */     } 
/* 157 */     Charset charset = getDefaultCharset();
/* 158 */     Assert.state((charset != null), "No default charset");
/* 159 */     return charset;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/StringHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */