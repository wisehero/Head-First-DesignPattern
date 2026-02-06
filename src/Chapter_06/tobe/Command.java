package Chapter_06.tobe;

/**
 * 커맨드 패턴의 핵심 인터페이스.
 * "무엇을 실행할 것인가"를 객체로 표현하기 위한 계약이다.
 * <p>
 * 이 인터페이스 하나로 리모컨은 "조명 켜기"든 "에어컨 온도 조절"이든
 * 구분 없이 동일하게 다룰 수 있게 된다.
 */
public interface Command {

    void execute();
}
