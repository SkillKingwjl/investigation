package com.zztaiwll.utils;

import java.io.File;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.ITask;

public class DeleteFileTask implements ITask{

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("执行定时任务＝＝＝＝＝＝＝＝＝");
		deleteFile();
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void deleteFile(){
		Prop p = PropKit.use("db.properties");
		File f1=new File(p.get("upload"));
        // 首先得到当前的路径  
        String[] childFile1 = f1.list();
        if(childFile1!=null&&childFile1.length>0){
        	for (String childFilePath : childFile1) {  
                File childFile = new File(f1.getAbsolutePath() + File.separator + childFilePath);  
                childFile.delete() ; 
            }  
        }
        File f2=new File(p.get("download"));
        String[] childFile2 = f2.list();
        if(childFile2!=null&&childFile2.length>0){
        	for (String childFilePath : childFile2) {  
        		String path=f2.getAbsolutePath();
                File childFile = new File(path + File.separator+ childFilePath);  
                childFile.delete() ; 
            }  
        }
	}
}
