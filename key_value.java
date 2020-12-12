import java.io.File;

public abstract class key_value {

    /*
    虚类，供包括Blob、Tree、Commit在内的其他类继承，包括了一些通用的变量和方法。
    变量包括：进行操作的文件对象、key-value存储路径以及计算key值的Hash对象。
    方法包括：从Hash对象中取出key、检查某个key是否以及存在key-value、根据key查找value并返回
     */

    protected File inputFile;
    protected String storagePath;
    protected Hash key;

    public key_value(File inputFile, String storagePath){
        this.inputFile = inputFile;
        this.storagePath = storagePath;
        this.key = new Hash();
    }

    public String getKey(){
        return key.getHashValue();
    }

    //判断给定的key是否对应一个有效的key-value文件
    protected boolean isValuableKey(){
        String hash = getKey();
        File[] files = new File(storagePath).listFiles();//列出全部的key-value文件
        for (File f: files) {
            if (f.getName().equals(hash))
                return true;
        }
        return false;
    }

    //给定key，查找对应的文件并返回（可调用）
    public File getValue(){
        String hash = getKey();
        File[] files = new File(storagePath).listFiles();//列出全部的key-value文件
        for (File f: files)
            if(f.getName().equals(hash))//若找到key对应的文件，则返回这个文件
                return f;
        //没找到对应的文件，返回一个临时的空文件
        return (new File(storagePath,"temp"));
    }

}
