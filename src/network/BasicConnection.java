package network;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import sun.misc.BASE64Encoder;
import utils.XmlParsingUtils;

import java.io.IOException;

import domain.UserCredentials;


/**
 * Classe que utiliza Apache HttpClient para comunicar pedidos e receber respostas
 * através da API de REST da plataforma twitter.com.
 */
public class BasicConnection extends AbstractConnection
{
    /** Realiza autenticação de tipo BASIC ao pedido passado por parâmetro */
    public void authenticate(HttpUriRequest request, UserCredentials user)
    {
        String credentials = new BASE64Encoder().encode(
            (user.getName() + ":" + user.getPassword()).getBytes());
        request.setHeader("Authorization", "Basic " + credentials);
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Inicia a sessão no servidor de Twitter
     * @api apiwiki.twitter.com/Twitter-REST-API-Method:-account%C2%A0verify_credentials
     */
    public boolean signIn(UserCredentials user) throws IOException
    {
        String url = url_account + "verify_credentials.xml";
        HttpGet request = new HttpGet(url);
        authenticate(request, user);

        HttpResponse response = doRequest(request, user, N_ATTEMPT, WAIT_TRY, true);
        String res = XmlParsingUtils.parseUsername(response);
        return res != null;
    }

    /**
     * Negocia com o servidor um nome de utilizador válido e regista-o
     * @api Esta operação não existe na API do twitter.com.
     */
    public boolean register(UserCredentials user)
    {
        throw new UnsupportedOperationException();
    }
}
