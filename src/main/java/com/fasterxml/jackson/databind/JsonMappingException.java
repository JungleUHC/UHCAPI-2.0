/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonIgnore;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonMappingException
/*     */   extends DatabindException
/*     */ {
/*     */   private static final long serialVersionUID = 3L;
/*     */   static final int MAX_REFS_TO_LIST = 1000;
/*     */   protected LinkedList<Reference> _path;
/*     */   protected transient Closeable _processor;
/*     */   
/*     */   public static class Reference
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */     protected transient Object _from;
/*     */     protected String _fieldName;
/*  69 */     protected int _index = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected String _desc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Reference() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Reference(Object from) {
/*  86 */       this._from = from;
/*     */     }
/*     */     public Reference(Object from, String fieldName) {
/*  89 */       this._from = from;
/*  90 */       if (fieldName == null) {
/*  91 */         throw new NullPointerException("Cannot pass null fieldName");
/*     */       }
/*  93 */       this._fieldName = fieldName;
/*     */     }
/*     */     
/*     */     public Reference(Object from, int index) {
/*  97 */       this._from = from;
/*  98 */       this._index = index;
/*     */     }
/*     */     
/*     */     void setFieldName(String n) {
/* 102 */       this._fieldName = n;
/* 103 */     } void setIndex(int ix) { this._index = ix; } void setDescription(String d) {
/* 104 */       this._desc = d;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @JsonIgnore
/*     */     public Object getFrom() {
/* 117 */       return this._from;
/*     */     }
/* 119 */     public String getFieldName() { return this._fieldName; } public int getIndex() {
/* 120 */       return this._index;
/*     */     } public String getDescription() {
/* 122 */       if (this._desc == null) {
/* 123 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 125 */         if (this._from == null) {
/* 126 */           sb.append("UNKNOWN");
/*     */         } else {
/* 128 */           Class<?> cls = (this._from instanceof Class) ? (Class)this._from : this._from.getClass();
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 133 */           int arrays = 0;
/* 134 */           while (cls.isArray()) {
/* 135 */             cls = cls.getComponentType();
/* 136 */             arrays++;
/*     */           } 
/* 138 */           sb.append(cls.getName());
/* 139 */           while (--arrays >= 0) {
/* 140 */             sb.append("[]");
/*     */           }
/*     */         } 
/* 143 */         sb.append('[');
/* 144 */         if (this._fieldName != null) {
/* 145 */           sb.append('"');
/* 146 */           sb.append(this._fieldName);
/* 147 */           sb.append('"');
/* 148 */         } else if (this._index >= 0) {
/* 149 */           sb.append(this._index);
/*     */         } else {
/* 151 */           sb.append('?');
/*     */         } 
/* 153 */         sb.append(']');
/* 154 */         this._desc = sb.toString();
/*     */       } 
/* 156 */       return this._desc;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 161 */       return getDescription();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Object writeReplace() {
/* 172 */       getDescription();
/* 173 */       return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg) {
/* 209 */     super(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, Throwable rootCause) {
/* 215 */     super(msg, rootCause);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, JsonLocation loc) {
/* 221 */     super(msg, loc);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause) {
/* 227 */     super(msg, loc, rootCause);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMappingException(Closeable processor, String msg) {
/* 233 */     super(msg);
/* 234 */     this._processor = processor;
/* 235 */     if (processor instanceof JsonParser)
/*     */     {
/*     */ 
/*     */       
/* 239 */       this._location = ((JsonParser)processor).getTokenLocation();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMappingException(Closeable processor, String msg, Throwable problem) {
/* 247 */     super(msg, problem);
/* 248 */     this._processor = processor;
/*     */     
/* 250 */     if (problem instanceof JacksonException) {
/* 251 */       this._location = ((JacksonException)problem).getLocation();
/* 252 */     } else if (processor instanceof JsonParser) {
/* 253 */       this._location = ((JsonParser)processor).getTokenLocation();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMappingException(Closeable processor, String msg, JsonLocation loc) {
/* 261 */     super(msg, loc);
/* 262 */     this._processor = processor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(JsonParser p, String msg) {
/* 269 */     return new JsonMappingException((Closeable)p, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(JsonParser p, String msg, Throwable problem) {
/* 276 */     return new JsonMappingException((Closeable)p, msg, problem);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(JsonGenerator g, String msg) {
/* 283 */     return new JsonMappingException((Closeable)g, msg, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(JsonGenerator g, String msg, Throwable problem) {
/* 290 */     return new JsonMappingException((Closeable)g, msg, problem);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(DeserializationContext ctxt, String msg) {
/* 297 */     return new JsonMappingException((Closeable)ctxt.getParser(), msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(DeserializationContext ctxt, String msg, Throwable t) {
/* 304 */     return new JsonMappingException((Closeable)ctxt.getParser(), msg, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(SerializerProvider ctxt, String msg) {
/* 311 */     return new JsonMappingException((Closeable)ctxt.getGenerator(), msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException from(SerializerProvider ctxt, String msg, Throwable problem) {
/* 321 */     return new JsonMappingException((Closeable)ctxt.getGenerator(), msg, problem);
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
/*     */   public static JsonMappingException fromUnexpectedIOE(IOException src) {
/* 335 */     return new JsonMappingException(null, 
/* 336 */         String.format("Unexpected IOException (of type %s): %s", new Object[] {
/* 337 */             src.getClass().getName(), 
/* 338 */             ClassUtil.exceptionMessage(src)
/*     */           }));
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
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName) {
/* 351 */     return wrapWithPath(src, new Reference(refFrom, refFieldName));
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
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index) {
/* 363 */     return wrapWithPath(src, new Reference(refFrom, index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Reference ref) {
/*     */     JsonMappingException jme;
/* 375 */     if (src instanceof JsonMappingException) {
/* 376 */       jme = (JsonMappingException)src;
/*     */     } else {
/*     */       
/* 379 */       String msg = ClassUtil.exceptionMessage(src);
/*     */       
/* 381 */       if (msg == null || msg.isEmpty()) {
/* 382 */         msg = "(was " + src.getClass().getName() + ")";
/*     */       }
/*     */       
/* 385 */       Closeable proc = null;
/* 386 */       if (src instanceof JacksonException) {
/* 387 */         Object proc0 = ((JacksonException)src).getProcessor();
/* 388 */         if (proc0 instanceof Closeable) {
/* 389 */           proc = (Closeable)proc0;
/*     */         }
/*     */       } 
/* 392 */       jme = new JsonMappingException(proc, msg, src);
/*     */     } 
/* 394 */     jme.prependPath(ref);
/* 395 */     return jme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMappingException withCause(Throwable cause) {
/* 402 */     initCause(cause);
/* 403 */     return this;
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
/*     */   public List<Reference> getPath() {
/* 418 */     if (this._path == null) {
/* 419 */       return Collections.emptyList();
/*     */     }
/* 421 */     return Collections.unmodifiableList(this._path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPathReference() {
/* 430 */     return getPathReference(new StringBuilder()).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getPathReference(StringBuilder sb) {
/* 435 */     _appendPathDesc(sb);
/* 436 */     return sb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prependPath(Object referrer, String fieldName) {
/* 445 */     prependPath(new Reference(referrer, fieldName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prependPath(Object referrer, int index) {
/* 454 */     prependPath(new Reference(referrer, index));
/*     */   }
/*     */ 
/*     */   
/*     */   public void prependPath(Reference r) {
/* 459 */     if (this._path == null) {
/* 460 */       this._path = new LinkedList<>();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 466 */     if (this._path.size() < 1000) {
/* 467 */       this._path.addFirst(r);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JsonIgnore
/*     */   public Object getProcessor() {
/* 479 */     return this._processor;
/*     */   }
/*     */   
/*     */   public String getLocalizedMessage() {
/* 483 */     return _buildMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 492 */     return _buildMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _buildMessage() {
/* 498 */     String msg = super.getMessage();
/* 499 */     if (this._path == null) {
/* 500 */       return msg;
/*     */     }
/* 502 */     StringBuilder sb = (msg == null) ? new StringBuilder() : new StringBuilder(msg);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 508 */     sb.append(" (through reference chain: ");
/* 509 */     sb = getPathReference(sb);
/* 510 */     sb.append(')');
/* 511 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 517 */     return getClass().getName() + ": " + getMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _appendPathDesc(StringBuilder sb) {
/* 528 */     if (this._path == null) {
/*     */       return;
/*     */     }
/* 531 */     Iterator<Reference> it = this._path.iterator();
/* 532 */     while (it.hasNext()) {
/* 533 */       sb.append(((Reference)it.next()).toString());
/* 534 */       if (it.hasNext())
/* 535 */         sb.append("->"); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/JsonMappingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */