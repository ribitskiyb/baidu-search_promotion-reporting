package etl.cloud.baidu.search_promotion;

import etl.cloud.baidu.search_promotion.internal.BaiduSPException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static etl.cloud.baidu.search_promotion.internal.BaiduSPConstants.*;
import static twitter4j.util.TwitterAdUtil.reallySleep;


public class ReportDownloader {

    public ReportDownloader(String username, String password, String token, int reportGenerationTimeout) {
        this.requestBodyTemplate = new JSONObject()
                .put(REQUEST_BODY_AUTH_FIELD, new JSONObject()
                        .put("username", username)
                        .put("password", password)
                        .put("token", token))
                .put(REQUEST_BODY_ARGUMENT_FIELD, (JSONObject) null);

        // Configure JsonPath
        Logger.getRootLogger().setLevel(Level.OFF); // suppress log4j warnings
        this.configuration = Configuration.defaultConfiguration()
                // set to work with org.json
                .jsonProvider(new JsonOrgJsonProvider())
                .mappingProvider(new JsonOrgMappingProvider())
                // force to return null in case of invalid path
                .addOptions(Option.SUPPRESS_EXCEPTIONS);

        this.reportGenerationTimeoutMsec = TimeUnit.SECONDS.toMillis(reportGenerationTimeout);
    }

    public void download(ReportRequest request, String outputFile) throws IOException, BaiduSPException {
        String reportId = requestReportGeneration(request.buildJSONObject());

        boolean generated;
        long deadline = System.currentTimeMillis() + reportGenerationTimeoutMsec;
        while (!(generated = (getReportState(reportId) == REPORT_GENERATED)) && System.currentTimeMillis() <= deadline) {
            reallySleep(REPORT_GENERATION_CHECK_SLEEP_TIME_MSEC);
        }
        if (!generated) {
            throw new BaiduSPException("report generation timed out.");
        }

        String fileURL = getReportFileURL(reportId);

        downloadReportFile(fileURL, outputFile);
    }

    private static final long REPORT_GENERATION_CHECK_SLEEP_TIME_MSEC = 1000;

    private final long reportGenerationTimeoutMsec;
    private JSONObject requestBodyTemplate;
    private Configuration configuration;

    private String requestReportGeneration(JSONObject requesterArgument) throws IOException, BaiduSPException {
        JSONObject response = executeRequest(REPORT_REQUESTER_URL,
                buildRequestBody(requesterArgument));

        String reportId = (String) getResponseData(response, REPORT_ID_FIELD);
        ensureResponseDataIsPresent(reportId);

        return reportId;
    }

    private void downloadReportFile(String url, String outputPath) throws IOException {
        URL source = new URL(url);
        File destination = new File(outputPath);

        FileUtils.copyURLToFile(source, destination);
    }

    private int getReportState(String reportId) throws IOException, BaiduSPException {
        JSONObject checkerArgument = new JSONObject().put(REPORT_ID_FIELD, reportId);
        JSONObject response = executeRequest(REPORT_CHECKER_URL,
                buildRequestBody(checkerArgument));

        Integer reportState = (Integer) getResponseData(response, REPORT_STATE_FIELD);
        ensureResponseDataIsPresent(reportState);

        return reportState.intValue();
    }

    private String getReportFileURL(String reportId) throws IOException, BaiduSPException {
        JSONObject getterArgument = new JSONObject().put(REPORT_ID_FIELD, reportId);
        JSONObject response = executeRequest(REPORT_FILE_URL_GETTER_URL,
                buildRequestBody(getterArgument));

        String reportFileURL = (String) getResponseData(response, REPORT_FILE_URL_FIELD);
        ensureResponseDataIsPresent(reportFileURL);

        return reportFileURL;
    }

    private JSONObject buildRequestBody(JSONObject argumentValue) {
        return requestBodyTemplate.put(REQUEST_BODY_ARGUMENT_FIELD, argumentValue);
    }

    private JSONObject executeRequest(String url, JSONObject body) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
        JSONObject responseObj;

        // Send
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
            dos.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        // Read
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        // Make exception message more specific
        try {
            responseObj = new JSONObject(new JSONTokener(response.toString()));
        } catch (JSONException e) {
            String message = String.format("failed to parse response (%s)", e.getMessage());
            throw new JSONException(message);
        }

        return responseObj;
    }

    private Object getResponseData(JSONObject response, String targetField) throws BaiduSPException {
        // Check if error was returned
        Object errorDetails = JsonPath
                .using(configuration)
                .parse(response)
                .read(RESPONSE_ERROR_DETAILS_JSONPATH);
        if (errorDetails != null) {
            throw new BaiduSPException("server returned error: " + errorDetails);
        }

        String targetJsonPath = String.format("%s.['%s']", RESPONSE_DATA_JSONPATH, targetField);

        return JsonPath
                .using(configuration)
                .parse(response)
                .read(targetJsonPath);
    }

    private void ensureResponseDataIsPresent(Object data) throws BaiduSPException {
        if (data == null)
            throw new BaiduSPException("no response data");
        else if (data instanceof String && ((String) data).isEmpty())
            throw new BaiduSPException("response data is empty");
    }

}
