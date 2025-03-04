package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  It provides the implementation of the gitlet commands like init, add,... etc.
 *  @author AlMahoruq
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** Directory contains collect of object like commit */
    public static final File objects = join(GITLET_DIR, "objects");

    /** The branches directory */
    public static final File branches = join(GITLET_DIR, "branches");

    /** The master branch */
    public static final File masterBranch = join(branches, "master");

    /** Pointer to the current branch */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    /** Pointer to the current commit object */
    public static final File cur_commit = join(GITLET_DIR, "cur_commit");

    /** The staging area */
    public static final File stagingArea = join(GITLET_DIR, "stagingArea");

    /** Create a new version-control system in the current working directory. */
    public static void init(){
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        objects.mkdir();
        branches.mkdir();

        // creating the first commit
        Commit commit = new Commit("initial commit", null);
        String allSha = Utils.sha1(commit.getString());
        File newFile = join(objects, allSha.substring(0, 6));
        if(!newFile.exists()) newFile.mkdir();
        File newCommit = join(objects, allSha.substring(0, 6), allSha);
        Utils.writeObject(newCommit, commit);
        Utils.writeContents(cur_commit, Utils.sha1(commit.getString()));
        Utils.writeContents(HEAD, "master");

        // initialize the staging area object
        Utils.writeObject(stagingArea, new Staging_Area());
    }


    /** Adding helper method */
    private static void add_helper_method(String name){
        File file = join(CWD, name);
        Staging_Area getStagingArea = Utils.readObject(stagingArea, Staging_Area.class);
        getStagingArea.add(name, Utils.readContentsAsString(file));
        Utils.writeObject(stagingArea, getStagingArea);
    }

    /** Adding a file to the staging area */
    public static void add(String name){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file = join(CWD, name);
        if(!file.exists()){
            System.out.println("File does not exist.");
            return;
        }

        // check If the current working version of the file is identical to the version in the current commit
        String currentCommit = Utils.readContentsAsString(cur_commit);
        File commitFile = join(objects, currentCommit.substring(0, 6), currentCommit);
        Commit commitObj = Utils.readObject(commitFile, Commit.class);
        if(commitObj.checkFileInCurrent(name)){
            String content = commitObj.getValueFromCurrent(name);
            String fileCont = Utils.readContentsAsString(file);
            if(!content.equals(fileCont)){
                // add it to the staging area
                add_helper_method(name);
            }else{
                // delete it from the staging area if there
                Staging_Area getStagingArea = Utils.readObject(stagingArea, Staging_Area.class);
                if(getStagingArea.checkFile(name)) {
                    getStagingArea.remove(name);
                    Utils.writeObject(stagingArea, getStagingArea);
                }
            }
        }else if(commitObj.checkFileInPrevious(name)){
            String parentCommit = commitObj.getValueFromPrevious(name);
            commitFile = join(objects, parentCommit.substring(0, 6), parentCommit);
            commitObj = Utils.readObject(commitFile, Commit.class);
            String content = commitObj.getValueFromCurrent(name);
            String fileCon = Utils.readContentsAsString(file);
            if(!content.equals(fileCon)){
                // add it to the staging area
                add_helper_method(name);
            }else{
                // delete it from the staging area if there
                Staging_Area getStagingArea = Utils.readObject(stagingArea, Staging_Area.class);
                if(getStagingArea.checkFile(name)) {
                    getStagingArea.remove(name);
                    Utils.writeObject(stagingArea, getStagingArea);
                }
            }
        }else{
            // It has not been tracked by a previous commit so add it
            add_helper_method(name);
        }
    }

    /**
     * Saves a snapshot of tracked files.
     * Any changes made to files after staging for addition or removal are ignored.
     * The staging area should be cleared after the commit.
     * @param message: the commit message.
     */
    public static void commit(String message){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if(message.isEmpty()){
            System.out.println("Please enter a commit message.");
            return;
        }

        // if no files are in the staging area
        Staging_Area getStagingArea = Utils.readObject(stagingArea, Staging_Area.class);
        Map<String, String> map = getStagingArea.getFiles();
        if(map.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }

        // making a new commit with the updates in the staging area
        Commit commit = new Commit(message, Utils.readContentsAsString(cur_commit));
        commit.addFiles(map);
        String allSha = Utils.sha1(commit.getString());
        File newFile = join(objects, allSha.substring(0, 6));
        if(!newFile.exists()) newFile.mkdir();
        File newCommit = join(objects, allSha.substring(0, 6), allSha);
        Utils.writeObject(newCommit, commit);
        Utils.writeContents(cur_commit, allSha);

        // clear the staging area
        getStagingArea.clear();
        Utils.writeObject(stagingArea, getStagingArea);
    }

    /** Print the commits history */
    public static void log(){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String lastCommit = Utils.readContentsAsString(cur_commit);
        while(true){
            File commitObj = join(objects, lastCommit.substring(0, 6), lastCommit);
            Commit commit = Utils.readObject(commitObj, Commit.class);
            System.out.println(commit.log_message());
            if(commit.getParent() == null) break;
            lastCommit = commit.getParent();
        }
    }

    /** checkout last version of file name from the last command */
    public static void checkoutFile(String name){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        String lastCommit = Utils.readContentsAsString(cur_commit);
        checkoutId(lastCommit, name);
    }

    /** checkout last version of file name form commit id */
    public static void checkoutId(String Id, String name){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File file = join(objects, Id.substring(0, 6));
        if(!file.exists()){
            System.out.println("No commit with that id exists.");
            return;
        }
        if(Id.length() == 40){
            file = join(objects, Id.substring(0, 6), Id);
        }else{
            List<String> l = Utils.plainFilenamesIn(file);
            file = join(objects, Id.substring(0, 6), l.get(0));
        }
        Commit commit = Utils.readObject(file, Commit.class);

        if(commit.checkFileInCurrent(name)){
            String content = commit.getValueFromCurrent(name);
            File check = join(CWD, name);
            Utils.writeContents(check, content);
        }else if(commit.checkFileInPrevious(name)){
            String parent = commit.getValueFromPrevious(name);
            file = join(objects, parent.substring(0, 6), parent);
            commit = Utils.readObject(file, Commit.class);
            String content = commit.getValueFromCurrent(name);
            File check = join(CWD, name);
            Utils.writeContents(check, content);
        }else{
            System.out.println("File does not exist in that commit.");
        }
    }

    /** checkout */
    public static void checkoutBranch(String name){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
    }

}
