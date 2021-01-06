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

    /**
     * 用于处理输入的参数，执行对应的指令
     * @param args
     * @throws Exception
     */
    private void commandLine(String[] args) throws Exception {
        switch (args[0]) {
            case "init":
                // 初始化，建立仓库
                folder = new VersionManagement(filePath);
                break;
            case "commit":
                // 提交一次commit
                folder = new VersionManagement(filePath);
                folder.commit();
                System.out.println("Commit 完成");
                break;
            case "log":
                // 查看提交记录
                folder = new VersionManagement(filePath);
                LinkedHashMap commitLog = folder.commit();
                System.out.println("Commit ID：" + commitLog.keySet());
                break;
            case "rollback":
                // 回滚
                rollBack rb = new rollBack(filePath);
                break;
            case "branch":
                // 输入只有branch的话是查看分支，branch后加名称是创建新的分支
                if (args.length == 1) {
                    Branch();
                } else {
                    createBranch(args[1]);
                }
                break;
            case "checkout":
                // 根据输入的名字切换分支
                if (checkoutBranch(args[1])) {
                    System.out.println("切换分支成功");
                } else {
                    System.out.println("切换分支失败");
                }
                break;
            default:
                System.out.println("请输入正确的指令！");
                break;
        }
    }


    public static void main(String[] args) throws Exception {
        String currentPath = new java.io.File( "." ).getCanonicalPath();    // 获取当前工作路径
        String vmPath = currentPath + "\\.versionManagement";
        gitCommand task = new gitCommand(new File(currentPath), vmPath, currentPath);

        // 判断是否输入参数
        if (args.length == 0)
            System.out.println("请输入指令！");
        else
            task.commandLine(args);
    }
}
