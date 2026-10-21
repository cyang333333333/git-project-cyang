import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class GitWrapper {

    /**
     * Initializes a new Git repository.
     * This method creates the necessary directory structure
     * and initial files (index, HEAD) required for a Git repository.
     */
    public void init() {
        // to-do: implement functionality here
        Git.init();
    }

    /**
     * Stages a file for the next commit.
     * This method adds a file to the index file.
     * If the file does not exist, it throws an IOException.
     * If the file is a directory, it throws an IOException.
     * If the file is already in the index, it does nothing.
     * If the file is successfully staged, it creates a blob for the file.
     * @param filePath The path to the file to be staged.
     */
    public void add(String filePath) {
        // to-do: implement functionality here
        try {
            Git.add(filePath);
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

    /**
     * Creates a commit with the given author and message.
     * It should capture the current state of the repository by building trees based on the index file,
     * writing the tree to the objects directory,
     * writing the commit to the objects directory,
     * updating the HEAD file,
     * and returning the commit hash.
     *
     * The commit should be formatted as follows:
     * tree: <tree_sha>
     * parent: <parent_sha>
     * author: <author>
     * date: <date>
     * message: <message>
     *
     * @param author  The name of the author making the commit.
     * @param message The commit message describing the changes.
     * @return The SHA1 hash of the new commit.
     */
    public String commit(String author, String message) {
        return Git.commit(message, author);
    }

    /**
     * Checks out a specific commit given its hash.
     * This method should read the HEAD file to determine the "checked out" commit.
     * Then it should update the working directory to match the
     * state of the repository at that commit by tracing through the root tree and
     * all its children.
     *
     * @param commitHash The SHA1 hash of the commit to check out.
     */
    public void checkout(String commitHash) {
        // to-do: implement functionality here
        String headHash = "";
        try {
            headHash = Files.readString(Path.of("git/HEAD")).trim();
        } catch (IOException e) {
            System.out.println(e);
        }

        String[] currentIndex = Git.rebuildHash(headHash).split("\n");
        String targetString = Git.rebuildHash(commitHash);
        String[] targetIndex = targetString.split("\n");

        for (int i = 0; i < currentIndex.length; i++) {
            try {
                Files.deleteIfExists(Paths.get(currentIndex[i].split(" ", 2)[1]));
                File parentDirectory = new File(currentIndex[i].split(" ", 2)[1]).getParentFile();
                while (parentDirectory != null && parentDirectory.delete()) {
                    parentDirectory = parentDirectory.getParentFile();
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        for (int i = 0; i < targetIndex.length; i++) {
            String hash = targetIndex[i].split(" ", 2)[0];
            String path = targetIndex[i].split(" ", 2)[1];

            File parentDirectory = new File(path).getParentFile();
            if (parentDirectory != null) {
                parentDirectory.mkdirs();
            }

            String contents = "";
            try {
                contents = Files.readString(Paths.get("git/objects/" + hash));
                FileWriter writer = new FileWriter(path);
                writer.write(contents);
                writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        //rewrite index file
        Git.addToIndexAfterRebuilding(targetString);

        //update head
        try (FileWriter writer = new FileWriter("git/HEAD")) {
            writer.write(commitHash);
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

        


        // String[] currentIndex = Git.rebuildHash(headHash).split("\n");
        // String targetString = Git.rebuildHash(commitHash);
        // String[] targetIndex = targetString.split("\n");

        // for (int i = 0; i < currentIndex.length; i++) {
        //     boolean found = false;
        //     for (int j = 0; j < targetIndex.length; j++) {
        //         if (currentIndex[i].split(" ", 2)[1].equals(targetIndex[j].split(" ", 2)[1])) {
        //             found = true;
        //         }
        //     }
            
        //     if (found == false) {
        //         try {
        //             Files.delete(Paths.get(currentIndex[i].split(" ", 2)[1]));
        //             File parentDirectory = new File(currentIndex[i].split(" ", 2)[1]).getParentFile();
                    
        //             while (parentDirectory.delete()) {
        //                 parentDirectory = parentDirectory.getParentFile(); //handles nested folders; delete only works if folder empty
        //             }
        //         } catch (IOException e) {
        //             System.out.println(e);
        //         }
        //     }
        // }
        
        
    
}