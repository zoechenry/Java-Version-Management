public class test {

    /*
    该测试类用于测试各个模块是否能正常运行，现在还比较简陋。
    已经将任务分配给组员，后期会形成集成化的自动检测方法。
     */

    public static void main(String[] args) throws Exception {
        //指定想要进行版本管理的文件夹路径
        String hashPath = "C:\\Users\\30346\\Desktop\\数据结构";
        //对指定文件路径进行版本管理
        String hashOfHomeFolder = KeyValueStorage.creatKeyValueOfPath(hashPath);
        System.out.println(hashOfHomeFolder);
    }
}
