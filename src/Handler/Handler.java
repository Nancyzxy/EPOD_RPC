package Handler;

import Framework.EdgeNodeImpl;

import java.util.List;

public abstract class Handler {
    EdgeNodeImpl node;
    public Handler(EdgeNodeImpl node){
        this.node = node;
    }

    public abstract boolean neighboringSet(List<Double> c1, List<Double> c2);
}
