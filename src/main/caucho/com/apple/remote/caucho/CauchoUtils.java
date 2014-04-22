package com.apple.remote.caucho;

import com.caucho.hessian.server.HessianSkeleton;




public abstract class CauchoUtils {

	private CauchoUtils() {}

	public HessianSkeleton getSkeleton(Object target, Class<?> ifc){
		return new HessianSkeleton(target, ifc);		
	}
}
