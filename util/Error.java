package util;

public class Error extends RuntimeException{
    public String msg;
    public position pos;
    public Error(position pos, String msg) {
        this.pos = pos;
        this.msg = msg;
    }
    @Override
    public String toString() {
        return "[Error] " + msg + " at " + pos.toString();
    }
}
