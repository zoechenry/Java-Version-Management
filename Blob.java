import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Blob extends key_value {

    /*
    继承自虚类key_value
    用于创建文件的key-value文件
     */

    public Blob(File inputFile, String storagePath) throws Exception {
        super(inputFile, storagePath);
        createBlob();//创建Blob文件
    }

    //创建Blob模块的Key-Value文件
    private void createBlob() throws Exception {
        key.setHashValue(inputFile);//计算key值
        if(isValuableKey())//若Blob文件已经存在，直接返回
            return;
        File keyValueFile = new File(storagePath, getKey());//若还没有对应的Blob文件，新建一个
        //使用缓冲区写入Blob文件
        FileInputStream is = new FileInputStream(inputFile);
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
}
