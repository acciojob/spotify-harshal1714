package com.driver;

import java.util.*;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {

        User u = new User(name,mobile);
        users.add(u);

        return u;

    }

    public Artist createArtist(String name) {

        Artist a = new Artist(name);
        artists.add(a);

        return a;
    }

    public boolean isPresent(String name,Artist art)
    {
        for(int i=0;i<artists.size();i++)
        {
            if(artists.get(i).getName().equals(name))
            {
                art = artists.get(i);
                return true;
            }
        }

        return false;
    }

    public Album createAlbum(String title, String artistName) {
        //created album
        Album al = new Album(title);
        albums.add(al);
        //check for artist is present in the album or not and get the artist
        Artist art = null;
        boolean ans = isPresent(artistName,art);
        if(ans ==false)
        {
            Artist a = new Artist(artistName);
            art = a;
            artists.add(a);
        }

        //creating artist and list of albums of the artists
        if(artistAlbumMap.containsKey(art))
        {
            List<Album> list = artistAlbumMap.get(art);
            list.add(al);
            artistAlbumMap.put(art,list);
        }
        else{
            List<Album> list = new ArrayList<>();
            list.add(al);
            artistAlbumMap.put(art,list);
        }

        return al;


    }
    public boolean albumPresent(String albumName,Album al)
    {
        for(int i=0;i<albums.size();i++)
        {
            if(albums.get(i).getTitle().equals(albumName))
            {
                al = albums.get(i);
                return true;
            }
        }

        return false;
    }
    public Song createSong(String title, String albumName, int length) throws Exception{
        //create song
        Song s = new Song(title,length);
        songs.add(s);
        Album al = null;
        boolean ans = albumPresent(albumName,al);
        try{
            if(ans==false )
            {
                throw new RuntimeException();
            }
            else{
                if(albumSongMap.containsKey(al))
                {
                    List<Song> list = albumSongMap.get(al);
                    list.add(s);
                    albumSongMap.put(al,list);
                }
                else{
                    List<Song> list = new ArrayList<>();
                    list.add(s);
                    albumSongMap.put(al,list);
                }

            }
        }
        catch(Exception e)
        {
            System.out.println("Album does not exist");
        }


        return s;
    }

    public boolean userPresent(String mobile,User u)
    {
        for(int i=0;i<users.size();i++)
        {
            if(users.get(i).getMobile().equals(mobile))
            {
                u = users.get(i);
                return true;
            }
        }

        return false;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        Playlist p = new Playlist(title);
        playlists.add(p);
        User u = null;
        boolean ans = userPresent(mobile,u);
        try{
            if(ans==false)
            {
                throw new RuntimeException();
            }
        }
        catch(Exception e)
        {
            System.out.println("User does not exist");
        }
        return p;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        Playlist p = new Playlist(title);
        playlists.add(p);
        User u = null;
        boolean ans = userPresent(mobile,u);
        try{
            if(ans==false)
            {
                throw new RuntimeException();
            }
            else{
                //add all songs
                List<Song> l = new ArrayList<>();
                for(int i=0;i<songTitles.size();i++)
                {
                    String s = songTitles.get(i);
                    for(int j=0;j<songs.size();j++)
                    {
                        if(songs.get(i).getTitle().equals(s))
                        {
                            l.add(songs.get(i));
                        }
                    }
                }
                playlistSongMap.put(p,l);
                creatorPlaylistMap.put(u,p);

                if(playlistListenerMap.containsKey(u)){
                    List<User> li = playlistListenerMap.get(u);
                    li.add(u);
                    playlistListenerMap.put(p,li);
                }
                else{
                    List<User> li = new ArrayList<>();
                    li.add(u);
                    playlistListenerMap.put(p,li);
                }

                if(userPlaylistMap.containsKey(u))
                {
                    List<Playlist> r = userPlaylistMap.get(u);
                    r.add(p);
                    userPlaylistMap.put(u,r);
                }
                else{
                    List<Playlist> r = new ArrayList<>();
                    r.add(p);
                    userPlaylistMap.put(u,r);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("User does not exist");
        }

        return p;

    }
    public boolean findPlaylist1(String playlistTitle,Playlist p)
    {
        for(int i=0;i<playlists.size();i++)
        {
            if(playlists.get(i).getTitle().equals(playlistTitle))
            {
                p = playlists.get(i);
                return true;
            }
        }

        return false;
    }

    public boolean findListner(User u)
    {
        for(Playlist i:playlistListenerMap.keySet())
        {
            List<User> list = playlistListenerMap.get(i);
            for(User j:list)
            {
                if(u.equals(j))
                {
                    return true;
                }
            }
        }

        return false;
    }
    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        Playlist p = null;
        User u = null;
        try{
            if(userPresent(mobile,u)==false)
            {
                throw new RuntimeException();
            }
        }
        catch(Exception e)
        {
            System.out.println("User does not exist");
        }
        try{
            if(findPlaylist1(playlistTitle,p)==false)
            {
                throw new RuntimeException();
            }
        }
        catch(Exception e)
        {
            System.out.println("Playlist does not exist");
        }

        if(!creatorPlaylistMap.containsKey(u) && findListner(u)==false)
        {
            if(playlistListenerMap.containsKey(u)){
                List<User> li = playlistListenerMap.get(u);
                li.add(u);
                playlistListenerMap.put(p,li);
            }
            else{
                List<User> li = new ArrayList<>();
                li.add(u);
                playlistListenerMap.put(p,li);
            }
        }

        return p;

    }


    public boolean songPresent(String songTitle,Song s)
    {
        for(int i=0;i<songs.size();i++)
        {
            if(songs.get(i).getTitle().equals(songTitle))
            {
                s = songs.get(i);
                return true;
            }
        }
        return false;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User u = null;
        boolean ans = userPresent(mobile,u);
        try{
            if(ans==false)
            {
                throw new RuntimeException();
            }
        }
        catch(Exception e)
        {
            System.out.println("User does not exist");
        }
        Song s = null;
        boolean ans1 = songPresent(songTitle,s);
        try{
            if(ans1==false)
            {
                throw new RuntimeException();
            }
        }
        catch(Exception e)
        {
            System.out.println("Song does not exist");
        }
        if(ans==true && ans1==true)
        {
            if(songLikeMap.containsKey(s))
            {
                List<User> list = songLikeMap.get(s);
                list.add(u);
                songLikeMap.put(s,list);
            }
            else{
                List<User> list = new ArrayList<>();
                list.add(u);
                s.setLikes(s.getLikes()+1);
                songLikeMap.put(s,list);
            }
        }



        return s;
    }

    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        String name=null;
        for(int i=0;i< artists.size();i++)
        {
            if(artists.get(i).getLikes()>max)
            {
                name = artists.get(i).getName();
                max = artists.get(i).getLikes();
            }
        }


        return name;

    }

    public String mostPopularSong() {
        int max = Integer.MIN_VALUE;
        String name = null;
        for(int i=0;i< songs.size();i++)
        {
            if(songs.get(i).getLikes()>max)
            {
                max = songs.get(i).getLikes();
                name = songs.get(i).getTitle();
            }
        }

        return name;
    }
}