/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.type.TypeBindings;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MethodGenericTypeResolver
/*     */ {
/*     */   public static TypeResolutionContext narrowMethodTypeParameters(Method candidate, JavaType requestedType, TypeFactory typeFactory, TypeResolutionContext emptyTypeResCtxt) {
/*  42 */     TypeBindings newTypeBindings = bindMethodTypeParameters(candidate, requestedType, emptyTypeResCtxt);
/*  43 */     return (newTypeBindings == null) ? emptyTypeResCtxt : new TypeResolutionContext.Basic(typeFactory, newTypeBindings);
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
/*     */   static TypeBindings bindMethodTypeParameters(Method candidate, JavaType requestedType, TypeResolutionContext emptyTypeResCtxt) {
/*  56 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])candidate.getTypeParameters();
/*  57 */     if (arrayOfTypeVariable.length == 0 || requestedType
/*     */       
/*  59 */       .getBindings().isEmpty())
/*     */     {
/*  61 */       return null;
/*     */     }
/*  63 */     Type genericReturnType = candidate.getGenericReturnType();
/*  64 */     if (!(genericReturnType instanceof ParameterizedType))
/*     */     {
/*     */       
/*  67 */       return null;
/*     */     }
/*     */     
/*  70 */     ParameterizedType parameterizedGenericReturnType = (ParameterizedType)genericReturnType;
/*     */ 
/*     */     
/*  73 */     if (!Objects.equals(requestedType.getRawClass(), parameterizedGenericReturnType.getRawType())) {
/*  74 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     Type[] methodReturnTypeArguments = parameterizedGenericReturnType.getActualTypeArguments();
/*  82 */     ArrayList<String> names = new ArrayList<>(arrayOfTypeVariable.length);
/*  83 */     ArrayList<JavaType> types = new ArrayList<>(arrayOfTypeVariable.length);
/*  84 */     for (int i = 0; i < methodReturnTypeArguments.length; i++) {
/*  85 */       Type methodReturnTypeArgument = methodReturnTypeArguments[i];
/*     */ 
/*     */       
/*  88 */       TypeVariable<?> typeVar = maybeGetTypeVariable(methodReturnTypeArgument);
/*  89 */       if (typeVar != null) {
/*  90 */         String typeParameterName = typeVar.getName();
/*  91 */         if (typeParameterName == null) {
/*  92 */           return null;
/*     */         }
/*     */         
/*  95 */         JavaType bindTarget = requestedType.getBindings().getBoundType(i);
/*  96 */         if (bindTarget == null) {
/*  97 */           return null;
/*     */         }
/*     */ 
/*     */         
/* 101 */         TypeVariable<?> methodTypeVariable = findByName((TypeVariable<?>[])arrayOfTypeVariable, typeParameterName);
/* 102 */         if (methodTypeVariable == null) {
/* 103 */           return null;
/*     */         }
/* 105 */         if (pessimisticallyValidateBounds(emptyTypeResCtxt, bindTarget, methodTypeVariable.getBounds())) {
/*     */           
/* 107 */           int existingIndex = names.indexOf(typeParameterName);
/* 108 */           if (existingIndex != -1) {
/* 109 */             JavaType existingBindTarget = types.get(existingIndex);
/* 110 */             if (!bindTarget.equals(existingBindTarget)) {
/*     */ 
/*     */               
/* 113 */               boolean existingIsSubtype = existingBindTarget.isTypeOrSubTypeOf(bindTarget.getRawClass());
/* 114 */               boolean newIsSubtype = bindTarget.isTypeOrSubTypeOf(existingBindTarget.getRawClass());
/* 115 */               if (!existingIsSubtype && !newIsSubtype)
/*     */               {
/* 117 */                 return null;
/*     */               }
/* 119 */               if (existingIsSubtype ^ newIsSubtype && newIsSubtype)
/*     */               {
/* 121 */                 types.set(existingIndex, bindTarget); } 
/*     */             } 
/*     */           } else {
/* 124 */             names.add(typeParameterName);
/* 125 */             types.add(bindTarget);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 131 */     if (names.isEmpty()) {
/* 132 */       return null;
/*     */     }
/* 134 */     return TypeBindings.create(names, types);
/*     */   }
/*     */ 
/*     */   
/*     */   private static TypeVariable<?> maybeGetTypeVariable(Type type) {
/* 139 */     if (type instanceof TypeVariable) {
/* 140 */       return (TypeVariable)type;
/*     */     }
/*     */     
/* 143 */     if (type instanceof WildcardType) {
/* 144 */       WildcardType wildcardType = (WildcardType)type;
/*     */       
/* 146 */       if ((wildcardType.getLowerBounds()).length != 0) {
/* 147 */         return null;
/*     */       }
/* 149 */       Type[] upperBounds = wildcardType.getUpperBounds();
/* 150 */       if (upperBounds.length == 1) {
/* 151 */         return maybeGetTypeVariable(upperBounds[0]);
/*     */       }
/*     */     } 
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static ParameterizedType maybeGetParameterizedType(Type type) {
/* 159 */     if (type instanceof ParameterizedType) {
/* 160 */       return (ParameterizedType)type;
/*     */     }
/*     */     
/* 163 */     if (type instanceof WildcardType) {
/* 164 */       WildcardType wildcardType = (WildcardType)type;
/*     */       
/* 166 */       if ((wildcardType.getLowerBounds()).length != 0) {
/* 167 */         return null;
/*     */       }
/* 169 */       Type[] upperBounds = wildcardType.getUpperBounds();
/* 170 */       if (upperBounds.length == 1) {
/* 171 */         return maybeGetParameterizedType(upperBounds[0]);
/*     */       }
/*     */     } 
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean pessimisticallyValidateBounds(TypeResolutionContext context, JavaType boundType, Type[] upperBound) {
/* 179 */     for (Type type : upperBound) {
/* 180 */       if (!pessimisticallyValidateBound(context, boundType, type)) {
/* 181 */         return false;
/*     */       }
/*     */     } 
/* 184 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean pessimisticallyValidateBound(TypeResolutionContext context, JavaType boundType, Type type) {
/* 189 */     if (!boundType.isTypeOrSubTypeOf(context.resolveType(type).getRawClass())) {
/* 190 */       return false;
/*     */     }
/* 192 */     ParameterizedType parameterized = maybeGetParameterizedType(type);
/* 193 */     if (parameterized != null && 
/*     */ 
/*     */       
/* 196 */       Objects.equals(boundType.getRawClass(), parameterized.getRawType())) {
/* 197 */       Type[] typeArguments = parameterized.getActualTypeArguments();
/* 198 */       TypeBindings bindings = boundType.getBindings();
/* 199 */       if (bindings.size() != typeArguments.length) {
/* 200 */         return false;
/*     */       }
/* 202 */       for (int i = 0; i < bindings.size(); i++) {
/* 203 */         JavaType boundTypeBound = bindings.getBoundType(i);
/* 204 */         Type typeArg = typeArguments[i];
/* 205 */         if (!pessimisticallyValidateBound(context, boundTypeBound, typeArg)) {
/* 206 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 210 */     return true;
/*     */   }
/*     */   
/*     */   private static TypeVariable<?> findByName(TypeVariable<?>[] typeVariables, String name) {
/* 214 */     if (typeVariables == null || name == null) {
/* 215 */       return null;
/*     */     }
/* 217 */     for (TypeVariable<?> typeVariable : typeVariables) {
/* 218 */       if (name.equals(typeVariable.getName())) {
/* 219 */         return typeVariable;
/*     */       }
/*     */     } 
/* 222 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/MethodGenericTypeResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */