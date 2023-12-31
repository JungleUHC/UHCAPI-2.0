/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubTypeValidator
/*     */ {
/*     */   protected static final String PREFIX_SPRING = "org.springframework.";
/*     */   protected static final String PREFIX_C3P0 = "com.mchange.v2.c3p0.";
/*     */   protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
/*     */   
/*     */   static {
/*  32 */     Set<String> s = new HashSet<>();
/*     */ 
/*     */     
/*  35 */     s.add("org.apache.commons.collections.functors.InvokerTransformer");
/*  36 */     s.add("org.apache.commons.collections.functors.InstantiateTransformer");
/*  37 */     s.add("org.apache.commons.collections4.functors.InvokerTransformer");
/*  38 */     s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
/*  39 */     s.add("org.codehaus.groovy.runtime.ConvertedClosure");
/*  40 */     s.add("org.codehaus.groovy.runtime.MethodClosure");
/*  41 */     s.add("org.springframework.beans.factory.ObjectFactory");
/*  42 */     s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
/*  43 */     s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
/*     */     
/*  45 */     s.add("com.sun.rowset.JdbcRowSetImpl");
/*     */     
/*  47 */     s.add("java.util.logging.FileHandler");
/*  48 */     s.add("java.rmi.server.UnicastRemoteObject");
/*     */ 
/*     */     
/*  51 */     s.add("org.springframework.beans.factory.config.PropertyPathFactoryBean");
/*     */     
/*  53 */     s.add("org.springframework.aop.config.MethodLocatingFactoryBean");
/*  54 */     s.add("org.springframework.beans.factory.config.BeanReferenceFactoryBean");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     s.add("org.apache.tomcat.dbcp.dbcp2.BasicDataSource");
/*  60 */     s.add("com.sun.org.apache.bcel.internal.util.ClassLoader");
/*     */     
/*  62 */     s.add("org.hibernate.jmx.StatisticsService");
/*  63 */     s.add("org.apache.ibatis.datasource.jndi.JndiDataSourceFactory");
/*     */     
/*  65 */     s.add("org.apache.ibatis.parsing.XPathParser");
/*     */ 
/*     */     
/*  68 */     s.add("jodd.db.connection.DataSourceConnectionProvider");
/*     */ 
/*     */     
/*  71 */     s.add("oracle.jdbc.connector.OracleManagedConnectionFactory");
/*  72 */     s.add("oracle.jdbc.rowset.OracleJDBCRowSet");
/*     */ 
/*     */     
/*  75 */     s.add("org.slf4j.ext.EventData");
/*  76 */     s.add("flex.messaging.util.concurrent.AsynchBeansWorkManagerExecutor");
/*  77 */     s.add("com.sun.deploy.security.ruleset.DRSHelper");
/*  78 */     s.add("org.apache.axis2.jaxws.spi.handler.HandlerResolverImpl");
/*     */ 
/*     */     
/*  81 */     s.add("org.jboss.util.propertyeditor.DocumentEditor");
/*  82 */     s.add("org.apache.openjpa.ee.RegistryManagedRuntime");
/*  83 */     s.add("org.apache.openjpa.ee.JNDIManagedRuntime");
/*  84 */     s.add("org.apache.openjpa.ee.WASRegistryManagedRuntime");
/*  85 */     s.add("org.apache.axis2.transport.jms.JMSOutTransportInfo");
/*     */ 
/*     */     
/*  88 */     s.add("com.mysql.cj.jdbc.admin.MiniAdmin");
/*     */ 
/*     */     
/*  91 */     s.add("ch.qos.logback.core.db.DriverManagerConnectionSource");
/*     */ 
/*     */     
/*  94 */     s.add("org.jdom.transform.XSLTransformer");
/*  95 */     s.add("org.jdom2.transform.XSLTransformer");
/*     */ 
/*     */     
/*  98 */     s.add("net.sf.ehcache.transaction.manager.DefaultTransactionManagerLookup");
/*  99 */     s.add("net.sf.ehcache.hibernate.EhcacheJtaTransactionManagerLookup");
/*     */ 
/*     */     
/* 102 */     s.add("ch.qos.logback.core.db.JNDIConnectionSource");
/*     */ 
/*     */     
/* 105 */     s.add("com.zaxxer.hikari.HikariConfig");
/*     */     
/* 107 */     s.add("com.zaxxer.hikari.HikariDataSource");
/*     */ 
/*     */     
/* 110 */     s.add("org.apache.cxf.jaxrs.provider.XSLTJaxbProvider");
/*     */ 
/*     */     
/* 113 */     s.add("org.apache.commons.configuration.JNDIConfiguration");
/* 114 */     s.add("org.apache.commons.configuration2.JNDIConfiguration");
/*     */ 
/*     */     
/* 117 */     s.add("org.apache.xalan.lib.sql.JNDIConnectionPool");
/*     */     
/* 119 */     s.add("com.sun.org.apache.xalan.internal.lib.sql.JNDIConnectionPool");
/*     */ 
/*     */ 
/*     */     
/* 123 */     s.add("org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS");
/* 124 */     s.add("org.apache.commons.dbcp.datasources.PerUserPoolDataSource");
/* 125 */     s.add("org.apache.commons.dbcp.datasources.SharedPoolDataSource");
/*     */     
/* 127 */     s.add("com.p6spy.engine.spy.P6DataSource");
/*     */ 
/*     */     
/* 130 */     s.add("org.apache.log4j.receivers.db.DriverManagerConnectionSource");
/* 131 */     s.add("org.apache.log4j.receivers.db.JNDIConnectionSource");
/*     */ 
/*     */     
/* 134 */     s.add("net.sf.ehcache.transaction.manager.selector.GenericJndiSelector");
/* 135 */     s.add("net.sf.ehcache.transaction.manager.selector.GlassfishSelector");
/*     */ 
/*     */     
/* 138 */     s.add("org.apache.xbean.propertyeditor.JndiConverter");
/*     */ 
/*     */     
/* 141 */     s.add("org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig");
/*     */ 
/*     */     
/* 144 */     s.add("com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig");
/* 145 */     s.add("br.com.anteros.dbcp.AnterosDBCPConfig");
/*     */     
/* 147 */     s.add("br.com.anteros.dbcp.AnterosDBCPDataSource");
/*     */ 
/*     */     
/* 150 */     s.add("javax.swing.JEditorPane");
/* 151 */     s.add("javax.swing.JTextPane");
/*     */ 
/*     */     
/* 154 */     s.add("org.apache.shiro.realm.jndi.JndiRealmFactory");
/* 155 */     s.add("org.apache.shiro.jndi.JndiObjectFactory");
/*     */ 
/*     */     
/* 158 */     s.add("org.apache.ignite.cache.jta.jndi.CacheJndiTmLookup");
/* 159 */     s.add("org.apache.ignite.cache.jta.jndi.CacheJndiTmFactory");
/* 160 */     s.add("org.quartz.utils.JNDIConnectionProvider");
/*     */ 
/*     */     
/* 163 */     s.add("org.apache.aries.transaction.jms.internal.XaPooledConnectionFactory");
/* 164 */     s.add("org.apache.aries.transaction.jms.RecoverablePooledConnectionFactory");
/*     */ 
/*     */     
/* 167 */     s.add("com.caucho.config.types.ResourceRef");
/*     */ 
/*     */     
/* 170 */     s.add("org.aoju.bus.proxy.provider.RmiProvider");
/* 171 */     s.add("org.aoju.bus.proxy.provider.remoting.RmiProvider");
/*     */ 
/*     */ 
/*     */     
/* 175 */     s.add("org.apache.activemq.ActiveMQConnectionFactory");
/* 176 */     s.add("org.apache.activemq.ActiveMQXAConnectionFactory");
/* 177 */     s.add("org.apache.activemq.spring.ActiveMQConnectionFactory");
/* 178 */     s.add("org.apache.activemq.spring.ActiveMQXAConnectionFactory");
/* 179 */     s.add("org.apache.activemq.pool.JcaPooledConnectionFactory");
/* 180 */     s.add("org.apache.activemq.pool.PooledConnectionFactory");
/* 181 */     s.add("org.apache.activemq.pool.XaPooledConnectionFactory");
/* 182 */     s.add("org.apache.activemq.jms.pool.XaPooledConnectionFactory");
/* 183 */     s.add("org.apache.activemq.jms.pool.JcaPooledConnectionFactory");
/*     */ 
/*     */     
/* 186 */     s.add("org.apache.commons.proxy.provider.remoting.RmiProvider");
/*     */ 
/*     */     
/* 189 */     s.add("org.apache.commons.jelly.impl.Embedded");
/*     */ 
/*     */     
/* 192 */     s.add("oadd.org.apache.xalan.lib.sql.JNDIConnectionPool");
/* 193 */     s.add("oadd.org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS");
/* 194 */     s.add("oadd.org.apache.commons.dbcp.datasources.PerUserPoolDataSource");
/* 195 */     s.add("oadd.org.apache.commons.dbcp.datasources.SharedPoolDataSource");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 200 */     s.add("oracle.jms.AQjmsQueueConnectionFactory");
/* 201 */     s.add("oracle.jms.AQjmsXATopicConnectionFactory");
/* 202 */     s.add("oracle.jms.AQjmsTopicConnectionFactory");
/* 203 */     s.add("oracle.jms.AQjmsXAQueueConnectionFactory");
/* 204 */     s.add("oracle.jms.AQjmsXAConnectionFactory");
/*     */ 
/*     */     
/* 207 */     s.add("org.jsecurity.realm.jndi.JndiRealmFactory");
/*     */ 
/*     */     
/* 210 */     s.add("com.pastdev.httpcomponents.configuration.JndiConfiguration");
/*     */ 
/*     */     
/* 213 */     s.add("com.nqadmin.rowset.JdbcRowSetImpl");
/* 214 */     s.add("org.arrah.framework.rdbms.UpdatableJdbcRowsetImpl");
/*     */ 
/*     */     
/* 217 */     s.add("org.apache.commons.dbcp2.datasources.PerUserPoolDataSource");
/* 218 */     s.add("org.apache.commons.dbcp2.datasources.SharedPoolDataSource");
/* 219 */     s.add("org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS");
/*     */ 
/*     */ 
/*     */     
/* 223 */     s.add("com.newrelic.agent.deps.ch.qos.logback.core.db.JNDIConnectionSource");
/* 224 */     s.add("com.newrelic.agent.deps.ch.qos.logback.core.db.DriverManagerConnectionSource");
/*     */ 
/*     */ 
/*     */     
/* 228 */     s.add("org.apache.tomcat.dbcp.dbcp.cpdsadapter.DriverAdapterCPDS");
/* 229 */     s.add("org.apache.tomcat.dbcp.dbcp.datasources.PerUserPoolDataSource");
/* 230 */     s.add("org.apache.tomcat.dbcp.dbcp.datasources.SharedPoolDataSource");
/*     */ 
/*     */ 
/*     */     
/* 234 */     s.add("org.apache.tomcat.dbcp.dbcp2.cpdsadapter.DriverAdapterCPDS");
/* 235 */     s.add("org.apache.tomcat.dbcp.dbcp2.datasources.PerUserPoolDataSource");
/* 236 */     s.add("org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource");
/*     */ 
/*     */ 
/*     */     
/* 240 */     s.add("com.oracle.wls.shaded.org.apache.xalan.lib.sql.JNDIConnectionPool");
/*     */ 
/*     */     
/* 243 */     s.add("org.docx4j.org.apache.xalan.lib.sql.JNDIConnectionPool");
/*     */     
/* 245 */     DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 251 */   protected Set<String> _cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
/*     */   
/* 253 */   private static final SubTypeValidator instance = new SubTypeValidator();
/*     */ 
/*     */   
/*     */   public static SubTypeValidator instance() {
/* 257 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validateSubType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 264 */     Class<?> raw = type.getRawClass();
/* 265 */     String full = raw.getName();
/*     */ 
/*     */ 
/*     */     
/* 269 */     if (!this._cfgIllegalClassNames.contains(full))
/*     */     
/*     */     { 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 276 */       if (!raw.isInterface())
/*     */       {
/* 278 */         if (full.startsWith("org.springframework."))
/* 279 */         { for (Class<?> cls = raw; cls != null && cls != Object.class; ) {
/* 280 */             String name = cls.getSimpleName();
/*     */             
/* 282 */             if (!"AbstractPointcutAdvisor".equals(name)) { if ("AbstractApplicationContext"
/*     */                 
/* 284 */                 .equals(name))
/*     */                 // Byte code: goto -> 134  cls = cls.getSuperclass(); }
/*     */              // Byte code: goto -> 134
/*     */           }  }
/* 288 */         else if (full.startsWith("com.mchange.v2.c3p0."))
/*     */         
/*     */         { 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 295 */           if (full.endsWith("DataSource"))
/*     */           
/*     */           { 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 302 */             ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", new Object[] { full }); return; }  }  }  return; }  ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", new Object[] { full });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/SubTypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */