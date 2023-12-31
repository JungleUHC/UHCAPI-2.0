/*     */ package org.springframework.remoting.jaxws;
/*     */ 
/*     */ import com.sun.net.httpserver.Authenticator;
/*     */ import com.sun.net.httpserver.Filter;
/*     */ import com.sun.net.httpserver.HttpContext;
/*     */ import com.sun.net.httpserver.HttpServer;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
/*     */ import javax.jws.WebService;
/*     */ import javax.xml.ws.Endpoint;
/*     */ import javax.xml.ws.WebServiceProvider;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.lang.UsesSunHttpServer;
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
/*     */ @Deprecated
/*     */ @UsesSunHttpServer
/*     */ public class SimpleHttpServerJaxWsServiceExporter
/*     */   extends AbstractJaxWsServiceExporter
/*     */ {
/*  58 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */   
/*     */   @Nullable
/*     */   private HttpServer server;
/*     */   
/*  63 */   private int port = 8080;
/*     */   
/*     */   @Nullable
/*     */   private String hostname;
/*     */   
/*  68 */   private int backlog = -1;
/*     */   
/*  70 */   private int shutdownDelay = 0;
/*     */   
/*  72 */   private String basePath = "/";
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private List<Filter> filters;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Authenticator authenticator;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean localServer = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServer(HttpServer server) {
/*  92 */     this.server = server;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPort(int port) {
/* 101 */     this.port = port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHostname(String hostname) {
/* 111 */     this.hostname = hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBacklog(int backlog) {
/* 121 */     this.backlog = backlog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShutdownDelay(int shutdownDelay) {
/* 131 */     this.shutdownDelay = shutdownDelay;
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
/*     */   public void setBasePath(String basePath) {
/* 143 */     this.basePath = basePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilters(List<Filter> filters) {
/* 151 */     this.filters = filters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAuthenticator(Authenticator authenticator) {
/* 159 */     this.authenticator = authenticator;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() throws Exception {
/* 165 */     if (this.server == null) {
/* 166 */       InetSocketAddress address = (this.hostname != null) ? new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port);
/*     */       
/* 168 */       HttpServer server = HttpServer.create(address, this.backlog);
/* 169 */       if (this.logger.isInfoEnabled()) {
/* 170 */         this.logger.info("Starting HttpServer at address " + address);
/*     */       }
/* 172 */       server.start();
/* 173 */       this.server = server;
/* 174 */       this.localServer = true;
/*     */     } 
/* 176 */     super.afterPropertiesSet();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
/* 181 */     endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
/* 186 */     endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpContext buildHttpContext(Endpoint endpoint, String serviceName) {
/* 196 */     Assert.state((this.server != null), "No HttpServer available");
/* 197 */     String fullPath = calculateEndpointPath(endpoint, serviceName);
/* 198 */     HttpContext httpContext = this.server.createContext(fullPath);
/* 199 */     if (this.filters != null) {
/* 200 */       httpContext.getFilters().addAll(this.filters);
/*     */     }
/* 202 */     if (this.authenticator != null) {
/* 203 */       httpContext.setAuthenticator(this.authenticator);
/*     */     }
/* 205 */     return httpContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String calculateEndpointPath(Endpoint endpoint, String serviceName) {
/* 215 */     return this.basePath + serviceName;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 221 */     super.destroy();
/* 222 */     if (this.server != null && this.localServer) {
/* 223 */       this.logger.info("Stopping HttpServer");
/* 224 */       this.server.stop(this.shutdownDelay);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/jaxws/SimpleHttpServerJaxWsServiceExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */