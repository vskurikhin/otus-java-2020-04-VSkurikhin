package su.svn.hiload.socialnetwork.services;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalErrorExceptionHandler extends DefaultErrorWebExceptionHandler {

    private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);

    public GlobalErrorExceptionHandler(
            ErrorAttributes errorAttributes,
            ResourceProperties resourceProperties,
            ServerProperties serverProperties,
            ObjectProvider<ViewResolver> viewResolvers,
            ServerCodecConfigurer serverCodecConfigurer,
            ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, serverProperties.getError(), applicationContext);
        setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        setMessageWriters(serverCodecConfigurer.getWriters());
        setMessageReaders(serverCodecConfigurer.getReaders());
    }

    public Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Map<String, Object> error = getErrorAttributes(request, true);
        error.put("errorMessage", error.get("message"));
        int errorStatus = getHttpStatus(error);
        ServerResponse.BodyBuilder responseBody = ServerResponse.status(errorStatus).contentType(TEXT_HTML_UTF8);

        return Mono.just("error").flatMap(view -> renderErrorView(view, responseBody, error));
    }

    protected Mono<ServerResponse> renderErrorView(
            String viewName,
            ServerResponse.BodyBuilder responseBody,
            Map<String, Object> error) {
        return responseBody.render(viewName, error);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }
}
