/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/4/14
 *         Time: 9:45 PM
 */
public class JettyServer {
    public static void main(String[] args) throws Exception {
        ThreadPool threadPool = new QueuedThreadPool(500);
        Server server = new Server(threadPool);
        // Scheduler
        server.addBean(new ScheduledExecutorScheduler("jetty", true));

        WebAppContext webAppContext = new WebAppContext("test-common/src/site", "/");
        webAppContext.setServer(server);
        webAppContext.setWelcomeFiles(new String[]{ "index.htm", "index.html" });
        server.setHandler(webAppContext);

        webAppContext.configure();

        configHttpConnector(server);
        //configHttpsConnector(server);

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        server.start();
        server.join();
    }

    private static void configHttpConnector(Server server) {
        HttpConfiguration configuration = defaultConfiguration();
        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(configuration));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);
    }

    private static void configHttpsConnector(Server server) {
        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath("test-common/src/test/resources/ssl/jetty.keystore");
        sslContextFactory.setKeyStorePassword("changeit");
        sslContextFactory.setKeyManagerPassword("changeit");
        sslContextFactory.setTrustStorePath("test-common/src/test/resources/ssl/client.truststore");
        sslContextFactory.setTrustStorePassword("changeit");
        sslContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration httpsConfig = defaultConfiguration();
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                new HttpConnectionFactory(httpsConfig));
        sslConnector.setPort(8443);
        server.addConnector(sslConnector);
    }

    private static HttpConfiguration defaultConfiguration(){
        // HTTP Configuration
        HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(8443);
        config.setOutputBufferSize(32768);
        config.setRequestHeaderSize(8192);
        config.setResponseHeaderSize(8192);
        config.setSendServerVersion(true);
        config.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        return config;
    }
}
