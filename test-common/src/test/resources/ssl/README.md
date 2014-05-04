
keytool 是JDK提供的证书生成工具，所有参数的用法参见keytool –help
-genkey 创建新证书
-v 详细信息
-alias tomcat 以”tomcat”作为该证书的别名。这里可以根据需要修改
-keyalg RSA 指定算法
-keystore /tmp/tomcat.keystore 保存路径及文件名
-dname "CN=127.0.0.1,OU=pde,O=pde,L=Peking,ST=Peking,C=CN" 证书发行者身份，这里的CN要与发布后的访问域名一致。但由于我们是自己发行的证书，如果在浏览器访问，仍然会有警告提示
-validity 3650证书有效期，单位为天
-storepass pdepde 证书的存取密码
-keypass pdepde 证书的私钥

生成服务端证书
keytool -genkey -v -alias jetty -keyalg RSA -keystore jetty.keystore -validity 3650 -storepass changeit -keypass changeit

生成客户端证书
keytool -genkey -v -alias client -keyalg RSA -storetype PKCS12 -keystore client.p12 -validity 3650 -storepass changeit -keypass changeit

导出客户端证书
keytool -export -alias client -keystore client.p12 -storetype PKCS12 -storepass changeit -rfc -file client.cer

把客户端证书加入服务端证书信任列表
keytool -import -alias client -v -file client.cer -keystore jetty.keystore -storepass changeit

导出服务端证书
keytool -export -alias jetty -keystore jetty.keystore -storepass changeit -rfc -file jetty.cer

生成客户端信任列表
keytool -import -file jetty.cer -storepass changeit -keystore client.truststore -alias jetty

生成证书请求
keytool -certreq -alias jetty -file jetty.csr -keystore jetty.keystore