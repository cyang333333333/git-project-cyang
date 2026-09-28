import java.io.File;
import java.io.IOException;

public class GitTester {
    public static void main(String[] args) throws IOException {
        Git.init();
        File newText = new File("nexText.txt");
        newText.createNewFile();
        checkRepo();
        cleanup();

        while (checkRepo() == false) {
            cleanup();
            Git.init();
        }
        
    }

    public static boolean checkRepo() {
        int exist = 0;
        File git1 = new File("git");
        if (git1.exists() == true) {
            exist+=1;
        }
        File git2 = new File("git/objects");
        if (git2.exists() == true) {
            exist+=1;
        }
        File git3 = new File("git/INDEX");
        if (git3.exists() == true) {
            exist+=1;
        }
        File git4 = new File("git/HEAD");
        if (git4.exists() == true) {
            exist+=1;
        } 
        if (exist == 4) {
            System.out.println("Git Repository correctly initialized");
            return true;
        } else {
            System.out.println("Git Repository doesnt correctly initialized");
            return false;
        }
    }

    public static void cleanup() {
        File git1 = new File("git");
        if (git1.exists() == true) {
            git1.delete();
        }
        File git2 = new File("git/objects");
        if (git2.exists() == true) {
            git2.delete();
        }
        File git3 = new File("git/INDEX");
        if (git3.exists() == true) {
            git3.delete();
        }
        File git4 = new File("git/HEAD");
        if (git4.exists() == true) {
            git4.delete();
        } 
    }
}
