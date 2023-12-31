/*     */ package org.springframework.http.converter.protobuf;
/*     */ 
/*     */ import com.google.protobuf.CodedOutputStream;
/*     */ import com.google.protobuf.ExtensionRegistry;
/*     */ import com.google.protobuf.ExtensionRegistryLite;
/*     */ import com.google.protobuf.Message;
/*     */ import com.google.protobuf.MessageOrBuilder;
/*     */ import com.google.protobuf.TextFormat;
/*     */ import com.google.protobuf.util.JsonFormat;
/*     */ import com.googlecode.protobuf.format.FormatFactory;
/*     */ import com.googlecode.protobuf.format.ProtobufFormatter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.AbstractHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageConversionException;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
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
/*     */ public class ProtobufHttpMessageConverter
/*     */   extends AbstractHttpMessageConverter<Message>
/*     */ {
/*  91 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";
/*     */ 
/*     */ 
/*     */   
/* 109 */   private static final Map<Class<?>, Method> methodCache = (Map<Class<?>, Method>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */   
/*     */   final ExtensionRegistry extensionRegistry;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final ProtobufFormatSupport protobufFormatSupport;
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtobufHttpMessageConverter() {
/* 121 */     this(null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProtobufHttpMessageConverter(@Nullable ExtensionRegistryInitializer registryInitializer) {
/* 132 */     this(null, null);
/* 133 */     if (registryInitializer != null) {
/* 134 */       registryInitializer.initializeExtensionRegistry(this.extensionRegistry);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtobufHttpMessageConverter(ExtensionRegistry extensionRegistry) {
/* 144 */     this(null, extensionRegistry);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ProtobufHttpMessageConverter(@Nullable ProtobufFormatSupport formatSupport, @Nullable ExtensionRegistry extensionRegistry) {
/* 150 */     if (formatSupport != null) {
/* 151 */       this.protobufFormatSupport = formatSupport;
/*     */     }
/* 153 */     else if (ClassUtils.isPresent("com.googlecode.protobuf.format.FormatFactory", getClass().getClassLoader())) {
/* 154 */       this.protobufFormatSupport = new ProtobufJavaFormatSupport();
/*     */     }
/* 156 */     else if (ClassUtils.isPresent("com.google.protobuf.util.JsonFormat", getClass().getClassLoader())) {
/* 157 */       this.protobufFormatSupport = new ProtobufJavaUtilSupport(null, null);
/*     */     } else {
/*     */       
/* 160 */       this.protobufFormatSupport = null;
/*     */     } 
/*     */ 
/*     */     
/* 164 */     (new MediaType[2])[0] = PROTOBUF; (new MediaType[2])[1] = MediaType.TEXT_PLAIN; setSupportedMediaTypes(Arrays.asList((this.protobufFormatSupport != null) ? this.protobufFormatSupport.supportedMediaTypes() : new MediaType[2]));
/*     */     
/* 166 */     this.extensionRegistry = (extensionRegistry == null) ? ExtensionRegistry.newInstance() : extensionRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/* 172 */     return Message.class.isAssignableFrom(clazz);
/*     */   }
/*     */ 
/*     */   
/*     */   protected MediaType getDefaultContentType(Message message) {
/* 177 */     return PROTOBUF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 184 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/* 185 */     if (contentType == null) {
/* 186 */       contentType = PROTOBUF;
/*     */     }
/* 188 */     Charset charset = contentType.getCharset();
/* 189 */     if (charset == null) {
/* 190 */       charset = DEFAULT_CHARSET;
/*     */     }
/*     */     
/* 193 */     Message.Builder builder = getMessageBuilder(clazz);
/* 194 */     if (PROTOBUF.isCompatibleWith(contentType)) {
/* 195 */       builder.mergeFrom(inputMessage.getBody(), (ExtensionRegistryLite)this.extensionRegistry);
/*     */     }
/* 197 */     else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
/* 198 */       InputStreamReader reader = new InputStreamReader(inputMessage.getBody(), charset);
/* 199 */       TextFormat.merge(reader, this.extensionRegistry, builder);
/*     */     }
/* 201 */     else if (this.protobufFormatSupport != null) {
/* 202 */       this.protobufFormatSupport.merge(inputMessage
/* 203 */           .getBody(), charset, contentType, this.extensionRegistry, builder);
/*     */     } 
/* 205 */     return builder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Message.Builder getMessageBuilder(Class<? extends Message> clazz) {
/*     */     try {
/* 214 */       Method method = methodCache.get(clazz);
/* 215 */       if (method == null) {
/* 216 */         method = clazz.getMethod("newBuilder", new Class[0]);
/* 217 */         methodCache.put(clazz, method);
/*     */       } 
/* 219 */       return (Message.Builder)method.invoke(clazz, new Object[0]);
/*     */     }
/* 221 */     catch (Exception ex) {
/* 222 */       throw new HttpMessageConversionException("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean canWrite(@Nullable MediaType mediaType) {
/* 230 */     return (super.canWrite(mediaType) || (this.protobufFormatSupport != null && this.protobufFormatSupport
/* 231 */       .supportsWriteOnly(mediaType)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(Message message, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 239 */     MediaType contentType = outputMessage.getHeaders().getContentType();
/* 240 */     if (contentType == null) {
/* 241 */       contentType = getDefaultContentType(message);
/* 242 */       Assert.state((contentType != null), "No content type");
/*     */     } 
/* 244 */     Charset charset = contentType.getCharset();
/* 245 */     if (charset == null) {
/* 246 */       charset = DEFAULT_CHARSET;
/*     */     }
/*     */     
/* 249 */     if (PROTOBUF.isCompatibleWith(contentType)) {
/* 250 */       setProtoHeader(outputMessage, message);
/* 251 */       CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputMessage.getBody());
/* 252 */       message.writeTo(codedOutputStream);
/* 253 */       codedOutputStream.flush();
/*     */     }
/* 255 */     else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
/* 256 */       OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
/* 257 */       TextFormat.print((MessageOrBuilder)message, outputStreamWriter);
/* 258 */       outputStreamWriter.flush();
/* 259 */       outputMessage.getBody().flush();
/*     */     }
/* 261 */     else if (this.protobufFormatSupport != null) {
/* 262 */       this.protobufFormatSupport.print(message, outputMessage.getBody(), contentType, charset);
/* 263 */       outputMessage.getBody().flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setProtoHeader(HttpOutputMessage response, Message message) {
/* 274 */     response.getHeaders().set("X-Protobuf-Schema", message.getDescriptorForType().getFile().getName());
/* 275 */     response.getHeaders().set("X-Protobuf-Message", message.getDescriptorForType().getFullName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static interface ProtobufFormatSupport
/*     */   {
/*     */     MediaType[] supportedMediaTypes();
/*     */ 
/*     */ 
/*     */     
/*     */     boolean supportsWriteOnly(@Nullable MediaType param1MediaType);
/*     */ 
/*     */ 
/*     */     
/*     */     void merge(InputStream param1InputStream, Charset param1Charset, MediaType param1MediaType, ExtensionRegistry param1ExtensionRegistry, Message.Builder param1Builder) throws IOException, HttpMessageConversionException;
/*     */ 
/*     */ 
/*     */     
/*     */     void print(Message param1Message, OutputStream param1OutputStream, MediaType param1MediaType, Charset param1Charset) throws IOException, HttpMessageConversionException;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class ProtobufJavaFormatSupport
/*     */     implements ProtobufFormatSupport
/*     */   {
/*     */     private final ProtobufFormatter jsonFormatter;
/*     */     
/*     */     private final ProtobufFormatter xmlFormatter;
/*     */     
/*     */     private final ProtobufFormatter htmlFormatter;
/*     */ 
/*     */     
/*     */     public ProtobufJavaFormatSupport() {
/* 310 */       FormatFactory formatFactory = new FormatFactory();
/* 311 */       this.jsonFormatter = formatFactory.createFormatter(FormatFactory.Formatter.JSON);
/* 312 */       this.xmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.XML);
/* 313 */       this.htmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.HTML);
/*     */     }
/*     */ 
/*     */     
/*     */     public MediaType[] supportedMediaTypes() {
/* 318 */       return new MediaType[] { ProtobufHttpMessageConverter.PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON };
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
/* 323 */       return MediaType.TEXT_HTML.isCompatibleWith(mediaType);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
/* 331 */       if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
/* 332 */         this.jsonFormatter.merge(input, charset, extensionRegistry, builder);
/*     */       }
/* 334 */       else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
/* 335 */         this.xmlFormatter.merge(input, charset, extensionRegistry, builder);
/*     */       } else {
/*     */         
/* 338 */         throw new HttpMessageConversionException("protobuf-java-format does not support parsing " + contentType);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
/* 347 */       if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
/* 348 */         this.jsonFormatter.print(message, output, charset);
/*     */       }
/* 350 */       else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
/* 351 */         this.xmlFormatter.print(message, output, charset);
/*     */       }
/* 353 */       else if (contentType.isCompatibleWith(MediaType.TEXT_HTML)) {
/* 354 */         this.htmlFormatter.print(message, output, charset);
/*     */       } else {
/*     */         
/* 357 */         throw new HttpMessageConversionException("protobuf-java-format does not support printing " + contentType);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class ProtobufJavaUtilSupport
/*     */     implements ProtobufFormatSupport
/*     */   {
/*     */     private final JsonFormat.Parser parser;
/*     */ 
/*     */     
/*     */     private final JsonFormat.Printer printer;
/*     */ 
/*     */ 
/*     */     
/*     */     public ProtobufJavaUtilSupport(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
/* 375 */       this.parser = (parser != null) ? parser : JsonFormat.parser();
/* 376 */       this.printer = (printer != null) ? printer : JsonFormat.printer();
/*     */     }
/*     */ 
/*     */     
/*     */     public MediaType[] supportedMediaTypes() {
/* 381 */       return new MediaType[] { ProtobufHttpMessageConverter.PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON };
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
/* 386 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
/* 394 */       if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
/* 395 */         InputStreamReader reader = new InputStreamReader(input, charset);
/* 396 */         this.parser.merge(reader, builder);
/*     */       } else {
/*     */         
/* 399 */         throw new HttpMessageConversionException("protobuf-java-util does not support parsing " + contentType);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
/* 408 */       if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
/* 409 */         OutputStreamWriter writer = new OutputStreamWriter(output, charset);
/* 410 */         this.printer.appendTo((MessageOrBuilder)message, writer);
/* 411 */         writer.flush();
/*     */       } else {
/*     */         
/* 414 */         throw new HttpMessageConversionException("protobuf-java-util does not support printing " + contentType);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */