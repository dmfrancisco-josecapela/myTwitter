package utils;

import java.util.ArrayList;
import java.util.Vector;

public class Utils
{
	/**
	 * Transforma um ArrayList numa única String separada por "delimiters"
	 * @param array (ArrayList de objectos)
	 * @param ifNull (String a devolver caso o ArrayList esteja vazio ou seja null)
	 * @param delimiter (String que delimita as várias Strings)
	 * @return concatenação de todas as Strings, separadas por vírgulas
	 */
	@SuppressWarnings("unchecked")
	public static String array2String(ArrayList array, String ifNull, String delimiter)
	{
		if (array == null || array.isEmpty()) return ifNull;
		String str = new String();

		for (Object obj : array)
			str += obj.toString() + delimiter;

		return str.substring(0, str.lastIndexOf(delimiter)).trim();
	}

	/**
	 * Transforma um Vector numa única String separada por "delimiters"
	 * @param vector (Vector de objectos)
	 * @param ifNull (String a devolver caso o ArrayList esteja vazio ou seja null)
	 * @param delimiter (String que delimita as várias Strings)
	 * @return concatenação de todas as Strings, separadas por vírgulas
	 */
	@SuppressWarnings("unchecked")
	public static String vector2String(Vector vector, String ifNull, String delimiter)
	{
		ArrayList array = new ArrayList(vector.isEmpty()? 1 : vector.size());
		for (Object obj : vector) array.add(obj);
		return array2String(array, ifNull, delimiter);
	}

	public static String capitalizeString(String s)
	{
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
