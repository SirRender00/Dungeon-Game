package byow.IO;

import byow.Core.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class WorldLoader {

    private static final String FILE_NAME = "./saved_data/save.txt";

    public static World loadWorld() {
        File f = new File(FILE_NAME);
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (World) os.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void saveWorld(World world) {
        File f = new File(FILE_NAME);
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(world);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
