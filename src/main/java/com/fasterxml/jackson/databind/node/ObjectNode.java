/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonPointer;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.util.RawValue;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ObjectNode
/*     */   extends ContainerNode<ObjectNode> implements Serializable {
/*     */   public ObjectNode(JsonNodeFactory nc) {
/*  29 */     super(nc);
/*  30 */     this._children = new LinkedHashMap<>();
/*     */   }
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Map<String, JsonNode> _children;
/*     */   
/*     */   public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids) {
/*  37 */     super(nc);
/*  38 */     this._children = kids;
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonNode _at(JsonPointer ptr) {
/*  43 */     return get(ptr.getMatchingProperty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode deepCopy() {
/*  54 */     ObjectNode ret = new ObjectNode(this._nodeFactory);
/*     */     
/*  56 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/*  57 */       ret._children.put(entry.getKey(), ((JsonNode)entry.getValue()).deepCopy());
/*     */     }
/*  59 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider serializers) {
/*  70 */     return this._children.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNodeType getNodeType() {
/*  81 */     return JsonNodeType.OBJECT;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isObject() {
/*  86 */     return true;
/*     */   }
/*     */   public JsonToken asToken() {
/*  89 */     return JsonToken.START_OBJECT;
/*     */   }
/*     */   
/*     */   public int size() {
/*  93 */     return this._children.size();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  97 */     return this._children.isEmpty();
/*     */   }
/*     */   
/*     */   public Iterator<JsonNode> elements() {
/* 101 */     return this._children.values().iterator();
/*     */   }
/*     */   
/*     */   public JsonNode get(int index) {
/* 105 */     return null;
/*     */   }
/*     */   
/*     */   public JsonNode get(String propertyName) {
/* 109 */     return this._children.get(propertyName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> fieldNames() {
/* 114 */     return this._children.keySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNode path(int index) {
/* 119 */     return MissingNode.getInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode path(String propertyName) {
/* 125 */     JsonNode n = this._children.get(propertyName);
/* 126 */     if (n != null) {
/* 127 */       return n;
/*     */     }
/* 129 */     return MissingNode.getInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNode required(String propertyName) {
/* 134 */     JsonNode n = this._children.get(propertyName);
/* 135 */     if (n != null) {
/* 136 */       return n;
/*     */     }
/* 138 */     return (JsonNode)_reportRequiredViolation("No value for property '%s' of `ObjectNode`", new Object[] { propertyName });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<String, JsonNode>> fields() {
/* 147 */     return this._children.entrySet().iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode with(String propertyName) {
/* 153 */     JsonNode n = this._children.get(propertyName);
/* 154 */     if (n != null) {
/* 155 */       if (n instanceof ObjectNode) {
/* 156 */         return (ObjectNode)n;
/*     */       }
/* 158 */       throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n
/*     */           
/* 160 */           .getClass().getName() + ")");
/*     */     } 
/* 162 */     ObjectNode result = objectNode();
/* 163 */     this._children.put(propertyName, result);
/* 164 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayNode withArray(String propertyName) {
/* 171 */     JsonNode n = this._children.get(propertyName);
/* 172 */     if (n != null) {
/* 173 */       if (n instanceof ArrayNode) {
/* 174 */         return (ArrayNode)n;
/*     */       }
/* 176 */       throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ArrayNode (but " + n
/*     */           
/* 178 */           .getClass().getName() + ")");
/*     */     } 
/* 180 */     ArrayNode result = arrayNode();
/* 181 */     this._children.put(propertyName, result);
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
/* 188 */     if (!(o instanceof ObjectNode)) {
/* 189 */       return false;
/*     */     }
/* 191 */     ObjectNode other = (ObjectNode)o;
/* 192 */     Map<String, JsonNode> m1 = this._children;
/* 193 */     Map<String, JsonNode> m2 = other._children;
/*     */     
/* 195 */     int len = m1.size();
/* 196 */     if (m2.size() != len) {
/* 197 */       return false;
/*     */     }
/*     */     
/* 200 */     for (Map.Entry<String, JsonNode> entry : m1.entrySet()) {
/* 201 */       JsonNode v2 = m2.get(entry.getKey());
/* 202 */       if (v2 == null || !((JsonNode)entry.getValue()).equals(comparator, v2)) {
/* 203 */         return false;
/*     */       }
/*     */     } 
/* 206 */     return true;
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
/*     */   public JsonNode findValue(String propertyName) {
/* 218 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/* 219 */       if (propertyName.equals(entry.getKey())) {
/* 220 */         return entry.getValue();
/*     */       }
/* 222 */       JsonNode value = ((JsonNode)entry.getValue()).findValue(propertyName);
/* 223 */       if (value != null) {
/* 224 */         return value;
/*     */       }
/*     */     } 
/* 227 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<JsonNode> findValues(String propertyName, List<JsonNode> foundSoFar) {
/* 233 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/* 234 */       if (propertyName.equals(entry.getKey())) {
/* 235 */         if (foundSoFar == null) {
/* 236 */           foundSoFar = new ArrayList<>();
/*     */         }
/* 238 */         foundSoFar.add(entry.getValue()); continue;
/*     */       } 
/* 240 */       foundSoFar = ((JsonNode)entry.getValue()).findValues(propertyName, foundSoFar);
/*     */     } 
/*     */     
/* 243 */     return foundSoFar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> findValuesAsText(String propertyName, List<String> foundSoFar) {
/* 249 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/* 250 */       if (propertyName.equals(entry.getKey())) {
/* 251 */         if (foundSoFar == null) {
/* 252 */           foundSoFar = new ArrayList<>();
/*     */         }
/* 254 */         foundSoFar.add(((JsonNode)entry.getValue()).asText()); continue;
/*     */       } 
/* 256 */       foundSoFar = ((JsonNode)entry.getValue()).findValuesAsText(propertyName, foundSoFar);
/*     */     } 
/*     */ 
/*     */     
/* 260 */     return foundSoFar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode findParent(String propertyName) {
/* 266 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/* 267 */       if (propertyName.equals(entry.getKey())) {
/* 268 */         return this;
/*     */       }
/* 270 */       JsonNode value = ((JsonNode)entry.getValue()).findParent(propertyName);
/* 271 */       if (value != null) {
/* 272 */         return (ObjectNode)value;
/*     */       }
/*     */     } 
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<JsonNode> findParents(String propertyName, List<JsonNode> foundSoFar) {
/* 281 */     for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
/* 282 */       if (propertyName.equals(entry.getKey())) {
/* 283 */         if (foundSoFar == null) {
/* 284 */           foundSoFar = new ArrayList<>();
/*     */         }
/* 286 */         foundSoFar.add(this);
/*     */         continue;
/*     */       } 
/* 289 */       foundSoFar = ((JsonNode)entry.getValue()).findParents(propertyName, foundSoFar);
/*     */     } 
/*     */     
/* 292 */     return foundSoFar;
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
/*     */   public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/* 311 */     boolean trimEmptyArray = (provider != null && !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
/* 312 */     g.writeStartObject(this);
/* 313 */     for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 319 */       BaseJsonNode value = (BaseJsonNode)en.getValue();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 324 */       if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
/*     */         continue;
/*     */       }
/* 327 */       g.writeFieldName(en.getKey());
/* 328 */       value.serialize(g, provider);
/*     */     } 
/* 330 */     g.writeEndObject();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 340 */     boolean trimEmptyArray = (provider != null && !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
/*     */     
/* 342 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 343 */         .typeId(this, JsonToken.START_OBJECT));
/* 344 */     for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
/* 345 */       BaseJsonNode value = (BaseJsonNode)en.getValue();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 350 */       if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
/*     */         continue;
/*     */       }
/*     */       
/* 354 */       g.writeFieldName(en.getKey());
/* 355 */       value.serialize(g, provider);
/*     */     } 
/* 357 */     typeSer.writeTypeSuffix(g, typeIdDef);
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
/*     */   public <T extends JsonNode> T set(String propertyName, JsonNode value) {
/* 388 */     if (value == null) {
/* 389 */       value = nullNode();
/*     */     }
/* 391 */     this._children.put(propertyName, value);
/* 392 */     return (T)this;
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
/*     */   public <T extends JsonNode> T setAll(Map<String, ? extends JsonNode> properties) {
/* 410 */     for (Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
/* 411 */       JsonNode n = en.getValue();
/* 412 */       if (n == null) {
/* 413 */         n = nullNode();
/*     */       }
/* 415 */       this._children.put(en.getKey(), n);
/*     */     } 
/* 417 */     return (T)this;
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
/*     */   public <T extends JsonNode> T setAll(ObjectNode other) {
/* 435 */     this._children.putAll(other._children);
/* 436 */     return (T)this;
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
/*     */   public JsonNode replace(String propertyName, JsonNode value) {
/* 453 */     if (value == null) {
/* 454 */       value = nullNode();
/*     */     }
/* 456 */     return this._children.put(propertyName, value);
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
/*     */   public <T extends JsonNode> T without(String propertyName) {
/* 472 */     this._children.remove(propertyName);
/* 473 */     return (T)this;
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
/*     */   public <T extends JsonNode> T without(Collection<String> propertyNames) {
/* 491 */     this._children.keySet().removeAll(propertyNames);
/* 492 */     return (T)this;
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
/*     */   @Deprecated
/*     */   public JsonNode put(String propertyName, JsonNode value) {
/* 517 */     if (value == null) {
/* 518 */       value = nullNode();
/*     */     }
/* 520 */     return this._children.put(propertyName, value);
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
/*     */   public JsonNode putIfAbsent(String propertyName, JsonNode value) {
/* 548 */     if (value == null) {
/* 549 */       value = nullNode();
/*     */     }
/* 551 */     return this._children.putIfAbsent(propertyName, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode remove(String propertyName) {
/* 562 */     return this._children.remove(propertyName);
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
/*     */   public ObjectNode remove(Collection<String> propertyNames) {
/* 575 */     this._children.keySet().removeAll(propertyNames);
/* 576 */     return this;
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
/*     */   public ObjectNode removeAll() {
/* 588 */     this._children.clear();
/* 589 */     return this;
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
/*     */   @Deprecated
/*     */   public JsonNode putAll(Map<String, ? extends JsonNode> properties) {
/* 604 */     return setAll(properties);
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
/*     */   @Deprecated
/*     */   public JsonNode putAll(ObjectNode other) {
/* 619 */     return setAll(other);
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
/*     */   public ObjectNode retain(Collection<String> propertyNames) {
/* 632 */     this._children.keySet().retainAll(propertyNames);
/* 633 */     return this;
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
/*     */   public ObjectNode retain(String... propertyNames) {
/* 645 */     return retain(Arrays.asList(propertyNames));
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
/*     */   public ArrayNode putArray(String propertyName) {
/* 667 */     ArrayNode n = arrayNode();
/* 668 */     _put(propertyName, n);
/* 669 */     return n;
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
/*     */   public ObjectNode putObject(String propertyName) {
/* 685 */     ObjectNode n = objectNode();
/* 686 */     _put(propertyName, n);
/* 687 */     return n;
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
/*     */   public ObjectNode putPOJO(String propertyName, Object pojo) {
/* 709 */     return _put(propertyName, pojoNode(pojo));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode putRawValue(String propertyName, RawValue raw) {
/* 716 */     return _put(propertyName, rawValueNode(raw));
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
/*     */   public ObjectNode putNull(String propertyName) {
/* 728 */     this._children.put(propertyName, nullNode());
/* 729 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String propertyName, short v) {
/* 738 */     return _put(propertyName, numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, Short v) {
/* 748 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 749 */         numberNode(v.shortValue()));
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
/*     */   public ObjectNode put(String fieldName, int v) {
/* 762 */     return _put(fieldName, numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, Integer v) {
/* 772 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 773 */         numberNode(v.intValue()));
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
/*     */   public ObjectNode put(String fieldName, long v) {
/* 786 */     return _put(fieldName, numberNode(v));
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
/*     */   public ObjectNode put(String fieldName, Long v) {
/* 802 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 803 */         numberNode(v.longValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, float v) {
/* 812 */     return _put(fieldName, numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, Float v) {
/* 822 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 823 */         numberNode(v.floatValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, double v) {
/* 832 */     return _put(fieldName, numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, Double v) {
/* 842 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 843 */         numberNode(v.doubleValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, BigDecimal v) {
/* 852 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 853 */         numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, BigInteger v) {
/* 864 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 865 */         numberNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, String v) {
/* 874 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 875 */         textNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, boolean v) {
/* 884 */     return _put(fieldName, booleanNode(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, Boolean v) {
/* 894 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 895 */         booleanNode(v.booleanValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode put(String fieldName, byte[] v) {
/* 904 */     return _put(fieldName, (v == null) ? nullNode() : 
/* 905 */         binaryNode(v));
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
/*     */   public boolean equals(Object o) {
/* 917 */     if (o == this) return true; 
/* 918 */     if (o == null) return false; 
/* 919 */     if (o instanceof ObjectNode) {
/* 920 */       return _childrenEqual((ObjectNode)o);
/*     */     }
/* 922 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _childrenEqual(ObjectNode other) {
/* 930 */     return this._children.equals(other._children);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 936 */     return this._children.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectNode _put(String fieldName, JsonNode value) {
/* 947 */     this._children.put(fieldName, value);
/* 948 */     return this;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/ObjectNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */