/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Version
/*     */   implements Comparable<Version>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   private static final Version UNKNOWN_VERSION = new Version(0, 0, 0, null, null, null);
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _majorVersion;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _minorVersion;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _patchLevel;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _groupId;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _artifactId;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _snapshotInfo;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Version(int major, int minor, int patchLevel, String snapshotInfo) {
/*  51 */     this(major, minor, patchLevel, snapshotInfo, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Version(int major, int minor, int patchLevel, String snapshotInfo, String groupId, String artifactId) {
/*  57 */     this._majorVersion = major;
/*  58 */     this._minorVersion = minor;
/*  59 */     this._patchLevel = patchLevel;
/*  60 */     this._snapshotInfo = snapshotInfo;
/*  61 */     this._groupId = (groupId == null) ? "" : groupId;
/*  62 */     this._artifactId = (artifactId == null) ? "" : artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Version unknownVersion() {
/*  72 */     return UNKNOWN_VERSION;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUnknownVersion() {
/*  80 */     return (this == UNKNOWN_VERSION);
/*     */   } public boolean isSnapshot() {
/*  82 */     return (this._snapshotInfo != null && this._snapshotInfo.length() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isUknownVersion() {
/*  91 */     return isUnknownVersion();
/*     */   }
/*  93 */   public int getMajorVersion() { return this._majorVersion; }
/*  94 */   public int getMinorVersion() { return this._minorVersion; } public int getPatchLevel() {
/*  95 */     return this._patchLevel;
/*     */   }
/*  97 */   public String getGroupId() { return this._groupId; } public String getArtifactId() {
/*  98 */     return this._artifactId;
/*     */   }
/*     */   public String toFullString() {
/* 101 */     return this._groupId + '/' + this._artifactId + '/' + toString();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 105 */     StringBuilder sb = new StringBuilder();
/* 106 */     sb.append(this._majorVersion).append('.');
/* 107 */     sb.append(this._minorVersion).append('.');
/* 108 */     sb.append(this._patchLevel);
/* 109 */     if (isSnapshot()) {
/* 110 */       sb.append('-').append(this._snapshotInfo);
/*     */     }
/* 112 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 116 */     return this._artifactId.hashCode() ^ this._groupId.hashCode() + this._majorVersion - this._minorVersion + this._patchLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 122 */     if (o == this) return true; 
/* 123 */     if (o == null) return false; 
/* 124 */     if (o.getClass() != getClass()) return false; 
/* 125 */     Version other = (Version)o;
/* 126 */     return (other._majorVersion == this._majorVersion && other._minorVersion == this._minorVersion && other._patchLevel == this._patchLevel && other._artifactId
/*     */ 
/*     */       
/* 129 */       .equals(this._artifactId) && other._groupId
/* 130 */       .equals(this._groupId));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Version other) {
/* 137 */     if (other == this) return 0;
/*     */     
/* 139 */     int diff = this._groupId.compareTo(other._groupId);
/* 140 */     if (diff == 0) {
/* 141 */       diff = this._artifactId.compareTo(other._artifactId);
/* 142 */       if (diff == 0) {
/* 143 */         diff = this._majorVersion - other._majorVersion;
/* 144 */         if (diff == 0) {
/* 145 */           diff = this._minorVersion - other._minorVersion;
/* 146 */           if (diff == 0) {
/* 147 */             diff = this._patchLevel - other._patchLevel;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 152 */     return diff;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/Version.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */