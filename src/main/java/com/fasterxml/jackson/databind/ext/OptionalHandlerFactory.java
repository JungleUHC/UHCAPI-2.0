/*     */ package com.fasterxml.jackson.databind.ext;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.deser.Deserializers;
/*     */ import com.fasterxml.jackson.databind.ser.Serializers;
/*     */ import com.fasterxml.jackson.databind.ser.std.DateSerializer;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptionalHandlerFactory
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
/*     */   private static final String SERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
/*     */   private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
/*     */   private static final String SERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMSerializer";
/*     */   private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
/*     */   private static final String DESERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
/*     */   private static final Class<?> CLASS_DOM_NODE;
/*     */   private static final Class<?> CLASS_DOM_DOCUMENT;
/*     */   private static final Java7Handlers _jdk7Helper;
/*     */   
/*     */   static {
/*  53 */     Class<?> doc = null, node = null;
/*     */     try {
/*  55 */       node = Node.class;
/*  56 */       doc = Document.class;
/*  57 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  63 */     CLASS_DOM_NODE = node;
/*  64 */     CLASS_DOM_DOCUMENT = doc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     Java7Handlers x = null;
/*     */     try {
/*  75 */       x = Java7Handlers.instance();
/*  76 */     } catch (Throwable throwable) {}
/*  77 */     _jdk7Helper = x;
/*     */   }
/*     */   
/*  80 */   public static final OptionalHandlerFactory instance = new OptionalHandlerFactory();
/*     */   
/*     */   private final Map<String, String> _sqlDeserializers;
/*     */   
/*     */   private final Map<String, Object> _sqlSerializers;
/*     */   
/*     */   private static final String CLS_NAME_JAVA_SQL_TIMESTAMP = "java.sql.Timestamp";
/*     */   
/*     */   private static final String CLS_NAME_JAVA_SQL_DATE = "java.sql.Date";
/*     */   private static final String CLS_NAME_JAVA_SQL_TIME = "java.sql.Time";
/*     */   private static final String CLS_NAME_JAVA_SQL_BLOB = "java.sql.Blob";
/*     */   private static final String CLS_NAME_JAVA_SQL_SERIALBLOB = "javax.sql.rowset.serial.SerialBlob";
/*     */   
/*     */   protected OptionalHandlerFactory() {
/*  94 */     this._sqlDeserializers = new HashMap<>();
/*  95 */     this._sqlDeserializers.put("java.sql.Date", "com.fasterxml.jackson.databind.deser.std.DateDeserializers$SqlDateDeserializer");
/*     */     
/*  97 */     this._sqlDeserializers.put("java.sql.Timestamp", "com.fasterxml.jackson.databind.deser.std.DateDeserializers$TimestampDeserializer");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 102 */     this._sqlSerializers = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     this._sqlSerializers.put("java.sql.Timestamp", DateSerializer.instance);
/* 108 */     this._sqlSerializers.put("java.sql.Date", "com.fasterxml.jackson.databind.ser.std.SqlDateSerializer");
/* 109 */     this._sqlSerializers.put("java.sql.Time", "com.fasterxml.jackson.databind.ser.std.SqlTimeSerializer");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     this._sqlSerializers.put("java.sql.Blob", "com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
/* 116 */     this._sqlSerializers.put("javax.sql.rowset.serial.SerialBlob", "com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
/*     */     String factoryName;
/* 128 */     Class<?> rawType = type.getRawClass();
/*     */     
/* 130 */     if (_IsXOfY(rawType, CLASS_DOM_NODE)) {
/* 131 */       return (JsonSerializer)instantiate("com.fasterxml.jackson.databind.ext.DOMSerializer", type);
/*     */     }
/*     */     
/* 134 */     if (_jdk7Helper != null) {
/* 135 */       JsonSerializer<?> ser = _jdk7Helper.getSerializerForJavaNioFilePath(rawType);
/* 136 */       if (ser != null) {
/* 137 */         return ser;
/*     */       }
/*     */     } 
/*     */     
/* 141 */     String className = rawType.getName();
/* 142 */     Object sqlHandler = this._sqlSerializers.get(className);
/*     */     
/* 144 */     if (sqlHandler != null) {
/* 145 */       if (sqlHandler instanceof JsonSerializer) {
/* 146 */         return (JsonSerializer)sqlHandler;
/*     */       }
/*     */       
/* 149 */       return (JsonSerializer)instantiate((String)sqlHandler, type);
/*     */     } 
/*     */ 
/*     */     
/* 153 */     if (className.startsWith("javax.xml.") || hasSuperClassStartingWith(rawType, "javax.xml.")) {
/* 154 */       factoryName = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
/*     */     } else {
/* 156 */       return null;
/*     */     } 
/*     */     
/* 159 */     Object ob = instantiate(factoryName, type);
/* 160 */     if (ob == null) {
/* 161 */       return null;
/*     */     }
/* 163 */     return ((Serializers)ob).findSerializer(config, type, beanDesc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*     */     String factoryName;
/* 170 */     Class<?> rawType = type.getRawClass();
/*     */     
/* 172 */     if (_jdk7Helper != null) {
/* 173 */       JsonDeserializer<?> deser = _jdk7Helper.getDeserializerForJavaNioFilePath(rawType);
/* 174 */       if (deser != null) {
/* 175 */         return deser;
/*     */       }
/*     */     } 
/* 178 */     if (_IsXOfY(rawType, CLASS_DOM_NODE)) {
/* 179 */       return (JsonDeserializer)instantiate("com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer", type);
/*     */     }
/* 181 */     if (_IsXOfY(rawType, CLASS_DOM_DOCUMENT)) {
/* 182 */       return (JsonDeserializer)instantiate("com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer", type);
/*     */     }
/* 184 */     String className = rawType.getName();
/* 185 */     String deserName = this._sqlDeserializers.get(className);
/* 186 */     if (deserName != null) {
/* 187 */       return (JsonDeserializer)instantiate(deserName, type);
/*     */     }
/*     */     
/* 190 */     if (className.startsWith("javax.xml.") || 
/* 191 */       hasSuperClassStartingWith(rawType, "javax.xml.")) {
/* 192 */       factoryName = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
/*     */     } else {
/* 194 */       return null;
/*     */     } 
/* 196 */     Object ob = instantiate(factoryName, type);
/* 197 */     if (ob == null) {
/* 198 */       return null;
/*     */     }
/* 200 */     return ((Deserializers)ob).findBeanDeserializer(type, config, beanDesc);
/*     */   }
/*     */   
/*     */   public boolean hasDeserializerFor(Class<?> valueType) {
/* 204 */     if (_IsXOfY(valueType, CLASS_DOM_NODE)) {
/* 205 */       return true;
/*     */     }
/* 207 */     if (_IsXOfY(valueType, CLASS_DOM_DOCUMENT)) {
/* 208 */       return true;
/*     */     }
/* 210 */     String className = valueType.getName();
/* 211 */     if (className.startsWith("javax.xml.") || 
/* 212 */       hasSuperClassStartingWith(valueType, "javax.xml.")) {
/* 213 */       return true;
/*     */     }
/*     */     
/* 216 */     return this._sqlDeserializers.containsKey(className);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean _IsXOfY(Class<?> valueType, Class<?> expType) {
/* 221 */     return (expType != null && expType.isAssignableFrom(valueType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object instantiate(String className, JavaType valueType) {
/*     */     try {
/* 233 */       return instantiate(Class.forName(className), valueType);
/* 234 */     } catch (Throwable e) {
/* 235 */       throw new IllegalStateException("Failed to find class `" + className + "` for handling values of type " + 
/* 236 */           ClassUtil.getTypeDescription(valueType) + ", problem: (" + e
/* 237 */           .getClass().getName() + ") " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Object instantiate(Class<?> handlerClass, JavaType valueType) {
/*     */     try {
/* 244 */       return ClassUtil.createInstance(handlerClass, false);
/* 245 */     } catch (Throwable e) {
/* 246 */       throw new IllegalStateException("Failed to create instance of `" + handlerClass
/* 247 */           .getName() + "` for handling values of type " + ClassUtil.getTypeDescription(valueType) + ", problem: (" + e
/* 248 */           .getClass().getName() + ") " + e.getMessage());
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
/*     */   private boolean hasSuperClassStartingWith(Class<?> rawType, String prefix) {
/* 262 */     for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
/* 263 */       if (supertype == Object.class) {
/* 264 */         return false;
/*     */       }
/* 266 */       if (supertype.getName().startsWith(prefix)) {
/* 267 */         return true;
/*     */       }
/*     */     } 
/* 270 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/OptionalHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */