package bgu.spl.mics.application;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.AttackEvent;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		HashMap input;
		try {
			input = gson.fromJson(new FileReader(System.getProperty("user.dir")+"//input.json"),HashMap.class);
			System.out.println(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}
}
