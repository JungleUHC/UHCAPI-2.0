/*     */ package org.springframework.util;
/*     */ 
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TypeUtils
/*     */ {
/*     */   public static boolean isAssignable(Type lhsType, Type rhsType) {
/*  46 */     Assert.notNull(lhsType, "Left-hand side type must not be null");
/*  47 */     Assert.notNull(rhsType, "Right-hand side type must not be null");
/*     */ 
/*     */     
/*  50 */     if (lhsType.equals(rhsType) || Object.class == lhsType) {
/*  51 */       return true;
/*     */     }
/*     */     
/*  54 */     if (lhsType instanceof Class) {
/*  55 */       Class<?> lhsClass = (Class)lhsType;
/*     */ 
/*     */       
/*  58 */       if (rhsType instanceof Class) {
/*  59 */         return ClassUtils.isAssignable(lhsClass, (Class)rhsType);
/*     */       }
/*     */       
/*  62 */       if (rhsType instanceof ParameterizedType) {
/*  63 */         Type rhsRaw = ((ParameterizedType)rhsType).getRawType();
/*     */ 
/*     */         
/*  66 */         if (rhsRaw instanceof Class) {
/*  67 */           return ClassUtils.isAssignable(lhsClass, (Class)rhsRaw);
/*     */         }
/*     */       }
/*  70 */       else if (lhsClass.isArray() && rhsType instanceof GenericArrayType) {
/*  71 */         Type rhsComponent = ((GenericArrayType)rhsType).getGenericComponentType();
/*     */         
/*  73 */         return isAssignable(lhsClass.getComponentType(), rhsComponent);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  78 */     if (lhsType instanceof ParameterizedType) {
/*  79 */       if (rhsType instanceof Class) {
/*  80 */         Type lhsRaw = ((ParameterizedType)lhsType).getRawType();
/*     */         
/*  82 */         if (lhsRaw instanceof Class) {
/*  83 */           return ClassUtils.isAssignable((Class)lhsRaw, (Class)rhsType);
/*     */         }
/*     */       }
/*  86 */       else if (rhsType instanceof ParameterizedType) {
/*  87 */         return isAssignable((ParameterizedType)lhsType, (ParameterizedType)rhsType);
/*     */       } 
/*     */     }
/*     */     
/*  91 */     if (lhsType instanceof GenericArrayType) {
/*  92 */       Type lhsComponent = ((GenericArrayType)lhsType).getGenericComponentType();
/*     */       
/*  94 */       if (rhsType instanceof Class) {
/*  95 */         Class<?> rhsClass = (Class)rhsType;
/*     */         
/*  97 */         if (rhsClass.isArray()) {
/*  98 */           return isAssignable(lhsComponent, rhsClass.getComponentType());
/*     */         }
/*     */       }
/* 101 */       else if (rhsType instanceof GenericArrayType) {
/* 102 */         Type rhsComponent = ((GenericArrayType)rhsType).getGenericComponentType();
/*     */         
/* 104 */         return isAssignable(lhsComponent, rhsComponent);
/*     */       } 
/*     */     } 
/*     */     
/* 108 */     if (lhsType instanceof WildcardType) {
/* 109 */       return isAssignable((WildcardType)lhsType, rhsType);
/*     */     }
/*     */     
/* 112 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isAssignable(ParameterizedType lhsType, ParameterizedType rhsType) {
/* 116 */     if (lhsType.equals(rhsType)) {
/* 117 */       return true;
/*     */     }
/*     */     
/* 120 */     Type[] lhsTypeArguments = lhsType.getActualTypeArguments();
/* 121 */     Type[] rhsTypeArguments = rhsType.getActualTypeArguments();
/*     */     
/* 123 */     if (lhsTypeArguments.length != rhsTypeArguments.length) {
/* 124 */       return false;
/*     */     }
/*     */     
/* 127 */     for (int size = lhsTypeArguments.length, i = 0; i < size; i++) {
/* 128 */       Type lhsArg = lhsTypeArguments[i];
/* 129 */       Type rhsArg = rhsTypeArguments[i];
/*     */       
/* 131 */       if (!lhsArg.equals(rhsArg) && (!(lhsArg instanceof WildcardType) || 
/* 132 */         !isAssignable((WildcardType)lhsArg, rhsArg))) {
/* 133 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 137 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isAssignable(WildcardType lhsType, Type rhsType) {
/* 141 */     Type[] lUpperBounds = lhsType.getUpperBounds();
/*     */ 
/*     */     
/* 144 */     if (lUpperBounds.length == 0) {
/* 145 */       lUpperBounds = new Type[] { Object.class };
/*     */     }
/*     */     
/* 148 */     Type[] lLowerBounds = lhsType.getLowerBounds();
/*     */ 
/*     */     
/* 151 */     if (lLowerBounds.length == 0) {
/* 152 */       lLowerBounds = new Type[] { null };
/*     */     }
/*     */     
/* 155 */     if (rhsType instanceof WildcardType) {
/*     */ 
/*     */ 
/*     */       
/* 159 */       WildcardType rhsWcType = (WildcardType)rhsType;
/* 160 */       Type[] rUpperBounds = rhsWcType.getUpperBounds();
/*     */       
/* 162 */       if (rUpperBounds.length == 0) {
/* 163 */         rUpperBounds = new Type[] { Object.class };
/*     */       }
/*     */       
/* 166 */       Type[] rLowerBounds = rhsWcType.getLowerBounds();
/*     */       
/* 168 */       if (rLowerBounds.length == 0) {
/* 169 */         rLowerBounds = new Type[] { null };
/*     */       }
/*     */       
/* 172 */       for (Type lBound : lUpperBounds) {
/* 173 */         for (Type rBound : rUpperBounds) {
/* 174 */           if (!isAssignableBound(lBound, rBound)) {
/* 175 */             return false;
/*     */           }
/*     */         } 
/*     */         
/* 179 */         for (Type rBound : rLowerBounds) {
/* 180 */           if (!isAssignableBound(lBound, rBound)) {
/* 181 */             return false;
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 186 */       for (Type lBound : lLowerBounds) {
/* 187 */         for (Type rBound : rUpperBounds) {
/* 188 */           if (!isAssignableBound(rBound, lBound)) {
/* 189 */             return false;
/*     */           }
/*     */         } 
/*     */         
/* 193 */         for (Type rBound : rLowerBounds) {
/* 194 */           if (!isAssignableBound(rBound, lBound)) {
/* 195 */             return false;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 201 */       for (Type lBound : lUpperBounds) {
/* 202 */         if (!isAssignableBound(lBound, rhsType)) {
/* 203 */           return false;
/*     */         }
/*     */       } 
/*     */       
/* 207 */       for (Type lBound : lLowerBounds) {
/* 208 */         if (!isAssignableBound(rhsType, lBound)) {
/* 209 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 214 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean isAssignableBound(@Nullable Type lhsType, @Nullable Type rhsType) {
/* 218 */     if (rhsType == null) {
/* 219 */       return true;
/*     */     }
/* 221 */     if (lhsType == null) {
/* 222 */       return false;
/*     */     }
/* 224 */     return isAssignable(lhsType, rhsType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/TypeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */