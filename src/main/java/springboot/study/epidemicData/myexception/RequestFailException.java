package springboot.study.epidemicData.myexception;

public class RequestFailException extends RuntimeException{

    public RequestFailException(String msg) {
        super(msg);
    }
}
