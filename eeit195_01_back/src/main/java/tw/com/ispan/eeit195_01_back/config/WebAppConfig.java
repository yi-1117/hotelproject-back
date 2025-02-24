// package tw.com.ispan.eeit195_01_back.config;

// import org.apache.catalina.Context;
// import org.apache.catalina.connector.Connector;
// import org.apache.tomcat.util.descriptor.web.SecurityCollection;
// import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
// import
// org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
// import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class WebAppConfig {
// @Bean
// public ServletWebServerFactory servletContainer() {
// TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

// @Override
// protected void postProcessContext(Context context) {
// SecurityConstraint securityConstraint = new SecurityConstraint();
// // 表示資料在雙方傳送時要啟動HTTPS
// securityConstraint.setUserConstraint("CONFIDENTIAL");
// SecurityCollection collection = new SecurityCollection();
// collection.addPattern("/*"); // 所有請求都要走HTTPS
// securityConstraint.addCollection(collection);
// context.addConstraint(securityConstraint);
// }
// };
// tomcat.addAdditionalTomcatConnectors(redirectConnector());
// return tomcat;
// }

// private Connector redirectConnector() {
// // 用程式的方式定義8080埠號，當要跳轉為HTTPS時，採用的埠號為8443
// Connector connector = new
// Connector("org.apache.coyote.http11.Http11NioProtocol");
// connector.setScheme("http");
// connector.setPort(8080);
// connector.setSecure(false);
// connector.setRedirectPort(8443);
// return connector;
// }
// }
