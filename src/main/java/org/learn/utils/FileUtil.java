package org.learn.utils;

import org.learn.controller.PageController;
import org.learn.enums.ConstEnum;
import org.learn.enums.MessageEnum;
import org.learn.exception.CustomizeException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.UUID;

public class FileUtil {

    private static String getUploadRootPath(){
        URL classes = FileUtil.class.getResource("/");
        return classes.getPath();
    }

    /**
     * 上传文件返回相对路径
     * @param file
     * @param path
     * @return
     */
    public static String uploadFile(MultipartFile file, String path){
        String relPath = null;
        if (file != null && !file.isEmpty()){
            String dir = getUploadRootPath() + ConstEnum.UPLOAD_ROOT_PATH.getValue() + File.separator;
            File dirFile = new File(dir);
            if (!dirFile.exists()){
                boolean flag = dirFile.mkdir();
                if (!flag){
                    throw new CustomizeException(MessageEnum.DIR_CREATE_FAIL);
                }
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.length() > 50){
                originalFilename = "";
            }
            int uuid = UUID.randomUUID().hashCode();
            String newName = uuid + "_" + originalFilename;
            path = path == null ? "" : path;
            relPath = path + newName;
            String fullPath = dir + relPath;
            File newFile = new File(fullPath);
            try {
                if (newFile.createNewFile()) {
                    file.getInputStream().transferTo(new FileOutputStream(newFile));
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new CustomizeException(MessageEnum.FILE_UPLOAD_FAIL);
            }
        }
        return relPath;
    }

    public static void outPutFile(HttpServletResponse response, String filePath) throws IOException {
        URL url = PageController.class.getResource(filePath);
        if (url != null) {
            FileInputStream fileInputStream = new FileInputStream(getUploadRootPath()+filePath);
            byte[] b = new byte[fileInputStream.available()];
            int readSize = fileInputStream.read(b);
            if (readSize > 0) {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(b);
                outputStream.flush();
                outputStream.close();
            }
        }else {
            throw new CustomizeException(MessageEnum.FILE_NOT_FOUND.getMsg());
        }
    }
}