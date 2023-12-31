/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLSession;
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
/*     */ final class DefaultSslInfo
/*     */   implements SslInfo
/*     */ {
/*     */   @Nullable
/*     */   private final String sessionId;
/*     */   @Nullable
/*     */   private final X509Certificate[] peerCertificates;
/*     */   
/*     */   DefaultSslInfo(@Nullable String sessionId, X509Certificate[] peerCertificates) {
/*  45 */     Assert.notNull(peerCertificates, "No SSL certificates");
/*  46 */     this.sessionId = sessionId;
/*  47 */     this.peerCertificates = peerCertificates;
/*     */   }
/*     */   
/*     */   DefaultSslInfo(SSLSession session) {
/*  51 */     Assert.notNull(session, "SSLSession is required");
/*  52 */     this.sessionId = initSessionId(session);
/*  53 */     this.peerCertificates = initCertificates(session);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getSessionId() {
/*  60 */     return this.sessionId;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public X509Certificate[] getPeerCertificates() {
/*  66 */     return this.peerCertificates;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static String initSessionId(SSLSession session) {
/*  72 */     byte[] bytes = session.getId();
/*  73 */     if (bytes == null) {
/*  74 */       return null;
/*     */     }
/*     */     
/*  77 */     StringBuilder sb = new StringBuilder();
/*  78 */     for (byte b : bytes) {
/*  79 */       String digit = Integer.toHexString(b);
/*  80 */       if (digit.length() < 2) {
/*  81 */         sb.append('0');
/*     */       }
/*  83 */       if (digit.length() > 2) {
/*  84 */         digit = digit.substring(digit.length() - 2);
/*     */       }
/*  86 */       sb.append(digit);
/*     */     } 
/*  88 */     return sb.toString();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static X509Certificate[] initCertificates(SSLSession session) {
/*     */     Certificate[] certificates;
/*     */     try {
/*  95 */       certificates = session.getPeerCertificates();
/*     */     }
/*  97 */     catch (Throwable ex) {
/*  98 */       return null;
/*     */     } 
/*     */     
/* 101 */     List<X509Certificate> result = new ArrayList<>(certificates.length);
/* 102 */     for (Certificate certificate : certificates) {
/* 103 */       if (certificate instanceof X509Certificate) {
/* 104 */         result.add((X509Certificate)certificate);
/*     */       }
/*     */     } 
/* 107 */     return !result.isEmpty() ? result.<X509Certificate>toArray(new X509Certificate[0]) : null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/DefaultSslInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */