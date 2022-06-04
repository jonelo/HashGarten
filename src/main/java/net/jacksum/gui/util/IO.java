/*

  HashGarten 0.9.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022 Dipl.-Inf. (FH) Johann N. Löfflmann,
  All Rights Reserved, <https://jacksum.net>.

  This program is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  This program is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
  details.

  You should have received a copy of the GNU General Public License along with
  this program. If not, see <https://www.gnu.org/licenses/>.

 */
package net.jacksum.gui.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 *
 * @author Johann N. Löfflmann
 */
public class IO {

    /**
     * Convert Object to byte[].
     *
     * @param object the Object that should be converted to a byte array.
     * @return the byte array.
     * @throws java.io.IOException in case of an I/O error.
     */
    public static byte[] objectToBytes(Object object) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try ( ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(object);
            return boas.toByteArray();
        }
    }

    /**
     * Convert Object to a base64 encoded string.
     *
     * @param object the Object that should be converted to a base64 encoded String.
     * @return the String
     * @throws java.io.IOException in case of an I/O error.
     */
    public static String objectToBase64String(Object object) throws IOException {
        return Base64.getEncoder().withoutPadding().encodeToString(objectToBytes(object));
    }
    

    /**
     * Convert byte[] to Object.
     *
     * @param bytes the byte array that should be converted to an Object.
     * @return the Object.
     * @throws java.io.IOException in case of an I/O error.
     * @throws java.lang.ClassNotFoundException in case a class is not found
     * during deserialization.
     */
    public static Object bytesToObject(byte[] bytes)
            throws IOException, ClassNotFoundException {
        InputStream is = new ByteArrayInputStream(bytes);
        try ( ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        }
    }
    
    /**
     * Convert a base64 encoded String to an Object.
     *
     * @param base64 the base64 encoded String.
     * @return the Object.
     * @throws java.io.IOException in case of an I/O error.
     * @throws java.lang.ClassNotFoundException in case a class is not found
     * during deserialization.
     */
    public static Object base64StringToObject(String base64)
            throws IOException, ClassNotFoundException {
        return bytesToObject(Base64.getDecoder().decode(base64));
    }

}
