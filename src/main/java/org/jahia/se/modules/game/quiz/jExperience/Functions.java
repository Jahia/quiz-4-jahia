package org.jahia.se.modules.game.quiz.jExperience;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.jahia.modules.jexperience.admin.ContextServerService;
import org.jahia.osgi.BundleUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
//import org.apache.unomi.api.ContextRequest;

public final class Functions {
    public static final Logger logger = LoggerFactory.getLogger(Functions.class);

    private Functions(){}

    public static Map<String, String> searchQuizScoreEvent (HttpServletRequest request, String siteKey, String quizKey, String scorePropertyName, String locale) {
        ContextServerService contextServerService = BundleUtils.getOsgiService(ContextServerService.class,null);
        String profileId = contextServerService.getProfileId(request, siteKey);
//        ContextRequest contextRequest = new ContextRequest();
//        contextRequest.setProfileId(profileId);
//        contextRequest.setRequiredProfileProperties()
//
//        // send request
//        try {
//            ContextResponse contextResponse = contextServerService.executeContextRequest(contextRequest, request, siteKey);
//        } catch (IOException e) {
//            logger.error("Error getting unomi user profile properties ", e);
//        }

        Map<String, String> properties = new HashMap<>();
        StringBuilder payloadJSONStringBuilder =
                new StringBuilder("{\"offset\" : 0,\"limit\" : 1,\"sortby\": \"timeStamp:desc\",");
        payloadJSONStringBuilder.append("\"condition\" : { \"type\": \"booleanCondition\",\"parameterValues\": {");
        payloadJSONStringBuilder.append("\"operator\": \"and\",\"subConditions\": [");
        payloadJSONStringBuilder.append("{ \"type\": \"eventTypeCondition\",\"parameterValues\" : {\"eventTypeId\": \"setQuizScore\"} },");
        payloadJSONStringBuilder.append("{ \"type\": \"eventPropertyCondition\",\"parameterValues\": {\"propertyName\": \"source.properties.quiz.key\",\"comparisonOperator\": \"equals\",\"propertyValue\":\""+quizKey+"\"} },");
        payloadJSONStringBuilder.append("{ \"type\": \"eventPropertyCondition\",\"parameterValues\": {\"propertyName\": \"profileId\",\"comparisonOperator\": \"equals\",\"propertyValue\":\""+profileId+"\"} }");
        payloadJSONStringBuilder.append("]}}}");

        try {

            final AsyncHttpClient asyncHttpClient =  contextServerService
                    .initAsyncHttpClient(siteKey);

            if (asyncHttpClient != null) {
                AsyncHttpClient.BoundRequestBuilder requestBuilder = contextServerService
                        .initAsyncRequestBuilder(siteKey, asyncHttpClient, "/cxs/events/search",
                                false, true, true);
                requestBuilder.setHeader("Accept", "application/json");
                requestBuilder.setHeader("Content-Type", "application/json;charset=UTF-8");
//                if (profileId != null) {
//                    requestBuilder.setHeader("Cookie", "context-profile-id=" + profileId);
//                }
                requestBuilder.setBody(payloadJSONStringBuilder.toString());

                ListenableFuture<Response> future = requestBuilder.execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public void onThrowable(Throwable t) {
                        asyncHttpClient.closeAsynchronously();
                    }

                    @Override
                    public Response onCompleted(Response response) {
                        logger.debug("Login event response code = " + response.getStatusCode());
                        if (response.getStatusCode() >= 400) {
                            logger.error(Integer.toString(response.getStatusCode()));
                            logger.error(response.getStatusText());
                        }
                        asyncHttpClient.closeAsynchronously();
                        return response;
                    }
                });

                JSONObject responseBody = new JSONObject(future.get().getResponseBody());
                JSONArray eventList = responseBody.getJSONArray("list");
                if(eventList.length() == 1){
                    JSONObject event = eventList.getJSONObject(0);
                    String quizScore = event.getJSONObject("properties").getJSONObject("update").getString("properties."+scorePropertyName);
//                    String pattern = "YY MM dd HH:mm:ss";
//                    SimpleDateFormat simpleDateFormat =
//                            new SimpleDateFormat(pattern, new Locale(locale));
//                    String quizReleaseDate = simpleDateFormat.format(Date.from(Instant.parse(event.getString("timeStamp"))));
//                    String quizReleaseDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(LocalDate.parse(event.getString("timeStamp")));
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale(locale));
                    String quizReleaseDate = df.format(Date.from(Instant.parse(event.getString("timeStamp"))));

                    properties.put("quizScore",quizScore);
                    properties.put("quizReleaseDate",quizReleaseDate);
                }
            }

        }catch ( InterruptedException | ExecutionException | IOException | JSONException e) {
            logger.error("Error happened", e);
        }
        return properties;
    };
}
