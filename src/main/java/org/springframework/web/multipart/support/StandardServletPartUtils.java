/*    */ package org.springframework.web.multipart.support;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.Part;
/*    */ import org.springframework.beans.MutablePropertyValues;
/*    */ import org.springframework.util.LinkedMultiValueMap;
/*    */ import org.springframework.util.MultiValueMap;
/*    */ import org.springframework.web.multipart.MultipartException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StandardServletPartUtils
/*    */ {
/*    */   public static MultiValueMap<String, Part> getParts(HttpServletRequest request) throws MultipartException {
/*    */     try {
/* 48 */       LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 49 */       for (Part part : request.getParts()) {
/* 50 */         linkedMultiValueMap.add(part.getName(), part);
/*    */       }
/* 52 */       return (MultiValueMap<String, Part>)linkedMultiValueMap;
/*    */     }
/* 54 */     catch (Exception ex) {
/* 55 */       throw new MultipartException("Failed to get request parts", ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Part> getParts(HttpServletRequest request, String name) throws MultipartException {
/*    */     try {
/* 68 */       List<Part> parts = new ArrayList<>(1);
/* 69 */       for (Part part : request.getParts()) {
/* 70 */         if (part.getName().equals(name)) {
/* 71 */           parts.add(part);
/*    */         }
/*    */       } 
/* 74 */       return parts;
/*    */     }
/* 76 */     catch (Exception ex) {
/* 77 */       throw new MultipartException("Failed to get request parts", ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void bindParts(HttpServletRequest request, MutablePropertyValues mpvs, boolean bindEmpty) throws MultipartException {
/* 91 */     getParts(request).forEach((key, values) -> {
/*    */           if (values.size() == 1) {
/*    */             Part part = values.get(0);
/*    */             if (bindEmpty || part.getSize() > 0L)
/*    */               mpvs.add(key, part); 
/*    */           } else {
/*    */             mpvs.add(key, values);
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/StandardServletPartUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */