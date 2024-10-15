package org.jahia.se.modules.quiz.jExperience;

import org.apache.unomi.api.PartialList;
import org.apache.unomi.api.Event;
import org.jahia.modules.jexperience.admin.ContextServerService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;


@Component(service = QuizTagFunctions.class, immediate = true)
public final class QuizTagFunctions {
    public static final Logger logger = LoggerFactory.getLogger(QuizTagFunctions.class);

    private static ContextServerService contextServerService;

    @Reference(service= ContextServerService.class)
    public void setContextServerService(ContextServerService contextServerService) {
        QuizTagFunctions.contextServerService = contextServerService;
    }

    public static Map<String, String> searchQuizScoreEvent (HttpServletRequest request, String siteKey, String quizKey, String scorePropertyName, String locale) {

        String profileId = contextServerService.getProfileId(request, siteKey);

        Map<String, String> properties = new HashMap<>();
        StringBuilder payloadJSONStringBuilder = getPayloadJSONStringBuilder(quizKey, profileId);

        try {
            PartialList events = contextServerService.executePostRequest(
                    siteKey,
                    "/cxs/events/search",
                    payloadJSONStringBuilder.toString(),
                    null,
                    null,
                    PartialList.class
            );
            if(events.size() == 1){
                HashMap<String,Object> event = (HashMap) events.get(0);

                String  quizScore = Optional.ofNullable(event)
                        .map(e -> (Map<String, Object>) e.get("properties"))
                        .map(props -> (Map<String, Object>) props.get("update"))
                        .map(update -> String.valueOf(update.get("properties."+scorePropertyName)))
                        .orElse(null);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale(locale));
                String quizReleaseDate = df.format(Date.from(Instant.parse((String)event.get("timeStamp"))));

                properties.put("quizScore",quizScore);
                properties.put("quizReleaseDate",quizReleaseDate);
            }
        }catch (IOException e) {
            logger.error("Error happened", e);
        }
        return properties;
    }

    private static @NotNull StringBuilder getPayloadJSONStringBuilder(String quizKey, String profileId) {
        StringBuilder payloadJSONStringBuilder =
                new StringBuilder("{\"offset\" : 0,\"limit\" : 1,\"sortby\": \"timeStamp:desc\",");
        payloadJSONStringBuilder.append("\"condition\" : { \"type\": \"booleanCondition\",\"parameterValues\": {");
        payloadJSONStringBuilder.append("\"operator\": \"and\",\"subConditions\": [");
        payloadJSONStringBuilder.append("{ \"type\": \"eventTypeCondition\",\"parameterValues\" : {\"eventTypeId\": \"setQuizScore\"} },");
        payloadJSONStringBuilder.append("{ \"type\": \"eventPropertyCondition\",\"parameterValues\": {\"propertyName\": \"source.properties.quiz.key\",\"comparisonOperator\": \"equals\",\"propertyValue\":\""+ quizKey +"\"} },");
        payloadJSONStringBuilder.append("{ \"type\": \"eventPropertyCondition\",\"parameterValues\": {\"propertyName\": \"profileId\",\"comparisonOperator\": \"equals\",\"propertyValue\":\""+ profileId +"\"} }");
        payloadJSONStringBuilder.append("]}}}");
        return payloadJSONStringBuilder;
    }

    ;
}
