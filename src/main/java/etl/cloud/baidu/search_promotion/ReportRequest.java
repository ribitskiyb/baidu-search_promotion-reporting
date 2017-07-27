package etl.cloud.baidu.search_promotion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static etl.cloud.baidu.search_promotion.util.BaiduSPUtil.checkNotNullOrEmpty;
import static etl.cloud.baidu.search_promotion.util.BaiduSPUtil.stringToList;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class ReportRequest {

    public ReportRequest(String reportType, String performanceData, String startDate, String endDate) {
        this.reportType = reportType;
        this.performanceData = stringToList(performanceData);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public JSONObject buildJSONObject() {
        JSONObject argumentValue = new JSONObject()
                .put("reportType", reportType)
                .put("performanceData", new JSONArray(performanceData))
                .put("startDate", startDate)
                .put("endDate", endDate);
        if (!isBlank(levelOfDetails)) argumentValue.put("levelOfDetails", levelOfDetails);
        if (!isBlank(attributes))     argumentValue.put("attributes",     attributes);
        if (!isBlank(statIds))        argumentValue.put("statIds",        statIds);
        if (!isBlank(statRange))      argumentValue.put("statRange",      statRange);
        if (!isBlank(unitOfTime))     argumentValue.put("unitOfTime",     unitOfTime);
        if (!isBlank(device))         argumentValue.put("device",         device);
        if (!isBlank(platform))       argumentValue.put("platform",       platform);
        if (!isBlank(styleType))      argumentValue.put("styleType",      styleType);

        return new JSONObject().put(REPORT_REQUESTER_ARGUMENT_NAME, argumentValue);
    }

    public ReportRequest setLevelOfDetails(String levelOfDetails) {
        checkNotNullOrEmpty(levelOfDetails);
        this.levelOfDetails = levelOfDetails;
        return this;
    }

    public ReportRequest setAttributes(String attributes) {
        checkNotNullOrEmpty(attributes);
        this.attributes = attributes;
        return this;
    }

    public ReportRequest setStatIds(String statIds) {
        checkNotNullOrEmpty(statIds);
        this.statIds = statIds;
        return this;
    }

    public ReportRequest setStatRange(String statRange) {
        checkNotNullOrEmpty(statRange);
        this.statRange = statRange;
        return this;
    }

    public ReportRequest setUnitOfTime(String unitOfTime) {
        checkNotNullOrEmpty(unitOfTime);
        this.unitOfTime = unitOfTime;
        return this;
    }

    public ReportRequest setDevice(String device) {
        checkNotNullOrEmpty(device);
        this.device = device;
        return this;
    }

    public ReportRequest setPlatform(String platform) {
        checkNotNullOrEmpty(platform);
        this.platform = platform;
        return this;
    }

    public ReportRequest setStyleType(String styleType) {
        checkNotNullOrEmpty(styleType);
        this.styleType = styleType;
        return this;
    }

    private static final String REPORT_REQUESTER_ARGUMENT_NAME = "reportRequestType";

    // Required arguments according to the docs
    // cloud.baidu.com/doc/SEM/guanliAPI.html#ReportRequestType
    private String reportType;
    private List<String> performanceData;
    private String startDate;
    private String endDate;

    // Optional
    private String levelOfDetails;
    private String attributes;
    private String statIds;
    private String statRange;
    private String unitOfTime;
    private String device;
    private String platform;
    private String styleType;

}
