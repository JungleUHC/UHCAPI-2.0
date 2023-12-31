/*      */ package org.springframework.web.client;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URI;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.stream.Collectors;
/*      */ import java.util.stream.Stream;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.springframework.core.ParameterizedTypeReference;
/*      */ import org.springframework.core.SpringProperties;
/*      */ import org.springframework.http.HttpEntity;
/*      */ import org.springframework.http.HttpHeaders;
/*      */ import org.springframework.http.HttpMethod;
/*      */ import org.springframework.http.HttpOutputMessage;
/*      */ import org.springframework.http.HttpStatus;
/*      */ import org.springframework.http.MediaType;
/*      */ import org.springframework.http.RequestEntity;
/*      */ import org.springframework.http.ResponseEntity;
/*      */ import org.springframework.http.client.ClientHttpRequest;
/*      */ import org.springframework.http.client.ClientHttpRequestFactory;
/*      */ import org.springframework.http.client.ClientHttpResponse;
/*      */ import org.springframework.http.client.support.InterceptingHttpAccessor;
/*      */ import org.springframework.http.converter.ByteArrayHttpMessageConverter;
/*      */ import org.springframework.http.converter.GenericHttpMessageConverter;
/*      */ import org.springframework.http.converter.HttpMessageConverter;
/*      */ import org.springframework.http.converter.ResourceHttpMessageConverter;
/*      */ import org.springframework.http.converter.StringHttpMessageConverter;
/*      */ import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
/*      */ import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
/*      */ import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
/*      */ import org.springframework.http.converter.json.GsonHttpMessageConverter;
/*      */ import org.springframework.http.converter.json.JsonbHttpMessageConverter;
/*      */ import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
/*      */ import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
/*      */ import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
/*      */ import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
/*      */ import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
/*      */ import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
/*      */ import org.springframework.http.converter.xml.SourceHttpMessageConverter;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.web.util.AbstractUriTemplateHandler;
/*      */ import org.springframework.web.util.DefaultUriBuilderFactory;
/*      */ import org.springframework.web.util.UriTemplateHandler;
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
/*      */ public class RestTemplate
/*      */   extends InterceptingHttpAccessor
/*      */   implements RestOperations
/*      */ {
/*  101 */   private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
/*      */   
/*      */   private static final boolean romePresent;
/*      */   
/*      */   private static final boolean jaxb2Present;
/*      */   
/*      */   private static final boolean jackson2Present;
/*      */   
/*      */   private static final boolean jackson2XmlPresent;
/*      */   
/*      */   private static final boolean jackson2SmilePresent;
/*      */   
/*      */   private static final boolean jackson2CborPresent;
/*      */   
/*      */   private static final boolean gsonPresent;
/*      */   
/*      */   private static final boolean jsonbPresent;
/*      */   
/*      */   private static final boolean kotlinSerializationJsonPresent;
/*      */   
/*      */   static {
/*  122 */     ClassLoader classLoader = RestTemplate.class.getClassLoader();
/*  123 */     romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", classLoader);
/*  124 */     jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
/*      */     
/*  126 */     jackson2Present = (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader));
/*  127 */     jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
/*  128 */     jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
/*  129 */     jackson2CborPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.cbor.CBORFactory", classLoader);
/*  130 */     gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
/*  131 */     jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", classLoader);
/*  132 */     kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
/*      */   }
/*      */ 
/*      */   
/*  136 */   private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
/*      */   
/*  138 */   private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
/*      */   
/*      */   private UriTemplateHandler uriTemplateHandler;
/*      */   
/*  142 */   private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RestTemplate() {
/*  150 */     this.messageConverters.add(new ByteArrayHttpMessageConverter());
/*  151 */     this.messageConverters.add(new StringHttpMessageConverter());
/*  152 */     this.messageConverters.add(new ResourceHttpMessageConverter(false));
/*  153 */     if (!shouldIgnoreXml) {
/*      */       try {
/*  155 */         this.messageConverters.add(new SourceHttpMessageConverter());
/*      */       }
/*  157 */       catch (Error error) {}
/*      */     }
/*      */ 
/*      */     
/*  161 */     this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
/*      */     
/*  163 */     if (romePresent) {
/*  164 */       this.messageConverters.add(new AtomFeedHttpMessageConverter());
/*  165 */       this.messageConverters.add(new RssChannelHttpMessageConverter());
/*      */     } 
/*      */     
/*  168 */     if (!shouldIgnoreXml) {
/*  169 */       if (jackson2XmlPresent) {
/*  170 */         this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
/*      */       }
/*  172 */       else if (jaxb2Present) {
/*  173 */         this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
/*      */       } 
/*      */     }
/*      */     
/*  177 */     if (jackson2Present) {
/*  178 */       this.messageConverters.add(new MappingJackson2HttpMessageConverter());
/*      */     }
/*  180 */     else if (gsonPresent) {
/*  181 */       this.messageConverters.add(new GsonHttpMessageConverter());
/*      */     }
/*  183 */     else if (jsonbPresent) {
/*  184 */       this.messageConverters.add(new JsonbHttpMessageConverter());
/*      */     }
/*  186 */     else if (kotlinSerializationJsonPresent) {
/*  187 */       this.messageConverters.add(new KotlinSerializationJsonHttpMessageConverter());
/*      */     } 
/*      */     
/*  190 */     if (jackson2SmilePresent) {
/*  191 */       this.messageConverters.add(new MappingJackson2SmileHttpMessageConverter());
/*      */     }
/*  193 */     if (jackson2CborPresent) {
/*  194 */       this.messageConverters.add(new MappingJackson2CborHttpMessageConverter());
/*      */     }
/*      */     
/*  197 */     this.uriTemplateHandler = (UriTemplateHandler)initUriTemplateHandler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RestTemplate(ClientHttpRequestFactory requestFactory) {
/*  207 */     this();
/*  208 */     setRequestFactory(requestFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
/*  218 */     validateConverters(messageConverters);
/*  219 */     this.messageConverters.addAll(messageConverters);
/*  220 */     this.uriTemplateHandler = (UriTemplateHandler)initUriTemplateHandler();
/*      */   }
/*      */ 
/*      */   
/*      */   private static DefaultUriBuilderFactory initUriTemplateHandler() {
/*  225 */     DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
/*  226 */     uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
/*  227 */     return uriFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
/*  236 */     validateConverters(messageConverters);
/*      */     
/*  238 */     if (this.messageConverters != messageConverters) {
/*  239 */       this.messageConverters.clear();
/*  240 */       this.messageConverters.addAll(messageConverters);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void validateConverters(List<HttpMessageConverter<?>> messageConverters) {
/*  245 */     Assert.notEmpty(messageConverters, "At least one HttpMessageConverter is required");
/*  246 */     Assert.noNullElements(messageConverters, "The HttpMessageConverter list must not contain null elements");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<HttpMessageConverter<?>> getMessageConverters() {
/*  254 */     return this.messageConverters;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setErrorHandler(ResponseErrorHandler errorHandler) {
/*  262 */     Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
/*  263 */     this.errorHandler = errorHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResponseErrorHandler getErrorHandler() {
/*  270 */     return this.errorHandler;
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
/*      */   public void setDefaultUriVariables(Map<String, ?> uriVars) {
/*  287 */     if (this.uriTemplateHandler instanceof DefaultUriBuilderFactory) {
/*  288 */       ((DefaultUriBuilderFactory)this.uriTemplateHandler).setDefaultUriVariables(uriVars);
/*      */     }
/*  290 */     else if (this.uriTemplateHandler instanceof AbstractUriTemplateHandler) {
/*  291 */       ((AbstractUriTemplateHandler)this.uriTemplateHandler)
/*  292 */         .setDefaultUriVariables(uriVars);
/*      */     } else {
/*      */       
/*  295 */       throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
/*      */     } 
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
/*      */   public void setUriTemplateHandler(UriTemplateHandler handler) {
/*  314 */     Assert.notNull(handler, "UriTemplateHandler must not be null");
/*  315 */     this.uriTemplateHandler = handler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriTemplateHandler getUriTemplateHandler() {
/*  322 */     return this.uriTemplateHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  331 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*      */     
/*  333 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  334 */     return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  340 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*      */     
/*  342 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  343 */     return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
/*  349 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*      */     
/*  351 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  352 */     return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  359 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*  360 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  361 */     return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  368 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*  369 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  370 */     return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
/*  375 */     RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
/*  376 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  377 */     return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
/*  385 */     return nonNull(execute(url, HttpMethod.HEAD, (RequestCallback)null, headersExtractor(), uriVariables));
/*      */   }
/*      */ 
/*      */   
/*      */   public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
/*  390 */     return nonNull(execute(url, HttpMethod.HEAD, (RequestCallback)null, headersExtractor(), uriVariables));
/*      */   }
/*      */ 
/*      */   
/*      */   public HttpHeaders headForHeaders(URI url) throws RestClientException {
/*  395 */     return nonNull(execute(url, HttpMethod.HEAD, (RequestCallback)null, headersExtractor()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public URI postForLocation(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
/*  406 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  407 */     HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
/*  408 */     return (headers != null) ? headers.getLocation() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
/*  416 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  417 */     HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
/*  418 */     return (headers != null) ? headers.getLocation() : null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public URI postForLocation(URI url, @Nullable Object request) throws RestClientException {
/*  424 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  425 */     HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor());
/*  426 */     return (headers != null) ? headers.getLocation() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  434 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  436 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  437 */     return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  445 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  447 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  448 */     return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
/*  456 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  458 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters());
/*  459 */     return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  466 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*  467 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  468 */     return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  475 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*  476 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  477 */     return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
/*  484 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*  485 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  486 */     return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
/*  496 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  497 */     execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null, uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {
/*  504 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  505 */     execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null, uriVariables);
/*      */   }
/*      */ 
/*      */   
/*      */   public void put(URI url, @Nullable Object request) throws RestClientException {
/*  510 */     RequestCallback requestCallback = httpEntityCallback(request);
/*  511 */     execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  522 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  524 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  525 */     return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T patchForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  533 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  535 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters(), this.logger);
/*  536 */     return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T> T patchForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
/*  544 */     RequestCallback requestCallback = httpEntityCallback(request, responseType);
/*      */     
/*  546 */     HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<>(responseType, getMessageConverters());
/*  547 */     return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void delete(String url, Object... uriVariables) throws RestClientException {
/*  555 */     execute(url, HttpMethod.DELETE, (RequestCallback)null, (ResponseExtractor<?>)null, uriVariables);
/*      */   }
/*      */ 
/*      */   
/*      */   public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
/*  560 */     execute(url, HttpMethod.DELETE, (RequestCallback)null, (ResponseExtractor<?>)null, uriVariables);
/*      */   }
/*      */ 
/*      */   
/*      */   public void delete(URI url) throws RestClientException {
/*  565 */     execute(url, HttpMethod.DELETE, (RequestCallback)null, (ResponseExtractor<?>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
/*  573 */     ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
/*  574 */     HttpHeaders headers = execute(url, HttpMethod.OPTIONS, (RequestCallback)null, headersExtractor, uriVariables);
/*  575 */     return (headers != null) ? headers.getAllow() : Collections.<HttpMethod>emptySet();
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
/*  580 */     ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
/*  581 */     HttpHeaders headers = execute(url, HttpMethod.OPTIONS, (RequestCallback)null, headersExtractor, uriVariables);
/*  582 */     return (headers != null) ? headers.getAllow() : Collections.<HttpMethod>emptySet();
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
/*  587 */     ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
/*  588 */     HttpHeaders headers = execute(url, HttpMethod.OPTIONS, (RequestCallback)null, headersExtractor);
/*  589 */     return (headers != null) ? headers.getAllow() : Collections.<HttpMethod>emptySet();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
/*  600 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
/*  601 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  602 */     return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  610 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
/*  611 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  612 */     return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
/*  619 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
/*  620 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  621 */     return nonNull(execute(url, method, requestCallback, responseExtractor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
/*  628 */     Type type = responseType.getType();
/*  629 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
/*  630 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
/*  631 */     return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
/*  638 */     Type type = responseType.getType();
/*  639 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
/*  640 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
/*  641 */     return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
/*  648 */     Type type = responseType.getType();
/*  649 */     RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
/*  650 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
/*  651 */     return nonNull(execute(url, method, requestCallback, responseExtractor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, Class<T> responseType) throws RestClientException {
/*  658 */     RequestCallback requestCallback = httpEntityCallback(entity, responseType);
/*  659 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
/*  660 */     return nonNull(doExecute(resolveUrl(entity), entity.getMethod(), requestCallback, responseExtractor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, ParameterizedTypeReference<T> responseType) throws RestClientException {
/*  667 */     Type type = responseType.getType();
/*  668 */     RequestCallback requestCallback = httpEntityCallback(entity, type);
/*  669 */     ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
/*  670 */     return nonNull(doExecute(resolveUrl(entity), entity.getMethod(), requestCallback, responseExtractor));
/*      */   }
/*      */   
/*      */   private URI resolveUrl(RequestEntity<?> entity) {
/*  674 */     if (entity instanceof RequestEntity.UriTemplateRequestEntity) {
/*  675 */       RequestEntity.UriTemplateRequestEntity<?> ext = (RequestEntity.UriTemplateRequestEntity)entity;
/*  676 */       if (ext.getVars() != null) {
/*  677 */         return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVars());
/*      */       }
/*  679 */       if (ext.getVarsMap() != null) {
/*  680 */         return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVarsMap());
/*      */       }
/*      */       
/*  683 */       throw new IllegalStateException("No variables specified for URI template: " + ext.getUriTemplate());
/*      */     } 
/*      */ 
/*      */     
/*  687 */     return entity.getUrl();
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
/*      */   @Nullable
/*      */   public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
/*  710 */     URI expanded = getUriTemplateHandler().expand(url, uriVariables);
/*  711 */     return doExecute(expanded, method, requestCallback, responseExtractor);
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
/*      */   @Nullable
/*      */   public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
/*  731 */     URI expanded = getUriTemplateHandler().expand(url, uriVariables);
/*  732 */     return doExecute(expanded, method, requestCallback, responseExtractor);
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
/*      */   @Nullable
/*      */   public <T> T execute(URI url, HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
/*  751 */     return doExecute(url, method, requestCallback, responseExtractor);
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
/*      */   @Nullable
/*      */   protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
/*  768 */     Assert.notNull(url, "URI is required");
/*  769 */     Assert.notNull(method, "HttpMethod is required");
/*  770 */     ClientHttpResponse response = null;
/*      */     try {
/*  772 */       ClientHttpRequest request = createRequest(url, method);
/*  773 */       if (requestCallback != null) {
/*  774 */         requestCallback.doWithRequest(request);
/*      */       }
/*  776 */       response = request.execute();
/*  777 */       handleResponse(url, method, response);
/*  778 */       return (responseExtractor != null) ? responseExtractor.extractData(response) : null;
/*      */     }
/*  780 */     catch (IOException ex) {
/*  781 */       String resource = url.toString();
/*  782 */       String query = url.getRawQuery();
/*  783 */       resource = (query != null) ? resource.substring(0, resource.indexOf('?')) : resource;
/*  784 */       throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + resource + "\": " + ex
/*  785 */           .getMessage(), ex);
/*      */     } finally {
/*      */       
/*  788 */       if (response != null) {
/*  789 */         response.close();
/*      */       }
/*      */     } 
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
/*      */   protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
/*  806 */     ResponseErrorHandler errorHandler = getErrorHandler();
/*  807 */     boolean hasError = errorHandler.hasError(response);
/*  808 */     if (this.logger.isDebugEnabled()) {
/*      */       try {
/*  810 */         int code = response.getRawStatusCode();
/*  811 */         HttpStatus status = HttpStatus.resolve(code);
/*  812 */         this.logger.debug("Response " + ((status != null) ? status : Integer.valueOf(code)));
/*      */       }
/*  814 */       catch (IOException iOException) {}
/*      */     }
/*      */ 
/*      */     
/*  818 */     if (hasError) {
/*  819 */       errorHandler.handleError(url, method, response);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
/*  829 */     return new AcceptHeaderRequestCallback(responseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
/*  837 */     return new HttpEntityRequestCallback(requestBody);
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
/*      */   public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
/*  849 */     return new HttpEntityRequestCallback(requestBody, responseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
/*  856 */     return new ResponseEntityResponseExtractor<>(responseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResponseExtractor<HttpHeaders> headersExtractor() {
/*  863 */     return this.headersExtractor;
/*      */   }
/*      */   
/*      */   private static <T> T nonNull(@Nullable T result) {
/*  867 */     Assert.state((result != null), "No result");
/*  868 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class AcceptHeaderRequestCallback
/*      */     implements RequestCallback
/*      */   {
/*      */     @Nullable
/*      */     private final Type responseType;
/*      */ 
/*      */     
/*      */     public AcceptHeaderRequestCallback(Type responseType) {
/*  881 */       this.responseType = responseType;
/*      */     }
/*      */ 
/*      */     
/*      */     public void doWithRequest(ClientHttpRequest request) throws IOException {
/*  886 */       if (this.responseType != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  892 */         List<MediaType> allSupportedMediaTypes = (List<MediaType>)RestTemplate.this.getMessageConverters().stream().filter(converter -> canReadResponse(this.responseType, converter)).flatMap(converter -> getSupportedMediaTypes(this.responseType, converter)).distinct().sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
/*  893 */         if (RestTemplate.this.logger.isDebugEnabled()) {
/*  894 */           RestTemplate.this.logger.debug("Accept=" + allSupportedMediaTypes);
/*      */         }
/*  896 */         request.getHeaders().setAccept(allSupportedMediaTypes);
/*      */       } 
/*      */     }
/*      */     
/*      */     private boolean canReadResponse(Type responseType, HttpMessageConverter<?> converter) {
/*  901 */       Class<?> responseClass = (responseType instanceof Class) ? (Class)responseType : null;
/*  902 */       if (responseClass != null) {
/*  903 */         return converter.canRead(responseClass, null);
/*      */       }
/*  905 */       if (converter instanceof GenericHttpMessageConverter) {
/*  906 */         GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter)converter;
/*  907 */         return genericConverter.canRead(responseType, null, null);
/*      */       } 
/*  909 */       return false;
/*      */     }
/*      */     
/*      */     private Stream<MediaType> getSupportedMediaTypes(Type type, HttpMessageConverter<?> converter) {
/*  913 */       Type rawType = (type instanceof ParameterizedType) ? ((ParameterizedType)type).getRawType() : type;
/*  914 */       Class<?> clazz = (rawType instanceof Class) ? (Class)rawType : null;
/*  915 */       return ((clazz != null) ? converter.getSupportedMediaTypes(clazz) : converter.getSupportedMediaTypes())
/*  916 */         .stream()
/*  917 */         .map(mediaType -> (mediaType.getCharset() != null) ? new MediaType(mediaType.getType(), mediaType.getSubtype()) : mediaType);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class HttpEntityRequestCallback
/*      */     extends AcceptHeaderRequestCallback
/*      */   {
/*      */     private final HttpEntity<?> requestEntity;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HttpEntityRequestCallback(Object requestBody) {
/*  935 */       this(requestBody, null);
/*      */     }
/*      */     
/*      */     public HttpEntityRequestCallback(@Nullable Object requestBody, Type responseType) {
/*  939 */       super(responseType);
/*  940 */       if (requestBody instanceof HttpEntity) {
/*  941 */         this.requestEntity = (HttpEntity)requestBody;
/*      */       }
/*  943 */       else if (requestBody != null) {
/*  944 */         this.requestEntity = new HttpEntity(requestBody);
/*      */       } else {
/*      */         
/*  947 */         this.requestEntity = HttpEntity.EMPTY;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
/*  954 */       super.doWithRequest(httpRequest);
/*  955 */       Object requestBody = this.requestEntity.getBody();
/*  956 */       if (requestBody == null) {
/*  957 */         HttpHeaders httpHeaders = httpRequest.getHeaders();
/*  958 */         HttpHeaders requestHeaders = this.requestEntity.getHeaders();
/*  959 */         if (!requestHeaders.isEmpty()) {
/*  960 */           requestHeaders.forEach((key, values) -> httpHeaders.put(key, new ArrayList(values)));
/*      */         }
/*  962 */         if (httpHeaders.getContentLength() < 0L) {
/*  963 */           httpHeaders.setContentLength(0L);
/*      */         }
/*      */       } else {
/*      */         
/*  967 */         Class<?> requestBodyClass = requestBody.getClass();
/*      */         
/*  969 */         Type requestBodyType = (this.requestEntity instanceof RequestEntity) ? ((RequestEntity)this.requestEntity).getType() : requestBodyClass;
/*  970 */         HttpHeaders httpHeaders = httpRequest.getHeaders();
/*  971 */         HttpHeaders requestHeaders = this.requestEntity.getHeaders();
/*  972 */         MediaType requestContentType = requestHeaders.getContentType();
/*  973 */         for (HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
/*  974 */           if (messageConverter instanceof GenericHttpMessageConverter) {
/*  975 */             GenericHttpMessageConverter<Object> genericConverter = (GenericHttpMessageConverter)messageConverter;
/*      */             
/*  977 */             if (genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
/*  978 */               if (!requestHeaders.isEmpty()) {
/*  979 */                 requestHeaders.forEach((key, values) -> httpHeaders.put(key, new ArrayList(values)));
/*      */               }
/*  981 */               logBody(requestBody, requestContentType, (HttpMessageConverter<?>)genericConverter);
/*  982 */               genericConverter.write(requestBody, requestBodyType, requestContentType, (HttpOutputMessage)httpRequest); return;
/*      */             } 
/*      */             continue;
/*      */           } 
/*  986 */           if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
/*  987 */             if (!requestHeaders.isEmpty()) {
/*  988 */               requestHeaders.forEach((key, values) -> httpHeaders.put(key, new ArrayList(values)));
/*      */             }
/*  990 */             logBody(requestBody, requestContentType, messageConverter);
/*  991 */             messageConverter.write(requestBody, requestContentType, (HttpOutputMessage)httpRequest);
/*      */             
/*      */             return;
/*      */           } 
/*      */         } 
/*  996 */         String message = "No HttpMessageConverter for " + requestBodyClass.getName();
/*  997 */         if (requestContentType != null) {
/*  998 */           message = message + " and content type \"" + requestContentType + "\"";
/*      */         }
/* 1000 */         throw new RestClientException(message);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void logBody(Object body, @Nullable MediaType mediaType, HttpMessageConverter<?> converter) {
/* 1005 */       if (RestTemplate.this.logger.isDebugEnabled()) {
/* 1006 */         if (mediaType != null) {
/* 1007 */           RestTemplate.this.logger.debug("Writing [" + body + "] as \"" + mediaType + "\"");
/*      */         } else {
/*      */           
/* 1010 */           RestTemplate.this.logger.debug("Writing [" + body + "] with " + converter.getClass().getName());
/*      */         } 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class ResponseEntityResponseExtractor<T>
/*      */     implements ResponseExtractor<ResponseEntity<T>>
/*      */   {
/*      */     @Nullable
/*      */     private final HttpMessageConverterExtractor<T> delegate;
/*      */ 
/*      */     
/*      */     public ResponseEntityResponseExtractor(Type responseType) {
/* 1026 */       if (responseType != null && Void.class != responseType) {
/* 1027 */         this.delegate = new HttpMessageConverterExtractor<>(responseType, RestTemplate.this.getMessageConverters(), RestTemplate.this.logger);
/*      */       } else {
/*      */         
/* 1030 */         this.delegate = null;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
/* 1036 */       if (this.delegate != null) {
/* 1037 */         T body = this.delegate.extractData(response);
/* 1038 */         return ((ResponseEntity.BodyBuilder)ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders())).body(body);
/*      */       } 
/*      */       
/* 1041 */       return ((ResponseEntity.BodyBuilder)ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders())).build();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class HeadersExtractor
/*      */     implements ResponseExtractor<HttpHeaders>
/*      */   {
/*      */     private HeadersExtractor() {}
/*      */ 
/*      */     
/*      */     public HttpHeaders extractData(ClientHttpResponse response) {
/* 1054 */       return response.getHeaders();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/RestTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */