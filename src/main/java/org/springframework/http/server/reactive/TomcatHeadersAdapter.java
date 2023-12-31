/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apache.tomcat.util.buf.MessageBytes;
/*     */ import org.apache.tomcat.util.http.MimeHeaders;
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
/*     */ class TomcatHeadersAdapter
/*     */   implements MultiValueMap<String, String>
/*     */ {
/*     */   private final MimeHeaders headers;
/*     */   
/*     */   TomcatHeadersAdapter(MimeHeaders headers) {
/*  49 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFirst(String key) {
/*  55 */     return this.headers.getHeader(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String key, @Nullable String value) {
/*  60 */     this.headers.addValue(key).setString(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  65 */     values.forEach(value -> add(key, value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/*  70 */     values.forEach(this::addAll);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String key, @Nullable String value) {
/*  75 */     this.headers.setValue(key).setString(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/*  80 */     values.forEach(this::set);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/*  85 */     Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
/*  86 */     keySet().forEach(key -> (String)singleValueMap.put(key, getFirst(key)));
/*  87 */     return singleValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  92 */     Enumeration<String> names = this.headers.names();
/*  93 */     int size = 0;
/*  94 */     while (names.hasMoreElements()) {
/*  95 */       size++;
/*  96 */       names.nextElement();
/*     */     } 
/*  98 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 103 */     return (this.headers.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 108 */     if (key instanceof String) {
/* 109 */       return (this.headers.findHeader((String)key, 0) != -1);
/*     */     }
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 116 */     if (value instanceof String) {
/* 117 */       MessageBytes needle = MessageBytes.newInstance();
/* 118 */       needle.setString((String)value);
/* 119 */       for (int i = 0; i < this.headers.size(); i++) {
/* 120 */         if (this.headers.getValue(i).equals(needle)) {
/* 121 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> get(Object key) {
/* 131 */     if (containsKey(key)) {
/* 132 */       return Collections.list(this.headers.values((String)key));
/*     */     }
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> put(String key, List<String> value) {
/* 140 */     List<String> previousValues = get(key);
/* 141 */     this.headers.removeHeader(key);
/* 142 */     value.forEach(v -> this.headers.addValue(key).setString(v));
/* 143 */     return previousValues;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> remove(Object key) {
/* 149 */     if (key instanceof String) {
/* 150 */       List<String> previousValues = get(key);
/* 151 */       this.headers.removeHeader((String)key);
/* 152 */       return previousValues;
/*     */     } 
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 159 */     map.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 164 */     this.headers.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 169 */     return new HeaderNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<String>> values() {
/* 174 */     return (Collection<List<String>>)keySet().stream().map(this::get).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 179 */     return new AbstractSet<Map.Entry<String, List<String>>>()
/*     */       {
/*     */         public Iterator<Map.Entry<String, List<String>>> iterator() {
/* 182 */           return new TomcatHeadersAdapter.EntryIterator();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/* 187 */           return TomcatHeadersAdapter.this.headers.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 195 */     return HttpHeaders.formatHeaders(this);
/*     */   }
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<String, List<String>>>
/*     */   {
/* 201 */     private Enumeration<String> names = TomcatHeadersAdapter.this.headers.names();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 205 */       return this.names.hasMoreElements();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, List<String>> next() {
/* 210 */       return new TomcatHeadersAdapter.HeaderEntry(this.names.nextElement());
/*     */     }
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private final class HeaderEntry implements Map.Entry<String, List<String>> {
/*     */     private final String key;
/*     */     
/*     */     HeaderEntry(String key) {
/* 220 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 225 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public List<String> getValue() {
/* 231 */       return TomcatHeadersAdapter.this.get(this.key);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public List<String> setValue(List<String> value) {
/* 237 */       List<String> previous = getValue();
/* 238 */       TomcatHeadersAdapter.this.headers.removeHeader(this.key);
/* 239 */       TomcatHeadersAdapter.this.addAll(this.key, value);
/* 240 */       return previous;
/*     */     }
/*     */   }
/*     */   
/*     */   private class HeaderNames
/*     */     extends AbstractSet<String> {
/*     */     private HeaderNames() {}
/*     */     
/*     */     public Iterator<String> iterator() {
/* 249 */       return new TomcatHeadersAdapter.HeaderNamesIterator(TomcatHeadersAdapter.this.headers.names());
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 254 */       Enumeration<String> names = TomcatHeadersAdapter.this.headers.names();
/* 255 */       int size = 0;
/* 256 */       while (names.hasMoreElements()) {
/* 257 */         names.nextElement();
/* 258 */         size++;
/*     */       } 
/* 260 */       return size;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class HeaderNamesIterator
/*     */     implements Iterator<String>
/*     */   {
/*     */     private final Enumeration<String> enumeration;
/*     */     @Nullable
/*     */     private String currentName;
/*     */     
/*     */     private HeaderNamesIterator(Enumeration<String> enumeration) {
/* 272 */       this.enumeration = enumeration;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 277 */       return this.enumeration.hasMoreElements();
/*     */     }
/*     */ 
/*     */     
/*     */     public String next() {
/* 282 */       this.currentName = this.enumeration.nextElement();
/* 283 */       return this.currentName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 288 */       if (this.currentName == null) {
/* 289 */         throw new IllegalStateException("No current Header in iterator");
/*     */       }
/* 291 */       int index = TomcatHeadersAdapter.this.headers.findHeader(this.currentName, 0);
/* 292 */       if (index == -1) {
/* 293 */         throw new IllegalStateException("Header not present: " + this.currentName);
/*     */       }
/* 295 */       TomcatHeadersAdapter.this.headers.removeHeader(index);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/TomcatHeadersAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */