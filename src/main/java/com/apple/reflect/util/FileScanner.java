package com.apple.reflect.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileScanner {
	
	private FileScanner(){}
	
	public static void scan(final File file, final ScannerListener sl){
		if(file.isDirectory()){
			for (final File child : file.listFiles()) {
				scan(child, sl);
			}
		}else if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip")){
			try {
				JarFileScanner.scan(file, "", sl);
			} catch (IOException ex) {
				throw new RuntimeException("IO error when scanning jar file " + file, ex);
			}
		}else{
			if(sl.onAccept(file.getName())){
				try {
					new Closing(new BufferedInputStream(new FileInputStream(file))).f(
							new Closing.Closure() {
						public void f(InputStream in) throws IOException {
							sl.onProcess(file.getName(), in);
						}
					});
				} catch (IOException ex) {
					throw new RuntimeException("IO error when scanning file " + file, ex);
				}
			}
		}
	}

}
