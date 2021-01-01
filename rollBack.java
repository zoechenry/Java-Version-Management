import java.io.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class rollBack {

    /*
    目前实现功能：根据HEAD文件寻找所有的Commit，
    选定一条Commit记录后，

    根据这条commit记录的根节点hash值深度优先遍历整个树结构，
    输出所有的文件夹即文件夹中的文件


     */
    private final String strPath;  // 创建git的路径
    private final String filePath; // 用于存储仓库.versionManagement的路径


    // 初始化rollBack对象时，保证有一次最新的提交
    public rollBack(LinkedHashMap commitLog, String commitID, String storagePath) throws Exception {
        strPath = storagePath;
        filePath = strPath + "\\.versionManagement";

        // 文件夹非空，先删除文件
        File[] files = new File(strPath).listFiles();
        for (File f: files){
            if (!f.getName().equals(".versionManagement"))
                deleteFolder(f);
        }

        String treeRoot = (String) commitLog.get(commitID);
        // 根据根节点在.versionManagement下寻找文件
        rb(treeRoot, strPath);
        // 最后头指针存的commit值要变
        changeHead(commitID);
    }

    private void changeHead(String commitID) throws IOException {
        BufferedWriter headOut = new BufferedWriter(new FileWriter(filePath + "\\HEAD"));
        headOut.write(commitID);
        headOut.flush();
        headOut.close();
    }

    private void rb(String filename, String recursivePath) throws Exception{
        String filenamePath = filePath +"\\"+ filename;
        File treeNode = new File(filenamePath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(treeNode));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while(line != null){
            String[] list = line.split(" {2}");
            if(list[0].equals("Blob")){
//                System.out.println("文件名："+list[2]);  // 如果是文件，就输出文件名
//                System.out.println("文件的值："+list[1]);  // 如果是文件，就输出文件名
//                System.out.println("文件的存储路径："+ recursivePath +"\n");
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

    public void deleteFolder(File folder) throws Exception {
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

    public static void main(String[] args) throws Exception {
        //指定想要创建key-value的文件路径或文件夹路径
        String hashPath = "C:\\Users\\zrc5\\Desktop\\test";

        // 创建一个.versionManagement文件夹
        VersionManagement folder = new VersionManagement(hashPath);
        // 提交一次commit，获得返回tree根节点的Hash值
        LinkedHashMap commitLog = folder.commit();

        System.out.println("Commit ID：" + commitLog.keySet());
        System.out.println("输入Commit ID："); // 写入备注:
        Scanner input = new Scanner(System.in);
        String commitID = input.nextLine();

        rollBack newTest = new rollBack(commitLog, commitID, hashPath);
    }
}
