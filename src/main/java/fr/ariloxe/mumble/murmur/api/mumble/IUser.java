package fr.ariloxe.mumble.murmur.api.mumble;

public interface IUser {
  IMessage unmuteUser();
  
  IMessage muteUser();
  
  boolean isSelfDeaf();
  
  boolean isMute();
  
  boolean isSelfMute();
  
  void deleteUser();
  
  String getName();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/murmur/api/mumble/IUser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */