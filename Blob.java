import java.io.File;


public class Blob extends KeyValue {
    /**
     * 继承自虚类key_value, 用于创建文件的key-value文件
     * @param inputFile
     * @param storagePath
     * @throws Exception
     */
    public Blob(File inputFile, String storagePath) throws Exception {
        super(inputFile, storagePath);
        createKeyValue();//创建Blob文件
    }
}
