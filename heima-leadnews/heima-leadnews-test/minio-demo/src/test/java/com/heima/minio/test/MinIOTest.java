package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    //把list.html文件上传到minio中，并且可以在浏览器中访问
    @Test
    public void test() throws Exception{
        FileInputStream fileInputStream =  new FileInputStream("C:\\Users\\86177\\Desktop\\学习\\ftl\\list.html");
        String path = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(path);
    }






    /**
     * 把list.html文件上传到minio中，并且可以在浏览器中访问
     * @param args
     * */
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        try {

            //fileInputStream =  new FileInputStream("C:\\Users\\86177\\Desktop\\学习\\ftl\\list.html");
            fileInputStream =  new FileInputStream("D:\\百度网盘下载的文件\\黑马头条\\day02-app端文章查看，静态化freemarker,分布式文件系统minIO\\资料\\模板文件\\plugins\\js\\index.js");

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    //.object("list.html")//文件名
                    //.contentType("text/html")//文件类型
                    .object("plugins/js/index.js")
                    .contentType("text/js")
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();
            minioClient.putObject(putObjectArgs);

            //访问路径
            //System.out.println("http://192.168.200.130:9000/leadnews/ak47.jpg");
            //System.out.println("http://192.168.200.130:9000/leadnews/list.html");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}