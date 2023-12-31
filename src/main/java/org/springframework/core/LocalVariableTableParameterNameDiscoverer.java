/*     */ package org.springframework.core;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.asm.ClassReader;
/*     */ import org.springframework.asm.ClassVisitor;
/*     */ import org.springframework.asm.Label;
/*     */ import org.springframework.asm.MethodVisitor;
/*     */ import org.springframework.asm.Type;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ public class LocalVariableTableParameterNameDiscoverer
/*     */   implements ParameterNameDiscoverer
/*     */ {
/*  59 */   private static final Log logger = LogFactory.getLog(LocalVariableTableParameterNameDiscoverer.class);
/*     */ 
/*     */   
/*  62 */   private static final Map<Executable, String[]> NO_DEBUG_INFO_MAP = (Map)Collections.emptyMap();
/*     */ 
/*     */   
/*  65 */   private final Map<Class<?>, Map<Executable, String[]>> parameterNamesCache = new ConcurrentHashMap<>(32);
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getParameterNames(Method method) {
/*  71 */     Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);
/*  72 */     return doGetParameterNames(originalMethod);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getParameterNames(Constructor<?> ctor) {
/*  78 */     return doGetParameterNames(ctor);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private String[] doGetParameterNames(Executable executable) {
/*  83 */     Class<?> declaringClass = executable.getDeclaringClass();
/*  84 */     Map<Executable, String[]> map = this.parameterNamesCache.computeIfAbsent(declaringClass, this::inspectClass);
/*  85 */     return (map != NO_DEBUG_INFO_MAP) ? map.get(executable) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<Executable, String[]> inspectClass(Class<?> clazz) {
/*  94 */     InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
/*  95 */     if (is == null) {
/*     */ 
/*     */       
/*  98 */       if (logger.isDebugEnabled()) {
/*  99 */         logger.debug("Cannot find '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names");
/*     */       }
/*     */       
/* 102 */       return NO_DEBUG_INFO_MAP;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 107 */       ClassReader classReader = new ClassReader(is);
/* 108 */       Map<Executable, String[]> map = (Map)new ConcurrentHashMap<>(32);
/* 109 */       classReader.accept(new ParameterNameDiscoveringVisitor(clazz, map), 0);
/* 110 */       return map;
/*     */     }
/* 112 */     catch (IOException ex) {
/* 113 */       if (logger.isDebugEnabled()) {
/* 114 */         logger.debug("Exception thrown while reading '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names", ex);
/*     */       
/*     */       }
/*     */     }
/* 118 */     catch (IllegalArgumentException ex) {
/* 119 */       if (logger.isDebugEnabled()) {
/* 120 */         logger.debug("ASM ClassReader failed to parse class file [" + clazz + "], probably due to a new Java class file version that isn't supported yet - unable to determine constructor/method parameter names", ex);
/*     */       }
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 127 */         is.close();
/*     */       }
/* 129 */       catch (IOException iOException) {}
/*     */     } 
/*     */ 
/*     */     
/* 133 */     return NO_DEBUG_INFO_MAP;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ParameterNameDiscoveringVisitor
/*     */     extends ClassVisitor
/*     */   {
/*     */     private static final String STATIC_CLASS_INIT = "<clinit>";
/*     */ 
/*     */     
/*     */     private final Class<?> clazz;
/*     */     
/*     */     private final Map<Executable, String[]> executableMap;
/*     */ 
/*     */     
/*     */     public ParameterNameDiscoveringVisitor(Class<?> clazz, Map<Executable, String[]> executableMap) {
/* 150 */       super(17432576);
/* 151 */       this.clazz = clazz;
/* 152 */       this.executableMap = executableMap;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
/* 159 */       if (!isSyntheticOrBridged(access) && !"<clinit>".equals(name)) {
/* 160 */         return new LocalVariableTableParameterNameDiscoverer.LocalVariableTableVisitor(this.clazz, this.executableMap, name, desc, isStatic(access));
/*     */       }
/* 162 */       return null;
/*     */     }
/*     */     
/*     */     private static boolean isSyntheticOrBridged(int access) {
/* 166 */       return ((access & 0x1000 | access & 0x40) > 0);
/*     */     }
/*     */     
/*     */     private static boolean isStatic(int access) {
/* 170 */       return ((access & 0x8) > 0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class LocalVariableTableVisitor
/*     */     extends MethodVisitor
/*     */   {
/*     */     private static final String CONSTRUCTOR = "<init>";
/*     */ 
/*     */     
/*     */     private final Class<?> clazz;
/*     */     
/*     */     private final Map<Executable, String[]> executableMap;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     private final Type[] args;
/*     */     
/*     */     private final String[] parameterNames;
/*     */     
/*     */     private final boolean isStatic;
/*     */     
/*     */     private boolean hasLvtInfo = false;
/*     */     
/*     */     private final int[] lvtSlotIndex;
/*     */ 
/*     */     
/*     */     public LocalVariableTableVisitor(Class<?> clazz, Map<Executable, String[]> map, String name, String desc, boolean isStatic) {
/* 200 */       super(17432576);
/* 201 */       this.clazz = clazz;
/* 202 */       this.executableMap = map;
/* 203 */       this.name = name;
/* 204 */       this.args = Type.getArgumentTypes(desc);
/* 205 */       this.parameterNames = new String[this.args.length];
/* 206 */       this.isStatic = isStatic;
/* 207 */       this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
/* 212 */       this.hasLvtInfo = true;
/* 213 */       for (int i = 0; i < this.lvtSlotIndex.length; i++) {
/* 214 */         if (this.lvtSlotIndex[i] == index) {
/* 215 */           this.parameterNames[i] = name;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitEnd() {
/* 222 */       if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0))
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 227 */         this.executableMap.put(resolveExecutable(), this.parameterNames);
/*     */       }
/*     */     }
/*     */     
/*     */     private Executable resolveExecutable() {
/* 232 */       ClassLoader loader = this.clazz.getClassLoader();
/* 233 */       Class<?>[] argTypes = new Class[this.args.length];
/* 234 */       for (int i = 0; i < this.args.length; i++) {
/* 235 */         argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
/*     */       }
/*     */       try {
/* 238 */         if ("<init>".equals(this.name)) {
/* 239 */           return this.clazz.getDeclaredConstructor(argTypes);
/*     */         }
/* 241 */         return this.clazz.getDeclaredMethod(this.name, argTypes);
/*     */       }
/* 243 */       catch (NoSuchMethodException ex) {
/* 244 */         throw new IllegalStateException("Method [" + this.name + "] was discovered in the .class file but cannot be resolved in the class object", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
/* 250 */       int[] lvtIndex = new int[paramTypes.length];
/* 251 */       int nextIndex = isStatic ? 0 : 1;
/* 252 */       for (int i = 0; i < paramTypes.length; i++) {
/* 253 */         lvtIndex[i] = nextIndex;
/* 254 */         if (isWideType(paramTypes[i])) {
/* 255 */           nextIndex += 2;
/*     */         } else {
/*     */           
/* 258 */           nextIndex++;
/*     */         } 
/*     */       } 
/* 261 */       return lvtIndex;
/*     */     }
/*     */ 
/*     */     
/*     */     private static boolean isWideType(Type aType) {
/* 266 */       return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/LocalVariableTableParameterNameDiscoverer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */