package com.catl.cal_index.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private static final Logger logger =  LoggerFactory.getLogger(FileUtil.class);

    /**
     * 生成ok文件
     * @param path：要生成ok文件的路径
     * @return
     */
    public static Integer saveOkFile(String path){
        Integer status = 0;
        File file = new File(path+"/------------OK-----------");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("saveOkFile error..."+e.getMessage());
                status = 1;
            }
        }
        return status;
    }

    // 查询 csv 文件是否已经从 hdfs get 到本地
    public static Integer findOKFile(String path){
        Integer status = 1; // 代表没有 SUCCESS 文件
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if(files.length > 0){
                for (File file1 : files) {
                    if(file1.getName().contains("OK")){
                        status = 0; // 找到了 success 文件
                        return status;
                    }
                }
            }
        }
        return status;
    }

    // 查询 csv 文件是否已经从 hdfs get 到本地
    public static Integer findSuccessFile(String path){
        Integer status = 1; // 代表没有 SUCCESS 文件
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if(files.length > 0){
                for (File file1 : files) {
                    if(file1.getName().contains("SUCCESS")){
                        status = 0; // 找到了 success 文件
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * 写入文件-- 这里只负责创建空的文件夹，具体的写入交由调用方来操作
     *
     * @param dir 要创建的文件夹的名称
     * @param flag    碰到重名文件夹如何做，flag=true,删掉原来文件重新创建；
     *                flag=false,如果有ok文件，存留原来文件，如果没有，删掉重建
     * @return
     */
    public static boolean createDir(String path,String dir, Integer flag) {
        File file = new File(path + "/" + dir);
        boolean result = true;
        // 如果文件夹存在，flag=true，就最多删除10次，然后新建
        if (file.exists()) {
            if (flag == 1) {
                if (null != findLatestOKFile(path,dir)){
                    return true;
                }
            }

            result = deleteFileOrDirectory(file);
            // 当删除成功并创建成功时，才返回 true
            return result && file.mkdir();
            // 如果flag为false，不做任何处理
        }
        // 如果文件不存在，直接返回新建结果
        return file.mkdir();
    }


    /**
     * 删除文件或文件夹
     */
    private static boolean deleteFileOrDirectory(File file) {
        if (null != file) {
            if (!file.exists()) {
                return true;
            }

            // file 是文件
            if (file.isFile()) {
                boolean result = file.delete();
                // 限制循环次数，避免死循环
                for (int i = 0; !result && i++ < 10; ) {
                    result = file.delete();
                    // 垃圾回收
                    System.gc();
                }
                return file.delete();
            }

            // file 是目录
            File[] files = file.listFiles();
            if (null != files) {
                for (File value : files) {
                    deleteFileOrDirectory(value);
                }
            }
            // 删除剩下的最后一个空文件夹
            return file.delete();
        }
        return false;
    }

    /**
     * 读取文件夹下的文件并找出最近的 OK 文件，并直接返回文件路径
     */
    public static String findLatestOKFile(String path,String fileName) {

        File dir = new File(path);// 文件夹路径

        // 1.1 判断 fileName 是否是一个文件夹
        if (dir.exists()) {
            //得到所有文件和文件夹
            File[] files = dir.listFiles();
            //如果文件夹为空直接返回空的路径
            if (null != files && files.length != 0) {
                //1.2 按照创建时间倒序排序
                Arrays.sort(files, new ComparatorByLastModified());
//                Arrays.sort(files, (f1,f2)-> (int) (f2.lastModified()-f1.lastModified())); // 不用comparator，直接用lambda表达式
                // 1.2 逐个分析文件或者文件夹的名称中是否开头前三个字母匹配 vin
                for (File file : files) {
//                    String regex = fileName + "[.]*";
                    String regex = fileName + ".*";
                    boolean matchResult = Pattern.compile(regex).matcher(file.getName()).find();
                    File[] okFiles = file.listFiles();
                    // 查找 OK 文件
                    if (null != okFiles && file.isDirectory() && matchResult) {
                        boolean ifContainOK = Arrays.stream(okFiles)
                                .filter(Objects::nonNull)
                                .anyMatch(f -> f.getName().contains("--OK--"));
                        if (ifContainOK) {
                            logger.info("latestOk filePath:{}", file.getAbsolutePath());
                            return file.getAbsolutePath();
                        }
                    }
                }
                return null;
            }
        }

        // 如果所给路径不是一个文件夹，直接返回空
        return null;
    }


    /**
     * 按照文件最后修改时间倒叙排序
     */
    public static class ComparatorByLastModified implements Comparator<File> {
        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff < 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }
    }

    // 压缩文件方法
    public static boolean fileToZip(String sourceFilePath, String zipFilePath,
                                    String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis ;
        BufferedInputStream bis = null;
        FileOutputStream fos ;
        ZipOutputStream zos = null;
        if (!sourceFile.exists()) {
            logger.error(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    logger.error(">>>>>> " + zipFilePath + " 目录下存在名字为："
                            + fileName + ".zip" + " 打包文件. <<<<<<");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        logger.error(">>>>>> 待压缩的文件目录：" + sourceFilePath
                                + " 里面不存在文件,无需压缩. <<<<<<");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (File file : sourceFiles) {
                            // 创建ZIP实体,并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);
                            // 读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(file);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read ;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            }catch (IOException e) {
                logger.error(e.toString());
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }


}
