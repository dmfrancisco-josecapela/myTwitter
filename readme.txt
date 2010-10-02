
 The documentation is written in portuguese, since this project was developed for a
 course assignment. This will not be updated in the future.

 Also, twitter has dropped basic authentication support, so only oauth
 authentication will work. Finally, to run this application you should get your own
 "consumerKey" and "consumerSecret" from "https://twitter.com/apps" and add them at
 "src/network/OAuthConnection.java". Thank you for visiting this project.



 Departamento de Engenharia Informática da Faculdade de Ciência e Tecnologia da UC
 ----------------------------------------------------------------------------------
 David Marquês Francisco       nº2007183509    <dmfranc@student.dei.uc.pt>
 José António Capela Dias      nº2007183794    <jacdias@student.dei.uc.pt>
 __________________________________________________________________________________


 # Compilação

 O código necessário para a compilação da aplicação cliente encontra-se na direc-
 toria "src". Este requer a utilização de várias bibliotecas, as quais podem ser
 encontradas na pasta "lib".


 # Execução do cliente

 Vá à pasta "bin" e siga os passos que se seguem.
 O cliente pode ser executado através dos seguintes comandos:
    java -jar myTwitterClient.jar
 ou java -jar myTwitterClient.jar <tipo autenticação>
 O tipo de autenticação pode ser "OAUTH" ou "BASIC".

 __________________________________________________________________________________
