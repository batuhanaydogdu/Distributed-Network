
public class PIMessage extends Message<PI> {
    private int parentId;


    public PIMessage(Processor from,int parentId) {
        super(from);
        this.parentId=parentId;
    }

    public int getParentId(){
        return this.parentId;
    }

}
