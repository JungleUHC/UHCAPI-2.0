/*    */ package com.fasterxml.jackson.databind.ext;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
/*    */ import java.io.StringReader;
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import javax.xml.parsers.ParserConfigurationException;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Node;
/*    */ import org.xml.sax.InputSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DOMDeserializer<T>
/*    */   extends FromStringDeserializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final DocumentBuilderFactory DEFAULT_PARSER_FACTORY;
/*    */   
/*    */   static {
/* 28 */     DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
/*    */     
/* 30 */     parserFactory.setNamespaceAware(true);
/*    */     
/* 32 */     parserFactory.setExpandEntityReferences(false);
/*    */     
/*    */     try {
/* 35 */       parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/* 36 */     } catch (ParserConfigurationException parserConfigurationException) {
/*    */     
/* 38 */     } catch (Error error) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 45 */       parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
/* 46 */     } catch (Throwable throwable) {}
/*    */     try {
/* 48 */       parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
/* 49 */     } catch (Throwable throwable) {}
/* 50 */     DEFAULT_PARSER_FACTORY = parserFactory;
/*    */   }
/*    */   protected DOMDeserializer(Class<T> cls) {
/* 53 */     super(cls);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected final Document parse(String value) throws IllegalArgumentException {
/*    */     try {
/* 60 */       return documentBuilder().parse(new InputSource(new StringReader(value)));
/* 61 */     } catch (Exception e) {
/* 62 */       throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected DocumentBuilder documentBuilder() throws ParserConfigurationException {
/* 73 */     return DEFAULT_PARSER_FACTORY.newDocumentBuilder();
/*    */   }
/*    */   
/*    */   public abstract T _deserialize(String paramString, DeserializationContext paramDeserializationContext);
/*    */   
/*    */   public static class NodeDeserializer
/*    */     extends DOMDeserializer<Node>
/*    */   {
/*    */     private static final long serialVersionUID = 1L;
/*    */     
/*    */     public NodeDeserializer() {
/* 84 */       super(Node.class);
/*    */     }
/*    */     public Node _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
/* 87 */       return parse(value);
/*    */     }
/*    */   }
/*    */   
/*    */   public static class DocumentDeserializer extends DOMDeserializer<Document> {
/*    */     public DocumentDeserializer() {
/* 93 */       super(Document.class);
/*    */     } private static final long serialVersionUID = 1L;
/*    */     public Document _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
/* 96 */       return parse(value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/DOMDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */