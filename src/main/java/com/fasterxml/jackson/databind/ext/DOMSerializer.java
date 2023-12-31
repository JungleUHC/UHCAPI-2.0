/*    */ package com.fasterxml.jackson.databind.ext;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.JsonNode;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.fasterxml.jackson.databind.ser.std.StdSerializer;
/*    */ import java.io.IOException;
/*    */ import java.io.StringWriter;
/*    */ import java.lang.reflect.Type;
/*    */ import javax.xml.transform.Transformer;
/*    */ import javax.xml.transform.TransformerConfigurationException;
/*    */ import javax.xml.transform.TransformerException;
/*    */ import javax.xml.transform.TransformerFactory;
/*    */ import javax.xml.transform.dom.DOMSource;
/*    */ import javax.xml.transform.stream.StreamResult;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class DOMSerializer
/*    */   extends StdSerializer<Node>
/*    */ {
/*    */   protected final TransformerFactory transformerFactory;
/*    */   
/*    */   public DOMSerializer() {
/* 27 */     super(Node.class);
/*    */     try {
/* 29 */       this.transformerFactory = TransformerFactory.newInstance();
/* 30 */       this.transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/* 31 */     } catch (Exception e) {
/* 32 */       throw new IllegalStateException("Could not instantiate `TransformerFactory`: " + e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(Node value, JsonGenerator g, SerializerProvider provider) throws IOException {
/*    */     try {
/* 41 */       Transformer transformer = this.transformerFactory.newTransformer();
/* 42 */       transformer.setOutputProperty("omit-xml-declaration", "yes");
/* 43 */       transformer.setOutputProperty("indent", "no");
/* 44 */       StreamResult result = new StreamResult(new StringWriter());
/* 45 */       transformer.transform(new DOMSource(value), result);
/* 46 */       g.writeString(result.getWriter().toString());
/* 47 */     } catch (TransformerConfigurationException e) {
/* 48 */       throw new IllegalStateException("Could not create XML Transformer for writing DOM `Node` value: " + e.getMessage(), e);
/* 49 */     } catch (TransformerException e) {
/* 50 */       provider.reportMappingProblem(e, "DOM `Node` value serialization failed: %s", new Object[] { e.getMessage() });
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 57 */     return (JsonNode)createSchemaNode("string", true);
/*    */   }
/*    */ 
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 62 */     if (visitor != null) visitor.expectAnyFormat(typeHint); 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/DOMSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */