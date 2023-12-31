/*     */ package org.springframework.util;
/*     */ 
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.util.Random;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.net.ServerSocketFactory;
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
/*     */ @Deprecated
/*     */ public class SocketUtils
/*     */ {
/*     */   public static final int PORT_RANGE_MIN = 1024;
/*     */   public static final int PORT_RANGE_MAX = 65535;
/*  69 */   private static final Random random = new Random(System.nanoTime());
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
/*     */   public static int findAvailableTcpPort() {
/*  99 */     return findAvailableTcpPort(1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int findAvailableTcpPort(int minPort) {
/* 110 */     return findAvailableTcpPort(minPort, 65535);
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
/*     */   public static int findAvailableTcpPort(int minPort, int maxPort) {
/* 122 */     return SocketType.TCP.findAvailablePort(minPort, maxPort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SortedSet<Integer> findAvailableTcpPorts(int numRequested) {
/* 133 */     return findAvailableTcpPorts(numRequested, 1024, 65535);
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
/*     */   public static SortedSet<Integer> findAvailableTcpPorts(int numRequested, int minPort, int maxPort) {
/* 146 */     return SocketType.TCP.findAvailablePorts(numRequested, minPort, maxPort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int findAvailableUdpPort() {
/* 156 */     return findAvailableUdpPort(1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int findAvailableUdpPort(int minPort) {
/* 167 */     return findAvailableUdpPort(minPort, 65535);
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
/*     */   public static int findAvailableUdpPort(int minPort, int maxPort) {
/* 179 */     return SocketType.UDP.findAvailablePort(minPort, maxPort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SortedSet<Integer> findAvailableUdpPorts(int numRequested) {
/* 190 */     return findAvailableUdpPorts(numRequested, 1024, 65535);
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
/*     */   public static SortedSet<Integer> findAvailableUdpPorts(int numRequested, int minPort, int maxPort) {
/* 203 */     return SocketType.UDP.findAvailablePorts(numRequested, minPort, maxPort);
/*     */   }
/*     */ 
/*     */   
/*     */   private enum SocketType
/*     */   {
/* 209 */     TCP
/*     */     {
/*     */       protected boolean isPortAvailable(int port) {
/*     */         try {
/* 213 */           ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, 
/* 214 */               InetAddress.getByName("localhost"));
/* 215 */           serverSocket.close();
/* 216 */           return true;
/*     */         }
/* 218 */         catch (Exception ex) {
/* 219 */           return false;
/*     */         }
/*     */       
/*     */       }
/*     */     },
/* 224 */     UDP
/*     */     {
/*     */       protected boolean isPortAvailable(int port) {
/*     */         try {
/* 228 */           DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
/* 229 */           socket.close();
/* 230 */           return true;
/*     */         }
/* 232 */         catch (Exception ex) {
/* 233 */           return false;
/*     */         } 
/*     */       }
/*     */     };
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
/*     */     private int findRandomPort(int minPort, int maxPort) {
/* 252 */       int portRange = maxPort - minPort;
/* 253 */       return minPort + SocketUtils.random.nextInt(portRange + 1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int findAvailablePort(int minPort, int maxPort) {
/*     */       int candidatePort;
/* 265 */       Assert.isTrue((minPort > 0), "'minPort' must be greater than 0");
/* 266 */       Assert.isTrue((maxPort >= minPort), "'maxPort' must be greater than or equal to 'minPort'");
/* 267 */       Assert.isTrue((maxPort <= 65535), "'maxPort' must be less than or equal to 65535");
/*     */       
/* 269 */       int portRange = maxPort - minPort;
/*     */       
/* 271 */       int searchCounter = 0;
/*     */       do {
/* 273 */         if (searchCounter > portRange)
/* 274 */           throw new IllegalStateException(String.format("Could not find an available %s port in the range [%d, %d] after %d attempts", new Object[] {
/*     */                   
/* 276 */                   name(), Integer.valueOf(minPort), Integer.valueOf(maxPort), Integer.valueOf(searchCounter)
/*     */                 })); 
/* 278 */         candidatePort = findRandomPort(minPort, maxPort);
/* 279 */         searchCounter++;
/*     */       }
/* 281 */       while (!isPortAvailable(candidatePort));
/*     */       
/* 283 */       return candidatePort;
/*     */     }
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
/*     */     SortedSet<Integer> findAvailablePorts(int numRequested, int minPort, int maxPort) {
/* 296 */       Assert.isTrue((minPort > 0), "'minPort' must be greater than 0");
/* 297 */       Assert.isTrue((maxPort > minPort), "'maxPort' must be greater than 'minPort'");
/* 298 */       Assert.isTrue((maxPort <= 65535), "'maxPort' must be less than or equal to 65535");
/* 299 */       Assert.isTrue((numRequested > 0), "'numRequested' must be greater than 0");
/* 300 */       Assert.isTrue((maxPort - minPort >= numRequested), "'numRequested' must not be greater than 'maxPort' - 'minPort'");
/*     */ 
/*     */       
/* 303 */       SortedSet<Integer> availablePorts = new TreeSet<>();
/* 304 */       int attemptCount = 0;
/* 305 */       while (++attemptCount <= numRequested + 100 && availablePorts.size() < numRequested) {
/* 306 */         availablePorts.add(Integer.valueOf(findAvailablePort(minPort, maxPort)));
/*     */       }
/*     */       
/* 309 */       if (availablePorts.size() != numRequested) {
/* 310 */         throw new IllegalStateException(String.format("Could not find %d available %s ports in the range [%d, %d]", new Object[] {
/*     */                 
/* 312 */                 Integer.valueOf(numRequested), name(), Integer.valueOf(minPort), Integer.valueOf(maxPort)
/*     */               }));
/*     */       }
/* 315 */       return availablePorts;
/*     */     }
/*     */     
/*     */     protected abstract boolean isPortAvailable(int param1Int);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/SocketUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */