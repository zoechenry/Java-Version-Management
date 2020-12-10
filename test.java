import java.io.File;


//测试类，用于测试
public class test {
    public static void main(String[] args) throws Exception {
        //指定key-value的存储位置
        String path = "D:\\test";
        //以指定的path创建key-value生成类
        KeyValueStorage keyValueStorage = new KeyValueStorage(path);
        //指定想要创建key-value的文件路径或文件夹路径
        String hashPath = "C:\\Users\\30346\\Desktop\\数据结构";
        //打开文件或文件夹
        File file = new File(hashPath);
        //创建对应的key-value文件，保存在指定的path
        keyValueStorage.creatAllKeyValueFile(file);
    }
}
