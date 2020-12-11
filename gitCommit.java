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

    public static <Stirng> boolean commit(String filePath)throws Exception{
        // 在.versionManagement下记录commit
        String logpath = filePath + "\\.versionManagement";
        String latestHash;
        // 保存Head的Hash值
        String hashOfHomeFolder = KeyValueStorage.createKeyValueOfPath(filePath);
        try{
        // 检查最新commit存储的Hash值，如果不同再进行新的commit
            // 首先找到最新的commit
            String latestFilename = latestCommit(logpath);  // 找到log中最新一次提交的文件
            if(latestFilename==null){
                latestHash = null;
            }
            else{
                latestHash = getCommitTreeKey(new File(logpath+"\\"+latestFilename)); // 提取这个文件tree对象的Hash值
            }

            if(!hashOfHomeFolder.equals(latestHash)){
                String commitHash = KeyValueStorage.createCommitLog(hashOfHomeFolder, latestFilename, logpath);
                KeyValueStorage.createHead(commitHash,logpath);  // 更新（创建）HEAD文件，存储这次commit的Key
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
