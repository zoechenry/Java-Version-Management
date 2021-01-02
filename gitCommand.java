import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public class gitCommand extends KeyValue{
    private final String filePath;
    private VersionManagement folder;

    private gitCommand(File inputFile, String storagePath, String Path) throws IOException {
        super(inputFile, storagePath);
        filePath = Path;
    }

    private void commandLine(String[] args) throws Exception {
        if (args[0].equals("init")){
            init();
        }
        else if (args[0].equals("commit")){
            // 提交一次commit
            folder = new VersionManagement(filePath);
            folder.commit();
            System.out.println("Commit 完成");
        }
        else if (args[0].equals("log")){
            // 查看提交记录
            folder = new VersionManagement(filePath);
            LinkedHashMap commitLog = folder.commit();
            System.out.println("Commit ID：" + commitLog.keySet());
        }
        else if (args[0].equals("rollback")){
            rollBack rb = new rollBack(filePath);
        }
        else if (args[0].equals("branch")){
            if (args.length==1){
                Branch();
            }
            else{
                createBranch(args[1]);
            }
        }
        else if (args[0].equals("checkout")){
            if (checkoutBranch(args[1])){
                System.out.println("切换分支成功");
            }else{
                System.out.println("切换分支失败");
            }
        }else{
            System.out.println("请输入正确的指令！");
        }
    }

    private void init() throws Exception {
        folder = new VersionManagement(filePath);
        System.out.println("初始化成功！");
    }

    public static void main(String[] args) throws Exception {
        String currentPath = new java.io.File( "." ).getCanonicalPath();
        String vmPath = currentPath + "\\.versionManagement";
        gitCommand task = new gitCommand(new File(currentPath), vmPath, currentPath);

        if (args.length == 0)
            System.out.println("请输入指令！");
        else
            task.commandLine(args);
    }
}
