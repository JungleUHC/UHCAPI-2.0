/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import org.springframework.context.ApplicationEvent;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestHandledEvent
/*     */   extends ApplicationEvent
/*     */ {
/*     */   @Nullable
/*     */   private String sessionId;
/*     */   @Nullable
/*     */   private String userName;
/*     */   private final long processingTimeMillis;
/*     */   @Nullable
/*     */   private Throwable failureCause;
/*     */   
/*     */   public RequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis) {
/*  66 */     super(source);
/*  67 */     this.sessionId = sessionId;
/*  68 */     this.userName = userName;
/*  69 */     this.processingTimeMillis = processingTimeMillis;
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
/*     */   public RequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis, @Nullable Throwable failureCause) {
/*  84 */     this(source, sessionId, userName, processingTimeMillis);
/*  85 */     this.failureCause = failureCause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getProcessingTimeMillis() {
/*  93 */     return this.processingTimeMillis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getSessionId() {
/* 101 */     return this.sessionId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getUserName() {
/* 111 */     return this.userName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean wasFailure() {
/* 118 */     return (this.failureCause != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Throwable getFailureCause() {
/* 126 */     return this.failureCause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortDescription() {
/* 135 */     StringBuilder sb = new StringBuilder();
/* 136 */     sb.append("session=[").append(this.sessionId).append("]; ");
/* 137 */     sb.append("user=[").append(this.userName).append("]; ");
/* 138 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescription() {
/* 146 */     StringBuilder sb = new StringBuilder();
/* 147 */     sb.append("session=[").append(this.sessionId).append("]; ");
/* 148 */     sb.append("user=[").append(this.userName).append("]; ");
/* 149 */     sb.append("time=[").append(this.processingTimeMillis).append("ms]; ");
/* 150 */     sb.append("status=[");
/* 151 */     if (!wasFailure()) {
/* 152 */       sb.append("OK");
/*     */     } else {
/*     */       
/* 155 */       sb.append("failed: ").append(this.failureCause);
/*     */     } 
/* 157 */     sb.append(']');
/* 158 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 163 */     return "RequestHandledEvent: " + getDescription();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/RequestHandledEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */