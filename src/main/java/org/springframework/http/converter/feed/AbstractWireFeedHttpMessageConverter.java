/*     */ package org.springframework.http.converter.feed;
/*     */ 
/*     */ import com.rometools.rome.feed.WireFeed;
/*     */ import com.rometools.rome.io.FeedException;
/*     */ import com.rometools.rome.io.WireFeedInput;
/*     */ import com.rometools.rome.io.WireFeedOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.AbstractHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public abstract class AbstractWireFeedHttpMessageConverter<T extends WireFeed>
/*     */   extends AbstractHttpMessageConverter<T>
/*     */ {
/*  61 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */ 
/*     */   
/*     */   protected AbstractWireFeedHttpMessageConverter(MediaType supportedMediaType) {
/*  65 */     super(supportedMediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/*  74 */     WireFeedInput feedInput = new WireFeedInput();
/*  75 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/*     */     
/*  77 */     Charset charset = (contentType != null && contentType.getCharset() != null) ? contentType.getCharset() : DEFAULT_CHARSET;
/*     */     try {
/*  79 */       InputStream inputStream = StreamUtils.nonClosing(inputMessage.getBody());
/*  80 */       Reader reader = new InputStreamReader(inputStream, charset);
/*  81 */       return (T)feedInput.build(reader);
/*     */     }
/*  83 */     catch (FeedException ex) {
/*  84 */       throw new HttpMessageNotReadableException("Could not read WireFeed: " + ex.getMessage(), ex, inputMessage);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(T wireFeed, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/*  93 */     Charset charset = StringUtils.hasLength(wireFeed.getEncoding()) ? Charset.forName(wireFeed.getEncoding()) : DEFAULT_CHARSET;
/*  94 */     MediaType contentType = outputMessage.getHeaders().getContentType();
/*  95 */     if (contentType != null) {
/*  96 */       contentType = new MediaType(contentType, charset);
/*  97 */       outputMessage.getHeaders().setContentType(contentType);
/*     */     } 
/*     */     
/* 100 */     WireFeedOutput feedOutput = new WireFeedOutput();
/*     */     try {
/* 102 */       Writer writer = new OutputStreamWriter(outputMessage.getBody(), charset);
/* 103 */       feedOutput.output((WireFeed)wireFeed, writer);
/*     */     }
/* 105 */     catch (FeedException ex) {
/* 106 */       throw new HttpMessageNotWritableException("Could not write WireFeed: " + ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/feed/AbstractWireFeedHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */