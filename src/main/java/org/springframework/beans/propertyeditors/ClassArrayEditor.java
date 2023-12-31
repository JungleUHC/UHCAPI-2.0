/*    */ package org.springframework.beans.propertyeditors;
/*    */ 
/*    */ import java.beans.PropertyEditorSupport;
/*    */ import java.util.StringJoiner;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.ClassUtils;
/*    */ import org.springframework.util.ObjectUtils;
/*    */ import org.springframework.util.StringUtils;
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
/*    */ 
/*    */ public class ClassArrayEditor
/*    */   extends PropertyEditorSupport
/*    */ {
/*    */   @Nullable
/*    */   private final ClassLoader classLoader;
/*    */   
/*    */   public ClassArrayEditor() {
/* 50 */     this((ClassLoader)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClassArrayEditor(@Nullable ClassLoader classLoader) {
/* 60 */     this.classLoader = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAsText(String text) throws IllegalArgumentException {
/* 66 */     if (StringUtils.hasText(text)) {
/* 67 */       String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
/* 68 */       Class<?>[] classes = new Class[classNames.length];
/* 69 */       for (int i = 0; i < classNames.length; i++) {
/* 70 */         String className = classNames[i].trim();
/* 71 */         classes[i] = ClassUtils.resolveClassName(className, this.classLoader);
/*    */       } 
/* 73 */       setValue(classes);
/*    */     } else {
/*    */       
/* 76 */       setValue(null);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsText() {
/* 82 */     Class<?>[] classes = (Class[])getValue();
/* 83 */     if (ObjectUtils.isEmpty((Object[])classes)) {
/* 84 */       return "";
/*    */     }
/* 86 */     StringJoiner sj = new StringJoiner(",");
/* 87 */     for (Class<?> klass : classes) {
/* 88 */       sj.add(ClassUtils.getQualifiedName(klass));
/*    */     }
/* 90 */     return sj.toString();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/propertyeditors/ClassArrayEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */