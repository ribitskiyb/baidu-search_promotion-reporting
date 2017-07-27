package etl.cloud.baidu.search_promotion.internal;

public class BaiduSPConstants {

    private BaiduSPConstants() {/*restrict instantiation*/}

    public static final String BASE_URL = "https://api.baidu.com/json/sms/service/";

    public static final String REPORT_SERVICE = "ReportService/";
    public static final String ASYNC_REPORT_REQUESTER = "getProfessionalReportId";
    public static final String ASYNC_REPORT_CHECKER = "getReportState";
    public static final String ASYNC_REPORT_FILE_URL_GETTER = "getReportFileUrl";

    public static final String REPORT_REQUESTER_URL       = BASE_URL + REPORT_SERVICE + ASYNC_REPORT_REQUESTER;
    public static final String REPORT_CHECKER_URL         = BASE_URL + REPORT_SERVICE + ASYNC_REPORT_CHECKER;
    public static final String REPORT_FILE_URL_GETTER_URL = BASE_URL + REPORT_SERVICE + ASYNC_REPORT_FILE_URL_GETTER;

    // Request
    public static final String REQUEST_BODY_AUTH_FIELD = "header";
    public static final String REQUEST_BODY_ARGUMENT_FIELD = "body";

    // Response
    public static final String RESPONSE_ERROR_DETAILS_JSONPATH = "$.header.failures[0]";
    public static final String RESPONSE_DATA_JSONPATH = "$.body.data[0]";

    public static final String REPORT_ID_FIELD = "reportId";

    public static final String REPORT_STATE_FIELD = "isGenerated";
    public static final int    REPORT_GENERATED = 3;

    public static final String REPORT_FILE_URL_FIELD = "reportFilePath";

}
