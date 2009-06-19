/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 16.10.2006$
 *
 * Copyright (c) 2006 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 ****************************************************************/
package ch.unartig.u_core.util;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Util class for performing security and cryptology related functions
 */
public class CryptoUtil
{
    public static Logger _logger = Logger.getLogger("ch.unartig.util.CryptUtil");

    static SecureRandom prng;

    public static String createHash()  {
        String retVal;
        String randomNum = Integer.toString(prng.nextInt());

        //get its digest
        MessageDigest sha;
        try
        {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e)
        {
            _logger.error("Can not create message digest, SHA-1 algorithm not available", e);
            throw new UnsupportedOperationException("message digest object not created");
        }
        byte[] result = sha.digest(randomNum.getBytes());
        retVal = hexEncode(result);
        return retVal;
    }

    /**
     * The byte[] returned by MessageDigest does not have a nice
     * textual representation, so some form of encoding is usually performed.
     * <p/>
     * This implementation follows the example of David Flanagan's book
     * "Java In A Nutshell", and converts a byte array into a String
     * of hex characters.
     * <p/>
     * Another popular alternative is to use a "Base64" encoding.
     * @return the message digest
     * @param aInput byte array
     */
    static private String hexEncode(byte[] aInput) {
        StringBuffer result = new StringBuffer();
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }
        return result.toString();
    }


    public static void setPrng(SecureRandom prng)
    {
        CryptoUtil.prng = prng;
    }
}
