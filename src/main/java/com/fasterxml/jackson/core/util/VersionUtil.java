/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.Versioned;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VersionUtil
/*     */ {
/*  28 */   private static final Pattern V_SEP = Pattern.compile("[-_./;:]");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Version version() {
/*  39 */     return Version.unknownVersion();
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
/*     */   public static Version versionFor(Class<?> cls) {
/*  62 */     Version v = null;
/*     */     try {
/*  64 */       String versionInfoClassName = cls.getPackage().getName() + ".PackageVersion";
/*  65 */       Class<?> vClass = Class.forName(versionInfoClassName, true, cls.getClassLoader());
/*     */       
/*     */       try {
/*  68 */         v = ((Versioned)vClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])).version();
/*  69 */       } catch (Exception e) {
/*  70 */         throw new IllegalArgumentException("Failed to get Versioned out of " + vClass);
/*     */       } 
/*  72 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/*  75 */     return (v == null) ? Version.unknownVersion() : v;
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
/*     */   public static Version packageVersionFor(Class<?> cls) {
/*  90 */     return versionFor(cls);
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
/*     */   @Deprecated
/*     */   public static Version mavenVersionFor(ClassLoader cl, String groupId, String artifactId) {
/* 111 */     InputStream pomProperties = cl.getResourceAsStream("META-INF/maven/" + groupId
/* 112 */         .replaceAll("\\.", "/") + "/" + artifactId + "/pom.properties");
/* 113 */     if (pomProperties != null) {
/*     */       try {
/* 115 */         Properties props = new Properties();
/* 116 */         props.load(pomProperties);
/* 117 */         String versionStr = props.getProperty("version");
/* 118 */         String pomPropertiesArtifactId = props.getProperty("artifactId");
/* 119 */         String pomPropertiesGroupId = props.getProperty("groupId");
/* 120 */         return parseVersion(versionStr, pomPropertiesGroupId, pomPropertiesArtifactId);
/* 121 */       } catch (IOException iOException) {
/*     */       
/*     */       } finally {
/* 124 */         _close(pomProperties);
/*     */       } 
/*     */     }
/* 127 */     return Version.unknownVersion();
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
/*     */   public static Version parseVersion(String s, String groupId, String artifactId) {
/* 142 */     if (s != null && (s = s.trim()).length() > 0) {
/* 143 */       String[] parts = V_SEP.split(s);
/* 144 */       return new Version(parseVersionPart(parts[0]), (parts.length > 1) ? 
/* 145 */           parseVersionPart(parts[1]) : 0, (parts.length > 2) ? 
/* 146 */           parseVersionPart(parts[2]) : 0, (parts.length > 3) ? parts[3] : null, groupId, artifactId);
/*     */     } 
/*     */ 
/*     */     
/* 150 */     return Version.unknownVersion();
/*     */   }
/*     */   
/*     */   protected static int parseVersionPart(String s) {
/* 154 */     int number = 0;
/* 155 */     for (int i = 0, len = s.length(); i < len; i++) {
/* 156 */       char c = s.charAt(i);
/* 157 */       if (c > '9' || c < '0')
/* 158 */         break;  number = number * 10 + c - 48;
/*     */     } 
/* 160 */     return number;
/*     */   }
/*     */   
/*     */   private static final void _close(Closeable c) {
/*     */     try {
/* 165 */       c.close();
/* 166 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final void throwInternal() {
/* 176 */     throw new RuntimeException("Internal error: this code path should never get executed");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/VersionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */