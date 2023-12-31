/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class NodeCursor
/*     */   extends JsonStreamContext
/*     */ {
/*     */   protected final NodeCursor _parent;
/*     */   protected String _currentName;
/*     */   protected Object _currentValue;
/*     */   
/*     */   public NodeCursor(int contextType, NodeCursor p) {
/*  34 */     this._type = contextType;
/*  35 */     this._index = -1;
/*  36 */     this._parent = p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final NodeCursor getParent() {
/*  47 */     return this._parent;
/*     */   }
/*     */   
/*     */   public final String getCurrentName() {
/*  51 */     return this._currentName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void overrideCurrentName(String name) {
/*  58 */     this._currentName = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/*  63 */     return this._currentValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/*  68 */     this._currentValue = v;
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
/*     */   public final NodeCursor iterateChildren() {
/*  88 */     JsonNode n = currentNode();
/*  89 */     if (n == null) throw new IllegalStateException("No current node"); 
/*  90 */     if (n.isArray()) {
/*  91 */       return new ArrayCursor(n, this);
/*     */     }
/*  93 */     if (n.isObject()) {
/*  94 */       return new ObjectCursor(n, this);
/*     */     }
/*  96 */     throw new IllegalStateException("Current node of type " + n.getClass().getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract JsonToken nextToken();
/*     */ 
/*     */   
/*     */   public abstract JsonNode currentNode();
/*     */ 
/*     */   
/*     */   public abstract NodeCursor startObject();
/*     */   
/*     */   public abstract NodeCursor startArray();
/*     */   
/*     */   protected static final class RootCursor
/*     */     extends NodeCursor
/*     */   {
/*     */     protected JsonNode _node;
/*     */     protected boolean _done = false;
/*     */     
/*     */     public RootCursor(JsonNode n, NodeCursor p) {
/* 117 */       super(0, p);
/* 118 */       this._node = n;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void overrideCurrentName(String name) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonToken nextToken() {
/* 128 */       if (!this._done) {
/* 129 */         this._index++;
/* 130 */         this._done = true;
/* 131 */         return this._node.asToken();
/*     */       } 
/* 133 */       this._node = null;
/* 134 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode currentNode() {
/* 141 */       return this._done ? this._node : null;
/*     */     }
/*     */     
/*     */     public NodeCursor startArray() {
/* 145 */       return new NodeCursor.ArrayCursor(this._node, this);
/*     */     }
/*     */     public NodeCursor startObject() {
/* 148 */       return new NodeCursor.ObjectCursor(this._node, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected static final class ArrayCursor
/*     */     extends NodeCursor
/*     */   {
/*     */     protected Iterator<JsonNode> _contents;
/*     */     protected JsonNode _currentElement;
/*     */     
/*     */     public ArrayCursor(JsonNode n, NodeCursor p) {
/* 160 */       super(1, p);
/* 161 */       this._contents = n.elements();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonToken nextToken() {
/* 167 */       if (!this._contents.hasNext()) {
/* 168 */         this._currentElement = null;
/* 169 */         return JsonToken.END_ARRAY;
/*     */       } 
/* 171 */       this._index++;
/* 172 */       this._currentElement = this._contents.next();
/* 173 */       return this._currentElement.asToken();
/*     */     }
/*     */     
/*     */     public JsonNode currentNode() {
/* 177 */       return this._currentElement;
/*     */     }
/*     */     public NodeCursor startArray() {
/* 180 */       return new ArrayCursor(this._currentElement, this);
/*     */     }
/*     */     public NodeCursor startObject() {
/* 183 */       return new NodeCursor.ObjectCursor(this._currentElement, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected static final class ObjectCursor
/*     */     extends NodeCursor
/*     */   {
/*     */     protected Iterator<Map.Entry<String, JsonNode>> _contents;
/*     */     
/*     */     protected Map.Entry<String, JsonNode> _current;
/*     */     protected boolean _needEntry;
/*     */     
/*     */     public ObjectCursor(JsonNode n, NodeCursor p) {
/* 197 */       super(2, p);
/* 198 */       this._contents = ((ObjectNode)n).fields();
/* 199 */       this._needEntry = true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonToken nextToken() {
/* 206 */       if (this._needEntry) {
/* 207 */         if (!this._contents.hasNext()) {
/* 208 */           this._currentName = null;
/* 209 */           this._current = null;
/* 210 */           return JsonToken.END_OBJECT;
/*     */         } 
/* 212 */         this._index++;
/* 213 */         this._needEntry = false;
/* 214 */         this._current = this._contents.next();
/* 215 */         this._currentName = (this._current == null) ? null : this._current.getKey();
/* 216 */         return JsonToken.FIELD_NAME;
/*     */       } 
/* 218 */       this._needEntry = true;
/* 219 */       return ((JsonNode)this._current.getValue()).asToken();
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode currentNode() {
/* 224 */       return (this._current == null) ? null : this._current.getValue();
/*     */     }
/*     */     
/*     */     public NodeCursor startArray() {
/* 228 */       return new NodeCursor.ArrayCursor(currentNode(), this);
/*     */     }
/*     */     public NodeCursor startObject() {
/* 231 */       return new ObjectCursor(currentNode(), this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/NodeCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */