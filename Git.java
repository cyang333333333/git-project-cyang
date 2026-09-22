import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;


public class Git {
    public static void main(String[] args) throws IOException {
        init();
        System.out.println(hashFile("README.md"));
        
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
        File git3 = new File("git/index");
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
            str = reader.readLine();
        } catch (IOException e) {
            System.out.println("cant");
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            byte[] encodedHash = digest.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();
                for (byte b : encodedHash) {
                    String hex = String.format("%02x", b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
            return hexString.toString();
        } catch (Exception e) {
            System.out.println("cant missing file");
            return null;
        }
    }
}