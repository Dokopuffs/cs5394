package com.me.mypenguins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Penguins";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		 Runtime.getRuntime().addShutdownHook(new Thread()
	        {
	            @Override
	            public void run()
	            {
	                System.out.println("Shutdown hook ran!");
	                writeFile();
	                try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
		
		 new LwjglApplication(new Penguins(), cfg);
		
	}
	
	public static void writeFile(){
		/*try {
			FileWriter fw = new FileWriter("data/gameWorldInfo.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(Integer.toString(1));
			bw.write('\n');
			bw.write(Integer.toString(6));
			
			for( int i = 0; i < 6; i++ ){
					bw.write('\n');
					bw.write(Integer.toString(i));
			}
			
			
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		FileHandle file = Gdx.files.local("data/gameWorldInfo.txt");
		file.writeString("16012345", false);
		
		
	}
}
