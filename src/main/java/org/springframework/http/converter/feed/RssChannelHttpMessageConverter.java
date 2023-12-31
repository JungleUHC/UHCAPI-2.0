/*    */ package org.springframework.http.converter.feed;
/*    */ 
/*    */ import com.rometools.rome.feed.rss.Channel;
/*    */ import org.springframework.http.MediaType;
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
/*    */ public class RssChannelHttpMessageConverter
/*    */   extends AbstractWireFeedHttpMessageConverter<Channel>
/*    */ {
/*    */   public RssChannelHttpMessageConverter() {
/* 41 */     super(MediaType.APPLICATION_RSS_XML);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean supports(Class<?> clazz) {
/* 46 */     return Channel.class.isAssignableFrom(clazz);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/feed/RssChannelHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */