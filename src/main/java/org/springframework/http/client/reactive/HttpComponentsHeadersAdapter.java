/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.hc.core5.http.Header;
/*     */ import org.apache.hc.core5.http.HttpMessage;
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
/*     */ class HttpComponentsHeadersAdapter
/*     */   implements MultiValueMap<String, String>
/*     */ {
/*     */   private final HttpMessage message;
/*     */   
/*     */   HttpComponentsHeadersAdapter(HttpMessage message) {
/*  51 */     this.message = message;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFirst(String key) {
/*  57 */     Header header = this.message.getFirstHeader(key);
/*  58 */     return (header != null) ? header.getValue() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String key, @Nullable String value) {
/*  63 */     this.message.addHeader(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  68 */     values.forEach(value -> add(key, value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/*  73 */     values.forEach(this::addAll);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String key, @Nullable String value) {
/*  78 */     this.message.setHeader(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/*  83 */     values.forEach(this::set);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/*  88 */     Map<String, String> map = CollectionUtils.newLinkedHashMap(size());
/*  89 */     this.message.headerIterator().forEachRemaining(h -> (String)map.putIfAbsent(h.getName(), h.getValue()));
/*  90 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  95 */     return (this.message.getHeaders()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 100 */     return ((this.message.getHeaders()).length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 105 */     return (key instanceof String && this.message.containsHeader((String)key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 110 */     return (value instanceof String && 
/* 111 */       Arrays.<Header>stream(this.message.getHeaders()).anyMatch(h -> h.getValue().equals(value)));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> get(Object key) {
/* 117 */     List<String> values = null;
/* 118 */     if (containsKey(key)) {
/* 119 */       Header[] headers = this.message.getHeaders((String)key);
/* 120 */       values = new ArrayList<>(headers.length);
/* 121 */       for (Header header : headers) {
/* 122 */         values.add(header.getValue());
/*     */       }
/*     */     } 
/* 125 */     return values;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> put(String key, List<String> values) {
/* 131 */     List<String> oldValues = remove(key);
/* 132 */     values.forEach(value -> add(key, value));
/* 133 */     return oldValues;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> remove(Object key) {
/* 139 */     if (key instanceof String) {
/* 140 */       List<String> oldValues = get(key);
/* 141 */       this.message.removeHeaders((String)key);
/* 142 */       return oldValues;
/*     */     } 
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 149 */     map.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 154 */     this.message.setHeaders(new Header[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 159 */     Set<String> keys = new LinkedHashSet<>(size());
/* 160 */     for (Header header : this.message.getHeaders()) {
/* 161 */       keys.add(header.getName());
/*     */     }
/* 163 */     return keys;
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<String>> values() {
/* 168 */     Collection<List<String>> values = new ArrayList<>(size());
/* 169 */     for (Header header : this.message.getHeaders()) {
/* 170 */       values.add(get(header.getName()));
/*     */     }
/* 172 */     return values;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 177 */     return new AbstractSet<Map.Entry<String, List<String>>>()
/*     */       {
/*     */         public Iterator<Map.Entry<String, List<String>>> iterator() {
/* 180 */           return new HttpComponentsHeadersAdapter.EntryIterator();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/* 185 */           return HttpComponentsHeadersAdapter.this.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 193 */     return HttpHeaders.formatHeaders(this);
/*     */   }
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<String, List<String>>>
/*     */   {
/* 199 */     private final Iterator<Header> iterator = HttpComponentsHeadersAdapter.this.message.headerIterator();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 203 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, List<String>> next() {
/* 208 */       return new HttpComponentsHeadersAdapter.HeaderEntry(((Header)this.iterator.next()).getName());
/*     */     }
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private class HeaderEntry implements Map.Entry<String, List<String>> {
/*     */     private final String key;
/*     */     
/*     */     HeaderEntry(String key) {
/* 218 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 223 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> getValue() {
/* 228 */       List<String> values = HttpComponentsHeadersAdapter.this.get(this.key);
/* 229 */       return (values != null) ? values : Collections.<String>emptyList();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> setValue(List<String> value) {
/* 234 */       List<String> previousValues = getValue();
/* 235 */       HttpComponentsHeadersAdapter.this.put(this.key, value);
/* 236 */       return previousValues;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/HttpComponentsHeadersAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */