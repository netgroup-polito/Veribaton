package it.polito.veribaton.api.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Controller
@EnableSwagger2
@Configuration
@ComponentScan(basePackages = {"it.polito.veribaton.*"})
@PropertySource("classpath:/swagger.properties")
public class SwaggerController {

    @Bean
    public Docket veribaton() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("it.polito.veribaton"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Veribaton REST API")
                .version("0.0.1")
                .description("")
                .build();
    }

    @ApiIgnore
    @RequestMapping("/swagger")
    public String home() {
        return "redirect:/swagger-ui.html";
    }
}