package gitlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Represents the staging area.
 * Has methods to check for a file in the staging area, get it's content, and add file or update file contents.
 * @author AlMahrouq
 */
public class Staging_Area implements Serializable{
    /** Represent the files currently in the staging area */
    private Map<String, String> files = new HashMap<>();

    /** checks for a file in the current files */
    public Boolean checkFile(String name){ return files.containsKey(name); }

    /** Return the content of a file called name from the current files */
    public String getValue(String name) { return files.get(name); }

    /**
     * Add a file to the staging area
     * If the file is already exists it's value will be updated
     */
    public void add(String name, String value){ files.put(name, value); }

    /** Remove a key from the current tracked files */
    public void remove(String name){ files.remove(name); }

    /** Return the files in the staging area */
    public Map<String, String> getFiles() {return files; }

    public void clear(){ files.clear(); }
}
