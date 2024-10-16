package com.bike.mp3mp4converter.Conversion;


import java.util.ArrayList;

public class MetadataManager {
    public String title, artist, album, year, genre;

    transient String metadataTag = "-metadata ";

    ArrayList<String> strings = new ArrayList<>();

    transient public String command;
    public void init() {
        command = "";
        if (hasTitle()) {
            command += metadataTag + "title=\"" + title + "\" ";
            strings.add(title);
        }
        if (hasArtist()) {
            command += metadataTag + "artist=\"" + artist + "\" ";
            strings.add(artist);
        }
        if (hasAlbum()) {
            command += metadataTag + "album=\"" + album + "\" ";
            strings.add(album);
        }
        if (hasYear()) {
            command += metadataTag + "year=\"" + year + "\" ";
            strings.add(year);
        }
        if (hasGenre()) {
            command += metadataTag + "genre=\"" + genre + "\" ";
            strings.add(genre);
        }
    }
    @Override
    public String toString() {
        return strings.toString();
    }

    public boolean hasMetadata() {
        return hasTitle() || hasArtist() || hasAlbum() || hasYear() || hasGenre();
    }

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasArtist() {
        return artist != null;
    }

    public boolean hasAlbum() {
        return album != null;
    }

    public boolean hasYear() {
        return year != null;
    }

    public boolean hasGenre() {
        return genre != null;
    }
}
