import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VersionManagement {

    /*
    版本管理类，对于仓库的所有操作都会从这里进行接入。
    初始化时自动完成仓库的创建。
    当前仅提供一个接口：commit，可以进行版本管理。
     */

    private File warehouse;
    private String storagePath;

    public VersionManagement(String warehousePath) throws Exception {
        warehouse = new File(warehousePath);
        if (!warehouse.exists()||!warehouse.isDirectory())//若是无效的仓库路径，则抛出异常
            throw new NoSuchFileException("无效的仓库路径");

        storagePath = warehousePath+"\\.versionManagement";
        File versionManagement = new File(storagePath);
        if  (!versionManagement.exists())
            versionManagement.mkdir();//若还没有创建".versionManagement"文件夹，则创建一个
    }

    public File getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehousePath)throws Exception {
        warehouse = new File(warehousePath);
        if (!warehouse.exists()||!warehouse.isDirectory())//若是无效的仓库路径，则抛出异常
            throw new NoSuchFileException("无效的仓库路径");

        storagePath = warehousePath+"\\.versionManagement";
        File versionManagement = new File(storagePath);
        if  (!versionManagement.exists())
            versionManagement.mkdir();//若还没有创建".versionManagement"文件夹，则创建一个
    }

    public LinkedHashMap commit() throws Exception {
        Commit cmt = new Commit(warehouse, storagePath);
        LinkedHashMap result = new LinkedHashMap<String,String>();
        String commit_id = cmt.getLatestCommit();
        do {
            result.put(commit_id,cmt.getInfoOfHomeFolder(commit_id,"hashOfHomeFolder"));
//            System.out.println("Commit ID："+commit_id);
            commit_id = cmt.getInfoOfHomeFolder(commit_id,"Parent");
        }while (!commit_id.equals("null"));
        return result;
    }
}
