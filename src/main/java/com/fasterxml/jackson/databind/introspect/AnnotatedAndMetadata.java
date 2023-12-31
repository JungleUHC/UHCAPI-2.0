/*    */ package com.fasterxml.jackson.databind.introspect;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AnnotatedAndMetadata<A extends Annotated, M>
/*    */ {
/*    */   public final A annotated;
/*    */   public final M metadata;
/*    */   
/*    */   public AnnotatedAndMetadata(A ann, M md) {
/* 15 */     this.annotated = ann;
/* 16 */     this.metadata = md;
/*    */   }
/*    */   
/*    */   public static <A extends Annotated, M> AnnotatedAndMetadata<A, M> of(A ann, M md) {
/* 20 */     return new AnnotatedAndMetadata<>(ann, md);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedAndMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */