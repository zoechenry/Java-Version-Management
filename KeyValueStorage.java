import java.io.*;
import java.nio.file.NoSuchFileException;


public class KeyValueStorage {

    /*
    该类用于进行版本管理
    提供3个对外接口：
    （1）static String creatKeyValueOfPath(String filePath)
    对于指定的路径创建仓库，无效仓库路径会报错（路径无效或不是文件夹）。具体的建仓库流程包括：
        （1）在路径下生成一个名为“.versionManagement”的隐藏文件夹，所有的Tree与Blob的key-value值都会保存在里面；
        （2）对于文件夹中现存的所有文件与文件夹创建key-value文件并保存在上述文件夹中；
        （3）对仓库整体创建key-value文件并返回该Tree的hash值，用于和commit模块进行配合。
    （2）static boolean isValuableKey(String key,String storagePath)
    判定指定文件路径下是否包含文件名为指定key值的文件。
    （3）static File getValue(String key,String storagePath)
    在指定路径下查找给定key值对应的文件并返回。
    在调用该方法前最好先调用isValuableKey方法验证该key值是有效的，如果是无效的key值，则会返回一个临时生成的空文件。
     */

    //创建Tree模块的Key-Value文件（不提供外部调用）
    private static String creatTreeKeyValueFile(File file,String storagePath) throws Exception {
        //获取对应的hash值
        String hashValue = getHashValue.getHash(file);
        //新建文件
        File keyValueFile = new File(storagePath, hashValue);
        FileOutputStream os = new FileOutputStream(keyValueFile);
        File[] files = file.listFiles();
        //将每一个子文件的类型、hash值、文件名写入文件中，一个一行
        for (File f : files) {
            //将下属的不是版本管理文件夹的文件条目更新到Tree文件
            if (!f.getName().equals(".versionManagement")) {
                updateEntry(f,os);
            }
        }
        os.close();
        return hashValue;
    }

    //获得子文件的可以写入Tree文件的条目（不提供外部调用）
    private static void updateEntry(File f,FileOutputStream os)throws Exception{
        byte fileType[] = new byte[6];
        //是文件夹，保存为Tree
        if (f.isDirectory())
            fileType = "Tree  ".getBytes();
            //是文件，保存为Blob
        else if (f.isFile())
            fileType = "Blob  ".getBytes();
        //写入文件的类型
        os.write(fileType, 0, fileType.length);
        //写入文件的hash值
        byte hashValue[] = getHashValue.getHash(f).getBytes();
        os.write(hashValue, 0, hashValue.length);
        //写入文件名称
        byte filename[] = ("  " + f.getName() + "\n").getBytes();
        os.write(filename, 0, filename.length);
    }

    //创建Blob模块的key-value文件（不提供外部调用）
    private static void createBlobKeyValueFile(File file, String storagePath) throws Exception {
        //若Blob文件已经存在，直接跳过，返回True
        if (isValuableKey(getHashValue.getHash(file),storagePath))
            return;
        //若还没有对应的Blob文件，新建一个
        File keyValueFile = new File(storagePath, getHashValue.getHash(file));
        //使用缓冲区将Blob文件的内容复制
        FileInputStream is = new FileInputStream(file);
        FileOutputStream os = new FileOutputStream(keyValueFile);
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

    //添加对应的key-value存储（不提供外部调用）
    private static void creatKeyValueFile(File file, String storagePath) throws Exception {
        if (file.isDirectory())
            creatTreeKeyValueFile(file, storagePath);
        else
            createBlobKeyValueFile(file, storagePath);
    }

    //利用递归，保存所属的全部文件与文件夹的key-value文件（不提供外部调用）
    private static void creatAllKeyValueFile(File file,String storagePath) throws Exception{
        //若输入为文件夹,创建文件夹的key-value文件，并递归创建每一个子文件的key-value文件
        if(file.isDirectory()){
            File[] files = file.listFiles();
            //递归创建每一个子文件的key-value文件
            for (File f: files) {
                if (!f.getName().equals(".versionManagement"))
                    creatAllKeyValueFile(f, storagePath);
            }
            //创建文件夹的key-value文件
            creatKeyValueFile(file,storagePath);
        }
        else if (file.isFile())
            //基类，创建文件的key-value文件
            creatKeyValueFile(file,storagePath);
    }

    //对于仓库的版本管理的集成接口，对仓库生成key-value文件，并返回仓库的key值（可调用）
    public static String creatKeyValueOfPath(String filePath)throws Exception{
        //若是无效文件夹路径，则抛出异常
        File file = new File(filePath);
        if (!file.exists()||!file.isDirectory())
            throw new NoSuchFileException("无效的文件夹路径");
        String storagePath = filePath+"\\.versionManagement";
        //若版本管理文件夹不存在就创建一个
        File versionManagement = new File(storagePath);
        if  (!versionManagement.exists())
            versionManagement.mkdir();
        File[] files = file.listFiles();
        //递归创建除了版本管理文件夹".versionManagement"每一个附属文件的key-value文件
        for (File f: files) {
            if (!f.getName().equals(".versionManagement"))
                creatAllKeyValueFile(f, storagePath);
        }
        //创建主文件夹的key-value文件并返回其hash值
        return creatTreeKeyValueFile(file,storagePath);
    }

    //判断给定的key是否对应一个有效的key-value文件（可调用）
    public static boolean isValuableKey(String key,String storagePath){
        File keyValueFile = new File(storagePath);
        //列出全部的key-value文件
        File[] files = keyValueFile.listFiles();
        for (File f: files)
            //若找到key对应的文件，则返回True
            if(f.getName()==key) {
                return true;
            }
        //没有找到，返回false
        return false;
    }

    //给定key，查找对应的文件并返回（可调用）
    public static File getValue(String key,String storagePath){
        File keyValueFile = new File(storagePath);
        //列出全部的key-value文件
        File[] files = keyValueFile.listFiles();
        for (File f: files)
            //若找到key对应的文件，则返回这个文件
            if(f.getName()==key) {
                return f;
            }
        //没找到对应的文件，返回一个临时的空文件
        return (new File(storagePath,"temp"));
    }
}
