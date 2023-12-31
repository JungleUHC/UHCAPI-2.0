/*     */ package org.springframework.web.server.session;
/*     */ 
/*     */ import java.time.Clock;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.ZoneId;
/*     */ import java.time.temporal.ChronoUnit;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.IdGenerator;
/*     */ import org.springframework.util.JdkIdGenerator;
/*     */ import org.springframework.web.server.WebSession;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.scheduler.Schedulers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InMemoryWebSessionStore
/*     */   implements WebSessionStore
/*     */ {
/*  48 */   private static final IdGenerator idGenerator = (IdGenerator)new JdkIdGenerator();
/*     */ 
/*     */   
/*  51 */   private int maxSessions = 10000;
/*     */   
/*  53 */   private Clock clock = Clock.system(ZoneId.of("GMT"));
/*     */   
/*  55 */   private final Map<String, InMemoryWebSession> sessions = new ConcurrentHashMap<>();
/*     */   
/*  57 */   private final ExpiredSessionChecker expiredSessionChecker = new ExpiredSessionChecker();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxSessions(int maxSessions) {
/*  69 */     this.maxSessions = maxSessions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxSessions() {
/*  77 */     return this.maxSessions;
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
/*     */   public void setClock(Clock clock) {
/*  90 */     Assert.notNull(clock, "Clock is required");
/*  91 */     this.clock = clock;
/*  92 */     removeExpiredSessions();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Clock getClock() {
/*  99 */     return this.clock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, WebSession> getSessions() {
/* 109 */     return Collections.unmodifiableMap((Map)this.sessions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<WebSession> createWebSession() {
/* 117 */     Instant now = this.clock.instant();
/* 118 */     this.expiredSessionChecker.checkIfNecessary(now);
/*     */     
/* 120 */     return Mono.fromSupplier(() -> new InMemoryWebSession(now))
/* 121 */       .subscribeOn(Schedulers.boundedElastic())
/* 122 */       .publishOn(Schedulers.parallel());
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<WebSession> retrieveSession(String id) {
/* 127 */     Instant now = this.clock.instant();
/* 128 */     this.expiredSessionChecker.checkIfNecessary(now);
/* 129 */     InMemoryWebSession session = this.sessions.get(id);
/* 130 */     if (session == null) {
/* 131 */       return Mono.empty();
/*     */     }
/* 133 */     if (session.isExpired(now)) {
/* 134 */       this.sessions.remove(id);
/* 135 */       return Mono.empty();
/*     */     } 
/*     */     
/* 138 */     session.updateLastAccessTime(now);
/* 139 */     return Mono.just(session);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> removeSession(String id) {
/* 145 */     this.sessions.remove(id);
/* 146 */     return Mono.empty();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<WebSession> updateLastAccessTime(WebSession session) {
/* 151 */     return Mono.fromSupplier(() -> {
/*     */           Assert.isInstanceOf(InMemoryWebSession.class, session);
/*     */           ((InMemoryWebSession)session).updateLastAccessTime(this.clock.instant());
/*     */           return session;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeExpiredSessions() {
/* 166 */     this.expiredSessionChecker.removeExpiredSessions(this.clock.instant());
/*     */   }
/*     */   
/*     */   private class InMemoryWebSession
/*     */     implements WebSession
/*     */   {
/* 172 */     private final AtomicReference<String> id = new AtomicReference<>(String.valueOf(InMemoryWebSessionStore.idGenerator.generateId()));
/*     */     
/* 174 */     private final Map<String, Object> attributes = new ConcurrentHashMap<>();
/*     */     
/*     */     private final Instant creationTime;
/*     */     
/*     */     private volatile Instant lastAccessTime;
/*     */     
/* 180 */     private volatile Duration maxIdleTime = Duration.ofMinutes(30L);
/*     */     
/* 182 */     private final AtomicReference<InMemoryWebSessionStore.State> state = new AtomicReference<>(InMemoryWebSessionStore.State.NEW);
/*     */ 
/*     */     
/*     */     public InMemoryWebSession(Instant creationTime) {
/* 186 */       this.creationTime = creationTime;
/* 187 */       this.lastAccessTime = this.creationTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/* 192 */       return this.id.get();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Object> getAttributes() {
/* 197 */       return this.attributes;
/*     */     }
/*     */ 
/*     */     
/*     */     public Instant getCreationTime() {
/* 202 */       return this.creationTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public Instant getLastAccessTime() {
/* 207 */       return this.lastAccessTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setMaxIdleTime(Duration maxIdleTime) {
/* 212 */       this.maxIdleTime = maxIdleTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public Duration getMaxIdleTime() {
/* 217 */       return this.maxIdleTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public void start() {
/* 222 */       this.state.compareAndSet(InMemoryWebSessionStore.State.NEW, InMemoryWebSessionStore.State.STARTED);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isStarted() {
/* 227 */       return (((InMemoryWebSessionStore.State)this.state.get()).equals(InMemoryWebSessionStore.State.STARTED) || !getAttributes().isEmpty());
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> changeSessionId() {
/* 232 */       String currentId = this.id.get();
/* 233 */       InMemoryWebSessionStore.this.sessions.remove(currentId);
/* 234 */       String newId = String.valueOf(InMemoryWebSessionStore.idGenerator.generateId());
/* 235 */       this.id.set(newId);
/* 236 */       InMemoryWebSessionStore.this.sessions.put(getId(), this);
/* 237 */       return Mono.empty();
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> invalidate() {
/* 242 */       this.state.set(InMemoryWebSessionStore.State.EXPIRED);
/* 243 */       getAttributes().clear();
/* 244 */       InMemoryWebSessionStore.this.sessions.remove(this.id.get());
/* 245 */       return Mono.empty();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Mono<Void> save() {
/* 251 */       checkMaxSessionsLimit();
/*     */ 
/*     */       
/* 254 */       if (!getAttributes().isEmpty()) {
/* 255 */         this.state.compareAndSet(InMemoryWebSessionStore.State.NEW, InMemoryWebSessionStore.State.STARTED);
/*     */       }
/*     */       
/* 258 */       if (isStarted()) {
/*     */         
/* 260 */         InMemoryWebSessionStore.this.sessions.put(getId(), this);
/*     */ 
/*     */         
/* 263 */         if (((InMemoryWebSessionStore.State)this.state.get()).equals(InMemoryWebSessionStore.State.EXPIRED)) {
/* 264 */           InMemoryWebSessionStore.this.sessions.remove(getId());
/* 265 */           return Mono.error(new IllegalStateException("Session was invalidated"));
/*     */         } 
/*     */       } 
/*     */       
/* 269 */       return Mono.empty();
/*     */     }
/*     */     
/*     */     private void checkMaxSessionsLimit() {
/* 273 */       if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
/* 274 */         InMemoryWebSessionStore.this.expiredSessionChecker.removeExpiredSessions(InMemoryWebSessionStore.this.clock.instant());
/* 275 */         if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
/* 276 */           throw new IllegalStateException("Max sessions limit reached: " + InMemoryWebSessionStore.this.sessions.size());
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isExpired() {
/* 283 */       return isExpired(InMemoryWebSessionStore.this.clock.instant());
/*     */     }
/*     */     
/*     */     private boolean isExpired(Instant now) {
/* 287 */       if (((InMemoryWebSessionStore.State)this.state.get()).equals(InMemoryWebSessionStore.State.EXPIRED)) {
/* 288 */         return true;
/*     */       }
/* 290 */       if (checkExpired(now)) {
/* 291 */         this.state.set(InMemoryWebSessionStore.State.EXPIRED);
/* 292 */         return true;
/*     */       } 
/* 294 */       return false;
/*     */     }
/*     */     
/*     */     private boolean checkExpired(Instant currentTime) {
/* 298 */       return (isStarted() && !this.maxIdleTime.isNegative() && currentTime
/* 299 */         .minus(this.maxIdleTime).isAfter(this.lastAccessTime));
/*     */     }
/*     */     
/*     */     private void updateLastAccessTime(Instant currentTime) {
/* 303 */       this.lastAccessTime = currentTime;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ExpiredSessionChecker
/*     */   {
/*     */     private static final int CHECK_PERIOD = 60000;
/*     */ 
/*     */     
/* 314 */     private final ReentrantLock lock = new ReentrantLock();
/*     */     
/* 316 */     private Instant checkTime = InMemoryWebSessionStore.this.clock.instant().plus(60000L, ChronoUnit.MILLIS);
/*     */ 
/*     */     
/*     */     public void checkIfNecessary(Instant now) {
/* 320 */       if (this.checkTime.isBefore(now)) {
/* 321 */         removeExpiredSessions(now);
/*     */       }
/*     */     }
/*     */     
/*     */     public void removeExpiredSessions(Instant now) {
/* 326 */       if (InMemoryWebSessionStore.this.sessions.isEmpty()) {
/*     */         return;
/*     */       }
/* 329 */       if (this.lock.tryLock())
/*     */         try {
/* 331 */           Iterator<InMemoryWebSessionStore.InMemoryWebSession> iterator = InMemoryWebSessionStore.this.sessions.values().iterator();
/* 332 */           while (iterator.hasNext()) {
/* 333 */             InMemoryWebSessionStore.InMemoryWebSession session = iterator.next();
/* 334 */             if (session.isExpired(now)) {
/* 335 */               iterator.remove();
/* 336 */               session.invalidate();
/*     */             } 
/*     */           } 
/*     */         } finally {
/*     */           
/* 341 */           this.checkTime = now.plus(60000L, ChronoUnit.MILLIS);
/* 342 */           this.lock.unlock();
/*     */         }  
/*     */     }
/*     */     
/*     */     private ExpiredSessionChecker() {} }
/*     */   
/*     */   private enum State {
/* 349 */     NEW, STARTED, EXPIRED;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/session/InMemoryWebSessionStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */