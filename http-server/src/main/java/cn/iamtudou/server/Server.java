package cn.iamtudou.server;

import cn.iamtudou.kit.EscapeKit;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private static final String ENTER = "\r\n";
    private static final String SPACE = " ";

    ExecutorService es = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void start() {
        try {
            ServerSocket ss = new ServerSocket(8081);
            while (true) {
                Socket client = ss.accept();
                es.execute(() -> receive(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive(Socket client) {
        try (InputStream inputStream = client.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStream outputStream = client.getOutputStream()) {
            byte[] bytes = new byte[1024];

            client.setSoTimeout(10);
            int len = 0;
            try {
                while ((len = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, len);
                    baos.flush();
                }
            } catch (Exception e) {
            }

            System.out.println(baos.toString());
            String host = client.getInetAddress().getHostName();
            response(outputStream, baos.toString(), host);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void response(OutputStream outputStream, String httpMsg, String host) {
        try (BufferedWriter respWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            StringBuilder contentText = new StringBuilder();
            contentText.append(baseAnalysis(httpMsg, host));
            StringBuilder sb = new StringBuilder();

            /*通用头域begin*/
            sb.append("HTTP/1.1").append(SPACE).append("200").append(SPACE).append("OK").append(ENTER);
            sb.append("Server: myServer").append(SPACE).append("0.0.1v").append(ENTER);
            sb.append("Date: ").append(new Date()).append(ENTER);
            sb.append("Content-Type: text/html; charset=UTF-8").append(ENTER);
            sb.append("Content-Length: ").append(contentText.toString().getBytes().length).append(ENTER);
            /*通用头域end*/
            sb.append(ENTER);//正文开始标识
            sb.append(contentText);//正文部分

            respWriter.write(sb.toString());
            respWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * header 参数解析
     *
     * @param headerStr
     * @return
     */
    private String headerAnalysis(String headerStr) {
        // 分割消息头为数组
        String[] headerArr = headerStr.split("\r\n");

        // 将数组从第三个元素开始至最后一个元素, 创建为新的集合, 集合内容为 header 参数
        List<String> headParam = new ArrayList<>();
        for (int i = 2, length = headerArr.length; i < length; i++) {
            headParam.add(headerArr[i]);
        }
        // 键值分割得到格式化后字符串
        String formatStr = spellingFromSplitArray(headParam.toArray(new String[headParam.size()]), ":");

        return formatStr;
    }

    /**
     * 完整消息解析
     *
     * @param reqStr
     * @param origin
     * @return
     */
    private String baseAnalysis(String reqStr, String origin) {
        String getModel = "{\"args\":{%s},\"headers\":{%s},\"origin\":\"%s\",\"url\":\"%s\"}";
        String postModel = "{\"args\": {}, \"data\": \"%s\", \"files\": {%s}, \"form\": {%s}, \"headers\": {%s}," +
                " \"json\": %s, \"origin\": \"%s\", \"url\": \"%s\"}";

        // 分割出消息头, 即 baseArr[0]
        String[] baseArr = reqStr.split("\r\n\r\n");
        String argStr = "", headerStr = "", urlStr = "", dataStr = "", fileStr = "", formStr = "", jsonStr = "null";
        headerStr = headerAnalysis(baseArr[0]);
        // 分割消息头为数组
        String[] partOneArr = baseArr[0].split("\r\n");

        if (partOneArr[0].contains("?")) {
            // 包含 args 参数的 get 请求

            // 取数组第一个元素中的 args 参数所在字符串
            String arg1Str = partOneArr[0].substring(partOneArr[0].indexOf("?") + 1,
                    partOneArr[0].lastIndexOf("HTTP") - 1);
            // 键值分割得到格式化后字符串
            argStr = spellingFromSplitArray(arg1Str.split("&"), "=");
        }

        if (baseArr.length > 1) {
            // 消息体解析
            if (baseArr[0].contains("Content-Type: multipart/form-data;boundary=")) {
                // 包含文件类型数据

                String boundary = getRegVal(reqStr, "boundary=(?<value>\\S+)\\s+");
                boundary = EscapeKit.convert(boundary);
                fileStr = fileExtract(reqStr, boundary);
            } else if (baseArr[0].contains("Content-Type: application/x-www-form-urlencoded")) {
                // 包含 form 表单数据
                formStr = spellingFromSplitArray(baseArr[1].split("&"), "=");
            } else if (baseArr[1].trim().length() > 1) {
                // post 请求参数为纯文本
                dataStr = baseArr[1].replace("\"", "\\\"");
                // json 值类型判断
                jsonStr = jsonJudge(baseArr[1]) ? baseArr[1] : null;
            }
        }

        String resultStr = null;
        if (partOneArr[0].trim().startsWith("GET")) {
            resultStr = String.format(getModel, argStr, headerStr, origin, urlStr);
        } else if (partOneArr[0].trim().startsWith("POST")) {
            resultStr = String.format(postModel, dataStr, fileStr, formStr, headerStr, jsonStr, origin, urlStr);
        }

        return resultStr;
    }

    /**
     * 按指定的分割字符, 分割和重新拼接数组中的元素
     *
     * @param paramArr  源数组
     * @param splitChar 数组分隔符
     * @return
     */
    private String spellingFromSplitArray(String[] paramArr, String splitChar) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = paramArr.length; i < length; i++) {
            String[] tmpArr = paramArr[i].split(splitChar);
            if (i == 0) {
                sb.append(String.format("\"%s\":\"%s\"", tmpArr[0], tmpArr[1].trim()));
            } else {
                sb.append(String.format(", \"%s\":\"%s\"", tmpArr[0], tmpArr[1].trim()));
            }
        }

        return sb.toString();
    }

    /**
     * json字符串类型判断
     *
     * @param str
     * @return
     */
    private boolean jsonJudge(String str) {
        try {
            JSONObject.parse(str);
        } catch (JSONException je) {
            try {
                JSONArray.parse(str);
            } catch (JSONException je1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 文件类型请求内容解析
     *
     * @param reqStr
     * @param boundary
     * @return
     */
    private String fileExtract(String reqStr, String boundary) {
        String dataBody = getRegVal(reqStr, String.format("\r\n\r\n--%s(?<value>[\\S\\s]+)--%s--", boundary, boundary));
        String result = getFileStr(dataBody, "\\s+name=\"(?<name>.*?)\";.*?\\r\\n\\r\\n(?<value>.*?)\\r\\n");
        return result;
    }

    /**
     * 使用正则的匹配值获取
     *
     * @param str
     * @param reg
     * @return
     */
    private String getRegVal(String str, String reg) {
        String result = null;
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result = matcher.group("value");
        }

        return result;
    }

    /**
     * 文件类型文本内容提取和拼接
     *
     * @param str
     * @param reg
     * @return
     */
    private String getFileStr(String str, String reg) {
        StringBuffer result = new StringBuffer();
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            if (result.length() == 0) {
                result.append(String.format("\"%s\":\"%s\"", matcher.group("name"),
                        matcher.group("value")));
            } else {
                result.append(String.format(", \"%s\":\"%s\"", matcher.group("name"),
                        matcher.group("value")));
            }
        }

        return result.toString();
    }
}
