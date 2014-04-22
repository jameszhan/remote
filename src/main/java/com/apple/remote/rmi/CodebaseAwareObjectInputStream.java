package com.apple.remote.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.RMIClassLoader;

import com.apple.common.ConfigurableObjectInputStream;

public class CodebaseAwareObjectInputStream extends ConfigurableObjectInputStream{
	
	private final String codebaseUrl;

	public CodebaseAwareObjectInputStream(InputStream in, String codebaseUrl) throws IOException {
		this(in, null, codebaseUrl);
	}

	public CodebaseAwareObjectInputStream(InputStream in, ClassLoader classLoader, String codebaseUrl) throws IOException {
		super(in, classLoader);
		this.codebaseUrl = codebaseUrl;
	}


	protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex)
			throws IOException, ClassNotFoundException {
		// If codebaseUrl is set, try to load the class with the RMIClassLoader. Else, propagate the ClassNotFoundException.
		if (this.codebaseUrl == null) {
			throw ex;
		}
		return RMIClassLoader.loadClass(this.codebaseUrl, className);
	}

	protected ClassLoader getFallbackClassLoader() throws IOException {
		return RMIClassLoader.getClassLoader(this.codebaseUrl);
	}

}
