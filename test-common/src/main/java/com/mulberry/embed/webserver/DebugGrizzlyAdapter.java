package com.mulberry.embed.webserver;

import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.grizzly.util.http.Cookie;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:07 PM
 */
public class DebugGrizzlyAdapter extends GrizzlyAdapter {

    @Override
    public void service(final GrizzlyRequest request, GrizzlyResponse response) {
        try{
            PrintWriter out = response.getWriter();

            out.println("<pre>");
            BufferedReader rd = request.getReader();
            String line = null;
            while((line = rd.readLine()) != null){
                out.println(line);
            }
            out.println("</pre>");

            out.println("<table border='1'>");
            out.println(list(request.getHeaderNames(), "Header:", new Callback<String>() {
                @Override
                public String exec(Object name) {
                    return request.getHeader(name.toString());
                }
            }));

            out.println(row("Protocol", request.getProtocol()));
            out.println(row("Method", request.getMethod()));
            out.println(row("URL", request.getRequestURL()));
            out.println(row("Scheme", request.getScheme()));
            out.println(row("ServerName", request.getServerName()));
            out.println(row("ServerPort", request.getServerPort()));
            out.println(row("URI", request.getRequestURI()));
            out.println(row("QueryString", request.getQueryString()));

            out.println(row("RequestedSessionId", request.getRequestedSessionId()));

            out.println(row("ContentType", request.getContentType()));
            out.println(row("ContentLength", request.getContentLength()));
            out.println(row("CharacterEncoding", request.getCharacterEncoding()));

            out.println(row("Locale", request.getLocale()));
            out.println(list(request.getLocales(), "Locales"));

            out.println(row("LocalAddr", request.getLocalAddr()));
            out.println(row("LocalName", request.getLocalName()));
            out.println(row("LocalPort", request.getLocalPort()));
            out.println(row("RemoteAddr", request.getRemoteAddr()));
            out.println(row("RemoteHost", request.getRemoteHost()));
            out.println(row("RemotePort", request.getRemotePort()));
            out.println(row("RemoteUser", request.getRemoteUser()));

            out.println(row("AuthType", request.getAuthType()));
            out.println(row("Authorization", request.getAuthorization()));
            out.println(row("UserPrincipal", request.getUserPrincipal()));

            out.println(list(request.getNoteNames(), "Nodes", new Callback<Object>() {
                @Override
                public Object exec(Object name) {
                    return request.getNote(name.toString());
                }
            }));
            out.println(row("JrouteId", request.getJrouteId()));

            String attributeInfo = list(request.getAttributeNames(), "Attributes: ", new Callback<String>() {
                @Override
                public String exec(Object input) {
                    return (String) request.getAttribute(input.toString());
                }
            });
            out.println(attributeInfo);

            String parameterInfo = list(request.getParameterNames(), "Parameters:", new Callback<String>() {
                @Override
                public String exec(Object input) {
                    return request.getParameter(input.toString());
                }
            });
            out.println(parameterInfo);
            out.println(list(request.getCookies()));

            out.println("</table>");
            out.flush();

        }catch(Exception ex){
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            response.setStatus(200, sw.toString());
        }
    }

    public String list(Enumeration<?> e, String name, Callback<?> callback){
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td>").append(name).append("</td><td>");
        sb.append("<ol>");
        while(e.hasMoreElements()){
            Object key = e.nextElement();
            sb.append(li(key, callback.exec(key)));
        }
        sb.append("</ol></td></tr>");
        return sb.toString();
    }

    public String row(Object key, Object value){
        return String.format("<tr><td>%s:</td><td>%s</td></tr>", key, value);
    }

    public String list(Enumeration<?> e, String name){
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td>").append(name).append("</td><td>");
        sb.append("<ol>");
        while(e.hasMoreElements()){
            Object key = e.nextElement();
            sb.append("<li>").append(key).append("</li>");
        }
        sb.append("</ol></td></tr>");
        return sb.toString();
    }

    public String list(Iterator<?> it, String name, Callback<?> callback){
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td>").append(name).append("</td><td>");
        sb.append("<ol>");
        while(it.hasNext()){
            Object key = it.next();
            sb.append(li(key, callback.exec(key)));
        }
        sb.append("</ol></td></tr>");
        return sb.toString();
    }

    public String li(Object key, Object value){
        return String.format("<li>%s: %s</li>", key, value);
    }

    private String list(Cookie[] cookies){
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td>").append("Cookie").append("</td><td>");
        if(cookies != null){
            for(Cookie cookie : cookies){
                sb.append("<li>").append(String.format("{n: %s, v: %s, p: %s, d: %s}",
                        cookie.getName(), cookie.getValue(),
                        cookie.getPath(), cookie.getDomain(), cookie.getVersion(), cookie.getMaxAge(),
                        cookie.getSecure(), cookie.getComment())).append("</li>");
            }
        }
        sb.append("</ol></td></tr>");
        return sb.toString();
    }

    private interface Callback<T>{
        T exec(Object input);
    }

}
