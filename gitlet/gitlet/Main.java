package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author AlMahrouq
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Please enter a command.");
            return ;
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.commit(args[1]);
                break;
            case "rm":
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                break;
            case "find":
                break;
            case "status":
                break;
            case "checkout":
                if(args.length == 2){ // branch name
                    Repository.checkoutBranch(args[1]);
                }else if(args.length == 3){ // file name
                    Repository.checkoutFile(args[2]);
                }else{ // commit id and file name
                    Repository.checkoutId(args[1], args[3]);
                }
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
