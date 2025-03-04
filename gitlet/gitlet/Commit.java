package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  This class has constructor to initialize the commit message and date
 *  also has a toString method to print the commit message details
 *  @author AlMahoruq
 */
public class Commit implements Serializable {
    /** The current working directory */
    static final File objects = join(System.getProperty("user.dir"), ".gitlet", "objects");

    private Map<String, String> previous_files = new HashMap<>();
    private Map<String, String> current_files = new HashMap<>();

    /** The message of this Commit. */
    private String message;

    /** The current branch name */
    private String branch;

    /** The date of the commit */
    private Date date;

    /** The parent of the commit */
    private String parent;

    /** True if the commit merged, else otherwise */
    private boolean isMerged;

    /** If the commit is merged these are the ids of the two parents */
    private String firstParent, secondParent;

    /** The constructor to initialize the new object */
    public Commit(String message, String parent){
        this.message = message;
        this.parent = parent;
        this.branch = "master";
        if(this.parent == null){
            this.date = new Date(0);
        }
        else{
            this.date = new Date();
            File getParent = join(objects, this.parent.substring(0, 6), this.parent);
            Commit parentCommit = Utils.readObject(getParent, Commit.class);
            for(Map.Entry<String, String> entry: parentCommit.getPrevious_files().entrySet()){
                this.previous_files.put(entry.getKey(), entry.getValue());
            }
            for(Map.Entry<String, String> entry: parentCommit.getCurrent_files().entrySet()){
                this.previous_files.put(entry.getKey(), parent);
            }
        }
    }

    /** Get the parent of the commit */
    public String getParent(){ return this.parent; }

    /** Get the previous commit files */
    public Map<String, String> getPrevious_files(){ return this.previous_files;}

    /** Get the current commit files */
    public Map<String, String> getCurrent_files(){ return this.current_files;}

    /** checks for a file in the current files of the commit */
    public Boolean checkFileInCurrent(String name){ return current_files.containsKey(name); }

    /** checks for a file in the previous files of the parent commit */
    public Boolean checkFileInPrevious(String name){ return previous_files.containsKey(name); }

    /** Return the content of a file called name from the current files */
    public String getValueFromCurrent(String name) { return current_files.get(name); }

    /** Return the content of a file called name from the previous files */
    public String getValueFromPrevious(String name) { return previous_files.get(name); }

    /** Adding files to the commit */
    public void addFiles(Map<String, String> map){
        current_files.putAll(map);
    }

    /** Create a unique string for this commit to be used in sha1 function */
    public String getString(){
        return this.date.toString() + this.parent + this.message;
    }

    /** Returns the commit log message details */
    public String log_message(){
        String unique = this.date.toString() + this.parent + this.message;
        String res = "===\n" + "commit " + Utils.sha1(unique) + "\n";
        if(isMerged){
            res += "Merge: " + this.firstParent.substring(0, 7) + " " + this.secondParent.substring(0, 7) + "\n";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        res += "Date: " + sdf.format(this.date) + "\n" + this.message + "\n";
        return res;
    }
}
