package rick.mobliesafe.blackcontact;

public class BlackContactInfo {
    public String phoneNumber;
    public String contactName;
    /*黑名单拦截模式 1为电话拦截 2位短信拦截 3为电话短信都拦截*/
    public int mode;
    public String getModeString(int mode){
        switch (mode){
            case 1:
                return "电话拦截";
            case 2:
                return "短信拦截";
            case 3:
                return "电话短信拦截";
        }
        return "";
    }
}
