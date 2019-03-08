package com.kgd.tools.library;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

public class FileUtil {
	
	public static final int UNZIP_PROGRESS  = 10001;
	public static final int COPY_FOLDER = 10002;
	/**
    * ��ѹ������.
    * ��zipFile�ļ���ѹ��folderPathĿ¼��.
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
    * ������Ŀ¼������һ�����·������Ӧ��ʵ���ļ���.
    * @param baseDir ָ����Ŀ¼
    * @param absFileName ���·������������ZipEntry�е�name
    * @return java.io.File ʵ�ʵ��ļ�
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
	 *  ���Ƶ�ǰ�ļ����µ����ݣ� ���������ļ���
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean copyFolder(String oldPath, String newPath, Handler handler){
		FileInputStream input = null;
		String outPath = null;
		FileOutputStream output = null;
		Message msg = null;
		Bundle bun = null;
		int count = 0;
		Log.i("","copyFolder old path is " + oldPath + " new path is " + newPath);
		try {
			(new File(newPath)).mkdirs(); // ����ļ��в����� �������ļ���
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {

				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					input = new FileInputStream(temp);
					outPath = newPath + "/" + temp.getName().toString();
					File outPutFile = new File(outPath);
					if (!outPutFile.exists()) {
						outPutFile.createNewFile();
					}
					output = new FileOutputStream(outPath);
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
					count++;
				}
				Log.i("copyFolder","copy file success=" + i+"="+temp.getPath());
				msg = handler.obtainMessage();
				msg.what = COPY_FOLDER;
				bun = new Bundle();
				bun.putInt("count", count);
				bun.putBoolean("flag", false);
				msg.setData(bun);
				handler.sendMessage(msg);
			}
			msg = handler.obtainMessage();
			msg.what = COPY_FOLDER;
			bun = new Bundle();
			bun.putInt("count", count);
			bun.putBoolean("flag", true);
			msg.setData(bun);
			handler.sendMessage(msg);
			return true;
		} catch (Exception e) {
			System.out.println("���������ļ������ݲ�������");
			Log.i("copyFolder","copy file Exception : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

    /**  
	  * ���������ļ�������  
	  * @param oldPath String ԭ�ļ�·�� �磺c:/folder  
	  * @param newPath String ���ƺ�·�� �磺f:/folder  
	  * @return boolean  
	  */   
	public static boolean copyFolder(String oldPath, String newPath) {
		try {
	        (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���
	        File a=new File(oldPath);
	        String[] file=a.list();
	        File temp=null;
	        for (int i = 0; i < file.length; i++) {   
	            if(oldPath.endsWith(File.separator)){
	                temp=new File(oldPath+file[i]);
	            }   
	            else{   
	                temp=new File(oldPath+ File.separator+file[i]);
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
	            
	            if(temp.isDirectory()){//��������ļ���   
	                copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);   
	            }
	        }
	        return true;
	    } catch (Exception e) {
	        System.out.println("���������ļ������ݲ�������");
	        e.printStackTrace();
	        return false;
	    } 
	}

	/**
	 *  ���Ƶ����ļ�����
	 * @param oldPath  String ԭ�ļ�·�����ļ��� �磺c:/folder/1.xml
	 * @param newPath  String ���ƺ�·�� �磺f:/folder
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath){
		try{
			(new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���
			File oldFile = new File(oldPath);
			if(oldFile.exists() && oldFile.isFile()){
				FileInputStream input = new FileInputStream(oldFile);
				String outPath = newPath + "/" + oldFile.getName().toString();
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
			return true;
		}catch (Exception e){
			System.out.println("�����ļ����ݲ�������");
			e.printStackTrace();
			return false;
		}
	}

	 /** 
	    * ���Ƶ����ļ� 
	    * @param oldPath String ԭ�ļ�·�� �磺c:/file.txt 
	    * @param newPath String ���ƺ�·�� �磺f:/file.txt 
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
	          if (oldfile.exists()) { //�ļ�����ʱ 
	              InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�
	              // ���Ŀ�ĵ�·���Ƿ���ڣ��������򴴽�
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
	                  //���͸��ƽ���
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
	          System.out.println("���Ƶ����ļ���������");
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
	 * ����  �޸�ʱ������  ���µ���
	 * @param addr
	 * @return
	 */
	public static File[] getListFiles(String addr){
		File Sfile = new File(addr);
		if(!Sfile.exists()) return null;
		File[] fs = Sfile.listFiles();

		Arrays.sort(fs, new Comparator<File>() { //����  �޸�ʱ������  ���µ���
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

	/**
	 * �ݹ�ɾ��Ŀ¼�µ������ļ�����Ŀ¼�������ļ�
	 * @param dir ��Ҫɾ�����ļ�Ŀ¼
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
//			�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// Ŀ¼��ʱΪ�գ�����ɾ��
		return dir.delete();
	}
}
