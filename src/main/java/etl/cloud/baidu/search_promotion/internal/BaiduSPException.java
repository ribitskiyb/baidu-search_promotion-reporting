package etl.cloud.baidu.search_promotion.internal;

public class BaiduSPException extends Exception {

    public BaiduSPException() {
        super();
    }

    public BaiduSPException(String msg) {
        super(msg);
    }

    public BaiduSPException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BaiduSPException(Throwable cause){
        super(cause);
    }

}
