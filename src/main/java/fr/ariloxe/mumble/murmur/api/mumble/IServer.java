package fr.ariloxe.mumble.murmur.api.mumble;

import java.util.List;

public interface IServer {
  int getId();
  
  int getPort();
  
  void stop();
  
  void delete();
  
  List<IUser> getUsers();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/api/mumble/IServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */