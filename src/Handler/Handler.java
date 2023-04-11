package Handler;

import Framework.EdgeNode;
import java.util.ArrayList;
import java.util.List;

public abstract class Handler {
    EdgeNode node;
    public Handler(EdgeNode node){
        this.node = node;
    }

    public abstract boolean neighboringSet(List<Double> c1, List<Double> c2);
}
