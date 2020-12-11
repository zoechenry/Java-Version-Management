import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;


public class KeyValueStorage {
    //创建Tree模块的Key-Value文件（不提供外部调用）
    private static void createTreeKeyValueFile(File file,String storagePath) throws Exception {
        //新建文件
        File keyValueFile = new File(storagePath, getHashValue.getHash(file));
        FileOutputStream os = new FileOutputStream(keyValueFile);
        File[] files = file.listFiles();
        //将每一个子文件的类型、hash值、文件名写入文件中，一个一行
        for (File f : files) {
            updateEntry(f,os);
        }
        os.close();
    }

    //创建主文件夹的Key-Value文件（不提供外部调用）
    private static String createTreeKeyValueFileOfHomeFolder(File file,String storagePath) throws Exception {
        //获取主文件夹的hash值
        String hashOfHomeFolder=getHashValue.getHashOfHomeFolder(file);
        //新建文件
        File keyValueFile = new File(storagePath,hashOfHomeFolder);
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
        return hashOfHomeFolder;
    }

    //获得子文件的可以写入Tree文件的条目
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
    private static void createKeyValueFile(File file, String storagePath) throws Exception {
        if (file.isDirectory())
            createTreeKeyValueFile(file, storagePath);
        else
            createBlobKeyValueFile(file, storagePath);
    }

    //利用递归，保存所属的全部文件与文件夹的key-value文件（可调用）
    private static void createAllKeyValueFile(File file,String storagePath) throws Exception{
        //若输入为文件夹,创建文件夹的key-value文件，并递归创建每一个子文件的key-value文件
        if(file.isDirectory()){
            File[] files = file.listFiles();
            //递归创建每一个子文件的key-value文件
            for (File f: files)
                createAllKeyValueFile(f,storagePath);
            //创建文件夹的key-value文件
            createKeyValueFile(file,storagePath);
        }
        else if (file.isFile())
            //基类，创建文件的key-value文件
            createKeyValueFile(file,storagePath);
    }

    public static String createKeyValueOfPath(String filePath)throws Exception{
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
                createAllKeyValueFile(f, storagePath);
        }
        //创建主文件夹的key-value文件并返回其hash值
        return createTreeKeyValueFileOfHomeFolder(file,storagePath);
    }

    //判断给定的key是否对应一个有效的key-value文件（可调用）
    public static boolean isValuableKey(String key,String storagePath){
        File keyValueFile = new File(storagePath);
        //列出全部的key-value文件
        File[] files = keyValueFile.listFiles();
        for (File f: files)
            //若找到key对应的文件，则返回True
            if(f.getName().equals(key)) {
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
            if(f.getName().equals(key)) {
                return f;
            }
        //没找到对应的文件，返回一个临时的空文件
        return (new File(storagePath,"temp"));
    }
}
