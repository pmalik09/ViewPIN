import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustAllCertificates implements X509TrustManager
{
    public TrustAllCertificates()
    {
        super();
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
    {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
    {
    }
}
