import java.io.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class rollBack {

    /*
    目前实现功能：根据HEAD文件寻找所有的Commit，
    选定一条Commit记录后，

    根据这条commit记录的根节点hash值深度优先遍历整个树结构，
    恢复所有的文件夹及文件夹中的文件
     */
    private String filePath; // 用于存储仓库.versionManagement的路径
    private LinkedHashMap commitLog;
    private String storagePath;
    private String commitID;

    /**
     * 初始化rollBack对象时，保证有一次最新的提交
     * @param path
     * @throws Exception
     */
    public rollBack(String path) throws Exception {
        storagePath = path;
        filePath = storagePath + "\\.versionManagement";

        commitLog();
        System.out.println("输入Commit ID：");
        Scanner input = new Scanner(System.in);
        commitID = input.nextLine();
        if (!commitLog.containsKey(commitID)){
            System.out.println("commitID 不正确！");
            return;
        }
        String treeRoot = (String) commitLog.get(commitID);

        // 文件夹非空，先删除文件
        File[] files = new File(storagePath).listFiles();
        for (File f: files){
            if (!f.getName().equals(".versionManagement"))
                deleteFolder(f);
        }
        // 根据根节点在.versionManagement下寻找文件
        rb(treeRoot, storagePath);
        // 最后头指针存的commit值要变
        changeBranch(commitID);
        System.out.println("回滚完成！");
    }

    /**
     * 获取commit记录
     * @throws Exception
     */
    public void commitLog() throws Exception {
        // 创建一个.versionManagement文件夹
        VersionManagement folder = new VersionManagement(storagePath);
        // 提交一次commit，获得返回tree根节点的Hash值
        commitLog = folder.commit();
        System.out.println("Commit ID：" + commitLog.keySet());
    }

    /**
     * 递归恢复文件
     * @param filename
     * @param recursivePath
     * @throws Exception
     */
    private void rb(String filename, String recursivePath) throws Exception{
        String filenamePath = filePath +"\\"+ filename;
        File treeNode = new File(filenamePath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(treeNode));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while(line != null){
            String[] list = line.split(" {2}");
            if(list[0].equals("Blob")){
                FileInputStream is = new FileInputStream(filePath+"\\"+list[1]);
                FileOutputStream os = new FileOutputStream(recursivePath+"\\"+list[2]);
                byte[] buffer = new byte[1024];
                int numRead = 0;
                while (numRead != -1) {
                    numRead = is.read(buffer);
                    //缓冲区读入numRead数量的字符
                    if (numRead > 0)
                        os.write(buffer, 0, numRead);
                }
                is.close();
                os.close();
            }
            else if(list[0].equals("Tree")){
                File f = new File(recursivePath + "\\" + list[2]);
                if (!f.exists())
                    f.mkdir();
                rb(list[1], recursivePath + "\\" + list[2]);  // 递归去深度优先遍历所有的文件
            }
            line = br.readLine();
        }
    }

    /**
     * 更改当前分支的commitID
     * @param newKey
     * @throws IOException
     */
    protected void changeBranch(String newKey) throws IOException {
        File HEADFile = new File(filePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        BufferedWriter branchOut = new BufferedWriter(new FileWriter(filePath + "\\branch\\" + str));
        branchOut.write(newKey);
        branchOut.flush();
        branchOut.close();
    }

    /**
     * 删除该文件夹下的所有文件
     * @param folder
     * @throws Exception
     */
    private void deleteFolder(File folder) throws Exception {
        if (!folder.exists()) {
            throw new Exception("文件不存在");
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    //递归直到目录下没有文件
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}
