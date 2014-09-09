package com.google.restful.user.provider.entity;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.google.restful.provider.entity.ListProcessor;
import com.google.restful.provider.entity.Processor;
import com.google.restful.user.bean.User;


@ListProcessor
public class UserListProcessor implements Processor {

	@Override
	public boolean accept(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public String process(List<?> l, MediaType mime) {
		@SuppressWarnings("unchecked")
		List<User> t = (List<User>) l;
		StringBuilder sb = new StringBuilder();
		if (mime.equals(MediaType.TEXT_HTML_TYPE)) {
			sb.append("<table>\n");
			for (User u : t) {
				sb.append("<tr>\n");
				sb.append("<td>").append(u.getUserId()).append("</td><td>").append(u.getName()).append("</td>");
				sb.append("</tr>\n");
			}
			sb.append("</table>");

		} else {
			for(User u : t)
			{
				sb.append(u.getUserId()).append(' ').append(u.getName()).append('\n');		
			}			
		}
		return sb.toString();		
	}
	
	

}
