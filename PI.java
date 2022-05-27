


import java.util.ArrayList;
import java.util.List;

public class PI implements Algorithm {

    private Processor processor;
    private static List<boolCheck> checkList;

    public PI(Processor processor) {
        assert (processor != null);
        this.processor = processor;
    }


    @Override
    public void onWakeUp() {
        checkList=new ArrayList();
        System.out.println("<"+processor.getId()+">");
        sendNode(processor.getId(),-999);
    }

    @Override
    public void onMessage(Message<?> message) {

        //if(message instanceof PIMessage){
            PIMessage mes=(PIMessage)message;
        //}


        //System.out.println("<"+processor.getId()+">-><"+mes.getParentId()+">");

        sendNode(processor.getId(),mes.getParentId());



    }

    private void sendNode(int i,int parentId) {
        boolean flag=true;
       // synchronized (boolCheck.class){
            for(boolCheck bc: checkList){
                if(bc.getId()==i){
                    if(bc.isVisited()){

                        flag=false;

                    }
                }
            }
       // }


        if(flag & parentId!=-999){
            System.out.println("<"+processor.getId()+">-><"+parentId+">");
        }




        List<CommunicationLink> adj= (List<CommunicationLink>) processor.getOutgoingLinks();

        processor.outgoingLinks()
                .forEach(communicationLink -> {
                    boolean f=true;
                    for(int k=0;k<checkList.size();k++){
                        if(checkList.get(k).getId()==communicationLink.getSource().getId()){
                            f=false;
                            break;
                        }
                    }
                    if(f){
                        communicationLink.send(new PIMessage(processor, processor.getId()));
                    }
                });



        checkList.add(new boolCheck(i,true));
        //processor.getRandomOutgoingLink().send(new PIMessage(processor,processor.getId()));

        //List<CommunicationLink> adjList=processor.outgoingLinks().filter(x->x.getSource().getId()==i).collect(Collectors.toList());


        //adjList.get(0).send(new PIMessage(processor,processor.getId()));

    }




    private class boolCheck{
        private int id;
        private boolean isVisited;

        public boolCheck(int id, boolean isVisited) {
            this.id = id;
            this.isVisited = isVisited;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isVisited() {
            return isVisited;
        }

        public void setVisited(boolean visited) {
            isVisited = visited;
        }
    }




}
