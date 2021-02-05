package mystok;


import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


//DeleteObjectの使い方                                                                                                                      
//                                                                                                                                          
//1.インスタンス化
//DeleteObject deo = new DeleteObject();
//
//2.メソッド呼び出し
//deo.deleteObject("000001");
//第一引数は"削除対象のファイル名"


public class DeleteObject {
  public static void deleteObject(String objectName) {
    // The ID of your GCP project
    String projectId = "my-kubernetes-test-20200822";

    // The ID of your GCS bucket
    String bucketName = "mystok-bucket";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    storage.delete(bucketName, objectName);

    System.out.println("Object " + objectName + " was deleted from " + bucketName);
  }
}
