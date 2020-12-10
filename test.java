import java.io.File;

//测试类，用于测试
public class test {
    public static void main(String[] args) throws Exception {
        //指定想要进行版本管理的文件夹路径
        String hashPath = "C:\\Users\\30346\\Desktop\\数据结构";
        //对指定文件路径进行版本管理
        String hashOfHomeFolder = KeyValueStorage.creatKeyValueOfPath(hashPath);
        System.out.println(hashOfHomeFolder);
    }
}
