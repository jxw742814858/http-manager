//package function;
//
//import cn.iamtudou.entity.HttpEntity;
//import cn.iamtudou.entity.ProxyEntity;
//import cn.iamtudou.kit.Request;
//import cn.iamtudou.kit.StrKit;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.junit.Test;
//
//import java.nio.charset.Charset;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class ProxyTest {
//    final String[] SOURCE_URLS = {
//            "http://www.xicidaili.com/nt/",
//            "http://www.xicidaili.com/nt/2",
//            "http://www.xicidaili.com/nt/3",
//            "http://www.xicidaili.com/nt/4",
//            "http://www.xicidaili.com/nt/5"
//    };
//
//    Map<String, String> header = new HashMap<>();
//
//
//    final String VALIDATE_URL = "https://www.baidu.com";
//
//    @Test
//    public void enter() {
//        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0");
//
//        ExecutorService service = Executors.newFixedThreadPool(SOURCE_URLS.length);
//        CountDownLatch latch = new CountDownLatch(SOURCE_URLS.length);
//
//        System.out.println();
//        for (int i = 0, length = SOURCE_URLS.length; i < length; i++) {
//            String url = SOURCE_URLS[i];
//            service.execute(() -> {
//                parsePage(url);
//                latch.countDown();
//            });
//
////            parsePage(SOURCE_URLS[index]);
//        }
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("parse finished...");
//    }
//
//    private void parsePage(String url) {
//        try {
//            String html = Request.get(url, header, Charset.defaultCharset()).getHtml();
//            if (StrKit.isBlank(html)) {
//                System.out.println(url + " request failed");
//                return;
//            }
//
//            Document doc = Jsoup.parse(html);
//            Elements listElms = doc.select("#ip_list tr");
//            listElms.remove(0); //去除表头栏
//
//            for (Element et : listElms) {
//                String ip = et.select("td").get(1).text();
//                int port = Integer.valueOf(et.select("td").get(2).text());
//                ProxyEntity proxy = new ProxyEntity(ip, port);
//                if (normalProxy(proxy))
//                    System.out.println(ip + "----" + port);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //可用校验
//    private boolean normalProxy(ProxyEntity proxy) {
//        try {
//            HttpEntity httpEntity = Request.head(VALIDATE_URL, proxy);
//            if (null != httpEntity && httpEntity.getResponseCode() == 200)
//                return true;
//        } catch (Exception e) {
//        }
//
//        return false;
//    }
//}
