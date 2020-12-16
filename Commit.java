import java.io.*;
import java.util.Scanner;

public class Commit extends KeyValue {

      /*
    继承自虚类key_value
    用于创建commit的key-value文件
    会对Tree类以及Blob类进行递归调用，创建整个仓库的key-value文件，并自动判断是否与前一次commit内容一样，若一样，则commit失败。
    创建HEAD文件，用于保存最近一次commit的key值
    commit内的文件包括：仓库tree对象的key、前驱commit对象的key、时间戳、备注
     */

    private String latestCommit;
    private String hashOfHomeFolder;
    private StringBuilder value;//保存Commit对象的value值，供计算key值和写入文件

    public Commit(File inputFile, String storagePath) throws Exception {
        super(inputFile, storagePath);
        if (isValidCommit()) {//判断此次commit是否合法
            updateValue();//更新commit的value
            createKeyValue(value);
            createHead();  // 更新（创建）HEAD文件，存储这次commit的Key
            System.out.println("更新成功！");
        } else
            System.out.println("无变化，不进行更新！");
    }

    // 判断该次的commit是否合法，及和上次相比HashOfHomeFolder是否有变化
    private boolean isValidCommit() throws Exception {
        latestCommit = getLatestCommit();
        String latestKeyOfHomeFolder = getInfoOfHomeFolder(latestCommit,"hashOfHomeFolder");//获取上一次commit的仓库key值
        hashOfHomeFolder = new Tree(inputFile,storagePath).getKey();//获取当前的仓库key值
        return !hashOfHomeFolder.equals(latestKeyOfHomeFolder);
    }

    //更新commit的value
    private void updateValue(){
        value = new StringBuilder();
        System.out.println("请输入备注："); // 写入备注:
        Scanner input = new Scanner(System.in);
        String commitNote = input.nextLine();
        value.append("hashOfHomeFolder: ").append(hashOfHomeFolder).append("\n"); // tree对象key
        value.append("Parent: ").append(latestCommit).append("\n"); // 前驱commit，若无则设为null
        value.append("Timestamp: ").append((int) System.currentTimeMillis()).append("\n"); // 时间戳
        value.append("Note: ").append(commitNote).append("\n"); // 备注
    }

}
