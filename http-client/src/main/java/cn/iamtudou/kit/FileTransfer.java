package cn.iamtudou.kit;

import cn.iamtudou.entity.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileTransfer {
    static Logger LOG = LoggerFactory.getLogger(FileTransfer.class);

    public static HttpEntity upload(String url, String[] filePaths) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        DataOutputStream dos = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer htmlBuff = null;
        String tmpLine = null;
        URL netUrl = null;
        HttpURLConnection conn = null;
        String filename = null;
        HttpEntity resEntity = null;

        try {
            netUrl = new URL(url);
            conn = (HttpURLConnection) netUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            for (int i = 0, length = filePaths.length; i < length; i++) {
                filename = filePaths[i].substring(filePaths[i].lastIndexOf("//") + 1);
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename
                        + "\"" + end);
                dos.writeBytes(end);
                FileInputStream fis = new FileInputStream(filePaths[i]);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = fis.read(bytes)) != -1)
                    dos.write(bytes, 0, len);
                dos.writeBytes(end);
                fis.close();
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            if (conn.getResponseCode() >= 300) {
                throw new IOException("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                htmlBuff = new StringBuffer();
                while ((tmpLine = br.readLine()) != null)
                    htmlBuff.append(tmpLine).append("\n");
            }

            resEntity = new HttpEntity(url, netUrl.getHost(), conn.getResponseCode(), htmlBuff.toString());
        } catch (IOException e) {
            LOG.error("", e);
        } finally {
            try {
                if (dos != null)
                    dos.close();
                if (is != null)
                    is.close();
                if (isr != null)
                    isr.close();
                if (br != null)
                    br.close();
                netUrl = null;
                if (conn != null)
                    conn.disconnect();
            } catch (IOException e) {
                LOG.error("a mistake in closing the resource! ", e);
            }

        }

        return resEntity;
    }

    public static String download(String urlPath, String dirPath) {
        File file = null;
        URL netUrl = null;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        BufferedInputStream bis = null;
        OutputStream out = null;
        String fileFullName = null;

        try {
            netUrl = new URL(urlPath);
            conn = (HttpURLConnection) netUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "UTF-8");
            conn.setRequestProperty("Content-Length", "0");
            String filePathUrl = conn.getURL().getFile();
            fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.write("".getBytes("UTF-8"), 0, 0);
            dos.flush();
            dos.close();

            bis = new BufferedInputStream(conn.getInputStream());
            String path = dirPath + File.separatorChar + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            out = new FileOutputStream(file);
            int size = 0, len = 0;
            byte[] bytes = new byte[1024];
            while ((size = bis.read(bytes)) != -1) {
                len += size;
                out.write(bytes, 0, size);
            }

            bis.close();
            out.close();
        } catch (IOException e) {
            LOG.error("", e);
            return "failed";
        } finally {
            netUrl = null;
            if (conn != null)
                conn.disconnect();
            file = null;
        }

        LOG.info("file -- {} save to [ {} ] success!", fileFullName, dirPath);
        return "success";
    }
}
