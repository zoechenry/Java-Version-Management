import java.io.*;

public class test {

    /*
    该测试类用于测试各个模块是否能正常运行，现在还比较简陋。
    已经将任务分配给组员，后期会形成集成化的自动检测方法。
     */

    /*
    目前实现功能：根据HEAD文件寻找最新一次Commit，
    然后根据根节点的hash值深度优先遍历整个树结构，
    输出所有的文件夹即文件夹中的文件
     */

    private final String filePath; // 用于存储仓库.versionManagement的路径
    private static String latestCommit; // 用于存储最新的提交记录
    private StringBuilder filenameStorage; // 用于存储文件与文件夹的名字

    // 初始化test对象时，保证有一次最新的提交
    public test(String storagePath) throws Exception {
        filenameStorage = new StringBuilder();
        filePath = storagePath + "\\.versionManagement";;
        // 创建一个.versionManagement文件夹
        VersionManagement folder = new VersionManagement(storagePath);
        // 提交一次commit，获得返回tree根节点的Hash值
        latestCommit = folder.commit();
    }

    public void run() throws Exception {
        filenameStorage.append("根目录的文件地址：").append(latestCommit).append("\n");
        // 根据根节点在.versionManagement下寻找文件
        readTree(latestCommit);
        System.out.println(filenameStorage.toString());
    }

    private void readTree(String filename) throws Exception{
        String filenamePath = filePath +"\\"+ filename;
        File treeNode = new File(filenamePath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(treeNode));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while(line != null){
            String[] list = line.split(" {2}");
            if(list[0].equals("Blob"))
                filenameStorage.append("文件名：").append(list[2]).append("\n");  // 如果是文件，就输出文件名
            else if(list[0].equals("Tree")){
                filenameStorage.append("文件夹名：").append(list[2]).append(" ");    // 如果是树，就输出文件夹名字
                filenameStorage.append("{").append("\n");
                readTree(list[1]);  // 递归去深度优先遍历所有的文件
                filenameStorage.append("}").append("\n");
            }
            line = br.readLine();
        }
    }

    public static void main(String[] args) throws Exception {
        //指定想要创建key-value的文件路径或文件夹路径
        String hashPath = "C:\\Users\\zrc5\\Desktop\\test";

        // 初始化一个test对象
        test newTest = new test(hashPath);
        newTest.run();
    }
}
