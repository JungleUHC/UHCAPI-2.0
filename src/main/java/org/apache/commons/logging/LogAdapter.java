/*     */ package org.apache.commons.logging;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.logging.log4j.Level;
/*     */ import org.apache.logging.log4j.LogManager;
/*     */ import org.apache.logging.log4j.spi.ExtendedLogger;
/*     */ import org.apache.logging.log4j.spi.LoggerContext;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.slf4j.spi.LocationAwareLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class LogAdapter
/*     */ {
/*     */   private static final String LOG4J_SPI = "org.apache.logging.log4j.spi.ExtendedLogger";
/*     */   private static final String LOG4J_SLF4J_PROVIDER = "org.apache.logging.slf4j.SLF4JProvider";
/*     */   private static final String SLF4J_SPI = "org.slf4j.spi.LocationAwareLogger";
/*     */   private static final String SLF4J_API = "org.slf4j.Logger";
/*     */   private static final LogApi logApi;
/*     */   
/*     */   static {
/*  51 */     if (isPresent("org.apache.logging.log4j.spi.ExtendedLogger")) {
/*  52 */       if (isPresent("org.apache.logging.slf4j.SLF4JProvider") && isPresent("org.slf4j.spi.LocationAwareLogger"))
/*     */       {
/*     */ 
/*     */         
/*  56 */         logApi = LogApi.SLF4J_LAL;
/*     */       }
/*     */       else
/*     */       {
/*  60 */         logApi = LogApi.LOG4J;
/*     */       }
/*     */     
/*  63 */     } else if (isPresent("org.slf4j.spi.LocationAwareLogger")) {
/*     */       
/*  65 */       logApi = LogApi.SLF4J_LAL;
/*     */     }
/*  67 */     else if (isPresent("org.slf4j.Logger")) {
/*     */       
/*  69 */       logApi = LogApi.SLF4J;
/*     */     }
/*     */     else {
/*     */       
/*  73 */       logApi = LogApi.JUL;
/*     */     } 
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
/*     */   public static Log createLog(String name) {
/*  87 */     switch (logApi) {
/*     */       case LOG4J:
/*  89 */         return Log4jAdapter.createLog(name);
/*     */       case SLF4J_LAL:
/*  91 */         return Slf4jAdapter.createLocationAwareLog(name);
/*     */       case SLF4J:
/*  93 */         return Slf4jAdapter.createLog(name);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     return JavaUtilAdapter.createLog(name);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isPresent(String className) {
/*     */     try {
/* 107 */       Class.forName(className, false, LogAdapter.class.getClassLoader());
/* 108 */       return true;
/*     */     }
/* 110 */     catch (ClassNotFoundException ex) {
/* 111 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private enum LogApi {
/* 116 */     LOG4J, SLF4J_LAL, SLF4J, JUL;
/*     */   }
/*     */   
/*     */   private static class Log4jAdapter
/*     */   {
/*     */     public static Log createLog(String name) {
/* 122 */       return new LogAdapter.Log4jLog(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Slf4jAdapter
/*     */   {
/*     */     public static Log createLocationAwareLog(String name) {
/* 130 */       Logger logger = LoggerFactory.getLogger(name);
/* 131 */       return (logger instanceof LocationAwareLogger) ? new LogAdapter.Slf4jLocationAwareLog((LocationAwareLogger)logger) : new LogAdapter.Slf4jLog<>(logger);
/*     */     }
/*     */ 
/*     */     
/*     */     public static Log createLog(String name) {
/* 136 */       return new LogAdapter.Slf4jLog<>(LoggerFactory.getLogger(name));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class JavaUtilAdapter
/*     */   {
/*     */     public static Log createLog(String name) {
/* 144 */       return new LogAdapter.JavaUtilLog(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Log4jLog
/*     */     implements Log, Serializable
/*     */   {
/* 152 */     private static final String FQCN = Log4jLog.class.getName();
/*     */ 
/*     */     
/* 155 */     private static final LoggerContext loggerContext = LogManager.getContext(Log4jLog.class.getClassLoader(), false);
/*     */     
/*     */     private final ExtendedLogger logger;
/*     */     
/*     */     public Log4jLog(String name) {
/* 160 */       LoggerContext context = loggerContext;
/* 161 */       if (context == null)
/*     */       {
/* 163 */         context = LogManager.getContext(Log4jLog.class.getClassLoader(), false);
/*     */       }
/* 165 */       this.logger = context.getLogger(name);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 170 */       return this.logger.isEnabled(Level.FATAL);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 175 */       return this.logger.isEnabled(Level.ERROR);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/* 180 */       return this.logger.isEnabled(Level.WARN);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/* 185 */       return this.logger.isEnabled(Level.INFO);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/* 190 */       return this.logger.isEnabled(Level.DEBUG);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isTraceEnabled() {
/* 195 */       return this.logger.isEnabled(Level.TRACE);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message) {
/* 200 */       log(Level.FATAL, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message, Throwable exception) {
/* 205 */       log(Level.FATAL, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message) {
/* 210 */       log(Level.ERROR, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message, Throwable exception) {
/* 215 */       log(Level.ERROR, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message) {
/* 220 */       log(Level.WARN, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message, Throwable exception) {
/* 225 */       log(Level.WARN, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message) {
/* 230 */       log(Level.INFO, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message, Throwable exception) {
/* 235 */       log(Level.INFO, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message) {
/* 240 */       log(Level.DEBUG, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message, Throwable exception) {
/* 245 */       log(Level.DEBUG, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message) {
/* 250 */       log(Level.TRACE, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message, Throwable exception) {
/* 255 */       log(Level.TRACE, message, exception);
/*     */     }
/*     */     
/*     */     private void log(Level level, Object message, Throwable exception) {
/* 259 */       if (message instanceof String) {
/*     */ 
/*     */         
/* 262 */         if (exception != null) {
/* 263 */           this.logger.logIfEnabled(FQCN, level, null, (String)message, exception);
/*     */         } else {
/*     */           
/* 266 */           this.logger.logIfEnabled(FQCN, level, null, (String)message);
/*     */         } 
/*     */       } else {
/*     */         
/* 270 */         this.logger.logIfEnabled(FQCN, level, null, message, exception);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Slf4jLog<T extends Logger>
/*     */     implements Log, Serializable
/*     */   {
/*     */     protected final String name;
/*     */     
/*     */     protected final transient T logger;
/*     */     
/*     */     public Slf4jLog(T logger) {
/* 284 */       this.name = logger.getName();
/* 285 */       this.logger = logger;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 290 */       return isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 295 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/* 300 */       return this.logger.isWarnEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/* 305 */       return this.logger.isInfoEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/* 310 */       return this.logger.isDebugEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isTraceEnabled() {
/* 315 */       return this.logger.isTraceEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message) {
/* 320 */       error(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message, Throwable exception) {
/* 325 */       error(message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message) {
/* 330 */       if (message instanceof String || this.logger.isErrorEnabled()) {
/* 331 */         this.logger.error(String.valueOf(message));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message, Throwable exception) {
/* 337 */       if (message instanceof String || this.logger.isErrorEnabled()) {
/* 338 */         this.logger.error(String.valueOf(message), exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message) {
/* 344 */       if (message instanceof String || this.logger.isWarnEnabled()) {
/* 345 */         this.logger.warn(String.valueOf(message));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message, Throwable exception) {
/* 351 */       if (message instanceof String || this.logger.isWarnEnabled()) {
/* 352 */         this.logger.warn(String.valueOf(message), exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message) {
/* 358 */       if (message instanceof String || this.logger.isInfoEnabled()) {
/* 359 */         this.logger.info(String.valueOf(message));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message, Throwable exception) {
/* 365 */       if (message instanceof String || this.logger.isInfoEnabled()) {
/* 366 */         this.logger.info(String.valueOf(message), exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message) {
/* 372 */       if (message instanceof String || this.logger.isDebugEnabled()) {
/* 373 */         this.logger.debug(String.valueOf(message));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message, Throwable exception) {
/* 379 */       if (message instanceof String || this.logger.isDebugEnabled()) {
/* 380 */         this.logger.debug(String.valueOf(message), exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message) {
/* 386 */       if (message instanceof String || this.logger.isTraceEnabled()) {
/* 387 */         this.logger.trace(String.valueOf(message));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message, Throwable exception) {
/* 393 */       if (message instanceof String || this.logger.isTraceEnabled()) {
/* 394 */         this.logger.trace(String.valueOf(message), exception);
/*     */       }
/*     */     }
/*     */     
/*     */     protected Object readResolve() {
/* 399 */       return LogAdapter.Slf4jAdapter.createLog(this.name);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Slf4jLocationAwareLog
/*     */     extends Slf4jLog<LocationAwareLogger>
/*     */     implements Serializable
/*     */   {
/* 407 */     private static final String FQCN = Slf4jLocationAwareLog.class.getName();
/*     */     
/*     */     public Slf4jLocationAwareLog(LocationAwareLogger logger) {
/* 410 */       super(logger);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message) {
/* 415 */       error(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message, Throwable exception) {
/* 420 */       error(message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message) {
/* 425 */       if (message instanceof String || this.logger.isErrorEnabled()) {
/* 426 */         this.logger.log(null, FQCN, 40, String.valueOf(message), null, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message, Throwable exception) {
/* 432 */       if (message instanceof String || this.logger.isErrorEnabled()) {
/* 433 */         this.logger.log(null, FQCN, 40, String.valueOf(message), null, exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message) {
/* 439 */       if (message instanceof String || this.logger.isWarnEnabled()) {
/* 440 */         this.logger.log(null, FQCN, 30, String.valueOf(message), null, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message, Throwable exception) {
/* 446 */       if (message instanceof String || this.logger.isWarnEnabled()) {
/* 447 */         this.logger.log(null, FQCN, 30, String.valueOf(message), null, exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message) {
/* 453 */       if (message instanceof String || this.logger.isInfoEnabled()) {
/* 454 */         this.logger.log(null, FQCN, 20, String.valueOf(message), null, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message, Throwable exception) {
/* 460 */       if (message instanceof String || this.logger.isInfoEnabled()) {
/* 461 */         this.logger.log(null, FQCN, 20, String.valueOf(message), null, exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message) {
/* 467 */       if (message instanceof String || this.logger.isDebugEnabled()) {
/* 468 */         this.logger.log(null, FQCN, 10, String.valueOf(message), null, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message, Throwable exception) {
/* 474 */       if (message instanceof String || this.logger.isDebugEnabled()) {
/* 475 */         this.logger.log(null, FQCN, 10, String.valueOf(message), null, exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message) {
/* 481 */       if (message instanceof String || this.logger.isTraceEnabled()) {
/* 482 */         this.logger.log(null, FQCN, 0, String.valueOf(message), null, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message, Throwable exception) {
/* 488 */       if (message instanceof String || this.logger.isTraceEnabled()) {
/* 489 */         this.logger.log(null, FQCN, 0, String.valueOf(message), null, exception);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object readResolve() {
/* 495 */       return LogAdapter.Slf4jAdapter.createLocationAwareLog(this.name);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class JavaUtilLog
/*     */     implements Log, Serializable
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final transient Logger logger;
/*     */     
/*     */     public JavaUtilLog(String name) {
/* 508 */       this.name = name;
/* 509 */       this.logger = Logger.getLogger(name);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 514 */       return isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 519 */       return this.logger.isLoggable(Level.SEVERE);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/* 524 */       return this.logger.isLoggable(Level.WARNING);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/* 529 */       return this.logger.isLoggable(Level.INFO);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/* 534 */       return this.logger.isLoggable(Level.FINE);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isTraceEnabled() {
/* 539 */       return this.logger.isLoggable(Level.FINEST);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message) {
/* 544 */       error(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void fatal(Object message, Throwable exception) {
/* 549 */       error(message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message) {
/* 554 */       log(Level.SEVERE, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Object message, Throwable exception) {
/* 559 */       log(Level.SEVERE, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message) {
/* 564 */       log(Level.WARNING, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(Object message, Throwable exception) {
/* 569 */       log(Level.WARNING, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message) {
/* 574 */       log(Level.INFO, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(Object message, Throwable exception) {
/* 579 */       log(Level.INFO, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message) {
/* 584 */       log(Level.FINE, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(Object message, Throwable exception) {
/* 589 */       log(Level.FINE, message, exception);
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message) {
/* 594 */       log(Level.FINEST, message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void trace(Object message, Throwable exception) {
/* 599 */       log(Level.FINEST, message, exception);
/*     */     }
/*     */     
/*     */     private void log(Level level, Object message, Throwable exception) {
/* 603 */       if (this.logger.isLoggable(level)) {
/*     */         LogRecord rec;
/* 605 */         if (message instanceof LogRecord) {
/* 606 */           rec = (LogRecord)message;
/*     */         } else {
/*     */           
/* 609 */           rec = new LogAdapter.LocationResolvingLogRecord(level, String.valueOf(message));
/* 610 */           rec.setLoggerName(this.name);
/* 611 */           rec.setResourceBundleName(this.logger.getResourceBundleName());
/* 612 */           rec.setResourceBundle(this.logger.getResourceBundle());
/* 613 */           rec.setThrown(exception);
/*     */         } 
/* 615 */         this.logger.log(rec);
/*     */       } 
/*     */     }
/*     */     
/*     */     protected Object readResolve() {
/* 620 */       return new JavaUtilLog(this.name);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class LocationResolvingLogRecord
/*     */     extends LogRecord
/*     */   {
/* 628 */     private static final String FQCN = LogAdapter.JavaUtilLog.class.getName();
/*     */     
/*     */     private volatile boolean resolved;
/*     */     
/*     */     public LocationResolvingLogRecord(Level level, String msg) {
/* 633 */       super(level, msg);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getSourceClassName() {
/* 638 */       if (!this.resolved) {
/* 639 */         resolve();
/*     */       }
/* 641 */       return super.getSourceClassName();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSourceClassName(String sourceClassName) {
/* 646 */       super.setSourceClassName(sourceClassName);
/* 647 */       this.resolved = true;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getSourceMethodName() {
/* 652 */       if (!this.resolved) {
/* 653 */         resolve();
/*     */       }
/* 655 */       return super.getSourceMethodName();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSourceMethodName(String sourceMethodName) {
/* 660 */       super.setSourceMethodName(sourceMethodName);
/* 661 */       this.resolved = true;
/*     */     }
/*     */     
/*     */     private void resolve() {
/* 665 */       StackTraceElement[] stack = (new Throwable()).getStackTrace();
/* 666 */       String sourceClassName = null;
/* 667 */       String sourceMethodName = null;
/* 668 */       boolean found = false;
/* 669 */       for (StackTraceElement element : stack) {
/* 670 */         String className = element.getClassName();
/* 671 */         if (FQCN.equals(className)) {
/* 672 */           found = true;
/*     */         }
/* 674 */         else if (found) {
/* 675 */           sourceClassName = className;
/* 676 */           sourceMethodName = element.getMethodName();
/*     */           break;
/*     */         } 
/*     */       } 
/* 680 */       setSourceClassName(sourceClassName);
/* 681 */       setSourceMethodName(sourceMethodName);
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object writeReplace() {
/* 686 */       LogRecord serialized = new LogRecord(getLevel(), getMessage());
/* 687 */       serialized.setLoggerName(getLoggerName());
/* 688 */       serialized.setResourceBundle(getResourceBundle());
/* 689 */       serialized.setResourceBundleName(getResourceBundleName());
/* 690 */       serialized.setSourceClassName(getSourceClassName());
/* 691 */       serialized.setSourceMethodName(getSourceMethodName());
/* 692 */       serialized.setSequenceNumber(getSequenceNumber());
/* 693 */       serialized.setParameters(getParameters());
/* 694 */       serialized.setThreadID(getThreadID());
/* 695 */       serialized.setMillis(getMillis());
/* 696 */       serialized.setThrown(getThrown());
/* 697 */       return serialized;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/apache/commons/logging/LogAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */