/*      */ package com.fasterxml.jackson.databind.type;
/*      */ 
/*      */ import com.fasterxml.jackson.core.type.TypeReference;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonNode;
/*      */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.LRUMap;
/*      */ import com.fasterxml.jackson.databind.util.LookupCache;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.GenericArrayType;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.lang.reflect.TypeVariable;
/*      */ import java.lang.reflect.WildcardType;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TypeFactory
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   41 */   private static final JavaType[] NO_TYPES = new JavaType[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   48 */   protected static final TypeFactory instance = new TypeFactory();
/*      */   
/*   50 */   protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   62 */   private static final Class<?> CLS_STRING = String.class;
/*   63 */   private static final Class<?> CLS_OBJECT = Object.class;
/*      */   
/*   65 */   private static final Class<?> CLS_COMPARABLE = Comparable.class;
/*   66 */   private static final Class<?> CLS_CLASS = Class.class;
/*   67 */   private static final Class<?> CLS_ENUM = Enum.class;
/*   68 */   private static final Class<?> CLS_JSON_NODE = JsonNode.class;
/*      */   
/*   70 */   private static final Class<?> CLS_BOOL = boolean.class;
/*   71 */   private static final Class<?> CLS_INT = int.class;
/*   72 */   private static final Class<?> CLS_LONG = long.class;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   81 */   protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
/*   82 */   protected static final SimpleType CORE_TYPE_INT = new SimpleType(CLS_INT);
/*   83 */   protected static final SimpleType CORE_TYPE_LONG = new SimpleType(CLS_LONG);
/*      */ 
/*      */   
/*   86 */   protected static final SimpleType CORE_TYPE_STRING = new SimpleType(CLS_STRING);
/*      */ 
/*      */   
/*   89 */   protected static final SimpleType CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   97 */   protected static final SimpleType CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  105 */   protected static final SimpleType CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   protected static final SimpleType CORE_TYPE_CLASS = new SimpleType(CLS_CLASS);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  121 */   protected static final SimpleType CORE_TYPE_JSON_NODE = new SimpleType(CLS_JSON_NODE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final LookupCache<Object, JavaType> _typeCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final TypeModifier[] _modifiers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final TypeParser _parser;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ClassLoader _classLoader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TypeFactory() {
/*  156 */     this((LookupCache<Object, JavaType>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected TypeFactory(LRUMap<Object, JavaType> typeCache) {
/*  165 */     this((LookupCache<Object, JavaType>)typeCache);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected TypeFactory(LookupCache<Object, JavaType> typeCache) {
/*      */     LRUMap lRUMap;
/*  172 */     if (typeCache == null) {
/*  173 */       lRUMap = new LRUMap(16, 200);
/*      */     }
/*  175 */     this._typeCache = (LookupCache<Object, JavaType>)lRUMap;
/*  176 */     this._parser = new TypeParser(this);
/*  177 */     this._modifiers = null;
/*  178 */     this._classLoader = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected TypeFactory(LRUMap<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
/*  188 */     this((LookupCache<Object, JavaType>)typeCache, p, mods, classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TypeFactory(LookupCache<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
/*      */     LRUMap lRUMap;
/*  197 */     if (typeCache == null) {
/*  198 */       lRUMap = new LRUMap(16, 200);
/*      */     }
/*  200 */     this._typeCache = (LookupCache<Object, JavaType>)lRUMap;
/*      */     
/*  202 */     this._parser = p.withFactory(this);
/*  203 */     this._modifiers = mods;
/*  204 */     this._classLoader = classLoader;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeFactory withModifier(TypeModifier mod) {
/*      */     TypeModifier[] mods;
/*  214 */     LookupCache<Object, JavaType> typeCache = this._typeCache;
/*      */     
/*  216 */     if (mod == null) {
/*  217 */       mods = null;
/*      */ 
/*      */       
/*  220 */       typeCache = null;
/*  221 */     } else if (this._modifiers == null) {
/*  222 */       mods = new TypeModifier[] { mod };
/*      */ 
/*      */       
/*  225 */       typeCache = null;
/*      */     } else {
/*      */       
/*  228 */       mods = (TypeModifier[])ArrayBuilders.insertInListNoDup((Object[])this._modifiers, mod);
/*      */     } 
/*  230 */     return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeFactory withClassLoader(ClassLoader classLoader) {
/*  238 */     return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TypeFactory withCache(LRUMap<Object, JavaType> cache) {
/*  251 */     return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeFactory withCache(LookupCache<Object, JavaType> cache) {
/*  262 */     return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TypeFactory defaultInstance() {
/*  270 */     return instance;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearCache() {
/*  283 */     this._typeCache.clear();
/*      */   }
/*      */   
/*      */   public ClassLoader getClassLoader() {
/*  287 */     return this._classLoader;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JavaType unknownType() {
/*  302 */     return defaultInstance()._unknownType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> rawClass(Type t) {
/*  312 */     if (t instanceof Class) {
/*  313 */       return (Class)t;
/*      */     }
/*      */     
/*  316 */     return defaultInstance().constructType(t).getRawClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?> findClass(String className) throws ClassNotFoundException {
/*  333 */     if (className.indexOf('.') < 0) {
/*  334 */       Class<?> prim = _findPrimitive(className);
/*  335 */       if (prim != null) {
/*  336 */         return prim;
/*      */       }
/*      */     } 
/*      */     
/*  340 */     Throwable prob = null;
/*  341 */     ClassLoader loader = getClassLoader();
/*  342 */     if (loader == null) {
/*  343 */       loader = Thread.currentThread().getContextClassLoader();
/*      */     }
/*  345 */     if (loader != null) {
/*      */       try {
/*  347 */         return classForName(className, true, loader);
/*  348 */       } catch (Exception e) {
/*  349 */         prob = ClassUtil.getRootCause(e);
/*      */       } 
/*      */     }
/*      */     try {
/*  353 */       return classForName(className);
/*  354 */     } catch (Exception e) {
/*  355 */       if (prob == null) {
/*  356 */         prob = ClassUtil.getRootCause(e);
/*      */       }
/*      */       
/*  359 */       ClassUtil.throwIfRTE(prob);
/*  360 */       throw new ClassNotFoundException(prob.getMessage(), prob);
/*      */     } 
/*      */   }
/*      */   
/*      */   protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
/*  365 */     return Class.forName(name, true, loader);
/*      */   }
/*      */   
/*      */   protected Class<?> classForName(String name) throws ClassNotFoundException {
/*  369 */     return Class.forName(name);
/*      */   }
/*      */ 
/*      */   
/*      */   protected Class<?> _findPrimitive(String className) {
/*  374 */     if ("int".equals(className)) return int.class; 
/*  375 */     if ("long".equals(className)) return long.class; 
/*  376 */     if ("float".equals(className)) return float.class; 
/*  377 */     if ("double".equals(className)) return double.class; 
/*  378 */     if ("boolean".equals(className)) return boolean.class; 
/*  379 */     if ("byte".equals(className)) return byte.class; 
/*  380 */     if ("char".equals(className)) return char.class; 
/*  381 */     if ("short".equals(className)) return short.class; 
/*  382 */     if ("void".equals(className)) return void.class; 
/*  383 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) throws IllegalArgumentException {
/*  408 */     return constructSpecializedType(baseType, subclass, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass, boolean relaxedCompatibilityCheck) throws IllegalArgumentException {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: invokevirtual getRawClass : ()Ljava/lang/Class;
/*      */     //   4: astore #4
/*      */     //   6: aload #4
/*      */     //   8: aload_2
/*      */     //   9: if_acmpne -> 14
/*      */     //   12: aload_1
/*      */     //   13: areturn
/*      */     //   14: aload #4
/*      */     //   16: ldc java/lang/Object
/*      */     //   18: if_acmpne -> 35
/*      */     //   21: aload_0
/*      */     //   22: aconst_null
/*      */     //   23: aload_2
/*      */     //   24: getstatic com/fasterxml/jackson/databind/type/TypeFactory.EMPTY_BINDINGS : Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   27: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   30: astore #5
/*      */     //   32: goto -> 266
/*      */     //   35: aload #4
/*      */     //   37: aload_2
/*      */     //   38: invokevirtual isAssignableFrom : (Ljava/lang/Class;)Z
/*      */     //   41: ifne -> 75
/*      */     //   44: new java/lang/IllegalArgumentException
/*      */     //   47: dup
/*      */     //   48: ldc 'Class %s not subtype of %s'
/*      */     //   50: iconst_2
/*      */     //   51: anewarray java/lang/Object
/*      */     //   54: dup
/*      */     //   55: iconst_0
/*      */     //   56: aload_2
/*      */     //   57: invokestatic nameOf : (Ljava/lang/Class;)Ljava/lang/String;
/*      */     //   60: aastore
/*      */     //   61: dup
/*      */     //   62: iconst_1
/*      */     //   63: aload_1
/*      */     //   64: invokestatic getTypeDescription : (Lcom/fasterxml/jackson/databind/JavaType;)Ljava/lang/String;
/*      */     //   67: aastore
/*      */     //   68: invokestatic format : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   71: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   74: athrow
/*      */     //   75: aload_1
/*      */     //   76: invokevirtual isContainerType : ()Z
/*      */     //   79: ifeq -> 195
/*      */     //   82: aload_1
/*      */     //   83: invokevirtual isMapLikeType : ()Z
/*      */     //   86: ifeq -> 136
/*      */     //   89: aload_2
/*      */     //   90: ldc java/util/HashMap
/*      */     //   92: if_acmpeq -> 113
/*      */     //   95: aload_2
/*      */     //   96: ldc java/util/LinkedHashMap
/*      */     //   98: if_acmpeq -> 113
/*      */     //   101: aload_2
/*      */     //   102: ldc java/util/EnumMap
/*      */     //   104: if_acmpeq -> 113
/*      */     //   107: aload_2
/*      */     //   108: ldc java/util/TreeMap
/*      */     //   110: if_acmpne -> 195
/*      */     //   113: aload_0
/*      */     //   114: aconst_null
/*      */     //   115: aload_2
/*      */     //   116: aload_2
/*      */     //   117: aload_1
/*      */     //   118: invokevirtual getKeyType : ()Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   121: aload_1
/*      */     //   122: invokevirtual getContentType : ()Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   125: invokestatic create : (Ljava/lang/Class;Lcom/fasterxml/jackson/databind/JavaType;Lcom/fasterxml/jackson/databind/JavaType;)Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   128: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   131: astore #5
/*      */     //   133: goto -> 266
/*      */     //   136: aload_1
/*      */     //   137: invokevirtual isCollectionLikeType : ()Z
/*      */     //   140: ifeq -> 195
/*      */     //   143: aload_2
/*      */     //   144: ldc java/util/ArrayList
/*      */     //   146: if_acmpeq -> 167
/*      */     //   149: aload_2
/*      */     //   150: ldc java/util/LinkedList
/*      */     //   152: if_acmpeq -> 167
/*      */     //   155: aload_2
/*      */     //   156: ldc java/util/HashSet
/*      */     //   158: if_acmpeq -> 167
/*      */     //   161: aload_2
/*      */     //   162: ldc java/util/TreeSet
/*      */     //   164: if_acmpne -> 186
/*      */     //   167: aload_0
/*      */     //   168: aconst_null
/*      */     //   169: aload_2
/*      */     //   170: aload_2
/*      */     //   171: aload_1
/*      */     //   172: invokevirtual getContentType : ()Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   175: invokestatic create : (Ljava/lang/Class;Lcom/fasterxml/jackson/databind/JavaType;)Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   178: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   181: astore #5
/*      */     //   183: goto -> 266
/*      */     //   186: aload #4
/*      */     //   188: ldc java/util/EnumSet
/*      */     //   190: if_acmpne -> 195
/*      */     //   193: aload_1
/*      */     //   194: areturn
/*      */     //   195: aload_1
/*      */     //   196: invokevirtual getBindings : ()Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   199: invokevirtual isEmpty : ()Z
/*      */     //   202: ifeq -> 219
/*      */     //   205: aload_0
/*      */     //   206: aconst_null
/*      */     //   207: aload_2
/*      */     //   208: getstatic com/fasterxml/jackson/databind/type/TypeFactory.EMPTY_BINDINGS : Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   211: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   214: astore #5
/*      */     //   216: goto -> 266
/*      */     //   219: aload_2
/*      */     //   220: invokevirtual getTypeParameters : ()[Ljava/lang/reflect/TypeVariable;
/*      */     //   223: arraylength
/*      */     //   224: istore #6
/*      */     //   226: iload #6
/*      */     //   228: ifne -> 245
/*      */     //   231: aload_0
/*      */     //   232: aconst_null
/*      */     //   233: aload_2
/*      */     //   234: getstatic com/fasterxml/jackson/databind/type/TypeFactory.EMPTY_BINDINGS : Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   237: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   240: astore #5
/*      */     //   242: goto -> 266
/*      */     //   245: aload_0
/*      */     //   246: aload_1
/*      */     //   247: iload #6
/*      */     //   249: aload_2
/*      */     //   250: iload_3
/*      */     //   251: invokespecial _bindingsForSubtype : (Lcom/fasterxml/jackson/databind/JavaType;ILjava/lang/Class;Z)Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   254: astore #7
/*      */     //   256: aload_0
/*      */     //   257: aconst_null
/*      */     //   258: aload_2
/*      */     //   259: aload #7
/*      */     //   261: invokevirtual _fromClass : (Lcom/fasterxml/jackson/databind/type/ClassStack;Ljava/lang/Class;Lcom/fasterxml/jackson/databind/type/TypeBindings;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   264: astore #5
/*      */     //   266: aload #5
/*      */     //   268: aload_1
/*      */     //   269: invokevirtual withHandlersFrom : (Lcom/fasterxml/jackson/databind/JavaType;)Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   272: astore #5
/*      */     //   274: aload #5
/*      */     //   276: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #433	-> 0
/*      */     //   #434	-> 6
/*      */     //   #435	-> 12
/*      */     //   #441	-> 14
/*      */     //   #442	-> 21
/*      */     //   #443	-> 32
/*      */     //   #445	-> 35
/*      */     //   #446	-> 44
/*      */     //   #447	-> 57
/*      */     //   #446	-> 68
/*      */     //   #453	-> 75
/*      */     //   #454	-> 82
/*      */     //   #455	-> 89
/*      */     //   #459	-> 113
/*      */     //   #460	-> 118
/*      */     //   #459	-> 128
/*      */     //   #461	-> 133
/*      */     //   #463	-> 136
/*      */     //   #464	-> 143
/*      */     //   #468	-> 167
/*      */     //   #469	-> 172
/*      */     //   #468	-> 178
/*      */     //   #470	-> 183
/*      */     //   #474	-> 186
/*      */     //   #475	-> 193
/*      */     //   #480	-> 195
/*      */     //   #481	-> 205
/*      */     //   #482	-> 216
/*      */     //   #486	-> 219
/*      */     //   #487	-> 226
/*      */     //   #488	-> 231
/*      */     //   #489	-> 242
/*      */     //   #492	-> 245
/*      */     //   #494	-> 256
/*      */     //   #500	-> 266
/*      */     //   #501	-> 274
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   32	3	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   133	3	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   183	3	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   216	3	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   242	3	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   226	40	6	typeParamCount	I
/*      */     //   256	10	7	tb	Lcom/fasterxml/jackson/databind/type/TypeBindings;
/*      */     //   0	277	0	this	Lcom/fasterxml/jackson/databind/type/TypeFactory;
/*      */     //   0	277	1	baseType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     //   0	277	2	subclass	Ljava/lang/Class;
/*      */     //   0	277	3	relaxedCompatibilityCheck	Z
/*      */     //   6	271	4	rawBase	Ljava/lang/Class;
/*      */     //   266	11	5	newType	Lcom/fasterxml/jackson/databind/JavaType;
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	277	2	subclass	Ljava/lang/Class<*>;
/*      */     //   6	271	4	rawBase	Ljava/lang/Class<*>;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass, boolean relaxedCompatibilityCheck) {
/*  507 */     PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];
/*  508 */     for (int i = 0; i < typeParamCount; i++) {
/*  509 */       placeholders[i] = new PlaceholderForType(i);
/*      */     }
/*  511 */     TypeBindings b = TypeBindings.create(subclass, (JavaType[])placeholders);
/*      */     
/*  513 */     JavaType tmpSub = _fromClass(null, subclass, b);
/*      */     
/*  515 */     JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
/*  516 */     if (baseWithPlaceholders == null) {
/*  517 */       throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", new Object[] { baseType
/*  518 */               .getRawClass().getName(), subclass
/*  519 */               .getName() }));
/*      */     }
/*      */     
/*  522 */     String error = _resolveTypePlaceholders(baseType, baseWithPlaceholders);
/*  523 */     if (error != null)
/*      */     {
/*      */       
/*  526 */       if (!relaxedCompatibilityCheck) {
/*  527 */         throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass
/*  528 */             .getName() + ", problem: " + error);
/*      */       }
/*      */     }
/*      */     
/*  532 */     JavaType[] typeParams = new JavaType[typeParamCount];
/*  533 */     for (int j = 0; j < typeParamCount; j++) {
/*  534 */       JavaType t = placeholders[j].actualType();
/*      */ 
/*      */ 
/*      */       
/*  538 */       if (t == null) {
/*  539 */         t = unknownType();
/*      */       }
/*  541 */       typeParams[j] = t;
/*      */     } 
/*  543 */     return TypeBindings.create(subclass, typeParams);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String _resolveTypePlaceholders(JavaType sourceType, JavaType actualType) throws IllegalArgumentException {
/*  549 */     List<JavaType> expectedTypes = sourceType.getBindings().getTypeParameters();
/*  550 */     List<JavaType> actualTypes = actualType.getBindings().getTypeParameters();
/*      */     
/*  552 */     int actCount = actualTypes.size();
/*      */     
/*  554 */     for (int i = 0, expCount = expectedTypes.size(); i < expCount; i++) {
/*  555 */       JavaType exp = expectedTypes.get(i);
/*  556 */       JavaType act = (i < actCount) ? actualTypes.get(i) : unknownType();
/*      */       
/*  558 */       if (!_verifyAndResolvePlaceholders(exp, act))
/*      */       {
/*      */ 
/*      */         
/*  562 */         if (!exp.hasRawClass(Object.class))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  571 */           if (i != 0 || 
/*  572 */             !sourceType.isMapLikeType() || 
/*  573 */             !act.hasRawClass(Object.class))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  579 */             if (!exp.isInterface() || 
/*  580 */               !exp.isTypeOrSuperTypeOf(act.getRawClass()))
/*      */             {
/*      */ 
/*      */               
/*  584 */               return String.format("Type parameter #%d/%d differs; can not specialize %s with %s", new Object[] {
/*  585 */                     Integer.valueOf(i + 1), Integer.valueOf(expCount), exp.toCanonical(), act.toCanonical()
/*      */                   }); }  }  }  } 
/*      */     } 
/*  588 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean _verifyAndResolvePlaceholders(JavaType exp, JavaType act) {
/*  594 */     if (act instanceof PlaceholderForType) {
/*  595 */       ((PlaceholderForType)act).actualType(exp);
/*  596 */       return true;
/*      */     } 
/*      */ 
/*      */     
/*  600 */     if (exp.getRawClass() != act.getRawClass()) {
/*  601 */       return false;
/*      */     }
/*      */     
/*  604 */     List<JavaType> expectedTypes = exp.getBindings().getTypeParameters();
/*  605 */     List<JavaType> actualTypes = act.getBindings().getTypeParameters();
/*  606 */     for (int i = 0, len = expectedTypes.size(); i < len; i++) {
/*  607 */       JavaType exp2 = expectedTypes.get(i);
/*  608 */       JavaType act2 = actualTypes.get(i);
/*  609 */       if (!_verifyAndResolvePlaceholders(exp2, act2)) {
/*  610 */         return false;
/*      */       }
/*      */     } 
/*  613 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/*  629 */     Class<?> rawBase = baseType.getRawClass();
/*  630 */     if (rawBase == superClass) {
/*  631 */       return baseType;
/*      */     }
/*  633 */     JavaType superType = baseType.findSuperType(superClass);
/*  634 */     if (superType == null) {
/*      */       
/*  636 */       if (!superClass.isAssignableFrom(rawBase)) {
/*  637 */         throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass
/*  638 */                 .getName(), baseType }));
/*      */       }
/*      */       
/*  641 */       throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass
/*      */               
/*  643 */               .getName(), baseType }));
/*      */     } 
/*  645 */     return superType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/*  660 */     return this._parser.parse(canonical);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/*  674 */     JavaType match = type.findSuperType(expType);
/*  675 */     if (match == null) {
/*  676 */       return NO_TYPES;
/*      */     }
/*  678 */     return match.getBindings().typeParameterArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/*  686 */     return findTypeParameters(constructType(clz, bindings), expType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/*  694 */     return findTypeParameters(constructType(clz), expType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/*  709 */     if (type1 == null) {
/*  710 */       return type2;
/*      */     }
/*  712 */     if (type2 == null) {
/*  713 */       return type1;
/*      */     }
/*  715 */     Class<?> raw1 = type1.getRawClass();
/*  716 */     Class<?> raw2 = type2.getRawClass();
/*  717 */     if (raw1 == raw2) {
/*  718 */       return type1;
/*      */     }
/*      */     
/*  721 */     if (raw1.isAssignableFrom(raw2)) {
/*  722 */       return type2;
/*      */     }
/*  724 */     return type1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructType(Type type) {
/*  734 */     return _fromAny(null, type, EMPTY_BINDINGS);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructType(TypeReference<?> typeRef) {
/*  740 */     return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType resolveMemberType(Type type, TypeBindings contextBindings) {
/*  778 */     return _fromAny(null, type, contextBindings);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructType(Type type, TypeBindings bindings) {
/*  801 */     if (type instanceof Class) {
/*  802 */       JavaType resultType = _fromClass(null, (Class)type, bindings);
/*  803 */       return _applyModifiers(type, resultType);
/*      */     } 
/*  805 */     return _fromAny(null, type, bindings);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructType(Type type, Class<?> contextClass) {
/*  813 */     JavaType contextType = (contextClass == null) ? null : constructType(contextClass);
/*  814 */     return constructType(type, contextType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructType(Type type, JavaType contextType) {
/*      */     TypeBindings bindings;
/*  823 */     if (contextType == null) {
/*  824 */       bindings = EMPTY_BINDINGS;
/*      */     } else {
/*  826 */       bindings = contextType.getBindings();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  831 */       if (type.getClass() != Class.class)
/*      */       {
/*      */         
/*  834 */         while (bindings.isEmpty()) {
/*  835 */           contextType = contextType.getSuperClass();
/*  836 */           if (contextType == null) {
/*      */             break;
/*      */           }
/*  839 */           bindings = contextType.getBindings();
/*      */         } 
/*      */       }
/*      */     } 
/*  843 */     return _fromAny(null, type, bindings);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayType constructArrayType(Class<?> elementType) {
/*  859 */     return ArrayType.construct(_fromAny(null, elementType, null), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayType constructArrayType(JavaType elementType) {
/*  869 */     return ArrayType.construct(elementType, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/*  880 */     return constructCollectionType(collectionClass, 
/*  881 */         _fromClass(null, elementClass, EMPTY_BINDINGS));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/*  893 */     TypeBindings bindings = TypeBindings.createIfNeeded(collectionClass, elementType);
/*  894 */     CollectionType result = (CollectionType)_fromClass(null, collectionClass, bindings);
/*      */ 
/*      */     
/*  897 */     if (bindings.isEmpty() && elementType != null) {
/*  898 */       JavaType t = result.findSuperType(Collection.class);
/*  899 */       JavaType realET = t.getContentType();
/*  900 */       if (!realET.equals(elementType))
/*  901 */         throw new IllegalArgumentException(String.format("Non-generic Collection class %s did not resolve to something with element type %s but %s ", new Object[] {
/*      */                 
/*  903 */                 ClassUtil.nameOf(collectionClass), elementType, realET
/*      */               })); 
/*      */     } 
/*  906 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/*  916 */     return constructCollectionLikeType(collectionClass, 
/*  917 */         _fromClass(null, elementClass, EMPTY_BINDINGS));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/*  927 */     JavaType type = _fromClass(null, collectionClass, 
/*  928 */         TypeBindings.createIfNeeded(collectionClass, elementType));
/*  929 */     if (type instanceof CollectionLikeType) {
/*  930 */       return (CollectionLikeType)type;
/*      */     }
/*  932 */     return CollectionLikeType.upgradeFrom(type, elementType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*      */     JavaType kt;
/*      */     JavaType vt;
/*  944 */     if (mapClass == Properties.class) {
/*  945 */       kt = vt = CORE_TYPE_STRING;
/*      */     } else {
/*  947 */       kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/*  948 */       vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*      */     } 
/*  950 */     return constructMapType(mapClass, kt, vt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/*  959 */     TypeBindings bindings = TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType });
/*  960 */     MapType result = (MapType)_fromClass(null, mapClass, bindings);
/*      */ 
/*      */     
/*  963 */     if (bindings.isEmpty()) {
/*  964 */       JavaType t = result.findSuperType(Map.class);
/*  965 */       JavaType realKT = t.getKeyType();
/*  966 */       if (!realKT.equals(keyType))
/*  967 */         throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with key type %s but %s ", new Object[] {
/*      */                 
/*  969 */                 ClassUtil.nameOf(mapClass), keyType, realKT
/*      */               })); 
/*  971 */       JavaType realVT = t.getContentType();
/*  972 */       if (!realVT.equals(valueType))
/*  973 */         throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with value type %s but %s ", new Object[] {
/*      */                 
/*  975 */                 ClassUtil.nameOf(mapClass), valueType, realVT
/*      */               })); 
/*      */     } 
/*  978 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*  988 */     return constructMapLikeType(mapClass, 
/*  989 */         _fromClass(null, keyClass, EMPTY_BINDINGS), 
/*  990 */         _fromClass(null, valueClass, EMPTY_BINDINGS));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 1001 */     JavaType type = _fromClass(null, mapClass, 
/* 1002 */         TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 1003 */     if (type instanceof MapLikeType) {
/* 1004 */       return (MapLikeType)type;
/*      */     }
/* 1006 */     return MapLikeType.upgradeFrom(type, keyType, valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 1015 */     return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 1029 */     return constructSimpleType(rawType, parameterTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 1042 */     return ReferenceType.construct(rawType, 
/* 1043 */         TypeBindings.create(rawType, referredType), null, null, referredType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType uncheckedSimpleType(Class<?> cls) {
/* 1062 */     return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 1083 */     int len = parameterClasses.length;
/* 1084 */     JavaType[] pt = new JavaType[len];
/* 1085 */     for (int i = 0; i < len; i++) {
/* 1086 */       pt[i] = _fromClass(null, parameterClasses[i], EMPTY_BINDINGS);
/*      */     }
/* 1088 */     return constructParametricType(parametrized, pt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 1109 */     return constructParametricType(rawType, TypeBindings.create(rawType, parameterTypes));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructParametricType(Class<?> rawType, TypeBindings parameterTypes) {
/* 1133 */     JavaType resultType = _fromClass(null, rawType, parameterTypes);
/* 1134 */     return _applyModifiers(rawType, resultType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 1145 */     return constructParametricType(parametrized, parameterTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 1156 */     return constructParametricType(parametrized, parameterClasses);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 1178 */     return constructCollectionType(collectionClass, unknownType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 1193 */     return constructCollectionLikeType(collectionClass, unknownType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapType constructRawMapType(Class<? extends Map> mapClass) {
/* 1208 */     return constructMapType(mapClass, unknownType(), unknownType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/* 1223 */     return constructMapLikeType(mapClass, unknownType(), unknownType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*      */     JavaType kt;
/*      */     JavaType vt;
/* 1238 */     if (rawClass == Properties.class)
/* 1239 */     { kt = vt = CORE_TYPE_STRING; }
/*      */     else
/* 1241 */     { List<JavaType> typeParams = bindings.getTypeParameters();
/*      */       
/* 1243 */       int pc = typeParams.size();
/* 1244 */       switch (pc)
/*      */       { case 0:
/* 1246 */           kt = vt = _unknownType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1258 */           return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);case 2: kt = typeParams.get(0); vt = typeParams.get(1); return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt); }  throw new IllegalArgumentException(String.format("Strange Map type %s with %d type parameter%s (%s), can not resolve", new Object[] { ClassUtil.nameOf(rawClass), Integer.valueOf(pc), (pc == 1) ? "" : "s", bindings })); }  return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*      */   }
/*      */ 
/*      */   
/*      */   private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*      */     JavaType ct;
/* 1264 */     List<JavaType> typeParams = bindings.getTypeParameters();
/*      */ 
/*      */     
/* 1267 */     if (typeParams.isEmpty()) {
/* 1268 */       ct = _unknownType();
/* 1269 */     } else if (typeParams.size() == 1) {
/* 1270 */       ct = typeParams.get(0);
/*      */     } else {
/* 1272 */       throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": cannot determine type parameters");
/*      */     } 
/* 1274 */     return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*      */   }
/*      */ 
/*      */   
/*      */   private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*      */     JavaType ct;
/* 1280 */     List<JavaType> typeParams = bindings.getTypeParameters();
/*      */ 
/*      */     
/* 1283 */     if (typeParams.isEmpty()) {
/* 1284 */       ct = _unknownType();
/* 1285 */     } else if (typeParams.size() == 1) {
/* 1286 */       ct = typeParams.get(0);
/*      */     } else {
/* 1288 */       throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": cannot determine type parameters");
/*      */     } 
/* 1290 */     return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 1304 */     if (bindings.isEmpty()) {
/* 1305 */       JavaType result = _findWellKnownSimple(raw);
/* 1306 */       if (result != null) {
/* 1307 */         return result;
/*      */       }
/*      */     } 
/* 1310 */     return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 1323 */     return new SimpleType(raw, bindings, superClass, superInterfaces);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _unknownType() {
/* 1332 */     return CORE_TYPE_OBJECT;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _findWellKnownSimple(Class<?> clz) {
/* 1343 */     if (clz.isPrimitive()) {
/* 1344 */       if (clz == CLS_BOOL) return CORE_TYPE_BOOL; 
/* 1345 */       if (clz == CLS_INT) return CORE_TYPE_INT; 
/* 1346 */       if (clz == CLS_LONG) return CORE_TYPE_LONG; 
/*      */     } else {
/* 1348 */       if (clz == CLS_STRING) return CORE_TYPE_STRING; 
/* 1349 */       if (clz == CLS_OBJECT) return CORE_TYPE_OBJECT; 
/* 1350 */       if (clz == CLS_JSON_NODE) return CORE_TYPE_JSON_NODE; 
/*      */     } 
/* 1352 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromAny(ClassStack context, Type srcType, TypeBindings bindings) {
/*      */     JavaType resultType;
/* 1371 */     if (srcType instanceof Class) {
/*      */       
/* 1373 */       resultType = _fromClass(context, (Class)srcType, EMPTY_BINDINGS);
/*      */     
/*      */     }
/* 1376 */     else if (srcType instanceof ParameterizedType) {
/* 1377 */       resultType = _fromParamType(context, (ParameterizedType)srcType, bindings);
/*      */     } else {
/* 1379 */       if (srcType instanceof JavaType)
/*      */       {
/* 1381 */         return (JavaType)srcType;
/*      */       }
/* 1383 */       if (srcType instanceof GenericArrayType) {
/* 1384 */         resultType = _fromArrayType(context, (GenericArrayType)srcType, bindings);
/*      */       }
/* 1386 */       else if (srcType instanceof TypeVariable) {
/* 1387 */         resultType = _fromVariable(context, (TypeVariable)srcType, bindings);
/*      */       }
/* 1389 */       else if (srcType instanceof WildcardType) {
/* 1390 */         resultType = _fromWildcard(context, (WildcardType)srcType, bindings);
/*      */       } else {
/*      */         
/* 1393 */         throw new IllegalArgumentException("Unrecognized Type: " + ((srcType == null) ? "[null]" : srcType.toString()));
/*      */       } 
/*      */     } 
/*      */     
/* 1397 */     return _applyModifiers(srcType, resultType);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JavaType _applyModifiers(Type srcType, JavaType resolvedType) {
/* 1402 */     if (this._modifiers == null) {
/* 1403 */       return resolvedType;
/*      */     }
/* 1405 */     JavaType resultType = resolvedType;
/* 1406 */     TypeBindings b = resultType.getBindings();
/* 1407 */     if (b == null) {
/* 1408 */       b = EMPTY_BINDINGS;
/*      */     }
/* 1410 */     for (TypeModifier mod : this._modifiers) {
/* 1411 */       JavaType t = mod.modifyType(resultType, srcType, b, this);
/* 1412 */       if (t == null) {
/* 1413 */         throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod
/*      */                 
/* 1415 */                 .getClass().getName(), resultType }));
/*      */       }
/* 1417 */       resultType = t;
/*      */     } 
/* 1419 */     return resultType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*      */     Object key;
/* 1429 */     JavaType result = _findWellKnownSimple(rawType);
/* 1430 */     if (result != null) {
/* 1431 */       return result;
/*      */     }
/*      */ 
/*      */     
/* 1435 */     if (bindings == null || bindings.isEmpty()) {
/* 1436 */       key = rawType;
/*      */     } else {
/* 1438 */       key = bindings.asKey(rawType);
/*      */     } 
/* 1440 */     result = (JavaType)this._typeCache.get(key);
/* 1441 */     if (result != null) {
/* 1442 */       return result;
/*      */     }
/*      */ 
/*      */     
/* 1446 */     if (context == null) {
/* 1447 */       context = new ClassStack(rawType);
/*      */     } else {
/* 1449 */       ClassStack prev = context.find(rawType);
/* 1450 */       if (prev != null) {
/*      */         
/* 1452 */         ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/* 1453 */         prev.addSelfReference(selfRef);
/* 1454 */         return selfRef;
/*      */       } 
/*      */       
/* 1457 */       context = context.child(rawType);
/*      */     } 
/*      */ 
/*      */     
/* 1461 */     if (rawType.isArray()) {
/* 1462 */       result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*      */     } else {
/*      */       JavaType superClass;
/*      */ 
/*      */       
/*      */       JavaType[] superInterfaces;
/*      */ 
/*      */       
/* 1470 */       if (rawType.isInterface()) {
/* 1471 */         superClass = null;
/* 1472 */         superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*      */       } else {
/*      */         
/* 1475 */         superClass = _resolveSuperClass(context, rawType, bindings);
/* 1476 */         superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*      */       } 
/*      */ 
/*      */       
/* 1480 */       if (rawType == Properties.class) {
/* 1481 */         result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1486 */       else if (superClass != null) {
/* 1487 */         result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*      */       } 
/*      */       
/* 1490 */       if (result == null) {
/* 1491 */         result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/* 1492 */         if (result == null) {
/* 1493 */           result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/* 1494 */           if (result == null)
/*      */           {
/* 1496 */             result = _newSimpleType(rawType, bindings, superClass, superInterfaces);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 1501 */     context.resolveSelfReferences(result);
/*      */ 
/*      */     
/* 1504 */     if (!result.hasHandlers()) {
/* 1505 */       this._typeCache.putIfAbsent(key, result);
/*      */     }
/* 1507 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/* 1512 */     Type parent = ClassUtil.getGenericSuperclass(rawType);
/* 1513 */     if (parent == null) {
/* 1514 */       return null;
/*      */     }
/* 1516 */     return _fromAny(context, parent, parentBindings);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/* 1521 */     Type[] types = ClassUtil.getGenericInterfaces(rawType);
/* 1522 */     if (types == null || types.length == 0) {
/* 1523 */       return NO_TYPES;
/*      */     }
/* 1525 */     int len = types.length;
/* 1526 */     JavaType[] resolved = new JavaType[len];
/* 1527 */     for (int i = 0; i < len; i++) {
/* 1528 */       Type type = types[i];
/* 1529 */       resolved[i] = _fromAny(context, type, parentBindings);
/*      */     } 
/* 1531 */     return resolved;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 1542 */     if (bindings == null) {
/* 1543 */       bindings = EMPTY_BINDINGS;
/*      */     }
/*      */     
/* 1546 */     if (rawType == Map.class) {
/* 1547 */       return _mapType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/* 1549 */     if (rawType == Collection.class) {
/* 1550 */       return _collectionType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/*      */     
/* 1553 */     if (rawType == AtomicReference.class) {
/* 1554 */       return _referenceType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1560 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 1568 */     int intCount = superInterfaces.length;
/*      */     
/* 1570 */     for (int i = 0; i < intCount; i++) {
/* 1571 */       JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/* 1572 */       if (result != null) {
/* 1573 */         return result;
/*      */       }
/*      */     } 
/* 1576 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*      */     TypeBindings newBindings;
/* 1587 */     Class<?> rawType = (Class)ptype.getRawType();
/*      */ 
/*      */ 
/*      */     
/* 1591 */     if (rawType == CLS_ENUM) {
/* 1592 */       return CORE_TYPE_ENUM;
/*      */     }
/* 1594 */     if (rawType == CLS_COMPARABLE) {
/* 1595 */       return CORE_TYPE_COMPARABLE;
/*      */     }
/* 1597 */     if (rawType == CLS_CLASS) {
/* 1598 */       return CORE_TYPE_CLASS;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1604 */     Type[] args = ptype.getActualTypeArguments();
/* 1605 */     int paramCount = (args == null) ? 0 : args.length;
/*      */ 
/*      */     
/* 1608 */     if (paramCount == 0) {
/* 1609 */       newBindings = EMPTY_BINDINGS;
/*      */     } else {
/* 1611 */       JavaType[] pt = new JavaType[paramCount];
/* 1612 */       for (int i = 0; i < paramCount; i++) {
/* 1613 */         pt[i] = _fromAny(context, args[i], parentBindings);
/*      */       }
/* 1615 */       newBindings = TypeBindings.create(rawType, pt);
/*      */     } 
/* 1617 */     return _fromClass(context, rawType, newBindings);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/* 1622 */     JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/* 1623 */     return ArrayType.construct(elementType, bindings);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*      */     Type[] bounds;
/* 1629 */     String name = var.getName();
/* 1630 */     if (bindings == null) {
/* 1631 */       throw new IllegalArgumentException("Null `bindings` passed (type variable \"" + name + "\")");
/*      */     }
/* 1633 */     JavaType type = bindings.findBoundType(name);
/* 1634 */     if (type != null) {
/* 1635 */       return type;
/*      */     }
/*      */ 
/*      */     
/* 1639 */     if (bindings.hasUnbound(name)) {
/* 1640 */       return CORE_TYPE_OBJECT;
/*      */     }
/* 1642 */     bindings = bindings.withUnboundVariable(name);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1653 */     synchronized (var) {
/* 1654 */       bounds = var.getBounds();
/*      */     } 
/* 1656 */     return _fromAny(context, bounds[0], bindings);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/* 1666 */     return _fromAny(context, type.getUpperBounds()[0], bindings);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/TypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */