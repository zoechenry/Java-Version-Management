import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class getHashValue {

    /*
    该类提供2个外部接口：
    public static String getHash(File file)
    对于打开的文件，获得其hash值并返回。
    该方法会自动判定这个文件是一个文件还是一个文件夹。
    请确保文件是真实存在的，否则会报错。

     public static String getHashValueFromStringBuilder(StringBuilder tempFile)
     输入StringBuilder返回hash值
     */

    //获取文件的hash值（不提供外部调用）
    private static String getHashOfFile(File file) throws Exception{
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
        return getHashValueFromMessageDigest(complete);
    }

    //获取文件夹的hash值（不提供外部调用）
    private static String getHashOfDirectory(File file) throws Exception {
        StringBuilder tempFile = new StringBuilder();
        File[] files = file.listFiles();
        for (File f: files){
            //将下属的不是版本管理文件夹的文件条目更新到Tree文件
            if (!f.getName().equals(".versionManagement"))
                updateEntry(f,tempFile);
        }
        return getHashValueFromStringBuilder(tempFile);
    }

    //获得子文件的可以写入Tree文件的条目（不提供外部调用）
    private static void updateEntry(File f,StringBuilder tempFile)throws Exception{
        byte fileType[] = new byte[6];
        //是文件夹，保存为Tree
        if (f.isDirectory())
            fileType = "Tree  ".getBytes();
            //是文件，保存为Blob
        else if (f.isFile())
            fileType = "Blob  ".getBytes();
        //写入文件的类型
        for (byte i : fileType)
            tempFile.append(i);
        //写入文件夹的hash值
        String hashValue;
        if (f.isFile())
            hashValue = getHashOfFile(f);
        else
            hashValue = getHashOfDirectory(f);
        for (byte i : hashValue.getBytes())
            tempFile.append(i);
        //写入文件名称
        byte filename[] = ("  " + f.getName() + "\n").getBytes();
        for (byte i : filename)
            tempFile.append(i);
    }

    //从StringBuilder中计算hash值
    public static String getHashValueFromStringBuilder(StringBuilder tempFile) throws NoSuchAlgorithmException {
        byte[] buffer = tempFile.toString().getBytes();
        //创建MessageDigest
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        complete.update(buffer,0, buffer.length);
        //将计算出的hash值转换为16进制输出
        return getHashValueFromMessageDigest(complete);
    }

    //将消息队列中的hash值取出（不提供外部调用）
    private static String getHashValueFromMessageDigest(MessageDigest complete){
        StringBuilder result = new StringBuilder();
        for (byte b : complete.digest()) {
            String temp = Integer.toString(b & 0xFF, 16);
            if(temp.length()==1)
                result.append('0');
            result.append(temp);
        }
        return result.toString();
    }

    //整合的计算hash值的方法（可调用）
    public static String getHash(File file) throws Exception {
        if(file.isFile())
            return getHashOfFile(file);
        else
            return getHashOfDirectory(file);
    }
}