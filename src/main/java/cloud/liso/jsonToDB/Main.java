package cloud.liso.jsonToDB;

import cloud.liso.Batch;

public class Main {
    public static void main(String[] args) {
        Batch batch = new Batch();
        if (args.length == 1) {
            if (args[0].equals("--drop")) {
//                batch.drop();
            } else {
                System.out.println("Invalid Parameter");
                System.exit(1);
            }
        } else if (args.length > 1) {
            System.out.println("Invalid Parameter " + args[1]);
            System.exit(1);
        } else {

        }

        batch.run();
    }
}
