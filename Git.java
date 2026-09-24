import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;


public class Git {
    public static void main(String[] args) throws IOException {
        add("test/testing.txt");
        add("README.md");
    }

    public static void init() {
        int exist = 0;
        File git1 = new File("git");
        if (git1.exists() == false) {
            git1.mkdir();

        } else {
            exist+=1;
        }
        File git2 = new File("git/objects");
        if (git2.exists() == false) {
            git2.mkdir();
        } else {
            exist+=1;
        }
        File git3 = new File("git/INDEX");
        if (git3.exists() == false) {
            try {
                git3.createNewFile();   
            } catch (IOException e) {
                System.out.println("cant");
            }
        } else {
            exist+=1;
        }
        File git4 = new File("git/HEAD");
        if (git4.exists() == false) {
            try {
                git4.createNewFile();   
            } catch (IOException e) {
                System.out.println("cant");
            }
        } else {
            exist+=1;
        }

        if (exist == 4) {
            System.out.println("Git Repository Already Exists");
        } else {
            System.out.println("Git Repository Created");
        }
    }

    public static String hashFile(String filePath) throws IOException {
        String str = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            str = reader.readAllAsString();
        } catch (IOException e) {
            System.out.println("cant");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
            return hexString.toString();
        } catch (Exception e) {
            System.out.println("cant");
            return null;
        }
    }

    public static void blob(String filePath) throws IOException {
        String fileName = hashFile(filePath);
        String newpath = "git/objects/" + fileName;
        File hashed = new File(newpath);
        hashed.createNewFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            try (FileWriter writer = new FileWriter(newpath)) {
                String result = reader.readAllAsString();
                writer.write(result);
                reader.close();
                writer.close();
            } catch (Exception e) {
                System.out.println("cant");
            }
        } catch (Exception e) {
                System.out.println("cant");
        }


    }

    public static void index(String filePath) throws IOException {
        String fileName = hashFile(filePath);
        String current = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("git/INDEX"))) {
                current = reader.readAllAsString();
        }
        try (FileWriter writer = new FileWriter("git/INDEX")) {
            String line = fileName + " " + filePath + "\n";
            writer.write(current);
            writer.append(line);
        }
    }

    public static void add(String filePath) throws IOException {
        blob(filePath);
        index(filePath);
    }
}