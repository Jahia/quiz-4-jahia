package org.jahia.se.modules.quiz.rules;

import org.apache.unomi.api.PropertyType;
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
        String restApiPathToTestPropertyExist = PROPERTIES_PATH+"/"+PROPERTY_PREFIX_ID+jExpPropertyId;
        try {
            JCRSiteNode site = jcrNodeWrapper.getResolveSite();

            String quizScoreProperty = getQuizScoreProperty(contextServerService,site,restApiPathToTestPropertyExist);

            if(quizScoreProperty == null)
                registerQuizScoreProperty(contextServerService, site, jExpPropertyId);

        } catch (RepositoryException e) {
            logger.error("",e);
        }
    }

    private static String getQuizScoreProperty(ContextServerService contextServerService, JCRSiteNode site, String restApiPathToTestPropertyExist){
        try{
            PropertyType scoreProps = contextServerService.executeGetRequest(
                    site.getSiteKey(),
                    restApiPathToTestPropertyExist,
                    null,
                    null,
                    PropertyType.class
            );
            return scoreProps.getItemId();
//            final AsyncHttpClient asyncHttpClient = contextServerService
//                    .initAsyncHttpClient(site.getSiteKey());
//
//            if (asyncHttpClient != null) {
//                AsyncHttpClient.BoundRequestBuilder requestBuilder = contextServerService
//                        .initAsyncRequestBuilder(site.getSiteKey(), asyncHttpClient, path,
//                                true, true, true);
//
//                ListenableFuture<Response> future = requestBuilder.execute(new AsyncCompletionHandler<Response>() {
//                    @Override
//                    public Response onCompleted(Response response) {
//                        asyncHttpClient.closeAsynchronously();
//                        return response;
//                    }
//                });
//
//                responseString = future.get().getResponseBody();
//            }

        }catch (IOException /*| ExecutionException | InterruptedException*/ e){
            logger.error("Error happened", e);
        }

        return null;
    };

    private static void registerQuizScoreProperty(ContextServerService contextServerService, JCRSiteNode site, String jExpPropertyId) {
        try{
            String payload = getPayload(jExpPropertyId);
            contextServerService.executePostRequest(
                    site.getSiteKey(),
                    PROPERTIES_PATH,
                    payload,
                    null,
                    null,
                    Boolean.class
            );
        } catch (IOException e) {
            logger.error("Error happened", e);
        }


//            try (AsyncHttpClient asyncHttpClient = contextServerService.initAsyncHttpClient(site.getSiteKey())) {
//                //preparePost Builder
//                AsyncHttpClient.BoundRequestBuilder requestBuilder = contextServerService
//                        .initAsyncRequestBuilder(site.getSiteKey(), asyncHttpClient, PROPERTIES_PATH,
//                                false, true, true);
//
//                requestBuilder.setBodyEncoding(StandardCharsets.UTF_8.name()).setBody(json);
//                requestBuilder.setHeader("accept", "application/json");
//                requestBuilder.setHeader("content-type", "application/json");
//                ListenableFuture<Response> future = requestBuilder.execute(new AsyncCompletionHandler<Response>() {
//                    @Override
//                    public Response onCompleted(Response response) {
//                        asyncHttpClient.closeAsynchronously();
//                        return response;
//                    }
//                });
//                response = future.get().getResponseBody();
//
//            } catch (IOException | ExecutionException | InterruptedException e) {
//                logger.error("Error happened", e);
//            }
//
//
//        return response;
    }
    private static String getPayload(String jExpPropertyId) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject metadata = new JSONObject();
            JSONArray systemTags = new JSONArray();

            systemTags.put("hasCardDataTag");
            systemTags.put("cardDataTag/_game4Quiz/20.1/Game4Quiz");
            systemTags.put("positionInCard.2");

            metadata.put("id", PROPERTY_PREFIX_ID + jExpPropertyId);
            metadata.put("name", "Quiz Score " + jExpPropertyId);
            metadata.put("readOnly", false);
            metadata.put("systemTags", systemTags);

            jsonObject.put("target", "profiles");
            jsonObject.put("multivalued", false);
            jsonObject.put("type", "integer");
            jsonObject.put("metadata", metadata);

            return jsonObject.toString();

        }catch (JSONException e){
            logger.error("JSON builder failed : ",e);
        }
        return null;
    }
}

