package function;

import cn.iamtudou.entity.HttpEntity;
import cn.iamtudou.entity.ProxyEntity;
import cn.iamtudou.kit.FileTransfer;
import cn.iamtudou.kit.Request;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class FunctionTest {
    public FunctionTest() {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void get() {
//        ProxyEntity proxy = new ProxyEntity("192.168.155.155", 25, "yproxyq", "zproxyx");
//        ProxyEntity proxy = new ProxyEntity("123.131.44.228", 9999);
        HttpEntity entity = Request.get("http://localhost:8081?username=username&password=password");
//        HttpEntity entity = Request.get("http://httpbin.org/get");
        System.out.println();
    }

    @Test
    public void post() {
        String dataStr = "{\"host\":\"\",\"account\":\"\",\"passwd\":\"\",\"area\":\"\",\"type\":1,\"status\":1}";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "multipart/form-data; boundary=--***");
        HttpEntity entity = Request.post("http://localhost:8081", header, dataStr);
        System.out.println();
    }

    @Test
    public void head() {
        HttpEntity entity = Request.head("https://www.baidu.com");
        System.out.println();
    }

    @Test
    public void download() {
        String urlPath = "http://d.hiphotos.baidu.com/image/pic/item/63d0f703918fa0ec53d199aa2a9759ee3d6ddb07.jpg";
        String dirPath = "/home/jiangxw/下载";

        FileTransfer.download(urlPath, dirPath);
        System.out.println();
    }

    public String getFileStr() {
        String fileName = "ip.txt";
        InputStream inputStream = FunctionTest.class.getClassLoader().getResourceAsStream(fileName);
        try {
            return inputStream2String(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    @Test
    public void upload() {
        String[] paths = {"/home/jiangxw/test.txt", "/home/jiangxw/test1.txt"};
//        HttpEntity entity = FileTransfer.upload("http://httpbin.org/post", paths);
        HttpEntity entity = FileTransfer.upload("http://localhost:8081", paths);
        System.out.println();
    }
}
