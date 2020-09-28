package com.redhat;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;

@Component
public class CamelRoutes extends RouteBuilder {


    @Value("${ansible.tower.url}")
    String ansibleTowerUrl;

    @Bean
    HostnameVerifier hostnameVerifier() {
        return new NoopHostnameVerifier();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/service/*");
        registration.setName("CamelServlet");
        return registration;
    }
    public void configure() throws Exception {

        //HttpComponent httpComponent = configureHttp4();
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto)
                .producerComponent("http4")
                .apiContextPath("/swagger") //swagger endpoint path
                .apiContextRouteId("swagger") //id of route providing the swagger endpoint
                .contextPath("/service")
                //Swagger properties
                .host("localhost:8087")
                .apiProperty("api.title", "Example REST api")
                .apiProperty("api.version", "1.0")
                .apiProperty("api.path", "/service");

           rest().post("/invoke-job/{jobId}")
                .route()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Authorization", constant("Bearer g6DJc8gWDkTYXehMmCXSEUpjil6pyo"))
                .setHeader("Content-Type", constant("application/json"))
                .to("https4://10.1.36.119:443/api/v2/job_templates/Demo%20Job%20Template/launch/?authenticationPreemptive=true&authMethod=Basic&authUsername=admin&authPassword=RedHat&x509HostnameVerifier=#hostnameVerifier&bridgeEndpoint=true");


    }






}
