import java.io.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class test {

    /*
    目前实现功能：根据HEAD文件寻找所有的Commit，
    选定一条Commit记录后，

    根据这条commit记录的根节点hash值深度优先遍历整个树结构，
    输出所有的文件夹即文件夹中的文件

    之后同样的深度优先遍历的方式遍历文件夹，得到遍历结果后比较两次是否有差异（如果选定的不是最新commit，则验证失败）

     */
    private final String strPath;  // 创建git的路径
    private final String filePath; // 用于存储仓库.versionManagement的路径
    private String commitID; // 用于存储最新的提交记录
    private String treeRoot; // 用于存放根节点的Hash值
    private StringBuilder filenameStorage; // 用于存储文件与文件夹的名字
    private StringBuilder filenameCheck = new StringBuilder();

    // 初始化test对象时，保证有一次最新的提交
    public test(String storagePath) throws Exception {
        strPath = storagePath;
        filenameStorage = new StringBuilder();
        filePath = strPath + "\\.versionManagement";
        // 创建一个.versionManagement文件夹
        VersionManagement folder = new VersionManagement(strPath);
        // 提交一次commit，获得返回tree根节点的Hash值
        LinkedHashMap commitLog = folder.commit();

        System.out.println("Commit ID：" + commitLog.keySet());

        System.out.println("输入Commit ID："); // 写入备注:
        Scanner input = new Scanner(System.in);
        commitID = input.nextLine();
        treeRoot = (String) commitLog.get(commitID);
    }

    public void run() throws Exception {
        System.out.println("根目录的文件地址："+treeRoot);
        // 根据根节点在.versionManagement下寻找文件
        readTree(treeRoot);
        System.out.println(filenameStorage.toString());

//        checkFile(); // 验证文件完整性
    }

    private void checkFile(){
        File treeNode = new File(strPath);
        readFolder(treeNode);

        // 最后检查两次遍历的结果是否相同
        if((filenameCheck.toString().equals(filenameStorage.toString())))
            System.out.println("验证成功！");
        else
            System.out.println("验证失败！");
    }

    private void readTree(String filename) throws Exception{
        String filenamePath = filePath +"\\"+ filename;
        File treeNode = new File(filenamePath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(treeNode));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while(line != null){
            String[] list = line.split(" {2}");
            if(list[0].equals("Blob")){
                filenameStorage.append("文件名：").append(list[2]).append("\n");  // 如果是文件，就输出文件名
            }
            else if(list[0].equals("Tree")){
                filenameStorage.append("文件夹名：").append(list[2]).append(" ");    // 如果是树，就输出文件夹名字
//                filenameStorage.append("{").append("\n");
                readTree(list[1]);  // 递归去深度优先遍历所有的文件
//                filenameStorage.append("}").append("\n");
            }
            line = br.readLine();
        }
    }

    // 深度优先遍历（相同的遍历方式）文件夹内的文件，结果存在filenameCheck中
    private void readFolder(File file){
        File[] files = file.listFiles();
        if(files!=null) {
            for (File f : files){
                if (!f.getName().equals(".versionManagement")){
                    if(f.isFile()){
                        filenameCheck.append("文件名：").append(f.getName()).append("\n");
                    }
                    else if(f.isDirectory()){
                        String newTree = f.getName();
                        filenameCheck.append("文件夹名：").append(newTree).append(" {").append("\n");
                        readFolder(f);
                        filenameCheck.append("}").append("\n");
                    }
                }
            }
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
