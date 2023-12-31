/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import io.netty.handler.codec.http.HttpHeaders;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
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
/*     */ class NettyHeadersAdapter
/*     */   implements MultiValueMap<String, String>
/*     */ {
/*     */   private final HttpHeaders headers;
/*     */   
/*     */   NettyHeadersAdapter(HttpHeaders headers) {
/*  47 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getFirst(String key) {
/*  54 */     return this.headers.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String key, @Nullable String value) {
/*  59 */     if (value != null) {
/*  60 */       this.headers.add(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  66 */     this.headers.add(key, values);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/*  71 */     values.forEach(this.headers::add);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String key, @Nullable String value) {
/*  76 */     if (value != null) {
/*  77 */       this.headers.set(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/*  83 */     values.forEach(this.headers::set);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/*  88 */     Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
/*  89 */     this.headers.entries()
/*  90 */       .forEach(entry -> {
/*     */           if (!singleValueMap.containsKey(entry.getKey())) {
/*     */             singleValueMap.put(entry.getKey(), entry.getValue());
/*     */           }
/*     */         });
/*  95 */     return singleValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 100 */     return this.headers.names().size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 105 */     return this.headers.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 110 */     return (key instanceof String && this.headers.contains((String)key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 115 */     return (value instanceof String && this.headers
/* 116 */       .entries().stream()
/* 117 */       .anyMatch(entry -> value.equals(entry.getValue())));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> get(Object key) {
/* 123 */     if (containsKey(key)) {
/* 124 */       return this.headers.getAll((String)key);
/*     */     }
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> put(String key, @Nullable List<String> value) {
/* 132 */     List<String> previousValues = this.headers.getAll(key);
/* 133 */     this.headers.set(key, value);
/* 134 */     return previousValues;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> remove(Object key) {
/* 140 */     if (key instanceof String) {
/* 141 */       List<String> previousValues = this.headers.getAll((String)key);
/* 142 */       this.headers.remove((String)key);
/* 143 */       return previousValues;
/*     */     } 
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 150 */     map.forEach(this.headers::set);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 155 */     this.headers.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 160 */     return new HeaderNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<String>> values() {
/* 165 */     return (Collection<List<String>>)this.headers.names().stream()
/* 166 */       .map(this.headers::getAll).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 171 */     return new AbstractSet<Map.Entry<String, List<String>>>()
/*     */       {
/*     */         public Iterator<Map.Entry<String, List<String>>> iterator() {
/* 174 */           return new NettyHeadersAdapter.EntryIterator();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/* 179 */           return NettyHeadersAdapter.this.headers.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 187 */     return HttpHeaders.formatHeaders(this);
/*     */   }
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<String, List<String>>>
/*     */   {
/* 193 */     private Iterator<String> names = NettyHeadersAdapter.this.headers.names().iterator();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 197 */       return this.names.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, List<String>> next() {
/* 202 */       return new NettyHeadersAdapter.HeaderEntry(this.names.next());
/*     */     }
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private class HeaderEntry implements Map.Entry<String, List<String>> {
/*     */     private final String key;
/*     */     
/*     */     HeaderEntry(String key) {
/* 212 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 217 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> getValue() {
/* 222 */       return NettyHeadersAdapter.this.headers.getAll(this.key);
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> setValue(List<String> value) {
/* 227 */       List<String> previousValues = NettyHeadersAdapter.this.headers.getAll(this.key);
/* 228 */       NettyHeadersAdapter.this.headers.set(this.key, value);
/* 229 */       return previousValues;
/*     */     }
/*     */   }
/*     */   
/*     */   private class HeaderNames
/*     */     extends AbstractSet<String> {
/*     */     private HeaderNames() {}
/*     */     
/*     */     public Iterator<String> iterator() {
/* 238 */       return new NettyHeadersAdapter.HeaderNamesIterator(NettyHeadersAdapter.this.headers.names().iterator());
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 243 */       return NettyHeadersAdapter.this.headers.names().size();
/*     */     }
/*     */   }
/*     */   
/*     */   private final class HeaderNamesIterator
/*     */     implements Iterator<String>
/*     */   {
/*     */     private final Iterator<String> iterator;
/*     */     @Nullable
/*     */     private String currentName;
/*     */     
/*     */     private HeaderNamesIterator(Iterator<String> iterator) {
/* 255 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 260 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public String next() {
/* 265 */       this.currentName = this.iterator.next();
/* 266 */       return this.currentName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 271 */       if (this.currentName == null) {
/* 272 */         throw new IllegalStateException("No current Header in iterator");
/*     */       }
/* 274 */       if (!NettyHeadersAdapter.this.headers.contains(this.currentName)) {
/* 275 */         throw new IllegalStateException("Header not present: " + this.currentName);
/*     */       }
/* 277 */       NettyHeadersAdapter.this.headers.remove(this.currentName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/NettyHeadersAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */