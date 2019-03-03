/**
 * 
 */
package hun;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author skyli
 *
 */
public class Command implements Serializable {
	
	private Hashtable<String , String>data;
	public Command()
	{
		data = new Hashtable<String , String>();
	}
	public void put(String key , String value)
	{
		data.put(key , value);
	}
	public String get(String key)
	{
		return data.get(key);
	}
	public String toString()
	{
		String temp = "";
		Enumeration<String> keys = data.keys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			String value = data.get(key);
			temp += key + ":" + value + " ";
		}
		return temp;
	}
}
