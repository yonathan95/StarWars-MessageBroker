package bgu.spl.mics.application;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		try {
			Input input = JsonInputReader.getInputFromJson("input.json");
			System.out.println(input.getEwoks());
			Ewoks ewoks = Ewoks.get();
			for (int i = 0; i < input.getEwoks(); ++i){
				ewoks.addEwok();
			}
			Thread hanSoloMicroservice = new Thread(new HanSoloMicroservice());
			Thread landoMicroservice = new Thread(new LandoMicroservice(input.getLando()));
			Thread c3POMicroservice = new Thread(new C3POMicroservice());
			Thread leiaMicroservice = new Thread(new LeiaMicroservice(input.getAttacks()));
			Thread r2D2Microservice = new Thread(new R2D2Microservice(input.getR2D2()));
			hanSoloMicroservice.start();
			landoMicroservice.start();
			c3POMicroservice.start();
			leiaMicroservice.start();
			r2D2Microservice.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
