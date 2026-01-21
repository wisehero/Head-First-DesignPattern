package Chapter_01.strategy.kakao;

/**
 * 카카오페이 API 응답
 */
public class KakaoPayResponse {
    private final String tid;
    private final String status;

    public KakaoPayResponse(String tid, String status) {
        this.tid = tid;
        this.status = status;
    }

    public String getTid() { return tid; }
    public String getStatus() { return status; }
}
