package xyz.juniverse.stuff.crypt;

/**
 * Created by juniverse on 21/03/2017.
 */

abstract public class Crypt
{
    private String encryptKey = null;
    private String decryptKey = null;

    public String encrypt(String plainText)
    {
        return encrypt(plainText, encryptKey);
    }

    public String decrypt(String encryptedText)
    {
        return decrypt(encryptedText, decryptKey);
    }

    abstract public String encrypt(String plainText, String key);
    abstract public String decrypt(String encryptedText, String key);
}
