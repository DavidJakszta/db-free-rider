package de.freerider.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


/**
 * Swagger configuration class.
 * 
 * Enables URLs:
 *  http://localhost:8080/swagger-ui/index.html
 *  http://localhost:8080/v3/api-docs
 *  https://editor.swagger.io/
 * 
 * 
 * @author sgra64
 */
@PropertySource("classpath:swagger.properties")
@Configuration
public class SwaggerConfig {

    @Autowired
    private Environment env;

    /*
     * PropertySourcesPlaceHolderConfigurer Bean only required for @Value("{}") annotations.
     * Remove this bean if you are not using @Value annotations for injecting properties.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public OpenAPI freeriderAPIDocs() {
        /*
         * read properties from file: swagger.properties
         * see:
         * - https://github.com/onap/msb-java-sdk/tree/master/example/src/main/resources
         */
        final String api_info_version = env.getProperty("api-info-version");
        final String api_info_title = env.getProperty("api-info-title");
        final String api_info_description = env.getProperty("api-info-description");
        final String api_ext_doc_description = env.getProperty("api-ext-doc-description");
        final String api_ext_doc_url = env.getProperty("api-ext-doc-url");
        return new OpenAPI()
            .info(
                new Info()
                    .title(api_info_title)
                    .description(api_info_description)
                    .version(api_info_version)
                    .license(new License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .externalDocs(
                new ExternalDocumentation()
                    .description(api_ext_doc_description)
                    .url(api_ext_doc_url));
    }


    /*
    * Enable code below to drive /v3/api-docs from static file
    * - resources/static/api-docs.yaml
    * rather than through API inspection.
    * 
    * Must also enable in application.yaml:
    *   springdoc:
    *     swagger-ui:
    *       url: /api-docs.yaml   # read from file rather than generate
    *       enabled: true
    *     api-docs:
    *       path: /v3/api-docs
    *       enabled: true
    * 
    * Source:
    * - What is a proper way to set up Swagger UI to use provided spec.yml?
    *   https://springdoc.org/faq.html#_what_is_a_proper_way_to_set_up_swagger_ui_to_use_provided_spec_yml
    */

    // @Bean
    // org.springdoc.core.configuration.SpringDocConfiguration springDocConfiguration(){
    //    return new org.springdoc.core.configuration.SpringDocConfiguration();
    // }
    //
    // @Bean
    // org.springdoc.core.properties.SpringDocConfigProperties springDocConfigProperties() {
    //    return new org.springdoc.core.properties.SpringDocConfigProperties();
    // }
    //
    // @Bean
    // org.springdoc.core.providers.ObjectMapperProvider objectMapperProvider(
    //     org.springdoc.core.properties.SpringDocConfigProperties springDocConfigProperties
    // ){
    //     return new org.springdoc.core.providers.ObjectMapperProvider(springDocConfigProperties);
    // }
}
