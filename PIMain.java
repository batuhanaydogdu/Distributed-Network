
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PIMain {
    private static List<Thread> allThreads;
    private static Processor[] processors;
    private static List<CommunicationLink> links;



    public static void main(String[] args) throws InterruptedException {
        links=new ArrayList<>();



        Scanner scanner = new java.util.Scanner(System.in);
       // System.out.println("enter num of proc");
        int numberOfProcessors = scanner.nextInt();
        createProcessors(numberOfProcessors);
        int i=1,j=1;
        while(i>-0.5 && j>-0.5){

            //System.out.println("enter i");
            i=scanner.nextInt();
            //System.out.println("enter j");
            j=scanner.nextInt();
            if(i>(-0.5) && j>(-0.5) ){
                createLink(i,j);
            }


        }






        int starterProcessorId=scanner.nextInt();
        createAndRunThreads();
        scanner.close();
        processors[starterProcessorId].wakeUp();

        joinThreads();






    }
    private static void createProcessors(int n) {
        processors = new Processor[n];
        for (int i=0; i < n; i++) {
            processors[i] = new Processor(i,PI.class);
        }
    }

    private static void createLink(int i,int j) {
/*
        for(int k=0;k<links.size();k++){
            for(int l=0;l<links.size();l++){
                if(links.get(k).getSource().getId()==links.get(l).getDestination().getId()){
                    return;
                }
            }
        }*/

        links.add(new CommunicationLink(processors[i], processors[j]));
        links.add(new CommunicationLink(processors[j], processors[i]));

    }

    private static void createAndRunThreads() {
        allThreads = new ArrayList<>();
        for (Processor p : processors) {
            allThreads.add(new Thread(p));
        }
        for (CommunicationLink l : links) {
            allThreads.add(new Thread(l));
        }
        for (Thread t : allThreads) {
            t.start();
        }
    }
    private static void joinThreads() throws InterruptedException {
        for (Thread t : allThreads) {
            t.join();
        }
    }

}
