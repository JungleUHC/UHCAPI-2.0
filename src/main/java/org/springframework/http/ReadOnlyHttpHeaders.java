/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ class ReadOnlyHttpHeaders
/*     */   extends HttpHeaders
/*     */ {
/*     */   private static final long serialVersionUID = -8578554704772377436L;
/*     */   @Nullable
/*     */   private MediaType cachedContentType;
/*     */   @Nullable
/*     */   private List<MediaType> cachedAccept;
/*     */   
/*     */   ReadOnlyHttpHeaders(MultiValueMap<String, String> headers) {
/*  50 */     super(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MediaType getContentType() {
/*  56 */     if (this.cachedContentType != null) {
/*  57 */       return this.cachedContentType;
/*     */     }
/*     */     
/*  60 */     MediaType contentType = super.getContentType();
/*  61 */     this.cachedContentType = contentType;
/*  62 */     return contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MediaType> getAccept() {
/*  68 */     if (this.cachedAccept != null) {
/*  69 */       return this.cachedAccept;
/*     */     }
/*     */     
/*  72 */     List<MediaType> accept = super.getAccept();
/*  73 */     this.cachedAccept = accept;
/*  74 */     return accept;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearContentHeaders() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> get(Object key) {
/*  85 */     List<String> values = (List<String>)this.headers.get(key);
/*  86 */     return (values != null) ? Collections.<String>unmodifiableList(values) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(String headerName, @Nullable String headerValue) {
/*  91 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(String key, List<? extends String> values) {
/*  96 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(MultiValueMap<String, String> values) {
/* 101 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(String headerName, @Nullable String headerValue) {
/* 106 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAll(Map<String, String> values) {
/* 111 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> toSingleValueMap() {
/* 116 */     return Collections.unmodifiableMap(this.headers.toSingleValueMap());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 121 */     return Collections.unmodifiableSet(this.headers.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> put(String key, List<String> value) {
/* 126 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> remove(Object key) {
/* 131 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 136 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 141 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<String>> values() {
/* 146 */     return Collections.unmodifiableCollection(this.headers.values());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 151 */     return (Set<Map.Entry<String, List<String>>>)this.headers.entrySet().stream().map(java.util.AbstractMap.SimpleImmutableEntry::new)
/* 152 */       .collect(Collectors.collectingAndThen(
/* 153 */           Collectors.toCollection(java.util.LinkedHashSet::new), Collections::unmodifiableSet));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ReadOnlyHttpHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */