/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.time.Duration;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.netty.http.HttpResources;
/*     */ import reactor.netty.resources.ConnectionProvider;
/*     */ import reactor.netty.resources.LoopResources;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReactorResourceFactory
/*     */   implements InitializingBean, DisposableBean
/*     */ {
/*     */   private boolean useGlobalResources = true;
/*     */   @Nullable
/*     */   private Consumer<HttpResources> globalResourcesConsumer;
/*     */   private Supplier<ConnectionProvider> connectionProviderSupplier = () -> ConnectionProvider.create("webflux", 500);
/*     */   @Nullable
/*     */   private ConnectionProvider connectionProvider;
/*     */   private Supplier<LoopResources> loopResourcesSupplier = () -> LoopResources.create("webflux-http");
/*     */   @Nullable
/*     */   private LoopResources loopResources;
/*     */   private boolean manageConnectionProvider = false;
/*     */   private boolean manageLoopResources = false;
/*  65 */   private Duration shutdownQuietPeriod = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_QUIET_PERIOD);
/*     */   
/*  67 */   private Duration shutdownTimeout = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_TIMEOUT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUseGlobalResources(boolean useGlobalResources) {
/*  80 */     this.useGlobalResources = useGlobalResources;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUseGlobalResources() {
/*  88 */     return this.useGlobalResources;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addGlobalResourcesConsumer(Consumer<HttpResources> consumer) {
/*  99 */     this.useGlobalResources = true;
/* 100 */     this
/* 101 */       .globalResourcesConsumer = (this.globalResourcesConsumer != null) ? this.globalResourcesConsumer.andThen(consumer) : consumer;
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
/*     */   public void setConnectionProviderSupplier(Supplier<ConnectionProvider> supplier) {
/* 113 */     this.connectionProviderSupplier = supplier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectionProvider(ConnectionProvider connectionProvider) {
/* 122 */     this.connectionProvider = connectionProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectionProvider getConnectionProvider() {
/* 129 */     Assert.state((this.connectionProvider != null), "ConnectionProvider not initialized yet");
/* 130 */     return this.connectionProvider;
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
/*     */   public void setLoopResourcesSupplier(Supplier<LoopResources> supplier) {
/* 142 */     this.loopResourcesSupplier = supplier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLoopResources(LoopResources loopResources) {
/* 151 */     this.loopResources = loopResources;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoopResources getLoopResources() {
/* 158 */     Assert.state((this.loopResources != null), "LoopResources not initialized yet");
/* 159 */     return this.loopResources;
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
/*     */   public void setShutdownQuietPeriod(Duration shutdownQuietPeriod) {
/* 175 */     Assert.notNull(shutdownQuietPeriod, "shutdownQuietPeriod should not be null");
/* 176 */     this.shutdownQuietPeriod = shutdownQuietPeriod;
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
/*     */   public void setShutdownTimeout(Duration shutdownTimeout) {
/* 192 */     Assert.notNull(shutdownTimeout, "shutdownTimeout should not be null");
/* 193 */     this.shutdownTimeout = shutdownTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 199 */     if (this.useGlobalResources) {
/* 200 */       Assert.isTrue((this.loopResources == null && this.connectionProvider == null), "'useGlobalResources' is mutually exclusive with explicitly configured resources");
/*     */       
/* 202 */       HttpResources httpResources = HttpResources.get();
/* 203 */       if (this.globalResourcesConsumer != null) {
/* 204 */         this.globalResourcesConsumer.accept(httpResources);
/*     */       }
/* 206 */       this.connectionProvider = (ConnectionProvider)httpResources;
/* 207 */       this.loopResources = (LoopResources)httpResources;
/*     */     } else {
/*     */       
/* 210 */       if (this.loopResources == null) {
/* 211 */         this.manageLoopResources = true;
/* 212 */         this.loopResources = this.loopResourcesSupplier.get();
/*     */       } 
/* 214 */       if (this.connectionProvider == null) {
/* 215 */         this.manageConnectionProvider = true;
/* 216 */         this.connectionProvider = this.connectionProviderSupplier.get();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 223 */     if (this.useGlobalResources) {
/* 224 */       HttpResources.disposeLoopsAndConnectionsLater(this.shutdownQuietPeriod, this.shutdownTimeout).block();
/*     */     } else {
/*     */       
/*     */       try {
/* 228 */         ConnectionProvider provider = this.connectionProvider;
/* 229 */         if (provider != null && this.manageConnectionProvider) {
/* 230 */           provider.disposeLater().block();
/*     */         }
/*     */       }
/* 233 */       catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 238 */         LoopResources resources = this.loopResources;
/* 239 */         if (resources != null && this.manageLoopResources) {
/* 240 */           resources.disposeLater(this.shutdownQuietPeriod, this.shutdownTimeout).block();
/*     */         }
/*     */       }
/* 243 */       catch (Throwable throwable) {}
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ReactorResourceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */