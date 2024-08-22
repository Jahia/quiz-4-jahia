package org.jahia.se.modules.game.quiz.rules;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.jahia.modules.jexperience.admin.ContextServerService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.rules.BackgroundAction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Component(service = BackgroundAction.class, immediate = true)
public class ScoreAsUserProperty implements BackgroundAction {
    private static final Logger logger = LoggerFactory.getLogger(ScoreAsUserProperty.class);
    private static final String PROPERTIES_PATH= "/cxs/profiles/properties";
    private static final String PROPERTY_PREFIX_ID= "quiz-score-";
    private static final String PROPERTY_NAME= "game4:quizKey";

    private ContextServerService contextServerService;

    @Reference(service=ContextServerService.class)
    public void setContextServerService(ContextServerService contextServerService) {
        this.contextServerService = contextServerService;
    }

    @Override
    public String getName() {
        return "scoreAsUserProperty";
    }

    @Override
    public void executeBackgroundAction(JCRNodeWrapper jcrNodeWrapper) {

        String jExpPropertyId = jcrNodeWrapper.getPropertyAsString(PROPERTY_NAME);
        String testPath = PROPERTIES_PATH+"/"+PROPERTY_PREFIX_ID+jExpPropertyId;
        try {
            JCRSiteNode site = jcrNodeWrapper.getResolveSite();

            String item = getItem(contextServerService,site,testPath);
            if(item=="")
                item = registerItem(contextServerService, site, PROPERTIES_PATH, jExpPropertyId);
        } catch (RepositoryException e) {
            logger.error("",e);
        }
    }

    private static String getItem(ContextServerService contextServerService, JCRSiteNode site, String path){
        String responseString = "";
        try{
            final AsyncHttpClient asyncHttpClient = contextServerService
                    .initAsyncHttpClient(site.getSiteKey());

            if (asyncHttpClient != null) {
                AsyncHttpClient.BoundRequestBuilder requestBuilder = contextServerService
                        .initAsyncRequestBuilder(site.getSiteKey(), asyncHttpClient, path,
                                true, true, true);

                ListenableFuture<Response> future = requestBuilder.execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) {
                        asyncHttpClient.closeAsynchronously();
                        return response;
                    }
                });

                responseString = future.get().getResponseBody();
            }

        }catch (IOException | ExecutionException | InterruptedException e){
            logger.error("Error happened", e);
        }

        return responseString;
    };

    private static String registerItem(ContextServerService contextServerService, JCRSiteNode site, String path, String jExpPropertyId) {
        String response="0";

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject metadata = new JSONObject();
            JSONArray systemTags = new JSONArray("[\"hasCardDataTag\",\"cardDataTag/_game4Quiz/20.1/Game4Quiz\",\"positionInCard.2\"]");

            metadata.put("id",PROPERTY_PREFIX_ID+jExpPropertyId);
            metadata.put("name","Quiz Score "+jExpPropertyId);
            metadata.put("readOnly",false);
            metadata.put("systemTags",systemTags);

            jsonObject.put("target", "profiles");
            jsonObject.put("multivalued", "false");
            jsonObject.put("type", "integer");
            jsonObject.put("metadata", metadata);

            String json = jsonObject.toString();
            try (AsyncHttpClient asyncHttpClient = contextServerService.initAsyncHttpClient(site.getSiteKey())) {
                //preparePost Builder
                AsyncHttpClient.BoundRequestBuilder requestBuilder = contextServerService
                        .initAsyncRequestBuilder(site.getSiteKey(), asyncHttpClient, PROPERTIES_PATH,
                                false, true, true);

                requestBuilder.setBodyEncoding(StandardCharsets.UTF_8.name()).setBody(json);
                requestBuilder.setHeader("accept", "application/json");
                requestBuilder.setHeader("content-type", "application/json");
                ListenableFuture<Response> future = requestBuilder.execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) {
                        asyncHttpClient.closeAsynchronously();
                        return response;
                    }
                });
                response = future.get().getResponseBody();

            } catch (IOException | ExecutionException | InterruptedException e) {
                logger.error("Error happened", e);
            }
        }catch (JSONException e){
            logger.error("JSON builder failed : ",e);
        }

        return response;
    }
}

