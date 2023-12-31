/*     */ package org.springframework.cglib.beans;
/*     */ 
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.springframework.asm.ClassVisitor;
/*     */ import org.springframework.cglib.core.AbstractClassGenerator;
/*     */ import org.springframework.cglib.core.KeyFactory;
/*     */ import org.springframework.cglib.core.ReflectUtils;
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
/*     */ public abstract class BeanMap
/*     */   implements Map
/*     */ {
/*     */   public static final int REQUIRE_GETTER = 1;
/*     */   public static final int REQUIRE_SETTER = 2;
/*     */   protected Object bean;
/*     */   
/*     */   public abstract BeanMap newInstance(Object paramObject);
/*     */   
/*     */   public abstract Class getPropertyType(String paramString);
/*     */   
/*     */   public static BeanMap create(Object bean) {
/*  66 */     Generator gen = new Generator();
/*  67 */     gen.setBean(bean);
/*  68 */     return gen.create();
/*     */   }
/*     */   
/*     */   public static class Generator extends AbstractClassGenerator {
/*  72 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanMap.class.getName());
/*     */ 
/*     */     
/*  75 */     private static final BeanMapKey KEY_FACTORY = (BeanMapKey)KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);
/*     */ 
/*     */     
/*     */     private Object bean;
/*     */     
/*     */     private Class beanClass;
/*     */     
/*     */     private int require;
/*     */ 
/*     */     
/*     */     public Generator() {
/*  86 */       super(SOURCE);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setBean(Object bean) {
/*  97 */       this.bean = bean;
/*  98 */       if (bean != null) {
/*  99 */         this.beanClass = bean.getClass();
/* 100 */         setContextClass(this.beanClass);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setBeanClass(Class beanClass) {
/* 110 */       this.beanClass = beanClass;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setRequire(int require) {
/* 119 */       this.require = require;
/*     */     }
/*     */     
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 123 */       return this.beanClass.getClassLoader();
/*     */     }
/*     */     
/*     */     protected ProtectionDomain getProtectionDomain() {
/* 127 */       return ReflectUtils.getProtectionDomain(this.beanClass);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BeanMap create() {
/* 135 */       if (this.beanClass == null)
/* 136 */         throw new IllegalArgumentException("Class of bean unknown"); 
/* 137 */       setNamePrefix(this.beanClass.getName());
/* 138 */       return (BeanMap)create(KEY_FACTORY.newInstance(this.beanClass, this.require));
/*     */     }
/*     */     
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 142 */       new BeanMapEmitter(v, getClassName(), this.beanClass, this.require);
/*     */     }
/*     */     
/*     */     protected Object firstInstance(Class type) {
/* 146 */       return ((BeanMap)ReflectUtils.newInstance(type)).newInstance(this.bean);
/*     */     }
/*     */     
/*     */     protected Object nextInstance(Object instance) {
/* 150 */       return ((BeanMap)instance).newInstance(this.bean);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static interface BeanMapKey
/*     */     {
/*     */       Object newInstance(Class param2Class, int param2Int);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanMap() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanMap(Object bean) {
/* 175 */     setBean(bean);
/*     */   }
/*     */   
/*     */   public Object get(Object key) {
/* 179 */     return get(this.bean, key);
/*     */   }
/*     */   
/*     */   public Object put(Object key, Object value) {
/* 183 */     return put(this.bean, key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Object get(Object paramObject1, Object paramObject2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Object put(Object paramObject1, Object paramObject2, Object paramObject3);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBean(Object bean) {
/* 212 */     this.bean = bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getBean() {
/* 221 */     return this.bean;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 225 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 229 */     return keySet().contains(key);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 233 */     for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
/* 234 */       Object v = get(it.next());
/* 235 */       if ((value == null && v == null) || (value != null && value.equals(v)))
/* 236 */         return true; 
/*     */     } 
/* 238 */     return false;
/*     */   }
/*     */   
/*     */   public int size() {
/* 242 */     return keySet().size();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 246 */     return (size() == 0);
/*     */   }
/*     */   
/*     */   public Object remove(Object key) {
/* 250 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public void putAll(Map t) {
/* 254 */     for (Iterator it = t.keySet().iterator(); it.hasNext(); ) {
/* 255 */       Object key = it.next();
/* 256 */       put(key, t.get(key));
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 261 */     if (o == null || !(o instanceof Map)) {
/* 262 */       return false;
/*     */     }
/* 264 */     Map other = (Map)o;
/* 265 */     if (size() != other.size()) {
/* 266 */       return false;
/*     */     }
/* 268 */     for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
/* 269 */       Object key = it.next();
/* 270 */       if (!other.containsKey(key)) {
/* 271 */         return false;
/*     */       }
/* 273 */       Object v1 = get(key);
/* 274 */       Object v2 = other.get(key);
/* 275 */       if ((v1 == null) ? (v2 == null) : v1.equals(v2))
/* 276 */         continue;  return false;
/*     */     } 
/*     */     
/* 279 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 283 */     int code = 0;
/* 284 */     for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
/* 285 */       Object key = it.next();
/* 286 */       Object value = get(key);
/* 287 */       code += ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value
/* 288 */         .hashCode());
/*     */     } 
/* 290 */     return code;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set entrySet() {
/* 295 */     HashMap<Object, Object> copy = new HashMap<>();
/* 296 */     for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
/* 297 */       Object key = it.next();
/* 298 */       copy.put(key, get(key));
/*     */     } 
/* 300 */     return Collections.<Object, Object>unmodifiableMap(copy).entrySet();
/*     */   }
/*     */   
/*     */   public Collection values() {
/* 304 */     Set<K> keys = keySet();
/* 305 */     List<Object> values = new ArrayList(keys.size());
/* 306 */     for (Iterator<K> it = keys.iterator(); it.hasNext();) {
/* 307 */       values.add(get(it.next()));
/*     */     }
/* 309 */     return Collections.unmodifiableCollection(values);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 317 */     StringBuffer sb = new StringBuffer();
/* 318 */     sb.append('{');
/* 319 */     for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
/* 320 */       Object key = it.next();
/* 321 */       sb.append(key);
/* 322 */       sb.append('=');
/* 323 */       sb.append(get(key));
/* 324 */       if (it.hasNext()) {
/* 325 */         sb.append(", ");
/*     */       }
/*     */     } 
/* 328 */     sb.append('}');
/* 329 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/beans/BeanMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */