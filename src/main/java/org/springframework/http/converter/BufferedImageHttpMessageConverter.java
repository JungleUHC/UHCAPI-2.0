/*     */ package org.springframework.http.converter;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.imageio.IIOImage;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.imageio.ImageReadParam;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.ImageWriteParam;
/*     */ import javax.imageio.ImageWriter;
/*     */ import javax.imageio.stream.FileCacheImageInputStream;
/*     */ import javax.imageio.stream.FileCacheImageOutputStream;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageInputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageOutputStream;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.StreamingHttpOutputMessage;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MimeType;
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
/*     */ 
/*     */ 
/*     */ public class BufferedImageHttpMessageConverter
/*     */   implements HttpMessageConverter<BufferedImage>
/*     */ {
/*  73 */   private final List<MediaType> readableMediaTypes = new ArrayList<>();
/*     */   
/*     */   @Nullable
/*     */   private MediaType defaultContentType;
/*     */   
/*     */   @Nullable
/*     */   private File cacheDir;
/*     */ 
/*     */   
/*     */   public BufferedImageHttpMessageConverter() {
/*  83 */     String[] readerMediaTypes = ImageIO.getReaderMIMETypes();
/*  84 */     for (String mediaType : readerMediaTypes) {
/*  85 */       if (StringUtils.hasText(mediaType)) {
/*  86 */         this.readableMediaTypes.add(MediaType.parseMediaType(mediaType));
/*     */       }
/*     */     } 
/*     */     
/*  90 */     String[] writerMediaTypes = ImageIO.getWriterMIMETypes();
/*  91 */     for (String mediaType : writerMediaTypes) {
/*  92 */       if (StringUtils.hasText(mediaType)) {
/*  93 */         this.defaultContentType = MediaType.parseMediaType(mediaType);
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultContentType(@Nullable MediaType defaultContentType) {
/* 105 */     if (defaultContentType != null) {
/* 106 */       Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(defaultContentType.toString());
/* 107 */       if (!imageWriters.hasNext()) {
/* 108 */         throw new IllegalArgumentException("Content-Type [" + defaultContentType + "] is not supported by the Java Image I/O API");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 113 */     this.defaultContentType = defaultContentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public MediaType getDefaultContentType() {
/* 122 */     return this.defaultContentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCacheDir(File cacheDir) {
/* 130 */     Assert.notNull(cacheDir, "'cacheDir' must not be null");
/* 131 */     Assert.isTrue(cacheDir.isDirectory(), "'cacheDir' is not a directory");
/* 132 */     this.cacheDir = cacheDir;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/* 138 */     return (BufferedImage.class == clazz && isReadable(mediaType));
/*     */   }
/*     */   
/*     */   private boolean isReadable(@Nullable MediaType mediaType) {
/* 142 */     if (mediaType == null) {
/* 143 */       return true;
/*     */     }
/* 145 */     Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(mediaType.toString());
/* 146 */     return imageReaders.hasNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/* 151 */     return (BufferedImage.class == clazz && isWritable(mediaType));
/*     */   }
/*     */   
/*     */   private boolean isWritable(@Nullable MediaType mediaType) {
/* 155 */     if (mediaType == null || MediaType.ALL.equalsTypeAndSubtype((MimeType)mediaType)) {
/* 156 */       return true;
/*     */     }
/* 158 */     Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(mediaType.toString());
/* 159 */     return imageWriters.hasNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getSupportedMediaTypes() {
/* 164 */     return Collections.unmodifiableList(this.readableMediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage read(@Nullable Class<? extends BufferedImage> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 171 */     ImageInputStream imageInputStream = null;
/* 172 */     ImageReader imageReader = null;
/*     */ 
/*     */     
/*     */     try {
/* 176 */       imageInputStream = createImageInputStream(inputMessage.getBody());
/* 177 */       MediaType contentType = inputMessage.getHeaders().getContentType();
/* 178 */       if (contentType == null) {
/* 179 */         throw new HttpMessageNotReadableException("No Content-Type header", inputMessage);
/*     */       }
/* 181 */       Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(contentType.toString());
/* 182 */       if (imageReaders.hasNext()) {
/* 183 */         imageReader = imageReaders.next();
/* 184 */         ImageReadParam irp = imageReader.getDefaultReadParam();
/* 185 */         process(irp);
/* 186 */         imageReader.setInput(imageInputStream, true);
/* 187 */         return imageReader.read(0, irp);
/*     */       } 
/*     */       
/* 190 */       throw new HttpMessageNotReadableException("Could not find javax.imageio.ImageReader for Content-Type [" + contentType + "]", inputMessage);
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */       
/* 196 */       if (imageReader != null) {
/* 197 */         imageReader.dispose();
/*     */       }
/* 199 */       if (imageInputStream != null) {
/*     */         try {
/* 201 */           imageInputStream.close();
/*     */         }
/* 203 */         catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ImageInputStream createImageInputStream(InputStream is) throws IOException {
/* 211 */     is = StreamUtils.nonClosing(is);
/* 212 */     if (this.cacheDir != null) {
/* 213 */       return new FileCacheImageInputStream(is, this.cacheDir);
/*     */     }
/*     */     
/* 216 */     return new MemoryCacheImageInputStream(is);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(BufferedImage image, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 225 */     MediaType selectedContentType = getContentType(contentType);
/* 226 */     outputMessage.getHeaders().setContentType(selectedContentType);
/*     */     
/* 228 */     if (outputMessage instanceof StreamingHttpOutputMessage) {
/* 229 */       StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
/* 230 */       streamingOutputMessage.setBody(outputStream -> writeInternal(image, selectedContentType, outputStream));
/*     */     } else {
/*     */       
/* 233 */       writeInternal(image, selectedContentType, outputMessage.getBody());
/*     */     } 
/*     */   }
/*     */   
/*     */   private MediaType getContentType(@Nullable MediaType contentType) {
/* 238 */     if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
/* 239 */       contentType = getDefaultContentType();
/*     */     }
/* 241 */     Assert.notNull(contentType, "Could not select Content-Type. Please specify one through the 'defaultContentType' property.");
/*     */     
/* 243 */     return contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeInternal(BufferedImage image, MediaType contentType, OutputStream body) throws IOException, HttpMessageNotWritableException {
/* 249 */     ImageOutputStream imageOutputStream = null;
/* 250 */     ImageWriter imageWriter = null;
/*     */     try {
/* 252 */       Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(contentType.toString());
/* 253 */       if (imageWriters.hasNext()) {
/* 254 */         imageWriter = imageWriters.next();
/* 255 */         ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
/* 256 */         process(iwp);
/* 257 */         imageOutputStream = createImageOutputStream(body);
/* 258 */         imageWriter.setOutput(imageOutputStream);
/* 259 */         imageWriter.write(null, new IIOImage(image, null, null), iwp);
/*     */       } else {
/*     */         
/* 262 */         throw new HttpMessageNotWritableException("Could not find javax.imageio.ImageWriter for Content-Type [" + contentType + "]");
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 267 */       if (imageWriter != null) {
/* 268 */         imageWriter.dispose();
/*     */       }
/* 270 */       if (imageOutputStream != null) {
/*     */         try {
/* 272 */           imageOutputStream.close();
/*     */         }
/* 274 */         catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ImageOutputStream createImageOutputStream(OutputStream os) throws IOException {
/* 282 */     if (this.cacheDir != null) {
/* 283 */       return new FileCacheImageOutputStream(os, this.cacheDir);
/*     */     }
/*     */     
/* 286 */     return new MemoryCacheImageOutputStream(os);
/*     */   }
/*     */   
/*     */   protected void process(ImageReadParam irp) {}
/*     */   
/*     */   protected void process(ImageWriteParam iwp) {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/BufferedImageHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */