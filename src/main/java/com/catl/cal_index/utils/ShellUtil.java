package com.catl.cal_index.utils;

//import org.slf4j.LoggerFactory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtil {
    private static final Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    public static Integer shellExecute(String command) {
        int runningStatus = 0;
        StringBuffer sb = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec(command);
            logger.info("shell execute starting >>>>>>>>>>>>>>>>>>>>>>>> ");
            try {
                // 开两个线程在waitfor()命令之前读出窗口的标准输出缓冲区和标准错误流的内容。防止死锁
                new Thread(() -> {
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;

                    try {
                        while ((line = in.readLine()) != null) {
                            logger.info("shell execute result : "+line);
                            System.out.println("shell execute result: " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(() -> {
                    BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line = null;
                    StringBuilder result=new StringBuilder();
                    try
                    {
                        while((line = err.readLine()) != null)
                        {
                            result.append(line);
                            logger.info("shell error result : "+line);
                            System.out.println("shell error result : " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            err.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                runningStatus = p.waitFor();
                logger.info("shell run stats..." + runningStatus);
            } catch (InterruptedException e) {
                runningStatus = 1;
                logger.error("等待shell脚本执行状态时，报错...", e);
                sb.append(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return runningStatus;
    }
}
