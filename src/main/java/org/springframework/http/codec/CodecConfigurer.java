package org.springframework.http.codec;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.lang.Nullable;

public interface CodecConfigurer {
  DefaultCodecs defaultCodecs();
  
  CustomCodecs customCodecs();
  
  void registerDefaults(boolean paramBoolean);
  
  List<HttpMessageReader<?>> getReaders();
  
  List<HttpMessageWriter<?>> getWriters();
  
  CodecConfigurer clone();
  
  public static interface DefaultCodecConfig {
    @Nullable
    Integer maxInMemorySize();
    
    @Nullable
    Boolean isEnableLoggingRequestDetails();
  }
  
  public static interface CustomCodecs {
    void register(Object param1Object);
    
    void registerWithDefaultConfig(Object param1Object);
    
    void registerWithDefaultConfig(Object param1Object, Consumer<CodecConfigurer.DefaultCodecConfig> param1Consumer);
    
    @Deprecated
    void decoder(Decoder<?> param1Decoder);
    
    @Deprecated
    void encoder(Encoder<?> param1Encoder);
    
    @Deprecated
    void reader(HttpMessageReader<?> param1HttpMessageReader);
    
    @Deprecated
    void writer(HttpMessageWriter<?> param1HttpMessageWriter);
    
    @Deprecated
    void withDefaultCodecConfig(Consumer<CodecConfigurer.DefaultCodecConfig> param1Consumer);
  }
  
  public static interface DefaultCodecs {
    void jackson2JsonDecoder(Decoder<?> param1Decoder);
    
    void jackson2JsonEncoder(Encoder<?> param1Encoder);
    
    void jackson2SmileDecoder(Decoder<?> param1Decoder);
    
    void jackson2SmileEncoder(Encoder<?> param1Encoder);
    
    void protobufDecoder(Decoder<?> param1Decoder);
    
    void protobufEncoder(Encoder<?> param1Encoder);
    
    void jaxb2Decoder(Decoder<?> param1Decoder);
    
    void jaxb2Encoder(Encoder<?> param1Encoder);
    
    void kotlinSerializationJsonDecoder(Decoder<?> param1Decoder);
    
    void kotlinSerializationJsonEncoder(Encoder<?> param1Encoder);
    
    void configureDefaultCodec(Consumer<Object> param1Consumer);
    
    void maxInMemorySize(int param1Int);
    
    void enableLoggingRequestDetails(boolean param1Boolean);
  }
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/CodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */