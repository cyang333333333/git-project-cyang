import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class Git {

    public static boolean init() {
        int fileExistsCount = 0;
        File gitDirectory = new File("git");
        if (gitDirectory.exists() == false) {
            gitDirectory.mkdir();

        } else {
            fileExistsCount += 1;
        }
        File objDirectory = new File("git/objects");
        if (objDirectory.exists() == false) {
            objDirectory.mkdir();
        } else {
            fileExistsCount += 1;
        }
        File indexFile = new File("git/index");
        if (indexFile.exists() == false) {
            try {
                indexFile.createNewFile();
            } catch (IOException e) {
                System.out.println("cant make index");
            }
        } else {
            fileExistsCount += 1;
        }
        File headFile = new File("git/HEAD");
        if (headFile.exists() == false) {
            try {
                headFile.createNewFile();
            } catch (IOException e) {
                System.out.println("cant make HEAD");
            }
        } else {
            fileExistsCount += 1;
        }

        if (fileExistsCount == 4) {
            System.out.println("Git Repository Already Exists");
            return true;
        } else {
            System.out.println("Git Repository Created");
            return false;
        }
    }

    public static String hashFile(String filePath) throws IOException {
        String fileContents = "";
        File fp = new File(filePath);
        if (fp.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                //fileContents = reader.readAllAsString();
                fileContents = Files.readString(Path.of(filePath));
            } catch (IOException e) {
                System.out.println("cant read all as string");
            }
        } else {
            fileContents = filePath;
        }

        try {
            MessageDigest sha1digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1digest.digest(fileContents.getBytes());
            StringBuilder hashHexString = new StringBuilder();
            for (byte b : hashBytes) {
                hashHexString.append(String.format("%02x", b));
            }
            return hashHexString.toString();
        } catch (Exception e) {
            System.out.println("cant hash");
            return null;
        }
    }

    public static String hashString(String contents) {
        try {
            MessageDigest sha1digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1digest.digest(contents.getBytes());
            StringBuilder hashHexString = new StringBuilder();
            for (byte b : hashBytes) {
                hashHexString.append(String.format("%02x", b));
            }
            return hashHexString.toString();
        } catch (Exception e) {
            System.out.println("cant hash");
            return null;
        }
    }

    public static void blob(String filePath) throws IOException {
        String fileName = hashFile(filePath);
        String newPath = "git/objects/" + fileName;
        File objectFile = new File(newPath);
        objectFile.createNewFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            try (FileWriter writer = new FileWriter(newPath)) {
                //String fileContents = reader.readAllAsString();
                String fileContents = Files.readString(Path.of(filePath));
                writer.write(fileContents);
                reader.close();
                writer.close();
            } catch (Exception e) {
                System.out.println("blob: cant read and write files: " + e);
            }
        } catch (Exception e) {
            System.out.println("cant: " + e);
        }
    }

    public static void index(String filePath) throws IOException {
        String fileHash = hashFile(filePath);
        String existingIndexContents = "";

        Path sourcePath = Paths.get(filePath);
        String relativePath = sourcePath.toString();
        String indexEntry = fileHash + " " + relativePath;

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("git/index"))) {
            //existingIndexContents = reader.readAllAsString();
            existingIndexContents = Files.readString(Path.of("git/index"));
        }

        try (FileWriter writer = new FileWriter("git/index")) {
            String[] indexContentArray = existingIndexContents.split("\n");
            StringBuilder newIndexContent = new StringBuilder();
            for (int i = 0; i < indexContentArray.length; i++) {
                // checking if filePath already exists in the index folder
                int spaceIndex = indexContentArray[i].indexOf(" ");
                String currFilePath = indexContentArray[i].substring(spaceIndex + 1);

                // add same filePath, but with diff hash
                if (currFilePath.equals(filePath)) {
                    newIndexContent.append(indexEntry + "\n");
                    updated = true;
                }

                // add existing line (different filePath)
                else {
                    newIndexContent.append(indexContentArray[i] + "\n");
                }
            }

            // if filePath doesn't exist yet
            if (!updated) {
                newIndexContent.append(indexEntry);
            }

            // cut out "\n" in beginning and end
            String newIndexContentString = newIndexContent.toString().trim();

            // write to index file with the new index contents
            writer.write(newIndexContentString);
        }
    }

    public static void add(String filePath) throws IOException {
        blob(filePath);
        index(filePath);
    }

    public static String createTree(ArrayList<String> workingList, String directoryPath)
            throws IOException {
        StringBuilder treeContents = new StringBuilder();

        for (String line : workingList) {
            // decomposing each entry into its type, hash, and file path
            String[] parts = line.split(" ", 3);

            String type = parts[0];
            String hash = parts[1];
            String filePath = parts[2];

            File file = new File(filePath);
            String parentDirectory = file.getParent();

            // checking if the file belongs directly inside the directory we're creating the tree
            // for
            if (directoryPath.equals(parentDirectory)) {
                String fileName = file.getName();
                treeContents.append(type + " " + hash + " " + fileName + "\n");
            }
        }

        String treeHash = hashFile(treeContents.toString());
        FileWriter fw = new FileWriter("git/objects/" + treeHash);
        fw.write(treeContents.toString());
        fw.close();
        return treeHash;
    }

    public static ArrayList<String> workingList() throws IOException {
        ArrayList<String> workingList = new ArrayList<>();
        ArrayList<String> workingListFiles = new ArrayList<>();
        HashMap<String, String> fileMap = new HashMap<>();

        // reading the existing index file (going line by line), prefix "blob"
        try (BufferedReader br = new BufferedReader(new FileReader("git/index"))) {
            String line = br.readLine();

            while (line != null) {
                // add same filePath, but with diff hash
                if (!line.isBlank()) {
                    workingList.add("blob " + line);
                }

                line = br.readLine();
            }
        }

        // get file paths and store original entries
        for (String line : workingList) {
            String[] parts = line.split(" ", 3);
            String filePath = parts[2];

            workingListFiles.add(filePath);
            fileMap.put(filePath, line);
        }

        // sort alphabetically
        Collections.sort(workingListFiles);

        // rebuild working list in sorted order
        ArrayList<String> sortedWorkingList = new ArrayList<>();

        for (String filePath : workingListFiles) {
            sortedWorkingList.add(fileMap.get(filePath));
        }

        return sortedWorkingList;
    }

    public static void collapseWorkingList(ArrayList<String> workingList, String directoryPath,
            String treeHash) {

        // go through workingList backwards (to not skip entry lines)
        for (int i = workingList.size() - 1; i >= 0; i--) {
            String line = workingList.get(i);

            // get filePath from the entry line
            String[] parts = line.split(" ", 3);
            String filePath = parts[2];

            File file = new File(filePath);
            String parentDirectory = file.getParent();

            // remove entries that belong directly to this directory
            if (directoryPath.equals(parentDirectory)) {
                workingList.remove(i);
            }
        }

        workingList.add("tree " + treeHash + " " + directoryPath);
    }

    public static String buildRootTree() throws IOException {
        ArrayList<String> wL = workingList();
        
        if (wL.isEmpty()) {
            return null;
        }

        String rootDirectory = Paths.get(wL.get(0).split(" ", 3)[2]).getName(0).toString();

        while (wL.size() > 1 || !wL.get(0).split(" ", 3)[0].equals("tree")
                || !wL.get(0).split(" ", 3)[2].equals(rootDirectory)) {
            String deepestDirectory = "";
            int maxDepth = -1;

            // find deepest directory in wL
            for (String line : wL) {
                String[] parts = line.split(" ", 3);
                String filePath = parts[2];

                File file = new File(filePath);
                String parentDirectory = file.getParent();

                // get amount of directories in the path
                if (parentDirectory != null) {
                    int depth = Paths.get(parentDirectory).getNameCount();

                    if (depth > maxDepth) {
                        maxDepth = depth;
                        deepestDirectory = parentDirectory;
                    }
                }
            }

            String treeHash = createTree(wL, deepestDirectory);
            collapseWorkingList(wL, deepestDirectory, treeHash);
        }

        return wL.get(0).split(" ", 3)[1];

    }

    public static String commit(String message, String authorName) {
        //recursivly go through index + build it
        //list of commits singly linked

        boolean isFirst = false;
        String commitHash = "";

        try {
            String tree = buildRootTree();
            String parentCommit = Files.readString(Paths.get("git/HEAD")).trim();
            if (parentCommit.isEmpty()) {
                isFirst = true;
            }

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            StringBuilder commit = new StringBuilder();
            commit.append("tree: " + tree + "\nparent: " + parentCommit + "\nauthor: " + authorName + "\ndate: " + date + "\nmessage: " + message);

            commitHash = hashString(commit.toString());
            Files.writeString(Paths.get("git/objects/" + commitHash), commit.toString());


            //update HEAD
            try (FileWriter writer = new FileWriter("git/HEAD")) {
                    writer.write(commitHash);
                    writer.close();
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return commitHash;
    }

    public static String rebuildHash(String commitHash) {
        List<String> headCommit = null;
        StringBuilder index = new StringBuilder();


        try {
            //String headHash = Files.readString(Path.of("git/HEAD")).trim();
            headCommit = Files.readAllLines(Path.of("git/objects/" + commitHash));
        } catch (IOException e) {
            System.out.println(e);
        }

        String rootHash = headCommit.get(0).substring(6); //get hash from tree in commit message
        rebuildHashRecursive("", rootHash, index);
        
        //addToIndexAfterRebuilding(index.toString());
        return index.toString();

    }

    public static void rebuildHashRecursive(String directory, String hash, StringBuilder index) {
        List<String> rootMessage = null;
        try {
            rootMessage = Files.readAllLines(Path.of("git/objects" + "/" + hash));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < rootMessage.size(); i++) {
            String[] lineContents = rootMessage.get(i).split(" "); 
            String newDirectory = "";
            if (directory.equals("")) {
                newDirectory = lineContents[2];
            } else {
                newDirectory = directory + "/" + lineContents[2];
            }
            
            if (lineContents[0].equals("tree")) {
                rebuildHashRecursive(newDirectory, lineContents[1], index);
            } else if (lineContents[0].equals("blob")) {
                index.append(lineContents[1] + " " + newDirectory + "\n");
                
            }
            
        }
    }

    public static void addToIndexAfterRebuilding(String index) {
        try (FileWriter writer = new FileWriter("git/index")) {
            writer.write(index.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
