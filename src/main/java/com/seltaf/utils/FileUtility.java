package com.seltaf.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class FileUtility {
	
	static Logger logger = Logger.getLogger(FileUtility.class);
    static final int BUFFER = 2048;
	
	 public static String decodePath(final String path) throws UnsupportedEncodingException {
	        return URLDecoder.decode(path, "UTF-8");
	    }
	 public static void copyFile(final String srcPath, final String dstPath) throws IOException {
	        copyFile(new File(srcPath), new File(dstPath));
	    }
	 public static void copyFile(final File srcPath, final File dstPath) throws IOException {

	        if (srcPath.isDirectory()) {
	            if (!dstPath.exists()) {
	                dstPath.mkdir();
	            }

	            String[] files = srcPath.list();
	            for (String file : files) {
	                copyFile(new File(srcPath, file), new File(dstPath, file));
	            }
	        } else {
	            if (!srcPath.exists()) {
	                throw new IOException("Directory Not Found ::: " + srcPath);
	            } else {
	                InputStream in = null;
	                OutputStream out = null;
	                try {
	                    if (!dstPath.getParentFile().exists()) {
	                        dstPath.getParentFile().mkdirs();
	                    }

	                    in = new FileInputStream(srcPath);
	                    out = new FileOutputStream(dstPath);

	                    // Transfer bytes
	                    byte[] buf = new byte[1024];
	                    int len;

	                    while ((len = in.read(buf)) > 0) {
	                        out.write(buf, 0, len);
	                    }
	                } finally {
	                    if (in != null) {
	                        try {
	                            in.close();
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }

	                    if (out != null) {
	                        try {
	                            out.close();
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                }
	            }
	        }
	    }
	 public static synchronized void writeImage(final String path, final byte[] byteArray) {
	        if (byteArray.length == 0) {
	            return;
	        }

	        System.gc();

	        InputStream in = null;
	        FileOutputStream fos = null;
	        try {
	            File parentDir = new File(path).getParentFile();
	            if (!parentDir.exists()) {
	                parentDir.mkdirs();
	            }

	            byte[] decodeBuffer = Base64.decodeBase64(byteArray);
	            in = new ByteArrayInputStream(decodeBuffer);

	            BufferedImage img = ImageIO.read(in);
	            fos = new FileOutputStream(path);
	            ImageIO.write(img, "png", fos);
	            img = null;
	        } catch (Exception e) {
	            logger.warn(e.getMessage());
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	            if (fos != null) {
	                try {
	                    fos.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    /**
	     * Saves HTML Source.
	     *
	     * @param   path
	     *
	     * @throws  Exception
	     */

	    public static void writeToFile(final String path, final String content) throws IOException {

	        System.gc();

	        FileOutputStream fileOutputStream = null;
	        OutputStreamWriter outputStreamWriter = null;
	        BufferedWriter bw = null;
	        try {
	            File parentDir = new File(path).getParentFile();
	            if (!parentDir.exists()) {
	                parentDir.mkdirs();
	            }

	            fileOutputStream = new FileOutputStream(path);
	            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
	            bw = new BufferedWriter(outputStreamWriter);
	            bw.write(content);
	        } finally {
	            if (bw != null) {
	                try {
	                    bw.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	            if (outputStreamWriter != null) {
	                try {
	                    outputStreamWriter.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	            if (fileOutputStream != null) {
	                try {
	                    fileOutputStream.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

}
