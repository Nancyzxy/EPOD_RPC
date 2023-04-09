package Handler;

import Framework.EdgeNode;
import java.util.ArrayList;

public abstract class Handler {
    EdgeNode node;
    public Handler(EdgeNode node){
        this.node = node;
    }

    public abstract boolean neighboringSet(ArrayList<?> c1, ArrayList<?> c2);
}
