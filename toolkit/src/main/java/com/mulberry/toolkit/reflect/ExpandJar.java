package com.mulberry.toolkit.reflect;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExpandJar {

    public static String expand(URL jar) throws IOException {
        return expand(jar, System.getProperty("java.io.tmpdir"));
    }

    public static String expand(URL jar, String workFolder) throws IOException {
        // Calculate the directory name of the expanded directory
        String pathname = jar.toString().replace('\\', '/');
        if (pathname.endsWith("!/")) {
            pathname = pathname.substring(0, pathname.length() - 2);
        }
        int period = pathname.lastIndexOf('.');
        if (period >= pathname.length() - 4)
            pathname = pathname.substring(0, period);
        int slash = pathname.lastIndexOf('/');
        if (slash >= 0) {
            pathname = pathname.substring(slash + 1);
        }
        return expand(jar, pathname, workFolder);
    }

    public static String expand(URL jar, String pathname, String dirname) throws IOException {
        // Make sure that there is no such directory already existing
        File appBase = new File(dirname);
        File docBase = new File(appBase, pathname);
        // Create the new document base directory
        docBase.mkdir();

        // Expand the jar into the new document base directory
        JarURLConnection juc = (JarURLConnection) jar.openConnection();
        juc.setUseCaches(false);
        JarFile jarFile = null;
        InputStream input = null;
        try {
            jarFile = juc.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                int last = name.lastIndexOf('/');
                if (last >= 0) {
                    File parent = new File(docBase, name.substring(0, last));
                    parent.mkdirs();
                }
                if (name.endsWith("/")) {
                    continue;
                }
                input = jarFile.getInputStream(jarEntry);
                expand(input, docBase, name);
                input.close();
                input = null;
            }
        } catch (IOException e) {
            // If something went wrong, delete expanded dir to keep things
            // clean
            deleteDir(docBase);
            throw e;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable t) {
                }
                input = null;
            }
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (Throwable t) {
                }
                jarFile = null;
            }
        }
        // Return the absolute path to our new document base directory
        return (docBase.getAbsolutePath());

    }

    public static boolean copy(File src, File dest) {

        boolean result = true;

        String files[] = null;
        if (src.isDirectory()) {
            files = src.list();
            result = dest.mkdir();
        } else {
            files = new String[1];
            files[0] = "";
        }
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; (i < files.length) && result; i++) {
            File fileSrc = new File(src, files[i]);
            File fileDest = new File(dest, files[i]);
            if (fileSrc.isDirectory()) {
                result = copy(fileSrc, fileDest);
            } else {
                FileChannel ic = null;
                FileChannel oc = null;
                try {
                    ic = (new FileInputStream(fileSrc)).getChannel();
                    oc = (new FileOutputStream(fileDest)).getChannel();
                    ic.transferTo(0, ic.size(), oc);
                } catch (IOException e) {

                    result = false;
                } finally {
                    if (ic != null) {
                        try {
                            ic.close();
                        } catch (IOException e) {
                        }
                    }
                    if (oc != null) {
                        try {
                            oc.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        return result;

    }

    public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            return deleteDir(dir);
        } else {
            return dir.delete();
        }
    }


    public static boolean deleteDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
        return dir.delete();

    }

    protected static void expand(InputStream input, File docBase, String name) throws IOException {

        File file = new File(docBase, name);
        BufferedOutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file));
            byte buffer[] = new byte[2048];
            while (true) {
                int n = input.read(buffer);
                if (n <= 0)
                    break;
                output.write(buffer, 0, n);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

    }
}