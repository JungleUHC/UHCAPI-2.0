/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import org.eclipse.jetty.http.HttpField;
/*     */ import org.eclipse.jetty.http.HttpFields;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ class JettyHeadersAdapter
/*     */   implements MultiValueMap<String, String>
/*     */ {
/*     */   private final HttpFields headers;
/*     */   
/*     */   JettyHeadersAdapter(HttpFields headers) {
/*  50 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFirst(String key) {
/*  56 */     return this.headers.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String key, @Nullable String value) {
/*  61 */     this.headers.add(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  66 */     values.forEach(value -> add(key, value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/*  71 */     values.forEach(this::addAll);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String key, @Nullable String value) {
/*  76 */     this.headers.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/*  81 */     values.forEach(this::set);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/*  86 */     Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
/*  87 */     Iterator<HttpField> iterator = this.headers.iterator();
/*  88 */     iterator.forEachRemaining(field -> {
/*     */           if (!singleValueMap.containsKey(field.getName())) {
/*     */             singleValueMap.put(field.getName(), field.getValue());
/*     */           }
/*     */         });
/*  93 */     return singleValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  98 */     return this.headers.getFieldNamesCollection().size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 103 */     return (this.headers.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 108 */     return (key instanceof String && this.headers.containsKey((String)key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 113 */     return (value instanceof String && this.headers
/* 114 */       .stream().anyMatch(field -> field.contains((String)value)));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> get(Object key) {
/* 120 */     if (containsKey(key)) {
/* 121 */       return this.headers.getValuesList((String)key);
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> put(String key, List<String> value) {
/* 129 */     List<String> oldValues = get(key);
/* 130 */     this.headers.put(key, value);
/* 131 */     return oldValues;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> remove(Object key) {
/* 137 */     if (key instanceof String) {
/* 138 */       List<String> oldValues = get(key);
/* 139 */       this.headers.remove((String)key);
/* 140 */       return oldValues;
/*     */     } 
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 147 */     map.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 152 */     this.headers.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 157 */     return new HeaderNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<String>> values() {
/* 162 */     return (Collection<List<String>>)this.headers.getFieldNamesCollection().stream()
/* 163 */       .map(this.headers::getValuesList).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 168 */     return new AbstractSet<Map.Entry<String, List<String>>>()
/*     */       {
/*     */         public Iterator<Map.Entry<String, List<String>>> iterator() {
/* 171 */           return new JettyHeadersAdapter.EntryIterator();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/* 176 */           return JettyHeadersAdapter.this.headers.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 184 */     return HttpHeaders.formatHeaders(this);
/*     */   }
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<String, List<String>>>
/*     */   {
/* 190 */     private final Enumeration<String> names = JettyHeadersAdapter.this.headers.getFieldNames();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 194 */       return this.names.hasMoreElements();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, List<String>> next() {
/* 199 */       return new JettyHeadersAdapter.HeaderEntry(this.names.nextElement());
/*     */     }
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private class HeaderEntry implements Map.Entry<String, List<String>> {
/*     */     private final String key;
/*     */     
/*     */     HeaderEntry(String key) {
/* 209 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 214 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> getValue() {
/* 219 */       return JettyHeadersAdapter.this.headers.getValuesList(this.key);
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> setValue(List<String> value) {
/* 224 */       List<String> previousValues = JettyHeadersAdapter.this.headers.getValuesList(this.key);
/* 225 */       JettyHeadersAdapter.this.headers.put(this.key, value);
/* 226 */       return previousValues;
/*     */     }
/*     */   }
/*     */   
/*     */   private class HeaderNames
/*     */     extends AbstractSet<String> {
/*     */     private HeaderNames() {}
/*     */     
/*     */     public Iterator<String> iterator() {
/* 235 */       return new JettyHeadersAdapter.HeaderNamesIterator(JettyHeadersAdapter.this.headers.getFieldNamesCollection().iterator());
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 240 */       return JettyHeadersAdapter.this.headers.getFieldNamesCollection().size();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private final class HeaderNamesIterator
/*     */     implements Iterator<String>
/*     */   {
/*     */     private final Iterator<String> iterator;
/*     */     @Nullable
/*     */     private String currentName;
/*     */     
/*     */     private HeaderNamesIterator(Iterator<String> iterator) {
/* 253 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 258 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public String next() {
/* 263 */       this.currentName = this.iterator.next();
/* 264 */       return this.currentName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 269 */       if (this.currentName == null) {
/* 270 */         throw new IllegalStateException("No current Header in iterator");
/*     */       }
/* 272 */       if (!JettyHeadersAdapter.this.headers.containsKey(this.currentName)) {
/* 273 */         throw new IllegalStateException("Header not present: " + this.currentName);
/*     */       }
/* 275 */       JettyHeadersAdapter.this.headers.remove(this.currentName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/JettyHeadersAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */