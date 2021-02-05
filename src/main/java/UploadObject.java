package mystok;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


//UploadObjectの使い方
//
//1.インスタンス化
//UploadObject uo = new UploadObject();
//
//2.メソッド呼び出し
//uo.uploadObject("000001","/usr/local/tomcat/webapps/mystok/Picture/RyouriPIC/ryouri000001.jpg");
//uo.uploadObject("000001","/usr/local/tomcat/webapps/mystok/Uploaded/000001.jpg");
//第一引数は"アップロード後の名前"
//第二引数は"アップロード対象ファイルへの絶対パス"



public class UploadObject {
  public static void uploadObject(String objectName, String filePath) throws IOException {
    //String projectId, String bucketName, String objectName, String filePath
    // The ID of your GCP project
    String projectId = "my-kubernetes-test-20200822";

    // The ID of your GCS bucket
    String bucketName = "mystok-bucket";

    // The ID of your GCS object
    //String objectName = objectName;

    // The path to your file to upload
    //String filePath = filePath;

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

    System.out.println(
        "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
  }
}
