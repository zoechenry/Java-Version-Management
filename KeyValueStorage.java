import java.io.*;
import java.security.MessageDigest;
import java.util.NoSuchElementException;


public class KeyValueStorage {
    private String storagePath;

    //构造一个KeyValueStorage对象，并指定版本管理用于存储key-value的位置
    KeyValueStorage(String storagePath){
        this.storagePath = storagePath;
    }

    //若没有指定版本管理用于存储key-value的位置，默认位置设置为D盘的根目录
    KeyValueStorage(){
        this("D:\\");
    }

    //修改版本管理用于存储key-value的位置
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    //获取版本管理用于存储key-value的位置
    public String getStoragePath() {
        return storagePath;
    }

    //获取文件的hash值（不提供外部调用）
    private StringBuilder getHashOfFile(File file) throws Exception{
        //创建MessageDigest
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        //创建一个缓冲区计算hash值
        byte[] buffer = new byte[1024];
        int numRead=0;
        FileInputStream is = new FileInputStream(file);
        while (numRead!=-1){
            numRead = is.read(buffer);
            //缓冲区读入numRead数量的字符
            if (numRead>0)
                complete.update(buffer,0,numRead);
        }
        is.close();
        //将计算出的hash值转换为16进制输出
        byte[] sha1 = complete.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : sha1)
            result.append(Integer.toString(b & 0xFF, 16));
        return result;
    }

    //获取文件夹的hash值（不提供外部调用）
    private  StringBuilder getHashOfDirectory(File file) throws Exception {
        //创建MessageDigest
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        StringBuilder tempFile = new StringBuilder();
        File[] files = file.listFiles();
        for (File f: files){
            byte fileType[] = new byte[6];
            //是文件夹，保存为Tree
            if(f.isDirectory())
                fileType = "Tree  ".getBytes();
                //是文件，保存为Blob
            else if(f.isFile())
                fileType = "Blob  ".getBytes();
            //写入文件的类型
            for(byte i:fileType)
            tempFile.append(i);
            //写入文件夹的hash值
            StringBuilder hashValue = new StringBuilder();
            if(f.isFile())
                hashValue = getHashOfFile(f);
            else if(f.isDirectory())
                hashValue = getHashOfDirectory(f);
            for(byte i:hashValue.toString().getBytes())
                tempFile.append(i);
            //写入文件名称
            byte filename[] = ("  "+f.getName()+"\n").getBytes();
            for(byte i:filename)
                tempFile.append(i);
        }
        byte[] buffer = tempFile.toString().getBytes();
        complete.update(buffer,0, buffer.length);
        //将计算出的hash值转换为16进制输出
        byte[] sha1 = complete.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : sha1)
            result.append(Integer.toString(b & 0xFF, 16));
        return result;
    }

    //整合的计算hash值的方法
    private StringBuilder getHashValue(File file) throws Exception {
        if(file.isFile())
            return getHashOfFile(file);
        else
            return getHashOfDirectory(file);
    }

    //创建Tree模块的Key-Value文件（不提供外部调用）
    private boolean creatTreeKeyValueFile(File file) throws Exception{
        //新建文件
        File keyValueFile = new File(storagePath, "Tree_newTree");
        FileOutputStream os = new FileOutputStream(keyValueFile);
        File[] files = file.listFiles();
        //将每一个子文件的类型、hash值、文件名写入文件中，一个一行
        for (File f: files){
            byte fileType[] = new byte[6];
            //是文件夹，保存为Tree
            if(f.isDirectory())
                fileType = "Tree  ".getBytes();
                //是文件，保存为Blob
            else if(f.isFile())
                fileType = "Blob  ".getBytes();
            //写入文件的类型
            os.write(fileType, 0, fileType.length);
            //写入文件的hash值
            byte hashValue[] = getHashValue(f).toString().getBytes();
            os.write(hashValue, 0, hashValue.length);
            //写入文件名称
            byte filename[] = ("  "+f.getName()+"\n").getBytes();
            os.write(filename, 0, filename.length);
        }
        os.close();
        //将新建的文件重命名为文件的hash值
        keyValueFile.renameTo(new File(storagePath, getHashValue(file).toString()));
        //操作成功，返回True
        return true;
    }

    //创建Blob模块的key-value文件（不提供外部调用）
    private boolean createBlobKeyValueFile(File file) throws Exception{
        //若Blob文件已经存在，直接跳过，返回True
        if(isValuableKey(getHashValue(file).toString()))
            return true;
        //若还没有对应的Blob文件，新建一个
        File keyValueFile = new File(storagePath, getHashValue(file).toString());
        //使用缓冲区将Blob文件的内容复制
        FileInputStream is = new FileInputStream(file);
        FileOutputStream os = new FileOutputStream(keyValueFile);
        byte[] buffer = new byte[1024];
        int numRead=0;
        while (numRead!=-1){
            numRead = is.read(buffer);
            //buffer get characters at the number of numRead
            if (numRead>0)
                os.write(buffer, 0 , numRead);
        }
        is.close();
        os.close();
        //操作成功，返回True
        return true;
    }

    //添加对应的key-value存储（可调用）
    public boolean creatKeyValueFile(File file) throws Exception {
        //若输入为文件夹,创建文件，将文件夹内的文件列表写如其中，并保存为一个文件
        if(file.isDirectory())
            return creatTreeKeyValueFile(file);
        //若输入为文件,创建文件，将原文件的内容完全复制过来
        else if (file.isFile())
            return createBlobKeyValueFile(file);
        else
            //操作失败，返回False
            return false;
    }

    //利用递归，保存所属的全部文件与文件夹的key-value文件（可调用）
    public void creatAllKeyValueFile(File file) throws Exception{
        //若输入为文件夹,创建文件夹的key-value文件，并递归创建每一个子文件的key-value文件
        if(file.isDirectory()){
            File[] files = file.listFiles();
            //递归创建每一个子文件的key-value文件
            for (File f: files)
                creatAllKeyValueFile(f);
            //创建文件夹的key-value文件
            creatKeyValueFile(file);
        }
        else if (file.isFile())
            //基类，创建文件的key-value文件
            creatKeyValueFile(file);
    }

    //判断给定的key是否对应一个有效的key-value文件（可调用）
    public boolean isValuableKey(String key){
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
    public File getValue(String key){
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
