/*      */ package com.fasterxml.jackson.databind.node;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonPointer;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.TreeNode;
/*      */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*      */ import com.fasterxml.jackson.databind.JsonNode;
/*      */ import com.fasterxml.jackson.databind.SerializerProvider;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.util.RawValue;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ArrayNode
/*      */   extends ContainerNode<ArrayNode>
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   private final List<JsonNode> _children;
/*      */   
/*      */   public ArrayNode(JsonNodeFactory nf) {
/*   33 */     super(nf);
/*   34 */     this._children = new ArrayList<>();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode(JsonNodeFactory nf, int capacity) {
/*   41 */     super(nf);
/*   42 */     this._children = new ArrayList<>(capacity);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
/*   49 */     super(nf);
/*   50 */     this._children = children;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonNode _at(JsonPointer ptr) {
/*   55 */     return get(ptr.getMatchingIndex());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode deepCopy() {
/*   63 */     ArrayNode ret = new ArrayNode(this._nodeFactory);
/*      */     
/*   65 */     for (JsonNode element : this._children) {
/*   66 */       ret._children.add(element.deepCopy());
/*      */     }
/*   68 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty(SerializerProvider serializers) {
/*   79 */     return this._children.isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNodeType getNodeType() {
/*   90 */     return JsonNodeType.ARRAY;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isArray() {
/*   95 */     return true;
/*      */   }
/*      */   public JsonToken asToken() {
/*   98 */     return JsonToken.START_ARRAY;
/*      */   }
/*      */   
/*      */   public int size() {
/*  102 */     return this._children.size();
/*      */   }
/*      */   
/*      */   public boolean isEmpty() {
/*  106 */     return this._children.isEmpty();
/*      */   }
/*      */   
/*      */   public Iterator<JsonNode> elements() {
/*  110 */     return this._children.iterator();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode get(int index) {
/*  115 */     if (index >= 0 && index < this._children.size()) {
/*  116 */       return this._children.get(index);
/*      */     }
/*  118 */     return null;
/*      */   }
/*      */   
/*      */   public JsonNode get(String fieldName) {
/*  122 */     return null;
/*      */   }
/*      */   public JsonNode path(String fieldName) {
/*  125 */     return MissingNode.getInstance();
/*      */   }
/*      */   
/*      */   public JsonNode path(int index) {
/*  129 */     if (index >= 0 && index < this._children.size()) {
/*  130 */       return this._children.get(index);
/*      */     }
/*  132 */     return MissingNode.getInstance();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode required(int index) {
/*  137 */     if (index >= 0 && index < this._children.size()) {
/*  138 */       return this._children.get(index);
/*      */     }
/*  140 */     return (JsonNode)_reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`", new Object[] {
/*  141 */           Integer.valueOf(index), Integer.valueOf(this._children.size())
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
/*  147 */     if (!(o instanceof ArrayNode)) {
/*  148 */       return false;
/*      */     }
/*  150 */     ArrayNode other = (ArrayNode)o;
/*  151 */     int len = this._children.size();
/*  152 */     if (other.size() != len) {
/*  153 */       return false;
/*      */     }
/*  155 */     List<JsonNode> l1 = this._children;
/*  156 */     List<JsonNode> l2 = other._children;
/*  157 */     for (int i = 0; i < len; i++) {
/*  158 */       if (!((JsonNode)l1.get(i)).equals(comparator, l2.get(i))) {
/*  159 */         return false;
/*      */       }
/*      */     } 
/*  162 */     return true;
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
/*      */   public void serialize(JsonGenerator f, SerializerProvider provider) throws IOException {
/*  174 */     List<JsonNode> c = this._children;
/*  175 */     int size = c.size();
/*  176 */     f.writeStartArray(this, size);
/*  177 */     for (int i = 0; i < size; i++) {
/*      */       
/*  179 */       JsonNode n = c.get(i);
/*  180 */       ((BaseJsonNode)n).serialize(f, provider);
/*      */     } 
/*  182 */     f.writeEndArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*  189 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/*  190 */         .typeId(this, JsonToken.START_ARRAY));
/*  191 */     for (JsonNode n : this._children) {
/*  192 */       ((BaseJsonNode)n).serialize(g, provider);
/*      */     }
/*  194 */     typeSer.writeTypeSuffix(g, typeIdDef);
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
/*      */   public JsonNode findValue(String fieldName) {
/*  206 */     for (JsonNode node : this._children) {
/*  207 */       JsonNode value = node.findValue(fieldName);
/*  208 */       if (value != null) {
/*  209 */         return value;
/*      */       }
/*      */     } 
/*  212 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
/*  218 */     for (JsonNode node : this._children) {
/*  219 */       foundSoFar = node.findValues(fieldName, foundSoFar);
/*      */     }
/*  221 */     return foundSoFar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
/*  227 */     for (JsonNode node : this._children) {
/*  228 */       foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
/*      */     }
/*  230 */     return foundSoFar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectNode findParent(String fieldName) {
/*  236 */     for (JsonNode node : this._children) {
/*  237 */       JsonNode parent = node.findParent(fieldName);
/*  238 */       if (parent != null) {
/*  239 */         return (ObjectNode)parent;
/*      */       }
/*      */     } 
/*  242 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
/*  248 */     for (JsonNode node : this._children) {
/*  249 */       foundSoFar = node.findParents(fieldName, foundSoFar);
/*      */     }
/*  251 */     return foundSoFar;
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
/*      */   public JsonNode set(int index, JsonNode value) {
/*  273 */     if (value == null) {
/*  274 */       value = nullNode();
/*      */     }
/*  276 */     if (index < 0 || index >= this._children.size()) {
/*  277 */       throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
/*      */     }
/*  279 */     return this._children.set(index, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(JsonNode value) {
/*  289 */     if (value == null) {
/*  290 */       value = nullNode();
/*      */     }
/*  292 */     _add(value);
/*  293 */     return this;
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
/*      */   public ArrayNode addAll(ArrayNode other) {
/*  306 */     this._children.addAll(other._children);
/*  307 */     return this;
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
/*      */   public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
/*  319 */     for (JsonNode node : nodes) {
/*  320 */       add(node);
/*      */     }
/*  322 */     return this;
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
/*      */   public ArrayNode insert(int index, JsonNode value) {
/*  336 */     if (value == null) {
/*  337 */       value = nullNode();
/*      */     }
/*  339 */     _insert(index, value);
/*  340 */     return this;
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
/*      */   public JsonNode remove(int index) {
/*  352 */     if (index >= 0 && index < this._children.size()) {
/*  353 */       return this._children.remove(index);
/*      */     }
/*  355 */     return null;
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
/*      */   public ArrayNode removeAll() {
/*  367 */     this._children.clear();
/*  368 */     return this;
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
/*      */   public ArrayNode addArray() {
/*  385 */     ArrayNode n = arrayNode();
/*  386 */     _add(n);
/*  387 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectNode addObject() {
/*  398 */     ObjectNode n = objectNode();
/*  399 */     _add(n);
/*  400 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode addPOJO(Object pojo) {
/*  410 */     return _add((pojo == null) ? nullNode() : pojoNode(pojo));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode addRawValue(RawValue raw) {
/*  419 */     return _add((raw == null) ? nullNode() : rawValueNode(raw));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode addNull() {
/*  428 */     return _add(nullNode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(short v) {
/*  439 */     return _add(numberNode(v));
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
/*      */   public ArrayNode add(Short v) {
/*  451 */     return _add((v == null) ? nullNode() : numberNode(v.shortValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(int v) {
/*  460 */     return _add(numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(Integer v) {
/*  470 */     return _add((v == null) ? nullNode() : numberNode(v.intValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(long v) {
/*  478 */     return _add(numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(Long v) {
/*  487 */     return _add((v == null) ? nullNode() : numberNode(v.longValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(float v) {
/*  496 */     return _add(numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(Float v) {
/*  506 */     return _add((v == null) ? nullNode() : numberNode(v.floatValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(double v) {
/*  515 */     return _add(numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(Double v) {
/*  525 */     return _add((v == null) ? nullNode() : numberNode(v.doubleValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(BigDecimal v) {
/*  534 */     return _add((v == null) ? nullNode() : numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(BigInteger v) {
/*  545 */     return _add((v == null) ? nullNode() : numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(String v) {
/*  554 */     return _add((v == null) ? nullNode() : textNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(boolean v) {
/*  563 */     return _add(booleanNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(Boolean v) {
/*  573 */     return _add((v == null) ? nullNode() : booleanNode(v.booleanValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode add(byte[] v) {
/*  583 */     return _add((v == null) ? nullNode() : binaryNode(v));
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
/*      */   public ArrayNode insertArray(int index) {
/*  596 */     ArrayNode n = arrayNode();
/*  597 */     _insert(index, n);
/*  598 */     return n;
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
/*      */   public ObjectNode insertObject(int index) {
/*  610 */     ObjectNode n = objectNode();
/*  611 */     _insert(index, n);
/*  612 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insertNull(int index) {
/*  622 */     return _insert(index, nullNode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insertPOJO(int index, Object pojo) {
/*  632 */     return _insert(index, (pojo == null) ? nullNode() : pojoNode(pojo));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insertRawValue(int index, RawValue raw) {
/*  641 */     return _insert(index, (raw == null) ? nullNode() : rawValueNode(raw));
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
/*      */   public ArrayNode insert(int index, short v) {
/*  653 */     return _insert(index, numberNode(v));
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
/*      */   public ArrayNode insert(int index, Short value) {
/*  665 */     return _insert(index, (value == null) ? nullNode() : numberNode(value.shortValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, int v) {
/*  675 */     return _insert(index, numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, Integer v) {
/*  685 */     return _insert(index, (v == null) ? nullNode() : numberNode(v.intValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, long v) {
/*  695 */     return _insert(index, numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, Long v) {
/*  705 */     return _insert(index, (v == null) ? nullNode() : numberNode(v.longValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, float v) {
/*  715 */     return _insert(index, numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, Float v) {
/*  725 */     return _insert(index, (v == null) ? nullNode() : numberNode(v.floatValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, double v) {
/*  735 */     return _insert(index, numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, Double v) {
/*  745 */     return _insert(index, (v == null) ? nullNode() : numberNode(v.doubleValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, BigDecimal v) {
/*  755 */     return _insert(index, (v == null) ? nullNode() : numberNode(v));
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
/*      */   public ArrayNode insert(int index, BigInteger v) {
/*  767 */     return _insert(index, (v == null) ? nullNode() : numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, String v) {
/*  777 */     return _insert(index, (v == null) ? nullNode() : textNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, boolean v) {
/*  787 */     return _insert(index, booleanNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, Boolean value) {
/*  797 */     if (value == null) {
/*  798 */       return insertNull(index);
/*      */     }
/*  800 */     return _insert(index, booleanNode(value.booleanValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode insert(int index, byte[] v) {
/*  811 */     if (v == null) {
/*  812 */       return insertNull(index);
/*      */     }
/*  814 */     return _insert(index, binaryNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode setNull(int index) {
/*  823 */     return _set(index, nullNode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode setPOJO(int index, Object pojo) {
/*  832 */     return _set(index, (pojo == null) ? nullNode() : pojoNode(pojo));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode setRawValue(int index, RawValue raw) {
/*  841 */     return _set(index, (raw == null) ? nullNode() : rawValueNode(raw));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, short v) {
/*  850 */     return _set(index, numberNode(v));
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
/*      */   public ArrayNode set(int index, Short v) {
/*  862 */     return _set(index, (v == null) ? nullNode() : numberNode(v.shortValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, int v) {
/*  871 */     return _set(index, numberNode(v));
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
/*      */   public ArrayNode set(int index, Integer v) {
/*  883 */     return _set(index, (v == null) ? nullNode() : numberNode(v.intValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, long v) {
/*  892 */     return _set(index, numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, Long v) {
/*  901 */     return _set(index, (v == null) ? nullNode() : numberNode(v.longValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, float v) {
/*  912 */     return _set(index, numberNode(v));
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
/*      */   public ArrayNode set(int index, Float v) {
/*  924 */     return _set(index, (v == null) ? nullNode() : numberNode(v.floatValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, double v) {
/*  935 */     return _set(index, numberNode(v));
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
/*      */   public ArrayNode set(int index, Double v) {
/*  947 */     return _set(index, (v == null) ? nullNode() : numberNode(v.doubleValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, BigDecimal v) {
/*  958 */     return _set(index, (v == null) ? nullNode() : numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, BigInteger v) {
/*  969 */     return _set(index, (v == null) ? nullNode() : numberNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, String v) {
/*  980 */     return _set(index, (v == null) ? nullNode() : textNode(v));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, boolean v) {
/*  991 */     return _set(index, booleanNode(v));
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
/*      */   public ArrayNode set(int index, Boolean v) {
/* 1003 */     return _set(index, (v == null) ? nullNode() : booleanNode(v.booleanValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayNode set(int index, byte[] v) {
/* 1014 */     return _set(index, (v == null) ? nullNode() : binaryNode(v));
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
/*      */   public boolean equals(Object o) {
/* 1026 */     if (o == this) return true; 
/* 1027 */     if (o == null) return false; 
/* 1028 */     if (o instanceof ArrayNode) {
/* 1029 */       return this._children.equals(((ArrayNode)o)._children);
/*      */     }
/* 1031 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _childrenEqual(ArrayNode other) {
/* 1038 */     return this._children.equals(other._children);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1043 */     return this._children.hashCode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ArrayNode _set(int index, JsonNode node) {
/* 1053 */     if (index < 0 || index >= this._children.size()) {
/* 1054 */       throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
/*      */     }
/* 1056 */     this._children.set(index, node);
/* 1057 */     return this;
/*      */   }
/*      */   
/*      */   protected ArrayNode _add(JsonNode node) {
/* 1061 */     this._children.add(node);
/* 1062 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected ArrayNode _insert(int index, JsonNode node) {
/* 1067 */     if (index < 0) {
/* 1068 */       this._children.add(0, node);
/* 1069 */     } else if (index >= this._children.size()) {
/* 1070 */       this._children.add(node);
/*      */     } else {
/* 1072 */       this._children.add(index, node);
/*      */     } 
/* 1074 */     return this;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/ArrayNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */