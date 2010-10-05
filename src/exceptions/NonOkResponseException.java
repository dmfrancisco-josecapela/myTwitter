package exceptions;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;

/**
 * Excepção que contém o código da resposta http e a causa, para respostas que não
 * sejam do tipo 200. Contém ainda a mensagem de erro devolvida pelo Twitter.
 */
public class NonOkResponseException extends ClientProtocolException
{
    private static final long serialVersionUID = 1L;

    private int code;
    private String reason;
    private String twitterError; // Para mensagens de erro específicas do twitter

    public NonOkResponseException(StatusLine statusLine, String twitterError) {
        this.code = statusLine.getStatusCode();
        this.reason = statusLine.getReasonPhrase();
        this.twitterError = twitterError;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public String getTwitterError() {
        return twitterError;
    }

    /** Mensagem de erro que o utilizador possa entender */
    public String getFriendlyMessage() {
        return twitterError.isEmpty()? reason : twitterError;
    }
}
