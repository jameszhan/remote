package com.apple.remote.caucho;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.apple.common.util.CommonsLogWriter;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;

public class HessianSkeletonInvoker {

	protected final HessianSkeleton skeleton;
	protected final SerializerFactory serializerFactory;
	private final Logger debugLogger = Logger.getLogger(this.getClass());

	public HessianSkeletonInvoker(HessianSkeleton skeleton, SerializerFactory serializerFactory) {
		this.skeleton = skeleton;
		this.serializerFactory = serializerFactory;
	}

	public void invoke(final InputStream inputStream, final OutputStream outputStream) throws Throwable {
		InputStream isToUse = inputStream;
		OutputStream osToUse = outputStream;

		if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
			PrintWriter debugWriter = new PrintWriter(new CommonsLogWriter(this.debugLogger));
			isToUse = new HessianDebugInputStream(inputStream, debugWriter);
			osToUse = DebugStreamFactory.createDebugOutputStream(outputStream, debugWriter);
		}

		Hessian2Input in = new Hessian2Input(isToUse);
		if (this.serializerFactory != null) {
			in.setSerializerFactory(this.serializerFactory);
		}

		int code = in.read();
		if (code != 'c') {
			throw new IOException("expected 'c' in hessian input at " + code);
		}

		AbstractHessianOutput out = null;
		int major = in.read();
		int minor = in.read();
		debugLogger.debug(String.format("major=%d, minor=%d\n", major, minor));
		if (major >= 2) {
			out = new Hessian2Output(osToUse);
		} else {
			out = new HessianOutput(osToUse);
		}
		if (this.serializerFactory != null) {
			out.setSerializerFactory(this.serializerFactory);
		}

		try {
			this.skeleton.invoke(in, out);
		} finally {
			try {
				in.close();
				isToUse.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
				osToUse.close();
			} catch (IOException ex) {
			}
		}
	}

	private static class DebugStreamFactory {

		public static OutputStream createDebugOutputStream(OutputStream os, PrintWriter debug) {
			return new HessianDebugOutputStream(os, debug);
		}
	}

}
