package fr.ariloxe.mumble.murmur.api.mumble;

public interface IMumbleManager {
  void setHostName(String paramString);
  
  String getHostName();
  
  void setPort(String paramString);
  
  String getPort();
  
  void createServer();
  
  IServer getServer();
  
  IChannel getChannel();
  
  IUser getUserFromName(String paramString);
  
  MumbleState getStateOf(String paramString);
  
  void createUser(String paramString1, String paramString2);
  
  void setServer(IServer paramIServer);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/api/mumble/IMumbleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */