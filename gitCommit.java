import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class gitCommit {
    // 获取commit文件中tree对象的Hash值
    public static String getCommitTreeKey(File file) throws IOException{
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String commitLatestHash = br.readLine();
        if(commitLatestHash==null) return null;
        String[] hashSplit = commitLatestHash.split(": ");
        if(!hashSplit[0].equals("hashOfHomeFolder")) return null;
        String result = hashSplit[hashSplit.length-1]; // 获取时间
        return result;
    }

    // 寻找Head文件，里面有最新一次commit的文件名，返回其名称
    public static String latestCommit(String storagePath) throws IOException {
        File HEADFile = new File(storagePath + "\\HEAD");
        if(!HEADFile.exists()) return null;
        else{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
            BufferedReader br = new BufferedReader(isr);
            String commitLatest = br.readLine();
            return commitLatest;
        }
    }

    // 更改文件的名字
    public static boolean rename(final File file, final String newName) {
        // file is null then return false
        if (file == null) return false;
        // file doesn't exist then return false
        if (!file.exists()) return false;
        // the new name equals old name then return true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        return !newFile.exists() && file.renameTo(newFile);
    }

    public static <Stirng> boolean commit(String filePath)throws Exception{
        // 在.versionManagement下记录commit
        String logpath = filePath + "\\.versionManagement";
        String latestHash;
        // 保存Head的Hash值
        String hashOfHomeFolder = KeyValueStorage.createKeyValueOfPath(filePath);
        try{
        // 检查.log中最新commit存储的Hash值，如果不同再进行新的commit
            // 首先找到最新的commit
            String latestFilename = latestCommit(logpath);  // 找到log中最新一次提交的文件
            if(latestFilename==null){
                latestHash = null;
            }
            else{
                latestHash = getCommitTreeKey(new File(logpath+"\\"+latestFilename)); // 提取这个文件tree对象的Hash值
            }

            if(!hashOfHomeFolder.equals(latestHash)){
                BufferedWriter out = new BufferedWriter(new FileWriter(logpath + "\\newCommit"));
                // 需要写入：tree对象的key；前驱commit对象的key；时间戳；备注
                // 写入备注:
                System.out.println("请输入备注：");
                Scanner input = new Scanner(System.in);
                String commitNote = input.nextLine();

                out.write("hashOfHomeFolder: " + hashOfHomeFolder + "\n"); // tree对象key
                out.write("Parent: " + latestFilename + "\n"); // 没有前驱commit，设为NULL
                out.write("Timestamp: " + (int) System.currentTimeMillis() + "\n"); // 时间戳
                out.write("Note: " + commitNote + "\n"); // 备注
                out.close();

                // 把文件名字改为commit的key
                File newFile = new File(logpath + "\\newCommit");
                String commitHash = getHashValue.getHash(newFile);
                rename(newFile, commitHash);

                // 更新（创建）HEAD文件，存储这次commit的Key
                BufferedWriter headOut = new BufferedWriter(new FileWriter(logpath + "\\HEAD"));
                headOut.write(commitHash);
                headOut.close();

                System.out.println("更新成功！");
                return true;
            }
            else{
                System.out.println("无变化，不进行更新！");
                return false;
            }
        }catch (IOException e){
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        String hashPath = "C:\\Users\\zrc5\\Desktop\\test";
        commit(hashPath);
    }
}
