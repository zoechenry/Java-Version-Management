import java.io.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class branch {
    private String branchName = "Main";
    private Commit cmt;
    private String storagePath;
    private String filePath;

    public branch(String branchName,File warehouse, String storagePath) throws Exception {
        this.branchName = branchName;
        this.storagePath = storagePath;
        cmt = new Commit(warehouse, storagePath);
        String latestCommit = cmt.getLatestCommit();
        if (latestCommit == null){
            createBranch(branchName);  // 更新（创建）HEAD文件，存储这次commit的Key
            createHead(branchName);
        }
        else{
            if (cmt.isValidCommit())
                updateBranch();
        }
    }

    /**
     * 以链表形式返回commit记录、对应的根结点hash值
     * @return
     * @throws Exception
     */
    public LinkedHashMap commitLog() throws Exception {
        LinkedHashMap result = new LinkedHashMap<String,String>();
        String commit_id = cmt.getLatestCommit();
        do {
            result.put(commit_id,cmt.getInfoOfHomeFolder(commit_id,"hashOfHomeFolder"));
            commit_id = cmt.getInfoOfHomeFolder(commit_id,"Parent");
        }while (!commit_id.equals("null"));
        return result;
    }

    /**
     * 更新（创建）branch文件，存储这次commit的Key
     * @param str
     * @throws Exception
     */
    protected void createBranch(String str) throws Exception {
        String branchPath =  storagePath + "\\branch";
        File branch = new File(branchPath);
        if  (!branch.exists())
            branch.mkdir();
        BufferedWriter headOut = new BufferedWriter(new FileWriter(storagePath + "\\branch\\" + str));
        if (cmt.isValidCommit())
            headOut.write(cmt.getKey());
        else
            headOut.write(cmt.getLatestCommit());
        headOut.flush();
        headOut.close();
        System.out.println("创建分支成功！");
    }

    /**
     * 更改当前分支的commitID
     * @throws IOException
     */
    protected void updateBranch() throws IOException {
        File HEADFile = new File(storagePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        BufferedWriter branchOut = new BufferedWriter(new FileWriter(storagePath + "\\branch\\" + str));
        branchOut.write(cmt.getKey());
        branchOut.flush();
        branchOut.close();
    }

    /**
     * 更改当前分支的commitID
     * @param commitID
     * @throws IOException
     */
    protected void updateBranch(String commitID) throws IOException {
        File HEADFile = new File(storagePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        BufferedWriter branchOut = new BufferedWriter(new FileWriter(storagePath + "\\branch\\" + str));
        branchOut.write(commitID);
        branchOut.flush();
        branchOut.close();
    }

    /**
     * 输出所有分支，和当前分支名字
     * @throws IOException
     */
    protected void branchList() throws IOException {
        File[] files = new File(storagePath + "\\branch").listFiles();
        System.out.println("目前已有分支：");
        for (File f : files)
            System.out.println(f.getName());
        File HEADFile = new File(storagePath + "\\HEAD");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(HEADFile));
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        System.out.println("当前所在分支：" + str);
    }

    /**
     * 更新（创建）HEAD文件，存储当前branch
     * @param str
     * @throws IOException
     */
    protected void createHead(String str) throws IOException {
        File head = new File(storagePath + "\\HEAD");
        BufferedWriter headOut = new BufferedWriter(new FileWriter(storagePath + "\\HEAD"));
        headOut.write(str);
        headOut.flush();
        headOut.close();
    }

    /**
     * 切换分支
     * @param branchName
     * @return
     * @throws IOException
     */
    public boolean checkoutBranch(String branchName) throws IOException {
        File[] files = new File(storagePath + "\\branch").listFiles();
        for (File f : files)
            if (f.getName().equals(branchName)){
                createHead(branchName);
                return true;
            }
        return false;
    }


    /**
     * 回滚部分
     *     目前实现功能：根据HEAD文件寻找所有的Commit，
     *     选定一条Commit记录后，
     *
     *     根据这条commit记录的根节点hash值深度优先遍历整个树结构，
     *     恢复所有的文件夹及文件夹中的文件
     */

    /**
     * 初始化rollBack对象时，保证有一次最新的提交
     * @param path
     * @throws Exception
     */
    public void rollBack(String path) throws Exception {
        filePath = path;
        LinkedHashMap cmtlog = commitLog();
        System.out.println("Commit ID：" + cmtlog.keySet());
        System.out.println("输入Commit ID：");
        Scanner input = new Scanner(System.in);
        String commitID = input.nextLine();
        if (!cmtlog.containsKey(commitID)){
            System.out.println("commitID 不正确！");
            return;
        }
        String treeRoot = (String) cmtlog.get(commitID);

        // 文件夹非空，先删除文件
        deleteFolder(new File(filePath));

        // 根据根节点在.versionManagement下寻找文件
        rb(treeRoot, filePath);
        // 最后头指针存的commit值要变
        updateBranch(commitID);
        System.out.println("回滚完成！");
    }


    /**
     * 递归恢复文件
     * @param filename
     * @param recursivePath
     * @throws Exception
     */
    private void rb(String filename, String recursivePath) throws Exception{
        String filenamePath = storagePath +"\\"+ filename;
        File treeNode = new File(filenamePath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(treeNode));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while(line != null){
            String[] list = line.split(" {2}");
            if(list[0].equals("Blob")){
                FileInputStream is = new FileInputStream(storagePath+"\\"+list[1]);
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
                if (!file.getName().equals(".versionManagement")){
                    if (file.isDirectory()) {
                        //递归直到目录下没有文件
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        folder.delete();
    }
}
