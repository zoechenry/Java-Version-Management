import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Tree extends key_value{

      /*
    继承自虚类key_value
    用于创建文件夹的key-value文件
    递归调用自身以及Blob类，完成自身以及子文件的key-value文件的创建。
     */

    private StringBuilder value;//保存Tree对象的value值，供计算key值和写入文件


    public Tree(File inputFile, String storagePath) throws Exception {
        super(inputFile, storagePath);
        createTree();
    }

    //创建Tree模块的Key-Value文件
    private void createTree() throws Exception{
        value = new StringBuilder();
        updateValue();//更新Tree的value
        key.setHashValue(value);//计算hash值
        File keyValueFile = new File(storagePath, getKey());//创建Tree文件
        BufferedWriter bw = new BufferedWriter(new FileWriter(keyValueFile));
        bw.write(value.toString());
        bw.flush();
        bw.close();
    }

    //更新Tree的value
    private void updateValue()throws Exception{
        File[] files = inputFile.listFiles();
        for (File f : files)
            if (!f.getName().equals(".versionManagement"))
                updateEntry(f);//将下属的不是版本管理文件夹的文件条目更新到value中，同时递归创建子文件的key-value
    }

    //更新value条目
    private void updateEntry(File f)throws Exception{
        if (f.isFile()) {
            value.append("Blob  ");//子项是文件，条目类型为Blob
            value.append(new Blob(f,storagePath).getKey());//新建子文件的Blob并返回key值,写入value中
        }
        else if (f.isDirectory()) {
            value.append("Tree  ");//子项是文件夹，条目类型为Tree
            value.append(new Tree(f,storagePath).getKey());//新建子文件夹的Tree并返回key值,写入value中
        }
        value.append("  ").append(f.getName()).append("\n");//写入文件名称
    }
}
