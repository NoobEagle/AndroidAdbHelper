package com.meta.adbhelper.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpUtil {

    private static HashMap<String, String> cacheMap = new HashMap<>();

    /**
     * 下载网络文件
     *
     * @param fileName    存储地址
     * @param downloadUrl 下载地址
     * @return 下载后的文件位置(绝对路径) 如下载失败，则返回空字符串
     */
    public static String downloadNet(File fileName, String downloadUrl) {
        return downloadNet(fileName.getAbsolutePath(), downloadUrl);
    }


    /**
     * 下载网络文件
     *
     * @param fileName    存储地址
     * @param downloadUrl 下载地址
     * @return 下载后的文件位置(绝对路径) 如下载失败，则返回空字符串
     */
    public static String downloadNet(String fileName, String downloadUrl) {
        // 下载网络文件
        FileOutputStream fs = null;
        File file1 = new File(fileName);
        try {
            if (file1.exists()) {
                // 已经存在，需要先删掉吧，目前不支持断点续传
                file1.delete();
            } else {
                // 不存在文件，判断父级目录是否存在
                File parentFile = file1.getParentFile();
                parentFile.mkdirs();
            }
            fs = new FileOutputStream(fileName);
            int bytesum = 0;
            int byteread = 0;
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode == 302) {
                // 如果是302，拿着新的URL重新走一遍下载流程
                String location = conn.getHeaderField("Location");
                downloadNet(fileName, location);
            }
            InputStream inStream = conn.getInputStream();
            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            fs.flush();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file1.getAbsolutePath();
    }

    /**
     * 从网络上获取字符串
     *
     * @param urlString
     * @return
     */
    public static String getStringFromNet(String urlString) {
        try {
            String s = cacheMap.get(urlString);
            if (!TextUtil.isEmpty(s)) {
                return s;
            }
            int byteread = 0;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2 * 60000);
            conn.setReadTimeout(2 * 60000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 302) {
                // 如果是302，拿着新的URL重新走一遍下载流程
                String location = conn.getHeaderField("Location");
                getStringFromNet(location);
            }
            InputStream inStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            String strRead = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((strRead = reader.readLine()) != null) {
                stringBuilder.append(strRead);
                stringBuilder.append("\r\n");
            }
            reader.close();
            String s1 = stringBuilder.toString();
            cacheMap.put(urlString, s1);
            return s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 从网络上获取字符串
     *
     * @param urlString
     * @return
     */
    public static String getStringFromNet(String urlString, String method, Map<String, Object> strParams) {
        try {
//            String s = cacheMap.get(urlString + "?" + param);
//            if (!TextUtil.isEmpty(s)) {
//                return s;
//            }
            int byteread = 0;
            if (strParams != null) {
                if (method.equalsIgnoreCase("GET")) {
                    if (!urlString.contains("?")) {
                        urlString += "?";
                    }
                    for (Map.Entry<String, Object> stringObjectEntry : strParams.entrySet()) {
                        urlString += stringObjectEntry.getKey() + "&" + stringObjectEntry.getValue();
                    }
                }
            }
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            if (strParams != null && method.equalsIgnoreCase("POST")) {
                conn.setDoOutput(true);     //需要输出
                conn.setDoInput(true);      //需要输入
                conn.setUseCaches(false);   //不允许缓存
                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(getStrParams(strParams).toString());
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 302) {
                // 如果是302，拿着新的URL重新走一遍下载流程
                String location = conn.getHeaderField("Location");
                getStringFromNet(location);
            }
            InputStream inStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            String strRead = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((strRead = reader.readLine()) != null) {
                stringBuilder.append(strRead);
                stringBuilder.append("\r\n");
            }
            reader.close();
            String s1 = stringBuilder.toString();
//            cacheMap.put(urlString + "?" + param, s1);
            return s1;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断网络地址是否可访问，文件存在不存在
     *
     * @param urlString
     * @return
     */
    public static boolean isExist(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clearCache() {
        if (cacheMap != null) {
            cacheMap.clear();
        }
    }


    private static final int TIME_OUT = 18 * 1000;                          //超时时间
    private static final String CHARSET = "utf-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";                        //换行

    /**
     * 上传文件到服务器
     *
     * @param requestUrl 上传地址
     * @param strParams  携带参数
     * @param fileParams 文件列表（key是文件名字）
     * @return
     */
    public static String upload(String requestUrl, Map<String, Object> strParams, Map<String, File> fileParams) {
        try {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(requestUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);//Post 请求不能使用缓存
                //设置请求头参数
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                /**
                 * 请求体
                 */
                //上传参数
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                //getStrParams()为一个
                dos.writeBytes(getStrFileParams(strParams).toString());
                dos.flush();

                //文件上传
                StringBuilder fileSb = new StringBuilder();
                for (Map.Entry<String, File> fileEntry : fileParams.entrySet()) {
                    fileSb.append(PREFIX)
                            .append(BOUNDARY)
                            .append(LINE_END)
                            /**
                             * 这里重点注意： name里面的值为服务端需要的key 只有这个key 才可以得到对应的文件
                             * filename是文件的名字，包含后缀名的 比如:abc.png
                             */
                            .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                    + fileEntry.getKey() + "\"" + LINE_END)
                            .append("Content-Type: image/jpg" + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
                            .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                            .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                    dos.writeBytes(fileSb.toString());
                    dos.flush();
                    InputStream is = new FileInputStream(fileEntry.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    is.close();
                    dos.writeBytes(LINE_END);
                }
                //请求结束标志
                dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
                dos.flush();
                dos.close();
                //读取服务器返回信息
                int responseCode = conn.getResponseCode();
                System.out.println("返回码:" + responseCode);
                if (responseCode == 200) {
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("异常:" + e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("外异常:" + e);
        }
        return "";
    }

    /**
     * 对post参数进行编码处理
     */
    private static StringBuilder getStrParams(Map<String, Object> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : strParams.entrySet()) {
            strSb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if (strParams.size() > 0) {
            strSb.delete(strSb.length() - 1, strSb.length());
        }
        return strSb;
    }

    /**
     * 对post参数进行编码处理
     */
    private static StringBuilder getStrFileParams(Map<String, Object> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : strParams.entrySet()) {
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }

    /**
     * 获取文件大小
     *
     * @param urlPath
     * @return
     */
    public static long getFileLength(String urlPath) {
        try {
            URL url = new URL(urlPath);
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String headerField = conn.getHeaderField("Content-Length");
                conn.disconnect();
                return Long.parseLong(headerField);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
