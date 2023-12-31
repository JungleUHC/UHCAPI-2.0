/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.node.ArrayNode;
/*     */ import com.fasterxml.jackson.databind.node.ContainerNode;
/*     */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonNodeDeserializer
/*     */   extends BaseNodeDeserializer<JsonNode>
/*     */ {
/*  28 */   private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonNodeDeserializer() {
/*  34 */     super(JsonNode.class, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
/*  42 */     if (nodeClass == ObjectNode.class) {
/*  43 */       return ObjectDeserializer.getInstance();
/*     */     }
/*  45 */     if (nodeClass == ArrayNode.class) {
/*  46 */       return ArrayDeserializer.getInstance();
/*     */     }
/*     */     
/*  49 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode getNullValue(DeserializationContext ctxt) {
/*  60 */     return (JsonNode)ctxt.getNodeFactory().nullNode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAbsentValue(DeserializationContext ctxt) {
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  80 */     BaseNodeDeserializer.ContainerStack stack = new BaseNodeDeserializer.ContainerStack();
/*  81 */     JsonNodeFactory nodeF = ctxt.getNodeFactory();
/*  82 */     switch (p.currentTokenId()) {
/*     */       case 1:
/*  84 */         return (JsonNode)_deserializeContainerNoRecursion(p, ctxt, nodeF, stack, (ContainerNode<?>)nodeF.objectNode());
/*     */       case 2:
/*  86 */         return (JsonNode)nodeF.objectNode();
/*     */       case 3:
/*  88 */         return (JsonNode)_deserializeContainerNoRecursion(p, ctxt, nodeF, stack, (ContainerNode<?>)nodeF.arrayNode());
/*     */       case 5:
/*  90 */         return (JsonNode)_deserializeObjectAtName(p, ctxt, nodeF, stack);
/*     */     } 
/*     */     
/*  93 */     return _deserializeAnyScalar(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ObjectDeserializer
/*     */     extends BaseNodeDeserializer<ObjectNode>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     protected static final ObjectDeserializer _instance = new ObjectDeserializer();
/*     */     protected ObjectDeserializer() {
/* 112 */       super(ObjectNode.class, Boolean.valueOf(true));
/*     */     } public static ObjectDeserializer getInstance() {
/* 114 */       return _instance;
/*     */     }
/*     */ 
/*     */     
/*     */     public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 119 */       JsonNodeFactory nodeF = ctxt.getNodeFactory();
/* 120 */       if (p.isExpectedStartObjectToken()) {
/* 121 */         ObjectNode root = nodeF.objectNode();
/* 122 */         _deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), (ContainerNode<?>)root);
/* 123 */         return root;
/*     */       } 
/* 125 */       if (p.hasToken(JsonToken.FIELD_NAME)) {
/* 126 */         return _deserializeObjectAtName(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack());
/*     */       }
/*     */ 
/*     */       
/* 130 */       if (p.hasToken(JsonToken.END_OBJECT)) {
/* 131 */         return nodeF.objectNode();
/*     */       }
/* 133 */       return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
/* 145 */       if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
/* 146 */         return (ObjectNode)updateObject(p, ctxt, node, new BaseNodeDeserializer.ContainerStack());
/*     */       }
/*     */       
/* 149 */       return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ArrayDeserializer
/*     */     extends BaseNodeDeserializer<ArrayNode>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/* 161 */     protected static final ArrayDeserializer _instance = new ArrayDeserializer();
/*     */     protected ArrayDeserializer() {
/* 163 */       super(ArrayNode.class, Boolean.valueOf(true));
/*     */     } public static ArrayDeserializer getInstance() {
/* 165 */       return _instance;
/*     */     }
/*     */ 
/*     */     
/*     */     public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 170 */       if (p.isExpectedStartArrayToken()) {
/* 171 */         JsonNodeFactory nodeF = ctxt.getNodeFactory();
/* 172 */         ArrayNode arrayNode = nodeF.arrayNode();
/* 173 */         _deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), (ContainerNode<?>)arrayNode);
/*     */         
/* 175 */         return arrayNode;
/*     */       } 
/* 177 */       return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode arrayNode) throws IOException {
/* 187 */       if (p.isExpectedStartArrayToken()) {
/* 188 */         _deserializeContainerNoRecursion(p, ctxt, ctxt.getNodeFactory(), new BaseNodeDeserializer.ContainerStack(), (ContainerNode<?>)arrayNode);
/*     */         
/* 190 */         return arrayNode;
/*     */       } 
/* 192 */       return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */