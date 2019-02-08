package it.polito.veribaton.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class VeribatonErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    ResponseEntity<Map<String, Object>> error(WebRequest webRequest, HttpServletResponse response) {
        return ResponseEntity.status(response.getStatus())
                .body(
                        errorAttributes.getErrorAttributes(webRequest, false)
                );
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
