package com.kgd.tools.library;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FileUtil {
	
	public static final int UNZIP_PROGRESS  = 10001;
	
	/**
    * 解压缩功能.
    * 将zipFile文件解压到folderPath目录下.
    * @throws Exception
	*/
    public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
        ZipFile zfile=new ZipFile(zipFile);
        Enumeration zList=zfile.entries();
        ZipEntry ze=null;
        byte[] buf=new byte[1024];
        while(zList.hasMoreElements()){
            ze=(ZipEntry)zList.nextElement();    
            if(ze.isDirectory()){
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = "+dirstr);
                File f=new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = "+ze.getName());
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();    
        }
        zfile.close();
        Log.d("upZipFile", "finishssssssssssssssssssss");
        return 0;
    }

    /**
    * 给定根目录，返回一个相对路径所对应的实际文件名.
    * @param baseDir 指定根目录
    * @param absFileName 相对路径名，来自于ZipEntry中的name
    * @return java.io.File 实际的文件
    */
    public static File getRealFileName(String baseDir, String absFileName){
        String[] dirs=absFileName.split("/");
        File ret=new File(baseDir);
        String substr = null;
        if(dirs.length > 1) {
            for (int i = 0; i < dirs.length-1;i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret=new File(ret, substr);
            }
            if(!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length-1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret=new File(ret, substr);
        } else if(dirs.length == 1) {
            if(!ret.exists())
                ret.mkdirs();
            substr = dirs[0];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret=new File(ret, substr);
        }
        Log.d("getRealFileName: ",  "dirs.length="+ dirs.length + ";" + ret.getAbsolutePath());
        return ret;
    }

    /**  
	  * 复制整个文件夹内容  
	  * @param oldPath String 原文件路径 如：c:/folder  
	  * @param newPath String 复制后路径 如：f:/folder  
	  * @return boolean  
	  */   
	public static boolean copyFolder(String oldPath, String newPath) {   
		try {
	        (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
	        File a=new File(oldPath);   
	        String[] file=a.list();   
	        File temp=null;   
	        for (int i = 0; i < file.length; i++) {   
	            if(oldPath.endsWith(File.separator)){   
	                temp=new File(oldPath+file[i]);   
	            }   
	            else{   
	                temp=new File(oldPath+File.separator+file[i]);   
	            }   
	
	            if(temp.isFile()){   
	                FileInputStream input = new FileInputStream(temp);
	                String outPath = newPath + "/" + temp.getName().toString();
	                File outPutFile = new File(outPath);
	                if(!outPutFile.exists()) {
	             	   outPutFile.createNewFile();
	                }
	                FileOutputStream output = new FileOutputStream(outPath);
	                byte[] b = new byte[1024 * 5];   
	                int len;   
	                while ( (len = input.read(b)) != -1) {   
	                    output.write(b, 0, len);   
	                }   
	                output.flush();   
	                output.close();   
	                input.close();   
	            }
	            
	            if(temp.isDirectory()){//如果是子文件夹   
	                copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);   
	            }
	        }
	        return true;
	    } catch (Exception e) {   
	        System.out.println("复制整个文件夹内容操作出错");
	        e.printStackTrace();
	        return false;
	    } 
	}
	
	 /** 
	    * 复制单个文件 
	    * @param oldPath String 原文件路径 如：c:/file.txt 
	    * @param newPath String 复制后路径 如：f:/file.txt 
	    * @return boolean 
	    */
	  public static boolean copyFileWithPrBar(String oldPath, String newPath, Handler handler) { 
	      boolean isok = true;
	      int progress = 0, tempProgress = 0;
	      Message msg = null;
	      Bundle bun = null;
	      try { 
	          int bytesum = 0; 
	          int byteread = 0; 
	          File oldfile = new File(oldPath); 
	          if (oldfile.exists()) { //文件存在时 
	              InputStream inStream = new FileInputStream(oldPath); //读入原文件
	              // 检查目的地路径是否存在，不存在则创建
	              File dirFile = new File(newPath.substring(0, newPath.lastIndexOf("/")));
	              if(!dirFile.exists()) {
	            	  dirFile.mkdirs();
	              }
	              File newPathFile = new File(newPath);
	              if(!newPathFile.exists()) {
	            	  newPathFile.createNewFile();
	              }
	              long length = oldfile.length();
	              long count = 0;
	              FileOutputStream fs = new FileOutputStream(newPath); 
	              byte[] buffer = new byte[1024]; 
	              while ( (byteread = inStream.read(buffer)) != -1) { 
	            	  bytesum += byteread;
	                  fs.write(buffer, 0, byteread);
	                  //发送复制进度
	                  if(handler != null){
	                	  tempProgress = (int) (((float) bytesum / length) * 100);
	                	  if(tempProgress > progress) {
	                		  progress = tempProgress;
	                		  msg = handler.obtainMessage();
			                  msg.what = UNZIP_PROGRESS;
			                  bun = new Bundle();
			                  bun.putInt("UNZIP_PROGRESS", progress);
			                  msg.setData(bun);
			                  handler.sendMessage(msg);
	                	  }
	                  }
	              } 
	              fs.flush(); 
	              fs.close(); 
	              inStream.close(); 
	          }
	          else
	          {
	           isok = false;
	          }
	      } 
	      catch (Exception e) {
	    	  isok = false;
	          System.out.println("复制单个文件操作出错"); 
	          e.printStackTrace(); 
	      } 
	      return isok;
	  }

	  public static void delete(File file) {
		  if(file.isFile()){
			  file.delete();
			  return;
		  }
		  if(file.isDirectory()){
			  File[] childFiles = file.listFiles();
			  if(childFiles == null || childFiles.length == 0) {
				  file.delete();
			  }
			  for(int i = 0; i < childFiles.length; i++) {
				  delete(childFiles[i]);
			  }
			  file.delete();
		  }
	  }

	  /**
	   * 根据  修改时间排序  由新到旧
	   * @param addr
	   * @return
	   */
	  public static File[] getListFiles(String addr){
		  File Sfile = new File(addr);
		  if(!Sfile.exists()) return null;
		  File[] fs = Sfile.listFiles();
	    	
		  Arrays.sort(fs, new Comparator<File>() { //根据  修改时间排序  由新到旧
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;
			}

			public boolean equals(Object obj) {
				return true;
			}
		  });
		  return fs;
	  }
	  
	  /**文件重命名 
	    * @param path 文件目录 
	    * @param oldname  原来的文件名 
	    * @param newname 新文件名 
	    */ 
	    public static void renameFile(String path,String oldname,String newname){ 
	        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
	            File oldfile=new File(path+"/"+oldname); 
	            File newfile=new File(path+"/"+newname); 
	            if(!oldfile.exists()){
	                return;//重命名文件不存在
	            }
	            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	                System.out.println(newname+"已经存在！"); 
	            else{ 
	                oldfile.renameTo(newfile); 
	            } 
	        }else{
	            System.out.println("新文件名和旧文件名相同...");
	        }
	    }
}
