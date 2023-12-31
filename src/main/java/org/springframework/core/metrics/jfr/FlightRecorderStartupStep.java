/*     */ package org.springframework.core.metrics.jfr;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ import org.springframework.core.metrics.StartupStep;
/*     */ import org.springframework.lang.NonNull;
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
/*     */ class FlightRecorderStartupStep
/*     */   implements StartupStep
/*     */ {
/*     */   private final FlightRecorderStartupEvent event;
/*  37 */   private final FlightRecorderTags tags = new FlightRecorderTags();
/*     */ 
/*     */   
/*     */   private final Consumer<FlightRecorderStartupStep> recordingCallback;
/*     */ 
/*     */ 
/*     */   
/*     */   public FlightRecorderStartupStep(long id, String name, long parentId, Consumer<FlightRecorderStartupStep> recordingCallback) {
/*  45 */     this.event = new FlightRecorderStartupEvent(id, name, parentId);
/*  46 */     this.event.begin();
/*  47 */     this.recordingCallback = recordingCallback;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  53 */     return this.event.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getId() {
/*  58 */     return this.event.eventId;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getParentId() {
/*  63 */     return Long.valueOf(this.event.parentId);
/*     */   }
/*     */ 
/*     */   
/*     */   public StartupStep tag(String key, String value) {
/*  68 */     this.tags.add(key, value);
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StartupStep tag(String key, Supplier<String> value) {
/*  74 */     this.tags.add(key, value.get());
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StartupStep.Tags getTags() {
/*  80 */     return this.tags;
/*     */   }
/*     */ 
/*     */   
/*     */   public void end() {
/*  85 */     this.event.end();
/*  86 */     if (this.event.shouldCommit()) {
/*  87 */       StringBuilder builder = new StringBuilder();
/*  88 */       this.tags.forEach(tag -> builder.append(tag.getKey()).append('=').append(tag.getValue()).append(','));
/*     */ 
/*     */       
/*  91 */       this.event.setTags(builder.toString());
/*     */     } 
/*  93 */     this.event.commit();
/*  94 */     this.recordingCallback.accept(this);
/*     */   }
/*     */   
/*     */   protected FlightRecorderStartupEvent getEvent() {
/*  98 */     return this.event;
/*     */   }
/*     */   
/*     */   static class FlightRecorderTags
/*     */     implements StartupStep.Tags
/*     */   {
/* 104 */     private StartupStep.Tag[] tags = new StartupStep.Tag[0];
/*     */     
/*     */     public void add(String key, String value) {
/* 107 */       StartupStep.Tag[] newTags = new StartupStep.Tag[this.tags.length + 1];
/* 108 */       System.arraycopy(this.tags, 0, newTags, 0, this.tags.length);
/* 109 */       newTags[newTags.length - 1] = new FlightRecorderStartupStep.FlightRecorderTag(key, value);
/* 110 */       this.tags = newTags;
/*     */     }
/*     */     
/*     */     public void add(String key, Supplier<String> value) {
/* 114 */       add(key, value.get());
/*     */     }
/*     */ 
/*     */     
/*     */     @NonNull
/*     */     public Iterator<StartupStep.Tag> iterator() {
/* 120 */       return new TagsIterator();
/*     */     }
/*     */     
/*     */     private class TagsIterator
/*     */       implements Iterator<StartupStep.Tag> {
/* 125 */       private int idx = 0;
/*     */ 
/*     */       
/*     */       public boolean hasNext() {
/* 129 */         return (this.idx < FlightRecorderStartupStep.FlightRecorderTags.this.tags.length);
/*     */       }
/*     */ 
/*     */       
/*     */       public StartupStep.Tag next() {
/* 134 */         return FlightRecorderStartupStep.FlightRecorderTags.this.tags[this.idx++];
/*     */       }
/*     */ 
/*     */       
/*     */       public void remove() {
/* 139 */         throw new UnsupportedOperationException("tags are append only");
/*     */       }
/*     */       
/*     */       private TagsIterator() {}
/*     */     }
/*     */   }
/*     */   
/*     */   static class FlightRecorderTag
/*     */     implements StartupStep.Tag {
/*     */     private final String key;
/*     */     private final String value;
/*     */     
/*     */     public FlightRecorderTag(String key, String value) {
/* 152 */       this.key = key;
/* 153 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 158 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 163 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/metrics/jfr/FlightRecorderStartupStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */