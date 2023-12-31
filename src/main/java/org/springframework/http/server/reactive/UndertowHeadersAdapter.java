/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
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
/*     */ class UndertowHeadersAdapter
/*     */   implements MultiValueMap<String, String>
/*     */ {
/*     */   private final HeaderMap headers;
/*     */   
/*     */   UndertowHeadersAdapter(HeaderMap headers) {
/*  49 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFirst(String key) {
/*  55 */     return this.headers.getFirst(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String key, @Nullable String value) {
/*  60 */     this.headers.add(HttpString.tryFromString(key), value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  66 */     this.headers.addAll(HttpString.tryFromString(key), values);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/*  71 */     values.forEach((key, list) -> this.headers.addAll(HttpString.tryFromString(key), list));
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String key, @Nullable String value) {
/*  76 */     this.headers.put(HttpString.tryFromString(key), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/*  81 */     values.forEach((key, list) -> this.headers.put(HttpString.tryFromString(key), list));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/*  86 */     Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
/*  87 */     this.headers.forEach(values -> (String)singleValueMap.put(values.getHeaderName().toString(), values.getFirst()));
/*     */     
/*  89 */     return singleValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  94 */     return this.headers.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  99 */     return (this.headers.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 104 */     return (key instanceof String && this.headers.contains((String)key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 109 */     return (value instanceof String && this.headers
/* 110 */       .getHeaderNames().stream()
/* 111 */       .map(this.headers::get)
/* 112 */       .anyMatch(values -> values.contains(value)));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> get(Object key) {
/* 118 */     if (key instanceof String) {
/* 119 */       return (List<String>)this.headers.get((String)key);
/*     */     }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> put(String key, List<String> value) {
/* 127 */     HeaderValues previousValues = this.headers.get(key);
/* 128 */     this.headers.putAll(HttpString.tryFromString(key), value);
/* 129 */     return (List<String>)previousValues;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> remove(Object key) {
/* 135 */     if (key instanceof String) {
/* 136 */       Collection<String> removed = this.headers.remove((String)key);
/* 137 */       if (removed != null) {
/* 138 */         return new ArrayList<>(removed);
/*     */       }
/*     */     } 
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 146 */     map.forEach((key, values) -> this.headers.putAll(HttpString.tryFromString(key), values));
/*     */   }
/*     */ 
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
/* 162 */     return (Collection<List<String>>)this.headers.getHeaderNames().stream()
/* 163 */       .map(this.headers::get)
/* 164 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 169 */     return new AbstractSet<Map.Entry<String, List<String>>>()
/*     */       {
/*     */         public Iterator<Map.Entry<String, List<String>>> iterator() {
/* 172 */           return new UndertowHeadersAdapter.EntryIterator();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/* 177 */           return UndertowHeadersAdapter.this.headers.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 185 */     return HttpHeaders.formatHeaders(this);
/*     */   }
/*     */   
/*     */   private class EntryIterator
/*     */     implements Iterator<Map.Entry<String, List<String>>>
/*     */   {
/* 191 */     private Iterator<HttpString> names = UndertowHeadersAdapter.this.headers.getHeaderNames().iterator();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 195 */       return this.names.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, List<String>> next() {
/* 200 */       return new UndertowHeadersAdapter.HeaderEntry(this.names.next());
/*     */     }
/*     */     
/*     */     private EntryIterator() {}
/*     */   }
/*     */   
/*     */   private class HeaderEntry implements Map.Entry<String, List<String>> {
/*     */     private final HttpString key;
/*     */     
/*     */     HeaderEntry(HttpString key) {
/* 210 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 215 */       return this.key.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> getValue() {
/* 220 */       return (List<String>)UndertowHeadersAdapter.this.headers.get(this.key);
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> setValue(List<String> value) {
/* 225 */       HeaderValues headerValues = UndertowHeadersAdapter.this.headers.get(this.key);
/* 226 */       UndertowHeadersAdapter.this.headers.putAll(this.key, value);
/* 227 */       return (List<String>)headerValues;
/*     */     }
/*     */   }
/*     */   
/*     */   private class HeaderNames
/*     */     extends AbstractSet<String> {
/*     */     private HeaderNames() {}
/*     */     
/*     */     public Iterator<String> iterator() {
/* 236 */       return new UndertowHeadersAdapter.HeaderNamesIterator(UndertowHeadersAdapter.this.headers.getHeaderNames().iterator());
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 241 */       return UndertowHeadersAdapter.this.headers.getHeaderNames().size();
/*     */     }
/*     */   }
/*     */   
/*     */   private final class HeaderNamesIterator
/*     */     implements Iterator<String>
/*     */   {
/*     */     private final Iterator<HttpString> iterator;
/*     */     @Nullable
/*     */     private String currentName;
/*     */     
/*     */     private HeaderNamesIterator(Iterator<HttpString> iterator) {
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
/* 263 */       this.currentName = ((HttpString)this.iterator.next()).toString();
/* 264 */       return this.currentName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 269 */       if (this.currentName == null) {
/* 270 */         throw new IllegalStateException("No current Header in iterator");
/*     */       }
/* 272 */       if (!UndertowHeadersAdapter.this.headers.contains(this.currentName)) {
/* 273 */         throw new IllegalStateException("Header not present: " + this.currentName);
/*     */       }
/* 275 */       UndertowHeadersAdapter.this.headers.remove(this.currentName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/UndertowHeadersAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */