import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VersionManagement {

    /*
    版本管理类，对于仓库的所有操作都会从这里进行接入。
    初始化时自动完成仓库的创建。
    当前仅提供一个接口：commit，可以进行版本管理。
     */

    private File warehouse;
    private String storagePath; // 仓库目录
    private String filePath;    // 工作区路径

    /**
     * 初始化仓库
     * @param warehousePath
     * @throws Exception
     */
    public VersionManagement(String warehousePath) {
        filePath = warehousePath;
        warehouse = new File(warehousePath);
        storagePath = warehousePath+"\\.versionManagement";
    }

    private void init() throws NoSuchFileException {
        if (!warehouse.exists()||!warehouse.isDirectory())//若是无效的仓库路径，则抛出异常
            throw new NoSuchFileException("无效的仓库路径");
        File versionManagement = new File(storagePath);
        if  (!versionManagement.exists()){
            versionManagement.mkdir();//若还没有创建".versionManagement"文件夹，则创建一个
            System.out.println("初始化成功");
        }
    }

    public File getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehousePath)throws Exception {
        warehouse = new File(warehousePath);
        if (!warehouse.exists()||!warehouse.isDirectory())//若是无效的仓库路径，则抛出异常
            throw new NoSuchFileException("无效的仓库路径");

        storagePath = warehousePath+"\\.versionManagement";
        File versionManagement = new File(storagePath);
        if  (!versionManagement.exists())
            versionManagement.mkdir();//若还没有创建".versionManagement"文件夹，则创建一个
    }

    /**
     * 提交一次commit，返回遍历commit记录的链表
     * @return LinkedHashMap
     * @throws Exception
     */


    /**
     * 用于处理输入的参数，执行对应的指令
     * @param args
     * @throws Exception
     */
    private void commandLine(String[] args) throws Exception {
        switch (args[0]) {
            case "init":
                // 初始化，建立仓库
                init();
                break;
            case "commit":
                // 提交一次commit
                new branch("Main", warehouse, storagePath);
                System.out.println("Commit 完成");
                break;
            case "log":
                // 查看提交记录
                branch br = new branch("Main", warehouse, storagePath);
                LinkedHashMap commitLog = br.commitLog();
                System.out.println("Commit ID：" + commitLog.keySet());
                break;
            case "rollback":
                // 回滚
                br = new branch("Main", warehouse, storagePath);
                br.rollBack(filePath);
                break;
            case "branch":
                br = new branch("Main", warehouse, storagePath);
                // 输入只有branch的话是查看分支，branch后加名称是创建新的分支
                if (args.length == 1) {
                    br.branchList();
                } else {
                    br.createBranch(args[1]);
                }
                break;
            case "checkout":
                br = new branch("Main", warehouse, storagePath);
                // 根据输入的名字切换分支
                if (br.checkoutBranch(args[1])) {
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
        VersionManagement task = new VersionManagement(currentPath);

        // 判断是否输入参数
        if (args.length == 0)
            System.out.println("请输入指令！");
        else
            task.commandLine(args);
    }
}
