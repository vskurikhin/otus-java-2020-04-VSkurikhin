package su.svn.hiload.socialnetwork.consistenthash;

public class ServiceNode implements Node {

    private final String idc;
    private final String ip;
    private final int port;

    public ServiceNode(String idc, String ip, int port) {
        this.idc = idc;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getKey() {
        return idc + "-" + ip + ":" + port;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public static void main(String[] args) {
    }
}
