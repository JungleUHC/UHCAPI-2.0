/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.StreamReadCapability;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.node.ArrayNode;
/*     */ import com.fasterxml.jackson.databind.node.BooleanNode;
/*     */ import com.fasterxml.jackson.databind.node.ContainerNode;
/*     */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*     */ import com.fasterxml.jackson.databind.node.NullNode;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.node.TextNode;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.RawValue;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
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
/*     */ abstract class BaseNodeDeserializer<T extends JsonNode>
/*     */   extends StdDeserializer<T>
/*     */ {
/*     */   protected final Boolean _supportsUpdates;
/*     */   
/*     */   public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
/* 210 */     super(vc);
/* 211 */     this._supportsUpdates = supportsUpdates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 220 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 225 */     return LogicalType.Untyped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 232 */     return true;
/*     */   }
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 236 */     return this._supportsUpdates;
/*     */   }
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
/*     */   protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws IOException {
/* 265 */     if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY))
/*     */     {
/*     */ 
/*     */       
/* 269 */       ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for `ObjectNode`: not allowed when `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY` enabled", new Object[] { fieldName });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 274 */     if (ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES))
/*     */     {
/*     */       
/* 277 */       if (oldValue.isArray()) {
/* 278 */         ((ArrayNode)oldValue).add(newValue);
/* 279 */         objectNode.replace(fieldName, oldValue);
/*     */       } else {
/* 281 */         ArrayNode arr = nodeFactory.arrayNode();
/* 282 */         arr.add(oldValue);
/* 283 */         arr.add(newValue);
/* 284 */         objectNode.replace(fieldName, (JsonNode)arr);
/*     */       } 
/*     */     }
/*     */   }
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
/*     */   protected final ObjectNode _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack) throws IOException {
/* 302 */     ObjectNode node = nodeFactory.objectNode();
/* 303 */     String key = p.currentName();
/* 304 */     for (; key != null; key = p.nextFieldName()) {
/*     */       ContainerNode<?> containerNode2, containerNode1; JsonNode value;
/* 306 */       JsonToken t = p.nextToken();
/* 307 */       if (t == null) {
/* 308 */         t = JsonToken.NOT_AVAILABLE;
/*     */       }
/* 310 */       switch (t.id()) {
/*     */         case 1:
/* 312 */           containerNode2 = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ContainerNode<?>)nodeFactory
/* 313 */               .objectNode());
/*     */           break;
/*     */         case 3:
/* 316 */           containerNode1 = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ContainerNode<?>)nodeFactory
/* 317 */               .arrayNode());
/*     */           break;
/*     */         default:
/* 320 */           value = _deserializeAnyScalar(p, ctxt); break;
/*     */       } 
/* 322 */       JsonNode old = node.replace(key, value);
/* 323 */       if (old != null) {
/* 324 */         _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
/*     */       }
/*     */     } 
/*     */     
/* 328 */     return node;
/*     */   }
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
/*     */   protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node, ContainerStack stack) throws IOException {
/* 341 */     if (p.isExpectedStartObjectToken()) {
/* 342 */       str = p.nextFieldName();
/*     */     } else {
/* 344 */       if (!p.hasToken(JsonToken.FIELD_NAME)) {
/* 345 */         return (JsonNode)deserialize(p, ctxt);
/*     */       }
/* 347 */       str = p.currentName();
/*     */     } 
/* 349 */     JsonNodeFactory nodeFactory = ctxt.getNodeFactory(); String str;
/* 350 */     for (; str != null; str = p.nextFieldName()) {
/*     */       ContainerNode<?> containerNode2, containerNode1; TextNode textNode; JsonNode jsonNode1; BooleanNode booleanNode2, booleanNode1; NullNode nullNode; JsonNode value;
/* 352 */       JsonToken t = p.nextToken();
/*     */ 
/*     */       
/* 355 */       JsonNode old = node.get(str);
/* 356 */       if (old != null) {
/* 357 */         if (old instanceof ObjectNode) {
/*     */ 
/*     */           
/* 360 */           if (t == JsonToken.START_OBJECT) {
/* 361 */             JsonNode newValue = updateObject(p, ctxt, (ObjectNode)old, stack);
/* 362 */             if (newValue != old) {
/* 363 */               node.set(str, newValue);
/*     */             }
/*     */             continue;
/*     */           } 
/* 367 */         } else if (old instanceof ArrayNode) {
/*     */ 
/*     */           
/* 370 */           if (t == JsonToken.START_ARRAY) {
/*     */ 
/*     */             
/* 373 */             _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ContainerNode<?>)old);
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } 
/*     */       }
/* 379 */       if (t == null) {
/* 380 */         t = JsonToken.NOT_AVAILABLE;
/*     */       }
/*     */       
/* 383 */       switch (t.id()) {
/*     */         case 1:
/* 385 */           containerNode2 = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ContainerNode<?>)nodeFactory
/* 386 */               .objectNode());
/*     */           break;
/*     */         case 3:
/* 389 */           containerNode1 = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ContainerNode<?>)nodeFactory
/* 390 */               .arrayNode());
/*     */           break;
/*     */         case 6:
/* 393 */           textNode = nodeFactory.textNode(p.getText());
/*     */           break;
/*     */         case 7:
/* 396 */           jsonNode1 = _fromInt(p, ctxt, nodeFactory);
/*     */           break;
/*     */         case 9:
/* 399 */           booleanNode2 = nodeFactory.booleanNode(true);
/*     */           break;
/*     */         case 10:
/* 402 */           booleanNode1 = nodeFactory.booleanNode(false);
/*     */           break;
/*     */         case 11:
/* 405 */           nullNode = nodeFactory.nullNode();
/*     */           break;
/*     */         default:
/* 408 */           value = _deserializeRareScalar(p, ctxt);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 419 */       node.set(str, value); continue;
/*     */     } 
/* 421 */     return (JsonNode)node;
/*     */   }
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
/*     */   protected final ContainerNode<?> _deserializeContainerNoRecursion(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack, ContainerNode<?> root) throws IOException {
/*     */     // Byte code:
/*     */     //   0: aload #5
/*     */     //   2: astore #6
/*     */     //   4: aload_2
/*     */     //   5: invokevirtual getDeserializationFeatures : ()I
/*     */     //   8: getstatic com/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer.F_MASK_INT_COERCIONS : I
/*     */     //   11: iand
/*     */     //   12: istore #7
/*     */     //   14: aload #6
/*     */     //   16: instanceof com/fasterxml/jackson/databind/node/ObjectNode
/*     */     //   19: ifeq -> 343
/*     */     //   22: aload #6
/*     */     //   24: checkcast com/fasterxml/jackson/databind/node/ObjectNode
/*     */     //   27: astore #8
/*     */     //   29: aload_1
/*     */     //   30: invokevirtual nextFieldName : ()Ljava/lang/String;
/*     */     //   33: astore #9
/*     */     //   35: aload #9
/*     */     //   37: ifnull -> 340
/*     */     //   40: aload_1
/*     */     //   41: invokevirtual nextToken : ()Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   44: astore #11
/*     */     //   46: aload #11
/*     */     //   48: ifnonnull -> 56
/*     */     //   51: getstatic com/fasterxml/jackson/core/JsonToken.NOT_AVAILABLE : Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   54: astore #11
/*     */     //   56: aload #11
/*     */     //   58: invokevirtual id : ()I
/*     */     //   61: tableswitch default -> 292, 1 -> 120, 2 -> 292, 3 -> 174, 4 -> 292, 5 -> 292, 6 -> 225, 7 -> 238, 8 -> 251, 9 -> 263, 10 -> 273, 11 -> 283
/*     */     //   120: aload_3
/*     */     //   121: invokevirtual objectNode : ()Lcom/fasterxml/jackson/databind/node/ObjectNode;
/*     */     //   124: astore #12
/*     */     //   126: aload #8
/*     */     //   128: aload #9
/*     */     //   130: aload #12
/*     */     //   132: invokevirtual replace : (Ljava/lang/String;Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   135: astore #13
/*     */     //   137: aload #13
/*     */     //   139: ifnull -> 157
/*     */     //   142: aload_0
/*     */     //   143: aload_1
/*     */     //   144: aload_2
/*     */     //   145: aload_3
/*     */     //   146: aload #9
/*     */     //   148: aload #8
/*     */     //   150: aload #13
/*     */     //   152: aload #12
/*     */     //   154: invokevirtual _handleDuplicateField : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;Ljava/lang/String;Lcom/fasterxml/jackson/databind/node/ObjectNode;Lcom/fasterxml/jackson/databind/JsonNode;Lcom/fasterxml/jackson/databind/JsonNode;)V
/*     */     //   157: aload #4
/*     */     //   159: aload #6
/*     */     //   161: invokevirtual push : (Lcom/fasterxml/jackson/databind/node/ContainerNode;)V
/*     */     //   164: aload #12
/*     */     //   166: dup
/*     */     //   167: astore #8
/*     */     //   169: astore #6
/*     */     //   171: goto -> 331
/*     */     //   174: aload_3
/*     */     //   175: invokevirtual arrayNode : ()Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   178: astore #12
/*     */     //   180: aload #8
/*     */     //   182: aload #9
/*     */     //   184: aload #12
/*     */     //   186: invokevirtual replace : (Ljava/lang/String;Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   189: astore #13
/*     */     //   191: aload #13
/*     */     //   193: ifnull -> 211
/*     */     //   196: aload_0
/*     */     //   197: aload_1
/*     */     //   198: aload_2
/*     */     //   199: aload_3
/*     */     //   200: aload #9
/*     */     //   202: aload #8
/*     */     //   204: aload #13
/*     */     //   206: aload #12
/*     */     //   208: invokevirtual _handleDuplicateField : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;Ljava/lang/String;Lcom/fasterxml/jackson/databind/node/ObjectNode;Lcom/fasterxml/jackson/databind/JsonNode;Lcom/fasterxml/jackson/databind/JsonNode;)V
/*     */     //   211: aload #4
/*     */     //   213: aload #6
/*     */     //   215: invokevirtual push : (Lcom/fasterxml/jackson/databind/node/ContainerNode;)V
/*     */     //   218: aload #12
/*     */     //   220: astore #6
/*     */     //   222: goto -> 592
/*     */     //   225: aload_3
/*     */     //   226: aload_1
/*     */     //   227: invokevirtual getText : ()Ljava/lang/String;
/*     */     //   230: invokevirtual textNode : (Ljava/lang/String;)Lcom/fasterxml/jackson/databind/node/TextNode;
/*     */     //   233: astore #10
/*     */     //   235: goto -> 300
/*     */     //   238: aload_0
/*     */     //   239: aload_1
/*     */     //   240: iload #7
/*     */     //   242: aload_3
/*     */     //   243: invokevirtual _fromInt : (Lcom/fasterxml/jackson/core/JsonParser;ILcom/fasterxml/jackson/databind/node/JsonNodeFactory;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   246: astore #10
/*     */     //   248: goto -> 300
/*     */     //   251: aload_0
/*     */     //   252: aload_1
/*     */     //   253: aload_2
/*     */     //   254: aload_3
/*     */     //   255: invokevirtual _fromFloat : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   258: astore #10
/*     */     //   260: goto -> 300
/*     */     //   263: aload_3
/*     */     //   264: iconst_1
/*     */     //   265: invokevirtual booleanNode : (Z)Lcom/fasterxml/jackson/databind/node/BooleanNode;
/*     */     //   268: astore #10
/*     */     //   270: goto -> 300
/*     */     //   273: aload_3
/*     */     //   274: iconst_0
/*     */     //   275: invokevirtual booleanNode : (Z)Lcom/fasterxml/jackson/databind/node/BooleanNode;
/*     */     //   278: astore #10
/*     */     //   280: goto -> 300
/*     */     //   283: aload_3
/*     */     //   284: invokevirtual nullNode : ()Lcom/fasterxml/jackson/databind/node/NullNode;
/*     */     //   287: astore #10
/*     */     //   289: goto -> 300
/*     */     //   292: aload_0
/*     */     //   293: aload_1
/*     */     //   294: aload_2
/*     */     //   295: invokevirtual _deserializeRareScalar : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   298: astore #10
/*     */     //   300: aload #8
/*     */     //   302: aload #9
/*     */     //   304: aload #10
/*     */     //   306: invokevirtual replace : (Ljava/lang/String;Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   309: astore #12
/*     */     //   311: aload #12
/*     */     //   313: ifnull -> 331
/*     */     //   316: aload_0
/*     */     //   317: aload_1
/*     */     //   318: aload_2
/*     */     //   319: aload_3
/*     */     //   320: aload #9
/*     */     //   322: aload #8
/*     */     //   324: aload #12
/*     */     //   326: aload #10
/*     */     //   328: invokevirtual _handleDuplicateField : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;Ljava/lang/String;Lcom/fasterxml/jackson/databind/node/ObjectNode;Lcom/fasterxml/jackson/databind/JsonNode;Lcom/fasterxml/jackson/databind/JsonNode;)V
/*     */     //   331: aload_1
/*     */     //   332: invokevirtual nextFieldName : ()Ljava/lang/String;
/*     */     //   335: astore #9
/*     */     //   337: goto -> 35
/*     */     //   340: goto -> 585
/*     */     //   343: aload #6
/*     */     //   345: checkcast com/fasterxml/jackson/databind/node/ArrayNode
/*     */     //   348: astore #8
/*     */     //   350: aload_1
/*     */     //   351: invokevirtual nextToken : ()Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   354: astore #9
/*     */     //   356: aload #9
/*     */     //   358: ifnonnull -> 366
/*     */     //   361: getstatic com/fasterxml/jackson/core/JsonToken.NOT_AVAILABLE : Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   364: astore #9
/*     */     //   366: aload #9
/*     */     //   368: invokevirtual id : ()I
/*     */     //   371: tableswitch default -> 570, 1 -> 428, 2 -> 570, 3 -> 452, 4 -> 476, 5 -> 570, 6 -> 479, 7 -> 496, 8 -> 513, 9 -> 529, 10 -> 543, 11 -> 557
/*     */     //   428: aload #4
/*     */     //   430: aload #6
/*     */     //   432: invokevirtual push : (Lcom/fasterxml/jackson/databind/node/ContainerNode;)V
/*     */     //   435: aload_3
/*     */     //   436: invokevirtual objectNode : ()Lcom/fasterxml/jackson/databind/node/ObjectNode;
/*     */     //   439: astore #6
/*     */     //   441: aload #8
/*     */     //   443: aload #6
/*     */     //   445: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   448: pop
/*     */     //   449: goto -> 592
/*     */     //   452: aload #4
/*     */     //   454: aload #6
/*     */     //   456: invokevirtual push : (Lcom/fasterxml/jackson/databind/node/ContainerNode;)V
/*     */     //   459: aload_3
/*     */     //   460: invokevirtual arrayNode : ()Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   463: astore #6
/*     */     //   465: aload #8
/*     */     //   467: aload #6
/*     */     //   469: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   472: pop
/*     */     //   473: goto -> 592
/*     */     //   476: goto -> 585
/*     */     //   479: aload #8
/*     */     //   481: aload_3
/*     */     //   482: aload_1
/*     */     //   483: invokevirtual getText : ()Ljava/lang/String;
/*     */     //   486: invokevirtual textNode : (Ljava/lang/String;)Lcom/fasterxml/jackson/databind/node/TextNode;
/*     */     //   489: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   492: pop
/*     */     //   493: goto -> 350
/*     */     //   496: aload #8
/*     */     //   498: aload_0
/*     */     //   499: aload_1
/*     */     //   500: iload #7
/*     */     //   502: aload_3
/*     */     //   503: invokevirtual _fromInt : (Lcom/fasterxml/jackson/core/JsonParser;ILcom/fasterxml/jackson/databind/node/JsonNodeFactory;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   506: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   509: pop
/*     */     //   510: goto -> 350
/*     */     //   513: aload #8
/*     */     //   515: aload_0
/*     */     //   516: aload_1
/*     */     //   517: aload_2
/*     */     //   518: aload_3
/*     */     //   519: invokevirtual _fromFloat : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   522: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   525: pop
/*     */     //   526: goto -> 350
/*     */     //   529: aload #8
/*     */     //   531: aload_3
/*     */     //   532: iconst_1
/*     */     //   533: invokevirtual booleanNode : (Z)Lcom/fasterxml/jackson/databind/node/BooleanNode;
/*     */     //   536: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   539: pop
/*     */     //   540: goto -> 350
/*     */     //   543: aload #8
/*     */     //   545: aload_3
/*     */     //   546: iconst_0
/*     */     //   547: invokevirtual booleanNode : (Z)Lcom/fasterxml/jackson/databind/node/BooleanNode;
/*     */     //   550: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   553: pop
/*     */     //   554: goto -> 350
/*     */     //   557: aload #8
/*     */     //   559: aload_3
/*     */     //   560: invokevirtual nullNode : ()Lcom/fasterxml/jackson/databind/node/NullNode;
/*     */     //   563: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   566: pop
/*     */     //   567: goto -> 350
/*     */     //   570: aload #8
/*     */     //   572: aload_0
/*     */     //   573: aload_1
/*     */     //   574: aload_2
/*     */     //   575: invokevirtual _deserializeRareScalar : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;)Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   578: invokevirtual add : (Lcom/fasterxml/jackson/databind/JsonNode;)Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   581: pop
/*     */     //   582: goto -> 350
/*     */     //   585: aload #4
/*     */     //   587: invokevirtual popOrNull : ()Lcom/fasterxml/jackson/databind/node/ContainerNode;
/*     */     //   590: astore #6
/*     */     //   592: aload #6
/*     */     //   594: ifnonnull -> 14
/*     */     //   597: aload #5
/*     */     //   599: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #430	-> 0
/*     */     //   #431	-> 4
/*     */     //   #435	-> 14
/*     */     //   #436	-> 22
/*     */     //   #437	-> 29
/*     */     //   #440	-> 35
/*     */     //   #442	-> 40
/*     */     //   #443	-> 46
/*     */     //   #444	-> 51
/*     */     //   #446	-> 56
/*     */     //   #449	-> 120
/*     */     //   #450	-> 126
/*     */     //   #451	-> 137
/*     */     //   #452	-> 142
/*     */     //   #455	-> 157
/*     */     //   #456	-> 164
/*     */     //   #458	-> 171
/*     */     //   #462	-> 174
/*     */     //   #463	-> 180
/*     */     //   #464	-> 191
/*     */     //   #465	-> 196
/*     */     //   #468	-> 211
/*     */     //   #469	-> 218
/*     */     //   #471	-> 222
/*     */     //   #473	-> 225
/*     */     //   #474	-> 235
/*     */     //   #476	-> 238
/*     */     //   #477	-> 248
/*     */     //   #479	-> 251
/*     */     //   #480	-> 260
/*     */     //   #482	-> 263
/*     */     //   #483	-> 270
/*     */     //   #485	-> 273
/*     */     //   #486	-> 280
/*     */     //   #488	-> 283
/*     */     //   #489	-> 289
/*     */     //   #491	-> 292
/*     */     //   #493	-> 300
/*     */     //   #494	-> 311
/*     */     //   #495	-> 316
/*     */     //   #440	-> 331
/*     */     //   #500	-> 340
/*     */     //   #502	-> 343
/*     */     //   #506	-> 350
/*     */     //   #507	-> 356
/*     */     //   #508	-> 361
/*     */     //   #510	-> 366
/*     */     //   #512	-> 428
/*     */     //   #513	-> 435
/*     */     //   #514	-> 441
/*     */     //   #515	-> 449
/*     */     //   #517	-> 452
/*     */     //   #518	-> 459
/*     */     //   #519	-> 465
/*     */     //   #520	-> 473
/*     */     //   #522	-> 476
/*     */     //   #524	-> 479
/*     */     //   #525	-> 493
/*     */     //   #527	-> 496
/*     */     //   #528	-> 510
/*     */     //   #530	-> 513
/*     */     //   #531	-> 526
/*     */     //   #533	-> 529
/*     */     //   #534	-> 540
/*     */     //   #536	-> 543
/*     */     //   #537	-> 554
/*     */     //   #539	-> 557
/*     */     //   #540	-> 567
/*     */     //   #542	-> 570
/*     */     //   #543	-> 582
/*     */     //   #550	-> 585
/*     */     //   #551	-> 592
/*     */     //   #552	-> 597
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   126	48	12	newOb	Lcom/fasterxml/jackson/databind/node/ObjectNode;
/*     */     //   137	37	13	old	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   180	42	12	newOb	Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   191	31	13	old	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   235	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   248	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   260	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   270	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   280	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   289	3	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   300	31	10	value	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   46	285	11	t	Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   311	20	12	old	Lcom/fasterxml/jackson/databind/JsonNode;
/*     */     //   29	311	8	currObject	Lcom/fasterxml/jackson/databind/node/ObjectNode;
/*     */     //   35	305	9	propName	Ljava/lang/String;
/*     */     //   356	229	9	t	Lcom/fasterxml/jackson/core/JsonToken;
/*     */     //   350	235	8	currArray	Lcom/fasterxml/jackson/databind/node/ArrayNode;
/*     */     //   0	600	0	this	Lcom/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer;
/*     */     //   0	600	1	p	Lcom/fasterxml/jackson/core/JsonParser;
/*     */     //   0	600	2	ctxt	Lcom/fasterxml/jackson/databind/DeserializationContext;
/*     */     //   0	600	3	nodeFactory	Lcom/fasterxml/jackson/databind/node/JsonNodeFactory;
/*     */     //   0	600	4	stack	Lcom/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer$ContainerStack;
/*     */     //   0	600	5	root	Lcom/fasterxml/jackson/databind/node/ContainerNode;
/*     */     //   4	596	6	curr	Lcom/fasterxml/jackson/databind/node/ContainerNode;
/*     */     //   14	586	7	intCoercionFeats	I
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	600	0	this	Lcom/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer<TT;>;
/*     */     //   0	600	5	root	Lcom/fasterxml/jackson/databind/node/ContainerNode<*>;
/*     */     //   4	596	6	curr	Lcom/fasterxml/jackson/databind/node/ContainerNode<*>;
/*     */   }
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
/*     */   protected final JsonNode _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 559 */     JsonNodeFactory nodeF = ctxt.getNodeFactory();
/* 560 */     switch (p.currentTokenId()) {
/*     */       case 2:
/* 562 */         return (JsonNode)nodeF.objectNode();
/*     */       case 6:
/* 564 */         return (JsonNode)nodeF.textNode(p.getText());
/*     */       case 7:
/* 566 */         return _fromInt(p, ctxt, nodeF);
/*     */       case 8:
/* 568 */         return _fromFloat(p, ctxt, nodeF);
/*     */       case 9:
/* 570 */         return (JsonNode)nodeF.booleanNode(true);
/*     */       case 10:
/* 572 */         return (JsonNode)nodeF.booleanNode(false);
/*     */       case 11:
/* 574 */         return (JsonNode)nodeF.nullNode();
/*     */       case 12:
/* 576 */         return _fromEmbedded(p, ctxt);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 581 */     return (JsonNode)ctxt.handleUnexpectedToken(handledType(), p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonNode _deserializeRareScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 588 */     switch (p.currentTokenId()) {
/*     */       case 2:
/* 590 */         return (JsonNode)ctxt.getNodeFactory().objectNode();
/*     */       case 8:
/* 592 */         return _fromFloat(p, ctxt, ctxt.getNodeFactory());
/*     */       case 12:
/* 594 */         return _fromEmbedded(p, ctxt);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 599 */     return (JsonNode)ctxt.handleUnexpectedToken(handledType(), p);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonNode _fromInt(JsonParser p, int coercionFeatures, JsonNodeFactory nodeFactory) throws IOException {
/* 605 */     if (coercionFeatures != 0) {
/* 606 */       if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(coercionFeatures)) {
/* 607 */         return (JsonNode)nodeFactory.numberNode(p.getBigIntegerValue());
/*     */       }
/* 609 */       return (JsonNode)nodeFactory.numberNode(p.getLongValue());
/*     */     } 
/* 611 */     JsonParser.NumberType nt = p.getNumberType();
/* 612 */     if (nt == JsonParser.NumberType.INT) {
/* 613 */       return (JsonNode)nodeFactory.numberNode(p.getIntValue());
/*     */     }
/* 615 */     if (nt == JsonParser.NumberType.LONG) {
/* 616 */       return (JsonNode)nodeFactory.numberNode(p.getLongValue());
/*     */     }
/* 618 */     return (JsonNode)nodeFactory.numberNode(p.getBigIntegerValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
/*     */     JsonParser.NumberType nt;
/* 625 */     int feats = ctxt.getDeserializationFeatures();
/* 626 */     if ((feats & F_MASK_INT_COERCIONS) != 0) {
/* 627 */       if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
/* 628 */         nt = JsonParser.NumberType.BIG_INTEGER;
/* 629 */       } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
/* 630 */         nt = JsonParser.NumberType.LONG;
/*     */       } else {
/* 632 */         nt = p.getNumberType();
/*     */       } 
/*     */     } else {
/* 635 */       nt = p.getNumberType();
/*     */     } 
/* 637 */     if (nt == JsonParser.NumberType.INT) {
/* 638 */       return (JsonNode)nodeFactory.numberNode(p.getIntValue());
/*     */     }
/* 640 */     if (nt == JsonParser.NumberType.LONG) {
/* 641 */       return (JsonNode)nodeFactory.numberNode(p.getLongValue());
/*     */     }
/* 643 */     return (JsonNode)nodeFactory.numberNode(p.getBigIntegerValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
/* 650 */     JsonParser.NumberType nt = p.getNumberType();
/* 651 */     if (nt == JsonParser.NumberType.BIG_DECIMAL) {
/* 652 */       return (JsonNode)nodeFactory.numberNode(p.getDecimalValue());
/*     */     }
/* 654 */     if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*     */ 
/*     */       
/* 657 */       if (p.isNaN()) {
/* 658 */         return (JsonNode)nodeFactory.numberNode(p.getDoubleValue());
/*     */       }
/* 660 */       return (JsonNode)nodeFactory.numberNode(p.getDecimalValue());
/*     */     } 
/* 662 */     if (nt == JsonParser.NumberType.FLOAT) {
/* 663 */       return (JsonNode)nodeFactory.numberNode(p.getFloatValue());
/*     */     }
/* 665 */     return (JsonNode)nodeFactory.numberNode(p.getDoubleValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 671 */     JsonNodeFactory nodeF = ctxt.getNodeFactory();
/* 672 */     Object ob = p.getEmbeddedObject();
/*     */     
/* 674 */     if (ob == null) {
/* 675 */       return (JsonNode)nodeF.nullNode();
/*     */     }
/* 677 */     Class<?> type = ob.getClass();
/* 678 */     if (type == byte[].class) {
/* 679 */       return (JsonNode)nodeF.binaryNode((byte[])ob);
/*     */     }
/*     */     
/* 682 */     if (ob instanceof RawValue) {
/* 683 */       return (JsonNode)nodeF.rawValueNode((RawValue)ob);
/*     */     }
/* 685 */     if (ob instanceof JsonNode)
/*     */     {
/* 687 */       return (JsonNode)ob;
/*     */     }
/*     */     
/* 690 */     return (JsonNode)nodeF.pojoNode(ob);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ContainerStack
/*     */   {
/*     */     private ContainerNode[] _stack;
/*     */ 
/*     */ 
/*     */     
/*     */     private int _top;
/*     */ 
/*     */ 
/*     */     
/*     */     private int _end;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() {
/* 713 */       return this._top;
/*     */     }
/*     */     
/*     */     public void push(ContainerNode node) {
/* 717 */       if (this._top < this._end) {
/* 718 */         this._stack[this._top++] = node;
/*     */         return;
/*     */       } 
/* 721 */       if (this._stack == null) {
/* 722 */         this._end = 10;
/* 723 */         this._stack = new ContainerNode[this._end];
/*     */       } else {
/*     */         
/* 726 */         this._end += Math.min(4000, Math.max(20, this._end >> 1));
/* 727 */         this._stack = Arrays.<ContainerNode>copyOf(this._stack, this._end);
/*     */       } 
/* 729 */       this._stack[this._top++] = node;
/*     */     }
/*     */     
/*     */     public ContainerNode popOrNull() {
/* 733 */       if (this._top == 0) {
/* 734 */         return null;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 739 */       return this._stack[--this._top];
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */